package com.app.myapp.Class;

public class Admin extends User {

    // Constructor mặc định
    public Admin() {
        super();
    }

    // Constructor với các tham số
    public Admin(String userId,String ten, String email, String phone, String password, Boolean role) {
        super(userId, ten, email, phone, password, role);
    }

    // Các phương thức quản trị (ví dụ)
    public void manageUsers() {
        // Logic quản lý người dùng
    }

    public void viewReports() {
        // Logic xem báo cáo
    }

    public void adjustSettings() {
        // Logic điều chỉnh cài đặt
    }
}
