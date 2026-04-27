package com.scanpay.app.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.scanpay.app.data.request.ForgotPasswordRequest;
import com.scanpay.app.data.request.ResetPasswordRequest;
import com.scanpay.app.data.response.ForgotPasswordResponse;
import com.scanpay.app.data.response.MessageResponse;

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

public class AuthPasswordRoutesApiTest {

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
    public void forgotPassword_callsExpectedBackendRouteWithPayload() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"message\":\"If the account exists, a password reset token has been generated.\",\"expires_in_seconds\":3600}"));

        ForgotPasswordRequest request = new ForgotPasswordRequest("john@example.com");
        Response<ForgotPasswordResponse> response = apiService.forgotPassword(request).execute();

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        String body = recordedRequest.getBody().readUtf8();

        assertEquals("POST", recordedRequest.getMethod());
        assertEquals("/api/auth/forgot-password", recordedRequest.getPath());
        assertTrue(body.contains("\"email\":\"john@example.com\""));

        assertTrue(response.isSuccessful());
        assertNotNull(response.body());
        assertEquals("If the account exists, a password reset token has been generated.",
                response.body().getMessage());
    }

    @Test
    public void resetPassword_callsExpectedBackendRouteWithPayload() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setBody("{\"message\":\"Password reset successful\"}"));

        ResetPasswordRequest request = new ResetPasswordRequest(
                "sample-reset-token",
                "newpass123",
                "newpass123");

        Response<MessageResponse> response = apiService.resetPassword(request).execute();

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        String body = recordedRequest.getBody().readUtf8();

        assertEquals("POST", recordedRequest.getMethod());
        assertEquals("/api/auth/reset-password", recordedRequest.getPath());
        assertTrue(body.contains("\"token\":\"sample-reset-token\""));
        assertTrue(body.contains("\"new_password\":\"newpass123\""));
        assertTrue(body.contains("\"confirm_password\":\"newpass123\""));

        assertTrue(response.isSuccessful());
        assertNotNull(response.body());
        assertEquals("Password reset successful", response.body().getMessage());
    }
}
