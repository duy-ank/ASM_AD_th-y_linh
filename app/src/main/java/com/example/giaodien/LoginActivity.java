package com.example.giaodien;

import android.content.Intent;
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

    public static final String KEY_EMAIL = "email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userDAO = new UserDAO(this);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        Button loginButton = findViewById(R.id.loginButton);
        TextView registerTextView = findViewById(R.id.signUpText);

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
                navigateToMainActivity(email);
            } else {
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
            }
        }
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

    private void navigateToMainActivity(String email) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(KEY_EMAIL, email);
        startActivity(intent);
        finish();
    }
}