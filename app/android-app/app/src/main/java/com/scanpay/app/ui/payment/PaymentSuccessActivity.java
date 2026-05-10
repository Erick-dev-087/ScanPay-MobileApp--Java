package com.scanpay.app.ui.payment;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;

import com.scanpay.app.ui.base.BaseActivity;

import com.scanpay.app.R;
import com.scanpay.app.ui.main.MainActivity;
import com.scanpay.app.ui.merchant.MerchantMainActivity;
import com.scanpay.app.utils.Constants;
import com.scanpay.app.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PaymentSuccessActivity extends BaseActivity {

    private TextView tvMerchantName, tvAmount, tvTransactionId, tvDateTime;
    private Button btnDone;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_success);
        sessionManager = new SessionManager(this);

        initViews();
        displayPaymentDetails();
        setupClickListeners();

        // Route system back to dashboard from this terminal screen.
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                goToDashboard();
            }
        });
    }

    private void initViews() {
        tvMerchantName = findViewById(R.id.tv_merchant_name);
        tvAmount = findViewById(R.id.tv_amount);
        tvTransactionId = findViewById(R.id.tv_transaction_id);
        tvDateTime = findViewById(R.id.tv_date_time);
        btnDone = findViewById(R.id.btn_done);
    }

    private void displayPaymentDetails() {
        String merchantName = getIntent().getStringExtra(Constants.EXTRA_MERCHANT_NAME);
        String amount = getIntent().getStringExtra(Constants.EXTRA_AMOUNT);
        String transactionId = getIntent().getStringExtra(Constants.EXTRA_TRANSACTION_ID);

        tvMerchantName.setText(merchantName != null ? merchantName : "Merchant");

        if (amount != null) {
            try {
                double amountValue = Double.parseDouble(amount);
                tvAmount.setText(String.format(Locale.getDefault(), "%,.0f", amountValue));
            } catch (NumberFormatException e) {
                tvAmount.setText(amount);
            }
        }

        tvTransactionId.setText(transactionId != null ? transactionId : "N/A");

        // Current date/time
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
        tvDateTime.setText(sdf.format(new Date()));
    }

    private void setupClickListeners() {
        btnDone.setOnClickListener(v -> goToDashboard());
    }

    private void goToDashboard() {
        Class<?> dashboardClass = sessionManager.isMerchant()
                ? MerchantMainActivity.class
                : MainActivity.class;

        Intent intent = new Intent(this, dashboardClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

