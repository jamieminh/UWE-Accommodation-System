package com.company;

public class Lease {
    private String leaseNo;
    private int duration;
    private Room room;
    private Tenant tenant;

    public Lease(String leaseNo, Room room, Tenant tenant) {
        this.leaseNo = leaseNo;
        this.duration = 12;
        this.room = room;
        this.tenant = tenant;
    }

    public String toString() {
        return String.format("\n%-15s: %s\n%-15s: %s\n%-15s: %s\n%-15s: %s",
                "Number", getLeaseNo(), "Duration", getDuration(),
                "Room", getRoom().getNumber(), "Tenant", getTenant().getFullName());
    }

    public String getLeaseNo() {
        return leaseNo;
    }

    public void setLeaseNo(String leaseNo) {
        this.leaseNo = leaseNo;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Student getTenant() {
        return tenant;
    }

    public void setTenant(Tenant tenant) {
        this.tenant = tenant;
    }
}
