package com.scanpay.app.data.request;

import com.google.gson.annotations.SerializedName;

/**
 * Request body for scanning/looking up a QR code.
 */
public class ScanQRRequest {

    @SerializedName("payload")
    private String payload;

    public ScanQRRequest() {}

    public ScanQRRequest(String payload) {
        this.payload = payload;
    }

    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
}
