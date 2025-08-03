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
        setContentView(R.layout.activity_user_info); // hoặc R.layout.dialog_user_info nếu chưa đổi tên

        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvUserCode = findViewById(R.id.tvUserCode);
        btnLogout = findViewById(R.id.btnLogout);
        btnClearData = findViewById(R.id.btnClearData);
        btnDeleteAccount = findViewById(R.id.btnDeleteAccount);

        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String username = preferences.getString("username", "");

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        Cursor cursor = dbHelper.getUserInfoByEmail(username);

        if (cursor != null && cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("fullname"));
            String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));
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
                        preferences.edit().clear().apply();
                        dbHelper.deleteUserByEmail(username);
                        Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    })
                    .setNegativeButton("Huỷ", null)
                    .show();
        });
    }
}
