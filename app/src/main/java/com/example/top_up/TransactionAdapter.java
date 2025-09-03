package com.example.top_up;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {

    private List<Transaction> transactionList;

    public TransactionAdapter(List<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactionList.get(position);

        // ================= Mask user_id =================
        String userId = transaction.getTransactionId();
        String maskedUserId = "N\u2640 XXXXXXXX"; // N♀ XXXXXXXX prefix
        if (userId.length() <= 3) {
            maskedUserId += userId; // যদি user_id 3 বা কম সংখ্যা হয়
        } else {
            maskedUserId += userId.substring(userId.length() - 3); // last 3 digits
        }

        holder.tvTransactionId.setText(maskedUserId);
        holder.tvDateTime.setText(transaction.getDateTime());
        holder.tvAmount.setText(transaction.getAmount());

        // ================= Amount color =================
        if (transaction.getAmount().startsWith("+")) {
            holder.tvAmount.setTextColor(0xFF4CAF50); // Green
        }
    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView tvTransactionId, tvDateTime, tvAmount;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTransactionId = itemView.findViewById(R.id.tvTransactionId);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvAmount = itemView.findViewById(R.id.tvAmount);
        }
    }
}
