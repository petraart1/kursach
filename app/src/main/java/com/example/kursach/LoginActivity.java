package com.example.kursach;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private EditText etEmail, etPassword;
    private Button btnLogin, btnToRegister, btnDemo;
    private ProgressDialog progressDialog;
    private AuthManager authManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "Activity created");
        authManager = new AuthManager();
        if (authManager.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        btnToRegister = findViewById(R.id.btn_to_register);
        btnDemo = findViewById(R.id.btn_demo);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString();
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(this, "Введите email и пароль", Toast.LENGTH_SHORT).show();
                return;
            }
            setButtonsEnabled(false);
            showProgress(true);
            authManager.login(email, password, new AuthManager.AuthCallback() {
                @Override
                public void onSuccess(FirebaseUser user) {
                    showProgress(false);
                    setButtonsEnabled(true);
                    Toast.makeText(LoginActivity.this, "Вход выполнен", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                }
                @Override
                public void onFailure(String errorMessage) {
                    showProgress(false);
                    setButtonsEnabled(true);
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            });
        });

        btnToRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });

        btnDemo.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, DemoHomeActivity.class));
            finish();
        });
    }

    private void setButtonsEnabled(boolean enabled) {
        btnLogin.setEnabled(enabled);
        btnToRegister.setEnabled(enabled);
        btnDemo.setEnabled(enabled);
    }

    private void showProgress(boolean show) {
        if (show) {
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Вход...");
                progressDialog.setCancelable(false);
            }
            progressDialog.show();
        } else if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
} 