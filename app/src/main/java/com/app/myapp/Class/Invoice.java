package com.app.myapp.Class;

public class Invoice {
    private String invoiceId;
    private int totalTickets;
    private int totalPrice;
    private String userId;
    private String purchaseDate; // Thêm trường purchaseDate

    public Invoice() {
        // Constructor mặc định cần cho Firebase
    }

    public Invoice(String invoiceId, int totalTickets, int totalPrice, String userId, String purchaseDate) {
        this.invoiceId = invoiceId;
        this.totalTickets = totalTickets;
        this.totalPrice = totalPrice;
        this.userId = userId;
        this.purchaseDate = purchaseDate; // Khởi tạo purchaseDate
    }

    // Getters và Setters

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
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

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }
}
