package model;

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
        int totalDays = 30;
        int workedDays = totalDays - leavesTaken;
        int baseSalary = workedDays * 45000;
        int extraLeaves = Math.max(0, leavesTaken - 4);
        int fine = extraLeaves * 10000;
        return baseSalary - fine;
    }

    public String toFileString() {
        return String.join("|", id, fullName, password, gender,
            String.valueOf(leavesTaken), workLocation, String.valueOf(calculateSalary()));
    }

    public String getId() { return id; }
    public String getPassword() { return password; }
    public String getFullName() { return fullName; }
    public String getGender() { return gender; }
    public int getLeavesTaken() { return leavesTaken; }
    public String getWorkLocation() { return workLocation; }
}