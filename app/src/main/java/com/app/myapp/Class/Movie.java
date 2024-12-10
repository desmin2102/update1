package com.app.myapp.Class;

import java.util.List;

public class Movie {
    private String id;
    private String title;
    private String duration;
    private String movieDateStart;
    private String genre;
    private double rating;
    private String summary;
    private String trailerUrl;
    private String imageUrl;

    public Movie() {
        // Constructor mặc định
    }

    public Movie(String id, String title, String duration, String movieDateStart, String genre, double rating, String summary, String trailerUrl, String imageUrl) {
        this.id = id;
        this.title = title;
        this.duration = duration;
        this.movieDateStart = movieDateStart;
        this.genre = genre;
        this.rating = rating;
        this.summary = summary;
        this.trailerUrl = trailerUrl;
        this.imageUrl = imageUrl;
    }

    // Getters và Setters
    public String getId() {
        return id;
    }

    public void setMovieId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getMovieDateStart() {
        return movieDateStart;
    }

    public void setMovieDateStart(String movieDateStart) {
        this.movieDateStart = movieDateStart;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTrailerUrl() {
        return trailerUrl;
    }

    public void setTrailerUrl(String trailerUrl) {
        this.trailerUrl = trailerUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


}
