package com.example.kursach;

import android.app.Activity;
import android.text.TextUtils;
import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class AuthManager {
    private final FirebaseAuth mAuth;

    public AuthManager() {
        mAuth = FirebaseAuth.getInstance();
    }

    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    public void register(String email, String password, final AuthCallback callback) {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            callback.onFailure("Email и пароль не могут быть пустыми");
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        callback.onSuccess(mAuth.getCurrentUser());
                    } else {
                        callback.onFailure(task.getException() != null ? task.getException().getMessage() : "Ошибка регистрации");
                    }
                }
            });
    }

    public void login(String email, String password, final AuthCallback callback) {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            callback.onFailure("Email и пароль не могут быть пустыми");
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        callback.onSuccess(mAuth.getCurrentUser());
                    } else {
                        callback.onFailure(task.getException() != null ? task.getException().getMessage() : "Ошибка входа");
                    }
                }
            });
    }

    public void logout() {
        mAuth.signOut();
    }

    public interface AuthCallback {
        void onSuccess(FirebaseUser user);
        void onFailure(String errorMessage);
    }
} 