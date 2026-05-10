package com.scanpay.app.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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
import com.scanpay.app.R;
import com.scanpay.app.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class AnalyticsFragment extends Fragment {

    private TextView tvHeading;
    private TextView tvSubheading;
    private TextView tvGrowthCopy;
    private TextView tvNodesTitle;
    private BarChart barChart;
    private LineChart lineChart;
    private LinearLayout topMerchantsContainer;
    private SessionManager sessionManager;

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
        loadMockData();
    }

    private void initViews(View view) {
        tvHeading = view.findViewById(R.id.tv_heading);
        tvSubheading = view.findViewById(R.id.tv_subheading);
        tvGrowthCopy = view.findViewById(R.id.tv_growth_copy);
        tvNodesTitle = view.findViewById(R.id.tv_nodes_title);
        barChart = view.findViewById(R.id.bar_chart);
        lineChart = view.findViewById(R.id.line_chart);
        topMerchantsContainer = view.findViewById(R.id.top_merchants_container);
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
        xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]{"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"}));
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
        xAxis.setValueFormatter(new IndexAxisValueFormatter(new String[]{"MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN"}));
        xAxis.setGranularity(1f);

        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setGridColor(0x332F3B37);
        leftAxis.setTextColor(0xFF8FA49B);
        lineChart.getAxisRight().setEnabled(false);
    }

    private void loadMockData() {
        topMerchantsContainer.removeAllViews();
        if (sessionManager.isMerchant()) {
            List<Entry> entries = new ArrayList<>();
            entries.add(new Entry(0, 14));
            entries.add(new Entry(1, 21));
            entries.add(new Entry(2, 19));
            entries.add(new Entry(3, 28));
            entries.add(new Entry(4, 25));
            entries.add(new Entry(5, 31));
            entries.add(new Entry(6, 29));

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
        } else {
            List<BarEntry> entries = new ArrayList<>();
            entries.add(new BarEntry(0, 8));
            entries.add(new BarEntry(1, 14));
            entries.add(new BarEntry(2, 10));
            entries.add(new BarEntry(3, 12));
            entries.add(new BarEntry(4, 16));
            entries.add(new BarEntry(5, 11));
            entries.add(new BarEntry(6, 9));

            BarDataSet set = new BarDataSet(entries, "Spent");
            set.setColor(0xFF5CFF76);
            set.setDrawValues(false);
            set.setHighLightAlpha(0);
            BarData barData = new BarData(set);
            barData.setBarWidth(0.55f);
            barChart.setData(barData);
            barChart.invalidate();
        }

        addNodeItem("Cloud Scale AI", "$2,450.00", "75%", R.color.transactionIconGreen);
        addNodeItem("Nexus Office", "$1,120.00", "42%", R.color.accentCyan);
        addNodeItem("BioFuel Grid", "$490.50", "18%", R.color.accentGreen);
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
