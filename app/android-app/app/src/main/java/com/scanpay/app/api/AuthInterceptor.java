package com.scanpay.app.api;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.scanpay.app.utils.SessionManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * OkHttp Interceptor that:
 * 1. Auto-attaches the JWT Bearer token to every request (if available).
 * 2. Detects 401 Unauthorized responses and broadcasts a session-expired event.
 */
public class AuthInterceptor implements Interceptor {

    private final SessionManager sessionManager;
    private final Context context;

    public AuthInterceptor(Context context) {
        this.context = context.getApplicationContext();
        this.sessionManager = new SessionManager(this.context);
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request originalRequest = chain.request();

        // Opt-out header for public endpoints (login/register/password reset).
        boolean skipAuth = "true".equalsIgnoreCase(originalRequest.header("No-Authentication"));

        // If there's already an Authorization header, don't override it
        if (originalRequest.header("Authorization") != null) {
            return chain.proceed(originalRequest);
        }

        // Attach token if available
        String token = sessionManager.getToken();
        Request.Builder builder = originalRequest.newBuilder();
        if (skipAuth) {
            builder.removeHeader("No-Authentication");
        } else if (token != null && !token.isEmpty()) {
            builder.addHeader("Authorization", "Bearer " + token);
        }

        Response response = chain.proceed(builder.build());

        // Handle 401 — session expired
        if (response.code() == 401) {
            sessionManager.logout();
            // Broadcast so any Activity can react
            Intent intent = new Intent("com.scanpay.app.SESSION_EXPIRED");
            intent.setPackage(context.getPackageName());
            context.sendBroadcast(intent);
        }

        return response;
    }
}

