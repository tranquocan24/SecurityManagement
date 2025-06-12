package ui;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import model.*;

public class LoginFrame extends JFrame {
    private static final String EMPLOYEE_FILE = "employees.txt";
    private static final String MANAGER_FILE = "managers.txt";

    public LoginFrame() {
        setTitle("Campus Security System - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(450, 350);
        setLocationRelativeTo(null);

        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        mainPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Welcome to Campus Security System");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        mainPanel.add(titleLabel);

        // Buttons
        JButton registerEmployeeBtn = createStyledButton("Register as Employee");
        JButton registerManagerBtn = createStyledButton("Register as Manager");
        JButton loginEmployeeBtn = createStyledButton("Login as Employee");
        JButton loginManagerBtn = createStyledButton("Login as Manager");

        // Add action listeners
        registerEmployeeBtn.addActionListener(e -> registerEmployee());
        registerManagerBtn.addActionListener(e -> registerManager());
        loginEmployeeBtn.addActionListener(e -> loginEmployee());
        loginManagerBtn.addActionListener(e -> loginManager());

        // Add buttons to panel
        mainPanel.add(registerEmployeeBtn);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(registerManagerBtn);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(loginEmployeeBtn);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(loginManagerBtn);

        add(mainPanel);
        setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(250, 40));
        button.setFocusPainted(false);
        button.setBackground(new Color(59, 89, 182));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return button;
    }

    private void registerEmployee() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField idField = new JTextField(15);
        JTextField nameField = new JTextField(15);
        JTextField genderField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);
        JTextField locationField = new JTextField(15);
        JTextField salaryField = new JTextField(15);

        String[] labels = { "ID:", "Full Name:", "Gender:", "Password:", "Work Location (Block 1-10):", "Salary:" };
        JTextField[] fields = { idField, nameField, genderField, passwordField, locationField, salaryField };

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            panel.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1;
            panel.add(fields[i], gbc);
        }

        int option = JOptionPane.showConfirmDialog(this, panel, "Register Employee", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (option == JOptionPane.OK_OPTION) {
            Employee emp = new Employee(idField.getText(), nameField.getText(), new String(passwordField.getPassword()),
                    genderField.getText(), 0, locationField.getText());
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(EMPLOYEE_FILE, true))) {
                bw.write(emp.toFileString());
                bw.newLine();
                bw.flush();
                JOptionPane.showMessageDialog(this, "Employee registered successfully.");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void registerManager() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField idField = new JTextField(15);
        JTextField nameField = new JTextField(15);
        JTextField genderField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);

        String[] labels = { "ID:", "Full Name:", "Gender:", "Password:" };
        JTextField[] fields = { idField, nameField, genderField, passwordField };

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            panel.add(new JLabel(labels[i]), gbc);
            gbc.gridx = 1;
            panel.add(fields[i], gbc);
        }

        int option = JOptionPane.showConfirmDialog(this, panel, "Register Manager", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (option == JOptionPane.OK_OPTION) {
            Manager manager = new Manager(idField.getText(), nameField.getText(),
                    new String(passwordField.getPassword()), genderField.getText());
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(MANAGER_FILE, true))) {
                bw.write(manager.toFileString());
                bw.newLine();
                bw.flush();
                JOptionPane.showMessageDialog(this, "Manager registered successfully.");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void loginEmployee() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField idField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1;
        panel.add(idField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        int option = JOptionPane.showConfirmDialog(this, panel, "Employee Login", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (option == JOptionPane.OK_OPTION) {
            try (BufferedReader br = new BufferedReader(new FileReader(EMPLOYEE_FILE))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split("\\|");
                    if (parts.length >= 3 && parts[0].equals(idField.getText())
                            && parts[2].equals(new String(passwordField.getPassword()))) {
                        Employee employee = new Employee(parts[0], parts[1], parts[2], parts[3],
                                Integer.parseInt(parts[4]), parts[5]);
                        new EmployeeDashboard(employee);
                        dispose();
                        return;
                    }
                }
                JOptionPane.showMessageDialog(this, "Invalid credentials", "Login Failed", JOptionPane.ERROR_MESSAGE);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void loginManager() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        JTextField idField = new JTextField(15);
        JPasswordField passwordField = new JPasswordField(15);

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("ID:"), gbc);
        gbc.gridx = 1;
        panel.add(idField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        int option = JOptionPane.showConfirmDialog(this, panel, "Manager Login", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        if (option == JOptionPane.OK_OPTION) {
            try (BufferedReader br = new BufferedReader(new FileReader(MANAGER_FILE))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split("\\|");
                    if (parts.length >= 3 && parts[0].equals(idField.getText())
                            && parts[2].equals(new String(passwordField.getPassword()))) {
                        new ManagerDashboard();
                        dispose();
                        return;
                    }
                }
                JOptionPane.showMessageDialog(this, "Invalid credentials", "Login Failed", JOptionPane.ERROR_MESSAGE);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
