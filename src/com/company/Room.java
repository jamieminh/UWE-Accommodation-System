package com.company;

public class Room {
    private int number;
    private double monthlyRent;
    private Hall hall;
    private Lease lease;
    private boolean isOccupied;
    private String cleaningStatus;
    private boolean isAvailable;

    public Room(int number, double monthlyRent, Hall hall, Lease lease, boolean isOccupied, String cleaningStatus, boolean isAvailable) {
        this.number = number;
        this.monthlyRent = monthlyRent;
        this.isOccupied = isOccupied;
        this.hall = hall;
        this.cleaningStatus = cleaningStatus;
        this.isAvailable = isAvailable;

        if (isOccupied) {
            setLease(lease);
        }
        else {
            System.out.println("Occupied Room must have a lease");
        }
    }

    @Override
    public String toString() {
        return String.format("\n%-10s %-10s %-10s %-10s %-10s %-10s %-10s",
                getNumber(), getMonthlyRent(), getHallID(), getLeaseNumber(),
                getIsOccupied(), getCleaningStatus(), getIsAvailable());
    }

    public Room(int number) {
        this.number = number;
    }

    public String getHallID() {
        return getHall().getID();
    }

    public String getLeaseNumber() {
        return getLease() != null ? getLease().getLeaseNo() : "";
    }

    public int getNumber() {
        return number;
    }

    public double getMonthlyRent() {
        return monthlyRent;
    }

    public void setMonthlyRent(double monthlyRent) {
        this.monthlyRent = monthlyRent;
    }

    public boolean getIsOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }

    public Lease getLease() {
        return lease;
    }

    public void setLease(Lease lease) {
        this.lease = lease;
    }

    public Hall getHall() {
        return hall;
    }

    public void setHall(Hall hall) {
        this.hall = hall;
    }

    public String getCleaningStatus() {
        return cleaningStatus;
    }

    public void setCleaningStatus(String cleaningStatus) {
        this.cleaningStatus = cleaningStatus;
    }

    public boolean getIsAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        this.isAvailable = available;
    }


}
