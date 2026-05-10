package com.scanpay.app.ui.more;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.scanpay.app.ui.base.BaseActivity;

import com.scanpay.app.R;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        LinearLayout btnNotifications = findViewById(R.id.btn_notifications);
        LinearLayout btnSecurity = findViewById(R.id.btn_security);
        LinearLayout btnAbout = findViewById(R.id.btn_about);

        btnNotifications.setOnClickListener(v ->
                Toast.makeText(this, R.string.settings_notifications_coming_soon, Toast.LENGTH_SHORT).show());
        btnSecurity.setOnClickListener(v ->
                Toast.makeText(this, R.string.settings_security_coming_soon, Toast.LENGTH_SHORT).show());
        btnAbout.setOnClickListener(v ->
                Toast.makeText(this, R.string.settings_about_coming_soon, Toast.LENGTH_SHORT).show());
    }
}
