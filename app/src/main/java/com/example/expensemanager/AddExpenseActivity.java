package com.example.expensemanager;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Calendar;

public class AddExpenseActivity extends AppCompatActivity {

    EditText edtAmount, edtNote;
    Button btnPickDate, btnQuick10000, btnQuick20000, btnQuick50000, btnQuick100000, btnQuick200000;
    GridView gridCategories;
    TextView btnCancel, btnDone;
    ImageButton btnAddImage;
    Spinner spinnerPayment;

    String selectedCategory = "";
    String selectedDate = "";
    String selectedPaymentMethod = "Tiền mặt";
    String selectedImagePath = null;

    int expenseId = -1;
    boolean isEditMode = false;

    DBHelper dbHelper;
    ArrayList<CategoryItem> categories = new ArrayList<>();
    CategoryAdapter categoryAdapter;

    private static final int PICK_IMAGE_REQUEST = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        dbHelper = new DBHelper(this);

        // Ánh xạ view
        edtAmount = findViewById(R.id.edtAmount);
        edtNote = findViewById(R.id.edtNote);
        btnPickDate = findViewById(R.id.btnPickDate);
        btnQuick10000 = findViewById(R.id.btnQuick10000);
        btnQuick20000 = findViewById(R.id.btnQuick20000);
        btnQuick50000 = findViewById(R.id.btnQuick50000);
        btnQuick100000 = findViewById(R.id.btnQuick100000);
        btnQuick200000 = findViewById(R.id.btnQuick200000);
        gridCategories = findViewById(R.id.gridCategories);
        btnCancel = findViewById(R.id.btnCancel);
        btnDone = findViewById(R.id.btnDone);
        btnAddImage = findViewById(R.id.btnAddImage);
        spinnerPayment = findViewById(R.id.spinnerPayment);

        // Kiểm tra chế độ sửa
        if (getIntent().hasExtra("mode") && getIntent().getStringExtra("mode").equals("edit")) {
            isEditMode = true;
            expenseId = getIntent().getIntExtra("id", -1);
            loadExpenseData(expenseId);
        }

        // Set dữ liệu spinner phương thức thanh toán
        String[] paymentMethods = {"Tiền mặt", "Chuyển khoản", "Thẻ tín dụng"};
        ArrayAdapter<String> paymentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, paymentMethods);
        paymentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPayment.setAdapter(paymentAdapter);

        spinnerPayment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                selectedPaymentMethod = paymentMethods[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Danh mục
        categories.add(new CategoryItem("Ăn tiệm", R.drawable.ic_food));
        categories.add(new CategoryItem("Sinh hoạt", R.drawable.ic_home));
        categories.add(new CategoryItem("Đi lại", R.drawable.ic_transport));
        categories.add(new CategoryItem("Trang phục", R.drawable.ic_clothes));
        categories.add(new CategoryItem("Hưởng thụ", R.drawable.ic_entertainment));
        categories.add(new CategoryItem("Con cái", R.drawable.ic_kid));
        categories.add(new CategoryItem("Hiếu hỉ", R.drawable.ic_gift));
        categories.add(new CategoryItem("Nhà cửa", R.drawable.ic_house));
        categories.add(new CategoryItem("Sức khoẻ", R.drawable.ic_health));
        categories.add(new CategoryItem("Bản thân", R.drawable.ic_self));
        categories.add(new CategoryItem("Khác", R.drawable.ic_other));

        categoryAdapter = new CategoryAdapter(this, categories);
        gridCategories.setAdapter(categoryAdapter);

        gridCategories.setOnItemClickListener((parent, view, position, id) -> {
            selectedCategory = categories.get(position).getName();
            categoryAdapter.setSelectedPosition(position);
        });

        // Chọn ngày
        btnPickDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
                        btnPickDate.setText(selectedDate);
                    },
                    c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        });

        // Nút số tiền nhanh
        btnQuick10000.setOnClickListener(v -> edtAmount.setText("10000"));
        btnQuick20000.setOnClickListener(v -> edtAmount.setText("20000"));
        btnQuick50000.setOnClickListener(v -> edtAmount.setText("50000"));
        btnQuick100000.setOnClickListener(v -> edtAmount.setText("100000"));
        btnQuick200000.setOnClickListener(v -> edtAmount.setText("200000"));

        // Nút hủy
        btnCancel.setOnClickListener(v -> finish());

        // Nút thêm ảnh
        btnAddImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        // Nút lưu
        btnDone.setOnClickListener(v -> {
            String amountStr = edtAmount.getText().toString();
            String note = edtNote.getText().toString();

            if (amountStr.isEmpty() || selectedCategory.isEmpty() || selectedDate.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Double.parseDouble(amountStr);
            boolean result;

            if (isEditMode) {
                result = dbHelper.updateExpense(expenseId, amount, selectedCategory, note, selectedDate, selectedPaymentMethod, selectedImagePath);
            } else {
                result = dbHelper.insertExpense(amount, selectedCategory, note, selectedDate, selectedPaymentMethod, selectedImagePath);
            }

            if (result) {
                Toast.makeText(this, isEditMode ? "Đã cập nhật" : "Đã lưu chi tiêu", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Lỗi lưu dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadExpenseData(int id) {
        Cursor cursor = dbHelper.getExpenseById(id);
        if (cursor.moveToFirst()) {
            edtAmount.setText(String.valueOf(cursor.getDouble(cursor.getColumnIndexOrThrow("amount"))));
            edtNote.setText(cursor.getString(cursor.getColumnIndexOrThrow("note")));
            selectedCategory = cursor.getString(cursor.getColumnIndexOrThrow("category"));
            selectedDate = cursor.getString(cursor.getColumnIndexOrThrow("date"));
            btnPickDate.setText(selectedDate);
            selectedPaymentMethod = cursor.getString(cursor.getColumnIndexOrThrow("payment_method"));
            selectedImagePath = cursor.getString(cursor.getColumnIndexOrThrow("image_path"));

            if (selectedImagePath != null) {
                btnAddImage.setImageURI(Uri.parse(selectedImagePath));
            }
        }
        cursor.close();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                selectedImagePath = selectedImageUri.toString();
                btnAddImage.setImageURI(selectedImageUri);
            }
        }
    }
}
