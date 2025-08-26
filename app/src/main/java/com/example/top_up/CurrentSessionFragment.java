package com.example.top_up;

import android.os.Bundle;
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

public class CurrentSessionFragment extends Fragment {

    private boolean isExpanded = false;

    // UI elements
    private TextView sessionStarted, sessionTimeDate;
    private TextView sessionIdValue, startBalanceValue, depositValue, paidOutValue;
    private FrameLayout sessionBlock;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_current_session, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Toggle button
        ImageView triangleToggle = view.findViewById(R.id.triangleToggle);
        LinearLayout balanceSection = view.findViewById(R.id.balanceSection);

        // Initialize UI elements
        sessionStarted = view.findViewById(R.id.sessionStarted);
        sessionTimeDate = view.findViewById(R.id.sessionTimeDate);
        sessionIdValue = view.findViewById(R.id.sessionIdValue);
        startBalanceValue = view.findViewById(R.id.startBalanceValue);
        depositValue = view.findViewById(R.id.depositValue);
        paidOutValue = view.findViewById(R.id.paidOutValue);
        sessionBlock = view.findViewById(R.id.sessionBlock);

        sessionBlock.setOnClickListener(view1 -> {
            isExpanded = !isExpanded;

            // Rotate triangle icon
            triangleToggle.animate()
                    .rotation(isExpanded ? 180f : 0f)
                    .setDuration(300)
                    .start();

            // Show or hide balance section
            balanceSection.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        });

        // Toggle click listener
        triangleToggle.setOnClickListener(v -> {
            isExpanded = !isExpanded;

            // Rotate triangle icon
            triangleToggle.animate()
                    .rotation(isExpanded ? 180f : 0f)
                    .setDuration(300)
                    .start();

            // Show or hide balance section
            balanceSection.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        });

        // Example: Load session data from database (replace with your DB code)
        loadSessionData();
    }

    private void loadSessionData() {
        // Example: Replace these with real values from your database
        String started = "Session started";
        String timeDate = "15:50, 18.08.2025";
        String sessionId = "436-270-287";
        String startBalance = "0 ৳";
        String deposits = "100 ৳";
        String paidOut = "50 ৳";

        // Set values to UI
        sessionStarted.setText(started);
        sessionTimeDate.setText(timeDate);
        sessionIdValue.setText(sessionId);
        startBalanceValue.setText(startBalance);
        depositValue.setText(deposits);
        paidOutValue.setText(paidOut);
    }
}
