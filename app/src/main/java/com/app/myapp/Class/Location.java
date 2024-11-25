package com.app.myapp.Class;

public class Location {
    private String id;
    private String name;
    private String address;
    private int numberOfRooms;

    public  Location()
    {

    }
    public Location(String id, int numberOfRooms, String address, String name) {
        this.id = id;
        this.numberOfRooms = numberOfRooms;
        this.address = address;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getNumberOfRooms() {
        return numberOfRooms;
    }

    public void setNumberOfRooms(int numberOfRooms) {
        this.numberOfRooms = numberOfRooms;
    }


}
