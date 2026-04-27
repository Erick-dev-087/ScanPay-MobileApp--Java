package com.scanpay.app.ui.payment;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.scanpay.app.R;
import com.scanpay.app.api.ApiClient;
import com.scanpay.app.api.ApiService;
import com.scanpay.app.data.request.PaymentRequest;
import com.scanpay.app.data.response.TransactionResponse;
import com.scanpay.app.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Test Activity for Payment Service
 * Allows testing the payment initiation endpoint from the mobile app
 * without using curl or Python scripts.
 */
public class PaymentTestActivity extends AppCompatActivity {

    private EditText etQRCodeId, etAmount;
    private Button btnInitiatePayment;
    private ProgressBar progressBar;
    private TextView tvResponse;
    private ScrollView svResponse;

    private ApiService apiService;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_test);

        apiService = ApiClient.getApiService();
        sessionManager = new SessionManager(this);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        etQRCodeId = findViewById(R.id.et_qr_code_id);
        etAmount = findViewById(R.id.et_amount);
        btnInitiatePayment = findViewById(R.id.btn_initiate_payment);
        progressBar = findViewById(R.id.progress_bar);
        tvResponse = findViewById(R.id.tv_response);
        svResponse = findViewById(R.id.sv_response);
    }

    private void setupClickListeners() {
        btnInitiatePayment.setOnClickListener(v -> testPaymentInitiation());
    }

    private void testPaymentInitiation() {
        String qrCodeIdStr = etQRCodeId.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();

        // Validate inputs
        if (qrCodeIdStr.isEmpty()) {
            showError("Please enter QR Code ID");
            return;
        }

        if (amountStr.isEmpty()) {
            showError("Please enter Amount");
            return;
        }

        int qrCodeId;
        double amount;

        try {
            qrCodeId = Integer.parseInt(qrCodeIdStr);
            amount = Double.parseDouble(amountStr);

            if (amount <= 0) {
                showError("Amount must be greater than 0");
                return;
            }
        } catch (NumberFormatException e) {
            showError("Invalid input: QR Code ID must be integer, Amount must be number");
            return;
        }

        // Get user phone from session
        String userPhone = sessionManager.getUserPhone();
        if (userPhone == null || userPhone.isEmpty()) {
            showError("User phone not found in session. Please login first.");
            return;
        }

        setLoading(true);
        logResponse("Testing payment initiation...\n" +
                "QR Code ID: " + qrCodeId + "\n" +
                "Amount: " + amount + "\n" +
                "User Phone: " + userPhone + "\n" +
                "Sending request to backend...\n");

        // Create payment request with qr_code_id
        // Note: PaymentRequest needs to be updated to support qr_code_id
        PaymentRequest request = new PaymentRequest();
        request.setMerchantCode(String.valueOf(qrCodeId)); // Using merchantCode field for QR ID temporarily
        request.setAmount(amount);
        request.setPhone(userPhone);

        apiService.initiatePayment(request)
                .enqueue(new Callback<TransactionResponse>() {
                    @Override
                    public void onResponse(Call<TransactionResponse> call, Response<TransactionResponse> response) {
                        setLoading(false);
                        if (response.isSuccessful() && response.body() != null) {
                            TransactionResponse transactionResponse = response.body();
                            logResponse("\n✓ SUCCESS - Status Code: " + response.code() + "\n\n");
                            logResponse("Response Body:\n");
                            logResponse("Message: " + transactionResponse.getMessage() + "\n");

                            if (transactionResponse.getTransaction() != null) {
                                logResponse("Transaction ID: " + transactionResponse.getTransaction().getTransactionId() + "\n");
                                logResponse("Amount: " + transactionResponse.getTransaction().getAmount() + "\n");
                                logResponse("Status: " + transactionResponse.getTransaction().getStatus() + "\n");
                            }

                            logResponse("Checkout Request ID: " + transactionResponse.getCheckoutRequestId() + "\n");

                            Toast.makeText(PaymentTestActivity.this,
                                    "Payment initiated successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            logResponse("\n✗ FAILED - Status Code: " + response.code() + "\n\n");
                            try {
                                logResponse("Response Body:\n" + response.errorBody().string() + "\n");
                            } catch (Exception e) {
                                logResponse("Error reading response: " + e.getMessage() + "\n");
                            }
                            showError("Request failed with code " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<TransactionResponse> call, Throwable t) {
                        setLoading(false);
                        logResponse("\n✗ NETWORK ERROR\n\n");
                        logResponse("Error: " + t.getMessage() + "\n");
                        logResponse("Cause: " + t.getCause() + "\n");
                        showError("Network error: " + t.getMessage());
                    }
                });
    }

    private void logResponse(String message) {
        tvResponse.append(message);
        // Auto-scroll to bottom
        svResponse.post(() -> svResponse.fullScroll(ScrollView.FOCUS_DOWN));
    }

    private void showError(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        logResponse("\n✗ ERROR: " + message + "\n");
    }

    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnInitiatePayment.setEnabled(!isLoading);
        etQRCodeId.setEnabled(!isLoading);
        etAmount.setEnabled(!isLoading);
    }
}

