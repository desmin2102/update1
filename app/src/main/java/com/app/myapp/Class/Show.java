package com.app.myapp.Class;

import java.util.Date;
import java.util.List;

public class Show {
    private String showId;
    private Date showDate;
    private String showTime;
    private String theater;
    private List<String> availableSeats;

    // Getters and Setters
    public String getShowId() {
        return showId;
    }

    public void setShowId(String showId) {
        this.showId = showId;
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

    public String getTheater() {
        return theater;
    }

    public void setTheater(String theater) {
        this.theater = theater;
    }

    public List<String> getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(List<String> availableSeats) {
        this.availableSeats = availableSeats;
    }

    // Method to check seat availability
    public boolean isSeatAvailable(String seat) {
        return availableSeats.contains(seat);
    }
}
