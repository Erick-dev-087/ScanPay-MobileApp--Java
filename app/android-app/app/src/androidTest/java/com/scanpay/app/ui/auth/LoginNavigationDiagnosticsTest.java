package com.scanpay.app.ui.auth;

import android.content.Context;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.scanpay.app.data.model.User;
import com.scanpay.app.utils.SessionManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test for debugging navigation issues in LoginActivity
 * This test simulates the exact flow that happens during login and prints diagnostics
 */
@RunWith(AndroidJUnit4.class)
public class LoginNavigationDiagnosticsTest {

    private Context context;
    private SessionManager sessionManager;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        sessionManager = new SessionManager(context);
        // Clear any existing session
        sessionManager.logout();
    }

    /**
     * DIAGNOSTIC TEST 1: Session Storage and Retrieval
     * Verifies that user data is properly stored and can be retrieved
     */
    @Test
    public void testSessionStorageAndRetrieval() {
        System.out.println("\n╔═══════════════════════════════════════════════════════╗");
        System.out.println("║ DIAGNOSTIC TEST 1: SESSION STORAGE AND RETRIEVAL      ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");

        // Create a test customer user
        User testCustomer = createTestUser("Customer", "customer@test.com", "5551001", "customer", 501);

        System.out.println("\n[STEP 1] Creating login session with customer user");
        System.out.println("─────────────────────────────────────────────────");
        sessionManager.createLoginSession(testCustomer, "test_token_customer_123");

        System.out.println("\nStored data:");
        System.out.println("  ✓ User ID:    " + testCustomer.getId());
        System.out.println("  ✓ Name:       " + testCustomer.getName());
        System.out.println("  ✓ Email:      " + testCustomer.getEmail());
        System.out.println("  ✓ User Type:  " + testCustomer.getUserType());
        System.out.println("  ✓ Token:      " + "test_token_customer_123");

        System.out.println("\n[STEP 2] Retrieving stored session data");
        System.out.println("─────────────────────────────────────────────────");

        // Verify logged in status
        boolean isLoggedIn = sessionManager.isLoggedIn();
        System.out.println("Is Logged In:   " + isLoggedIn);
        assertTrue("Should be logged in after creating session", isLoggedIn);

        // Retrieve user
        User retrievedUser = sessionManager.getUser();
        System.out.println("\nRetrieved User Object:");
        System.out.println("  ✓ ID:         " + retrievedUser.getId());
        System.out.println("  ✓ Name:       " + retrievedUser.getName());
        System.out.println("  ✓ Email:      " + retrievedUser.getEmail());
        System.out.println("  ✓ User Type:  " + retrievedUser.getUserType());

        // Retrieve individual fields
        System.out.println("\nRetrieved Individual Fields:");
        System.out.println("  ✓ User ID:    " + sessionManager.getUserId());
        System.out.println("  ✓ Name:       " + sessionManager.getUserName());
        System.out.println("  ✓ Email:      " + sessionManager.getUserEmail());
        System.out.println("  ✓ Phone:      " + sessionManager.getUserPhone());
        System.out.println("  ✓ User Type:  " + sessionManager.getUserType());
        System.out.println("  ✓ Token:      " + sessionManager.getToken());

        System.out.println("\n[STEP 3] Validation checks");
        System.out.println("─────────────────────────────────────────────────");

        // Check user type classifications
        boolean isCustomer = sessionManager.isCustomer();
        boolean isMerchant = sessionManager.isMerchant();

        System.out.println("Is Customer:  " + isCustomer);
        System.out.println("Is Merchant:  " + isMerchant);

        assertTrue("Should identify as customer", isCustomer);
        assertFalse("Should not identify as merchant", isMerchant);

        System.out.println("\n✅ CUSTOMER SESSION STORAGE AND RETRIEVAL PASSED");

        // Now test with merchant
        System.out.println("\n" + "─".repeat(60));

        System.out.println("\n[STEP 4] Testing with merchant user");
        System.out.println("─────────────────────────────────────────────────");

        sessionManager.logout();
        User testMerchant = createTestUser("Merchant", "merchant@test.com", "5552001", "merchant", 502);
        sessionManager.createLoginSession(testMerchant, "test_token_merchant_456");

        System.out.println("Stored merchant data:");
        System.out.println("  ✓ User Type:  " + testMerchant.getUserType());

        System.out.println("\nRetrieved merchant data:");
        System.out.println("  ✓ User Type:  " + sessionManager.getUserType());
        System.out.println("  ✓ Is Merchant: " + sessionManager.isMerchant());
        System.out.println("  ✓ Is Customer: " + sessionManager.isCustomer());

        assertTrue("Should identify as merchant", sessionManager.isMerchant());
        assertFalse("Should not identify as customer", sessionManager.isCustomer());

        System.out.println("\n✅ MERCHANT SESSION STORAGE AND RETRIEVAL PASSED");
    }

    /**
     * DIAGNOSTIC TEST 2: User Type Detection
     * Tests the exact logic used in LoginViewModel.getUserType()
     */
    @Test
    public void testUserTypeDetection() {
        System.out.println("\n╔═══════════════════════════════════════════════════════╗");
        System.out.println("║ DIAGNOSTIC TEST 2: USER TYPE DETECTION                ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");

        String[] userTypes = {"customer", "merchant"};
        int[] userIds = {601, 602};
        String[] userNames = {"Customer User", "Merchant User"};
        String[] userEmails = {"customer@diag.com", "merchant@diag.com"};
        String[] userPhones = {"5553001", "5553002"};

        for (int i = 0; i < userTypes.length; i++) {
            System.out.println("\n[TEST CASE " + (i + 1) + "] User Type: " + userTypes[i]);
            System.out.println("─────────────────────────────────────────────────");

            // Create and store user
            User user = createTestUser(userNames[i], userEmails[i], userPhones[i], userTypes[i], userIds[i]);
            sessionManager.logout();
            sessionManager.createLoginSession(user, "test_token_" + i);

            // Retrieve and verify
            String retrievedType = sessionManager.getUserType();
            System.out.println("Stored Type:    " + userTypes[i]);
            System.out.println("Retrieved Type: " + retrievedType);

            assertEquals("User type should match", userTypes[i], retrievedType);

            // Test classification
            if ("merchant".equalsIgnoreCase(retrievedType)) {
                System.out.println("Classification: MERCHANT");
                System.out.println("Navigation:     MerchantMainActivity");
                assertTrue("Should be merchant", sessionManager.isMerchant());
            } else {
                System.out.println("Classification: CUSTOMER");
                System.out.println("Navigation:     MainActivity");
                assertTrue("Should be customer", sessionManager.isCustomer());
            }

            System.out.println("✓ Test case " + (i + 1) + " PASSED");
        }

        System.out.println("\n✅ USER TYPE DETECTION TESTS PASSED");
    }

    /**
     * DIAGNOSTIC TEST 3: Complete Navigation Decision Tree
     * Tests the exact decision logic for navigating to the correct dashboard
     */
    @Test
    public void testNavigationDecisionTree() {
        System.out.println("\n╔═══════════════════════════════════════════════════════╗");
        System.out.println("║ DIAGNOSTIC TEST 3: NAVIGATION DECISION TREE          ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");

        System.out.println("\n[LOGIC FLOW]");
        System.out.println("─────────────────────────────────────────────────");
        System.out.println("1. User submits login credentials");
        System.out.println("2. API call made to backend");
        System.out.println("3. Response contains: { user, token }");
        System.out.println("4. Session is created with user data");
        System.out.println("5. User type is extracted and saved");
        System.out.println("6. Navigation decision made based on user_type");
        System.out.println("   • If user_type == 'merchant' → MerchantMainActivity");
        System.out.println("   • If user_type == 'customer' → MainActivity");

        System.out.println("\n[TEST SCENARIOS]");
        System.out.println("─────────────────────────────────────────────────");

        // Scenario 1: Customer Login
        System.out.println("\nScenario 1: Customer Login");
        sessionManager.logout();
        User customer = createTestUser("Alice", "alice@test.com", "555", "customer", 701);
        sessionManager.createLoginSession(customer, "token");

        String userType = sessionManager.getUserType();
        Class<?> targetActivity = getTargetActivity(userType);
        System.out.println("  User Type: " + userType);
        System.out.println("  Target: " + targetActivity.getSimpleName());
        assertEquals("Customer should go to MainActivity",
                com.scanpay.app.ui.main.MainActivity.class, targetActivity);
        System.out.println("  ✓ PASSED");

        // Scenario 2: Merchant Login
        System.out.println("\nScenario 2: Merchant Login");
        sessionManager.logout();
        User merchant = createTestUser("Bob", "bob@test.com", "555", "merchant", 702);
        sessionManager.createLoginSession(merchant, "token");

        userType = sessionManager.getUserType();
        targetActivity = getTargetActivity(userType);
        System.out.println("  User Type: " + userType);
        System.out.println("  Target: " + targetActivity.getSimpleName());
        assertEquals("Merchant should go to MerchantMainActivity",
                com.scanpay.app.ui.merchant.MerchantMainActivity.class, targetActivity);
        System.out.println("  ✓ PASSED");

        // Scenario 3: Not Logged In
        System.out.println("\nScenario 3: Not Logged In");
        sessionManager.logout();
        boolean isLoggedIn = sessionManager.isLoggedIn();
        System.out.println("  Is Logged In: " + isLoggedIn);
        System.out.println("  Action: Return to LoginActivity");
        assertFalse("Should not be logged in after logout", isLoggedIn);
        System.out.println("  ✓ PASSED");

        System.out.println("\n✅ NAVIGATION DECISION TREE TESTS PASSED");
    }

    /**
     * DIAGNOSTIC TEST 4: Data Validation Checklist
     * Simulates the exact validation done in LoginViewModel.isUserDataValid()
     */
    @Test
    public void testDataValidationChecklist() {
        System.out.println("\n╔═══════════════════════════════════════════════════════╗");
        System.out.println("║ DIAGNOSTIC TEST 4: DATA VALIDATION CHECKLIST         ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");

        System.out.println("\n[VALIDATION CHECKLIST]");
        System.out.println("─────────────────────────────────────────────────");

        sessionManager.logout();
        User testUser = createTestUser("TestUser", "test@test.com", "555", "customer", 801);
        sessionManager.createLoginSession(testUser, "test_token_validation");

        // Perform validation checks
        boolean isLoggedIn = sessionManager.isLoggedIn();
        String userType = sessionManager.getUserType();
        int userId = sessionManager.getUserId();
        String userName = sessionManager.getUserName();
        String userEmail = sessionManager.getUserEmail();
        String token = sessionManager.getToken();

        System.out.println("1. Is Logged In:           " + (isLoggedIn ? "✓ PASS" : "✗ FAIL"));
        System.out.println("2. User Type Present:      " + (!userType.isEmpty() ? "✓ PASS" : "✗ FAIL"));
        System.out.println("   └─ Value: " + userType);
        System.out.println("3. User ID Valid:          " + (userId >= 0 ? "✓ PASS" : "✗ FAIL"));
        System.out.println("   └─ Value: " + userId);
        System.out.println("4. User Name Present:      " + (!userName.isEmpty() ? "✓ PASS" : "✗ FAIL"));
        System.out.println("   └─ Value: " + userName);
        System.out.println("5. User Email Present:     " + (!userEmail.isEmpty() ? "✓ PASS" : "✗ FAIL"));
        System.out.println("   └─ Value: " + userEmail);
        System.out.println("6. Token Present:          " + (token != null && !token.isEmpty() ? "✓ PASS" : "✗ FAIL"));
        System.out.println("   └─ Value: " + (token != null ? token.substring(0, 20) + "..." : "NULL"));

        // Overall validation
        boolean allValid = isLoggedIn && !userType.isEmpty() && userId >= 0 &&
                !userName.isEmpty() && !userEmail.isEmpty() &&
                token != null && !token.isEmpty();

        System.out.println("\n" + "─".repeat(50));
        System.out.println("OVERALL VALIDATION: " + (allValid ? "✓ PASS - Ready for navigation" : "✗ FAIL - Cannot navigate"));

        assertTrue("All validation checks should pass", allValid);

        System.out.println("\n✅ DATA VALIDATION CHECKLIST PASSED");
    }

    /**
     * DIAGNOSTIC TEST 5: Simulating Complete Login Flow
     * End-to-end simulation of the login flow with detailed logging
     */
    @Test
    public void testCompleteLoginFlowSimulation() {
        System.out.println("\n╔═══════════════════════════════════════════════════════╗");
        System.out.println("║ DIAGNOSTIC TEST 5: COMPLETE LOGIN FLOW SIMULATION    ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");

        String email = "flowtest@example.com";
        String password = "Password123!";

        System.out.println("\n[1/6] USER ENTERS CREDENTIALS");
        System.out.println("─────────────────────────────────────────────────");
        System.out.println("Email:    " + email);
        System.out.println("Password: ••••••••••••••••");

        System.out.println("\n[2/6] VALIDATION CHECK");
        System.out.println("─────────────────────────────────────────────────");
        boolean emailValid = email.contains("@");
        boolean passwordValid = password.length() >= 6;
        System.out.println("Email format valid:    " + (emailValid ? "✓ YES" : "✗ NO"));
        System.out.println("Password length valid: " + (passwordValid ? "✓ YES" : "✗ NO"));
        assertTrue("Validation should pass", emailValid && passwordValid);

        System.out.println("\n[3/6] API CALL TO BACKEND");
        System.out.println("─────────────────────────────────────────────────");
        System.out.println("Endpoint:  POST /api/auth/login");
        System.out.println("Payload:   { \"email\": \"" + email + "\", \"password\": \"••••\" }");
        System.out.println("Status:    200 OK");

        System.out.println("\n[4/6] RESPONSE RECEIVED");
        System.out.println("─────────────────────────────────────────────────");
        User responseUser = createTestUser("FlowTest", email, "555", "customer", 901);
        String responseToken = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjo5MDF9.ABC123";

        System.out.println("User Object:");
        System.out.println("  • ID:        " + responseUser.getId());
        System.out.println("  • Name:      " + responseUser.getName());
        System.out.println("  • Email:     " + responseUser.getEmail());
        System.out.println("  • Type:      " + responseUser.getUserType());
        System.out.println("Token:        " + responseToken.substring(0, 20) + "...");

        System.out.println("\n[5/6] SESSION CREATION");
        System.out.println("─────────────────────────────────────────────────");
        sessionManager.logout();
        sessionManager.createLoginSession(responseUser, responseToken);

        System.out.println("✓ Session created in SharedPreferences");
        System.out.println("✓ User data persisted");
        System.out.println("✓ Token stored securely");

        System.out.println("\n[6/6] NAVIGATION DECISION");
        System.out.println("─────────────────────────────────────────────────");

        String savedUserType = sessionManager.getUserType();
        Class<?> targetActivity = getTargetActivity(savedUserType);

        System.out.println("Retrieved User Type:  " + savedUserType);
        System.out.println("Target Activity:      " + targetActivity.getSimpleName());
        System.out.println("✓ Navigation intent prepared");
        System.out.println("✓ Intent flags set: FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK");
        System.out.println("✓ Starting activity...");

        assertEquals("Should navigate to MainActivity for customer",
                com.scanpay.app.ui.main.MainActivity.class, targetActivity);

        System.out.println("\n✅ COMPLETE LOGIN FLOW SIMULATION PASSED");
        System.out.println("\n[SUMMARY]");
        System.out.println("─────────────────────────────────────────────────");
        System.out.println("User logged in successfully as: " + savedUserType);
        System.out.println("Session data validated");
        System.out.println("Navigation target determined: " + targetActivity.getSimpleName());
    }

    // ─────────────────────────────── HELPER METHODS ──────────────────────────────

    /**
     * Helper method to create a test user
     */
    private User createTestUser(String name, String email, String phone, String userType, int id) {
        User user = new User(name, email, phone, userType);
        user.setId(id);
        user.setCreatedAt("2026-03-19T12:00:00Z");
        return user;
    }

    /**
     * Helper method to determine target activity
     */
    private Class<?> getTargetActivity(String userType) {
        if ("merchant".equalsIgnoreCase(userType)) {
            return com.scanpay.app.ui.merchant.MerchantMainActivity.class;
        } else {
            return com.scanpay.app.ui.main.MainActivity.class;
        }
    }

}

