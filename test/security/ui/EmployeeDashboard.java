package ui;

import model.Employee;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
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

        sb.append("--- Weekly Schedule ---\n");
        LocalDate today = LocalDate.now();
        for (int i = 0; i < 7; i++) {
            LocalDate date = today.plusDays(i);
            sb.append(date.getDayOfWeek()).append(" (" + date + "): ");
            sb.append("Duty at Block ").append(emp.getWorkLocation()).append(" from 08:00 to 17:00\n");
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
                bw.write(emp.getId() + "|" + day);
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

