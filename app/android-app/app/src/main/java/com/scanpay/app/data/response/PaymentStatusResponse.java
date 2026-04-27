package com.scanpay.app.data.response;

import com.google.gson.annotations.SerializedName;

/**
 * Response from GET /api/payment/{transaction_id}/status
 */
public class PaymentStatusResponse {

    @SerializedName("id")
    private Integer id;

    @SerializedName("status")
    private String status;

    @SerializedName("amount")
    private Double amount;

    @SerializedName("phone")
    private String phone;

    @SerializedName("mpesa_receipt")
    private String mpesaReceipt;

    @SerializedName("initiated_at")
    private String initiatedAt;

    @SerializedName("completed_at")
    private String completedAt;

    @SerializedName("error")
    private String error;

    public Integer getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public Double getAmount() {
        return amount;
    }

    public String getPhone() {
        return phone;
    }

    public String getMpesaReceipt() {
        return mpesaReceipt;
    }

    public String getInitiatedAt() {
        return initiatedAt;
    }

    public String getCompletedAt() {
        return completedAt;
    }

    public String getError() {
        return error;
    }
}
