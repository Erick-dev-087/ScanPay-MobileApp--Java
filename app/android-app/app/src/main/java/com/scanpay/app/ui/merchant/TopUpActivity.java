package com.scanpay.app.ui.merchant;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.scanpay.app.R;
import com.scanpay.app.api.ApiClient;
import com.scanpay.app.api.ApiService;
import com.scanpay.app.data.request.TopUpRequest;
import com.scanpay.app.data.response.PaymentStatusResponse;
import com.scanpay.app.data.response.TopUpResponse;
import com.scanpay.app.ui.base.BaseActivity;
import com.scanpay.app.utils.Constants;
import com.scanpay.app.utils.SessionManager;
import com.scanpay.app.utils.ValidationUtils;

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TopUpActivity extends BaseActivity {

    private static final int MAX_STATUS_CHECK_ATTEMPTS = 24;
    private static final long STATUS_CHECK_INTERVAL_MS = 5000L;
    private static final double[] PRESET_AMOUNTS = {500, 1000, 2000, 5000};

    private ImageView btnBack;
    private ImageButton btnThemeToggle;
    private TextInputEditText etPhone;
    private TextInputEditText etAmount;
    private TextView tvTopUpStatus;
    private MaterialButton btnInitiatePayment;
    private ProgressBar progressTopUp;
    private BottomNavigationView bottomNavigation;

    private final List<TextView> presetButtons = new java.util.ArrayList<>();

    private ApiService apiService;
    private SessionManager sessionManager;
    private boolean isProgrammaticNav;

    private final Handler statusPollHandler = new Handler(Looper.getMainLooper());
    private boolean awaitingFinalStatus = false;
    private int statusCheckAttempts = 0;
    private int currentTransactionId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topup);

        apiService = ApiClient.getApiService();
        sessionManager = new SessionManager(this);

        initViews();
        setupPresetAmounts();
        setupClickListeners();
        setupBottomNavigation();
        prefillPhoneNumber();
        selectPresetAmount(1000);
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        btnThemeToggle = findViewById(R.id.btn_theme_toggle);
        etPhone = findViewById(R.id.et_phone);
        etAmount = findViewById(R.id.et_amount);
        tvTopUpStatus = findViewById(R.id.tv_topup_status);
        btnInitiatePayment = findViewById(R.id.btn_initiate_payment);
        progressTopUp = findViewById(R.id.progress_topup);
        bottomNavigation = findViewById(R.id.bottom_navigation);

        presetButtons.add(findViewById(R.id.btn_amount_500));
        presetButtons.add(findViewById(R.id.btn_amount_1000));
        presetButtons.add(findViewById(R.id.btn_amount_2000));
        presetButtons.add(findViewById(R.id.btn_amount_5000));
    }

    private void prefillPhoneNumber() {
        String phone = sessionManager.getUserPhone();
        if (!TextUtils.isEmpty(phone)) {
            if (phone.startsWith("254")) {
                etPhone.setText("+254 " + phone.substring(3));
            } else if (phone.startsWith("0")) {
                etPhone.setText("+254 " + phone.substring(1));
            } else {
                etPhone.setText("+254 " + phone);
            }
        }
    }

    private void setupPresetAmounts() {
        for (int i = 0; i < presetButtons.size(); i++) {
            TextView button = presetButtons.get(i);
            double amount = PRESET_AMOUNTS[i];
            button.setTag(amount);
            button.setOnClickListener(v -> selectPresetAmount(amount));
        }
    }

    private void selectPresetAmount(double amount) {
        etAmount.setText(String.valueOf((int) amount));

        for (TextView button : presetButtons) {
            Object tag = button.getTag();
            boolean selected = tag instanceof Double && ((Double) tag) == amount;
            button.setSelected(selected);
            button.setTextColor(ContextCompat.getColor(this,
                    selected ? R.color.ui_primary : R.color.ui_on_surface));
        }
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> onBackPressed());
        btnThemeToggle.setOnClickListener(v -> toggleThemePreference());
        btnInitiatePayment.setOnClickListener(v -> initiateTopUp());

        etAmount.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                clearPresetSelection();
            }
        });
    }

    private void clearPresetSelection() {
        for (TextView button : presetButtons) {
            button.setSelected(false);
            button.setTextColor(ContextCompat.getColor(this, R.color.ui_on_surface));
        }
    }

    private void setupBottomNavigation() {
        isProgrammaticNav = true;
        bottomNavigation.setSelectedItemId(R.id.navigation_analytics);
        isProgrammaticNav = false;

        bottomNavigation.setOnItemSelectedListener(item -> {
            if (isProgrammaticNav) {
                return true;
            }

            int itemId = item.getItemId();
            if (itemId == R.id.navigation_dashboard) {
                finish();
                return true;
            } else if (itemId == R.id.navigation_qr_codes) {
                navigateToMerchantTab(R.id.navigation_qr_codes);
                return true;
            } else if (itemId == R.id.navigation_analytics) {
                return true;
            } else if (itemId == R.id.navigation_more) {
                navigateToMerchantTab(R.id.navigation_more);
                return true;
            }
            return false;
        });
    }

    private void navigateToMerchantTab(int navItemId) {
        Intent intent = new Intent(this, MerchantMainActivity.class);
        intent.putExtra(Constants.EXTRA_NAV_TARGET, navItemId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private void initiateTopUp() {
        String phoneRaw = etPhone.getText() != null ? etPhone.getText().toString().trim() : "";
        String amountStr = etAmount.getText() != null ? etAmount.getText().toString().trim() : "";

        if (!ValidationUtils.isValidKenyanPhone(phoneRaw)) {
            Toast.makeText(this, R.string.error_invalid_phone, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!ValidationUtils.isValidAmount(amountStr)) {
            Toast.makeText(this, R.string.error_invalid_amount, Toast.LENGTH_SHORT).show();
            return;
        }

        String normalizedPhone = ValidationUtils.normalizeKenyanPhone(phoneRaw);
        double amount = ValidationUtils.parseAmount(amountStr);

        setLoading(true);
        updateStatus(getString(R.string.top_up_initiating), true);

        TopUpRequest request = new TopUpRequest(normalizedPhone, amount);
        apiService.initiateTopUp(request).enqueue(new Callback<TopUpResponse>() {
            @Override
            public void onResponse(Call<TopUpResponse> call, Response<TopUpResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    handleInitiationResponse(response.body());
                } else {
                    setLoading(false);
                    updateStatus(getString(R.string.top_up_initiation_failed), true);
                    Toast.makeText(TopUpActivity.this,
                            getString(R.string.top_up_initiation_failed), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TopUpResponse> call, Throwable t) {
                setLoading(false);
                updateStatus(getString(R.string.error_network_try_again), true);
                Toast.makeText(TopUpActivity.this,
                        getString(R.string.error_network_try_again), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleInitiationResponse(TopUpResponse response) {
        Integer transactionId = response.getTransactionId();
        if (transactionId == null || transactionId <= 0) {
            setLoading(false);
            updateStatus(getString(R.string.payment_missing_transaction_reference), true);
            return;
        }

        currentTransactionId = transactionId;

        String instructions = response.getInstructions();
        if (TextUtils.isEmpty(instructions)) {
            instructions = getString(R.string.payment_check_phone_instruction);
        }
        updateStatus(instructions, true);

        if (response.getBalance() != null) {
            setResultBalance(response.getBalance());
        }

        startStatusPolling();
    }

    private void startStatusPolling() {
        awaitingFinalStatus = true;
        statusCheckAttempts = 0;
        btnInitiatePayment.setText(R.string.checking_payment_status);
        pollTransactionStatus();
    }

    private void pollTransactionStatus() {
        statusCheckAttempts++;
        apiService.checkPaymentStatus(currentTransactionId).enqueue(new Callback<PaymentStatusResponse>() {
            @Override
            public void onResponse(Call<PaymentStatusResponse> call, Response<PaymentStatusResponse> response) {
                if (!awaitingFinalStatus) {
                    return;
                }

                if (response.code() == 401 || response.code() == 403) {
                    completePolling();
                    setLoading(false);
                    btnInitiatePayment.setText(R.string.initiate_payment);
                    updateStatus(getString(R.string.payment_status_unauthorized), true);
                    return;
                }

                if (!response.isSuccessful() || response.body() == null) {
                    handlePendingOrTimeout(getString(R.string.payment_status_waiting_backend));
                    return;
                }

                String normalizedStatus = normalizeStatus(response.body().getStatus());
                if (isSuccessStatus(normalizedStatus)) {
                    completePolling();
                    setLoading(false);
                    btnInitiatePayment.setText(R.string.initiate_payment);
                    updateStatus(getString(R.string.top_up_success), true);
                    Toast.makeText(TopUpActivity.this, R.string.top_up_success, Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    return;
                }

                if (isFailedStatus(normalizedStatus)) {
                    completePolling();
                    setLoading(false);
                    btnInitiatePayment.setText(R.string.initiate_payment);
                    updateStatus(getString(R.string.payment_failed_with_status, normalizedStatus), true);
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
            btnInitiatePayment.setText(R.string.initiate_payment);
            updateStatus(getString(R.string.payment_status_timeout), true);
            return;
        }

        updateStatus(pendingMessage, true);
        statusPollHandler.postDelayed(this::pollTransactionStatus, STATUS_CHECK_INTERVAL_MS);
    }

    private void completePolling() {
        awaitingFinalStatus = false;
        statusPollHandler.removeCallbacksAndMessages(null);
    }

    private void setResultBalance(double balance) {
        Intent data = new Intent();
        data.putExtra(Constants.EXTRA_SERVICE_TOKEN_BALANCE, balance);
        setResult(RESULT_OK, data);
    }

    private String normalizeStatus(String status) {
        return status == null ? "" : status.trim().toLowerCase();
    }

    private boolean isSuccessStatus(String status) {
        return "success".equals(status) || "completed".equals(status);
    }

    private boolean isFailedStatus(String status) {
        return Arrays.asList("failed", "cancelled", "canceled", "timed_out", "timeout", "expired")
                .contains(status);
    }

    private void updateStatus(String statusText, boolean visible) {
        tvTopUpStatus.setText(statusText);
        tvTopUpStatus.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void setLoading(boolean isLoading) {
        progressTopUp.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnInitiatePayment.setEnabled(!isLoading);
        etPhone.setEnabled(!isLoading);
        etAmount.setEnabled(!isLoading);
        for (TextView button : presetButtons) {
            button.setEnabled(!isLoading);
        }
    }

    @Override
    public void onBackPressed() {
        if (awaitingFinalStatus) {
            Toast.makeText(this, R.string.payment_waiting_for_confirmation, Toast.LENGTH_SHORT).show();
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
