package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class LeaveManagementDialog extends JDialog {
    private static final String LEAVE_FILE = "leave_requests.txt";
    private static final String EMPLOYEE_FILE = "employees.txt";
    private DefaultTableModel model;

    public LeaveManagementDialog(JFrame parent) {
        super(parent, "Manage Leave Requests", true);
        setSize(600, 300);
        setLocationRelativeTo(parent);

        model = new DefaultTableModel(new String[] { "Employee ID", "Date", "Status" }, 0);
        JTable table = new JTable(model);
        loadLeaveRequests();

        JButton approveBtn = new JButton("Approve");
        JButton declineBtn = new JButton("Declined");

        approveBtn.addActionListener(e -> updateStatus(table, "Approved"));
        declineBtn.addActionListener(e -> updateStatus(table, "Declined"));

        JPanel btnPanel = new JPanel();
        btnPanel.add(approveBtn);
        btnPanel.add(declineBtn);

        add(new JScrollPane(table), BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
        setVisible(true);
    }

    private void loadLeaveRequests() {
        try (BufferedReader br = new BufferedReader(new FileReader(LEAVE_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 2) {
                    String status = parts.length == 3 ? parts[2] : "Pending";
                    model.addRow(new Object[] { parts[0], parts[1], status });
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void updateStatus(JTable table, String newStatus) {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a request to update.");
            return;
        }

        String empId = (String) model.getValueAt(row, 0);
        String date = (String) model.getValueAt(row, 1);
        model.setValueAt(newStatus, row, 2);

        List<String> updatedLines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(LEAVE_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 2 && parts[0].equals(empId) && parts[1].equals(date)) {
                    updatedLines.add(empId + "|" + date + "|" + newStatus);
                } else {
                    updatedLines.add(line);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(LEAVE_FILE))) {
            for (String l : updatedLines) {
                bw.write(l);
                bw.newLine();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        if (newStatus.equals("Approved")) {
            incrementLeaveInEmployeeFile(empId);
        }
    }

    private void incrementLeaveInEmployeeFile(String empId) {
        List<String> updated = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(EMPLOYEE_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 7 && parts[0].equals(empId)) {
                    int currentLeaves = Integer.parseInt(parts[4]);
                    currentLeaves++;
                    int workedDays = 30 - currentLeaves;
                    int baseSalary = workedDays * 45000;
                    int extraLeaves = Math.max(0, currentLeaves - 4);
                    int fine = extraLeaves * 10000;
                    int salary = baseSalary - fine;
                    updated.add(String.join("|", parts[0], parts[1], parts[2], parts[3],
                            String.valueOf(currentLeaves), parts[5], String.valueOf(salary)));
                } else {
                    updated.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(EMPLOYEE_FILE))) {
            for (String l : updated) {
                bw.write(l);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
