package com.example.financemanager;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.financemanager.databinding.ActivityRegisterBinding;
import com.example.financemanager.repository.UserRepository;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userRepository = new UserRepository();
        setupViews();
    }

    private void setupViews() {
        binding.registerButton.setOnClickListener(v -> attemptRegister());
        binding.loginLink.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void attemptRegister() {
        // Сброс ошибок
        binding.emailInput.setError(null);
        binding.passwordInput.setError(null);
        binding.nameInput.setError(null);

        String email = binding.emailInput.getText().toString().trim();
        String password = binding.passwordInput.getText().toString();
        String name = binding.nameInput.getText().toString().trim();

        boolean cancel = false;
        View focusView = null;

        // Проверка имени
        if (TextUtils.isEmpty(name)) {
            binding.nameInput.setError(getString(R.string.error_required));
            focusView = binding.nameInput;
            cancel = true;
        }

        // Проверка пароля
        if (TextUtils.isEmpty(password)) {
            binding.passwordInput.setError(getString(R.string.error_required));
            focusView = binding.passwordInput;
            cancel = true;
        } else if (password.length() < 6) {
            binding.passwordInput.setError(getString(R.string.error_short_password));
            focusView = binding.passwordInput;
            cancel = true;
        }

        // Проверка email
        if (TextUtils.isEmpty(email)) {
            binding.emailInput.setError(getString(R.string.error_required));
            focusView = binding.emailInput;
            cancel = true;
        } else if (!email.contains("@")) {
            binding.emailInput.setError(getString(R.string.error_invalid_email));
            focusView = binding.emailInput;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            registerUser(email, password, name);
        }
    }

    private void registerUser(String email, String password, String name) {
        userRepository.registerUser(email, password, name)
                .addOnCompleteListener(this, task -> {
                    showProgress(false);
                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this,
                                "Регистрация успешна!",
                                Toast.LENGTH_SHORT).show();
                        // Регистрация успешна, переходим в главное активити
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        // Ошибка регистрации
                        Toast.makeText(RegisterActivity.this,
                                "Ошибка регистрации: " + (task.getException() != null ? 
                                        task.getException().getMessage() : "Неизвестная ошибка"),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showProgress(boolean show) {
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.registerForm.setVisibility(show ? View.GONE : View.VISIBLE);
    }
} 