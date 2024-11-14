package com.app.myapp.Class;

import java.util.List;

public class Cinema {
    private int id;
    private String name;
    private String address;
    private int numberOfCinemaHalls;
    private List<Integer> cinemaHallIds; // Danh sách ID của các phòng chiếu

    public Cinema(int id, String name, String address, int numberOfCinemaHalls, List<Integer> cinemaHallIds) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.numberOfCinemaHalls = numberOfCinemaHalls;
        this.cinemaHallIds = cinemaHallIds;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getNumberOfCinemaHalls() {
        return numberOfCinemaHalls;
    }

    public void setNumberOfCinemaHalls(int numberOfCinemaHalls) {
        this.numberOfCinemaHalls = numberOfCinemaHalls;
    }

    public List<Integer> getCinemaHallIds() {
        return cinemaHallIds;
    }

    public void setCinemaHallIds(List<Integer> cinemaHallIds) {
        this.cinemaHallIds = cinemaHallIds;
    }
}
