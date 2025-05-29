package com.example.financemanager.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.financemanager.model.User;
import com.example.financemanager.model.UserRole;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class UserRepository {
    private static final String COLLECTION_USERS = "users";
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;
    private User demoUser;

    public UserRepository() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
    }

    public Task<AuthResult> loginUser(String email, String password) {
        return auth.signInWithEmailAndPassword(email, password);
    }

    public Task<AuthResult> registerUser(String email, String password, String name) {
        return auth.createUserWithEmailAndPassword(email, password)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    FirebaseUser firebaseUser = task.getResult().getUser();
                    if (firebaseUser == null) {
                        throw new IllegalStateException("User creation failed");
                    }

                    User newUser = new User(email, name, UserRole.USER);
                    newUser.setId(firebaseUser.getUid());
                    return createUser(newUser).continueWith(t -> task.getResult());
                });
    }

    public Task<Void> createUser(User user) {
        return db.collection(COLLECTION_USERS)
                .document(user.getId())
                .set(user);
    }

    public Task<DocumentSnapshot> getUser(String uid) {
        return db.collection(COLLECTION_USERS)
                .document(uid)
                .get();
    }

    public Task<QuerySnapshot> getAllUsers() {
        return db.collection(COLLECTION_USERS)
                .get();
    }

    public Task<Void> updateUserRole(String uid, UserRole role) {
        return db.collection(COLLECTION_USERS)
                .document(uid)
                .update("role", role);
    }

    public Task<Void> updateUserProfile(String uid, String displayName) {
        return db.collection(COLLECTION_USERS)
                .document(uid)
                .update("displayName", displayName);
    }

    public Task<Void> deleteUser(String uid) {
        return db.collection(COLLECTION_USERS)
                .document(uid)
                .delete();
    }

    public FirebaseUser getCurrentUser() {
        return auth.getCurrentUser();
    }

    public Task<DocumentSnapshot> getCurrentUserData() {
        FirebaseUser currentUser = getCurrentUser();
        if (currentUser == null) {
            return null;
        }
        return getUser(currentUser.getUid());
    }

    public boolean isCurrentUserAdmin() {
        // TODO: Реализовать проверку роли в Firebase
        FirebaseUser currentUser = getCurrentUser();
        return currentUser != null && currentUser.getEmail() != null 
                && currentUser.getEmail().contains("admin");
    }

    public void logout() {
        auth.signOut();
        demoUser = null;
    }

    @NonNull
    public User getDemoUser() {
        if (demoUser == null) {
            demoUser = User.createDemoUser();
        }
        return demoUser;
    }

    public boolean isUserLoggedIn() {
        return auth.getCurrentUser() != null;
    }
} 