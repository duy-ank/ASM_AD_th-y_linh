package com.example.giaodien;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.giaodien.database.DatabaseHelper;
import com.example.giaodien.Expense;

import java.text.NumberFormat;
import java.util.Locale;

public class AddExpenseActivity extends AppCompatActivity {

    private EditText etAmount, etDescription;
    private Spinner spCategory, spPaymentMethod;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        initializeViews();
        setupSpinners();
        setupSaveButton();
    }

    private void initializeViews() {
        etAmount = findViewById(R.id.etAmount);
        etDescription = findViewById(R.id.etDescription);
        spCategory = findViewById(R.id.spCategory);
        spPaymentMethod = findViewById(R.id.spPaymentMethod);
        btnSave = findViewById(R.id.btnSave);

        // Focus vào trường số tiền khi mở activity
        etAmount.requestFocus();
    }

    private void setupSpinners() {
        // Thiết lập danh mục chi tiêu
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.expense_categories,
                android.R.layout.simple_spinner_item
        );
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(categoryAdapter);

        // Thiết lập phương thức thanh toán
        ArrayAdapter<CharSequence> paymentAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.payment_methods,
                android.R.layout.simple_spinner_item
        );
        paymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPaymentMethod.setAdapter(paymentAdapter);
    }

    private void setupSaveButton() {
        btnSave.setOnClickListener(v -> {
            if (validateInput()) {
                saveExpense();
            }
        });
    }

    private boolean validateInput() {
        String amountStr = etAmount.getText().toString().trim();
        String description = etDescription.getText().toString().trim();

        if (amountStr.isEmpty()) {
            showError(etAmount, "Vui lòng nhập số tiền");
            return false;
        }

        try {
            double amount = parseAmount(amountStr);
            if (amount <= 0) {
                showError(etAmount, "Số tiền phải lớn hơn 0");
                return false;
            }
        } catch (NumberFormatException e) {
            showError(etAmount, "Số tiền không hợp lệ");
            return false;
        }

        if (description.isEmpty()) {
            showError(etDescription, "Vui lòng nhập mô tả");
            return false;
        }

        return true;
    }

    private void showError(EditText editText, String message) {
        editText.setError(message);
        editText.requestFocus();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private double parseAmount(String amountStr) throws NumberFormatException {
        // Xử lý chuỗi số có chứa dấu phân cách
        String cleanString = amountStr.replaceAll("[.,\\s]", "");
        return Double.parseDouble(cleanString);
    }

    private void saveExpense() {
        try {
            double amount = parseAmount(etAmount.getText().toString().trim());
            String category = spCategory.getSelectedItem().toString();
            String paymentMethod = spPaymentMethod.getSelectedItem().toString();
            String description = etDescription.getText().toString().trim();

            Expense expense = new Expense(
                    amount,
                    category,
                    paymentMethod,
                    description,
                    System.currentTimeMillis()
            );

            DatabaseHelper dbHelper = new DatabaseHelper(this);
            long result = dbHelper.addExpense(expense);

            if (result != -1) {
                showSuccessMessage(amount);

                // Trả về kết quả thành công và dữ liệu expense
                Intent resultIntent = new Intent();
                resultIntent.putExtra("new_expense", expense);
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(this, "Lỗi khi lưu chi tiêu", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            showError(etAmount, "Số tiền không hợp lệ");
        }
    }

    private void showSuccessMessage(double amount) {
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.getDefault());
        String formattedAmount = formatter.format(amount);

        Toast.makeText(this,
                "Đã lưu chi tiêu: " + formattedAmount + "đ",
                Toast.LENGTH_LONG).show();
    }
}