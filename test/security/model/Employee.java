package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Employee {
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
                if (p[0].equals(id) ) {
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
        return baseSalary - fine;
    }

    public String toFileString() {
        return String.join("|", id, fullName, password, gender,
                String.valueOf(leavesTaken), workLocation, String.valueOf(calculateSalary()));
    }

    public String getId() {
        return id;
    }

    public String getPassword() {
        return password;
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