package com.app.myapp.Class;

public class Ticket {
    private String ticketId;
    private String seatName;
    private String sessionId;
    private String price;
    private String userId;
    private String invoiceId;

    public Ticket() {
        // Constructor mặc định cần cho Firebase
    }

    public Ticket(String ticketId, String seatName, String sessionId, String price, String userId,String invoiceId) {
        this.ticketId = ticketId;
        this.seatName = seatName;
        this.sessionId = sessionId;
        this.price = price;
        this.userId = userId;
        this.invoiceId=invoiceId;

    }

    // Getters và Setters
    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }


    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getSeatName() {
        return seatName;
    }

    public void setSeatName(String seatName) {
        this.seatName = seatName;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


}
