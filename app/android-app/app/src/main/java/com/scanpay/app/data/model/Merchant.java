package com.scanpay.app.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Merchant model representing a business/vendor in the system
 */
public class Merchant {

    @SerializedName("id")
    private int id;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("name")
    private String name;

    @SerializedName("business_name")
    private String businessName;

    @SerializedName("shortcode_type")
    private String shortcodeType; // TILL or PAYBILL

    @SerializedName("business_short_code")
    private String businessShortCode;

    @SerializedName("paybill_account_number")
    private String paybillAccountNumber; // For PAYBILL: Account number



    @SerializedName("merchant_id")
    private String merchantId;

    @SerializedName("mcc")
    private String mcc; // Merchant Category Code

    @SerializedName("store_label")
    private String storeLabel;

    @SerializedName("email")
    private String email;

    @SerializedName("phone")
    private String phone;

    @SerializedName("qr_code_url")
    private String qrCodeUrl;

    @SerializedName("created_at")
    private String createdAt;

    // Default constructor
    public Merchant() {}

    // Full constructor for merchant registration
    public Merchant(String name, String businessName, String businessShortCode,
                    String merchantId, String mcc, String storeLabel,
                    String email, String phone) {
        this.name = name;
        this.businessName = businessName;
        this.businessShortCode = businessShortCode;
        this.merchantId = merchantId;
        this.mcc = mcc;
        this.storeLabel = storeLabel;
        this.email = email;
        this.phone = phone;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getBusinessShortCode() {
        return businessShortCode;
    }

    public void setBusinessShortCode(String businessShortCode) {
        this.businessShortCode = businessShortCode;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getMcc() {
        return mcc;
    }

    public void setMcc(String mcc) {
        this.mcc = mcc;
    }

    public String getStoreLabel() {
        return storeLabel;
    }

    public void setStoreLabel(String storeLabel) {
        this.storeLabel = storeLabel;
    }

    public String getShortcodeType() {
        return shortcodeType;
    }

    public void setShortcodeType(String shortcodeType) {
        this.shortcodeType = shortcodeType;
    }

    public String getPaybillAccountNumber() {
        return paybillAccountNumber;
    }

    public void setPaybillAccountNumber(String paybillAccountNumber) {
        this.paybillAccountNumber = paybillAccountNumber;
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

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Get display name - business name or name
     */
    public String getDisplayName() {
        return businessName != null && !businessName.isEmpty() ? businessName : name;
    }
}

