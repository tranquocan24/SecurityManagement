package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;

public class ManagerDashboard extends JFrame {
    private static final String EMPLOYEE_FILE = "employees.txt";

    public ManagerDashboard() {
        setTitle("Manager Dashboard");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();
        JButton manageLeavesBtn = new JButton("Manage Leave Requests");
        manageLeavesBtn.addActionListener(e -> new LeaveManagementDialog(this));
        add(manageLeavesBtn, BorderLayout.SOUTH);


        JPanel employeePanel = new JPanel(new BorderLayout());
        String[] cols = {"ID", "Full Name", "Gender", "Leaves Taken", "Work Location", "Salary", ""};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);

        try (BufferedReader br = new BufferedReader(new FileReader(EMPLOYEE_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                String[] parts = line.split("\\|");
                if (parts.length == 7) {
                    model.addRow(new Object[]{parts[0], parts[1], parts[3], parts[4], parts[5], parts[6], "View"});
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        table.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
        table.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor(new JCheckBox(), table));

        employeePanel.add(new JScrollPane(table), BorderLayout.CENTER);
        tabbedPane.addTab("Employees", employeePanel);

        JPanel leaveRequestPanel = new JPanel();
        leaveRequestPanel.add(new JLabel("Leave Requests tab content here"));
        tabbedPane.addTab("Request Leaves", leaveRequestPanel);

        add(tabbedPane);
        setVisible(true);
    }

    private void showWorkRoutineDialog(String name, String block) {
        StringBuilder routine = new StringBuilder();
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
        for (String day : days) {
            routine.append(day).append(" - Patrol at Block ").append(block).append("\n");
        }
        JOptionPane.showMessageDialog(this, routine.toString(), name + "'s Weekly Routine", JOptionPane.INFORMATION_MESSAGE);
    }

    class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setText("View");
        }
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private JTable table;

        public ButtonEditor(JCheckBox checkBox, JTable table) {
            super(checkBox);
            this.table = table;
            this.button = new JButton("View");
            button.addActionListener(e -> {
                int row = table.getSelectedRow();
                if (row >= 0) {
                    String name = (String) table.getValueAt(row, 1);
                    String location = (String) table.getValueAt(row, 4);
                    showWorkRoutineDialog(name, location);
                }
            });
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            return button;
        }
    }
}