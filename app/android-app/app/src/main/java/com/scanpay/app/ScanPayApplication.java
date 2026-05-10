package com.scanpay.app;

import android.app.Activity;
import android.app.Application;
import android.util.Log;

import com.google.gson.Gson;
import com.scanpay.app.api.ApiClient;
import com.scanpay.app.api.ApiService;
import com.scanpay.app.data.response.AppConfigResponse;
import com.scanpay.app.R;
import com.scanpay.app.ui.auth.ForgotPasswordActivity;
import com.scanpay.app.ui.auth.LoginActivity;
import com.scanpay.app.ui.auth.RegisterActivity;
import com.scanpay.app.ui.auth.ResetPasswordActivity;
import com.scanpay.app.ui.auth.UserTypeSelectionActivity;
import com.scanpay.app.ui.splash.SplashActivity;
import com.scanpay.app.utils.SessionManager;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        SessionManager sessionManager = new SessionManager(this);
        applyLocalFallbackConfig(sessionManager);
        fetchRemoteConfig(sessionManager);
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityResumed(Activity activity) {
                if (sessionManager.isLoggedIn()
                        && sessionManager.isSessionExpired()
                        && !isAuthActivity(activity)) {
                    sessionManager.logout();
                    LoginActivity.startFresh(activity);
                    activity.finish();
                    return;
                }

                if (sessionManager.isLoggedIn()) {
                    sessionManager.updateLastActive();
                }
            }

            @Override
            public void onActivityPaused(Activity activity) {
                if (sessionManager.isLoggedIn()) {
                    sessionManager.updateLastActive();
                }
            }

            @Override
            public void onActivityCreated(Activity activity, android.os.Bundle savedInstanceState) {}

            @Override
            public void onActivityStarted(Activity activity) {}

            @Override
            public void onActivityStopped(Activity activity) {}

            @Override
            public void onActivitySaveInstanceState(Activity activity, android.os.Bundle outState) {}

            @Override
            public void onActivityDestroyed(Activity activity) {}
        });

        Log.d(TAG, "=== Application onCreate COMPLETED ===");
    }

    private void applyLocalFallbackConfig(SessionManager sessionManager) {
        try (InputStream inputStream = getResources().openRawResource(R.raw.app_config)) {
            byte[] buffer = new byte[inputStream.available()];
            if (inputStream.read(buffer) > 0) {
                String json = new String(buffer, StandardCharsets.UTF_8);
                AppConfigResponse config = new Gson().fromJson(json, AppConfigResponse.class);
                applyRuntimeConfig(sessionManager, config);
            }
        } catch (Exception e) {
            Log.w(TAG, "No local config fallback applied", e);
        }
    }

    private void fetchRemoteConfig(SessionManager sessionManager) {
        ApiService service = ApiClient.getApiService();
        service.getRuntimeConfig().enqueue(new Callback<AppConfigResponse>() {
            @Override
            public void onResponse(Call<AppConfigResponse> call, Response<AppConfigResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Log.w(TAG, "Runtime config fetch failed: HTTP " + response.code());
                    return;
                }

                applyRuntimeConfig(sessionManager, response.body());
            }

            @Override
            public void onFailure(Call<AppConfigResponse> call, Throwable t) {
                Log.w(TAG, "Runtime config fetch failed", t);
            }
        });
    }

    private void applyRuntimeConfig(SessionManager sessionManager, AppConfigResponse config) {
        if (config == null) {
            return;
        }

        Long idleTimeoutMs = config.getSessionIdleTimeoutMs();
        if (idleTimeoutMs != null && idleTimeoutMs > 0) {
            sessionManager.setSessionIdleTimeoutMs(idleTimeoutMs);
        }
    }

    private boolean isAuthActivity(Activity activity) {
        return activity instanceof SplashActivity
                || activity instanceof LoginActivity
                || activity instanceof RegisterActivity
                || activity instanceof ForgotPasswordActivity
                || activity instanceof ResetPasswordActivity
                || activity instanceof UserTypeSelectionActivity;
    }

    public static ScanPayApplication getInstance() {
        return instance;
    }
}

