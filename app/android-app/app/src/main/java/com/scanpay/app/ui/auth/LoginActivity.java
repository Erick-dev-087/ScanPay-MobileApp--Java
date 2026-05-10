package com.scanpay.app.ui.auth;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.scanpay.app.ui.base.BaseActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.scanpay.app.R;
import com.scanpay.app.ui.main.MainActivity;
import com.scanpay.app.ui.merchant.MerchantMainActivity;

public class LoginActivity extends BaseActivity {

    private static final String TAG = "LoginActivity";

    private TextInputLayout tilEmail, tilPassword;
    private TextInputEditText etEmail, etPassword;
    private Button btnLogin;
    private ProgressBar progressBar;
    private TextView tvSignUp, tvForgotPassword;

    private LoginViewModel viewModel;

    // Listens for session-expired broadcast from AuthInterceptor
    private final BroadcastReceiver sessionExpiredReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(LoginActivity.this,
                    "Session expired. Please log in again.", Toast.LENGTH_LONG).show();
        }
    };

    public static void startFresh(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate called");
        try {
            super.onCreate(savedInstanceState);
            Log.d(TAG, "super.onCreate completed");

            setContentView(R.layout.activity_login);
            Log.d(TAG, "setContentView completed");

            viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
            Log.d(TAG, "ViewModel created");

            initViews();
            Log.d(TAG, "initViews completed");

            setupClickListeners();
            Log.d(TAG, "setupClickListeners completed");

            observeViewModel();
            Log.d(TAG, "observeViewModel completed - onCreate finished");
        } catch (Exception e) {
            Log.e(TAG, "CRASH in onCreate", e);
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter("com.scanpay.app.SESSION_EXPIRED");
        ContextCompat.registerReceiver(this, sessionExpiredReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(sessionExpiredReceiver);
    }

    private void initViews() {
        tilEmail = findViewById(R.id.til_email);
        tilPassword = findViewById(R.id.til_password);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        progressBar = findViewById(R.id.progress_bar);
        tvSignUp = findViewById(R.id.tv_sign_up);
        tvForgotPassword = findViewById(R.id.tv_forgot_password);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> attemptLogin());

        tvSignUp.setOnClickListener(v ->
                startActivity(new Intent(this, UserTypeSelectionActivity.class)));

        tvForgotPassword.setOnClickListener(v ->
            startActivity(new Intent(this, ForgotPasswordActivity.class)));
    }

    private void observeViewModel() {
        // Observe validation errors
        viewModel.getEmailError().observe(this, error -> tilEmail.setError(error));
        viewModel.getPasswordError().observe(this, error -> tilPassword.setError(error));

        // Observe login result
        viewModel.getLoginResult().observe(this, resource -> {
            if (resource == null) {
                Log.w(TAG, "Login result is null");
                return;
            }

            Log.d(TAG, "Login result status: " + resource.getStatus());

            switch (resource.getStatus()) {
                case LOADING:
                    Log.d(TAG, "Login in progress...");
                    setLoading(true);
                    break;

                case SUCCESS:
                    Log.d(TAG, "Login successful! Checking user data validity...");
                    setLoading(false);

                    // Validate user data before navigation
                    if (viewModel.isUserDataValid()) {
                        Log.d(TAG, "User data is valid. User type: " + viewModel.getUserType());
                        com.scanpay.app.data.model.User user = viewModel.getLoggedInUser();
                        if (user != null) {
                            Log.d(TAG, "Logged-in user: " + user.getName() + " (" + user.getUserType() + ")");
                        }
                        Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
                        navigateToDashboard();
                    } else {
                        Log.e(TAG, "User data validation failed after successful login");
                        Toast.makeText(this, "Login successful but user data incomplete. Please try again.",
                                Toast.LENGTH_LONG).show();
                    }
                    break;

                case ERROR:
                    Log.e(TAG, "Login failed: " + resource.getMessage());
                    setLoading(false);
                    String msg = resource.getMessage() != null
                            ? resource.getMessage() : "Login failed. Please try again.";
                    Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                    break;
            }
        });
    }

    private void attemptLogin() {
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";
        viewModel.login(email, password);
    }

    private void navigateToDashboard() {
        // Validate that user data is properly saved in session before navigation
        if (!viewModel.isUserDataValid()) {
            Toast.makeText(this, "Error: User data not properly saved. Please try again.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // Determine the target dashboard based on user type
        Class<?> targetActivity = viewModel.getDashboardActivity();
        if (targetActivity == null) {
            Toast.makeText(this, "Error: Unable to determine user role. Please try again.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent(this, targetActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!isLoading);
        etEmail.setEnabled(!isLoading);
        etPassword.setEnabled(!isLoading);
    }
}

