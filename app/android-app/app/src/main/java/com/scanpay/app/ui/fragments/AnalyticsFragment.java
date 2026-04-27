package com.scanpay.app.ui.fragments;

import android.graphics.Color;
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

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.scanpay.app.R;

import java.util.ArrayList;
import java.util.List;

public class AnalyticsFragment extends Fragment {

    private TextView tvMonthlySpending;
    private PieChart pieChart;
    private LinearLayout topMerchantsContainer;
    private LinearLayout frequentPaymentsContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_analytics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupPieChart();
        loadMockData();
    }

    private void initViews(View view) {
        tvMonthlySpending = view.findViewById(R.id.tv_monthly_spending);
        pieChart = view.findViewById(R.id.pie_chart);
        topMerchantsContainer = view.findViewById(R.id.top_merchants_container);
        frequentPaymentsContainer = view.findViewById(R.id.frequent_payments_container);
    }

    private void setupPieChart() {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(ContextCompat.getColor(requireContext(), R.color.backgroundColor));
        pieChart.setTransparentCircleColor(Color.WHITE);
        pieChart.setTransparentCircleAlpha(110);
        pieChart.setHoleRadius(50f);
        pieChart.setTransparentCircleRadius(55f);
        pieChart.setDrawCenterText(false);
        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);
        pieChart.getLegend().setEnabled(false);
    }

    private void loadMockData() {
        // Monthly spending
        tvMonthlySpending.setText("14,200");

        // Pie chart data
        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(45f, "Shopping"));
        entries.add(new PieEntry(25f, "Food"));
        entries.add(new PieEntry(20f, "Transport"));
        entries.add(new PieEntry(10f, "Bills"));

        int[] colors = {
                ContextCompat.getColor(requireContext(), R.color.pieColor1),
                ContextCompat.getColor(requireContext(), R.color.pieColor2),
                ContextCompat.getColor(requireContext(), R.color.pieColor3),
                ContextCompat.getColor(requireContext(), R.color.pieColor5)
        };

        PieDataSet dataSet = new PieDataSet(entries, "Spending");
        dataSet.setColors(colors);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.WHITE);

        PieData data = new PieData(dataSet);
        pieChart.setData(data);
        pieChart.invalidate();

        // Top Merchants
        addMerchantItem(topMerchantsContainer, "SuperMart", R.color.transactionIconGreen);
        addMerchantItem(topMerchantsContainer, "ElectroHub", R.color.transactionIconBlue);
        addMerchantItem(topMerchantsContainer, "Café Java", R.color.transactionIconOrange);

        // Frequent Payments
        addPaymentItem(frequentPaymentsContainer, "Anna", R.color.transactionIconGreen);
        addPaymentItem(frequentPaymentsContainer, "Samuel", R.color.accentGreen);
        addPaymentItem(frequentPaymentsContainer, "Rent Payment", R.color.transactionIconBlue);
    }

    private void addMerchantItem(LinearLayout container, String name, int colorRes) {
        LinearLayout item = new LinearLayout(requireContext());
        item.setOrientation(LinearLayout.HORIZONTAL);
        item.setPadding(0, 8, 0, 8);

        View dot = new View(requireContext());
        LinearLayout.LayoutParams dotParams = new LinearLayout.LayoutParams(24, 24);
        dot.setLayoutParams(dotParams);
        dot.setBackgroundResource(R.drawable.bg_legend_dot);
        dot.getBackground().setTint(ContextCompat.getColor(requireContext(), colorRes));

        TextView textView = new TextView(requireContext());
        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        textParams.setMarginStart(16);
        textView.setLayoutParams(textParams);
        textView.setText(name);
        textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.textPrimary));
        textView.setTextSize(14);

        item.addView(dot);
        item.addView(textView);
        container.addView(item);
    }

    private void addPaymentItem(LinearLayout container, String name, int colorRes) {
        addMerchantItem(container, name, colorRes);
    }
}

