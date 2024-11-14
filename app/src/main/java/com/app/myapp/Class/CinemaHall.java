package com.app.myapp.Class;

public class CinemaHall {
    private int id;
    private String name;
    private int numberOfSeats;
    private int cinemaId; // ID của Cinema mà phòng chiếu thuộc về
    private int movieId; // ID của phim đang chiếu trong phòng chiếu

    public CinemaHall(int id, String name, int numberOfSeats, int cinemaId, int movieId) {
        this.id = id;
        this.name = name;
        this.numberOfSeats = numberOfSeats;
        this.cinemaId = cinemaId;
        this.movieId = movieId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberOfSeats() {
        return numberOfSeats;
    }

    public void setNumberOfSeats(int numberOfSeats) {
        this.numberOfSeats = numberOfSeats;
    }

    public int getCinemaId() {
        return cinemaId;
    }

    public void setCinemaId(int cinemaId) {
        this.cinemaId = cinemaId;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }
}
