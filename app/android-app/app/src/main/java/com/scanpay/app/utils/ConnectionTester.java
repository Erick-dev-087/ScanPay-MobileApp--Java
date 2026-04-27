package com.scanpay.app.utils;

import android.util.Log;

import com.scanpay.app.api.ApiClient;
import com.scanpay.app.api.DebugService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Connection Test Utility
 *
 * Use this class to test if the app can reach your backend.
 * Call this from your LoginActivity or RegisterActivity to diagnose connection issues.
 *
 * Example usage in your Activity:
 *   ConnectionTester.testBackendConnection(new ConnectionTester.ConnectionCallback() {
 *       @Override
 *       public void onSuccess(String response) {
 *           Log.d("ConnectionTest", "Backend is reachable: " + response);
 *       }
 *       @Override
 *       public void onFailure(String error) {
 *           Log.e("ConnectionTest", "Backend unreachable: " + error);
 *       }
 *   });
 */
public class ConnectionTester {

    private static final String TAG = "ConnectionTester";

    public interface ConnectionCallback {
        void onSuccess(String response);
        void onFailure(String error);
    }

    /**
     * Test basic connectivity to the backend.
     * This makes a simple GET request to /api/health endpoint.
     *
     * @param callback Receives the result of the connection test
     */
    public static void testBackendConnection(ConnectionCallback callback) {
        Log.d(TAG, "Starting backend connection test...");
        Log.d(TAG, "Base URL: " + Constants.BASE_URL);

        Retrofit retrofit = ApiClient.getClient();
        DebugService debugService = retrofit.create(DebugService.class);

        debugService.checkHealth().enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String msg = "✓ Backend is reachable! Status: " + response.code()
                            + " | Response: " + response.body();
                    Log.d(TAG, msg);
                    callback.onSuccess(msg);
                } else {
                    String msg = "✗ Backend responded with error code: " + response.code()
                            + " | Error: " + response.message();
                    Log.w(TAG, msg);
                    callback.onFailure(msg);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                String errorMsg = "✗ Failed to reach backend: " + t.getClass().getSimpleName()
                        + " - " + t.getMessage();
                Log.e(TAG, errorMsg, t);
                callback.onFailure(errorMsg);
            }
        });
    }

    /**
     * Print debug information about the current network configuration.
     */
    public static void printDebugInfo() {
        Log.d(TAG, "=== Backend Configuration Debug Info ===");
        Log.d(TAG, "Active Base URL: " + Constants.BASE_URL);
        Log.d(TAG, "Emulator URL: " + Constants.BASE_URL_EMULATOR);
        Log.d(TAG, "Physical Device URL: " + Constants.BASE_URL_PHYSICAL_DEVICE);
        Log.d(TAG, "=========================================");
    }
}

