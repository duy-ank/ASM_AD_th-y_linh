package com.example.giaodien;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.giaodien.database.DatabaseHelper;

public class UserInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_user_info); // dùng lại layout dialog_user_info
        setContentView(R.layout.activity_user_info); // dùng lại layout dialog_user_info

        ImageView imgAvatar = findViewById(R.id.imgAvatar);
        TextView tvName = findViewById(R.id.tvName);
        TextView tvEmail = findViewById(R.id.tvEmail);
        Button btnLogout = findViewById(R.id.btnLogout);

        DatabaseHelper dbHelper = new DatabaseHelper(this);

        // Lấy email từ SharedPreferences (hoặc sửa thành lấy từ Intent nếu bạn dùng Intent để truyền email)
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String username = preferences.getString("username", "");

        Cursor cursor = dbHelper.getUserInfoByEmail(username);
        if (cursor != null && cursor.moveToFirst()) {
            String name  = cursor.getString(cursor.getColumnIndexOrThrow("fullname"));
            String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));

            tvName.setText(name);
            tvEmail.setText(email);

            cursor.close();
        } else {
            tvName.setText("Không tìm thấy thông tin");
            tvEmail.setText("Email không xác định");
        }

        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(UserInfoActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}
