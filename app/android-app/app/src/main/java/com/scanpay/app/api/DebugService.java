package com.scanpay.app.api;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Debug Service for testing backend connectivity.
 * Use this to verify that the app can reach your backend.
 */
public interface DebugService {

    /**
     * Simple health check endpoint.
     * This should return a 200 OK response if backend is reachable.
     *
     * Expected endpoint on backend: GET /api/health or GET /health
     */
    @GET("health")
    Call<String> checkHealth();

}

