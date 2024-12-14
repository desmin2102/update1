package com.app.myapp.Class;

public class Room {
    private String roomId;
    private String roomName;
    private int totalSeats;
    private int columns;
    private int rows;
    private String locationId;

    public Room() {
        // Constructor mặc định cần cho Firebase
    }

    public Room(String roomId, String roomName, int totalSeats, int columns, int rows, String locationId) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.totalSeats = totalSeats;
        this.columns = columns;
        this.rows = rows;
        this.locationId = locationId;
    }

    // Getters và Setters

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }
}
