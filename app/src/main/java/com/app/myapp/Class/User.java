package com.app.myapp.Class;

public class User {
    private String id;
    private String ten;
    private String email;
    private String phone;
    private String password;
    private boolean role; // true là admin, false là người dùng

    public User() {
        // Constructor mặc định
    }

    public User(String id,String ten, String email, String phone, String password, boolean role) {
        this.id = id;
        this.ten = ten;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.role = role;
    }

    // Các phương thức getter và setter
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTen() { return ten; }

    public void setTen(String ten) { this.ten = ten; }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isRole() {
        return role;
    }

    public void setRole(boolean role) {
        this.role = role;
    }
}
