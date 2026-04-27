package com.scanpay.app.data.response;

import com.google.gson.annotations.SerializedName;
import com.scanpay.app.data.model.Merchant;

/**
 * Response from scanning/looking up a QR code.
 */
public class ScanQRResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("merchant")
    private Merchant merchant;

    @SerializedName("qr_code_id")
    private int qrCodeId;

    @SerializedName("merchant_code")
    private String merchantCode;

    @SerializedName("merchant_name")
    private String merchantName;

    @SerializedName("amount")
    private double amount;

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Merchant getMerchant() { return merchant; }
    public void setMerchant(Merchant merchant) { this.merchant = merchant; }
    public int getQrCodeId() { return qrCodeId; }
    public void setQrCodeId(int qrCodeId) { this.qrCodeId = qrCodeId; }
    public String getMerchantCode() { return merchantCode; }
    public void setMerchantCode(String merchantCode) { this.merchantCode = merchantCode; }
    public String getMerchantName() { return merchantName; }
    public void setMerchantName(String merchantName) { this.merchantName = merchantName; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
}
