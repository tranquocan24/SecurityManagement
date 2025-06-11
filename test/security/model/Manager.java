package model;

public class Manager implements Profile{
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
        return String.join("|", id, fullName, password, gender);
    }

    
}