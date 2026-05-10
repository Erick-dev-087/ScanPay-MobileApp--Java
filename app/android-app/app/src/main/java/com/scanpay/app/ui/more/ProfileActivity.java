package com.scanpay.app.ui.more;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.scanpay.app.ui.base.BaseActivity;

import com.scanpay.app.R;
import com.scanpay.app.utils.SessionManager;

public class ProfileActivity extends BaseActivity {

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        sessionManager = new SessionManager(this);

        TextView tvName = findViewById(R.id.tv_profile_name);
        TextView tvEmail = findViewById(R.id.tv_profile_email);
        TextView tvPhone = findViewById(R.id.tv_profile_phone);
        Button btnEdit = findViewById(R.id.btn_edit_profile);

        tvName.setText(sessionManager.getUserName());
        tvEmail.setText(sessionManager.getUserEmail());
        tvPhone.setText(sessionManager.getUserPhone());

        btnEdit.setOnClickListener(v ->
                Toast.makeText(this, R.string.edit_profile_coming_soon, Toast.LENGTH_SHORT).show());
    }
}
