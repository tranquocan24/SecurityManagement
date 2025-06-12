package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Employee implements Profile {
    private String id;
    private String fullName;
    private String password;
    private String gender;
    private int leavesTaken;
    private String workLocation;

    public Employee(String id, String fullName, String password, String gender, int leavesTaken, String workLocation) {
        this.id = id;
        this.fullName = fullName;
        this.password = password;
        this.gender = gender;
        this.leavesTaken = leavesTaken;
        this.workLocation = workLocation;
    }

    public int calculateSalary() {
        int totalDays = 0;
        try (BufferedReader br = new BufferedReader(new FileReader("routines.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split("\\|");
                if (p[0].equals(id)) {
                    totalDays += 1;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        int workedDays = totalDays;
        int baseSalary = workedDays * 100000;
        int extraLeaves = Math.max(0, leavesTaken - 4);
        int fine = extraLeaves * 10000;
        int finalSalary = baseSalary - fine;

        updateEmployeeFileWithSalary(finalSalary);

        return finalSalary;
    }

    public void updateEmployeeFileWithSalary(int salary) {
        StringBuilder updatedContent = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader("employees.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 6 && parts[0].equals(this.id)) {
                    updatedContent.append(String.join("|", id, fullName, password, gender,
                            String.valueOf(leavesTaken), workLocation, String.valueOf(salary)));
                } else {
                    updatedContent.append(line);
                }
                updatedContent.append(System.lineSeparator());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.FileWriter("employees.txt"))) {
            bw.write(updatedContent.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getId() {
        // TODO Auto-generated method stub
        return id;
    }

    @Override
    public String getPassword() {
        // TODO Auto-generated method stub
        return password;
    }

    @Override
    public String toFileString() {
        // TODO Auto-generated method stub
        return String.join("|", id, fullName, password, gender,
                String.valueOf(leavesTaken), workLocation, String.valueOf(calculateSalary()));
    }

    public String getFullName() {
        return fullName;
    }

    public String getGender() {
        return gender;
    }

    public int getLeavesTaken() {
        return leavesTaken;
    }

    public String getWorkLocation() {
        return workLocation;
    }
}