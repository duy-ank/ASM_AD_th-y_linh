package com.example.giaodien;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.giaodien.adapter.ExpenseHistoryAdapter;
import com.example.giaodien.database.DatabaseHelper;
import com.example.giaodien.Expense;
import com.google.android.material.button.MaterialButton;
import java.util.List;

public class ExpenseHistoryActivity extends AppCompatActivity {

    private RecyclerView rvExpenseHistory;
    private MaterialButton btnFilterDate, btnFilterCategory;
    private DatabaseHelper dbHelper;
    private ExpenseHistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_history);

        // Initialize views
        rvExpenseHistory = findViewById(R.id.rvExpenseHistory);
        btnFilterDate = findViewById(R.id.btnFilterDate);
        btnFilterCategory = findViewById(R.id.btnFilterCategory);

        // Setup toolbar
        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Setup RecyclerView
        setupRecyclerView();

        // Set click listeners
        btnFilterDate.setOnClickListener(v -> filterExpensesByDate());
        btnFilterCategory.setOnClickListener(v -> filterExpensesByCategory());
    }

    private void setupRecyclerView() {
        // Get all expenses from database
        List<Expense> expenses = dbHelper.getAllExpenses();

        // Create and set adapter
        adapter = new ExpenseHistoryAdapter(expenses);
        rvExpenseHistory.setLayoutManager(new LinearLayoutManager(this));
        rvExpenseHistory.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rvExpenseHistory.setAdapter(adapter);
    }

    private void filterExpensesByDate() {
        // Implement your date filtering logic here
        Toast.makeText(this, "Lọc theo ngày", Toast.LENGTH_SHORT).show();
    }

    private void filterExpensesByCategory() {
        // Implement your category filtering logic here
        Toast.makeText(this, "Lọc theo danh mục", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}