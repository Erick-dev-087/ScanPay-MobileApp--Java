package com.scanpay.app.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.scanpay.app.data.request.PaymentRequest;
import com.scanpay.app.data.response.PaymentStatusResponse;
import com.scanpay.app.data.response.TransactionResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PaymentRoutesApiTest {

    private MockWebServer mockWebServer;
    private ApiService apiService;

    @Before
    public void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mockWebServer.url("/api/"))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    @After
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void initiatePayment_callsExpectedRouteAndParsesTransactionReference() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(201)
                .setBody("{\"message\":\"Payment initiated successfully\",\"transaction_id\":42,\"checkout_request_id\":\"ws_CO_123\",\"status\":\"Pending\"}"));

        PaymentRequest request = new PaymentRequest(3, 500.0);
        Response<TransactionResponse> response = apiService.initiatePayment(request).execute();

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        String body = recordedRequest.getBody().readUtf8();

        assertEquals("POST", recordedRequest.getMethod());
        assertEquals("/api/payment/initiate", recordedRequest.getPath());
        assertTrue(body.contains("\"qr_code_id\":3"));
        assertTrue(body.contains("\"amount\":500.0"));

        assertTrue(response.isSuccessful());
        assertNotNull(response.body());
        assertEquals(Integer.valueOf(42), response.body().getTransactionId());
        assertEquals("ws_CO_123", response.body().getCheckoutRequestId());
    }

    @Test
    public void checkPaymentStatus_callsExpectedRoute() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"id\":42,\"status\":\"success\",\"amount\":500,\"mpesa_receipt\":\"QGK123ABC\"}"));

        Response<PaymentStatusResponse> response = apiService.checkPaymentStatus(42).execute();

        RecordedRequest recordedRequest = mockWebServer.takeRequest();

        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("/api/payment/42/status", recordedRequest.getPath());

        assertTrue(response.isSuccessful());
        assertNotNull(response.body());
        assertEquals("success", response.body().getStatus());
        assertEquals(Integer.valueOf(42), response.body().getId());
    }
}
