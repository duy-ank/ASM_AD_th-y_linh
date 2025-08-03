package com.example.giaodien;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.giaodien.adapter.ExpenseHistoryAdapter;
import com.example.giaodien.database.DatabaseHelper;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class ExpenseHistoryActivity extends AppCompatActivity implements ExpenseHistoryAdapter.OnItemLongClickListener {

    private RecyclerView rvExpenseHistory;
    private MaterialButton btnFilterDate, btnFilterCategory;
    private DatabaseHelper dbHelper;
    private ExpenseHistoryAdapter adapter;
    private int itemPositionToEdit = -1;

    private final ActivityResultLauncher<Intent> editExpenseLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && itemPositionToEdit != -1) {
                    refreshRecyclerView();
                    Toast.makeText(this, "Đã cập nhật giao dịch", Toast.LENGTH_SHORT).show();
                }
                itemPositionToEdit = -1;
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_history);

        rvExpenseHistory = findViewById(R.id.rvExpenseHistory);
        btnFilterDate = findViewById(R.id.btnFilterDate);
        btnFilterCategory = findViewById(R.id.btnFilterCategory);

        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dbHelper = new DatabaseHelper(this);
        setupRecyclerView();

        btnFilterDate.setOnClickListener(v -> filterExpensesByDate());
        btnFilterCategory.setOnClickListener(v -> filterExpensesByCategory());
    }

    private void setupRecyclerView() {
        List<Expense> expenses = dbHelper.getAllExpenses();
        adapter = new ExpenseHistoryAdapter(expenses, this);
        rvExpenseHistory.setLayoutManager(new LinearLayoutManager(this));
        rvExpenseHistory.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        rvExpenseHistory.setAdapter(adapter);
    }

    @Override
    public void onItemLongClick(Expense expense, int position) {
        showActionDialog(expense, position);
    }

    private void showActionDialog(final Expense expense, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Chọn hành động")
                .setItems(new CharSequence[]{"Sửa", "Xóa"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            itemPositionToEdit = position;
                            editExpense(expense);
                        } else {
                            deleteExpense(expense, position);
                        }
                    }
                });
        builder.create().show();
    }

    private void deleteExpense(Expense expense, int position) {
        int deletedRows = dbHelper.deleteExpense(expense.getId());
        if (deletedRows > 0) {
            adapter.removeExpense(position);
            Toast.makeText(this, "Đã xóa giao dịch", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Xóa giao dịch thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    private void editExpense(Expense expense) {
        Intent intent = new Intent(ExpenseHistoryActivity.this, AddExpenseActivity.class);
        intent.putExtra("expense_to_edit", expense);
        editExpenseLauncher.launch(intent);
    }

    private void refreshRecyclerView() {
        List<Expense> expenses = dbHelper.getAllExpenses();
        adapter.setExpenses(expenses);
    }

    private void filterExpensesByDate() {
        Toast.makeText(this, "Lọc theo ngày", Toast.LENGTH_SHORT).show();
    }

    private void filterExpensesByCategory() {
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