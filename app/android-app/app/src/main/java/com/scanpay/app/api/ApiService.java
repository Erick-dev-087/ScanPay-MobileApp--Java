package com.scanpay.app.api;

import com.scanpay.app.data.request.CustomerRegisterRequest;
import com.scanpay.app.data.request.ForgotPasswordRequest;
import com.scanpay.app.data.request.LoginRequest;
import com.scanpay.app.data.request.MerchantRegisterRequest;
import com.scanpay.app.data.request.PaymentRequest;
import com.scanpay.app.data.request.ResetPasswordRequest;
import com.scanpay.app.data.response.AuthResponse;
import com.scanpay.app.data.response.ForgotPasswordResponse;
import com.scanpay.app.data.response.MessageResponse;
import com.scanpay.app.data.response.VendorAuthResponse;
import com.scanpay.app.data.response.TransactionResponse;
import com.scanpay.app.data.response.PaymentStatusResponse;
import com.scanpay.app.data.response.AnalyticsResponse;
import com.scanpay.app.data.response.GenerateQRResponse;
import com.scanpay.app.data.response.MerchantResponse;
import com.scanpay.app.data.response.QRImageResponse;
import com.scanpay.app.data.response.QRScanResponse;
import com.scanpay.app.data.response.AppConfigResponse;
import com.scanpay.app.data.response.TopUpBalanceResponse;
import com.scanpay.app.data.response.TopUpResponse;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * API Service interface for Retrofit.
 *
 * Note: Authorization header is automatically attached by {@link AuthInterceptor}.
 * No need to pass {@literal @}Header("Authorization") on authenticated endpoints.
 * All endpoints are relative to BASE_URL (http://localhost:5000/api/)
 */
public interface ApiService {

    // ── Authentication ──────────────────────────────────────────────

    @Headers("No-Authentication: true")
    @POST("auth/login")
    Call<AuthResponse> login(@Body LoginRequest request);

    /** Customer registration — POST /api/auth/register/user */
    @Headers("No-Authentication: true")
    @POST("auth/register/user")
    Call<AuthResponse> registerCustomer(@Body CustomerRegisterRequest request);

    /** Vendor/Merchant registration — POST /api/auth/register/vendor */
    @Headers("No-Authentication: true")
    @POST("auth/register/vendor")
    Call<VendorAuthResponse> registerVendor(@Body MerchantRegisterRequest request);

    @POST("auth/logout")
    Call<Void> logout();

    @Headers("No-Authentication: true")
    @POST("auth/forgot-password")
    Call<ForgotPasswordResponse> forgotPassword(@Body ForgotPasswordRequest request);

    @Headers("No-Authentication: true")
    @POST("auth/reset-password")
    Call<MessageResponse> resetPassword(@Body ResetPasswordRequest request);

    // ── Runtime Config ─────────────────────────────────────────────

    @Headers("No-Authentication: true")
    @GET("config")
    Call<AppConfigResponse> getRuntimeConfig();



    // ── User Management ─────────────────────────────────────────────
    // Note: Profile update endpoints require UpdateUserRequest class (TBD)

    @GET("user/profile")
    Call<AuthResponse> getUserProfile();



    // ── User Transactions ───────────────────────────────────────────

    @GET("user/transactions")
    Call<List<TransactionResponse>> getUserTransactions(@Query("page") int page, @Query("per_page") int perPage);

    @GET("user/transactions/{transaction_id}")
    Call<TransactionResponse> getUserTransaction(@Path("transaction_id") int transactionId);

    // ── User Analytics ─────────────────────────────────────────────

    @GET("user/analytics")
    Call<AnalyticsResponse> getUserAnalytics(@Query("days") int days);

    // ── Merchant/Vendor Management ──────────────────────────────────
    // Note: Profile update endpoints require UpdateMerchantRequest class (TBD)

    @GET("merchant/profile")
    Call<MerchantResponse> getMerchantProfile();


    // ── Merchant Transactions ───────────────────────────────────────

    @GET("merchant/transactions")
    Call<List<TransactionResponse>> getMerchantTransactions(@Query("page") int page, @Query("per_page") int perPage);

    @GET("merchant/transactions/{transaction_id}")
    Call<TransactionResponse> getMerchantTransaction(@Path("transaction_id") int transactionId);

    // ── Merchant Analytics ──────────────────────────────────────────

    @GET("merchant/analytics")
    Call<AnalyticsResponse> getMerchantAnalytics(@Query("days") int days);

    // ── QR Code Management ──────────────────────────────────────────

    @POST("qr/generate")
    Call<GenerateQRResponse> generateQRCode(@Body com.scanpay.app.data.request.GenerateQRRequest request);

    @GET("qr/image/{qr_id}")
    Call<QRImageResponse> generateQRCodeImage(@Path("qr_id") int qrId);

    @GET("qr/image/file/{file_name}")
    Call<ResponseBody> getQRCodeImageFile(@Path("file_name") String fileName);

    @POST("qr/scan")
    Call<QRScanResponse> scanQRCode(@Body com.scanpay.app.data.request.ScanQRRequest request);

    // ── Payment Processing ──────────────────────────────────────────

    @POST("payment/initiate")
    Call<TransactionResponse> initiatePayment(@Body PaymentRequest request);

    @GET("payment/{transaction_id}/status")
    Call<PaymentStatusResponse> checkPaymentStatus(@Path("transaction_id") int transactionId);

    // ── Top-up / Service Tokens ─────────────────────────────────────

    @GET("topup/balance")
    Call<TopUpBalanceResponse> getTopUpBalance();

    @POST("topup/initiate")
    Call<TopUpResponse> initiateTopUp(@Body com.scanpay.app.data.request.TopUpRequest request);
}
