package com.scanpay.app.ui.scanner;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import com.scanpay.app.ui.base.BaseActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
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

public class QRScannerActivity extends BaseActivity {

    private static final int CAMERA_PERMISSION_REQUEST = 1001;
    private static final int PICK_IMAGE_REQUEST = 2001;
    private ApiService apiService;
    private DecoratedBarcodeView barcodeView;
    private View scanFrame;
    private View scanLine;
    private View btnFlashlight;
    private View btnUpload;
    private View btnManualEntry;
    private ImageButton btnThemeToggle;
    private ImageButton btnHelp;
    private ImageButton btnBack;
    private TextView tvScanInstruction;

    private boolean torchEnabled = false;
    private boolean isHandlingResult = false;
    private android.animation.ValueAnimator scanLineAnimator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);
        apiService = ApiClient.getApiService();

        initViews();
        setupClickListeners();
        hideSystemViewfinder();

        if (checkCameraPermission()) {
            startScanner();
        } else {
            requestCameraPermission();
        }
    }

    private void initViews() {
        barcodeView = findViewById(R.id.barcode_view);
        scanFrame = findViewById(R.id.scan_frame);
        scanLine = findViewById(R.id.scan_line);
        btnFlashlight = findViewById(R.id.btn_flashlight);
        btnUpload = findViewById(R.id.btn_upload);
        btnManualEntry = findViewById(R.id.btn_manual_entry);
        btnThemeToggle = findViewById(R.id.btn_theme_toggle);
        btnHelp = findViewById(R.id.btn_help);
        btnBack = findViewById(R.id.btn_back);
        tvScanInstruction = findViewById(R.id.tv_scan_instruction);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> onBackPressed());

        btnThemeToggle.setOnClickListener(v -> toggleThemePreference());

        btnHelp.setOnClickListener(v ->
                Toast.makeText(this, R.string.help_support, Toast.LENGTH_SHORT).show());

        btnFlashlight.setOnClickListener(v -> toggleFlashlight());

        btnUpload.setOnClickListener(v -> openImagePicker());

        btnManualEntry.setOnClickListener(v ->
                Toast.makeText(this, R.string.scan_enter_code, Toast.LENGTH_SHORT).show());
    }

    private void toggleFlashlight() {
        if (barcodeView == null) {
            return;
        }

        try {
            torchEnabled = !torchEnabled;
            if (torchEnabled) {
                barcodeView.setTorchOn();
            } else {
                barcodeView.setTorchOff();
            }
        } catch (Exception e) {
            torchEnabled = false;
            Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void hideSystemViewfinder() {
        if (barcodeView == null) {
            return;
        }
        int viewFinderId = getResources().getIdentifier(
                "zxing_viewfinder_view",
                "id",
                "com.journeyapps.barcodescanner"
        );
        View viewFinder = viewFinderId != 0 ? barcodeView.findViewById(viewFinderId) : null;
        if (viewFinder != null) {
            viewFinder.setVisibility(View.GONE);
        }
        int statusViewId = getResources().getIdentifier(
                "zxing_status_view",
                "id",
                "com.journeyapps.barcodescanner"
        );
        View statusView = statusViewId != 0 ? barcodeView.findViewById(statusViewId) : null;
        if (statusView != null) {
            statusView.setVisibility(View.GONE);
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
                startScanner();
            } else {
                Toast.makeText(this, R.string.camera_permission_required, Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void startScanner() {
        if (barcodeView == null) {
            return;
        }

        barcodeView.decodeContinuous(barcodeCallback);
        barcodeView.resume();
        startScanLineAnimation();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, R.string.scan_upload, Toast.LENGTH_SHORT).show();
            }
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (barcodeView != null && checkCameraPermission()) {
            barcodeView.resume();
            startScanLineAnimation();
        }
    }

    @Override
    protected void onPause() {
        if (barcodeView != null) {
            barcodeView.pause();
        }
        stopScanLineAnimation();
        super.onPause();
    }

    private void processQRCode(String qrContent) {
        if (isHandlingResult) {
            return;
        }
        isHandlingResult = true;
        if (barcodeView != null) {
            barcodeView.pause();
        }
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
            restartScanner();
        }
    }

    /**
     * Show error message and restart scanner
     */
    private void showErrorAndRetry(String errorMessage) {
        Toast.makeText(QRScannerActivity.this, errorMessage, Toast.LENGTH_LONG).show();
        restartScanner();
    }

    private void restartScanner() {
        isHandlingResult = false;
        if (barcodeView != null) {
            barcodeView.resume();
        }
        startScanLineAnimation();
    }

    private void startScanLineAnimation() {
        if (scanFrame == null || scanLine == null) {
            return;
        }
        stopScanLineAnimation();
        scanFrame.post(() -> {
            int frameHeight = scanFrame.getHeight();
            if (frameHeight <= 0) {
                return;
            }
            scanLineAnimator = android.animation.ValueAnimator.ofFloat(0f, frameHeight - scanLine.getHeight());
            scanLineAnimator.setDuration(1800);
            scanLineAnimator.setRepeatMode(android.animation.ValueAnimator.REVERSE);
            scanLineAnimator.setRepeatCount(android.animation.ValueAnimator.INFINITE);
            scanLineAnimator.addUpdateListener(animation -> {
                float value = (float) animation.getAnimatedValue();
                scanLine.setTranslationY(value);
            });
            scanLineAnimator.start();
        });
    }

    private void stopScanLineAnimation() {
        if (scanLineAnimator != null) {
            scanLineAnimator.cancel();
            scanLineAnimator = null;
        }
    }

    private final BarcodeCallback barcodeCallback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result == null || result.getText() == null) {
                return;
            }
            processQRCode(result.getText());
        }
    };
}

