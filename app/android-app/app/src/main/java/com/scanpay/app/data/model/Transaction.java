package com.scanpay.app.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Transaction model representing a payment transaction
 */
public class Transaction {

    @SerializedName("id")
    private int id;

    @SerializedName("transaction_id")
    private String transactionId;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("merchant_id")
    private int merchantId;

    @SerializedName("merchant_name")
    private String merchantName;

    @SerializedName("amount")
    private double amount;

    @SerializedName("status")
    private String status;

    @SerializedName("reference")
    private String reference;

    @SerializedName("mpesa_receipt")
    private String mpesaReceipt;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("completed_at")
    private String completedAt;

    @SerializedName("category")
    private String category;

    private int iconColor;

    public Transaction() {}

    public Transaction(String merchantName, double amount, String status) {
        this.merchantName = merchantName;
        this.amount = amount;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getMerchantId() { return merchantId; }
    public void setMerchantId(int merchantId) { this.merchantId = merchantId; }
    public String getMerchantName() { return merchantName; }
    public void setMerchantName(String merchantName) { this.merchantName = merchantName; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
    public String getMpesaReceipt() { return mpesaReceipt; }
    public void setMpesaReceipt(String mpesaReceipt) { this.mpesaReceipt = mpesaReceipt; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getCompletedAt() { return completedAt; }
    public void setCompletedAt(String completedAt) { this.completedAt = completedAt; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public int getIconColor() { return iconColor; }
    public void setIconColor(int iconColor) { this.iconColor = iconColor; }

    public String getFormattedAmount() {
        return String.format("%,.0f", amount);
    }

    public boolean isCompleted() { return "completed".equalsIgnoreCase(status); }
    public boolean isPending() { return "pending".equalsIgnoreCase(status); }
    public boolean isFailed() { return "failed".equalsIgnoreCase(status); }
}

