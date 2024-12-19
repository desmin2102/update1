package com.app.myapp.Class;

public class Customer extends User {
    private int diemTV;
    private String rankId;

    public Customer()
    {

    }
    public Customer(String userId, String ten, String email, String phone, String password, Boolean role, int diemTV, String rankId) {
        super(userId, ten, email, phone, password, role); // Truyền tham số role vào constructor của lớp User
        this.diemTV = diemTV;
        this.rankId = determineRankId(diemTV); // Cập nhật rankId khi điểm thay đổi
         }


    public int getDiemTV() {
        return diemTV;
    }

    public void setDiemTV(int diemTV) {
        this.diemTV = diemTV;
        this.rankId = determineRankId(diemTV);
    }

    public String getRankId() {
        return rankId;
    }

    private String determineRankId(int diemTV) {
        if (diemTV >= 4000) {
            return "5";
        } else if (diemTV >= 3000) {
            return "4";
        } else if (diemTV >= 2000) {
            return "3";
        } else if (diemTV >= 1000) {
            return "2";
        } else {
            return "1";
        }
    }
    public void deductPoints(int points) { this.diemTV = Math.max(0, this.diemTV - points);
        this.rankId = determineRankId(this.diemTV); }
}


