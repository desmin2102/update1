package com.app.myapp.Class;

import java.util.Map;

public class MovieSession {
    private String sessionId;
    private String movieId;
    private String roomId;
    private String startTime;
    private String endTime;
    private int availableSeats;
    private String price; // Thêm thuộc tính price
    private static Map<String, Room> roomMap;

    public MovieSession() {
        // Constructor mặc định cần cho Firebase
    }

    public MovieSession(String sessionId, String movieId, String roomId, String startTime, String endTime, String price) {
        this.sessionId = sessionId;
        this.movieId = movieId;
        this.roomId = roomId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.availableSeats = getTotalSeatsFromRoom(roomId);
        this.price = price; // Khởi tạo price
    }

    // Getters và Setters

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    private int getTotalSeatsFromRoom(String roomId) {
        Room room = roomMap.get(roomId);
        return room != null ? room.getTotalSeats() : 0;
    }

    public static void setRoomMap(Map<String, Room> roomMap) {
        MovieSession.roomMap = roomMap;
    }
}
