package ui;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import model.*;

public class LoginFrame extends JFrame {
    private static final String EMPLOYEE_FILE = "employees.txt";
    private static final String MANAGER_FILE = "managers.txt";

    public LoginFrame() {
        setTitle("Campus Security System - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        JButton registerEmployeeBtn = new JButton("Register as Employee");
        JButton registerManagerBtn = new JButton("Register as Manager");
        JButton loginEmployeeBtn = new JButton("Login as Employee");
        JButton loginManagerBtn = new JButton("Login as Manager");

        registerEmployeeBtn.addActionListener(e -> registerEmployee());
        registerManagerBtn.addActionListener(e -> registerManager());
        loginEmployeeBtn.addActionListener(e -> loginEmployee());
        loginManagerBtn.addActionListener(e -> loginManager());

        setLayout(new GridLayout(4, 1, 10, 10));
        add(registerEmployeeBtn);
        add(registerManagerBtn);
        add(loginEmployeeBtn);
        add(loginManagerBtn);

        setVisible(true);
    }

    private void registerEmployee() {
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField genderField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField locationField = new JTextField();

        Object[] fields = {"ID:", idField, "Full Name:", nameField, "Gender:", genderField,
                "Password:", passwordField, "Work Location (Block 1-10):", locationField};
        int option = JOptionPane.showConfirmDialog(this, fields, "Register Employee", JOptionPane.OK_CANCEL_OPTION);
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
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField genderField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        Object[] fields = {"ID:", idField, "Full Name:", nameField, "Gender:", genderField, "Password:", passwordField};
        int option = JOptionPane.showConfirmDialog(this, fields, "Register Manager", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            Manager manager = new Manager(idField.getText(), nameField.getText(), new String(passwordField.getPassword()), genderField.getText());
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
        JTextField idField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        Object[] fields = {"ID:", idField, "Password:", passwordField};
        int option = JOptionPane.showConfirmDialog(this, fields, "Employee Login", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try (BufferedReader br = new BufferedReader(new FileReader(EMPLOYEE_FILE))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split("\\|");
                    if (parts.length >= 3 && parts[0].equals(idField.getText()) && parts[2].equals(new String(passwordField.getPassword()))) {
                        Employee employee = new Employee(parts[0], parts[1], parts[2], parts[3], Integer.parseInt(parts[4]), parts[5]);
                        new EmployeeDashboard(employee);
                        dispose();
                        return;
                    }
                }
                JOptionPane.showMessageDialog(this, "Invalid credentials");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void loginManager() {
        JTextField idField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        Object[] fields = {"ID:", idField, "Password:", passwordField};
        int option = JOptionPane.showConfirmDialog(this, fields, "Manager Login", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try (BufferedReader br = new BufferedReader(new FileReader(MANAGER_FILE))) {
                String line;
                boolean found = false;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split("\\|");
                    if (parts.length >= 3 && parts[0].equals(idField.getText()) && parts[2].equals(new String(passwordField.getPassword()))) {
                        found = true;
                        new ManagerDashboard();
                        dispose();
                        break;
                    }
                }
                if (!found) JOptionPane.showMessageDialog(this, "Invalid credentials");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}