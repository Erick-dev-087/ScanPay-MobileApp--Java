package com.scanpay.app.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.scanpay.app.data.request.TopUpRequest;
import com.scanpay.app.data.response.TopUpBalanceResponse;
import com.scanpay.app.data.response.TopUpResponse;

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

public class TopUpRoutesApiTest {

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
    public void initiateTopUp_callsExpectedRouteAndParsesResponse() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(201)
                .setBody("{\"message\":\"Top-up initiated successfully\",\"transaction_id\":55,\"checkout_request_id\":\"ws_CO_topup\",\"status\":\"Pending\",\"amount\":1000,\"instructions\":\"Check your phone\"}"));

        TopUpRequest request = new TopUpRequest("254712345678", 1000);
        Response<TopUpResponse> response = apiService.initiateTopUp(request).execute();

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        String body = recordedRequest.getBody().readUtf8();

        assertEquals("POST", recordedRequest.getMethod());
        assertEquals("/api/topup/initiate", recordedRequest.getPath());
        assertTrue(body.contains("\"phone\":\"254712345678\""));
        assertTrue(body.contains("\"amount\":1000.0"));

        assertTrue(response.isSuccessful());
        assertNotNull(response.body());
        assertEquals(Integer.valueOf(55), response.body().getTransactionId());
        assertEquals("ws_CO_topup", response.body().getCheckoutRequestId());
    }

    @Test
    public void getTopUpBalance_callsExpectedRoute() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"message\":\"Balance retrieved\",\"balance\":30.0,\"currency\":\"KES\"}"));

        Response<TopUpBalanceResponse> response = apiService.getTopUpBalance().execute();

        RecordedRequest recordedRequest = mockWebServer.takeRequest();

        assertEquals("GET", recordedRequest.getMethod());
        assertEquals("/api/topup/balance", recordedRequest.getPath());
        assertTrue(response.isSuccessful());
        assertNotNull(response.body());
        assertEquals(Double.valueOf(30.0), response.body().getBalance());
    }
}
