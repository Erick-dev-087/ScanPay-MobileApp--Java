package com.scanpay.app.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.scanpay.app.api.ApiClient;
import com.scanpay.app.api.ApiService;
import com.scanpay.app.data.Resource;
import com.scanpay.app.data.model.User;
import com.scanpay.app.data.request.CustomerRegisterRequest;
import com.scanpay.app.data.request.ForgotPasswordRequest;
import com.scanpay.app.data.request.LoginRequest;
import com.scanpay.app.data.request.MerchantRegisterRequest;
import com.scanpay.app.data.request.ResetPasswordRequest;
import com.scanpay.app.data.response.AuthResponse;
import com.scanpay.app.data.response.ForgotPasswordResponse;
import com.scanpay.app.data.response.MessageResponse;
import com.scanpay.app.data.response.VendorAuthResponse;
import com.scanpay.app.utils.SessionManager;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Repository that handles all authentication operations.
 * Encapsulates API calls and session management.
 */
public class AuthRepository {

    private final ApiService apiService;
    private final SessionManager sessionManager;

    public AuthRepository(SessionManager sessionManager) {
        this.apiService = ApiClient.getApiService();
        this.sessionManager = sessionManager;
    }

    // ── Login ───────────────────────────────────────────────────────

    public LiveData<Resource<AuthResponse>> login(String email, String password) {
        MutableLiveData<Resource<AuthResponse>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        LoginRequest request = new LoginRequest(email, password);
        apiService.login(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(@NonNull Call<AuthResponse> call,
                                   @NonNull Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse body = response.body();
                    if (body.isSuccessful()) {
                        User user = body.getUser();
                        if (user == null && body.getVendor() != null) {
                            // Vendor login returns "vendor" payload; convert it for session storage.
                            user = body.getVendor().toUser();
                        }
                        if (user == null) {
                            result.setValue(Resource.error("Login response did not include user data."));
                            return;
                        }
                        // Prefer explicit user_type from response, otherwise preserve converted value.
                        if (body.getUserType() != null && !body.getUserType().isEmpty()) {
                            user.setUserType(body.getUserType());
                        }
                        sessionManager.createLoginSession(user, body.getAccessToken());
                        result.setValue(Resource.success(body));
                    } else {
                        String msg = body.getMessage() != null
                                ? body.getMessage() : "Login failed";
                        result.setValue(Resource.error(msg));
                    }
                } else {
                    result.setValue(Resource.error(parseError(response)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<AuthResponse> call, @NonNull Throwable t) {
                result.setValue(Resource.error(networkError(t)));
            }
        });

        return result;
    }

    // ── Customer Registration ───────────────────────────────────────

    public LiveData<Resource<AuthResponse>> registerCustomer(String name, String email,
                                                             String phone, String password) {
        MutableLiveData<Resource<AuthResponse>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        CustomerRegisterRequest request = new CustomerRegisterRequest(name, email, phone, password);
        apiService.registerCustomer(request).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(@NonNull Call<AuthResponse> call,
                                   @NonNull Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse body = response.body();
                    if (body.isSuccessful() && body.getUser() != null) {
                        // Set user type from response if provided
                        User user = body.getUser();
                        if (body.getUserType() != null && !body.getUserType().isEmpty()) {
                            user.setUserType(body.getUserType());
                        }
                        sessionManager.createLoginSession(user, body.getAccessToken());
                        result.setValue(Resource.success(body));
                    } else {
                        String msg = body.getMessage() != null
                                ? body.getMessage() : "Registration failed";
                        result.setValue(Resource.error(msg));
                    }
                } else {
                    result.setValue(Resource.error(parseError(response)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<AuthResponse> call, @NonNull Throwable t) {
                result.setValue(Resource.error(networkError(t)));
            }
        });

        return result;
    }

    // ── Merchant/Vendor Registration ────────────────────────────────

    public LiveData<Resource<VendorAuthResponse>> registerVendor(
            String name, String email, String phone, String password,
            String businessName, String businessShortcode,
            String merchantId, String mcc, String storeLabel,
            String shortcodeType, String paybillAccountNumber) {

        MutableLiveData<Resource<VendorAuthResponse>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        MerchantRegisterRequest request = new MerchantRegisterRequest(
                name, email, phone, password,
                businessName, businessShortcode, merchantId, mcc, storeLabel,
                shortcodeType, paybillAccountNumber);

        apiService.registerVendor(request).enqueue(new Callback<VendorAuthResponse>() {
            @Override
            public void onResponse(@NonNull Call<VendorAuthResponse> call,
                                   @NonNull Response<VendorAuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    VendorAuthResponse body = response.body();
                    if (body.isSuccessful()) {
                        // Build User from vendor data or use the user field
                        User user = body.getUser();
                        if (user == null && body.getVendor() != null) {
                            user = body.getVendor().toUser();
                        }
                        if (user != null) {
                            user.setUserType("merchant");
                            sessionManager.createLoginSession(user, body.getAccessToken());
                        }
                        result.setValue(Resource.success(body));
                    } else {
                        String msg = body.getMessage() != null
                                ? body.getMessage() : "Registration failed";
                        result.setValue(Resource.error(msg));
                    }
                } else {
                    result.setValue(Resource.error(parseError(response)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<VendorAuthResponse> call, @NonNull Throwable t) {
                result.setValue(Resource.error(networkError(t)));
            }
        });

        return result;
    }

    // ── Logout ──────────────────────────────────────────────────────

    public void logout() {
        // Fire-and-forget the server logout; clear local session immediately
        try {
            apiService.logout().enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call,
                                       @NonNull Response<Void> response) { /* no-op */ }
                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) { /* no-op */ }
            });
        } catch (Exception ignored) {
        }

        sessionManager.logout();
        ApiClient.resetClient(); // Force new OkHttp client on next login
    }

    // ── Forgot Password ─────────────────────────────────────────────

    public LiveData<Resource<ForgotPasswordResponse>> forgotPassword(String email) {
        MutableLiveData<Resource<ForgotPasswordResponse>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        ForgotPasswordRequest request = new ForgotPasswordRequest(email);
        apiService.forgotPassword(request).enqueue(new Callback<ForgotPasswordResponse>() {
            @Override
            public void onResponse(@NonNull Call<ForgotPasswordResponse> call,
                                   @NonNull Response<ForgotPasswordResponse> response) {
                if (response.isSuccessful()) {
                    ForgotPasswordResponse body = response.body();
                    if (body == null) {
                        body = new ForgotPasswordResponse();
                        body.setMessage("If the account exists, a password reset token has been generated.");
                    }
                    result.setValue(Resource.success(body));
                } else {
                    result.setValue(Resource.error(parseError(response)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<ForgotPasswordResponse> call, @NonNull Throwable t) {
                result.setValue(Resource.error(networkError(t)));
            }
        });

        return result;
    }

    // ── Reset Password ──────────────────────────────────────────────

    public LiveData<Resource<MessageResponse>> resetPassword(String token,
                                                             String newPassword,
                                                             String confirmPassword) {
        MutableLiveData<Resource<MessageResponse>> result = new MutableLiveData<>();
        result.setValue(Resource.loading());

        ResetPasswordRequest request = new ResetPasswordRequest(token, newPassword, confirmPassword);
        apiService.resetPassword(request).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(@NonNull Call<MessageResponse> call,
                                   @NonNull Response<MessageResponse> response) {
                if (response.isSuccessful()) {
                    MessageResponse body = response.body();
                    if (body == null) {
                        body = new MessageResponse();
                        body.setMessage("Password reset successful");
                    }
                    result.setValue(Resource.success(body));
                } else {
                    result.setValue(Resource.error(parseError(response)));
                }
            }

            @Override
            public void onFailure(@NonNull Call<MessageResponse> call, @NonNull Throwable t) {
                result.setValue(Resource.error(networkError(t)));
            }
        });

        return result;
    }

    // ── Helpers ─────────────────────────────────────────────────────

    /**
     * Parse the error body from a non-2xx response.
     */
    private String parseError(Response<?> response) {
        try {
            if (response.errorBody() != null) {
                String errorJson = response.errorBody().string();
                JSONObject obj = new JSONObject(errorJson);
                if (obj.has("message")) {
                    return obj.getString("message");
                }
            }
        } catch (Exception ignored) {
        }

        switch (response.code()) {
            case 400:
                return "Invalid request. Please check your input.";
            case 401:
                return "Invalid credentials.";
            case 409:
                return "An account with this email already exists.";
            case 422:
                return "Please check your input fields.";
            case 500:
                return "Server error. Please try again later.";
            default:
                return "Something went wrong. Please try again.";
        }
    }

    /**
     * Generate a user-friendly message from a network error.
     */
    private String networkError(Throwable t) {
        if (t instanceof java.net.UnknownHostException
                || t instanceof java.net.ConnectException) {
            return "Unable to connect to the server. Please check your internet connection.";
        }
        if (t instanceof java.net.SocketTimeoutException) {
            return "Connection timed out. Please try again.";
        }
        return "Network error. Please try again.";
    }
}

