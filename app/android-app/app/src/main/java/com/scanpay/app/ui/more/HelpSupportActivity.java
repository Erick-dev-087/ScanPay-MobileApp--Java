package com.scanpay.app.ui.more;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.scanpay.app.ui.base.BaseActivity;

import com.scanpay.app.R;

public class HelpSupportActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_support);

        Button btnContact = findViewById(R.id.btn_contact_support);
        Button btnFaq = findViewById(R.id.btn_faq);

        btnContact.setOnClickListener(v ->
                Toast.makeText(this, R.string.help_support_contact_coming_soon, Toast.LENGTH_SHORT).show());
        btnFaq.setOnClickListener(v ->
                Toast.makeText(this, R.string.help_support_faq_coming_soon, Toast.LENGTH_SHORT).show());
    }
}
