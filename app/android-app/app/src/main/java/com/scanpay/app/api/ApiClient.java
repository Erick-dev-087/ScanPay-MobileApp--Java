package com.scanpay.app.api;

import android.content.Context;

import com.scanpay.app.utils.Constants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * API Client singleton for network requests.
 * Must be initialised with a Context via {@link #init(Context)} before first use
 * so the AuthInterceptor can read the stored JWT token.
 */
public class ApiClient {

    private static Retrofit retrofit = null;
    private static ApiService apiService = null;
    private static Context appContext = null;

    /**
     * Initialise with Application context (call once from ScanPayApplication).
     */
    public static void init(Context context) {
        appContext = context.getApplicationContext();
    }

    /**
     * Get Retrofit instance.
     */
    public static Retrofit getClient() {
        if (retrofit == null) {
            // Logging interceptor for debugging
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            // Build OkHttp client with auth + logging interceptors
            OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS);

            // Attach AuthInterceptor if context is available
            if (appContext != null) {
                httpBuilder.addInterceptor(new AuthInterceptor(appContext));
            }

            // Logging last so it logs the final request with headers
            httpBuilder.addInterceptor(loggingInterceptor);

            retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpBuilder.build())
                    .build();
        }
        return retrofit;
    }

    /**
     * Get API Service instance.
     */
    public static ApiService getApiService() {
        if (apiService == null) {
            apiService = getClient().create(ApiService.class);
        }
        return apiService;
    }

    /**
     * Reset the client (useful when changing base URL or after logout).
     */
    public static void resetClient() {
        retrofit = null;
        apiService = null;
    }
}

