package com.scanpay.app;

import android.app.Application;
import android.util.Log;

import com.scanpay.app.api.ApiClient;

/**
 * Main Application class for ScanPay
 */
public class ScanPayApplication extends Application {

    private static final String TAG = "ScanPayApplication";
    private static ScanPayApplication instance;
    private Thread.UncaughtExceptionHandler defaultHandler;

    @Override
    public void onCreate() {
        Log.d(TAG, "=== Application onCreate STARTING ===");

        try {
            super.onCreate();
            Log.d(TAG, "super.onCreate completed");
        } catch (Exception e) {
            Log.e(TAG, "CRASH in super.onCreate", e);
            return;
        }

        instance = this;

        // Save default handler and set up custom one for logging
        defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            Log.e(TAG, "=== UNCAUGHT EXCEPTION ===");
            Log.e(TAG, "Thread: " + thread.getName());
            Log.e(TAG, "Exception: " + throwable.getMessage());
            Log.e(TAG, "Stack trace:", throwable);

            // Pass to default handler (don't call System.exit - let Android handle it)
            if (defaultHandler != null) {
                defaultHandler.uncaughtException(thread, throwable);
            }
        });

        Log.d(TAG, "Exception handler set up");

        // Initialise networking layer with app context for AuthInterceptor
        try {
            ApiClient.init(this);
            Log.d(TAG, "ApiClient initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize ApiClient", e);
        }

        Log.d(TAG, "=== Application onCreate COMPLETED ===");
    }

    public static ScanPayApplication getInstance() {
        return instance;
    }
}

