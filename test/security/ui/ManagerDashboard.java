package ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.io.*;
import java.util.*;
import java.text.*;

public class ManagerDashboard extends JFrame {
    private JTabbedPane tabbedPane;

    public ManagerDashboard() {
        setTitle("Manager Dashboard");
        setSize(800, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Employee Info", createEmployeeTab());
        tabbedPane.addTab("Leave Requests", createLeaveTab());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        add(mainPanel);
        setVisible(true);
    }

    private void styleButton(JButton btn, Color bgColor) {
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(150, 36));
    }

    private JPanel createEmployeeTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        DefaultTableModel empModel = new DefaultTableModel(
                new String[] { "ID", "Name", "Gender", "Leaves", "Work Location", "Salary" }, 0);
        JTable empTable = new JTable(empModel);
        empTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        empTable.setRowHeight(24);
        empTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));

        loadEmployees(empModel);

        JButton editRoutineBtn = new JButton("Edit Routine");
        JButton viewRoutineBtn = new JButton("View Routine");
        JButton reloadBtn = new JButton("Reload");
        JButton exitBtn = new JButton("Logout");

        styleButton(editRoutineBtn, new Color(0, 123, 255)); // blue
        styleButton(viewRoutineBtn, new Color(40, 167, 69)); // green
        styleButton(reloadBtn, new Color(255, 193, 7)); // yellow
        styleButton(exitBtn, new Color(220, 53, 69)); // red

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        buttonPanel.add(editRoutineBtn);
        buttonPanel.add(viewRoutineBtn);
        buttonPanel.add(reloadBtn);
        buttonPanel.add(exitBtn);

        editRoutineBtn.addActionListener(e -> {
            int selectedRow = empTable.getSelectedRow();
            if (selectedRow >= 0) {
                String empId = (String) empModel.getValueAt(selectedRow, 0);
                new RoutineEditorDialog(this, empId);
            } else {
                JOptionPane.showMessageDialog(this, "Please select an employee to edit routine.");
            }
        });

        viewRoutineBtn.addActionListener(e -> {
            int selectedRow = empTable.getSelectedRow();
            if (selectedRow >= 0) {
                String empId = (String) empModel.getValueAt(selectedRow, 0);
                new RoutineViewerDialog(this, empId);
            } else {
                JOptionPane.showMessageDialog(this, "Please select an employee to view routine.");
            }
        });

        reloadBtn.addActionListener(e -> loadEmployees(empModel));
        exitBtn.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        panel.add(new JScrollPane(empTable), BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadEmployees(DefaultTableModel model) {

        try (BufferedReader br = new BufferedReader(new FileReader("employees.txt"))) {
            String line;
            model.setRowCount(0);
            while ((line = br.readLine()) != null) {
                String[] p = line.split("\\|");
                if (p.length >= 7)
                    model.addRow(new Object[] { p[0], p[1], p[3], p[4], p[5], p[6] }); // p[6] is salary
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JPanel createLeaveTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        DefaultTableModel leaveModel = new DefaultTableModel(new String[] { "Employee ID", "Date", "Status" }, 0);
        JTable leaveTable = new JTable(leaveModel);
        leaveTable.setFont(new Font("SansSerif", Font.PLAIN, 13));
        leaveTable.setRowHeight(24);
        leaveTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));

        loadLeaveRequests(leaveModel);

        JButton approveBtn = new JButton("Approve");
        JButton declineBtn = new JButton("Decline");
        JButton exitBtn = new JButton("Logout");

        styleButton(approveBtn, new Color(40, 167, 69)); // green
        styleButton(declineBtn, new Color(255, 193, 7)); // yellow
        styleButton(exitBtn, new Color(220, 53, 69)); // red

        approveBtn.addActionListener(e -> updateLeaveStatus(leaveModel, leaveTable, "Approved"));
        declineBtn.addActionListener(e -> updateLeaveStatus(leaveModel, leaveTable, "Declined"));

        exitBtn.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnPanel.add(approveBtn);
        btnPanel.add(declineBtn);
        btnPanel.add(exitBtn);

        panel.add(new JScrollPane(leaveTable), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void loadLeaveRequests(DefaultTableModel model) {
        try (BufferedReader br = new BufferedReader(new FileReader("leave_requests.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split("\\|");
                if (p.length == 3) {
                    model.addRow(new Object[] { p[0], p[1], p[2] });
                } else if (p.length == 2) {
                    model.addRow(new Object[] { p[0], p[1], "Pending" });
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateLeaveStatus(DefaultTableModel model, JTable table, String status) {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a request.");
            return;
        }

        String empId = (String) model.getValueAt(row, 0);
        String date = (String) model.getValueAt(row, 1);
        int leave = 0;
        model.setValueAt(status, row, 2);

        List<String> updated = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("leave_requests.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith(empId) && line.contains("Approved")) {
                    leave++;
                }
                if (line.startsWith(empId + "|" + date + "|")) {
                    updated.add(empId + "|" + date + "|" + status);
                } else {
                    updated.add(line);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("leave_requests.txt"))) {
            for (String s : updated) {
                bw.write(s);
                bw.newLine();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        if ("Approved".equals(status)) {
            List<String> routineUpdated = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader("routines.txt"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] p = line.split("\\|");
                    if (p.length == 6) {
                        String rEmpId = p[0];
                        String rDate = p[5];
                        if (!(rEmpId.equals(empId) && rDate.equals(date))) {
                            routineUpdated.add(line);
                        }
                    } else {
                        routineUpdated.add(line);
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter("routines.txt"))) {
                for (String s : routineUpdated) {
                    bw.write(s);
                    bw.newLine();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            List<String> employeesUpdated = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader("employees.txt"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] p = line.split("\\|");
                    if (p.length >= 6) {
                        String rEmpId = p[0];
                        if ((rEmpId.equals(empId))) {
                            line = p[0] + "|" + p[1] + "|" + p[2] + "|" + p[3] + "|" + leave + "|" + p[5]
                                    + "|" + p[6];
                            employeesUpdated.add(line);
                        } else {
                            employeesUpdated.add(line);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try (BufferedWriter bw = new BufferedWriter(new FileWriter("employees.txt"))) {
                for (String s : employeesUpdated) {
                    bw.write(s);
                    bw.newLine();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static class RoutineEditorDialog extends JDialog {
        public RoutineEditorDialog(JFrame parent, String empId) {
            super(parent, "Edit Routine for Employee ID: " + empId, true);
            setSize(400, 300);
            setLocationRelativeTo(parent);
            setLayout(new BorderLayout(10, 10));

            // Title
            JLabel title = new JLabel("Routine Details");
            title.setFont(new Font("Segoe UI", Font.BOLD, 14));
            JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            titlePanel.add(title);
            add(titlePanel, BorderLayout.NORTH);

            // Form panel
            JPanel formPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 8, 8, 8);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JTextField startTime = new JTextField();
            JTextField endTime = new JTextField();
            JTextField workDate = new JTextField();
            JTextField block = new JTextField();

            JLabel[] labels = {
                    new JLabel("Start Time (HH:mm):"),
                    new JLabel("End Time (HH:mm):"),
                    new JLabel("Work Date (yyyy/MM/dd):"),
                    new JLabel("Block:")
            };

            JTextField[] fields = { startTime, endTime, workDate, block };

            for (int i = 0; i < labels.length; i++) {
                labels[i].setFont(new Font("Segoe UI", Font.PLAIN, 13));
                gbc.gridx = 0;
                gbc.gridy = i;
                gbc.weightx = 0.3;
                formPanel.add(labels[i], gbc);

                fields[i].setPreferredSize(new Dimension(200, 25));
                fields[i].setFont(new Font("Segoe UI", Font.PLAIN, 13));
                gbc.gridx = 1;
                gbc.weightx = 0.7;
                formPanel.add(fields[i], gbc);
            }

            add(formPanel, BorderLayout.CENTER);

            // Buttons
            JPanel buttonPanel = new JPanel();
            JButton saveBtn = new JButton("Save");
            saveBtn.setBackground(new Color(0, 120, 215));
            saveBtn.setForeground(Color.WHITE);

            JButton cancelBtn = new JButton("Cancel");
            cancelBtn.setBackground(Color.RED);
            cancelBtn.setForeground(Color.WHITE);

            buttonPanel.add(saveBtn);
            buttonPanel.add(cancelBtn);
            add(buttonPanel, BorderLayout.SOUTH);

            // === Event Logic (Giữ nguyên logic cũ) ===
            saveBtn.addActionListener(e -> {
                try {
                    String dateInput = workDate.getText().trim();
                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy/MM/dd");
                    SimpleDateFormat storeFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date parsedDate = inputFormat.parse(dateInput);

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(parsedDate);
                    String dayName = new DateFormatSymbols().getWeekdays()[calendar.get(Calendar.DAY_OF_WEEK)]
                            .toUpperCase();

                    String newDateStr = storeFormat.format(parsedDate);
                    String newBlock = block.getText().trim();
                    String newStart = startTime.getText().trim();
                    String newEnd = endTime.getText().trim();

                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                    Date newStartTime = timeFormat.parse(newStart);
                    Date newEndTime = timeFormat.parse(newEnd);

                    try (BufferedReader br = new BufferedReader(new FileReader("routines.txt"))) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            String[] p = line.split("\\|");
                            if (p.length >= 6) {
                                String existingStart = p[1];
                                String existingEnd = p[2];
                                String existingBlock = p[4];
                                String existingDate = p[5];

                                if (existingDate.equals(newDateStr)) {
                                    if (existingBlock.equalsIgnoreCase(newBlock)) {
                                        JOptionPane.showMessageDialog(this,
                                                "Block '" + newBlock + "' is already assigned on " + newDateStr + ".",
                                                "Block Conflict",
                                                JOptionPane.WARNING_MESSAGE);
                                        return;
                                    }

                                    Date existingStartTime = timeFormat.parse(existingStart);
                                    Date existingEndTime = timeFormat.parse(existingEnd);

                                    boolean timeOverlaps = newStartTime.before(existingEndTime)
                                            && newEndTime.after(existingStartTime);
                                    if (timeOverlaps) {
                                        JOptionPane.showMessageDialog(this,
                                                "Time conflict with another routine on " + newDateStr + ".",
                                                "Time Conflict",
                                                JOptionPane.WARNING_MESSAGE);
                                        return;
                                    }
                                }
                            }
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                    String routineLine = empId + "|" + newStart + "|" + newEnd + "|" + dayName + "|" + newBlock + "|"
                            + newDateStr;
                    saveRoutine(empId, routineLine);
                    dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Invalid input: please check time or date format.");
                }
            });

            cancelBtn.addActionListener(e -> dispose());

            setVisible(true);
        }

        private void saveRoutine(String empId, String routineLine) {
            List<String> lines = new ArrayList<>();
            File file = new File("routines.txt");

            boolean updated = false;

            if (file.exists()) {
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        if (line.startsWith(empId + "|") && line.split("\\|").length >= 6 &&
                                line.split("\\|")[5].equals(routineLine.split("\\|")[5])) {
                            lines.add(routineLine);
                            updated = true;
                        } else {
                            lines.add(line);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (!updated) {
                lines.add(routineLine);
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
                for (String l : lines) {
                    bw.write(l);
                    bw.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class RoutineViewerDialog extends JDialog {
        public RoutineViewerDialog(JFrame parent, String empId) {
            super(parent, "View Routine for " + empId, true);
            setSize(600, 450);
            setLocationRelativeTo(parent);
            setLayout(new BorderLayout(10, 10)); // spacing between components
            getContentPane().setBackground(Color.WHITE); // background color

            // Title Label
            JLabel title = new JLabel("Routine Schedule for Employee ID: " + empId);
            title.setFont(new Font("Segoe UI", Font.BOLD, 18));
            title.setHorizontalAlignment(SwingConstants.CENTER);
            title.setBorder(new EmptyBorder(10, 0, 10, 0));
            add(title, BorderLayout.NORTH);

            // Text Area with scroll
            JTextArea routineArea = new JTextArea();
            routineArea.setEditable(false);
            routineArea.setFont(new Font("Consolas", Font.PLAIN, 14));
            routineArea.setMargin(new Insets(10, 10, 10, 10));
            routineArea.setBackground(new Color(245, 245, 245));
            routineArea.setBorder(BorderFactory.createTitledBorder(
                    BorderFactory.createLineBorder(Color.GRAY, 1),
                    "Routine Details",
                    TitledBorder.LEFT,
                    TitledBorder.TOP,
                    new Font("Segoe UI", Font.BOLD, 14),
                    Color.DARK_GRAY));

            try (BufferedReader br = new BufferedReader(new FileReader("routines.txt"))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] p = line.split("\\|");
                    if ((p.length == 6 || p.length == 5) && p[0].equals(empId)) {
                        String start = p[1];
                        String end = p[2];
                        String block = p[4];
                        String dayOrNum = p[3];
                        String startDate = p.length == 6 ? p[5] : null;

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        Calendar calendar = Calendar.getInstance();

                        try {
                            if (startDate != null) {
                                calendar.setTime(sdf.parse(startDate));
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if (dayOrNum.matches("\\d+")) {
                            int numDays = Integer.parseInt(dayOrNum);
                            for (int i = 0; i < numDays; i++) {
                                String dayName = new DateFormatSymbols().getWeekdays()[calendar
                                        .get(Calendar.DAY_OF_WEEK)];
                                String dateStr = sdf.format(calendar.getTime());

                                routineArea.append(String.format("• %s (%s): Block %s, %s - %s\n",
                                        dayName, dateStr, block, start, end));
                                calendar.add(Calendar.DAY_OF_YEAR, 1);
                            }
                        } else {
                            String dayName = dayOrNum;
                            String dateStr = sdf.format(calendar.getTime());
                            routineArea.append(String.format("• %s (%s): Block %s, %s - %s\n",
                                    dayName, dateStr, block, start, end));
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            JScrollPane scrollPane = new JScrollPane(routineArea);
            scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
            add(scrollPane, BorderLayout.CENTER);

            // Close Button
            JButton closeBtn = new JButton("Close");
            closeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            closeBtn.setFocusPainted(false);
            closeBtn.setBackground(new Color(220, 53, 69));
            closeBtn.setForeground(Color.WHITE);
            closeBtn.setPreferredSize(new Dimension(100, 35));
            closeBtn.addActionListener(e -> dispose());

            JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
            bottom.setBackground(Color.WHITE);
            bottom.add(closeBtn);
            add(bottom, BorderLayout.SOUTH);

            setVisible(true);
        }
    }

}
