package com.example.financemanager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class SettingsFragment extends PreferenceFragmentCompat {
    private int clickCount = 0;
    private final int requiredClicks = 7;
    private long lastClickTime = 0;
    private final long clickTimeout = 3000; // 3 seconds

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        
        // Check if user is admin and show admin panel option
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            checkIfAdmin(auth.getCurrentUser().getUid());
        }

        // Add click listener to app version preference for admin activation
        Preference exportDataPref = findPreference("export_data");
        if (exportDataPref != null) {
            exportDataPref.setOnPreferenceClickListener(preference -> {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastClickTime > clickTimeout) {
                    clickCount = 1;
                } else {
                    clickCount++;
                }
                lastClickTime = currentTime;

                if (clickCount == requiredClicks) {
                    makeCurrentUserAdmin();
                }
                return true;
            });
        }
    }

    private void makeCurrentUserAdmin() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(auth.getCurrentUser().getUid())
                .update("isAdmin", true)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireContext(), "Админ права активированы", Toast.LENGTH_SHORT).show();
                    // Add admin panel preference
                    addAdminPanelPreference();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Ошибка при активации админ прав", Toast.LENGTH_SHORT).show();
                });
        }
    }

    private void checkIfAdmin(String userId) {
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener(document -> {
                Boolean isAdmin = document.getBoolean("isAdmin");
                if (isAdmin != null && isAdmin) {
                    addAdminPanelPreference();
                }
            });
    }

    private void addAdminPanelPreference() {
        Preference adminPanelPref = new Preference(requireContext());
        adminPanelPref.setKey("admin_panel");
        adminPanelPref.setTitle(getString(R.string.admin_panel));
        adminPanelPref.setOnPreferenceClickListener(preference -> {
            startActivity(new Intent(requireContext(), AdminPanelActivity.class));
            return true;
        });
        getPreferenceScreen().addPreference(adminPanelPref);
    }
} 