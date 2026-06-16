package com.scanpay.app.utils;

/**
 * Constants used throughout the app
 */
public class Constants {

    // API Base URL - Update this to your backend URL
    // FOR EMULATOR: Use 10.0.2.2 (special alias to localhost)
    // Special case: 10.0.2.2 is an alias that maps to the host's 127.0.0.1 in Android Emulator
    public static final String BASE_URL_EMULATOR = "http://10.0.2.2:5000/api/";

    // FOR PHYSICAL PHONE / REMOTE DEVICE:
    // Use a backend address reachable from that device.
    // - If the phone is on the same Wi-Fi/LAN as your PC, use the PC's LAN IP.
    // - If you're using Firebase Test Lab / remote cloud devices, your local PC IP will not be reachable;
    //   expose the backend publicly (or through a tunnel) and replace this with that public URL.
    // NOTE: This is used for production/remote deployments like Render
    public static final String BASE_URL = "https://qr-pay-system.onrender.com/api/";

    // FOR PHYSICAL DEVICES on same local network:
    // Replace with your PC's LAN IP address (e.g., 192.168.x.x)
    public static final String BASE_URL_PHYSICAL_DEVICE = "http://192.168.1.100:5000/api/";

    // Default selection.
    // Keep BASE_URL_EMULATOR for Android Studio emulator runs.
    // Switch to BASE_URL_PHYSICAL_DEVICE when running on a real phone on same LAN.
    // Switch to BASE_URL for production/remote deployments (Render, Firebase devices, etc.)

    // User Types
    public static final String USER_TYPE_CUSTOMER = "customer";
    public static final String USER_TYPE_MERCHANT = "merchant";

    // Payment Method Types for Vendors
    public static final String PAYMENT_METHOD_TILL = "TILL";
    public static final String PAYMENT_METHOD_PAYBILL = "PAYBILL";

    // Intent Extras
    public static final String EXTRA_USER_TYPE = "user_type";
    public static final String EXTRA_QR_DATA = "qr_data";
    public static final String EXTRA_QR_CODE_ID = "qr_code_id";
    public static final String EXTRA_VENDOR_ID = "vendor_id";
    public static final String EXTRA_MERCHANT_NAME = "merchant_name";
    public static final String EXTRA_MERCHANT_CODE = "merchant_code";
    public static final String EXTRA_AMOUNT = "amount";
    public static final String EXTRA_TRANSACTION_ID = "transaction_id";
    public static final String EXTRA_RESET_TOKEN = "reset_token";
    public static final String EXTRA_SERVICE_TOKEN_BALANCE = "service_token_balance";
    public static final String EXTRA_NAV_TARGET = "nav_target";

    // Request Codes
    public static final int REQUEST_QR_SCAN = 1001;
    public static final int REQUEST_CAMERA_PERMISSION = 1002;

    // Merchant Category Codes (MCC)
    public static final String[] MCC_CODES = {
            "5411 - Grocery Stores",
            "5541 - Gas Stations",
            "5812 - Restaurants",
            "5814 - Fast Food",
            "5912 - Pharmacy",
            "5942 - Book Stores",
            "5977 - Cosmetics",
            "7011 - Hotels",
            "7230 - Beauty Shops",
            "7299 - Other Services",
            "7311 - Advertising",
            "7512 - Car Rental",
            "7832 - Cinemas",
            "7922 - Entertainment",
            "8011 - Medical Services",
            "8021 - Dental Services",
            "8062 - Hospitals",
            "8211 - Schools",
            "8299 - Education Services"
    };

    // Transaction Status
    public static final String STATUS_PENDING = "pending";
    public static final String STATUS_COMPLETED = "completed";
    public static final String STATUS_FAILED = "failed";

    // Time formats
    public static final String DATE_FORMAT_API = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String DATE_FORMAT_DISPLAY = "dd MMM yyyy, HH:mm";
    public static final String DATE_FORMAT_SHORT = "dd/MM/yyyy";

    // Currency
    public static final String CURRENCY_CODE = "KES";
    public static final String CURRENCY_SYMBOL = "KSH";
}
