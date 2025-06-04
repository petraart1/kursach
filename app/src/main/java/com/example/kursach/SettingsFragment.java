package com.example.kursach;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

public class SettingsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        Button btnLogout = view.findViewById(R.id.btn_logout);
        Spinner spTheme = view.findViewById(R.id.sp_theme);
        Button btnLogin = view.findViewById(R.id.btn_login);
        Button btnRegister = view.findViewById(R.id.btn_register);
        btnLogin.setVisibility(View.GONE);
        btnRegister.setVisibility(View.GONE);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.theme_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTheme.setAdapter(adapter);
        int currentMode = AppCompatDelegate.getDefaultNightMode();
        if (currentMode == AppCompatDelegate.MODE_NIGHT_YES) spTheme.setSelection(1);
        else if (currentMode == AppCompatDelegate.MODE_NIGHT_NO) spTheme.setSelection(0);
        else spTheme.setSelection(2);
        spTheme.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            int lastMode = currentMode;
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                int newMode;
                switch (position) {
                    case 0:
                        newMode = AppCompatDelegate.MODE_NIGHT_NO;
                        break;
                    case 1:
                        newMode = AppCompatDelegate.MODE_NIGHT_YES;
                        break;
                    default:
                        newMode = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;
                        break;
                }
                if (lastMode != newMode) {
                    lastMode = newMode;
                    AppCompatDelegate.setDefaultNightMode(newMode);
                    if (getActivity() != null) {
                        View nav = getActivity().findViewById(R.id.bottom_navigation);
                        if (nav instanceof com.google.android.material.bottomnavigation.BottomNavigationView) {
                            ((com.google.android.material.bottomnavigation.BottomNavigationView) nav).setSelectedItemId(R.id.nav_settings);
                        }
                    }
                }
            }
            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        btnLogout.setOnClickListener(v -> {
            new AuthManager().logout();
            Toast.makeText(getContext(), "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            if (getActivity() != null) getActivity().finish();
        });
        return view;
    }
} 