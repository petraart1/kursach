package com.example.kursach;

import android.app.Application;
import android.util.Log;
import com.google.firebase.FirebaseApp;

public class KursachApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            FirebaseApp.initializeApp(this);
            Log.d("KursachApp", "Firebase инициализирован");
        } catch (Exception e) {
            Log.e("KursachApp", "Ошибка инициализации Firebase", e);
        }
    }
} 