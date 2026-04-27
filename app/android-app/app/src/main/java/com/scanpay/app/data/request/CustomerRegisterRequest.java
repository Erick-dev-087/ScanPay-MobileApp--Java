package com.scanpay.app.data.request;

import com.google.gson.annotations.SerializedName;

/**
 * Request body for POST /api/register/user (customer registration).
 */
public class CustomerRegisterRequest {

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("phone_number")
    private String phone;

    @SerializedName("password")
    private String password;

    public CustomerRegisterRequest(String name, String email, String phone, String password) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getPassword() { return password; }
}
