package com.example.kursach;

import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import java.util.HashMap;
import java.util.Map;

public class TransactionRepository {
    private final CollectionReference transactionsRef;

    public TransactionRepository() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        transactionsRef = db.collection("transactions");
    }

    public ListenerRegistration getTransactions(String userId, EventListener<QuerySnapshot> listener) {
        return transactionsRef.whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener(listener);
    }

    public void addTransaction(Transaction transaction, OnCompleteListener<DocumentReference> listener) {
        Map<String, Object> data = new HashMap<>();
        data.put("userId", transaction.userId);
        data.put("amount", transaction.amount);
        data.put("category", transaction.category);
        data.put("description", transaction.description);
        data.put("timestamp", transaction.timestamp);
        data.put("type", transaction.type);
        transactionsRef.add(data).addOnCompleteListener(listener);
    }

    public void deleteTransaction(String transactionId, OnCompleteListener<Void> listener) {
        transactionsRef.document(transactionId).delete().addOnCompleteListener(listener);
    }
} 