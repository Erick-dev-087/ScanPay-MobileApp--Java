package com.scanpay.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.scanpay.app.R;
import com.scanpay.app.utils.Constants;

public class UserTypeSelectionActivity extends AppCompatActivity {

    private CardView cardCustomer, cardMerchant;
    private TextView tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_type_selection);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        cardCustomer = findViewById(R.id.card_customer);
        cardMerchant = findViewById(R.id.card_merchant);
        tvLogin = findViewById(R.id.tv_login);
    }

    private void setupClickListeners() {
        cardCustomer.setOnClickListener(v -> {
            navigateToRegister(Constants.USER_TYPE_CUSTOMER);
        });

        cardMerchant.setOnClickListener(v -> {
            navigateToRegister(Constants.USER_TYPE_MERCHANT);
        });

        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void navigateToRegister(String userType) {
        Intent intent = new Intent(this, RegisterActivity.class);
        intent.putExtra(Constants.EXTRA_USER_TYPE, userType);
        startActivity(intent);
    }
}

