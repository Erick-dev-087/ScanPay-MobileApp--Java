package com.scanpay.app.utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Utility class for KES currency formatting.
 */
public class CurrencyUtils {

    private static final DecimalFormat KES_FORMAT = new DecimalFormat("#,##0");
    private static final DecimalFormat KES_FORMAT_DECIMAL = new DecimalFormat("#,##0.00");

    /**
     * Format amount as "KSH 1,234" (no decimals).
     */
    public static String formatKsh(double amount) {
        return "KSH " + KES_FORMAT.format(amount);
    }

    /**
     * Format amount as "KSH 1,234.00" (with decimals).
     */
    public static String formatKshDecimal(double amount) {
        return "KSH " + KES_FORMAT_DECIMAL.format(amount);
    }

    /**
     * Format amount as "1,234" (no currency symbol, no decimals).
     */
    public static String formatAmount(double amount) {
        return KES_FORMAT.format(amount);
    }

    /**
     * Format amount as "1,234.00" (no currency symbol, with decimals).
     */
    public static String formatAmountDecimal(double amount) {
        return KES_FORMAT_DECIMAL.format(amount);
    }

    /**
     * Parse a formatted amount string back to double.
     */
    public static double parseAmount(String formatted) {
        if (formatted == null || formatted.isEmpty()) return 0;
        String cleaned = formatted.replaceAll("[^\\d.]", "");
        try {
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}

