package com.scanpay.app.data.request;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {
    @SerializedName("name")
    private String name;
    @SerializedName("email")
    private String email;
    @SerializedName("phone")
    private String phone;
    @SerializedName("password")
    private String password;
    @SerializedName("user_type")
    private String userType;
    @SerializedName("business_name")
    private String businessName;
    @SerializedName("business_short_code")
    private String businessShortCode;
    @SerializedName("merchant_id")
    private String merchantId;
    @SerializedName("mcc")
    private String mcc;
    @SerializedName("store_label")
    private String storeLabel;

    public RegisterRequest(String name, String email, String phone, String password) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.userType = "customer";
    }

    public RegisterRequest(String name, String email, String phone, String password,
                           String businessName, String businessShortCode, String merchantId,
                           String mcc, String storeLabel) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.userType = "merchant";
        this.businessName = businessName;
        this.businessShortCode = businessShortCode;
        this.merchantId = merchantId;
        this.mcc = mcc;
        this.storeLabel = storeLabel;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }
    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }
    public String getBusinessShortCode() { return businessShortCode; }
    public void setBusinessShortCode(String businessShortCode) { this.businessShortCode = businessShortCode; }
    public String getMerchantId() { return merchantId; }
    public void setMerchantId(String merchantId) { this.merchantId = merchantId; }
    public String getMcc() { return mcc; }
    public void setMcc(String mcc) { this.mcc = mcc; }
    public String getStoreLabel() { return storeLabel; }
    public void setStoreLabel(String storeLabel) { this.storeLabel = storeLabel; }
}

