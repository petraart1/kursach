package com.example.financemanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.financemanager.databinding.ActivityMainBinding;
import com.example.financemanager.repository.UserRepository;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private UserRepository userRepository;
    private boolean isAdmin = false;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userRepository = new UserRepository();
        checkUserStatus();
        setupNavigation();
        if (savedInstanceState == null) {
            loadFragment(new TransactionsFragment());
        }
    }

    protected void checkUserStatus() {
        if (!userRepository.isUserLoggedIn() && !isDemoMode()) {
            // Если пользователь не авторизован и не в демо-режиме, перенаправляем на экран входа
            startActivity(new Intent(this, WelcomeActivity.class));
            finish();
            return;
        }

        // Проверяем, является ли пользователь администратором
        if (userRepository.isUserLoggedIn()) {
            userRepository.getCurrentUserData()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot != null && documentSnapshot.exists()) {
                            isAdmin = userRepository.isCurrentUserAdmin();
                            invalidateOptionsMenu(); // Обновляем меню
                        }
                    });
        }
    }

    private void setupNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            Fragment fragment;
            int itemId = item.getItemId();
            
            if (itemId == R.id.navigation_home) {
                fragment = new TransactionsFragment();
            } else if (itemId == R.id.navigation_statistics) {
                fragment = new StatisticsFragment();
            } else if (itemId == R.id.navigation_ai_assistant) {
                fragment = new AIAssistantFragment();
            } else if (itemId == R.id.navigation_profile) {
                fragment = new ProfileFragment();
            } else if (itemId == R.id.navigation_settings) {
                fragment = new SettingsFragment();
            } else {
                return false;
            }

            loadFragment(fragment);
            return true;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        menu.findItem(R.id.action_admin_panel).setVisible(isAdmin);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            userRepository.logout();
            startActivity(new Intent(this, WelcomeActivity.class));
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_admin_panel) {
            startActivity(new Intent(this, AdminPanelActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean isDemoMode() {
        return !userRepository.isUserLoggedIn();
    }

    private void loadFragment(Fragment fragment) {
        currentFragment = fragment;
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    public void refreshTransactions() {
        if (currentFragment instanceof TransactionsFragment) {
            ((TransactionsFragment) currentFragment).refreshTransactions();
        }
    }
}