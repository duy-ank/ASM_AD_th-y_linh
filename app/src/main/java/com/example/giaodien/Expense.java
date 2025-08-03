package com.example.giaodien;

import java.io.Serializable;
import java.util.Date;
import java.util.Locale;
import java.text.SimpleDateFormat;
import java.text.NumberFormat;

public class Expense implements Serializable {
    private long id; // Đã đổi sang kiểu long
    private double amount;
    private String category;
    private String paymentMethod;
    private String description;
    private long timestamp;
    private long userId; // Đã đổi sang kiểu long

    // Constructor khi lấy dữ liệu từ database (có ID và userId)
    public Expense(long id, double amount, String category, String paymentMethod, String description, long timestamp, long userId) {
        this.id = id;
        this.amount = amount;
        this.category = category;
        this.paymentMethod = paymentMethod;
        this.description = description;
        this.timestamp = timestamp;
        this.userId = userId;
    }

    // Constructor khi thêm giao dịch mới (chưa có ID)
    public Expense(double amount, String category, String paymentMethod, String description, long timestamp, long userId) {
        this.amount = amount;
        this.category = category;
        this.paymentMethod = paymentMethod;
        this.description = description;
        this.timestamp = timestamp;
        this.userId = userId;
    }

    // --- Các phương thức Getter và Setter ---

    // Getter và Setter cho id
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    // Getter và Setter cho amount
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    // Getter và Setter cho category
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    // Getter và Setter cho paymentMethod
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    // Getter và Setter cho description
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    // Getter và Setter cho timestamp
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    // Getter và Setter cho userId
    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    // --- Các phương thức tiện ích (giữ nguyên) ---

    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp * 1000L)); // Chú ý * 1000L
    }

    public String getFormattedAmount() {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return currencyFormat.format(amount);
    }
}