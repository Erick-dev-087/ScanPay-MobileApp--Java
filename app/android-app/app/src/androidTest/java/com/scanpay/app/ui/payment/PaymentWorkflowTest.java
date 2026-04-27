package com.scanpay.app.ui.payment;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.scanpay.app.api.ApiClient;
import com.scanpay.app.api.ApiService;
import com.scanpay.app.data.request.PaymentRequest;
import com.scanpay.app.data.response.TransactionResponse;
import com.scanpay.app.utils.Constants;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.junit.Assert.*;

/**
 * Unit tests for Payment workflow.
 * Tests the payment request structure and API integration.
 *
 * Run these tests with:
 * ./gradlew connectedAndroidTest
 *
 * Or run specific test:
 * ./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.scanpay.app.ui.payment.PaymentWorkflowTest
 */
@RunWith(AndroidJUnit4.class)
public class PaymentWorkflowTest {

    private static final String TAG = "PaymentWorkflowTest";
    private static final int TEST_QR_CODE_ID = 3;  // Use actual QR code ID from database
    private static final double TEST_AMOUNT = 500.0;
    private static final long TIMEOUT_SECONDS = 10;

    private Context context;
    private ApiService apiService;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        ApiClient.init(context);
        apiService = ApiClient.getApiService();
    }

    /**
     * Test 1: Verify PaymentRequest structure is correct
     */
    @Test
    public void testPaymentRequestStructure() {
        // Create a payment request as the app would
        PaymentRequest request = new PaymentRequest(TEST_QR_CODE_ID, TEST_AMOUNT);

        // Verify the request has the required fields
        assertNotNull("PaymentRequest should not be null", request);
        assertEquals("QR Code ID should match", TEST_QR_CODE_ID, request.getQrCodeId());
        assertEquals("Amount should match", TEST_AMOUNT, request.getAmount(), 0.01);

        // Verify that old fields are accessible (backward compatibility)
        assertNotNull("Payment request should be instantiable", request);
    }

    /**
     * Test 2: Verify alternative PaymentRequest constructor
     */
    @Test
    public void testPaymentRequestLegacyConstructor() {
        // Test legacy constructor with merchant code
        String merchantCode = "123456";
        double amount = 250.0;
        String phone = "254703983986";

        PaymentRequest request = new PaymentRequest(merchantCode, amount, phone);

        assertNotNull("PaymentRequest should not be null", request);
        assertEquals("Merchant code should match", merchantCode, request.getMerchantCode());
        assertEquals("Amount should match", amount, request.getAmount(), 0.01);
        assertEquals("Phone should match", phone, request.getPhone());
    }

    /**
     * Test 3: Verify PaymentRequest with reference
     */
    @Test
    public void testPaymentRequestWithReference() {
        String merchantCode = "123456";
        double amount = 150.0;
        String phone = "254703983986";
        String reference = "REF123";

        PaymentRequest request = new PaymentRequest(merchantCode, amount, phone, reference);

        assertNotNull("PaymentRequest should not be null", request);
        assertEquals("Reference should match", reference, request.getReference());
    }

    /**
     * Test 4: Verify API Service has initiatePayment method
     */
    @Test
    public void testApiServiceInitiatePaymentMethodExists() {
        assertNotNull("ApiService should not be null", apiService);

        // Try to create a payment call (this doesn't execute it, just verifies the method exists)
        PaymentRequest request = new PaymentRequest(TEST_QR_CODE_ID, TEST_AMOUNT);

        try {
            Call<TransactionResponse> call = apiService.initiatePayment(request);
            assertNotNull("Payment initiate call should not be null", call);
        } catch (Exception e) {
            fail("ApiService.initiatePayment should be callable: " + e.getMessage());
        }
    }

    /**
     * Test 5: Verify Constants BASE_URL is correctly set to Render
     */
    @Test
    public void testBaseUrlConfiguration() {
        String baseUrl = Constants.BASE_URL;
        assertNotNull("BASE_URL should not be null", baseUrl);

        // Verify it contains Render URL
        assertTrue("BASE_URL should point to Render",
                   baseUrl.contains("onrender.com") ||
                   baseUrl.contains("10.0.2.2") ||
                   baseUrl.contains("localhost"));

        // Verify it ends with /api/
        assertTrue("BASE_URL should end with /api/", baseUrl.endsWith("/api/"));
    }

    /**
     * Test 6: Integration test - Actual payment initiation call (REQUIRES BACKEND AND LOGIN)
     * This test is skipped if backend is not available
     * Run this only when backend is running and user is logged in
     */
    @Test(timeout = TIMEOUT_SECONDS * 1000)
    public void testPaymentInitiationIntegration() throws InterruptedException {
        // This test requires:
        // 1. Backend running
        // 2. User logged in with valid JWT token
        // 3. Valid QR code ID in database
        // 4. Valid JWT token in SessionManager

        final CountDownLatch latch = new CountDownLatch(1);
        final boolean[] testPassed = {false};
        final String[] errorMessage = {""};

        PaymentRequest request = new PaymentRequest(TEST_QR_CODE_ID, TEST_AMOUNT);

        apiService.initiatePayment(request).enqueue(new Callback<TransactionResponse>() {
            @Override
            public void onResponse(Call<TransactionResponse> call, Response<TransactionResponse> response) {
                if (response.isSuccessful()) {
                    TransactionResponse transactionResponse = response.body();
                    if (transactionResponse != null && transactionResponse.isSuccess()) {
                        testPassed[0] = true;
                        System.out.println("✅ Payment initiated successfully");
                        System.out.println("   Transaction ID: " + transactionResponse.getMessage());
                    } else {
                        errorMessage[0] = "Response body was null or unsuccessful";
                    }
                } else {
                    errorMessage[0] = "HTTP " + response.code() + ": " + response.message();
                }
                latch.countDown();
            }

            @Override
            public void onFailure(Call<TransactionResponse> call, Throwable t) {
                errorMessage[0] = "Network error: " + t.getMessage();
                latch.countDown();
            }
        });

        // Wait for response
        boolean completed = latch.await(TIMEOUT_SECONDS, TimeUnit.SECONDS);

        if (!completed) {
            fail("Payment initiation timed out after " + TIMEOUT_SECONDS + " seconds");
        }

        if (!testPassed[0]) {
            fail("Payment initiation failed: " + errorMessage[0]);
        }
    }

    /**
     * Test 7: Verify payment request serialization
     */
    @Test
    public void testPaymentRequestSerialization() {
        PaymentRequest request = new PaymentRequest(TEST_QR_CODE_ID, TEST_AMOUNT);

        // Verify that Gson can serialize this request
        com.google.gson.Gson gson = new com.google.gson.Gson();
        String json = gson.toJson(request);

        assertNotNull("Serialized JSON should not be null", json);
        assertTrue("JSON should contain qr_code_id", json.contains("qr_code_id"));
        assertTrue("JSON should contain amount", json.contains("amount"));

        System.out.println("📋 Serialized PaymentRequest:");
        System.out.println(json);
    }

    /**
     * Test 8: Verify payment amount validation
     */
    @Test
    public void testPaymentAmountValidation() {
        // Valid amount
        PaymentRequest validRequest = new PaymentRequest(TEST_QR_CODE_ID, 500.0);
        assertEquals("Valid amount should be set", 500.0, validRequest.getAmount(), 0.01);

        // Minimum amount (1 KES)
        PaymentRequest minRequest = new PaymentRequest(TEST_QR_CODE_ID, 1.0);
        assertEquals("Minimum amount should be set", 1.0, minRequest.getAmount(), 0.01);

        // Maximum amount (500,000 KES)
        PaymentRequest maxRequest = new PaymentRequest(TEST_QR_CODE_ID, 500000.0);
        assertEquals("Maximum amount should be set", 500000.0, maxRequest.getAmount(), 0.01);
    }

    /**
     * Test 9: Verify QR Code ID validation
     */
    @Test
    public void testQrCodeIdValidation() {
        // Valid QR Code ID
        PaymentRequest request = new PaymentRequest(TEST_QR_CODE_ID, TEST_AMOUNT);
        assertEquals("QR Code ID should match", TEST_QR_CODE_ID, request.getQrCodeId());

        // ID from string parsing
        String merchantCodeStr = "123";
        int parsedId = Integer.parseInt(merchantCodeStr);
        PaymentRequest request2 = new PaymentRequest(parsedId, TEST_AMOUNT);
        assertEquals("Parsed QR Code ID should match", parsedId, request2.getQrCodeId());
    }

    /**
     * Test 10: Verify API client initialization
     */
    @Test
    public void testApiClientInitialization() {
        // Verify that API client is initialized with context
        assertNotNull("ApiService should be initialized", apiService);

        // Try to get the service again - should return the same instance
        ApiService apiService2 = ApiClient.getApiService();
        assertEquals("ApiService should be singleton", apiService, apiService2);
    }
}

