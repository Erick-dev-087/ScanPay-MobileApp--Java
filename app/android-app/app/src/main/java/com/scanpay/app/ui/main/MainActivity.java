package com.scanpay.app.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.scanpay.app.R;
import com.scanpay.app.adapters.TransactionAdapter;
import com.scanpay.app.data.model.Transaction;
import com.scanpay.app.ui.scanner.QRScannerActivity;
import com.scanpay.app.ui.fragments.AnalyticsFragment;
import com.scanpay.app.ui.fragments.QRCodesFragment;
import com.scanpay.app.ui.fragments.MoreFragment;
import com.scanpay.app.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView tvGreeting, tvTotalSpent;
    private Button btnScanPay;
    private RecyclerView rvRecentActivity;
    private BottomNavigationView bottomNavigation;
    private ImageView btnMenu;
    private View dashboardContent;

    private SessionManager sessionManager;
    private TransactionAdapter transactionAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sessionManager = new SessionManager(this);

        initViews();
        setupUI();
        setupBottomNavigation();
        loadMockData();
    }

    private void initViews() {
        tvGreeting = findViewById(R.id.tv_greeting);
        tvTotalSpent = findViewById(R.id.tv_total_spent);
        btnScanPay = findViewById(R.id.btn_scan_pay);
        rvRecentActivity = findViewById(R.id.rv_recent_activity);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        btnMenu = findViewById(R.id.btn_menu);
    }

    private void setupUI() {
        // Set greeting
        String userName = sessionManager.getUserName();
        if (userName != null && !userName.isEmpty()) {
            tvGreeting.setText(getString(R.string.hi_greeting, userName));
        } else {
            tvGreeting.setText("Hi there");
        }

        // Setup RecyclerView
        rvRecentActivity.setLayoutManager(new LinearLayoutManager(this));
        transactionAdapter = new TransactionAdapter(new ArrayList<>(), transaction -> {
            Toast.makeText(this, "Transaction: " + transaction.getMerchantName(), Toast.LENGTH_SHORT).show();
        });
        rvRecentActivity.setAdapter(transactionAdapter);

        // Scan & Pay button
        btnScanPay.setOnClickListener(v -> {
            startActivity(new Intent(this, QRScannerActivity.class));
        });

        // Menu button
        btnMenu.setOnClickListener(v -> {
            Toast.makeText(this, "Menu clicked", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_dashboard) {
                showDashboard();
                return true;
            } else if (itemId == R.id.navigation_qr_codes) {
                showFragment(new QRCodesFragment());
                return true;
            } else if (itemId == R.id.navigation_analytics) {
                showFragment(new AnalyticsFragment());
                return true;
            } else if (itemId == R.id.navigation_more) {
                showFragment(new MoreFragment());
                return true;
            }
            return false;
        });
    }

    private void showDashboard() {
        // Remove any fragments and show main content
        getSupportFragmentManager().popBackStack();
    }

    private void showFragment(androidx.fragment.app.Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, fragment)
                .addToBackStack(null)
                .commit();
    }

    private void loadMockData() {
        // Mock total spent
        tvTotalSpent.setText("14,200");

        // Mock transactions
        List<Transaction> transactions = new ArrayList<>();

        Transaction t1 = new Transaction("SuperMart", 2300, "completed");
        t1.setIconColor(ContextCompat.getColor(this, R.color.transactionIconGreen));
        transactions.add(t1);

        Transaction t2 = new Transaction("Café Java", 850, "completed");
        t2.setIconColor(ContextCompat.getColor(this, R.color.transactionIconOrange));
        transactions.add(t2);

        Transaction t3 = new Transaction("City Transport", 150, "completed");
        t3.setIconColor(ContextCompat.getColor(this, R.color.transactionIconBlue));
        transactions.add(t3);

        transactionAdapter.updateData(transactions);
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            bottomNavigation.setSelectedItemId(R.id.navigation_dashboard);
        } else {
            super.onBackPressed();
        }
    }
}

