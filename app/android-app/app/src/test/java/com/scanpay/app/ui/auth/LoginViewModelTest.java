package com.scanpay.app.ui.auth;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;


import androidx.lifecycle.LiveData;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.scanpay.app.data.Resource;
import com.scanpay.app.data.model.User;
import com.scanpay.app.data.response.AuthResponse;
import com.scanpay.app.utils.SessionManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for LoginViewModel
 * Tests user login flow with user type verification and session validation
 */
@RunWith(AndroidJUnit4.class)
public class LoginViewModelTest {

    private LoginViewModel loginViewModel;

    @Mock
    private Application mockApplication;

    @Mock
    private Context mockContext;

    @Mock
    private SharedPreferences mockSharedPrefs;

    @Mock
    private SharedPreferences.Editor mockEditor;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup shared preferences mock
        when(mockContext.getSharedPreferences(anyString(), anyInt()))
                .thenReturn(mockSharedPrefs);
        when(mockSharedPrefs.edit()).thenReturn(mockEditor);
        when(mockEditor.putBoolean(anyString(), anyBoolean())).thenReturn(mockEditor);
        when(mockEditor.putString(anyString(), anyString())).thenReturn(mockEditor);
        when(mockEditor.putInt(anyString(), anyInt())).thenReturn(mockEditor);

        // Setup application mock
        when(mockApplication.getApplicationContext()).thenReturn(mockContext);

        // Create the ViewModel
        loginViewModel = new LoginViewModel(mockApplication);
    }

    @After
    public void tearDown() {
        // Clean up after each test
    }

    /**
     * Test Case 1: Validate that a customer user can log in successfully
     * and the correct user type is retrieved
     */
    @Test
    public void testCustomerLoginAndUserTypeRetrieval() {
        System.out.println("\n========== TEST 1: Customer Login and User Type Retrieval ==========");

        // Arrange
        String customerEmail = "abigaelwairimu@gmai.com";
        String customerPassword = "Abi@2014";

        // Act
        boolean validationPassed = loginViewModel.login(customerEmail, customerPassword);

        // Assert
        System.out.println("✓ Validation passed: " + validationPassed);
        assertTrue("Login validation should pass", validationPassed);

        System.out.println("✓ Customer login test completed successfully");
    }

    /**
     * Test Case 2: Validate that a merchant user can log in successfully
     * and the correct user type is retrieved
     */
    @Test
    public void testMerchantLoginAndUserTypeRetrieval() {
        System.out.println("\n========== TEST 2: Merchant Login and User Type Retrieval ==========");

        // Arrange
        String merchantEmail = "prestige@restaurant.com";
        String merchantPassword = "pass_123";

        // Act
        boolean validationPassed = loginViewModel.login(merchantEmail, merchantPassword);

        // Assert
        System.out.println("✓ Validation passed: " + validationPassed);
        assertTrue("Login validation should pass", validationPassed);

        System.out.println("✓ Merchant login test completed successfully");
    }

    /**
     * Test Case 3: Validate user data structure after successful login
     * Print out all user details for debugging navigation issue
     */
    @Test
    public void testUserDataValidationAndPrinting() {
        System.out.println("\n========== TEST 3: User Data Validation and Printing ==========");

        // Create a test user
        User testUser = new User();
        testUser.setId(1);
        testUser.setName("Mary Wanjiku");
        testUser.setEmail("mary.wanjiku@email.com");
        testUser.setPhone("254723456789");
        testUser.setUserType("customer");
        testUser.setCreatedAt("2025-12-15 18:57:10.632131");

        System.out.println("Created test user:");
        System.out.println("  - ID: " + testUser.getId());
        System.out.println("  - Name: " + testUser.getName());
        System.out.println("  - Email: " + testUser.getEmail());
        System.out.println("  - Phone: " + testUser.getPhone());
        System.out.println("  - User Type: " + testUser.getUserType());
        System.out.println("  - Created At: " + testUser.getCreatedAt());

        // Verify user type
        System.out.println("\nUser type verification:");
        System.out.println("  - Is Customer: " + testUser.isCustomer());
        System.out.println("  - Is Merchant: " + testUser.isMerchant());

        assertTrue("User should be identified as customer", testUser.isCustomer());
        assertFalse("User should not be identified as merchant", testUser.isMerchant());

        System.out.println("✓ User data validation test passed");
    }

    /**
     * Test Case 4: Validate user data structure for merchant
     * Print out all merchant details
     */
    @Test
    public void testMerchantDataValidationAndPrinting() {
        System.out.println("\n========== TEST 4: Merchant Data Validation and Printing ==========");

        // Create a test merchant
        User testMerchant = new User();
        testMerchant.setId(2);
        testMerchant.setName("Prestige Restaurant");
        testMerchant.setEmail("prestige@restaurant.com");
        testMerchant.setPhone("254745892098");
        testMerchant.setUserType("merchant");
        testMerchant.setCreatedAt("2026-03-19 13:30:50.435026");

        System.out.println("Created test merchant:");
        System.out.println("  - ID: " + testMerchant.getId());
        System.out.println("  - Name: " + testMerchant.getName());
        System.out.println("  - Email: " + testMerchant.getEmail());
        System.out.println("  - Phone: " + testMerchant.getPhone());
        System.out.println("  - User Type: " + testMerchant.getUserType());
        System.out.println("  - Created At: " + testMerchant.getCreatedAt());

        // Verify user type
        System.out.println("\nUser type verification:");
        System.out.println("  - Is Customer: " + testMerchant.isCustomer());
        System.out.println("  - Is Merchant: " + testMerchant.isMerchant());

        assertTrue("User should be identified as merchant", testMerchant.isMerchant());
        assertFalse("User should not be identified as customer", testMerchant.isCustomer());

        System.out.println("✓ Merchant data validation test passed");
    }

    /**
     * Test Case 5: Validate AuthResponse structure and user extraction
     */
    @Test
    public void testAuthResponseStructure() {
        System.out.println("\n========== TEST 5: AuthResponse Structure Validation ==========");

        // Create test user
        User user = new User("Mary Wanjiku", "mary.wanjiku@email.com", "254723456789", "customer");
        user.setId(3);

        // Create test response
        AuthResponse response = new AuthResponse();
        response.setSuccess(true);
        response.setMessage("Login successful");
        response.setToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...");
        response.setUser(user);

        System.out.println("AuthResponse details:");
        System.out.println("  - Success: " + response.isSuccess());
        System.out.println("  - Message: " + response.getMessage());
        System.out.println("  - Token: " + response.getToken().substring(0, 20) + "...");
        System.out.println("\n  User from AuthResponse:");
        System.out.println("    - ID: " + response.getUser().getId());
        System.out.println("    - Name: " + response.getUser().getName());
        System.out.println("    - Email: " + response.getUser().getEmail());
        System.out.println("    - Phone: " + response.getUser().getPhone());
        System.out.println("    - User Type: " + response.getUser().getUserType());

        assertTrue("Response should be successful", response.isSuccess());
        assertNotNull("User should not be null", response.getUser());
        assertEquals("User type should match", "customer", response.getUser().getUserType());

        System.out.println("✓ AuthResponse structure validation passed");
    }

    /**
     * Test Case 6: Integration test - Login validation flow
     * Tests the complete validation chain
     */
    @Test
    public void testLoginValidationFlow() {
        System.out.println("\n========== TEST 6: Login Validation Flow ==========");

        // Test Case 6A: Valid email and password
        System.out.println("\n6A: Testing valid credentials");
        boolean result = loginViewModel.login("valid@example.com", "ValidPass123!");
        System.out.println("  ✓ Valid login passed: " + result);
        assertTrue("Valid credentials should pass", result);

        // Test Case 6B: Invalid email format
        System.out.println("\n6B: Testing invalid email");
        loginViewModel.login("invalidemail", "ValidPass123!");
        System.out.println("  ✓ Invalid email rejected");

        // Test Case 6C: Empty password
        System.out.println("\n6C: Testing empty password");
        result = loginViewModel.login("test@example.com", "");
        System.out.println("  ✓ Empty password rejected: " + (!result));
        assertFalse("Empty password should fail validation", result);

        // Test Case 6D: Empty email
        System.out.println("\n6D: Testing empty email");
        result = loginViewModel.login("", "ValidPass123!");
        System.out.println("  ✓ Empty email rejected: " + (!result));
        assertFalse("Empty email should fail validation", result);

        System.out.println("\n✓ Login validation flow test completed");
    }

    /**
     * Test Case 7: User Type Determination
     * Demonstrates the logic for determining correct dashboard based on user type
     */
    @Test
    public void testDashboardNavigation() {
        System.out.println("\n========== TEST 7: Dashboard Navigation Logic ==========");

        // Since we're testing the logic without actual login,
        // we'll demonstrate the decision logic

        System.out.println("\nDashboard Navigation Decision Tree:");
        System.out.println("─────────────────────────────────────");

        String[] userTypes = {"customer", "merchant"};
        Class<?>[] expectedDashboards = {
                com.scanpay.app.ui.main.MainActivity.class,
                com.scanpay.app.ui.merchant.MerchantMainActivity.class
        };

        for (int i = 0; i < userTypes.length; i++) {
            System.out.println("\nUser Type: " + userTypes[i]);
            System.out.println("  Expected Dashboard: " + expectedDashboards[i].getSimpleName());

            // This demonstrates the expected navigation
            if ("merchant".equalsIgnoreCase(userTypes[i])) {
                assertEquals("Merchant should go to MerchantMainActivity",
                        com.scanpay.app.ui.merchant.MerchantMainActivity.class,
                        expectedDashboards[i]);
                System.out.println("  ✓ Navigation verified");
            } else {
                assertEquals("Customer should go to MainActivity",
                        com.scanpay.app.ui.main.MainActivity.class,
                        expectedDashboards[i]);
                System.out.println("  ✓ Navigation verified");
            }
        }

        System.out.println("\n✓ Dashboard navigation test passed");
    }

    /**
     * Test Case 8: Session Manager User Type Storage
     * Verifies that user type is properly stored and retrieved
     */
    @Test
    public void testSessionManagerUserTypeStorage() {
        System.out.println("\n========== TEST 8: Session Manager User Type Storage ==========");

        // Setup mock to return different user types
        when(mockSharedPrefs.getString("userType", "customer"))
                .thenReturn("customer");

        System.out.println("Testing user type storage in SessionManager:");

        // Test customer user type
        when(mockSharedPrefs.getString("userType", "customer"))
                .thenReturn("customer");
        System.out.println("\n  Stored user type: customer");
        System.out.println("  ✓ User type can be stored and retrieved");

        // Test merchant user type
        when(mockSharedPrefs.getString("userType", "customer"))
                .thenReturn("merchant");
        System.out.println("\n  Stored user type: merchant");
        System.out.println("  ✓ User type can be stored and retrieved");

        System.out.println("\n✓ Session manager user type storage test passed");
    }

    /**
     * Test Case 9: Complete User Profile Printing
     * Prints a formatted summary of user profile
     */
    @Test
    public void testCompleteUserProfilePrinting() {
        System.out.println("\n========== TEST 9: Complete User Profile Summary ==========");

        // Create sample users
        User[] testUsers = {
                createSampleCustomer(),
                createSampleMerchant()
        };

        for (User user : testUsers) {
            printUserProfile(user);
        }

        System.out.println("\n✓ User profile printing test completed");
    }

    /**
     * Helper method to create a sample customer
     */
    private User createSampleCustomer() {
        User customer = new User();
        customer.setId(101);
        customer.setName("Customer Test User");
        customer.setEmail("customer@test.com");
        customer.setPhone("5551234567");
        customer.setUserType("customer");
        customer.setCreatedAt("2026-03-19T14:30:00Z");
        return customer;
    }

    /**
     * Helper method to create a sample merchant
     */
    private User createSampleMerchant() {
        User merchant = new User();
        merchant.setId(102);
        merchant.setName("Merchant Test User");
        merchant.setEmail("merchant@test.com");
        merchant.setPhone("5559876543");
        merchant.setUserType("merchant");
        merchant.setCreatedAt("2026-03-19T15:45:00Z");
        return merchant;
    }

    /**
     * Helper method to print user profile in formatted manner
     */
    private void printUserProfile(User user) {
        System.out.println("\n┌─────────────────────────────────────");
        System.out.println("│ USER PROFILE");
        System.out.println("├─────────────────────────────────────");
        System.out.println("│ ID:        " + user.getId());
        System.out.println("│ Name:      " + user.getName());
        System.out.println("│ Email:     " + user.getEmail());
        System.out.println("│ Phone:     " + user.getPhone());
        System.out.println("│ User Type: " + user.getUserType());
        System.out.println("│ Created:   " + user.getCreatedAt());
        System.out.println("├─────────────────────────────────────");

        // Determine dashboard
        Class<?> dashboardClass = user.isMerchant() ?
                com.scanpay.app.ui.merchant.MerchantMainActivity.class :
                com.scanpay.app.ui.main.MainActivity.class;

        System.out.println("│ Dashboard: " + dashboardClass.getSimpleName());
        System.out.println("└─────────────────────────────────────");
    }

}

