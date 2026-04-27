package com.scanpay.app.data.response;

import com.google.gson.annotations.SerializedName;
import com.scanpay.app.data.model.QRCode;

public class GenerateQRResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("qr_code")
    private QRCode qrCode;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public QRCode getQrCode() {
        return qrCode;
    }

    public void setQrCode(QRCode qrCode) {
        this.qrCode = qrCode;
    }
}
