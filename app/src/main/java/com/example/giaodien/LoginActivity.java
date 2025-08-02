package com.example.giaodien;



import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.giaodien.database.UserDAO;
import com.example.giaodien.model.User;

public class LoginActivity extends AppCompatActivity {
    private EditText emailEditText, passwordEditText;
    private UserDAO userDAO;
    private static final String PREFS_NAME = "LoginPrefs";
    private static final String KEY_EMAIL = "email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userDAO = new UserDAO(this);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.loginButton);
        TextView registerTextView = findViewById(R.id.signUpText);

        // Check if already logged in
        if (isLoggedIn()) {
            navigateToMainActivity(getStoredEmail());
            return;
        }

        // Sửa lại lambda expression
        loginButton.setOnClickListener(v -> attemptLogin());

        registerTextView.setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class))
        );
    }

    private void attemptLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (validateInput(email, password)) {
            User user = userDAO.loginUser(email, password);
            if (user != null) {
                saveLoginState(email);
                navigateToMainActivity(email);
            } else {
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isLoggedIn() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.contains(KEY_EMAIL);
    }

    private String getStoredEmail() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getString(KEY_EMAIL, "");
    }

    private boolean validateInput(String email, String password) {
        boolean isValid = true;

        if (email.isEmpty()) {
            emailEditText.setError("Please enter email");
            isValid = false;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("Please enter password");
            isValid = false;
        }

        return isValid;
    }

    private void saveLoginState(String email) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putString(KEY_EMAIL, email).apply();
    }

    private void navigateToMainActivity(String email) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(KEY_EMAIL, email);
        startActivity(intent);
        finish();
    }
}