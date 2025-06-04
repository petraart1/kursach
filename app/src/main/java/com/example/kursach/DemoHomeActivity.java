package com.example.kursach;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import androidx.appcompat.app.AppCompatDelegate;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.text.TextUtils;
import java.util.Calendar;

public class DemoHomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_home);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            bottomNavigationView.setOnItemSelectedListener(this::onNavItemSelected);
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        }
        showHome();
    }

    private boolean onNavItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            showHome();
            return true;
        } else if (id == R.id.nav_stats || id == R.id.nav_assistant) {
            showAuthDialog();
            return false;
        } else if (id == R.id.nav_settings) {
            showDemoSettings();
            return true;
        }
        return false;
    }

    private void showHome() {
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.demo_fragment_container, new DemoTransactionsFragment())
            .commit();
    }

    private void showAuthDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Только для зарегистрированных пользователей")
            .setMessage("Для доступа к этому разделу необходимо войти или зарегистрироваться.")
            .setPositiveButton("Войти", (d, w) -> {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
            })
            .setNegativeButton("Зарегистрироваться", (d, w) -> {
                startActivity(new Intent(this, RegisterActivity.class));
                finish();
            })
            .setNeutralButton("Отмена", null)
            .show();
    }

    private void showDemoSettings() {
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.demo_fragment_container, new DemoSettingsFragment())
            .commit();
    }

    public static class DemoTransactionsFragment extends Fragment {
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_home, container, false);
            RecyclerView recyclerView = view.findViewById(R.id.recycler_transactions);
            Button btnAdd = view.findViewById(R.id.btn_add_transaction);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            DemoTransactionAdapter adapter = new DemoTransactionAdapter();
            recyclerView.setAdapter(adapter);
            List<Transaction> demoList = new ArrayList<>(getDemoTransactions());
            adapter.submitList(new ArrayList<>(demoList));
            view.findViewById(R.id.progress_bar).setVisibility(View.GONE);
            btnAdd.setVisibility(View.VISIBLE);
            btnAdd.setOnClickListener(v -> showAddDialog(adapter, demoList));
            // Swipe to delete
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                    return false;
                }
                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    demoList.remove(viewHolder.getAdapterPosition());
                    adapter.submitList(new ArrayList<>(demoList));
                }
            });
            itemTouchHelper.attachToRecyclerView(recyclerView);
            return view;
        }
        private List<Transaction> getDemoTransactions() {
            List<Transaction> list = new ArrayList<>();
            list.add(new Transaction("1", "demo", 15000, "Зарплата", "Май 2024", System.currentTimeMillis() - 86400000L * 10, "income"));
            list.add(new Transaction("2", "demo", -2500, "Продукты", "Магазин Пятёрочка", System.currentTimeMillis() - 86400000L * 8, "expense"));
            list.add(new Transaction("3", "demo", -1200, "Транспорт", "Метро", System.currentTimeMillis() - 86400000L * 7, "expense"));
            list.add(new Transaction("4", "demo", 500, "Подарки", "День рождения", System.currentTimeMillis() - 86400000L * 5, "income"));
            list.add(new Transaction("5", "demo", -3000, "Развлечения", "Кино", System.currentTimeMillis() - 86400000L * 2, "expense"));
            return list;
        }
        private void showAddDialog(DemoTransactionAdapter adapter, List<Transaction> demoList) {
            View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_transaction, null);
            EditText etAmount = dialogView.findViewById(R.id.et_amount);
            Spinner spCategory = dialogView.findViewById(R.id.sp_category);
            EditText etDescription = dialogView.findViewById(R.id.et_description);
            EditText etDate = dialogView.findViewById(R.id.et_date);
            RadioGroup rgType = dialogView.findViewById(R.id.rg_type);
            RadioButton rbIncome = dialogView.findViewById(R.id.rb_income);
            RadioButton rbExpense = dialogView.findViewById(R.id.rb_expense);
            ArrayAdapter<CharSequence> catAdapter = ArrayAdapter.createFromResource(getContext(), R.array.categories, android.R.layout.simple_spinner_item);
            catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spCategory.setAdapter(catAdapter);
            final Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
            etDate.setText(sdf.format(calendar.getTime()));
            etDate.setOnClickListener(v -> {
                new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
                    calendar.set(year, month, dayOfMonth);
                    etDate.setText(sdf.format(calendar.getTime()));
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            });
            new AlertDialog.Builder(getContext())
                .setTitle("Добавить транзакцию")
                .setView(dialogView)
                .setPositiveButton("Добавить", (dialog, which) -> {
                    String amountStr = etAmount.getText().toString();
                    String category = spCategory.getSelectedItem().toString();
                    String description = etDescription.getText().toString();
                    long timestamp = calendar.getTimeInMillis();
                    String type = (rgType.getCheckedRadioButtonId() == R.id.rb_income) ? "income" : "expense";
                    if (TextUtils.isEmpty(amountStr)) {
                        Toast.makeText(getContext(), "Введите сумму", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    double amount;
                    try {
                        amount = Double.parseDouble(amountStr);
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Некорректная сумма", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String id = String.valueOf(System.currentTimeMillis());
                    demoList.add(0, new Transaction(id, "demo", amount, category, description, timestamp, type));
                    adapter.submitList(new ArrayList<>(demoList));
                })
                .setNegativeButton("Отмена", null)
                .show();
        }
        private static class DemoTransactionAdapter extends ListAdapter<Transaction, DemoTransactionAdapter.TransactionViewHolder> {
            protected DemoTransactionAdapter() { super(DIFF_CALLBACK); }
            @NonNull
            @Override
            public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_item, parent, false);
                return new TransactionViewHolder(v);
            }
            @Override
            public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
                Transaction t = getItem(position);
                holder.bind(t);
            }
            static final DiffUtil.ItemCallback<Transaction> DIFF_CALLBACK = new DiffUtil.ItemCallback<Transaction>() {
                @Override
                public boolean areItemsTheSame(@NonNull Transaction oldItem, @NonNull Transaction newItem) {
                    return oldItem.id != null && oldItem.id.equals(newItem.id);
                }
                @Override
                public boolean areContentsTheSame(@NonNull Transaction oldItem, @NonNull Transaction newItem) {
                    return oldItem.amount == newItem.amount &&
                            oldItem.category.equals(newItem.category) &&
                            oldItem.description.equals(newItem.description) &&
                            oldItem.timestamp == newItem.timestamp &&
                            oldItem.type.equals(newItem.type);
                }
            };
            static class TransactionViewHolder extends RecyclerView.ViewHolder {
                public TransactionViewHolder(@NonNull View itemView) { super(itemView); }
                public void bind(Transaction t) {
                    ((android.widget.TextView)itemView.findViewById(R.id.tv_amount)).setText(String.format(Locale.getDefault(), "%+.2f ₽", t.amount));
                    ((android.widget.TextView)itemView.findViewById(R.id.tv_category)).setText(t.category);
                    ((android.widget.TextView)itemView.findViewById(R.id.tv_description)).setText(t.description);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                    ((android.widget.TextView)itemView.findViewById(R.id.tv_date)).setText(sdf.format(new Date(t.timestamp)));
                }
            }
        }
    }

    public static class DemoSettingsFragment extends Fragment {
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_settings, container, false);
            Button btnLogout = view.findViewById(R.id.btn_logout);
            Button btnLogin = view.findViewById(R.id.btn_login);
            Button btnRegister = view.findViewById(R.id.btn_register);
            Spinner spTheme = view.findViewById(R.id.sp_theme);

            // Кнопка выхода из демо-режима
            btnLogout.setText("Выйти из демо-режима");
            btnLogout.setOnClickListener(v -> {
                startActivity(new Intent(getActivity(), LoginActivity.class));
                requireActivity().finish();
            });

            // Кнопка входа
            btnLogin.setVisibility(View.VISIBLE);
            btnLogin.setOnClickListener(v -> {
                startActivity(new Intent(getActivity(), LoginActivity.class));
                requireActivity().finish();
            });

            // Кнопка регистрации
            btnRegister.setVisibility(View.VISIBLE);
            btnRegister.setOnClickListener(v -> {
                startActivity(new Intent(getActivity(), RegisterActivity.class));
                requireActivity().finish();
            });

            // Настройка выбора темы
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
                    }
                }
                @Override
                public void onNothingSelected(android.widget.AdapterView<?> parent) {}
            });
            return view;
        }
    }
} 