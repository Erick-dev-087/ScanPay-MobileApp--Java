package com.scanpay.app.data.response;

import com.google.gson.annotations.SerializedName;
import com.scanpay.app.data.model.User;
import com.scanpay.app.data.model.Vendor;

/**
 * Response from POST /api/auth/register/vendor.
 * The backend returns vendor/user details alongside the auth token.
 */
public class VendorAuthResponse {

    @SerializedName("message")
    private String message;

    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("user_type")
    private String userType;

    @SerializedName("vendor")
    private Vendor vendor;

    @SerializedName("user")
    private User user;

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }

    public Vendor getVendor() { return vendor; }
    public void setVendor(Vendor vendor) { this.vendor = vendor; }




    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    /**
     * Determine if registration was successful based on presence of required fields
     * Backend doesn't return a "success" field, so we infer it from the presence
     * of access_token and either vendor or user data
     */
    public boolean isSuccessful() {
        return accessToken != null && !accessToken.isEmpty() &&
               (vendor != null || user != null);
    }
}
