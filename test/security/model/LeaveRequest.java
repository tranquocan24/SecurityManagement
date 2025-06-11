package model;

public class LeaveRequest {
    private String employeeId;
    private String date;
    private String status;

    public LeaveRequest(String employeeId, String date, String status) {
        this.employeeId = employeeId;
        this.date = date;
        this.status = status;
    }

    public static LeaveRequest fromString(String line) {
        String[] parts = line.split("\\|");
        return new LeaveRequest(parts[0], parts[1], parts.length > 2 ? parts[2] : "Pending");
    }

    public String toFileString() {
        return employeeId + "|" + date + "|" + status;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getDate() {
        return date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
