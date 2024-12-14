package com.app.myapp.Class;

import java.util.UUID;

public class Seat {
    private String seatId;
    private String row;
    private int column;
    private String sessionId;
    private String seatName;

    public Seat() {
        // Constructor mặc định cần cho Firebase
    }

    public Seat(String seatId, String row, int column, String sessionId, String seatName) {
        this.seatId = seatId;
        this.row = row;
        this.column = column;
        this.sessionId = sessionId;
        this.seatName = seatName; // Sử dụng seatName từ tham số đầu vào
    }

    // Getters và Setters

    public String getSeatId() {
        return seatId;
    }

    public void setSeatId(String seatId) {
        this.seatId = seatId;
    }

    public String getRow() {
        return row;
    }

    public void setRow(String row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSeatName() {
        return seatName;
    }

    public void setSeatName(String seatName) {
        this.seatName = seatName;
    }
}
