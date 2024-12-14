package com.app.myapp.Class;

public class Ticket {
    private String ticketId;
    private String movieName;
    private String roomName;
    private String seatName;
    private String startTime;
    private int price;
    private String userId;
    private String invoiceId; // Thêm trường invoiceId

    public Ticket() {
        // Constructor mặc định cần cho Firebase
    }

    public Ticket(String ticketId, String movieName, String roomName, String seatName, String startTime, int price, String userId, String invoiceId) {
        this.ticketId = ticketId;
        this.movieName = movieName;
        this.roomName = roomName;
        this.seatName = seatName;
        this.startTime = startTime;
        this.price = price;
        this.userId = userId;
        this.invoiceId = invoiceId; // Khởi tạo trường invoiceId
    }

    // Getters và Setters

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getSeatName() {
        return seatName;
    }

    public void setSeatName(String seatName) {
        this.seatName = seatName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }
}
