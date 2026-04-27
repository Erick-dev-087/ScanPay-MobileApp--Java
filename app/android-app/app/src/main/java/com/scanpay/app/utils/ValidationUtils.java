package com.scanpay.app.utils;

import android.text.TextUtils;
import android.util.Patterns;

/**
 * Centralized input validation utilities for all forms.
 */
public class ValidationUtils {

    /**
     * Validate email address format.
     */
    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Validate Kenyan phone number.
     * Accepts formats: 0712345678, 254712345678, +254712345678, 712345678
     */
    public static boolean isValidKenyanPhone(String phone) {
        if (TextUtils.isEmpty(phone)) return false;
        String cleaned = phone.replaceAll("[\\s\\-+]", "");
        // 712345678 (9 digits, starts with 7 or 1)
        if (cleaned.matches("^[71]\\d{8}$")) return true;
        // 0712345678 (10 digits, starts with 07 or 01)
        if (cleaned.matches("^0[71]\\d{8}$")) return true;
        // 254712345678 (12 digits, starts with 254)
        if (cleaned.matches("^254[71]\\d{8}$")) return true;
        return false;
    }

    /**
     * Normalize phone number to 254XXXXXXXXX format (no +).
     */
    public static String normalizeKenyanPhone(String phone) {
        if (TextUtils.isEmpty(phone)) return "";
        String cleaned = phone.replaceAll("[\\s\\-+]", "");
        if (cleaned.startsWith("0")) {
            return "254" + cleaned.substring(1);
        } else if (cleaned.startsWith("254")) {
            return cleaned;
        } else if (cleaned.length() == 9) {
            return "254" + cleaned;
        }
        return cleaned;
    }

    /**
     * Validate password strength: min 6 characters.
     */
    public static boolean isValidPassword(String password) {
        return !TextUtils.isEmpty(password) && password.length() >= 6;
    }

    /**
     * Check if two passwords match.
     */
    public static boolean doPasswordsMatch(String password, String confirmPassword) {
        return !TextUtils.isEmpty(password) && password.equals(confirmPassword);
    }

    /**
     * Validate a non-empty text field.
     */
    public static boolean isNotEmpty(String text) {
        return !TextUtils.isEmpty(text) && !text.trim().isEmpty();
    }

    /**
     * Validate a KES amount: positive, within M-Pesa limits.
     */
    public static boolean isValidAmount(String amountStr) {
        if (TextUtils.isEmpty(amountStr)) return false;
        try {
            double amount = Double.parseDouble(amountStr.replaceAll("[,\\s]", ""));
            return amount > 0 && amount <= 150000; // M-Pesa limit
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Parse amount string to double, returns -1 if invalid.
     */
    public static double parseAmount(String amountStr) {
        if (TextUtils.isEmpty(amountStr)) return -1;
        try {
            return Double.parseDouble(amountStr.replaceAll("[,\\s]", ""));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Validate business short code (numeric, 5-7 digits).
     */
    public static boolean isValidShortCode(String code) {
        if (TextUtils.isEmpty(code)) return false;
        return code.matches("^\\d{5,7}$");
    }
}

