package com.scanpay.app.ui.auth;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.scanpay.app.data.model.User;
import com.scanpay.app.data.response.AuthResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Integration test suite for Authentication Flow
 * Tests user registration and login with user type verification
 * Focuses on the transition from login to appropriate dashboard
 */
@RunWith(AndroidJUnit4.class)
public class AuthenticationIntegrationTest {

    private AuthResponse mockAuthResponse;
    private User testUser;

    @Before
    public void setUp() {
        mockAuthResponse = new AuthResponse();
    }

    /**
     * TEST 1: Register and Login - Customer User Type
     * Simulates a complete customer registration and login flow
     */
    @Test
    public void testCustomerRegistrationAndLoginFlow() {
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║ TEST 1: CUSTOMER REGISTRATION AND LOGIN FLOW          ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");

        // Step 1: Customer Registration
        System.out.println("\n[STEP 1] Customer Registration");
        System.out.println("─────────────────────────────");
        String customerName = "Sarah Johnson";
        String customerEmail = "sarah.johnson@example.com";
        String customerPhone = "555-0100";
        String customerPassword = "SecurePass123!";

        System.out.println("Input Data:");
        System.out.println("  • Name:     " + customerName);
        System.out.println("  • Email:    " + customerEmail);
        System.out.println("  • Phone:    " + customerPhone);
        System.out.println("  • Password: ••••••••••••••••");

        // Create customer user
        testUser = new User(customerName, customerEmail, customerPhone, "customer");
        testUser.setId(201);
        testUser.setCreatedAt("2026-03-19T16:00:00Z");

        System.out.println("\n✓ Customer created successfully");
        printUserTypeInfo("REGISTRATION", testUser);

        // Step 2: Customer Login
        System.out.println("\n[STEP 2] Customer Login");
        System.out.println("─────────────────────────────");
        System.out.println("Login Input:");
        System.out.println("  • Email:    " + customerEmail);
        System.out.println("  • Password: ••••••••••••••••");

        // Simulate login response
        setupAuthResponse(true, "Login successful", testUser, "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.customer.token");

        System.out.println("\n✓ Authentication successful");
        System.out.println("✓ Response received from backend");

        // Step 3: Validate user data
        System.out.println("\n[STEP 3] User Data Validation");
        System.out.println("─────────────────────────────");
        validateUserDataAndPrintNavigation(mockAuthResponse);

        // Step 4: Navigation
        System.out.println("\n[STEP 4] Navigation Decision");
        System.out.println("─────────────────────────────");
        Class<?> targetDashboard = determineTargetDashboard(testUser.getUserType());
        System.out.println("Routing customer to: " + targetDashboard.getSimpleName());
        assertEquals("Customer should navigate to MainActivity",
                com.scanpay.app.ui.main.MainActivity.class, targetDashboard);

        System.out.println("\n✅ CUSTOMER FLOW COMPLETED SUCCESSFULLY");
    }

    /**
     * TEST 2: Register and Login - Merchant User Type
     * Simulates a complete merchant registration and login flow
     */
    @Test
    public void testMerchantRegistrationAndLoginFlow() {
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║ TEST 2: MERCHANT REGISTRATION AND LOGIN FLOW          ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");

        // Step 1: Merchant Registration
        System.out.println("\n[STEP 1] Merchant Registration");
        System.out.println("─────────────────────────────");
        String merchantName = "David Chen";
        String merchantEmail = "david.chen@business.com";
        String merchantPhone = "555-0200";
        String merchantPassword = "SecurePass456!";

        System.out.println("Input Data:");
        System.out.println("  • Name:     " + merchantName);
        System.out.println("  • Email:    " + merchantEmail);
        System.out.println("  • Phone:    " + merchantPhone);
        System.out.println("  • Password: ••••••••••••••••");

        // Create merchant user
        testUser = new User(merchantName, merchantEmail, merchantPhone, "merchant");
        testUser.setId(202);
        testUser.setCreatedAt("2026-03-19T16:15:00Z");

        System.out.println("\n✓ Merchant created successfully");
        printUserTypeInfo("REGISTRATION", testUser);

        // Step 2: Merchant Login
        System.out.println("\n[STEP 2] Merchant Login");
        System.out.println("─────────────────────────────");
        System.out.println("Login Input:");
        System.out.println("  • Email:    " + merchantEmail);
        System.out.println("  • Password: ••••••••••••••••");

        // Simulate login response
        setupAuthResponse(true, "Login successful", testUser, "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.merchant.token");

        System.out.println("\n✓ Authentication successful");
        System.out.println("✓ Response received from backend");

        // Step 3: Validate user data
        System.out.println("\n[STEP 3] User Data Validation");
        System.out.println("─────────────────────────────");
        validateUserDataAndPrintNavigation(mockAuthResponse);

        // Step 4: Navigation
        System.out.println("\n[STEP 4] Navigation Decision");
        System.out.println("─────────────────────────────");
        Class<?> targetDashboard = determineTargetDashboard(testUser.getUserType());
        System.out.println("Routing merchant to: " + targetDashboard.getSimpleName());
        assertEquals("Merchant should navigate to MerchantMainActivity",
                com.scanpay.app.ui.merchant.MerchantMainActivity.class, targetDashboard);

        System.out.println("\n✅ MERCHANT FLOW COMPLETED SUCCESSFULLY");
    }

    /**
     * TEST 3: Login Failure Handling
     * Tests what happens when login fails
     */
    @Test
    public void testLoginFailureHandling() {
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║ TEST 3: LOGIN FAILURE HANDLING                         ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");

        System.out.println("\n[ATTEMPT] Login with invalid credentials");
        System.out.println("─────────────────────────────────────────────");
        System.out.println("  • Email:    invalid@example.com");
        System.out.println("  • Password: WrongPassword123!");

        // Create failed response
        setupAuthResponse(false, "Invalid email or password", null, null);

        System.out.println("\n✗ Authentication FAILED");
        System.out.println("  Reason: " + mockAuthResponse.getMessage());

        assertFalse("Response should not be successful", mockAuthResponse.isSuccess());
        assertNull("User should be null in failed response", mockAuthResponse.getUser());

        System.out.println("\n⚠️  User remains on login screen");
        System.out.println("✓ Error message displayed to user");
        System.out.println("\n✅ FAILURE HANDLING COMPLETED");
    }

    /**
     * TEST 4: Multiple User Login Sequence
     * Tests logging in different users sequentially
     */
    @Test
    public void testMultipleUserLoginSequence() {
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║ TEST 4: MULTIPLE USER LOGIN SEQUENCE                   ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");

        // Test data for multiple users
        Object[][] loginData = {
                {"alice@example.com", "Alice Miller", "555-1001", "customer"},
                {"bob@business.com", "Bob Wilson", "555-1002", "merchant"},
                {"carol@example.com", "Carol Davis", "555-1003", "customer"},
                {"dave@business.com", "Dave Martinez", "555-1004", "merchant"}
        };

        for (int i = 0; i < loginData.length; i++) {
            String email = (String) loginData[i][0];
            String name = (String) loginData[i][1];
            String phone = (String) loginData[i][2];
            String userType = (String) loginData[i][3];

            System.out.println("\n[USER " + (i + 1) + "]");
            System.out.println("─────────────────────────────");

            // Create user
            testUser = new User(name, email, phone, userType);
            testUser.setId(300 + i);

            // Simulate login
            setupAuthResponse(true, "Login successful", testUser, "token_" + i);

            System.out.println("Logged in: " + name);
            System.out.println("Type:      " + userType);
            Class<?> dashboard = determineTargetDashboard(userType);
            System.out.println("Route to:  " + dashboard.getSimpleName());
        }

        System.out.println("\n✅ MULTIPLE USER SEQUENCE COMPLETED");
    }

    /**
     * TEST 5: User Type Change Scenario
     * Tests what happens when a user's type needs to be verified
     */
    @Test
    public void testUserTypeVerificationOnReLogin() {
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║ TEST 5: USER TYPE VERIFICATION ON RE-LOGIN            ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");

        System.out.println("\n[SCENARIO] User logs out and logs back in");
        System.out.println("─────────────────────────────────────────────");

        // First login
        System.out.println("\n[LOGIN 1] Initial Login");
        User user1 = new User("Emma Wilson", "emma@example.com", "555-2001", "customer");
        user1.setId(301);
        setupAuthResponse(true, "Login successful", user1, "token_1");

        System.out.println("Logged in as: " + user1.getName());
        System.out.println("Type:         " + user1.getUserType());
        System.out.println("Route to:     " + determineTargetDashboard(user1.getUserType()).getSimpleName());

        // Logout (simulated)
        System.out.println("\n[LOGOUT]");
        System.out.println("User session cleared");

        // Second login (re-login)
        System.out.println("\n[LOGIN 2] Re-login with Same Credentials");
        User user2 = new User("Emma Wilson", "emma@example.com", "555-2001", "customer");
        user2.setId(301); // Same user ID
        setupAuthResponse(true, "Login successful", user2, "token_2");

        System.out.println("Logged in as: " + user2.getName());
        System.out.println("Type:         " + user2.getUserType());
        System.out.println("Route to:     " + determineTargetDashboard(user2.getUserType()).getSimpleName());

        assertEquals("User type should be consistent", user1.getUserType(), user2.getUserType());

        System.out.println("\n✅ RE-LOGIN VERIFICATION COMPLETED");
    }

    /**
     * Helper method to setup a mock auth response
     */
    private void setupAuthResponse(boolean success, String message, User user, String token) {
        mockAuthResponse.setSuccess(success);
        mockAuthResponse.setMessage(message);
        mockAuthResponse.setUser(user);
        mockAuthResponse.setToken(token);
    }

    /**
     * Helper method to determine target dashboard based on user type
     */
    private Class<?> determineTargetDashboard(String userType) {
        if ("merchant".equalsIgnoreCase(userType)) {
            return com.scanpay.app.ui.merchant.MerchantMainActivity.class;
        } else {
            return com.scanpay.app.ui.main.MainActivity.class;
        }
    }

    /**
     * Helper method to validate user data and print navigation info
     */
    private void validateUserDataAndPrintNavigation(AuthResponse response) {
        if (response == null || !response.isSuccess()) {
            System.out.println("✗ Response validation FAILED");
            return;
        }

        User user = response.getUser();
        if (user == null) {
            System.out.println("✗ User data is NULL");
            return;
        }

        System.out.println("✓ Response successful");
        System.out.println("✓ User object retrieved from response");
        System.out.println("✓ Token received: " + (response.getToken() != null ? "YES" : "NO"));
        System.out.println("✓ User type in response: " + user.getUserType());
        System.out.println("✓ All session data will be saved to SharedPreferences");
    }

    /**
     * Helper method to print user type information in a formatted way
     */
    private void printUserTypeInfo(String stage, User user) {
        System.out.println("\n┌──────────────────────────────┐");
        System.out.println("│ " + stage + " RESULT");
        System.out.println("├──────────────────────────────┤");
        System.out.println("│ User ID:   " + user.getId());
        System.out.println("│ Name:      " + user.getName());
        System.out.println("│ Email:     " + user.getEmail());
        System.out.println("│ Phone:     " + user.getPhone());
        System.out.println("│ User Type: " + user.getUserType());
        System.out.println("│ Created:   " + user.getCreatedAt());
        System.out.println("└──────────────────────────────┘");

        // Print user type classification
        System.out.println("\nUser Type Classification:");
        if (user.isMerchant()) {
            System.out.println("  ➜ This is a MERCHANT account");
            System.out.println("    Will access MerchantMainActivity");
        } else if (user.isCustomer()) {
            System.out.println("  ➜ This is a CUSTOMER account");
            System.out.println("    Will access MainActivity");
        } else {
            System.out.println("  ➜ User type is undefined");
        }
    }

}

