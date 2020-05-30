package com.company;


import javafx.scene.control.CheckBox;

public class Student {
    private String firstName;
    private String lastName;
    private String studentID;
    private CheckBox select;

    public Student(String first, String last, String id) {
        this.firstName = first;
        this.lastName = last;
        this.studentID = id;
        this.select = new CheckBox();
    }

    public String toString() {
        return String.format("\n%-15s: %s\n%-15s: %s", "Student ID", getStudentID(), "Full Name", getFullName());
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getStudentID() {
        return studentID;
    }

    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public String getFullName(){
        return this.firstName + " " + this.lastName;
    }

    public CheckBox getSelect() {
        return select;
    }
}

class Tenant extends Student {
    private Lease lease;

    public Tenant(String first, String last, String id) {
        super(first, last, id);
    }

    public Tenant(String first, String last, String id, Lease lease) {
        super(first, last, id);
        this.lease = lease;
    }

    public Lease getLease() {
        return lease;
    }

    public void setLease(Lease lease) {
        this.lease = lease;
    }
}
