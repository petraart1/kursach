package com.example.financemanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.example.financemanager.databinding.ActivityWelcomeBinding;
import com.example.financemanager.repository.UserRepository;

public class WelcomeActivity extends AppCompatActivity {
    private ActivityWelcomeBinding binding;
    private UserRepository userRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userRepository = new UserRepository();

        // Check if user is already signed in
        if (userRepository.isUserLoggedIn()) {
            startMainActivity();
            finish();
            return;
        }

        setupClickListeners();
    }

    private void setupClickListeners() {
        binding.loginButton.setOnClickListener(v -> {
            // Navigate to login screen
            startActivity(new Intent(this, LoginActivity.class));
        });

        binding.registerButton.setOnClickListener(v -> {
            // Navigate to registration screen
            startActivity(new Intent(this, RegisterActivity.class));
        });

        binding.demoButton.setOnClickListener(v -> {
            // Start demo mode
            startDemoMode();
        });
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void startDemoMode() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("DEMO_MODE", true);
        startActivity(intent);
    }
} 