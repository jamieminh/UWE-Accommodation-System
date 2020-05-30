package com.company;

public class Login {
    private String email;
    private String password;
    private String staff;
    private Hall hall;

    public Login(String email, String password, String staff, Hall hall) {
        this.email = email;
        this.password = password;
        this.staff = staff;
        this.hall = hall;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStaff() {
        return staff;
    }

    public void setStaff(String staff) {
        this.staff = staff;
    }

    public Hall getHall() {
        return hall;
    }

    public void setHall(Hall hall) {
        this.hall = hall;
    }
}
