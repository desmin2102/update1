package com.app.myapp.Class;

public class Review {
    private String reviewId;
    private String userId;
    private String movieId;
    private String content;
    private String reviewTime;
    private double rating;
    public Review()
    {

    }
    public Review(String reviewId, String movieId, String userId, String content,String reviewTime,double rating) {
        this.reviewId = reviewId;
        this.movieId = movieId;
        this.userId = userId;
        this.content = content;
        this.reviewTime=reviewTime;
        this.rating=rating;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getReviewTime() {
        return reviewTime;
    }

    public void setReviewTime(String reviewTime) {
        this.reviewTime = reviewTime;
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
