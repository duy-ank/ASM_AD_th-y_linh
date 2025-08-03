package com.example.giaodien;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class Expense implements Serializable {
    private String id;
    private double amount;
    private String category;
    private String paymentMethod;
    private String description;
    private long timestamp;
    private String userId;  // Thêm trường userId để liên kết với người dùng

    // Constructor đầy đủ (5 tham số)
    public Expense(double amount, String category, String paymentMethod,
                   String description, long timestamp) {
        this.id = generateId();
        this.amount = amount;
        this.category = category;
        this.paymentMethod = paymentMethod;
        this.description = description;
        this.timestamp = timestamp;
    }

    // Constructor 4 tham số (tự động thêm timestamp hiện tại)
    public Expense(double amount, String category,
                   String paymentMethod, String description) {
        this(amount, category, paymentMethod, description, System.currentTimeMillis());
    }

    // Constructor 3 tham số (không có description)
    public Expense(double amount, String category, String paymentMethod) {
        this(amount, category, paymentMethod, "", System.currentTimeMillis());
    }

    // Phương thức generate ID sử dụng UUID
    private String generateId() {
        return "EXP-" + UUID.randomUUID().toString();
    }

    // Getter methods
    public String getId() { return id; }
    public double getAmount() { return amount; }
    public String getCategory() { return category; }
    public String getPaymentMethod() { return paymentMethod; }
    public String getDescription() { return description; }
    public long getTimestamp() { return timestamp; }
    public String getUserId() { return userId; }

    // Setter methods
    public void setAmount(double amount) { this.amount = amount; }
    public void setCategory(String category) { this.category = category; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setDescription(String description) { this.description = description; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public void setUserId(String userId) { this.userId = userId; }

    // Phương thức tiện ích
    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    public String getFormattedAmount() {
        return String.format(Locale.getDefault(), "%,.0fđ", amount);
    }

    public String getShortDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    @Override
    public String toString() {
        return "Expense{" +
                "id='" + id + '\'' +
                ", amount=" + amount +
                ", category='" + category + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", timestamp=" + getFormattedDate() +
                ", userId='" + userId + '\'' +
                '}';
    }
}