package com.scanpay.app.ui.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.net.Uri;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import android.content.ContentValues;
import android.os.Environment;
import java.io.OutputStream;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.scanpay.app.R;
import com.scanpay.app.utils.SessionManager;
import com.scanpay.app.api.ApiClient;
import com.scanpay.app.data.request.GenerateQRRequest;
import com.scanpay.app.data.response.GenerateQRResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QRCodesFragment extends Fragment {

    private ImageView ivQrCode;
    private TextView tvTitle, tvSubtitle;
    private Button btnShare, btnDownload;

    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_qr_codes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sessionManager = new SessionManager(requireContext());

        initViews(view);
        setupUI();
    }

    private void initViews(View view) {
        ivQrCode = view.findViewById(R.id.iv_qr_code);
        tvTitle = view.findViewById(R.id.tv_title);
        tvSubtitle = view.findViewById(R.id.tv_subtitle);
        btnShare = view.findViewById(R.id.btn_share);
        btnDownload = view.findViewById(R.id.btn_download);
    }

    private void setupUI() {
        if (sessionManager.isMerchant()) {
            tvTitle.setText("My QR Code");
            tvSubtitle.setText("Customers can scan to pay");

            // Fetch QR from backend
            fetchMerchantQRCode();
        } else {
            tvTitle.setText("Scan to Pay");
            tvSubtitle.setText("Point camera at merchant QR code");

            // For customers, show placeholder or their ID QR
            String qrContent = "CUSTOMER|" + sessionManager.getUserPhone();
            generateQRCode(qrContent);

            if(btnShare != null) btnShare.setVisibility(View.GONE);
            if(btnDownload != null) btnDownload.setVisibility(View.GONE);
        }

        if (btnShare != null) {
            btnShare.setOnClickListener(v -> shareQRCode());
        }
        if (btnDownload != null) {
            btnDownload.setOnClickListener(v -> downloadQRCode());
        }
    }

    private void fetchMerchantQRCode() {
        GenerateQRRequest request = new GenerateQRRequest("STATIC"); // Generate Static QR
        ApiClient.getApiService().generateQRCode(request).enqueue(new Callback<GenerateQRResponse>() {
            @Override
            public void onResponse(Call<GenerateQRResponse> call, Response<GenerateQRResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getQrCode() != null && response.body().getQrCode().getPayload() != null) {
                        generateQRCode(response.body().getQrCode().getPayload());
                    } else {
                        Toast.makeText(requireContext(), "Failed to extract QR payload", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to generate QR: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GenerateQRResponse> call, Throwable t) {
                if (isAdded()) {
                    Toast.makeText(requireContext(), "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void shareQRCode() {
        Uri imageUri = saveImageToMediaStore("Shared_QR_" + System.currentTimeMillis() + ".png");
        if (imageUri != null) {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/png");
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            startActivity(Intent.createChooser(shareIntent, "Share QR Code"));
        } else {
            Toast.makeText(requireContext(), "Failed to share QR Code", Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadQRCode() {
        Uri imageUri = saveImageToMediaStore("ScanPay_QR_" + sessionManager.getUserName() + ".png");
        if (imageUri != null) {
            Toast.makeText(requireContext(), "QR Code saved to gallery \n" + imageUri.toString(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(requireContext(), "Failed to download QR Code", Toast.LENGTH_SHORT).show();
        }
    }

    private Uri saveImageToMediaStore(String displayName) {
        if (ivQrCode.getDrawable() == null) return null;
        Bitmap bitmap = ((BitmapDrawable) ivQrCode.getDrawable()).getBitmap();

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, displayName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/ScanPay");

        Uri uri = requireContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        if (uri != null) {
            try (OutputStream out = requireContext().getContentResolver().openOutputStream(uri)) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                return uri;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void generateQRCode(String content) {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            int colorDark = requireContext().getColor(R.color.accentGreen);
            int colorLight = requireContext().getColor(R.color.cardBackground);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? colorDark : colorLight);
                }
            }
            ivQrCode.setImageBitmap(bmp);
        } catch (WriterException e) {
            Toast.makeText(requireContext(), "Failed to generate QR", Toast.LENGTH_SHORT).show();
        }
    }
}

