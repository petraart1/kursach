package com.example.kursach;

import android.app.Application;
import android.util.Log;
import com.google.firebase.FirebaseApp;
import androidx.appcompat.app.AppCompatDelegate;
import android.content.SharedPreferences;

public class KursachApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Восстановление темы
        SharedPreferences prefs = getSharedPreferences("settings", MODE_PRIVATE);
        int nightMode = prefs.getInt("night_mode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        AppCompatDelegate.setDefaultNightMode(nightMode);
        try {
            FirebaseApp.initializeApp(this);
            Log.d("KursachApp", "Firebase инициализирован");
        } catch (Exception e) {
            Log.e("KursachApp", "Ошибка инициализации Firebase", e);
        }
    }
} 