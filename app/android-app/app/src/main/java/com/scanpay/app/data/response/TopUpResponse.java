package com.scanpay.app.data.response;

import com.google.gson.annotations.SerializedName;

public class TopUpResponse {

    @SerializedName("message")
    private String message;

    @SerializedName("transaction_id")
    private Integer transactionId;

    @SerializedName("checkout_request_id")
    private String checkoutRequestId;

    @SerializedName("status")
    private String status;

    @SerializedName("amount")
    private Double amount;

    @SerializedName("balance")
    private Double balance;

    @SerializedName("instructions")
    private String instructions;

    @SerializedName("error")
    private String error;

    public String getMessage() {
        return message;
    }

    public Integer getTransactionId() {
        return transactionId;
    }

    public String getCheckoutRequestId() {
        return checkoutRequestId;
    }

    public String getStatus() {
        return status;
    }

    public Double getAmount() {
        return amount;
    }

    public Double getBalance() {
        return balance;
    }

    public String getInstructions() {
        return instructions;
    }

    public String getError() {
        return error;
    }
}
