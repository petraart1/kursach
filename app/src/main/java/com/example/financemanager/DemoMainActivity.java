package com.example.financemanager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.financemanager.databinding.ActivityMainBinding;
import com.example.financemanager.demo.DemoDataProvider;

public class DemoMainActivity extends MainActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        // Hide admin-specific menu items
        binding.bottomNavigation.getMenu().findItem(R.id.navigation_profile).setVisible(false);
        
        // Load demo data
        DemoDataProvider.initializeDemoData();
        
        // Start with transactions fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new TransactionsFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.demo_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_exit_demo) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean isDemoMode() {
        return true;
    }

    @Override
    protected void checkUserStatus() {
        // Do nothing in demo mode
    }
} 