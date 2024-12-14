package com.app.myapp.Class;

public class Invoice {
    private String invoiceId;
    private String movieName;
    private String location;
    private int totalTickets;
    private int totalPrice;
    private String userId;

    public Invoice() {
        // Constructor mặc định cần cho Firebase
    }

    public Invoice(String invoiceId, String movieName, String location, int totalTickets, int totalPrice, String userId) {
        this.invoiceId = invoiceId;
        this.movieName = movieName;
        this.location = location;
        this.totalTickets = totalTickets;
        this.totalPrice = totalPrice;
        this.userId = userId;
    }

    // Getters và Setters

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getMovieName() {
        return movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getTotalTickets() {
        return totalTickets;
    }

    public void setTotalTickets(int totalTickets) {
        this.totalTickets = totalTickets;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
