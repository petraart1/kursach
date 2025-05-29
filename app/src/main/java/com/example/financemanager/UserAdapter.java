package com.example.financemanager;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.financemanager.databinding.ItemUserBinding;
import com.example.financemanager.model.User;
import com.example.financemanager.model.UserRole;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private final List<User> users;
    private final OnUserRoleChangeListener listener;

    public interface OnUserRoleChangeListener {
        void onUserRoleChange(User user);
    }

    public UserAdapter(List<User> users, OnUserRoleChangeListener listener) {
        this.users = users;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemUserBinding binding = ItemUserBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new UserViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.bind(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        private final ItemUserBinding binding;

        public UserViewHolder(@NonNull ItemUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(User user) {
            binding.userName.setText(user.getDisplayName());
            binding.userEmail.setText(user.getEmail());
            binding.roleSwitch.setChecked(user.getRole() == UserRole.ADMIN);
            
            binding.roleSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (buttonView.isPressed()) {
                    listener.onUserRoleChange(user);
                }
            });
        }
    }
} 