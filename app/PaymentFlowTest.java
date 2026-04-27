import com.google.gson.Gson;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class PaymentFlowTest {

    private static final String BACKEND_URL = "https://qr-pay-system.onrender.com";
    private static final String API_BASE = BACKEND_URL + "/api";
    private static final String EMAIL = "dan@njoroge.com";
    private static final String PASSWORD = "Password123!";
    private static final int AMOUNT = 100;

    private static String accessToken = null;

    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("Payment Flow Test - QR Payment Application");
        System.out.println("=".repeat(80));

        // Step 1: Health Check
        System.out.println("\n[1] Backend Health Check");
        if (!healthCheck()) {
            System.out.println("[✗] Backend health check failed");
            return;
        }

        // Step 2: Login
        System.out.println("\n[2] User Authentication");
        if (!login()) {
            System.out.println("[✗] Login failed");
            return;
        }

        // Step 3: Get QR Code ID
        System.out.println("\n[3] Fetching QR Code ID");
        int qrCodeId = getQRCodeId();
        if (qrCodeId <= 0) {
            System.out.println("[✗] Failed to get QR Code ID");
            return;
        }
        System.out.println("[✓] QR Code ID: " + qrCodeId);

        // Step 4: Initiate Payment
        System.out.println("\n[4] Initiating Payment");
        if (!initiatePayment(qrCodeId, AMOUNT)) {
            System.out.println("[✗] Payment initiation failed");
            return;
        }

        System.out.println("\n" + "=".repeat(80));
        System.out.println("Payment Flow Test Completed Successfully");
        System.out.println("=".repeat(80));
    }

    private static boolean healthCheck() {
        try {
            String response = makeRequest(API_BASE + "/health", "GET", null, null);
            System.out.println("[✓] Backend is running");
            System.out.println("    Response: " + response);
            return true;
        } catch (Exception e) {
            System.out.println("[✗] Health check failed: " + e.getMessage());
            return false;
        }
    }

    private static boolean login() {
        try {
            String loginJson = "{\"email\":\"" + EMAIL + "\",\"password\":\"" + PASSWORD + "\"}";
            String response = makeRequest(API_BASE + "/auth/login", "POST", loginJson, null);

            Gson gson = new Gson();
            LoginResponse loginResp = gson.fromJson(response, LoginResponse.class);

            if (loginResp.access_token != null) {
                accessToken = loginResp.access_token;
                System.out.println("[✓] Login successful");
                System.out.println("    User Type: " + loginResp.user_type);
                System.out.println("    User ID: " + loginResp.user.id);
                System.out.println("    Token: " + accessToken.substring(0, 20) + "...");
                return true;
            } else {
                System.out.println("[✗] Login response missing token");
                return false;
            }
        } catch (Exception e) {
            System.out.println("[✗] Login failed: " + e.getMessage());
            return false;
        }
    }

    private static int getQRCodeId() {
        try {
            String response = makeRequest(API_BASE + "/qr/codes", "GET", null, accessToken);

            Gson gson = new Gson();
            QRCodesResponse qrResp = gson.fromJson(response, QRCodesResponse.class);

            if (qrResp.qr_codes != null && qrResp.qr_codes.length > 0) {
                System.out.println("[✓] Retrieved QR codes");
                for (QRCode qr : qrResp.qr_codes) {
                    System.out.println("    ID: " + qr.id + ", Type: " + qr.type + ", Amount: " + qr.amount);
                }
                return qrResp.qr_codes[0].id;
            } else {
                System.out.println("[i] No QR codes found, using default ID: 1");
                return 1;
            }
        } catch (Exception e) {
            System.out.println("[i] Could not fetch QR codes: " + e.getMessage());
            System.out.println("    Using default QR Code ID: 1");
            return 1;
        }
    }

    private static boolean initiatePayment(int qrCodeId, int amount) {
        try {
            String paymentJson = "{\"qr_code_id\":" + qrCodeId + ",\"amount\":" + amount + "}";
            System.out.println("    Request Body: " + paymentJson);

            String response = makeRequest(API_BASE + "/payment/initiate", "POST", paymentJson, accessToken);

            Gson gson = new Gson();
            PaymentResponse paymentResp = gson.fromJson(response, PaymentResponse.class);

            System.out.println("[✓] Payment initiated successfully");
            System.out.println("    Message: " + paymentResp.message);
            System.out.println("    Transaction ID: " + paymentResp.transaction_id);
            System.out.println("    Amount: " + paymentResp.amount);
            if (paymentResp.checkout_request_id != null) {
                System.out.println("    Checkout Request ID: " + paymentResp.checkout_request_id);
            }
            System.out.println("    Status: " + paymentResp.status);
            System.out.println("    Instructions: " + paymentResp.instructions);

            return true;
        } catch (Exception e) {
            System.out.println("[✗] Payment initiation failed: " + e.getMessage());
            return false;
        }
    }

    private static String makeRequest(String urlStr, String method, String body, String token) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", "application/json");
        if (token != null) {
            conn.setRequestProperty("Authorization", "Bearer " + token);
        }
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(15000);

        if (body != null) {
            conn.setDoOutput(true);
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = body.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
        }

        int statusCode = conn.getResponseCode();
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(
                statusCode >= 400 ? conn.getErrorStream() : conn.getInputStream()
            )
        );
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        if (statusCode >= 400) {
            throw new Exception("HTTP " + statusCode + ": " + response.toString());
        }

        return response.toString();
    }

    // Response classes
    static class LoginResponse {
        String access_token;
        String user_type;
        User user;

        static class User {
            int id;
            String email;
            String name;
            String phone;
        }
    }

    static class QRCodesResponse {
        QRCode[] qr_codes;

        static class QRCode {
            int id;
            String type;
            int amount;
        }
    }

    static class QRCode {
        int id;
        String type;
        int amount;
    }

    static class PaymentResponse {
        String message;
        int transaction_id;
        String checkout_request_id;
        int amount;
        String status;
        String instructions;
    }
}

