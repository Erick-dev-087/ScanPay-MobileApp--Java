package com.scanpay.app.data.response;

import com.google.gson.annotations.SerializedName;
import com.scanpay.app.data.model.Transaction;

public class TransactionResponse {
    @SerializedName("success")
    private boolean success;
    @SerializedName("message")
    private String message;
    @SerializedName("transaction")
    private Transaction transaction;
    @SerializedName("transaction_id")
    private Integer transactionId;
    @SerializedName("status")
    private String status;
    @SerializedName("amount")
    private Double amount;
    @SerializedName("instructions")
    private String instructions;
    @SerializedName("error")
    private String error;
    @SerializedName("checkout_request_id")
    private String checkoutRequestId;

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public Transaction getTransaction() { return transaction; }
    public void setTransaction(Transaction transaction) { this.transaction = transaction; }
    public Integer getTransactionId() { return transactionId; }
    public void setTransactionId(Integer transactionId) { this.transactionId = transactionId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }
    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
    public String getCheckoutRequestId() { return checkoutRequestId; }
    public void setCheckoutRequestId(String checkoutRequestId) { this.checkoutRequestId = checkoutRequestId; }
}

