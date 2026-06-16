package com.scanpay.app.data.request;

import com.google.gson.annotations.SerializedName;

public class TopUpRequest {

    @SerializedName("phone")
    private String phone;

    @SerializedName("amount")
    private double amount;

    public TopUpRequest() {}

    public TopUpRequest(String phone, double amount) {
        this.phone = phone;
        this.amount = amount;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
