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
import com.scanpay.app.data.response.VendorAuthResponse;
import com.scanpay.app.utils.Constants;
import com.scanpay.app.utils.SessionManager;
import com.scanpay.app.utils.ValidationUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * ViewModel for RegisterActivity.
 * Supports both customer and merchant registration flows.
 */
public class RegisterViewModel extends AndroidViewModel {

    private final AuthRepository authRepository;
    private final SessionManager sessionManager;

    // Registration results
    private final MediatorLiveData<Resource<AuthResponse>> customerRegResult = new MediatorLiveData<>();
    private final MediatorLiveData<Resource<VendorAuthResponse>> vendorRegResult = new MediatorLiveData<>();

    // Per-field validation errors: key = field name, value = error message (null if valid)
    private final MutableLiveData<Map<String, String>> fieldErrors = new MutableLiveData<>(new HashMap<>());

    public RegisterViewModel(@NonNull Application application) {
        super(application);
        sessionManager = new SessionManager(application);
        authRepository = new AuthRepository(sessionManager);
    }

    // ── Observables ─────────────────────────────────────────────────

    public LiveData<Resource<AuthResponse>> getCustomerRegResult() {
        return customerRegResult;
    }

    public LiveData<Resource<VendorAuthResponse>> getVendorRegResult() {
        return vendorRegResult;
    }

    public LiveData<Map<String, String>> getFieldErrors() {
        return fieldErrors;
    }

    // ── Customer Registration ───────────────────────────────────────

    /**
     * Validate and register a customer.
     * @return true if validation passed and API call was initiated
     */
    public boolean registerCustomer(String name, String email, String phone,
                                     String password, String confirmPassword) {
        Map<String, String> errors = new HashMap<>();

        if (!ValidationUtils.isNotEmpty(name)) {
            errors.put("name", "Please enter your name");
        }
        if (!ValidationUtils.isNotEmpty(email)) {
            errors.put("email", "Please enter your email");
        } else if (!ValidationUtils.isValidEmail(email)) {
            errors.put("email", "Please enter a valid email");
        }
        if (!ValidationUtils.isNotEmpty(phone)) {
            errors.put("phone", "Please enter your phone number");
        } else if (!ValidationUtils.isValidKenyanPhone(phone)) {
            errors.put("phone", "Please enter a valid Kenyan phone number");
        }
        if (!ValidationUtils.isNotEmpty(password)) {
            errors.put("password", "Please enter a password");
        } else if (!ValidationUtils.isValidPassword(password)) {
            errors.put("password", "Password must be at least 6 characters");
        }
        if (!ValidationUtils.doPasswordsMatch(password, confirmPassword)) {
            errors.put("confirmPassword", "Passwords do not match");
        }

        fieldErrors.setValue(errors);
        if (!errors.isEmpty()) return false;

        // Normalise phone to 254 format
        String normPhone = ValidationUtils.normalizeKenyanPhone(phone.trim());

        LiveData<Resource<AuthResponse>> source =
                authRepository.registerCustomer(name.trim(), email.trim(), normPhone, password);

        customerRegResult.addSource(source, resource -> {
            customerRegResult.setValue(resource);
            if (resource != null && !resource.isLoading()) {
                customerRegResult.removeSource(source);
            }
        });

        return true;
    }

    // ── Merchant Registration ───────────────────────────────────────

    /**
     * Validate and register a merchant/vendor.
     * @return true if validation passed and API call was initiated
     */
    public boolean registerMerchant(String name, String email, String phone,
                                     String password, String confirmPassword,
                                     String businessName, String shortCode,
                                     String merchantId, String mcc, String storeLabel,
                                     String shortcodeType, String paybillAccountNumber) {

        Map<String, String> errors = new HashMap<>();

        // Common fields
        if (!ValidationUtils.isNotEmpty(name)) {
            errors.put("name", "Please enter your name");
        }
        if (!ValidationUtils.isNotEmpty(email)) {
            errors.put("email", "Please enter your email");
        } else if (!ValidationUtils.isValidEmail(email)) {
            errors.put("email", "Please enter a valid email");
        }
        if (!ValidationUtils.isNotEmpty(phone)) {
            errors.put("phone", "Please enter your phone number");
        } else if (!ValidationUtils.isValidKenyanPhone(phone)) {
            errors.put("phone", "Please enter a valid Kenyan phone number");
        }
        if (!ValidationUtils.isNotEmpty(password)) {
            errors.put("password", "Please enter a password");
        } else if (!ValidationUtils.isValidPassword(password)) {
            errors.put("password", "Password must be at least 6 characters");
        }
        if (!ValidationUtils.doPasswordsMatch(password, confirmPassword)) {
            errors.put("confirmPassword", "Passwords do not match");
        }

        // Merchant-specific fields
        if (!ValidationUtils.isNotEmpty(businessName)) {
            errors.put("businessName", "Please enter your business name");
        }
        if (!ValidationUtils.isNotEmpty(shortCode)) {
            errors.put("shortCode", "Please enter your business short code");
        }

        fieldErrors.setValue(errors);
        if (!errors.isEmpty()) return false;

        String normPhone = ValidationUtils.normalizeKenyanPhone(phone.trim());

        LiveData<Resource<VendorAuthResponse>> source =
                authRepository.registerVendor(
                        name.trim(), email.trim(), normPhone, password,
                        businessName.trim(), shortCode.trim(),
                        merchantId != null ? merchantId.trim() : "",
                        mcc != null ? mcc.trim() : "",
                        storeLabel != null ? storeLabel.trim() : "",
                        shortcodeType != null ? shortcodeType.trim() : "",
                        paybillAccountNumber != null ? paybillAccountNumber.trim() : "");

        vendorRegResult.addSource(source, resource -> {
            vendorRegResult.setValue(resource);
            if (resource != null && !resource.isLoading()) {
                vendorRegResult.removeSource(source);
            }
        });

        return true;
    }

    // ── Helpers ─────────────────────────────────────────────────────

    public boolean isMerchant() {
        return sessionManager.isMerchant();
    }
}

