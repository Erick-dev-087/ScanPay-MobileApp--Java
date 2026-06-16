package com.scanpay.app.ui.fragments;

import android.os.Bundle;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.core.widget.NestedScrollView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.button.MaterialButton;
import com.scanpay.app.R;
import com.scanpay.app.utils.CurrencyUtils;
import com.scanpay.app.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class AnalyticsFragment extends Fragment {

    private static final String ARG_SCROLL_TO_NODES = "scroll_to_nodes";

    private TextView tvHeading;
    private TextView tvSubheading;
    private TextView tvGrowthCopy;
    private TextView tvNodesTitle;
    private TextView tvTimeframeSubtitle;
    private BarChart barChart;
    private LineChart lineChart;
    private LinearLayout topMerchantsContainer;
    private NestedScrollView analyticsScroll;
    private MaterialButton btnTimeframeDaily;
    private MaterialButton btnTimeframeWeekly;
    private MaterialButton btnTimeframeMonthly;
    private SessionManager sessionManager;

    private enum Timeframe {
        DAILY,
        WEEKLY,
        MONTHLY
    }

    public static AnalyticsFragment newInstance(boolean scrollToNodes) {
        AnalyticsFragment fragment = new AnalyticsFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_SCROLL_TO_NODES, scrollToNodes);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_analytics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sessionManager = new SessionManager(requireContext());
        initViews(view);
        setupUIByRole();
        setupCharts();
        setupTimeframeButtons();
        loadMockData();
        handleScrollRequest();
    }

    private void initViews(View view) {
        tvHeading = view.findViewById(R.id.tv_heading);
        tvSubheading = view.findViewById(R.id.tv_subheading);
        tvGrowthCopy = view.findViewById(R.id.tv_growth_copy);
        tvNodesTitle = view.findViewById(R.id.tv_nodes_title);
        tvTimeframeSubtitle = view.findViewById(R.id.tv_timeframe_subtitle);
        barChart = view.findViewById(R.id.bar_chart);
        lineChart = view.findViewById(R.id.line_chart);
        topMerchantsContainer = view.findViewById(R.id.top_merchants_container);
        analyticsScroll = view.findViewById(R.id.analytics_scroll);
        btnTimeframeDaily = view.findViewById(R.id.btn_timeframe_daily);
        btnTimeframeWeekly = view.findViewById(R.id.btn_timeframe_weekly);
        btnTimeframeMonthly = view.findViewById(R.id.btn_timeframe_monthly);
    }

    private void setupUIByRole() {
        boolean isMerchant = sessionManager.isMerchant();
        if (isMerchant) {
            tvHeading.setText("Merchant Insights");
            tvSubheading.setText("Track inflow, outflow and store performance.");
            tvGrowthCopy.setText("Store collections are up by 18% compared to your previous cycle.");
            tvNodesTitle.setText("Top Paying Customers");
            barChart.setVisibility(View.GONE);
            lineChart.setVisibility(View.VISIBLE);
        } else {
            tvHeading.setText("Financial Insights");
            tvSubheading.setText("Analyze your kinetic cash flow and velocity.");
            tvGrowthCopy.setText("Your kinetic income is up by 15% this month compared to your 60-day average.");
            tvNodesTitle.setText("Top Expenditure Nodes");
            barChart.setVisibility(View.VISIBLE);
            lineChart.setVisibility(View.GONE);
        }
    }

    private void setupCharts() {
        setupBarChart();
        setupLineChart();
    }

    private void setupBarChart() {
        barChart.getDescription().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.setPinchZoom(false);
        barChart.setScaleEnabled(false);
        barChart.setDrawGridBackground(false);
        barChart.setDrawBarShadow(false);
        barChart.setFitBars(true);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(0xFF8FA49B);
        xAxis.setGranularity(1f);

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(0x332F3B37);
        leftAxis.setTextColor(0xFF8FA49B);
        barChart.getAxisRight().setEnabled(false);
    }

    private void setupLineChart() {
        lineChart.getDescription().setEnabled(false);
        lineChart.getLegend().setEnabled(false);
        lineChart.setPinchZoom(false);
        lineChart.setScaleEnabled(false);
        lineChart.setDrawGridBackground(false);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(0xFF8FA49B);
        xAxis.setGranularity(1f);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(0x332F3B37);
        leftAxis.setTextColor(0xFF8FA49B);
        lineChart.getAxisRight().setEnabled(false);
    }

    private void loadMockData() {
        topMerchantsContainer.removeAllViews();
        setActiveTimeframe(Timeframe.WEEKLY);
        updateTimeframe(Timeframe.WEEKLY);

        addNodeItem("Cloud Scale AI", CurrencyUtils.formatKshDecimal(2450.00), "75%", R.color.transactionIconGreen);
        addNodeItem("Nexus Office", CurrencyUtils.formatKshDecimal(1120.00), "42%", R.color.accentCyan);
        addNodeItem("BioFuel Grid", CurrencyUtils.formatKshDecimal(490.50), "18%", R.color.accentGreen);
    }

    private void setupTimeframeButtons() {
        btnTimeframeDaily.setOnClickListener(v -> {
            setActiveTimeframe(Timeframe.DAILY);
            updateTimeframe(Timeframe.DAILY);
        });
        btnTimeframeWeekly.setOnClickListener(v -> {
            setActiveTimeframe(Timeframe.WEEKLY);
            updateTimeframe(Timeframe.WEEKLY);
        });
        btnTimeframeMonthly.setOnClickListener(v -> {
            setActiveTimeframe(Timeframe.MONTHLY);
            updateTimeframe(Timeframe.MONTHLY);
        });
    }

    private void setActiveTimeframe(Timeframe timeframe) {
        boolean isDaily = timeframe == Timeframe.DAILY;
        boolean isWeekly = timeframe == Timeframe.WEEKLY;
        boolean isMonthly = timeframe == Timeframe.MONTHLY;

        applyButtonState(btnTimeframeDaily, isDaily);
        applyButtonState(btnTimeframeWeekly, isWeekly);
        applyButtonState(btnTimeframeMonthly, isMonthly);
    }

    private void applyButtonState(MaterialButton button, boolean isSelected) {
        int backgroundColor = isSelected ? 0xFF1F2628 : 0xFF101516;
        button.setBackgroundTintList(ColorStateList.valueOf(backgroundColor));
        button.setTextColor(isSelected ? 0xFF8FFF99 : 0xFF9AA7A1);
    }

    private void updateTimeframe(Timeframe timeframe) {
        if (tvTimeframeSubtitle != null) {
            switch (timeframe) {
                case DAILY:
                    tvTimeframeSubtitle.setText("Last 24 hours activity");
                    break;
                case WEEKLY:
                    tvTimeframeSubtitle.setText("Last 7 days activity");
                    break;
                case MONTHLY:
                default:
                    tvTimeframeSubtitle.setText("Last 4 weeks activity");
                    break;
            }
        }

        if (sessionManager.isMerchant()) {
            updateLineChart(timeframe);
        } else {
            updateBarChart(timeframe);
        }
    }

    private void updateBarChart(Timeframe timeframe) {
        List<BarEntry> entries = new ArrayList<>();
        String[] labels;

        switch (timeframe) {
            case DAILY:
                labels = new String[]{"00", "02", "04", "06", "08", "10", "12", "14", "16", "18", "20", "22"};
                float[] dailyValues = new float[]{3, 6, 4, 7, 5, 9, 4, 8, 6, 7, 5, 6};
                for (int i = 0; i < dailyValues.length; i++) {
                    entries.add(new BarEntry(i, dailyValues[i]));
                }
                break;
            case MONTHLY:
                labels = new String[]{"W1", "W2", "W3", "W4"};
                float[] monthlyValues = new float[]{42, 58, 46, 62};
                for (int i = 0; i < monthlyValues.length; i++) {
                    entries.add(new BarEntry(i, monthlyValues[i]));
                }
                break;
            case WEEKLY:
            default:
                labels = new String[]{"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};
                float[] weeklyValues = new float[]{8, 14, 10, 12, 16, 11, 9};
                for (int i = 0; i < weeklyValues.length; i++) {
                    entries.add(new BarEntry(i, weeklyValues[i]));
                }
                break;
        }

        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setLabelCount(labels.length, false);

        BarDataSet set = new BarDataSet(entries, "Spent");
        set.setColor(0xFF5CFF76);
        set.setDrawValues(false);
        set.setHighLightAlpha(0);

        BarData barData = new BarData(set);
        barData.setBarWidth(0.55f);
        barChart.setData(barData);
        barChart.invalidate();
    }

    private void updateLineChart(Timeframe timeframe) {
        List<Entry> entries = new ArrayList<>();
        String[] labels;

        switch (timeframe) {
            case DAILY:
                labels = new String[]{"00", "02", "04", "06", "08", "10", "12", "14", "16", "18", "20", "22"};
                float[] dailyValues = new float[]{12, 18, 11, 16, 20, 24, 18, 26, 22, 25, 19, 21};
                for (int i = 0; i < dailyValues.length; i++) {
                    entries.add(new Entry(i, dailyValues[i]));
                }
                break;
            case MONTHLY:
                labels = new String[]{"W1", "W2", "W3", "W4"};
                float[] monthlyValues = new float[]{88, 102, 94, 116};
                for (int i = 0; i < monthlyValues.length; i++) {
                    entries.add(new Entry(i, monthlyValues[i]));
                }
                break;
            case WEEKLY:
            default:
                labels = new String[]{"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"};
                float[] weeklyValues = new float[]{14, 21, 19, 28, 25, 31, 29};
                for (int i = 0; i < weeklyValues.length; i++) {
                    entries.add(new Entry(i, weeklyValues[i]));
                }
                break;
        }

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setLabelCount(labels.length, false);

        LineDataSet dataSet = new LineDataSet(entries, "Received");
        dataSet.setColor(0xFF5CFF76);
        dataSet.setCircleColor(0xFF5CFF76);
        dataSet.setCircleRadius(3f);
        dataSet.setDrawValues(false);
        dataSet.setDrawFilled(true);
        dataSet.setFillAlpha(45);
        dataSet.setFillColor(0xFF5CFF76);
        dataSet.setLineWidth(2f);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        lineChart.setData(new LineData(dataSet));
        lineChart.invalidate();
    }

    private void handleScrollRequest() {
        boolean shouldScroll = getArguments() != null && getArguments().getBoolean(ARG_SCROLL_TO_NODES, false);
        if (!shouldScroll || analyticsScroll == null || tvNodesTitle == null) {
            return;
        }

        analyticsScroll.post(() -> analyticsScroll.smoothScrollTo(0, tvNodesTitle.getTop()));
    }

    private void addNodeItem(String name, String amount, String ratio, int colorRes) {
        LinearLayout item = new LinearLayout(requireContext());
        item.setOrientation(LinearLayout.HORIZONTAL);
        item.setPadding(16, 16, 16, 16);
        item.setBackgroundColor(0xFF101516);
        LinearLayout.LayoutParams itemParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        itemParams.bottomMargin = 10;
        item.setLayoutParams(itemParams);

        View dot = new View(requireContext());
        LinearLayout.LayoutParams dotParams = new LinearLayout.LayoutParams(50, 50);
        dot.setLayoutParams(dotParams);
        dot.setBackgroundResource(R.drawable.bg_legend_dot);
        dot.getBackground().setTint(ContextCompat.getColor(requireContext(), colorRes));

        TextView title = new TextView(requireContext());
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        titleParams.setMarginStart(12);
        title.setLayoutParams(titleParams);
        title.setText(name + "\n" + amount);
        title.setTextColor(0xFFEAF3EE);
        title.setTextSize(13);

        TextView badge = new TextView(requireContext());
        badge.setText(ratio);
        badge.setTextColor(0xFFEAF3EE);
        badge.setTextSize(11);
        badge.setBackgroundColor(0xFF121A18);
        badge.setPadding(10, 6, 10, 6);

        item.addView(dot);
        item.addView(title);
        item.addView(badge);
        topMerchantsContainer.addView(item);
    }
}
