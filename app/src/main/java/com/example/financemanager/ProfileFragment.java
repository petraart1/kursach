package com.example.financemanager;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.financemanager.databinding.FragmentProfileBinding;
import com.example.financemanager.model.UserRole;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private FirebaseAuth firebaseAuth;
    private boolean isAdmin = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        firebaseAuth = FirebaseAuth.getInstance();
        setupUserInfo();
        setupButtons();
    }

    private void setupUserInfo() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            binding.emailText.setText("Email: " + currentUser.getEmail());
            binding.nameInput.setText(currentUser.getDisplayName());
            
            // TODO: Получение роли пользователя из Firebase
            // Временно устанавливаем роль администратора для тестирования
            if (currentUser.getEmail() != null && currentUser.getEmail().contains("admin")) {
                isAdmin = true;
                binding.roleText.setText("Роль: администратор");
                binding.adminPanel.setVisibility(View.VISIBLE);
            }
        } else if (getMainActivity().isDemoMode()) {
            binding.emailText.setText("Email: demo@example.com");
            binding.nameInput.setText("Демо пользователь");
            binding.roleText.setText("Роль: демо-режим");
            disableEditing();
        }
    }

    private void setupButtons() {
        binding.saveProfileButton.setOnClickListener(v -> saveProfile());
        binding.changePasswordButton.setOnClickListener(v -> showChangePasswordDialog());
        binding.logoutButton.setOnClickListener(v -> logout());

        if (isAdmin) {
            binding.viewAllUsersButton.setOnClickListener(v -> showAllUsers());
            binding.exportDataButton.setOnClickListener(v -> exportData());
        }
    }

    private void saveProfile() {
        String newName = binding.nameInput.getText().toString().trim();
        if (TextUtils.isEmpty(newName)) {
            binding.nameInputLayout.setError("Введите имя");
            return;
        }

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(newName)
                    .build();

            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(requireContext(), "Профиль обновлен", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(), "Ошибка обновления профиля", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void showChangePasswordDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
        new AlertDialog.Builder(requireContext())
                .setTitle("Изменение пароля")
                .setView(dialogView)
                .setPositiveButton("Изменить", (dialog, which) -> {
                    // TODO: Реализовать изменение пароля
                    Toast.makeText(requireContext(), "Функция в разработке", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    private void showAllUsers() {
        // TODO: Реализовать просмотр всех пользователей
        Toast.makeText(requireContext(), "Функция в разработке", Toast.LENGTH_SHORT).show();
    }

    private void exportData() {
        // TODO: Реализовать экспорт данных
        Toast.makeText(requireContext(), "Функция в разработке", Toast.LENGTH_SHORT).show();
    }

    private void disableEditing() {
        binding.nameInput.setEnabled(false);
        binding.saveProfileButton.setEnabled(false);
        binding.changePasswordButton.setEnabled(false);
    }

    private void logout() {
        // Выход из Firebase Auth
        firebaseAuth.signOut();
        
        // Переход на экран приветствия
        Intent intent = new Intent(requireActivity(), WelcomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    private MainActivity getMainActivity() {
        return (MainActivity) requireActivity();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
} 