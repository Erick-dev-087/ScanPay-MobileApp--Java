package com.scanpay.app.data.response;

import com.google.gson.annotations.SerializedName;

public class TopUpBalanceResponse {

    @SerializedName("message")
    private String message;

    @SerializedName("balance")
    private Double balance;

    @SerializedName("currency")
    private String currency;

    public String getMessage() {
        return message;
    }

    public Double getBalance() {
        return balance;
    }

    public String getCurrency() {
        return currency;
    }
}
