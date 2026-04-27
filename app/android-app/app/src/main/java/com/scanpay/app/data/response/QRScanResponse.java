package com.scanpay.app.data.response;

import com.google.gson.annotations.SerializedName;
import com.scanpay.app.data.model.Vendor;
import com.scanpay.app.data.model.QRCode;

/**
 * Response from POST /api/qr/scan endpoint.
 * Contains verified vendor information and QR code details from backend validation.
 */
public class QRScanResponse {

    @SerializedName("message")
    private String message;

    @SerializedName("vendor")
    private Vendor vendor;

    @SerializedName("qr_code")
    private QRCode qrCode;

    @SerializedName("next_step")
    private String nextStep;

    public QRScanResponse() {}

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Vendor getVendor() {
        return vendor;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }

    public QRCode getQrCode() {
        return qrCode;
    }

    public void setQrCode(QRCode qrCode) {
        this.qrCode = qrCode;
    }

    public String getNextStep() {
        return nextStep;
    }

    public void setNextStep(String nextStep) {
        this.nextStep = nextStep;
    }

    /**
     * Check if the scan response is valid (vendor is active and QR is valid)
     */
    public boolean isValid() {
        return vendor != null && qrCode != null;
    }
}

