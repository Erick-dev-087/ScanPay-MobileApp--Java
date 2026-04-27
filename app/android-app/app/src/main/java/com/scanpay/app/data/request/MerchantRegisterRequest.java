package com.scanpay.app.data.request;

import com.google.gson.annotations.SerializedName;

/**
 * Request body for POST /api/register/vendor (merchant registration).
 */
public class MerchantRegisterRequest {

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("phone")
    private String phone;

    @SerializedName("password")
    private String password;

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

    @SerializedName("shortcode_type")
    private String shortcodeType; // TILL or PAYBILL

    @SerializedName("paybill_account_number")
    private String paybillAccountNumber; // For PAYBILL: Account number

    public MerchantRegisterRequest(String name, String email, String phone, String password,
                                   String businessName, String businessShortcode,
                                   String merchantId, String mcc, String storeLabel,
                                   String shortcodeType, String paybillAccountNumber) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.businessName = businessName;
        this.businessShortcode = businessShortcode;
        this.merchantId = merchantId;
        this.mcc = mcc;
        this.storeLabel = storeLabel;
        this.shortcodeType = shortcodeType;
        this.paybillAccountNumber = paybillAccountNumber;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getPassword() { return password; }
    public String getBusinessName() { return businessName; }
    public String getBusinessShortcode() { return businessShortcode; }
    public String getMerchantId() { return merchantId; }
    public String getMcc() { return mcc; }
    public String getStoreLabel() { return storeLabel; }
    public String getShortcodeType() { return shortcodeType; }
    public String getPaybillAccountNumber() { return paybillAccountNumber; }
}
