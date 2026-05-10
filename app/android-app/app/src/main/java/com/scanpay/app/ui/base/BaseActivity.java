package com.scanpay.app.ui.base;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.scanpay.app.ui.auth.ForgotPasswordActivity;
import com.scanpay.app.ui.auth.LoginActivity;
import com.scanpay.app.ui.auth.RegisterActivity;
import com.scanpay.app.ui.auth.ResetPasswordActivity;
import com.scanpay.app.ui.auth.UserTypeSelectionActivity;
import com.scanpay.app.ui.splash.SplashActivity;
import com.scanpay.app.utils.SessionManager;

public abstract class BaseActivity extends AppCompatActivity {

    private static final long SESSION_CHECK_INTERVAL_MS = 15000;
    private static final String PREFS_NAME = "scanpay_prefs";
    private static final String KEY_DARK_MODE = "dark_mode";

    private final Handler sessionHandler = new Handler(Looper.getMainLooper());
    private final Runnable sessionCheckRunnable = this::checkSessionExpiry;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applyThemePreference();
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sessionManager.isLoggedIn()) {
            sessionManager.updateLastActive();
        }
        scheduleSessionCheck();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sessionHandler.removeCallbacks(sessionCheckRunnable);
        if (sessionManager.isLoggedIn()) {
            sessionManager.updateLastActive();
        }
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        if (sessionManager.isLoggedIn()) {
            sessionManager.updateLastActive();
        }
    }

    private void scheduleSessionCheck() {
        sessionHandler.removeCallbacks(sessionCheckRunnable);
        sessionHandler.postDelayed(sessionCheckRunnable, SESSION_CHECK_INTERVAL_MS);
    }

    private void checkSessionExpiry() {
        if (sessionManager.isLoggedIn() && sessionManager.isSessionExpired() && !isAuthActivity(this)) {
            sessionManager.logout();
            LoginActivity.startFresh(this);
            finish();
            return;
        }

        scheduleSessionCheck();
    }

    private boolean isAuthActivity(Activity activity) {
        return activity instanceof SplashActivity
                || activity instanceof LoginActivity
                || activity instanceof RegisterActivity
                || activity instanceof ForgotPasswordActivity
                || activity instanceof ResetPasswordActivity
                || activity instanceof UserTypeSelectionActivity;
    }

    protected void toggleThemePreference() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isDark = prefs.getBoolean(KEY_DARK_MODE, true);
        boolean newValue = !isDark;
        prefs.edit().putBoolean(KEY_DARK_MODE, newValue).apply();
        AppCompatDelegate.setDefaultNightMode(newValue
                ? AppCompatDelegate.MODE_NIGHT_YES
                : AppCompatDelegate.MODE_NIGHT_NO);
        recreate();
    }

    protected boolean isDarkModeEnabled() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getBoolean(KEY_DARK_MODE, true);
    }

    private void applyThemePreference() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isDark = prefs.getBoolean(KEY_DARK_MODE, true);
        AppCompatDelegate.setDefaultNightMode(isDark
                ? AppCompatDelegate.MODE_NIGHT_YES
                : AppCompatDelegate.MODE_NIGHT_NO);
    }
}
