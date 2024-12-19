package com.app.myapp.Class;

public class Rank {
    private String rankId;
    private String name;
    private int totalpoint;
    private double discount;

    Rank()
    {

    }
    // Constructor
    public Rank(String rankId, String name, int totalpoint, double discount) {
        this.rankId = rankId;
        this.name = name;
        this.totalpoint = totalpoint;
        this.discount = discount;
    }



    // Getters and Setters

    public String getRankId() {
        return rankId;
    }

    public void setRankId(String rankId) {
        this.rankId = rankId;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getTotalpoint() { return totalpoint; }
    public void setTotalpoint(int totalpoint) { this.totalpoint = totalpoint; }

    public double getDiscount() { return discount; }
    public void setDiscount(double discount) { this.discount = discount; }
}
