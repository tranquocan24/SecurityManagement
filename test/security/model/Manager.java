package model;

public class Manager {
    private String id;
    private String fullName;
    private String password;
    private String gender;

    public Manager(String id, String fullName, String password, String gender) {
        this.id = id;
        this.fullName = fullName;
        this.password = password;
        this.gender = gender;
    }

    public String toFileString() {
        return String.join("|", id, fullName, password, gender);
    }

    public String getId() { return id; }
    public String getPassword() { return password; }
}