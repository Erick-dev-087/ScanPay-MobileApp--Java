package com.scanpay.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.scanpay.app.ui.base.BaseActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.scanpay.app.R;
import com.scanpay.app.ui.main.MainActivity;
import com.scanpay.app.ui.merchant.MerchantMainActivity;
import com.scanpay.app.utils.Constants;

import java.util.Map;

public class RegisterActivity extends BaseActivity {

    private ImageView btnBack;
    private TextView tvUserType, tvLogin, tvPaymentMethodLabel;
    private TextInputLayout tilName, tilBusinessName, tilShortCode, tilMerchantId;
    private TextInputLayout tilMcc, tilStoreLabel, tilEmail, tilPhone, tilPassword, tilConfirmPassword;
    private TextInputLayout tilBusinessNumber;
    private TextInputEditText etName, etBusinessName, etShortCode, etMerchantId;
    private TextInputEditText etStoreLabel, etEmail, etPhone, etPassword, etConfirmPassword;
    private TextInputEditText etBusinessNumber;
    private AutoCompleteTextView dropdownMcc;
    private RadioGroup radioGroupPaymentMethod;
    private RadioButton rbTill, rbPaybill;
    private Button btnRegister;
    private ProgressBar progressBar;

    private String userType;
    private RegisterViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userType = getIntent().getStringExtra(Constants.EXTRA_USER_TYPE);
        if (userType == null) userType = Constants.USER_TYPE_CUSTOMER;

        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        initViews();
        setupUI();
        setupClickListeners();
        observeViewModel();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back);
        tvUserType = findViewById(R.id.tv_user_type);
        tvLogin = findViewById(R.id.tv_login);
        tvPaymentMethodLabel = findViewById(R.id.tv_payment_method_label);

        tilName = findViewById(R.id.til_name);
        tilBusinessName = findViewById(R.id.til_business_name);
        tilShortCode = findViewById(R.id.til_short_code);
        tilMerchantId = findViewById(R.id.til_merchant_id);
        tilMcc = findViewById(R.id.til_mcc);
        tilStoreLabel = findViewById(R.id.til_store_label);
        tilBusinessNumber = findViewById(R.id.til_business_number);
        tilEmail = findViewById(R.id.til_email);
        tilPhone = findViewById(R.id.til_phone);
        tilPassword = findViewById(R.id.til_password);
        tilConfirmPassword = findViewById(R.id.til_confirm_password);

        etName = findViewById(R.id.et_name);
        etBusinessName = findViewById(R.id.et_business_name);
        etShortCode = findViewById(R.id.et_short_code);
        etMerchantId = findViewById(R.id.et_merchant_id);
        etStoreLabel = findViewById(R.id.et_store_label);
        etBusinessNumber = findViewById(R.id.et_business_number);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        dropdownMcc = findViewById(R.id.dropdown_mcc);

        radioGroupPaymentMethod = findViewById(R.id.rg_payment_method);
        rbTill = findViewById(R.id.rb_till);
        rbPaybill = findViewById(R.id.rb_paybill);

        btnRegister = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progress_bar);
    }

    private void setupUI() {
        boolean isMerchant = Constants.USER_TYPE_MERCHANT.equals(userType);

        tvUserType.setText(isMerchant ? "Merchant Registration" : "Customer Registration");

        // Show/hide merchant-specific fields
        int merchantVisibility = isMerchant ? View.VISIBLE : View.GONE;
        tilBusinessName.setVisibility(merchantVisibility);
        tilShortCode.setVisibility(merchantVisibility);
        tilMerchantId.setVisibility(merchantVisibility);
        tilMcc.setVisibility(merchantVisibility);
        tilStoreLabel.setVisibility(merchantVisibility);
        radioGroupPaymentMethod.setVisibility(merchantVisibility);
        tvPaymentMethodLabel.setVisibility(merchantVisibility);
        tilBusinessNumber.setVisibility(merchantVisibility);

        // Setup MCC dropdown
        if (isMerchant) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    this, android.R.layout.simple_dropdown_item_1line, Constants.MCC_CODES);
            dropdownMcc.setAdapter(adapter);

            // Setup radio button listener for payment method type
            radioGroupPaymentMethod.setOnCheckedChangeListener((group, checkedId) -> {
                if (checkedId == R.id.rb_paybill) {
                    // Show business number field for Paybill
                    tilBusinessNumber.setVisibility(View.VISIBLE);
                } else {
                    // Hide business number field for Till
                    tilBusinessNumber.setVisibility(View.GONE);
                }
            });
        }
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        btnRegister.setOnClickListener(v -> attemptRegister());
    }

    private void observeViewModel() {
        // Observe per-field validation errors
        viewModel.getFieldErrors().observe(this, errors -> {
            if (errors == null) return;
            clearAllErrors();

            for (Map.Entry<String, String> entry : errors.entrySet()) {
                switch (entry.getKey()) {
                    case "name":            tilName.setError(entry.getValue()); break;
                    case "email":           tilEmail.setError(entry.getValue()); break;
                    case "phone":           tilPhone.setError(entry.getValue()); break;
                    case "password":        tilPassword.setError(entry.getValue()); break;
                    case "confirmPassword": tilConfirmPassword.setError(entry.getValue()); break;
                    case "businessName":    tilBusinessName.setError(entry.getValue()); break;
                    case "shortCode":       tilShortCode.setError(entry.getValue()); break;
                }
            }
        });

        // Observe customer registration result
        viewModel.getCustomerRegResult().observe(this, resource -> {
            if (resource == null) return;
            switch (resource.getStatus()) {
                case LOADING:
                    setLoading(true);
                    break;
                case SUCCESS:
                    setLoading(false);
                    Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                    navigateToDashboard();
                    break;
                case ERROR:
                    setLoading(false);
                    String msg = resource.getMessage() != null
                            ? resource.getMessage() : "Registration failed.";
                    Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                    break;
            }
        });

        // Observe vendor registration result
        viewModel.getVendorRegResult().observe(this, resource -> {
            if (resource == null) return;
            switch (resource.getStatus()) {
                case LOADING:
                    setLoading(true);
                    break;
                case SUCCESS:
                    setLoading(false);
                    Toast.makeText(this, "Registration successful!", Toast.LENGTH_SHORT).show();
                    navigateToDashboard();
                    break;
                case ERROR:
                    setLoading(false);
                    String msg = resource.getMessage() != null
                            ? resource.getMessage() : "Registration failed.";
                    Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                    break;
            }
        });
    }

    private void attemptRegister() {
        String name = getText(etName);
        String email = getText(etEmail);
        String phone = getText(etPhone);
        String password = getText(etPassword);
        String confirmPassword = getText(etConfirmPassword);

        if (Constants.USER_TYPE_MERCHANT.equals(userType)) {
            String businessName = getText(etBusinessName);
            String shortCode = getText(etShortCode);
            String merchantId = getText(etMerchantId);
            // Extract only the MCC code (e.g., "5411" from "5411 - Grocery Stores")
            String mccFullText = dropdownMcc.getText() != null ? dropdownMcc.getText().toString().trim() : "";
            String mcc = mccFullText.contains(" - ") ? mccFullText.split(" - ")[0].trim() : mccFullText;
            String storeLabel = getText(etStoreLabel);

            // Get payment method type (TILL or PAYBILL)
            String shortcodeType = rbPaybill.isChecked() ? Constants.PAYMENT_METHOD_PAYBILL : Constants.PAYMENT_METHOD_TILL;

            // Get account number (only for Paybill)
            String paybillAccountNumber = rbPaybill.isChecked() ? getText(etBusinessNumber) : "";

            viewModel.registerMerchant(name, email, phone, password, confirmPassword,
                    businessName, shortCode, merchantId, mcc, storeLabel,
                    shortcodeType, paybillAccountNumber);
        } else {
            viewModel.registerCustomer(name, email, phone, password, confirmPassword);
        }
    }

    private void navigateToDashboard() {
        Intent intent;
        if (Constants.USER_TYPE_MERCHANT.equals(userType)) {
            intent = new Intent(this, MerchantMainActivity.class);
        } else {
            intent = new Intent(this, MainActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void clearAllErrors() {
        tilName.setError(null);
        tilEmail.setError(null);
        tilPhone.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);
        tilBusinessName.setError(null);
        tilShortCode.setError(null);
        tilMerchantId.setError(null);
        tilMcc.setError(null);
        tilStoreLabel.setError(null);
    }

    private String getText(TextInputEditText editText) {
        return editText.getText() != null ? editText.getText().toString().trim() : "";
    }

    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnRegister.setEnabled(!isLoading);
    }
}

