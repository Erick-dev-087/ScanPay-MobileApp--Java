package com.scanpay.app.data.request;

import com.google.gson.annotations.SerializedName;

public class PaymentRequest {
    @SerializedName("qr_code_id")
    private int qrCodeId;

    @SerializedName("amount")
    private double amount;

    // Legacy fields for backward compatibility (kept but not sent in API calls)
    @SerializedName("merchant_code")
    private String merchantCode;

    @SerializedName("phone")
    private String phone;

    @SerializedName("reference")
    private String reference;

    // Default constructor
    public PaymentRequest() {}

    // Constructor with QR Code ID (NEW - for backend compatibility)
    public PaymentRequest(int qrCodeId, double amount) {
        this.qrCodeId = qrCodeId;
        this.amount = amount;
    }

    // Legacy constructor (kept for backward compatibility)
    public PaymentRequest(String merchantCode, double amount, String phone) {
        this.merchantCode = merchantCode;
        this.amount = amount;
        this.phone = phone;
        // Extract QR ID from merchant code if it's a number
        try {
            this.qrCodeId = Integer.parseInt(merchantCode);
        } catch (NumberFormatException e) {
            this.qrCodeId = 0;
        }
    }

    // Legacy constructor with reference (kept for backward compatibility)
    public PaymentRequest(String merchantCode, double amount, String phone, String reference) {
        this.merchantCode = merchantCode;
        this.amount = amount;
        this.phone = phone;
        this.reference = reference;
        // Extract QR ID from merchant code if it's a number
        try {
            this.qrCodeId = Integer.parseInt(merchantCode);
        } catch (NumberFormatException e) {
            this.qrCodeId = 0;
        }
    }

    // Getters and Setters
    public int getQrCodeId() { return qrCodeId; }
    public void setQrCodeId(int qrCodeId) { this.qrCodeId = qrCodeId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getMerchantCode() { return merchantCode; }
    public void setMerchantCode(String merchantCode) { this.merchantCode = merchantCode; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }
}
