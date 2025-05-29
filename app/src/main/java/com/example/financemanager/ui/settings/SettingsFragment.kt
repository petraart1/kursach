package com.example.financemanager.ui.settings

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.example.financemanager.R
import com.example.financemanager.ui.admin.AdminActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SettingsFragment : PreferenceFragmentCompat() {
    private var clickCount = 0
    private val requiredClicks = 7
    private var lastClickTime = 0L
    private val clickTimeout = 3000L // 3 seconds

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
        
        // Check if user is admin and show admin panel option
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            checkIfAdmin(currentUser.uid)
        }

        // Add click listener to app version preference for admin activation
        findPreference<Preference>("export_data")?.setOnPreferenceClickListener {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime > clickTimeout) {
                clickCount = 1
            } else {
                clickCount++
            }
            lastClickTime = currentTime

            if (clickCount == requiredClicks) {
                makeCurrentUserAdmin()
            }
            true
        }
    }

    private fun makeCurrentUserAdmin() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(currentUser.uid)
                .update("isAdmin", true)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Админ права активированы", Toast.LENGTH_SHORT).show()
                    // Add admin panel preference
                    val adminPanelPref = Preference(requireContext()).apply {
                        key = "admin_panel"
                        title = getString(R.string.admin_panel)
                        setOnPreferenceClickListener {
                            startActivity(Intent(requireContext(), AdminActivity::class.java))
                            true
                        }
                    }
                    preferenceScreen.addPreference(adminPanelPref)
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Ошибка при активации админ прав", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun checkIfAdmin(userId: String) {
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(userId)
            .get()
            .addOnSuccessListener { document ->
                val isAdmin = document.getBoolean("isAdmin") ?: false
                if (isAdmin) {
                    // Add admin panel preference
                    val adminPanelPref = Preference(requireContext()).apply {
                        key = "admin_panel"
                        title = getString(R.string.admin_panel)
                        setOnPreferenceClickListener {
                            startActivity(Intent(requireContext(), AdminActivity::class.java))
                            true
                        }
                    }
                    preferenceScreen.addPreference(adminPanelPref)
                }
            }
    }
} 