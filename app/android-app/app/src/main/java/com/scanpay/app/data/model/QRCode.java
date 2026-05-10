package com.scanpay.app.data.model;

import com.google.gson.annotations.SerializedName;

public class QRCode {

    @SerializedName("id")
    private int id;

    @SerializedName(value = "payload_data", alternate = {"payload"})
    private String payloadJson;

    @SerializedName(value = "qr_type", alternate = {"type"})
    private String qrType;

    @SerializedName("is_dynamic")
    private Boolean dynamic;

    @SerializedName("amount")
    private Double amount;

    @SerializedName("created_at")
    private String createdAt;

    public QRCode() {}

    public QRCode(String payload, String qrType, String createdAt) {
        this.payloadJson = payload;
        this.qrType = qrType;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPayload() {
        return payloadJson;
    }

    public void setPayload(String payload) {
        this.payloadJson = payload;
    }

    public String getQrType() {
        return qrType;
    }

    public void setQrType(String qrType) {
        this.qrType = qrType;
    }

    public boolean isDynamic() {
        if (dynamic != null) {
            return dynamic;
        }
        return "dynamic".equalsIgnoreCase(qrType);
    }

    public void setDynamic(Boolean dynamic) {
        this.dynamic = dynamic;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

}
