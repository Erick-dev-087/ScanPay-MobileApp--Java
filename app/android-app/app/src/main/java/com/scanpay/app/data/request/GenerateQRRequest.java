package com.scanpay.app.data.request;

import com.google.gson.annotations.SerializedName;

/**
 * Request body for QR code generation.
 */
public class GenerateQRRequest {

    @SerializedName("merchant_code")
    private String merchantCode;

    @SerializedName("amount")
    private double amount;

    @SerializedName("qr_type")
    private String qrType;

    public GenerateQRRequest() {}

    public GenerateQRRequest(String qrType) {
        this.qrType = qrType;
    }

    public GenerateQRRequest(String qrType, double amount) {
        this.qrType = qrType;
        this.amount = amount;
    }

    public String getQrType() { return qrType; }
    public void setQrType(String qrType) { this.qrType = qrType; }

    public String getMerchantCode() { return merchantCode; }
    public void setMerchantCode(String merchantCode) { this.merchantCode = merchantCode; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
}
