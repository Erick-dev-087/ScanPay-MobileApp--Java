package com.scanpay.app.data.response;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AnalyticsResponse {
    @SerializedName("success")
    private boolean success;
    @SerializedName("total_spent")
    private double totalSpent;
    @SerializedName("total_revenue")
    private double totalRevenue;
    @SerializedName("today_revenue")
    private double todayRevenue;
    @SerializedName("week_revenue")
    private double weekRevenue;
    @SerializedName("month_revenue")
    private double monthRevenue;
    @SerializedName("inflow")
    private double inflow;
    @SerializedName("outflow")
    private double outflow;
    @SerializedName("spending_breakdown")
    private List<CategorySpending> spendingBreakdown;
    @SerializedName("top_merchants")
    private List<TopMerchant> topMerchants;
    @SerializedName("frequent_payments")
    private List<FrequentPayment> frequentPayments;
    @SerializedName("daily_transactions")
    private List<DailyTransaction> dailyTransactions;

    // Getters and Setters
    public boolean isSuccess() { return success; }
    public double getTotalSpent() { return totalSpent; }
    public double getTotalRevenue() { return totalRevenue; }
    public double getTodayRevenue() { return todayRevenue; }
    public double getWeekRevenue() { return weekRevenue; }
    public double getMonthRevenue() { return monthRevenue; }
    public double getInflow() { return inflow; }
    public double getOutflow() { return outflow; }
    public List<CategorySpending> getSpendingBreakdown() { return spendingBreakdown; }
    public List<TopMerchant> getTopMerchants() { return topMerchants; }
    public List<FrequentPayment> getFrequentPayments() { return frequentPayments; }
    public List<DailyTransaction> getDailyTransactions() { return dailyTransactions; }

    // Inner classes
    public static class CategorySpending {
        @SerializedName("category")
        private String category;
        @SerializedName("amount")
        private double amount;
        @SerializedName("percentage")
        private float percentage;

        public String getCategory() { return category; }
        public double getAmount() { return amount; }
        public float getPercentage() { return percentage; }
    }

    public static class TopMerchant {
        @SerializedName("name")
        private String name;
        @SerializedName("total_amount")
        private double totalAmount;
        @SerializedName("color")
        private String color;

        public String getName() { return name; }
        public double getTotalAmount() { return totalAmount; }
        public String getColor() { return color; }
    }

    public static class FrequentPayment {
        @SerializedName("name")
        private String name;
        @SerializedName("count")
        private int count;
        @SerializedName("color")
        private String color;

        public String getName() { return name; }
        public int getCount() { return count; }
        public String getColor() { return color; }
    }

    public static class DailyTransaction {
        @SerializedName("day")
        private String day;
        @SerializedName("amount")
        private double amount;

        public String getDay() { return day; }
        public double getAmount() { return amount; }
    }
}

