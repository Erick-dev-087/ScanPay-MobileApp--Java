package com.scanpay.app.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.scanpay.app.ui.base.BaseActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.scanpay.app.R;
import com.scanpay.app.adapters.TransactionAdapter;
import com.scanpay.app.data.model.Transaction;
import com.scanpay.app.ui.scanner.QRScannerActivity;
import com.scanpay.app.ui.fragments.AnalyticsFragment;
import com.scanpay.app.ui.fragments.QRCodesFragment;
import com.scanpay.app.ui.fragments.MoreFragment;
import com.scanpay.app.utils.CurrencyUtils;
import com.scanpay.app.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private TextView tvGreeting, tvTotalSpent;
    private MaterialButton btnViewAll;
    private Button btnScanPay;
    private RecyclerView rvRecentActivity;
    private BottomNavigationView bottomNavigation;
    private ImageView btnMenu;
    private ImageView btnThemeToggle;
    private ImageView btnNotifications;
    private View dashboardContent;
    private boolean isProgrammaticNav;

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
        btnThemeToggle = findViewById(R.id.btn_theme_toggle);
        btnNotifications = findViewById(R.id.btn_notifications);
        btnViewAll = findViewById(R.id.btn_view_all);
    }

    private void setupUI() {
        tvGreeting.setText(getString(R.string.available_balance));

        // Setup RecyclerView
        rvRecentActivity.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        transactionAdapter = new TransactionAdapter(new ArrayList<>(), transaction -> {
            Toast.makeText(this, "Transaction: " + transaction.getMerchantName(), Toast.LENGTH_SHORT).show();
        });
        rvRecentActivity.setAdapter(transactionAdapter);

        // Scan & Pay button
        btnScanPay.setOnClickListener(v -> {
            startActivity(new Intent(this, QRScannerActivity.class));
        });

        btnMenu.setOnClickListener(v ->
            Toast.makeText(this, "Menu clicked", Toast.LENGTH_SHORT).show());

        btnThemeToggle.setOnClickListener(v -> toggleThemePreference());

        btnNotifications.setOnClickListener(v ->
            Toast.makeText(this, "Notifications", Toast.LENGTH_SHORT).show());

        btnViewAll.setOnClickListener(v -> navigateToAnalytics(true));

    }

    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            if (isProgrammaticNav) {
                return true;
            }
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_dashboard) {
                showDashboard();
                return true;
            } else if (itemId == R.id.navigation_qr_codes) {
                showFragment(new QRCodesFragment());
                return true;
            } else if (itemId == R.id.navigation_analytics) {
                showFragment(AnalyticsFragment.newInstance(false));
                return true;
            } else if (itemId == R.id.navigation_more) {
                showFragment(new MoreFragment());
                return true;
            }
            return false;
        });
    }

    private void navigateToAnalytics(boolean scrollToNodes) {
        isProgrammaticNav = true;
        bottomNavigation.setSelectedItemId(R.id.navigation_analytics);
        isProgrammaticNav = false;
        showFragment(AnalyticsFragment.newInstance(scrollToNodes));
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
        tvTotalSpent.setText(CurrencyUtils.formatKshDecimal(14245.80));

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

