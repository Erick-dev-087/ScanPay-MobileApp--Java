package com.scanpay.app.ui.payment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.scanpay.app.R;
import com.scanpay.app.api.ApiClient;
import com.scanpay.app.api.ApiService;
import com.scanpay.app.data.model.Transaction;
import com.scanpay.app.data.request.PaymentRequest;
import com.scanpay.app.data.response.PaymentStatusResponse;
import com.scanpay.app.data.response.TransactionResponse;
import com.scanpay.app.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentConfirmationActivity extends AppCompatActivity {

    private static final int MAX_STATUS_CHECK_ATTEMPTS = 24;
    private static final long STATUS_CHECK_INTERVAL_MS = 5000L;

    private ImageView btnBack;
    private TextView tvMerchantName, tvMerchantCode;
    private TextView tvPaymentStatus;
    private TextInputLayout tilAmount;
    private TextInputEditText etAmount;
    private Button btnPayNow;
    private ProgressBar progressBar;

    private int qrCodeId;
    private int vendorId;
    private String merchantCode;
    private String merchantName;
    private String presetAmount;

    private ApiService apiService;
    private final Handler statusPollHandler = new Handler(Looper.getMainLooper());

    private boolean awaitingFinalStatus = false;
    private int statusCheckAttempts = 0;
    private int currentTransactionId = 0;
    private double initiatedAmount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_confirmation);

        apiService = ApiClient.getApiService();

        // Get intent extras (from backend verified scan response)
        qrCodeId = getIntent().getIntExtra(Constants.EXTRA_QR_CODE_ID, 0);
        vendorId = getIntent().getIntExtra(Constants.EXTRA_VENDOR_ID, 0);
        merchantCode = getIntent().getStringExtra(Constants.EXTRA_MERCHANT_CODE);
        merchantName = getIntent().getStringExtra(Constants.EXTRA_MERCHANT_NAME);
        presetAmount = getIntent().getStringExtra(Constants.EXTRA_AMOUNT);

        initViews();
        setupUI();
        setupClickListeners();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        tvMerchantName = findViewById(R.id.tv_merchant_name);
        tvMerchantCode = findViewById(R.id.tv_merchant_code);
        tvPaymentStatus = findViewById(R.id.tv_payment_status);
        tilAmount = findViewById(R.id.til_amount);
        etAmount = findViewById(R.id.et_amount);
        btnPayNow = findViewById(R.id.btn_pay_now);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void setupUI() {
        tvMerchantName.setText(merchantName != null ? merchantName : "Unknown Merchant");
        tvMerchantCode.setText(merchantCode != null ? "Till: " + merchantCode : "");

        // If amount is preset (dynamic QR), show it and disable editing
        if (presetAmount != null && !presetAmount.isEmpty()) {
            etAmount.setText(presetAmount);
            etAmount.setEnabled(false);
        }
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> onBackPressed());

        btnPayNow.setOnClickListener(v -> initiatePayment());
    }

    private void initiatePayment() {
        String amountStr = etAmount.getText() != null ? etAmount.getText().toString().trim() : "";

        if (amountStr.isEmpty()) {
            tilAmount.setError(getString(R.string.error_empty_amount));
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                tilAmount.setError(getString(R.string.error_invalid_amount));
                return;
            }
        } catch (NumberFormatException e) {
            tilAmount.setError(getString(R.string.error_invalid_amount));
            return;
        }

        // Validate we have a QR code ID from backend verification
        if (qrCodeId <= 0) {
            Toast.makeText(this, "Error: Invalid QR code. Please scan again.", Toast.LENGTH_SHORT).show();
            return;
        }

        tilAmount.setError(null);
        initiatedAmount = amount;
        setLoading(true);
        updatePaymentStatus(getString(R.string.payment_initiating), true);

        // Use qr_code_id from backend verification (already validated and safe)
        PaymentRequest request = new PaymentRequest(qrCodeId, amount);

        apiService.initiatePayment(request)
                .enqueue(new Callback<TransactionResponse>() {
                    @Override
                    public void onResponse(Call<TransactionResponse> call, Response<TransactionResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            TransactionResponse transactionResponse = response.body();
                            handleInitiationResponse(transactionResponse);
                        } else {
                            setLoading(false);
                            updatePaymentStatus(getString(R.string.payment_initiation_failed), true);
                            Toast.makeText(PaymentConfirmationActivity.this,
                                    "Payment initiation failed: " + response.message(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<TransactionResponse> call, Throwable t) {
                        setLoading(false);
                        updatePaymentStatus(getString(R.string.payment_initiation_failed), true);
                        Toast.makeText(PaymentConfirmationActivity.this,
                                "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void handleInitiationResponse(TransactionResponse response) {
        int transactionId = extractTransactionId(response);
        if (transactionId <= 0) {
            setLoading(false);
            updatePaymentStatus(getString(R.string.payment_missing_transaction_reference), true);
            Toast.makeText(this,
                    getString(R.string.payment_missing_transaction_reference),
                    Toast.LENGTH_LONG).show();
            return;
        }

        currentTransactionId = transactionId;
        if (response.getAmount() != null && response.getAmount() > 0) {
            initiatedAmount = response.getAmount();
        }

        String instructions = response.getInstructions();
        if (instructions == null || instructions.trim().isEmpty()) {
            instructions = getString(R.string.payment_check_phone_instruction);
        }

        updatePaymentStatus(instructions, true);
        startStatusPolling();
    }

    private void startStatusPolling() {
        awaitingFinalStatus = true;
        statusCheckAttempts = 0;
        btnPayNow.setText(R.string.checking_payment_status);
        pollTransactionStatus();
    }

    private void pollTransactionStatus() {
        statusCheckAttempts++;
        apiService.checkPaymentStatus(currentTransactionId)
                .enqueue(new Callback<PaymentStatusResponse>() {
                    @Override
                    public void onResponse(Call<PaymentStatusResponse> call, Response<PaymentStatusResponse> response) {
                        if (!awaitingFinalStatus) {
                            return;
                        }

                        if (response.code() == 401 || response.code() == 403) {
                            completePolling();
                            setLoading(false);
                            btnPayNow.setText(R.string.pay_now);
                            updatePaymentStatus(getString(R.string.payment_status_unauthorized), true);
                            Toast.makeText(PaymentConfirmationActivity.this,
                                    getString(R.string.payment_status_unauthorized),
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (!response.isSuccessful() || response.body() == null) {
                            handlePendingOrTimeout(getString(R.string.payment_status_waiting_backend));
                            return;
                        }

                        PaymentStatusResponse statusResponse = response.body();
                        String normalizedStatus = normalizeStatus(statusResponse.getStatus());

                        if (isSuccessStatus(normalizedStatus)) {
                            completePolling();
                            double resolvedAmount = statusResponse.getAmount() != null && statusResponse.getAmount() > 0
                                    ? statusResponse.getAmount()
                                    : initiatedAmount;
                            navigateToSuccess(currentTransactionId, resolvedAmount);
                            return;
                        }

                        if (isFailedStatus(normalizedStatus)) {
                            completePolling();
                            setLoading(false);
                            btnPayNow.setText(R.string.pay_now);
                            updatePaymentStatus(getString(R.string.payment_failed_with_status, normalizedStatus), true);
                            Toast.makeText(PaymentConfirmationActivity.this,
                                    getString(R.string.payment_failed_check_status),
                                    Toast.LENGTH_LONG).show();
                            return;
                        }

                        handlePendingOrTimeout(getString(R.string.payment_waiting_for_confirmation));
                    }

                    @Override
                    public void onFailure(Call<PaymentStatusResponse> call, Throwable t) {
                        if (!awaitingFinalStatus) {
                            return;
                        }
                        handlePendingOrTimeout(getString(R.string.payment_status_waiting_backend));
                    }
                });
    }

    private void handlePendingOrTimeout(String pendingMessage) {
        if (statusCheckAttempts >= MAX_STATUS_CHECK_ATTEMPTS) {
            completePolling();
            setLoading(false);
            btnPayNow.setText(R.string.pay_now);
            updatePaymentStatus(getString(R.string.payment_status_timeout), true);
            Toast.makeText(this, getString(R.string.payment_status_timeout), Toast.LENGTH_LONG).show();
            return;
        }

        updatePaymentStatus(pendingMessage, true);
        statusPollHandler.postDelayed(this::pollTransactionStatus, STATUS_CHECK_INTERVAL_MS);
    }

    private void completePolling() {
        awaitingFinalStatus = false;
        statusPollHandler.removeCallbacksAndMessages(null);
    }

    private int extractTransactionId(TransactionResponse response) {
        if (response.getTransactionId() != null && response.getTransactionId() > 0) {
            return response.getTransactionId();
        }

        Transaction transaction = response.getTransaction();
        if (transaction != null) {
            if (transaction.getId() > 0) {
                return transaction.getId();
            }

            String transactionRef = transaction.getTransactionId();
            if (transactionRef != null) {
                try {
                    return Integer.parseInt(transactionRef);
                } catch (NumberFormatException ignored) {
                    // Keep checking other fallbacks.
                }
            }
        }

        return 0;
    }

    private String normalizeStatus(String status) {
        return status == null ? "" : status.trim().toLowerCase();
    }

    private boolean isSuccessStatus(String status) {
        return "success".equals(status) || "completed".equals(status);
    }

    private boolean isFailedStatus(String status) {
        return "failed".equals(status)
                || "cancelled".equals(status)
                || "canceled".equals(status)
                || "timed_out".equals(status)
                || "timeout".equals(status)
                || "expired".equals(status);
    }

    private void navigateToSuccess(int transactionId, double amount) {
        Intent intent = new Intent(this, PaymentSuccessActivity.class);
        intent.putExtra(Constants.EXTRA_MERCHANT_NAME, merchantName);
        intent.putExtra(Constants.EXTRA_AMOUNT, String.valueOf(amount));
        intent.putExtra(Constants.EXTRA_TRANSACTION_ID, String.valueOf(transactionId));
        startActivity(intent);
        finish();
    }

    private void updatePaymentStatus(String statusText, boolean visible) {
        tvPaymentStatus.setText(statusText);
        tvPaymentStatus.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnPayNow.setEnabled(!isLoading);
        etAmount.setEnabled(!isLoading && (presetAmount == null || presetAmount.isEmpty()));
    }

    @Override
    public void onBackPressed() {
        if (awaitingFinalStatus) {
            Toast.makeText(this, getString(R.string.payment_waiting_for_confirmation), Toast.LENGTH_SHORT).show();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        completePolling();
        super.onDestroy();
    }
}

