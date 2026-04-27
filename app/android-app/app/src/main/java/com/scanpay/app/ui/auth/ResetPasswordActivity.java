package com.scanpay.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.scanpay.app.R;
import com.scanpay.app.api.ApiClient;
import com.scanpay.app.api.ApiService;
import com.scanpay.app.data.request.ResetPasswordRequest;
import com.scanpay.app.data.response.MessageResponse;
import com.scanpay.app.utils.Constants;
import com.scanpay.app.utils.ValidationUtils;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    private TextInputLayout tilToken;
    private TextInputLayout tilNewPassword;
    private TextInputLayout tilConfirmPassword;

    private TextInputEditText etToken;
    private TextInputEditText etNewPassword;
    private TextInputEditText etConfirmPassword;

    private Button btnResetPassword;
    private ProgressBar progressBar;
    private TextView tvBackToLogin;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        apiService = ApiClient.getApiService();

        initViews();
        prefillTokenIfAvailable();
        setupClickListeners();
    }

    private void initViews() {
        tilToken = findViewById(R.id.til_token);
        tilNewPassword = findViewById(R.id.til_new_password);
        tilConfirmPassword = findViewById(R.id.til_confirm_password);

        etToken = findViewById(R.id.et_token);
        etNewPassword = findViewById(R.id.et_new_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);

        btnResetPassword = findViewById(R.id.btn_reset_password);
        progressBar = findViewById(R.id.progress_bar);
        tvBackToLogin = findViewById(R.id.tv_back_to_login);
    }

    private void prefillTokenIfAvailable() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Constants.EXTRA_RESET_TOKEN)) {
            String token = intent.getStringExtra(Constants.EXTRA_RESET_TOKEN);
            if (token != null) {
                etToken.setText(token);
            }
        }
    }

    private void setupClickListeners() {
        btnResetPassword.setOnClickListener(v -> submitPasswordReset());
        tvBackToLogin.setOnClickListener(v -> navigateToLogin());
    }

    private void submitPasswordReset() {
        clearErrors();

        String token = getText(etToken);
        String newPassword = getText(etNewPassword);
        String confirmPassword = getText(etConfirmPassword);

        boolean valid = true;

        if (!ValidationUtils.isNotEmpty(token)) {
            tilToken.setError(getString(R.string.error_empty_reset_token));
            valid = false;
        }

        if (!ValidationUtils.isValidPassword(newPassword)) {
            tilNewPassword.setError(getString(R.string.error_short_password));
            valid = false;
        }

        if (!ValidationUtils.doPasswordsMatch(newPassword, confirmPassword)) {
            tilConfirmPassword.setError(getString(R.string.error_password_mismatch));
            valid = false;
        }

        if (!valid) {
            return;
        }

        setLoading(true);

        ResetPasswordRequest request = new ResetPasswordRequest(token, newPassword, confirmPassword);
        apiService.resetPassword(request).enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(@NonNull Call<MessageResponse> call,
                                   @NonNull Response<MessageResponse> response) {
                setLoading(false);

                if (response.isSuccessful()) {
                    MessageResponse body = response.body();
                    String message = body != null && body.getMessage() != null
                            ? body.getMessage()
                            : getString(R.string.reset_password_success_fallback);

                    Toast.makeText(ResetPasswordActivity.this, message, Toast.LENGTH_LONG).show();
                    navigateToLogin();
                } else {
                    Toast.makeText(ResetPasswordActivity.this, parseError(response), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MessageResponse> call, @NonNull Throwable t) {
                setLoading(false);
                Toast.makeText(ResetPasswordActivity.this,
                        getString(R.string.error_network_try_again), Toast.LENGTH_LONG).show();
            }
        });
    }

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

        if (response.code() == 400) {
            return getString(R.string.error_invalid_reset_token_or_password);
        }
        if (response.code() >= 500) {
            return getString(R.string.server_error);
        }
        return getString(R.string.error_generic_try_again);
    }

    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private void clearErrors() {
        tilToken.setError(null);
        tilNewPassword.setError(null);
        tilConfirmPassword.setError(null);
    }

    private String getText(TextInputEditText editText) {
        return editText.getText() != null ? editText.getText().toString().trim() : "";
    }

    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnResetPassword.setEnabled(!isLoading);
        etToken.setEnabled(!isLoading);
        etNewPassword.setEnabled(!isLoading);
        etConfirmPassword.setEnabled(!isLoading);
    }
}
