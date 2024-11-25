package com.app.myapp.Class;

public class Customer extends User {
    private String idTV;
    private int diemTV;
    private String rankTV;

    public Customer(String userId, String email, String phone, String password, Boolean role, String idTV, int diemTV, String rankTV) {
        super(userId, email, phone, password, role); // Truyền tham số role vào constructor của lớp User
        this.idTV = idTV;
        this.diemTV = diemTV;
        this.rankTV = rankTV;
    }

    // Các phương thức getter và setter
    public String getIdTV() {
        return idTV;
    }

    public void setIdTV(String idTV) {
        this.idTV = idTV;
    }

    public int getDiemTV() {
        return diemTV;
    }

    public void setDiemTV(int diemTV) {
        this.diemTV = diemTV;
    }

    public String getRankTV() {
        return rankTV;
    }

    public void setRankTV(String rankTV) {
        this.rankTV = rankTV;
    }
}
