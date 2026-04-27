package com.scanpay.app.data.response;

import com.google.gson.annotations.SerializedName;
import com.scanpay.app.data.model.User;

public class AuthResponse {
    @SerializedName("success")
    private Boolean success;

    @SerializedName("message")
    private String message;

    @SerializedName("access_token")
    private String accessToken;

    @SerializedName("user_type")
    private String userType;

    @SerializedName("user")
    private User user;

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    // Backward-compatible aliases used by legacy tests.
    public Boolean getSuccess() { return success; }
    public void setSuccess(Boolean success) { this.success = success; }
    public boolean isSuccess() { return isSuccessful(); }

    public String getToken() { return accessToken; }
    public void setToken(String token) { this.accessToken = token; }

    /**
     * Determine if login was successful based on presence of required fields
     * Backend doesn't return a "success" field, so we infer it from the presence
     * of user and access_token
     */
    public boolean isSuccessful() {
        if (success != null) {
            return success;
        }
        return user != null && accessToken != null && !accessToken.isEmpty();
    }
}

