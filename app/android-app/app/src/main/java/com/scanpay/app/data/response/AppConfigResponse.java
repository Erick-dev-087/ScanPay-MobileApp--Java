package com.scanpay.app.data.response;

import com.google.gson.annotations.SerializedName;

public class AppConfigResponse {

    @SerializedName("auth_session_inactivity_seconds")
    private Long authSessionInactivitySeconds;

    @SerializedName("session_idle_timeout_ms")
    private Long sessionIdleTimeoutMs;

    public Long getAuthSessionInactivitySeconds() {
        return authSessionInactivitySeconds;
    }

    public Long getSessionIdleTimeoutMs() {
        return sessionIdleTimeoutMs;
    }
}
