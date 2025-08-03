package com.example.giaodien;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.giaodien.database.DatabaseHelper;

public class AccountActivity extends AppCompatActivity {

    private TextView tvName, tvEmail, tvUserCode;
    private LinearLayout btnLogout, btnClearData, btnDeleteAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvUserCode = findViewById(R.id.tvUserCode);
        btnLogout = findViewById(R.id.btnLogout);
        btnClearData = findViewById(R.id.btnClearData);
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);

        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String userEmail = preferences.getString("email", "");

        DatabaseHelper dbHelper = new DatabaseHelper(this);

        if (userEmail.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy phiên đăng nhập.", Toast.LENGTH_SHORT).show();
            // Có thể chuyển hướng về LoginActivity nếu cần
            return;
        }

        Cursor cursor = dbHelper.getUserInfoByEmail(userEmail);

        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_FULL_NAME));
            String email = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_EMAIL));
            tvName.setText(name);
            tvEmail.setText(email);
            cursor.close();
        } else {
            tvName.setText("Không tìm thấy thông tin");
            tvEmail.setText("Email không xác định");
        }

        btnLogout.setOnClickListener(v -> {
            preferences.edit().clear().apply();
            Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        btnClearData.setOnClickListener(v -> {
            Toast.makeText(this, "Đã xoá dữ liệu và làm mới", Toast.LENGTH_SHORT).show();
            recreate();
        });

        btnDeleteAccount.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Xác nhận xoá tài khoản")
                    .setMessage("Bạn có chắc muốn xoá tài khoản và ngừng sử dụng?")
                    .setPositiveButton("Xoá", (dialogInterface, i) -> {
                        dbHelper.deleteUserByEmail(userEmail); // Gọi phương thức xóa tài khoản
                        preferences.edit().clear().apply();
                        Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Huỷ", null)
                    .show();
        });
    }
}