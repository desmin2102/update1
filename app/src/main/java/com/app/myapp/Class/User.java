package com.app.myapp.Class;

import java.util.Date;
import java.util.List;

public class User {
    private String userId;
    private String userName;
    private String email;
    private String password;
    private Date dateOfBirth;
    private String gender;
    private String city;
    private String district;
    private String address;
    private int points; // Điểm để tăng rank của user
    private String rank; // Rank của user
    private List<Review> reviews; // Danh sách các đối tượng Review mà người dùng đã viết
    private List<Ticket> tickets; // Danh sách các vé đã mua, đã hủy
    private List<Movie> watchedMovies; // Danh sách các phim đã xem

    public User() {
        // Constructor mặc định
    }

    public User(String userId, String userName, String email, String password, Date dateOfBirth, String gender, String city, String district, String address, int points, String rank, List<Review> reviews, List<Ticket> tickets, List<Movie> watchedMovies) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.city = city;
        this.district = district;
        this.address = address;
        this.points = points;
        this.rank = rank;
        this.reviews = reviews;
        this.tickets = tickets;
        this.watchedMovies = watchedMovies;
    }

    // Các phương thức getter và setter

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public List<Ticket> getTickets() {
        return tickets;
    }

    public void setTickets(List<Ticket> tickets) {
        this.tickets = tickets;
    }

    public List<Movie> getWatchedMovies() {
        return watchedMovies;
    }

    public void setWatchedMovies(List<Movie> watchedMovies) {
        this.watchedMovies = watchedMovies;
    }
}
