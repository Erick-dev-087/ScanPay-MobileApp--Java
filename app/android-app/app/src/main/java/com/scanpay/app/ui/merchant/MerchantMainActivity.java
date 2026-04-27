package com.scanpay.app.ui.merchant;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.scanpay.app.R;
import com.scanpay.app.ui.fragments.AnalyticsFragment;
import com.scanpay.app.ui.fragments.QRCodesFragment;
import com.scanpay.app.ui.fragments.MoreFragment;
import com.scanpay.app.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class MerchantMainActivity extends AppCompatActivity {

    private TextView tvTodayRevenue, tvWeekRevenue, tvMonthRevenue;
    private TextView tvInflow, tvOutflow;
    private LineChart lineChart;
    private BottomNavigationView bottomNavigation;

    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant_main);

        sessionManager = new SessionManager(this);

        initViews();
        setupChart();
        setupBottomNavigation();
        loadMockData();
    }

    private void initViews() {
        tvTodayRevenue = findViewById(R.id.tv_today_revenue);
        tvWeekRevenue = findViewById(R.id.tv_week_revenue);
        tvMonthRevenue = findViewById(R.id.tv_month_revenue);
        tvInflow = findViewById(R.id.tv_inflow);
        tvOutflow = findViewById(R.id.tv_outflow);
        lineChart = findViewById(R.id.line_chart);
        bottomNavigation = findViewById(R.id.bottom_navigation);
    }

    private void setupChart() {
        // Chart styling
        lineChart.setBackgroundColor(Color.TRANSPARENT);
        lineChart.getDescription().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(false);
        lineChart.setPinchZoom(false);
        lineChart.setDrawGridBackground(false);
        lineChart.getLegend().setEnabled(false);

        // X Axis
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(ContextCompat.getColor(this, R.color.textSecondary));
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]{"Mon", "Tue", "Wed", "Thu", "Fri"}));

        // Left Y Axis
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setTextColor(ContextCompat.getColor(this, R.color.textSecondary));
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(ContextCompat.getColor(this, R.color.divider));

        // Right Y Axis (disabled)
        lineChart.getAxisRight().setEnabled(false);
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
        // Revenue data
        tvTodayRevenue.setText("12,500");
        tvWeekRevenue.setText("85,300");
        tvMonthRevenue.setText("280,750");
        tvInflow.setText("62,300");
        tvOutflow.setText("21,500");

        // Chart data
        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(0, 20));
        entries.add(new Entry(1, 60));
        entries.add(new Entry(2, 30));
        entries.add(new Entry(3, 90));
        entries.add(new Entry(4, 40));

        LineDataSet dataSet = new LineDataSet(entries, "Transactions");
        dataSet.setColor(ContextCompat.getColor(this, R.color.accentGreen));
        dataSet.setLineWidth(2f);
        dataSet.setCircleColor(ContextCompat.getColor(this, R.color.accentGreen));
        dataSet.setCircleRadius(4f);
        dataSet.setDrawCircleHole(false);
        dataSet.setValueTextColor(Color.TRANSPARENT);
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(ContextCompat.getColor(this, R.color.accentGreen));
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

