package ui;

import model.Employee;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.*;

public class EmployeeDashboard extends JFrame {
    public EmployeeDashboard(Employee emp) {
        setTitle("Employee Dashboard - " + emp.getFullName());
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Info", createInfoPanel(emp));
        tabs.add("Request Leave", createLeavePanel(emp));

        add(tabs);
        setVisible(true);
    }

    private JPanel createInfoPanel(Employee emp) {
    JPanel panel = new JPanel(new BorderLayout());
    JTextArea infoArea = new JTextArea();
    infoArea.setEditable(false);
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

                // Search next 30 days for matching days
                for (int i = 0; i < 30 && shown < 7; i++) {
                    LocalDate checkDate = today.plusDays(i);
                    DayOfWeek dow = checkDate.getDayOfWeek();

                    if (dow.name().equals(dayName) && !checkDate.isBefore(startDate)) {
                        sb.append(String.format("%s (%s): Duty at Block %s from %s to %s\n",
                                dow, checkDate, block, startTime, endTime));
                        shown++;
                        break; // Stop checking further days for this routine line
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
        JPanel panel = new JPanel(new BorderLayout());
        JTextField dayField = new JTextField();
        JButton requestButton = new JButton("Request Leave");

        JPanel top = new JPanel(new GridLayout(2, 2));
        top.add(new JLabel("Enter day (e.g., 2025-06-05):"));
        top.add(dayField);
        top.add(new JLabel(" "));
        top.add(requestButton);

        requestButton.addActionListener(e -> {
            String day = dayField.getText().trim();
            if (!day.matches("\\d{4}-\\d{2}-\\d{2}")) {
                JOptionPane.showMessageDialog(panel, "Invalid date format. Use YYYY-MM-DD");
                return;
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter("leave_requests.txt", true))) {
                // Add "Pending" status as the third field
                bw.write(emp.getId() + "|" + day + "|Pending");
                bw.newLine();
                JOptionPane.showMessageDialog(panel, "Leave requested for: " + day);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        panel.add(top, BorderLayout.NORTH);
        return panel;
    }
}
