package com.scanpay.app.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * User model representing a customer in the system
 */
public class User {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("phone")
    private String phone;

    @SerializedName("user_type")
    private String userType; // "customer" or "merchant"

    @SerializedName("created_at")
    private String createdAt;

    // Default constructor
    public User() {}

    // Constructor for customer registration
    public User(String name, String email, String phone, String userType) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.userType = userType;
    }

    // Getters and Setters
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

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isCustomer() {
        return "customer".equalsIgnoreCase(userType)
                || "user".equalsIgnoreCase(userType);
    }

    public boolean isMerchant() {
        return "merchant".equalsIgnoreCase(userType)
                || "vendor".equalsIgnoreCase(userType);
    }
}

