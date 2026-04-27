package com.scanpay.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.scanpay.app.data.model.User;
import com.google.gson.Gson;

/**
 * SessionManager handles user session data storage and retrieval
 */
public class SessionManager {

    private static final String PREF_NAME = "ScanPaySession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER = "user";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USER_TYPE = "userType";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USER_NAME = "userName";
    private static final String KEY_USER_EMAIL = "userEmail";
    private static final String KEY_USER_PHONE = "userPhone";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context context;
    private Gson gson;

    public SessionManager(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
        gson = new Gson();
    }

    /**
     * Save user session after login
     */
    public void createLoginSession(User user, String token) {
        String normalizedUserType = normalizeUserType(user != null ? user.getUserType() : null);

        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_USER, gson.toJson(user));
        editor.putString(KEY_USER_TYPE, normalizedUserType);
        editor.putInt(KEY_USER_ID, user.getId());
        editor.putString(KEY_USER_NAME, user.getName());
        editor.putString(KEY_USER_EMAIL, user.getEmail());
        editor.putString(KEY_USER_PHONE, user.getPhone());
        editor.apply();
    }

    /**
     * Check if user is logged in
     */
    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /**
     * Get the stored user object
     */
    public User getUser() {
        String userJson = prefs.getString(KEY_USER, null);
        if (userJson != null) {
            return gson.fromJson(userJson, User.class);
        }
        return null;
    }

    /**
     * Get auth token
     */
    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    /**
     * Get user type (customer/merchant)
     */
    public String getUserType() {
        return normalizeUserType(prefs.getString(KEY_USER_TYPE, "customer"));
    }

    /**
     * Check if user is a merchant
     */
    public boolean isMerchant() {
        return "merchant".equalsIgnoreCase(getUserType());
    }

    /**
     * Check if user is a customer
     */
    public boolean isCustomer() {
        return "customer".equalsIgnoreCase(getUserType());
    }

    private String normalizeUserType(String userType) {
        if (userType == null) {
            return "customer";
        }

        String normalized = userType.trim().toLowerCase();
        if ("vendor".equals(normalized) || "merchant".equals(normalized)) {
            return "merchant";
        }
        if ("user".equals(normalized) || "customer".equals(normalized)) {
            return "customer";
        }
        return normalized;
    }

    /**
     * Get user ID
     */
    public int getUserId() {
        return prefs.getInt(KEY_USER_ID, -1);
    }

    /**
     * Get user name
     */
    public String getUserName() {
        return prefs.getString(KEY_USER_NAME, "");
    }

    /**
     * Get user email
     */
    public String getUserEmail() {
        return prefs.getString(KEY_USER_EMAIL, "");
    }

    /**
     * Get user phone
     */
    public String getUserPhone() {
        return prefs.getString(KEY_USER_PHONE, "");
    }

    /**
     * Clear session data (logout)
     */
    public void logout() {
        editor.clear();
        editor.apply();
    }

    /**
     * Update user data
     */
    public void updateUser(User user) {
        editor.putString(KEY_USER, gson.toJson(user));
        editor.putString(KEY_USER_NAME, user.getName());
        editor.putString(KEY_USER_EMAIL, user.getEmail());
        editor.putString(KEY_USER_PHONE, user.getPhone());
        editor.apply();
    }
}

