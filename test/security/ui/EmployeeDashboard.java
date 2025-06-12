package ui;

import model.Employee;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.time.*;

public class EmployeeDashboard extends JFrame {

    public EmployeeDashboard(Employee emp) {
        setTitle("Employee Dashboard - " + emp.getFullName());
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tabs.add("Personal Info", createInfoPanel(emp));
        tabs.add("Leave Request", createLeavePanel(emp));

        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(220, 53, 69));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFont(new Font("SansSerif", Font.BOLD, 13));
        logoutButton.setFocusPainted(false);
        logoutButton.setPreferredSize(new Dimension(80, 40));
        logoutButton.addActionListener(e -> {
            dispose(); // close current window
            new LoginFrame(); // go back to login
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(logoutButton);

        JPanel content = new JPanel(new BorderLayout());
        content.add(tabs, BorderLayout.CENTER);
        content.add(bottomPanel, BorderLayout.SOUTH);

        add(content);
        setVisible(true);
    }

    private JPanel createInfoPanel(Employee emp) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextArea infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        infoArea.setBackground(new Color(245, 245, 245));
        infoArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(emp.getId()).append("\n");
        sb.append("Name: ").append(emp.getFullName()).append("\n");
        sb.append("Gender: ").append(emp.getGender()).append("\n");
        sb.append("Work Location: ").append(emp.getWorkLocation()).append("\n\n");

        sb.append("--- Upcoming Routine ---\n");

        try (BufferedReader br = new BufferedReader(new FileReader("routines.txt"))) {
            String line;
            LocalDate today = LocalDate.now();
            int shown = 0;

            while ((line = br.readLine()) != null && shown < 7) {
                String[] p = line.split("\\|");
                if (p.length == 6 && p[0].equals(emp.getId())) {
                    String startTime = p[1];
                    String endTime = p[2];
                    String dayName = p[3].toUpperCase();
                    String block = p[4];
                    LocalDate startDate = LocalDate.parse(p[5]);

                    for (int i = 0; i < 30 && shown < 7; i++) {
                        LocalDate checkDate = today.plusDays(i);
                        DayOfWeek dow = checkDate.getDayOfWeek();
                        if (dow.name().equals(dayName) && !checkDate.isBefore(startDate)) {
                            sb.append(String.format("%s (%s): Duty at Block %s from %s to %s\n",
                                    dow, checkDate, block, startTime, endTime));
                            shown++;
                            break;
                        }
                    }
                }
            }

            if (shown == 0) {
                sb.append("No routine scheduled in the next 30 days.\n");
            }

        } catch (Exception e) {
            sb.append("Error loading routine.\n");
            e.printStackTrace();
        }

        sb.append("\nLeaves Taken: ").append(emp.getLeavesTaken()).append("\n");
        sb.append("Leaves Remaining: ").append(Math.max(4 - emp.getLeavesTaken(), 0)).append("\n");
        sb.append("Salary (This Month): ").append(emp.calculateSalary()).append("\n");

        infoArea.setText(sb.toString());
        panel.add(new JScrollPane(infoArea), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createLeavePanel(Employee emp) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextField dayField = new JTextField();
        JButton requestButton = new JButton("Request Leave");

        requestButton.setBackground(new Color(40, 167, 69));
        requestButton.setForeground(Color.WHITE);
        requestButton.setFocusPainted(false);
        requestButton.setFont(new Font("SansSerif", Font.BOLD, 12));

        JPanel top = new JPanel(new BorderLayout(10, 10));
        JPanel form = new JPanel(new GridLayout(2, 1, 5, 5));
        form.add(new JLabel("Enter leave date (YYYY-MM-DD):"));
        form.add(dayField);
        top.add(form, BorderLayout.CENTER);
        top.add(requestButton, BorderLayout.EAST);

        // Table
        String[] columnNames = { "Date", "Status" };
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable leaveTable = new JTable(tableModel);
        leaveTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        leaveTable.setRowHeight(22);

        loadLeaveRequests(emp.getId(), tableModel);

        requestButton.addActionListener(e -> {
            String day = dayField.getText().trim();
            if (!day.matches("\\d{4}-\\d{2}-\\d{2}")) {
                JOptionPane.showMessageDialog(panel, "Invalid date format. Use YYYY-MM-DD");
                return;
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter("leave_requests.txt", true))) {
                bw.write(emp.getId() + "|" + day + "|Pending");
                bw.newLine();
                JOptionPane.showMessageDialog(panel, "Leave requested for: " + day);
                dayField.setText("");

                tableModel.setRowCount(0); // clear and reload
                loadLeaveRequests(emp.getId(), tableModel);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(leaveTable), BorderLayout.CENTER);
        return panel;
    }

    private void loadLeaveRequests(String empId, DefaultTableModel model) {
        try (BufferedReader br = new BufferedReader(new FileReader("leave_requests.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split("\\|");
                if (p.length >= 2 && p[0].equals(empId)) {
                    String date = p[1];
                    String status = (p.length == 3) ? p[2] : "Pending";
                    model.addRow(new Object[] { date, status });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
