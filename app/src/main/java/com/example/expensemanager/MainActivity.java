package com.example.expensemanager;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.*;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    DBHelper dbHelper;
    ListView listExpenses;
    TextView tvTotal;
    Button btnAddExpense;
    ArrayAdapter<String> adapter;
    ArrayList<String> expenseList;
    ArrayList<Integer> expenseIds; // Lưu ID để sửa/xóa

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this);
        listExpenses = findViewById(R.id.listExpenses);
        tvTotal = findViewById(R.id.tvTotal);
        btnAddExpense = findViewById(R.id.btnAddExpense);

        expenseList = new ArrayList<>();
        expenseIds = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, expenseList);
        listExpenses.setAdapter(adapter);

        btnAddExpense.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
            startActivity(intent);
        });

        // Nhấn giữ để sửa/xóa
        listExpenses.setOnItemLongClickListener((parent, view, position, id) -> {
            int expenseId = expenseIds.get(position);

            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Tùy chọn")
                    .setItems(new CharSequence[]{"Sửa", "Xóa"}, (dialog, which) -> {
                        if (which == 0) {
                            Intent intent = new Intent(MainActivity.this, AddExpenseActivity.class);
                            intent.putExtra("mode", "edit");
                            intent.putExtra("id", expenseId);
                            startActivity(intent);
                        } else if (which == 1) {
                            dbHelper.deleteExpense(expenseId);
                            loadExpenses();
                            Toast.makeText(MainActivity.this, "Đã xóa", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .show();
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadExpenses();
    }

    private void loadExpenses() {
        expenseList.clear();
        expenseIds.clear();
        Cursor cursor = dbHelper.getAllExpenses();
        double total = 0;

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));
                String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
                String note = cursor.getString(cursor.getColumnIndexOrThrow("note"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("date"));

                total += amount;
                expenseIds.add(id);
                expenseList.add(amount + " đ - " + category + " (" + date + ") " + note);
            } while (cursor.moveToNext());
        }
        cursor.close();

        adapter.notifyDataSetChanged();
        tvTotal.setText("Tổng chi: " + total + " đ");
    }
}
