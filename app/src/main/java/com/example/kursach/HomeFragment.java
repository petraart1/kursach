package com.example.kursach;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.Button;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.widget.TextView;

public class HomeFragment extends Fragment {
    private HomeViewModel viewModel;
    private TransactionAdapter adapter;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_transactions);
        progressBar = view.findViewById(R.id.progress_bar);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        Button btnAdd = view.findViewById(R.id.btn_add_transaction);

        adapter = new TransactionAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        viewModel.getTransactions().observe(getViewLifecycleOwner(), new Observer<List<Transaction>>() {
            @Override
            public void onChanged(List<Transaction> transactions) {
                adapter.submitList(transactions);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        viewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (!TextUtils.isEmpty(error)) {
                Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> viewModel.reload());
        btnAdd.setOnClickListener(v -> showAddDialog());

        adapter.setOnDeleteListener(transaction -> viewModel.deleteTransaction(transaction));

        // Swipe to delete
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Transaction t = adapter.getCurrentList().get(viewHolder.getAdapterPosition());
                viewModel.deleteTransaction(t);
            }
        });
        itemTouchHelper.attachToRecyclerView(recyclerView);

        return view;
    }

    private void showAddDialog() {
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
                viewModel.addTransaction(amount, category, description, timestamp, type);
            })
            .setNegativeButton("Отмена", null)
            .show();
    }

    // --- Adapter ---
    private static class TransactionAdapter extends ListAdapter<Transaction, TransactionAdapter.TransactionViewHolder> {
        private OnDeleteListener onDeleteListener;
        protected TransactionAdapter() {
            super(DIFF_CALLBACK);
        }
        @NonNull
        @Override
        public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_item, parent, false);
            return new TransactionViewHolder(v);
        }
        @Override
        public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
            Transaction t = getItem(position);
            holder.bind(t, onDeleteListener);
        }
        public void setOnDeleteListener(OnDeleteListener listener) {
            this.onDeleteListener = listener;
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
                        oldItem.timestamp == newItem.timestamp;
            }
        };
        static class TransactionViewHolder extends RecyclerView.ViewHolder {
            public TransactionViewHolder(@NonNull View itemView) { super(itemView); }
            public void bind(Transaction t, OnDeleteListener onDeleteListener) {
                TextView tvAmount = itemView.findViewById(R.id.tv_amount);
                String amountStr;
                int color;
                if ("income".equals(t.type)) {
                    amountStr = String.format(Locale.getDefault(), "+%.2f ₽", t.amount);
                    color = 0xFF388E3C; // зелёный
                } else {
                    amountStr = String.format(Locale.getDefault(), "-%.2f ₽", Math.abs(t.amount));
                    color = 0xFFD32F2F; // красный
                }
                tvAmount.setText(amountStr);
                tvAmount.setTextColor(color);
                ((TextView)itemView.findViewById(R.id.tv_category)).setText(t.category);
                ((TextView)itemView.findViewById(R.id.tv_description)).setText(t.description);
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
                ((TextView)itemView.findViewById(R.id.tv_date)).setText(sdf.format(new Date(t.timestamp)));
                itemView.setOnLongClickListener(v -> {
                    if (onDeleteListener != null) onDeleteListener.onDelete(t);
                    return true;
                });
            }
        }
        interface OnDeleteListener { void onDelete(Transaction t); }
    }
} 