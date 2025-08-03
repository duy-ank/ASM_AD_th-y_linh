package com.example.giaodien.database;

import com.example.giaodien.Expense;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpenseDAO {
    private final DatabaseHelper dbHelper;

    public ExpenseDAO(DatabaseHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    // Lấy tổng chi tiêu hàng ngày trong tháng hiện tại
    public Map<Integer, Double> getDailyExpensesForMonth(long userId) {
        // Lấy tất cả chi tiêu của người dùng từ DatabaseHelper
        List<Expense> allExpenses = dbHelper.getExpensesByUser(userId);

        Map<Integer, Double> dailyExpenses = new HashMap<>();

        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);

        // Lặp qua danh sách chi tiêu và tổng hợp theo ngày
        for (Expense expense : allExpenses) {
            calendar.setTimeInMillis(expense.getTimestamp() * 1000L); // timestamp là giây, cần * 1000 để thành mili giây
            int expenseMonth = calendar.get(Calendar.MONTH);
            int expenseYear = calendar.get(Calendar.YEAR);

            // Chỉ xử lý chi tiêu trong tháng hiện tại
            if (expenseMonth == currentMonth && expenseYear == currentYear) {
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                double currentTotal = dailyExpenses.getOrDefault(dayOfMonth, 0.0);
                dailyExpenses.put(dayOfMonth, currentTotal + expense.getAmount());
            }
        }
        return dailyExpenses;
    }
}