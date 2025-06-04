package com.example.kursach;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {
    private final TransactionRepository repository = new TransactionRepository();
    private final MutableLiveData<List<Transaction>> transactions = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    private final MutableLiveData<String> error = new MutableLiveData<>("");
    private ListenerRegistration registration;

    public HomeViewModel() {
        loadTransactions();
    }

    public LiveData<List<Transaction>> getTransactions() { return transactions; }
    public LiveData<Boolean> getLoading() { return loading; }
    public LiveData<String> getError() { return error; }

    public void reload() {
        if (registration != null) registration.remove();
        loadTransactions();
    }

    private void loadTransactions() {
        loading.setValue(true);
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) {
            error.setValue("Пользователь не авторизован");
            loading.setValue(false);
            return;
        }
        registration = repository.getTransactions(userId, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot value, FirebaseFirestoreException e) {
                loading.setValue(false);
                if (e != null) {
                    error.setValue("Ошибка загрузки: " + e.getMessage());
                    return;
                }
                List<Transaction> list = new ArrayList<>();
                if (value != null) {
                    for (DocumentChange dc : value.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.ADDED || dc.getType() == DocumentChange.Type.MODIFIED) {
                            Transaction t = dc.getDocument().toObject(Transaction.class);
                            t.id = dc.getDocument().getId();
                            list.add(t);
                        }
                    }
                }
                transactions.setValue(list);
            }
        });
    }

    public void addTransaction(double amount, String category, String description, long timestamp, String type) {
        loading.setValue(true);
        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) {
            error.setValue("Пользователь не авторизован");
            loading.setValue(false);
            return;
        }
        Transaction t = new Transaction(null, userId, amount, category, description, timestamp, type);
        repository.addTransaction(t, new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(Task<DocumentReference> task) {
                loading.setValue(false);
                if (!task.isSuccessful()) {
                    error.setValue("Ошибка добавления: " + (task.getException() != null ? task.getException().getMessage() : ""));
                }
            }
        });
    }

    public void deleteTransaction(Transaction t) {
        loading.setValue(true);
        repository.deleteTransaction(t.id, task -> {
            loading.setValue(false);
            if (!task.isSuccessful()) {
                error.setValue("Ошибка удаления: " + (task.getException() != null ? task.getException().getMessage() : ""));
            }
        });
    }

    @Override
    protected void onCleared() {
        if (registration != null) registration.remove();
        super.onCleared();
    }
} 