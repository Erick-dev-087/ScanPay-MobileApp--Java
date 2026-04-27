package com.scanpay.app.ui.scanner;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.scanpay.app.R;
import com.scanpay.app.api.ApiClient;
import com.scanpay.app.api.ApiService;
import com.scanpay.app.data.request.ScanQRRequest;
import com.scanpay.app.data.response.QRScanResponse;
import com.scanpay.app.ui.payment.PaymentConfirmationActivity;
import com.scanpay.app.utils.Constants;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QRScannerActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST = 1001;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiService = ApiClient.getApiService();

        if (checkCameraPermission()) {
            startQRScanner();
        } else {
            requestCameraPermission();
        }
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.permission_camera_title)
                    .setMessage(R.string.permission_camera_message)
                    .setPositiveButton(R.string.grant_permission, (dialog, which) -> {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.CAMERA},
                                CAMERA_PERMISSION_REQUEST);
                    })
                    .setNegativeButton(R.string.cancel, (dialog, which) -> finish())
                    .show();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startQRScanner();
            } else {
                Toast.makeText(this, R.string.camera_permission_required, Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void startQRScanner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt(getString(R.string.point_camera_at_qr));
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(false);
        integrator.setOrientationLocked(true);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            if (result.getContents() == null) {
                // Scan cancelled
                finish();
            } else {
                // QR code scanned successfully
                processQRCode(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void processQRCode(String qrContent) {
        // Show loading indicator while validating
        Toast.makeText(this, R.string.validating_qr_code, Toast.LENGTH_SHORT).show();

        // Call backend to validate QR code (CRC checksum, vendor verification, etc.)
        validateQRCodeWithBackend(qrContent);
    }

    /**
     * Send QR code payload to backend for verification
     * Backend will:
     * 1. Validate CRC checksum
     * 2. Parse EMVCo format
     * 3. Find vendor in database
     * 4. Verify vendor is active
     * 5. Verify QR code is active
     * 6. Return verified vendor and QR details
     */
    private void validateQRCodeWithBackend(String qrPayload) {
        ScanQRRequest request = new ScanQRRequest(qrPayload);

        apiService.scanQRCode(request)
                .enqueue(new Callback<QRScanResponse>() {
                    @Override
                    public void onResponse(Call<QRScanResponse> call, Response<QRScanResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            QRScanResponse scanResponse = response.body();

                            // Verify response is valid
                            if (scanResponse.isValid() && scanResponse.getVendor() != null && scanResponse.getQrCode() != null) {
                                // QR code is legitimate - proceed to payment confirmation
                                proceedToPaymentConfirmation(scanResponse);
                            } else {
                                // Vendor or QR code is invalid
                                showErrorAndRetry("Invalid or inactive vendor/QR code");
                            }
                        } else {
                            // Backend error - QR might be invalid, expired, or vendor inactive
                            showErrorAndRetry("QR code verification failed: " + response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<QRScanResponse> call, Throwable t) {
                        showErrorAndRetry("Network error: " + t.getMessage());
                    }
                });
    }

    /**
     * Proceed to payment confirmation with verified vendor and QR details from backend
     */
    private void proceedToPaymentConfirmation(QRScanResponse scanResponse) {
        try {
            Intent intent = new Intent(this, PaymentConfirmationActivity.class);

            // Pass validated data from backend
            if (scanResponse.getVendor() != null) {
                intent.putExtra(Constants.EXTRA_MERCHANT_CODE, scanResponse.getVendor().getBusinessShortcode());
                intent.putExtra(Constants.EXTRA_MERCHANT_NAME, scanResponse.getVendor().getName());
                intent.putExtra(Constants.EXTRA_VENDOR_ID, scanResponse.getVendor().getId());
            }

            if (scanResponse.getQrCode() != null) {
                intent.putExtra(Constants.EXTRA_QR_CODE_ID, scanResponse.getQrCode().getId());

                // If dynamic QR, pass preset amount
                if (scanResponse.getQrCode().isDynamic() && scanResponse.getQrCode().getAmount() != null) {
                    intent.putExtra(Constants.EXTRA_AMOUNT, String.valueOf(scanResponse.getQrCode().getAmount()));
                }
            }

            // Pass original QR payload for reference
            intent.putExtra(Constants.EXTRA_QR_DATA, scanResponse.getQrCode() != null ? scanResponse.getQrCode().getPayload() : "");

            startActivity(intent);
            finish();

        } catch (Exception e) {
            Toast.makeText(QRScannerActivity.this, "Error processing QR: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            startQRScanner(); // Restart scanner
        }
    }

    /**
     * Show error message and restart scanner
     */
    private void showErrorAndRetry(String errorMessage) {
        Toast.makeText(QRScannerActivity.this, errorMessage, Toast.LENGTH_LONG).show();
        // Restart scanner to try again
        startQRScanner();
    }
}

