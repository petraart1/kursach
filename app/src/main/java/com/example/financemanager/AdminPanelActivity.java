package com.example.financemanager;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.financemanager.databinding.ActivityAdminPanelBinding;
import com.example.financemanager.model.User;
import com.example.financemanager.model.UserRole;
import com.example.financemanager.repository.UserRepository;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminPanelActivity extends AppCompatActivity {
    private ActivityAdminPanelBinding binding;
    private UserRepository userRepository;
    private List<User> users = new ArrayList<>();
    private UserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminPanelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Настраиваем toolbar
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.admin_panel);
        }

        userRepository = new UserRepository();
        setupRecyclerView();
        loadUsers();
    }

    private void setupRecyclerView() {
        adapter = new UserAdapter(users, this::toggleUserRole);
        binding.usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.usersRecyclerView.setAdapter(adapter);
    }

    private void loadUsers() {
        userRepository.getAllUsers()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    users.clear();
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        User user = document.toObject(User.class);
                        if (user != null) {
                            user.setId(document.getId());
                            users.add(user);
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Ошибка загрузки пользователей", Toast.LENGTH_SHORT).show();
                });
    }

    private void toggleUserRole(User user) {
        UserRole newRole = user.getRole() == UserRole.ADMIN ? UserRole.USER : UserRole.ADMIN;
        userRepository.updateUserRole(user.getId(), newRole)
                .addOnSuccessListener(aVoid -> {
                    user.setRole(newRole);
                    adapter.notifyDataSetChanged();
                    String message = String.format(
                            "Роль пользователя %s изменена на %s",
                            user.getDisplayName(),
                            newRole.toString()
                    );
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Ошибка изменения роли", Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 