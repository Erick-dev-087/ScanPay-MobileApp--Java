package com.scanpay.app.ui.splash;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.scanpay.app.ui.base.BaseActivity;

import com.scanpay.app.ui.auth.LoginActivity;
import com.scanpay.app.ui.main.MainActivity;
import com.scanpay.app.ui.merchant.MerchantMainActivity;
import com.scanpay.app.utils.SessionManager;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends BaseActivity {

    private static final String TAG = "SplashActivity";
    private static final long SPLASH_DELAY = 2000; // 2 seconds
    private Handler handler;
    private Runnable navigationRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate called");
        super.onCreate(savedInstanceState);
        Log.d(TAG, "super.onCreate completed");
        // Using splash theme background, no layout needed

        handler = new Handler(Looper.getMainLooper());
        navigationRunnable = this::navigateToNextScreen;

        Log.d(TAG, "Scheduling navigation in " + SPLASH_DELAY + "ms...");
        handler.postDelayed(navigationRunnable, SPLASH_DELAY);
        Log.d(TAG, "onCreate completed");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Clean up handler to prevent memory leaks
        if (handler != null && navigationRunnable != null) {
            handler.removeCallbacks(navigationRunnable);
        }
    }

    private void navigateToNextScreen() {
        Log.d(TAG, "=== navigateToNextScreen called ===");

        // Safety check - make sure activity isn't destroyed
        if (isFinishing() || isDestroyed()) {
            Log.w(TAG, "Activity is finishing/destroyed, aborting navigation");
            return;
        }

        try {
            Log.d(TAG, "Creating SessionManager...");
            SessionManager sessionManager = new SessionManager(this);
            Log.d(TAG, "SessionManager created successfully");

            boolean isLoggedIn = sessionManager.isLoggedIn();
            Log.d(TAG, "isLoggedIn: " + isLoggedIn);

            Class<?> targetActivity;

            if (isLoggedIn) {
                boolean isMerchant = sessionManager.isMerchant();
                Log.d(TAG, "isMerchant: " + isMerchant);
                targetActivity = isMerchant ? MerchantMainActivity.class : MainActivity.class;
            } else {
                targetActivity = LoginActivity.class;
            }

            Log.d(TAG, "Target activity: " + targetActivity.getSimpleName());

            Intent intent = new Intent(SplashActivity.this, targetActivity);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            Log.d(TAG, "Starting activity...");
            startActivity(intent);
            Log.d(TAG, "Activity started, calling finish...");
            finish();
            Log.d(TAG, "=== Navigation completed successfully ===");

        } catch (Exception e) {
            Log.e(TAG, "ERROR in navigateToNextScreen", e);
            e.printStackTrace();

            // Emergency fallback - try to open LoginActivity directly
            try {
                Log.d(TAG, "Attempting emergency fallback to LoginActivity...");
                Intent fallbackIntent = new Intent(SplashActivity.this, LoginActivity.class);
                fallbackIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(fallbackIntent);
                finish();
            } catch (Exception e2) {
                Log.e(TAG, "CRITICAL: Even fallback failed!", e2);
            }
        }
    }
}

