package com.scanpay.app.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Vendor model representing a merchant/business in the system.
 */
public class Vendor {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("business_name")
    private String businessName;

    @SerializedName("business_shortcode")
    private String businessShortcode;

    @SerializedName("merchant_id")
    private String merchantId;

    @SerializedName("mcc")
    private String mcc;

    @SerializedName("store_label")
    private String storeLabel;

    @SerializedName("email")
    private String email;

    @SerializedName("phone")
    private String phone;

    @SerializedName("country_code")
    private String countryCode;

    @SerializedName("currency_code")
    private String currencyCode;

    @SerializedName("created_at")
    private String createdAt;

    public Vendor() {}

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }
    public String getBusinessShortcode() { return businessShortcode; }
    public void setBusinessShortcode(String businessShortcode) { this.businessShortcode = businessShortcode; }
    public String getMerchantId() { return merchantId; }
    public void setMerchantId(String merchantId) { this.merchantId = merchantId; }
    public String getMcc() { return mcc; }
    public void setMcc(String mcc) { this.mcc = mcc; }
    public String getStoreLabel() { return storeLabel; }
    public void setStoreLabel(String storeLabel) { this.storeLabel = storeLabel; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getCountryCode() { return countryCode; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
    public String getCurrencyCode() { return currencyCode; }
    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    /**
     * Convert vendor data into a User object (merchant type) for session storage.
     */
    public User toUser() {
        User user = new User();
        user.setId(id);
        user.setName(name != null ? name : businessName);
        user.setEmail(email);
        user.setPhone(phone);
        user.setUserType("merchant");
        return user;
    }
}
