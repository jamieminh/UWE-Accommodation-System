package com.company;

import java.util.ArrayList;
import java.util.Arrays;

public class Hall {
    private String name;
    private String ID;
    private String address;
    private String tel;
    private int totalRooms;
    private ArrayList<Room> rooms;

    public Hall(String name, String ID, String address, String tel, int totalRooms, ArrayList<Room> rooms) {
        this.name = name;
        this.ID = ID;
        this.address = address;
        this.tel = tel;
        this.totalRooms = totalRooms;
        this.rooms = rooms;
    }

    @Override
    public String toString() {
        return String.format("\n%-15s: %s\n%-15s: %s\n%-15s: %s\n%-15s: %s\n%-15s: %s\n%-15s: %s",
                "Number", getID(), "Name", getName(), "Address", getAddress(), "Telephone", getTel(),
                "Total Rooms", getTotalRooms(), "Rooms", Arrays.toString(getRooms().toArray()));
    }

    public String getName() {
        return name;
    }

    public String getRoomNumbers(){
        int[] roomNumbers = getRooms().stream().mapToInt(Room::getNumber).toArray();
        return Arrays.toString(roomNumbers).replaceAll("[\\[\\]]", "");
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public int getTotalRooms() {
        return totalRooms;
    }

    public void setTotalRooms(int totalRooms) {
        this.totalRooms = totalRooms;
    }

    public ArrayList<Room> getRooms() {
        return rooms;
    }

    public void setRooms(ArrayList<Room> rooms) {
        this.rooms = rooms;
    }


}
