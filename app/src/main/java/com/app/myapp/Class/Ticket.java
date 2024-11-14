package com.app.myapp.Class;

import java.util.Date;

public class Ticket {
    private String ticketId;
    private String userId;
    private String movieId;
    private String movieTitle;
    private Date showDate;
    private String showTime;
    private String seatNumber;
    private String cinemaName;
    private String cinemaLocation;
    private Date purchaseDate;
    private double price;
    private String status; // e.g., "purchased", "canceled", "used"
    private CinemaHall cinemaHall; // Thêm thuộc tính CinemaHall

    public Ticket() {
        // Constructor mặc định
    }

    public Ticket(String ticketId, String userId, String movieId, String movieTitle, Date showDate, String showTime, String seatNumber, String cinemaName, String cinemaLocation, Date purchaseDate, double price, String status, CinemaHall cinemaHall) {
        this.ticketId = ticketId;
        this.userId = userId;
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.showDate = showDate;
        this.showTime = showTime;
        this.seatNumber = seatNumber;
        this.cinemaName = cinemaName;
        this.cinemaLocation = cinemaLocation;
        this.purchaseDate = purchaseDate;
        this.price = price;
        this.status = status;
        this.cinemaHall = cinemaHall; // Khởi tạo CinemaHall
    }

    // Các phương thức getter và setter

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public Date getShowDate() {
        return showDate;
    }

    public void setShowDate(Date showDate) {
        this.showDate = showDate;
    }

    public String getShowTime() {
        return showTime;
    }

    public void setShowTime(String showTime) {
        this.showTime = showTime;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getCinemaName() {
        return cinemaName;
    }

    public void setCinemaName(String cinemaName) {
        this.cinemaName = cinemaName;
    }

    public String getCinemaLocation() {
        return cinemaLocation;
    }

    public void setCinemaLocation(String cinemaLocation) {
        this.cinemaLocation = cinemaLocation;
    }

    public Date getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(Date purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public CinemaHall getCinemaHall() {
        return cinemaHall;
    }

    public void setCinemaHall(CinemaHall cinemaHall) {
        this.cinemaHall = cinemaHall;
    }
}
