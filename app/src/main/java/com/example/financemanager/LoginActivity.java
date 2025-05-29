package com.example.financemanager;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.financemanager.databinding.ActivityLoginBinding;
import com.example.financemanager.repository.UserRepository;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userRepository = new UserRepository();
        setupClickListeners();
    }

    private void setupClickListeners() {
        binding.loginButton.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        String email = binding.emailInput.getText().toString().trim();
        String password = binding.passwordInput.getText().toString().trim();

        if (validateForm(email, password)) {
            binding.loginButton.setEnabled(false);
            
            userRepository.loginUser(email, password)
                    .addOnCompleteListener(task -> {
                        binding.loginButton.setEnabled(true);
                        if (task.isSuccessful()) {
                            startMainActivity();
                        } else {
                            String errorMessage = task.getException() != null ? 
                                    task.getException().getMessage() : 
                                    "Ошибка при входе";
                            Toast.makeText(LoginActivity.this, 
                                    errorMessage, 
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private boolean validateForm(String email, String password) {
        boolean valid = true;

        if (TextUtils.isEmpty(email)) {
            binding.emailInput.setError("Обязательное поле");
            valid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailInput.setError("Введите корректный email");
            valid = false;
        } else {
            binding.emailInput.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            binding.passwordInput.setError("Обязательное поле");
            valid = false;
        } else if (password.length() < 6) {
            binding.passwordInput.setError("Минимум 6 символов");
            valid = false;
        } else {
            binding.passwordInput.setError(null);
        }

        return valid;
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
} 