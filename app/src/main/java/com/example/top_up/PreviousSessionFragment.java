package com.example.top_up;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PreviousSessionFragment extends Fragment {

    private boolean isExpanded = false;
    private FrameLayout sessionBlock;

    // UI elements
    private TextView sessionStarted, sessionStartTimeDate, sessionEndedTimeDate;
    private TextView sessionIdValue, startBalanceValue, endedBalanceValue, depositValue, paidOutValue;
    private LinearLayout balanceSection;

    // RecyclerView elements
    private RecyclerView previousTransactionRecyclerView;
    private LinearLayout previousEmptyStateLayout;
    private TransactionAdapter transactionAdapter;
    private List<Transaction> previousTransactionList = new ArrayList<>();
    private Handler handler = new Handler();
    private Runnable runnable;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_previous_session, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Toggle button and balance section
        ImageView triangleToggle = view.findViewById(R.id.triangleToggle);
        balanceSection = view.findViewById(R.id.balanceSection);

        // Initialize UI elements
        sessionStarted = view.findViewById(R.id.sessionStarted);

        sessionEndedTimeDate = view.findViewById(R.id.sessionEndedTimeDate);
        sessionIdValue = view.findViewById(R.id.sessionIdValue);
        startBalanceValue = view.findViewById(R.id.startBalanceValue);
        endedBalanceValue = view.findViewById(R.id.endedBalanceValue);
        depositValue = view.findViewById(R.id.depositValue);
        paidOutValue = view.findViewById(R.id.paidOutValue);
        sessionBlock = view.findViewById(R.id.sessionBlock);
        sessionStartTimeDate = view.findViewById(R.id.sessionStartTimeDate);
        startDateTimeUpdater(sessionStartTimeDate);
        // RecyclerView & Empty state
        previousTransactionRecyclerView = view.findViewById(R.id.previousTransactionRecyclerView);
        previousEmptyStateLayout = view.findViewById(R.id.previousEmptyStateLayout);

        previousTransactionRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        transactionAdapter = new TransactionAdapter(previousTransactionList);
        previousTransactionRecyclerView.setAdapter(transactionAdapter);

        // Toggle listener
        View.OnClickListener toggleListener = v -> {
            isExpanded = !isExpanded;
            triangleToggle.animate()
                    .rotation(isExpanded ? 180f : 0f)
                    .setDuration(300)
                    .start();
            balanceSection.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        };

        sessionBlock.setOnClickListener(toggleListener);
        triangleToggle.setOnClickListener(toggleListener);

        // Load data
        loadPreviousSessionData();
        loadPreviousTransactions();
    }



    private void startDateTimeUpdater(TextView sessionTimeDate) {
        runnable = new Runnable() {
            @Override
            public void run() {
                // Format সেট করা (15:50, 18.08.2025)
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm, dd.MM.yyyy", Locale.getDefault());
                String currentDateTime = sdf.format(new Date());

                // TextView তে সেট করা
                sessionTimeDate.setText(currentDateTime);

                // প্রতি 1 সেকেন্ডে আবার আপডেট হবে
                handler.postDelayed(this, 1000);
            }
        };

        // Runnable শুরু করা
        handler.post(runnable);
    }


    private void loadPreviousSessionData() {
        // Example values (replace with your database query)
        String started = "session started";
        String startTime = "14:30, 25.08.2025";
        String endedTime = "16:45, 25.08.2025";
        String sessionId = "123-456-789";
        String startBalance = "0 ৳";
        String endedBalance = "0 ৳";
        String deposits = "0 ৳";
        String paidOut = "0 ৳";

        sessionStarted.setText(started);
        sessionStartTimeDate.setText(startTime);
        sessionEndedTimeDate.setText(endedTime);
        sessionIdValue.setText(sessionId);
        startBalanceValue.setText(startBalance);
        endedBalanceValue.setText(endedBalance);
        depositValue.setText(deposits);
        paidOutValue.setText(paidOut);
    }

    private void loadPreviousTransactions() {
        // TODO: Replace with your database fetch
        previousTransactionList.clear();



        // Empty state check
        if (previousTransactionList.isEmpty()) {
            previousEmptyStateLayout.setVisibility(View.VISIBLE);
            previousTransactionRecyclerView.setVisibility(View.GONE);
        } else {
            previousEmptyStateLayout.setVisibility(View.GONE);
            previousTransactionRecyclerView.setVisibility(View.VISIBLE);
            transactionAdapter.notifyDataSetChanged();
        }
    }
}
