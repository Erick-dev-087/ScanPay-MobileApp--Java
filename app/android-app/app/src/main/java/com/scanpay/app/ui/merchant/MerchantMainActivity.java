package com.scanpay.app.ui.merchant;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.scanpay.app.ui.base.BaseActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.scanpay.app.R;
import com.scanpay.app.api.ApiClient;
import com.scanpay.app.api.ApiService;
import com.scanpay.app.data.response.TopUpBalanceResponse;
import com.scanpay.app.ui.fragments.AnalyticsFragment;
import com.scanpay.app.ui.fragments.QRCodesFragment;
import com.scanpay.app.ui.fragments.MoreFragment;
import com.scanpay.app.utils.Constants;
import com.scanpay.app.utils.CurrencyUtils;
import com.scanpay.app.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MerchantMainActivity extends BaseActivity {

    private static final String MASKED_BALANCE = "••••••";

    private TextView tvWeekRevenue;
    private TextView tvServiceTokenBalance;
    private LineChart lineChart;
    private BottomNavigationView bottomNavigation;
    private MaterialButton btnViewAllReceipts;
    private MaterialButton btnTopUp;
    private ImageButton btnRefreshBalance;
    private ImageButton btnToggleBalanceVisibility;
    private ProgressBar progressBalance;
    private boolean isProgrammaticNav;
    private boolean balanceVisible = true;
    private double currentBalance = 0;

    private SessionManager sessionManager;
    private ApiService apiService;

    private final ActivityResultLauncher<Intent> topUpLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    double updatedBalance = result.getData().getDoubleExtra(
                            Constants.EXTRA_SERVICE_TOKEN_BALANCE, -1);
                    if (updatedBalance >= 0) {
                        currentBalance = updatedBalance;
                        updateBalanceDisplay();
                    } else {
                        fetchServiceTokenBalance(false);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_main);

        sessionManager = new SessionManager(this);
        apiService = ApiClient.getApiService();

        initViews();
        setupChart();
        setupBottomNavigation();
        setupServiceTokensSection();
        loadMockData();
        fetchServiceTokenBalance(false);
        handleNavigationIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleNavigationIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchServiceTokenBalance(false);
    }

    private void initViews() {
        tvWeekRevenue = findViewById(R.id.tv_week_revenue);
        tvServiceTokenBalance = findViewById(R.id.tv_service_token_balance);
        lineChart = findViewById(R.id.line_chart);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        btnViewAllReceipts = findViewById(R.id.btn_view_all_receipts);
        btnTopUp = findViewById(R.id.btn_top_up);
        btnRefreshBalance = findViewById(R.id.btn_refresh_balance);
        btnToggleBalanceVisibility = findViewById(R.id.btn_toggle_balance_visibility);
        progressBalance = findViewById(R.id.progress_balance);
    }

    private void setupServiceTokensSection() {
        btnTopUp.setOnClickListener(v ->
                topUpLauncher.launch(new Intent(this, TopUpActivity.class)));

        btnRefreshBalance.setOnClickListener(v -> fetchServiceTokenBalance(true));

        btnToggleBalanceVisibility.setOnClickListener(v -> {
            balanceVisible = !balanceVisible;
            btnToggleBalanceVisibility.setImageResource(balanceVisible
                    ? R.drawable.ic_visibility
                    : R.drawable.ic_visibility_off);
            updateBalanceDisplay();
        });
    }

    private void fetchServiceTokenBalance(boolean showToastOnSuccess) {
        progressBalance.setVisibility(View.VISIBLE);

        apiService.getTopUpBalance().enqueue(new Callback<TopUpBalanceResponse>() {
            @Override
            public void onResponse(Call<TopUpBalanceResponse> call, Response<TopUpBalanceResponse> response) {
                progressBalance.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null && response.body().getBalance() != null) {
                    currentBalance = response.body().getBalance();
                    updateBalanceDisplay();
                    if (showToastOnSuccess) {
                        Toast.makeText(MerchantMainActivity.this,
                                R.string.balance_refreshed, Toast.LENGTH_SHORT).show();
                    }
                } else if (showToastOnSuccess) {
                    Toast.makeText(MerchantMainActivity.this,
                            R.string.balance_refresh_failed, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TopUpBalanceResponse> call, Throwable t) {
                progressBalance.setVisibility(View.GONE);
                if (showToastOnSuccess) {
                    Toast.makeText(MerchantMainActivity.this,
                            R.string.error_network_try_again, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateBalanceDisplay() {
        if (!balanceVisible) {
            tvServiceTokenBalance.setText(MASKED_BALANCE);
            return;
        }
        tvServiceTokenBalance.setText(CurrencyUtils.formatKshDecimal(currentBalance));
    }

    private void handleNavigationIntent(Intent intent) {
        if (intent == null) {
            return;
        }

        int navTarget = intent.getIntExtra(Constants.EXTRA_NAV_TARGET, -1);
        if (navTarget == -1) {
            return;
        }

        intent.removeExtra(Constants.EXTRA_NAV_TARGET);

        isProgrammaticNav = true;
        bottomNavigation.setSelectedItemId(navTarget);
        isProgrammaticNav = false;

        if (navTarget == R.id.navigation_qr_codes) {
            showFragment(new QRCodesFragment());
        } else if (navTarget == R.id.navigation_analytics) {
            showFragment(AnalyticsFragment.newInstance(false));
        } else if (navTarget == R.id.navigation_more) {
            showFragment(new MoreFragment());
        }
    }

    private void setupChart() {
        lineChart.setBackgroundColor(Color.TRANSPARENT);
        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(false);
        lineChart.setPinchZoom(false);
        lineChart.setDrawGridBackground(false);
        lineChart.getLegend().setEnabled(false);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(ContextCompat.getColor(this, R.color.ui_on_surface_variant));
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]{"Mon", "Tue", "Wed", "Thu", "Fri"}));

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setTextColor(ContextCompat.getColor(this, R.color.ui_on_surface_variant));
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(ContextCompat.getColor(this, R.color.ui_outline_variant));

        lineChart.getAxisRight().setEnabled(false);
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
        tvWeekRevenue.setText(CurrencyUtils.formatKshDecimal(14289.50));

        btnViewAllReceipts.setOnClickListener(v -> navigateToAnalytics(true));

        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, 20));
        entries.add(new Entry(1, 60));
        entries.add(new Entry(2, 30));
        entries.add(new Entry(3, 90));
        entries.add(new Entry(4, 40));

        LineDataSet dataSet = new LineDataSet(entries, "Transactions");
        dataSet.setColor(ContextCompat.getColor(this, R.color.ui_primary));
        dataSet.setLineWidth(2f);
        dataSet.setCircleColor(ContextCompat.getColor(this, R.color.ui_primary));
        dataSet.setCircleRadius(4f);
        dataSet.setDrawCircleHole(false);
        dataSet.setValueTextColor(Color.TRANSPARENT);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(ContextCompat.getColor(this, R.color.ui_primary));
        dataSet.setFillAlpha(50);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);
        lineChart.invalidate();
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
