package com.scanpay.app.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import com.scanpay.app.ui.base.BaseActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.scanpay.app.R;
import com.scanpay.app.api.ApiClient;
import com.scanpay.app.api.ApiService;
import com.scanpay.app.data.request.ForgotPasswordRequest;
import com.scanpay.app.data.response.ForgotPasswordResponse;
import com.scanpay.app.utils.Constants;
import com.scanpay.app.utils.ValidationUtils;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends BaseActivity {

    private TextInputLayout tilEmail;
    private TextInputEditText etEmail;
    private Button btnSendReset;
    private ProgressBar progressBar;
    private TextView tvGoToReset;

    private ApiService apiService;
    private String pendingResetToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        apiService = ApiClient.getApiService();

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        tilEmail = findViewById(R.id.til_email);
        etEmail = findViewById(R.id.et_email);
        btnSendReset = findViewById(R.id.btn_send_reset);
        progressBar = findViewById(R.id.progress_bar);
        tvGoToReset = findViewById(R.id.tv_go_to_reset);
        tvGoToReset.setVisibility(View.GONE);
    }

    private void setupClickListeners() {
        btnSendReset.setOnClickListener(v -> requestPasswordReset());
        tvGoToReset.setOnClickListener(v -> openResetPasswordScreen());
    }

    private void requestPasswordReset() {
        tilEmail.setError(null);

        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        if (!ValidationUtils.isValidEmail(email)) {
            tilEmail.setError(getString(R.string.error_invalid_email));
            return;
        }

        setLoading(true);

        ForgotPasswordRequest request = new ForgotPasswordRequest(email);
        apiService.forgotPassword(request).enqueue(new Callback<ForgotPasswordResponse>() {
            @Override
            public void onResponse(@NonNull Call<ForgotPasswordResponse> call,
                                   @NonNull Response<ForgotPasswordResponse> response) {
                setLoading(false);

                if (response.isSuccessful()) {
                    ForgotPasswordResponse body = response.body();
                    String message = body != null && body.getMessage() != null
                            ? body.getMessage()
                            : getString(R.string.forgot_password_success_fallback);

                    String instruction = getString(R.string.check_email_for_reset_token);
                    String fullMessage = message + " " + instruction;

                    Toast.makeText(ForgotPasswordActivity.this, fullMessage, Toast.LENGTH_LONG).show();
                    if (body != null && body.getResetToken() != null && !body.getResetToken().trim().isEmpty()) {
                        pendingResetToken = body.getResetToken().trim();
                    }

                    tvGoToReset.setText(R.string.continue_to_reset_password);
                    tvGoToReset.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, parseError(response), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ForgotPasswordResponse> call, @NonNull Throwable t) {
                setLoading(false);
                Toast.makeText(ForgotPasswordActivity.this,
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
            return getString(R.string.error_invalid_request_data);
        }
        if (response.code() >= 500) {
            return getString(R.string.server_error);
        }
        return getString(R.string.error_generic_try_again);
    }

    private void setLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        btnSendReset.setEnabled(!isLoading);
        etEmail.setEnabled(!isLoading);
    }

    private void openResetPasswordScreen() {
        Intent intent = new Intent(this, ResetPasswordActivity.class);
        if (pendingResetToken != null && !pendingResetToken.isEmpty()) {
            intent.putExtra(Constants.EXTRA_RESET_TOKEN, pendingResetToken);
        }
        startActivity(intent);
    }
}
