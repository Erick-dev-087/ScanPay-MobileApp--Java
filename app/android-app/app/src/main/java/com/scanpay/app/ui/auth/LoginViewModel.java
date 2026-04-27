package com.scanpay.app.ui.auth;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.scanpay.app.data.Resource;
import com.scanpay.app.data.repository.AuthRepository;
import com.scanpay.app.data.response.AuthResponse;
import com.scanpay.app.utils.SessionManager;
import com.scanpay.app.utils.ValidationUtils;

/**
 * ViewModel for LoginActivity.
 * Handles input validation and delegates API calls to AuthRepository.
 */
public class LoginViewModel extends AndroidViewModel {

    private final AuthRepository authRepository;
    private final SessionManager sessionManager;

    // Login result observable
    private final MediatorLiveData<Resource<AuthResponse>> loginResult = new MediatorLiveData<>();

    // Validation error messages (null = valid)
    private final MutableLiveData<String> emailError = new MutableLiveData<>();
    private final MutableLiveData<String> passwordError = new MutableLiveData<>();

    public LoginViewModel(@NonNull Application application) {
        super(application);
        sessionManager = new SessionManager(application);
        authRepository = new AuthRepository(sessionManager);
    }

    // ── Observables ─────────────────────────────────────────────────

    public LiveData<Resource<AuthResponse>> getLoginResult() {
        return loginResult;
    }

    public LiveData<String> getEmailError() {
        return emailError;
    }

    public LiveData<String> getPasswordError() {
        return passwordError;
    }

    // ── Actions ─────────────────────────────────────────────────────

    /**
     * Validate inputs and attempt login.
     * @return true if validation passed and API call was initiated.
     */
    public boolean login(String email, String password) {
        emailError.setValue(null);
        passwordError.setValue(null);

        boolean valid = true;

        if (!ValidationUtils.isNotEmpty(email)) {
            emailError.setValue("Please enter your email");
            valid = false;
        } else if (!ValidationUtils.isValidEmail(email)) {
            emailError.setValue("Please enter a valid email");
            valid = false;
        }

        if (!ValidationUtils.isNotEmpty(password)) {
            passwordError.setValue("Please enter your password");
            valid = false;
        }

        if (!valid) return false;

        LiveData<Resource<AuthResponse>> source = authRepository.login(email.trim(), password);
        loginResult.addSource(source, resource -> {
            loginResult.setValue(resource);
            if (resource != null && !resource.isLoading()) {
                loginResult.removeSource(source);
            }
        });

        return true;
    }

    /**
     * Check if the user is already logged in.
     */
    public boolean isLoggedIn() {
        return sessionManager.isLoggedIn();
    }

    /**
     * Check if logged-in user is a merchant.
     */
    public boolean isMerchant() {
        return sessionManager.isMerchant();
    }

    /**
     * Validate that user data is properly stored in session.
     * This ensures the user has been successfully logged in before navigation.
     * @return true if all required user data is available in session
     */
    public boolean isUserDataValid() {
        if (!isLoggedIn()) {
            return false;
        }

        // Verify essential user data is available
        String userType = sessionManager.getUserType();
        int userId = sessionManager.getUserId();
        String userName = sessionManager.getUserName();
        String userEmail = sessionManager.getUserEmail();
        String token = sessionManager.getToken();

        // All critical fields must be present and valid
        return userType != null && !userType.isEmpty() &&
               userId >= 0 &&
               userName != null && !userName.isEmpty() &&
               userEmail != null && !userEmail.isEmpty() &&
               token != null && !token.isEmpty();
    }

    /**
     * Get the user type for navigation purposes.
     * Validates that user data is available before returning.
     * @return "merchant" if user is a merchant, "customer" otherwise, or null if data is invalid
     */
    public String getUserType() {
        if (!isUserDataValid()) {
            return null;
        }
        return sessionManager.getUserType();
    }

    /**
     * Get the logged-in user from session.
     * @return User object from session, or null if not available
     */
    public com.scanpay.app.data.model.User getLoggedInUser() {
        return sessionManager.getUser();
    }

    /**
     * Determine the correct dashboard based on user type.
     * This method should be called after successful login validation.
     * @return Class reference to the appropriate activity (MerchantMainActivity or MainActivity)
     */
    public Class<?> getDashboardActivity() {
        if (!isUserDataValid()) {
            return null;
        }

        if (sessionManager.isMerchant()) {
            return com.scanpay.app.ui.merchant.MerchantMainActivity.class;
        } else {
            return com.scanpay.app.ui.main.MainActivity.class;
        }
    }

}
