package com.example.giaodien.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.giaodien.R;
import com.example.giaodien.Expense;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExpenseHistoryAdapter extends RecyclerView.Adapter<ExpenseHistoryAdapter.ExpenseViewHolder> {
    private List<Expense> expenses;
    private final SimpleDateFormat dateFormat;
    private final NumberFormat currencyFormat;
    private final OnItemLongClickListener longClickListener;

    public ExpenseHistoryAdapter(List<Expense> expenses, OnItemLongClickListener longClickListener) {
        this.expenses = expenses;
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        this.currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        this.longClickListener = longClickListener;
    }

    public void setExpenses(List<Expense> newExpenses) {
        this.expenses = newExpenses;
        notifyDataSetChanged();
    }

    public void removeExpense(int position) {
        expenses.remove(position);
        notifyItemRemoved(position);
    }

    public void updateExpense(Expense updatedExpense, int position) {
        expenses.set(position, updatedExpense);
        notifyItemChanged(position);
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense_history, parent, false);
        return new ExpenseViewHolder(view, dateFormat, currencyFormat);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        final Expense expense = expenses.get(position);
        holder.bind(expense);

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onItemLongClick(expense, position);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvAmount, tvCategory, tvDate, tvPaymentMethod, tvDescription;
        private final SimpleDateFormat dateFormat;
        private final NumberFormat currencyFormat;

        public ExpenseViewHolder(@NonNull View itemView, SimpleDateFormat dateFormat, NumberFormat currencyFormat) {
            super(itemView);
            this.dateFormat = dateFormat;
            this.currencyFormat = currencyFormat;

            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvPaymentMethod = itemView.findViewById(R.id.tvPaymentMethod);
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }

        public void bind(Expense expense) {
            tvAmount.setText(currencyFormat.format(expense.getAmount()));
            tvCategory.setText(expense.getCategory());
            tvDate.setText(dateFormat.format(new Date(expense.getTimestamp() * 1000L)));
            tvPaymentMethod.setText(expense.getPaymentMethod());
            tvDescription.setText(expense.getDescription());
        }
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(Expense expense, int position);
    }
}