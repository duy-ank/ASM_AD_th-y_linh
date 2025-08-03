package com.example.giaodien;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.giaodien.database.DatabaseHelper;
import com.example.giaodien.Expense;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * AddExpenseActivity là màn hình để thêm hoặc sửa một khoản chi tiêu.
 * Nó xử lý giao diện nhập liệu, validation và tương tác với database.
 */
public class AddExpenseActivity extends AppCompatActivity {

    // Khai báo các thành phần UI
    private EditText etAmount, etDescription;
    private Spinner spCategory, spPaymentMethod;
    private Button btnSave;
    private TextView tvDate;
    private TextView tvTitle;

    // Khai báo các đối tượng và biến cần thiết
    private Calendar selectedDate; // Biến lưu trữ ngày đã chọn
    private DatabaseHelper dbHelper;
    private long currentUserId = -1;
    private Expense expenseToEdit; // Biến để lưu giao dịch cần sửa

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        // Khởi tạo DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Ánh xạ các thành phần UI
        initializeViews();
        setupSpinners();

        // Lấy userId từ SharedPreferences và kiểm tra
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String userEmail = preferences.getString("email", "");
        if (userEmail.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy người dùng. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Lấy userId từ database
        Cursor cursor = dbHelper.getUserInfoByEmail(userEmail);
        if (cursor != null && cursor.moveToFirst()) {
            currentUserId = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USER_ID));
            cursor.close();
        } else {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin người dùng trong DB.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Kiểm tra xem có dữ liệu sửa được truyền qua Intent không
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("expense_to_edit")) {
            expenseToEdit = (Expense) intent.getSerializableExtra("expense_to_edit");
            if (expenseToEdit != null) {
                loadExpenseData(expenseToEdit);
                tvTitle.setText("Sửa khoản chi");
                btnSave.setText("Cập nhật");
            }
        } else {
            // Chế độ thêm mới: thiết lập ngày hiện tại
            selectedDate = Calendar.getInstance();
            updateDateTextView();
        }

        // Thiết lập các chức năng khác
        setupDatePicker();
        setupSaveButton();
    }

    /**
     * Ánh xạ các thành phần UI từ layout XML và thiết lập focus.
     */
    private void initializeViews() {
        etAmount = findViewById(R.id.etAmount);
        etDescription = findViewById(R.id.etDescription);
        spCategory = findViewById(R.id.spCategory);
        spPaymentMethod = findViewById(R.id.spPaymentMethod);
        btnSave = findViewById(R.id.btnSave);
        tvDate = findViewById(R.id.tvDate);
        tvTitle = findViewById(R.id.tvTitle);

        etAmount.requestFocus();
    }

    /**
     * Tải dữ liệu từ đối tượng Expense (chế độ sửa) lên các trường UI.
     */
    private void loadExpenseData(Expense expense) {
        etAmount.setText(String.valueOf(expense.getAmount()));
        etDescription.setText(expense.getDescription());

        // Đặt ngày
        selectedDate = Calendar.getInstance();
        selectedDate.setTimeInMillis(expense.getTimestamp() * 1000L);
        updateDateTextView();

        // Đặt category
        ArrayAdapter<CharSequence> categoryAdapter = (ArrayAdapter<CharSequence>) spCategory.getAdapter();
        if (categoryAdapter != null) {
            int spinnerPosition = categoryAdapter.getPosition(expense.getCategory());
            spCategory.setSelection(spinnerPosition);
        }

        // Đặt payment method
        ArrayAdapter<CharSequence> paymentAdapter = (ArrayAdapter<CharSequence>) spPaymentMethod.getAdapter();
        if (paymentAdapter != null) {
            int spinnerPosition = paymentAdapter.getPosition(expense.getPaymentMethod());
            spPaymentMethod.setSelection(spinnerPosition);
        }
    }

    /**
     * Thiết lập dữ liệu cho các Spinner (dropdown list).
     */
    private void setupSpinners() {
        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.expense_categories,
                android.R.layout.simple_spinner_item
        );
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategory.setAdapter(categoryAdapter);

        ArrayAdapter<CharSequence> paymentAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.payment_methods,
                android.R.layout.simple_spinner_item
        );
        paymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPaymentMethod.setAdapter(paymentAdapter);
    }

    /**
     * Thiết lập chức năng chọn ngày bằng DatePickerDialog.
     */
    private void setupDatePicker() {
        updateDateTextView();

        tvDate.setOnClickListener(v -> {
            int year = selectedDate.get(Calendar.YEAR);
            int month = selectedDate.get(Calendar.MONTH);
            int day = selectedDate.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        selectedDate.set(selectedYear, selectedMonth, selectedDay);
                        updateDateTextView();
                    }, year, month, day);
            datePickerDialog.show();
        });
    }

    /**
     * Cập nhật TextView hiển thị ngày đã chọn.
     */
    private void updateDateTextView() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        tvDate.setText(sdf.format(selectedDate.getTime()));
    }

    /**
     * Thiết lập sự kiện click cho nút "Lưu/Cập nhật".
     */
    private void setupSaveButton() {
        btnSave.setOnClickListener(v -> {
            if (validateInput() && currentUserId != -1) {
                saveExpense();
            } else if (currentUserId == -1) {
                Toast.makeText(this, "Không tìm thấy người dùng. Vui lòng đăng nhập lại.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Kiểm tra tính hợp lệ của dữ liệu đầu vào.
     */
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

    /**
     * Phương thức trợ giúp để hiển thị lỗi.
     */
    private void showError(EditText editText, String message) {
        editText.setError(message);
        editText.requestFocus();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Chuyển đổi chuỗi số có định dạng địa phương thành double.
     */
    private double parseAmount(String amountStr) throws NumberFormatException {
        String cleanString = amountStr.replaceAll("[.,\\s]", "");
        return Double.parseDouble(cleanString);
    }

    /**
     * Lấy dữ liệu từ UI và lưu/cập nhật vào database.
     */
    private void saveExpense() {
        try {
            double amount = parseAmount(etAmount.getText().toString().trim());
            String category = spCategory.getSelectedItem().toString();
            String paymentMethod = spPaymentMethod.getSelectedItem().toString();
            String description = etDescription.getText().toString().trim();

            long timestamp = selectedDate.getTimeInMillis() / 1000L;

            long result = -1;

            if (expenseToEdit != null) {
                // Chế độ sửa: Cập nhật đối tượng đã có
                expenseToEdit.setAmount(amount);
                expenseToEdit.setCategory(category);
                expenseToEdit.setPaymentMethod(paymentMethod);
                expenseToEdit.setDescription(description);
                expenseToEdit.setTimestamp(timestamp);
                result = dbHelper.updateExpense(expenseToEdit);
            } else {
                // Chế độ thêm mới: Tạo đối tượng mới
                Expense newExpense = new Expense(
                        amount,
                        category,
                        paymentMethod,
                        description,
                        timestamp,
                        currentUserId
                );
                result = dbHelper.addExpense(newExpense);
            }

            if (result != -1) {
                showSuccessMessage(amount);
                setResult(RESULT_OK); // Gửi kết quả thành công về màn hình trước
                finish();
            } else {
                Toast.makeText(this, "Lỗi khi lưu chi tiêu", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            showError(etAmount, "Số tiền không hợp lệ");
        }
    }

    /**
     * Hiển thị thông báo thành công sau khi lưu.
     */
    private void showSuccessMessage(double amount) {
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.getDefault());
        String formattedAmount = formatter.format(amount);

        Toast.makeText(this,
                "Đã lưu chi tiêu: " + formattedAmount + "đ",
                Toast.LENGTH_LONG).show();
    }
}