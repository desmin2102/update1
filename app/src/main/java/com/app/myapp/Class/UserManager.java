package com.app.myapp.Class;

public class UserManager {
    private static Customer currentCustomer;

    public static void login(Customer customer) {
        currentCustomer = customer;
    }

    public static Customer getCurrentCustomer() {
        return currentCustomer;
    }
}
