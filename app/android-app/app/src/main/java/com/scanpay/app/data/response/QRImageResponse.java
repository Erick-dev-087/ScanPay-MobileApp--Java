package com.scanpay.app.data.response;

import com.google.gson.annotations.SerializedName;

public class QRImageResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("qr_id")
    private Integer qrId;

    @SerializedName("file_name")
    private String fileName;

    @SerializedName("file_path")
    private String filePath;

    @SerializedName("qr_image")
    private String qrImage;

    public String getMessage() {
        return message;
    }

    public Integer getQrId() {
        return qrId;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getQrImage() {
        return qrImage;
    }
}
