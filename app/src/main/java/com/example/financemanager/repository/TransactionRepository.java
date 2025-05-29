package com.example.financemanager.repository;

import com.example.financemanager.model.Transaction;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class TransactionRepository {
    private static final String COLLECTION_TRANSACTIONS = "transactions";
    private final FirebaseFirestore db;
    private final FirebaseAuth auth;
    private final UserRepository userRepository;

    public TransactionRepository() {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        userRepository = new UserRepository();
    }

    public Task<DocumentReference> addTransaction(Transaction transaction) {
        // Если пользователь в демо-режиме, не сохраняем транзакции
        if (!userRepository.isUserLoggedIn()) {
            return null;
        }

        // Добавляем ID пользователя к транзакции
        transaction.setUserId(auth.getCurrentUser().getUid());
        
        return db.collection(COLLECTION_TRANSACTIONS)
                .add(transaction);
    }

    public Task<QuerySnapshot> getUserTransactions() {
        // Если пользователь в демо-режиме, возвращаем пустой результат
        if (!userRepository.isUserLoggedIn()) {
            return null;
        }

        return db.collection(COLLECTION_TRANSACTIONS)
                .whereEqualTo("userId", auth.getCurrentUser().getUid())
                .orderBy("date", Query.Direction.DESCENDING)
                .get();
    }

    public Task<QuerySnapshot> getAllTransactions() {
        // Только для администраторов
        if (!userRepository.isCurrentUserAdmin()) {
            return null;
        }

        return db.collection(COLLECTION_TRANSACTIONS)
                .orderBy("date", Query.Direction.DESCENDING)
                .get();
    }

    public Task<Void> deleteTransaction(String transactionId) {
        return db.collection(COLLECTION_TRANSACTIONS)
                .document(transactionId)
                .delete();
    }

    public Task<Void> updateTransaction(String transactionId, Transaction transaction) {
        return db.collection(COLLECTION_TRANSACTIONS)
                .document(transactionId)
                .set(transaction);
    }

    public Task<QuerySnapshot> getTransactions() {
        return db.collection(COLLECTION_TRANSACTIONS)
                .get();
    }
} 