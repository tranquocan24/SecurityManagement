package ui;

import javax.swing.*;
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

    private JPanel createEmployeeTab() {
        JPanel panel = new JPanel(new BorderLayout());

        DefaultTableModel empModel = new DefaultTableModel(
                new String[] { "ID", "Name", "Gender", "Leaves", "Work Location", "Salary" }, 0);
        JTable empTable = new JTable(empModel);
        loadEmployees(empModel);

        JButton editRoutineBtn = new JButton("Edit Routine");
        JButton exitBtn = new JButton("Log Out");
        JButton viewRoutineBtn = new JButton("View Routine");

        JPanel bottom = new JPanel();
        bottom.add(editRoutineBtn);
        bottom.add(exitBtn);
        bottom.add(viewRoutineBtn);
        JButton reloadBtn = new JButton("reload");
        reloadBtn.addActionListener(e -> {
            loadEmployees(empModel);
        });
        panel.add(reloadBtn, BorderLayout.EAST);
        editRoutineBtn.addActionListener(e -> {
            int selectedRow = empTable.getSelectedRow();
            if (selectedRow >= 0) {
                String empId = (String) empModel.getValueAt(selectedRow, 0);
                new RoutineEditorDialog(this, empId);
            } else {
                JOptionPane.showMessageDialog(this, "Select an employee to edit routine.");
            }
        });

        viewRoutineBtn.addActionListener(e -> {
            int selectedRow = empTable.getSelectedRow();
            if (selectedRow >= 0) {
                String empId = (String) empModel.getValueAt(selectedRow, 0);
                new RoutineViewerDialog(this, empId);
            } else {
                JOptionPane.showMessageDialog(this, "Select an employee to view routine.");
            }
        });

        exitBtn.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

        panel.add(new JScrollPane(empTable), BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);
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
        JPanel panel = new JPanel(new BorderLayout());

        DefaultTableModel leaveModel = new DefaultTableModel(new String[] { "Employee ID", "Date", "Status" }, 0);
        JTable leaveTable = new JTable(leaveModel);
        loadLeaveRequests(leaveModel);

        JButton approveBtn = new JButton("Approve");
        JButton declineBtn = new JButton("Decline");
        JButton exitBtn = new JButton("Log Out");

        JPanel btnPanel = new JPanel();
        btnPanel.add(approveBtn);
        btnPanel.add(declineBtn);
        btnPanel.add(exitBtn);

        approveBtn.addActionListener(e -> updateLeaveStatus(leaveModel, leaveTable, "Approved"));
        declineBtn.addActionListener(e -> updateLeaveStatus(leaveModel, leaveTable, "Declined"));

        exitBtn.addActionListener(e -> {
            dispose();
            new LoginFrame();
        });

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
            super(parent, "Edit Routine", true);
            setLayout(new GridLayout(5, 2, 5, 5));
            setSize(300, 250);
            setLocationRelativeTo(parent);

            JTextField startTime = new JTextField(); // e.g., 08:00
            JTextField endTime = new JTextField(); // e.g., 17:00
            JTextField workDate = new JTextField(); // e.g., 2025/06/11
            JTextField block = new JTextField(); // e.g., A, B, etc.

            add(new JLabel("Start Time (HH:mm):"));
            add(startTime);
            add(new JLabel("End Time (HH:mm):"));
            add(endTime);
            add(new JLabel("Work Date (yyyy/MM/dd):"));
            add(workDate);
            add(new JLabel("Block:"));
            add(block);

            JButton saveBtn = new JButton("Save");
            JButton cancelBtn = new JButton("Cancel");
            add(saveBtn);
            add(cancelBtn);

            saveBtn.addActionListener(e -> {
                try {
                    // Parse the date
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

                    // Check for block or time conflicts
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
                                    // Same block used on the same day
                                    if (existingBlock.equalsIgnoreCase(newBlock)) {
                                        JOptionPane.showMessageDialog(this,
                                                "Block '" + newBlock + "' is already assigned to another employee on "
                                                        + newDateStr + ".",
                                                "Block Conflict",
                                                JOptionPane.WARNING_MESSAGE);
                                        return;
                                    }

                                    // Time overlap
                                    Date existingStartTime = timeFormat.parse(existingStart);
                                    Date existingEndTime = timeFormat.parse(existingEnd);

                                    boolean timeOverlaps = newStartTime.before(existingEndTime)
                                            && newEndTime.after(existingStartTime);
                                    if (timeOverlaps) {
                                        JOptionPane.showMessageDialog(this,
                                                "Another employee already has a duty on " + newDateStr + " from "
                                                        + existingStart + " to " + existingEnd + ".",
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

                    // Save routine
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
                            // Replace routine for the same date
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
            setSize(500, 400);
            setLocationRelativeTo(parent);
            setLayout(new BorderLayout());

            JTextArea routineArea = new JTextArea();
            routineArea.setEditable(false);

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
                                        .get(Calendar.DAY_OF_WEEK)].toUpperCase();
                                String dateStr = sdf.format(calendar.getTime());

                                routineArea.append(String.format("%s (%s): Duty at Block %s from %s to %s\n",
                                        dayName, dateStr, block, start, end));

                                calendar.add(Calendar.DAY_OF_YEAR, 1);
                            }
                        } else {
                            String dateStr = sdf.format(calendar.getTime());
                            routineArea.append(String.format("%s (%s): Duty at Block %s from %s to %s\n",
                                    dayOrNum, dateStr, block, start, end));
                        }
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            add(new JScrollPane(routineArea), BorderLayout.CENTER);

            JButton closeBtn = new JButton("Close");
            closeBtn.addActionListener(e -> dispose());

            JPanel bottom = new JPanel();
            bottom.add(closeBtn);
            add(bottom, BorderLayout.SOUTH);

            setVisible(true);
        }
    }

}
