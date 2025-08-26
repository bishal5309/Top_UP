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

public class PreviousSessionFragment extends Fragment {

    private boolean isExpanded = false;
    private FrameLayout sessionBlock;

    // UI elements
    private TextView sessionStarted, sessionStartTimeDate, sessionEndedTimeDate;
    private TextView sessionIdValue, startBalanceValue, endedBalanceValue, depositValue, paidOutValue;

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
        LinearLayout balanceSection = view.findViewById(R.id.balanceSection);

        // Initialize UI elements
        sessionStarted = view.findViewById(R.id.sessionStarted);
        sessionStartTimeDate = view.findViewById(R.id.sessionStartTimeDate);
        sessionEndedTimeDate = view.findViewById(R.id.sessionEndedTimeDate);
        sessionIdValue = view.findViewById(R.id.sessionIdValue);
        startBalanceValue = view.findViewById(R.id.startBalanceValue);
        endedBalanceValue = view.findViewById(R.id.endedBalanceValue);
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

        // Example: Load previous session data
        loadPreviousSessionData();
    }

    private void loadPreviousSessionData() {
        // Example values (replace with your database query)
        String started = "session started";
        String startTime = "14:30, 25.08.2025";
        String endedTime = "16:45, 25.08.2025";
        String sessionId = "123-456-789";
        String startBalance = "500 ৳";
        String endedBalance = "750 ৳";
        String deposits = "300 ৳";
        String paidOut = "50 ৳";

        // Set values to UI
        sessionStarted.setText(started);
        sessionStartTimeDate.setText(startTime);
        sessionEndedTimeDate.setText(endedTime);
        sessionIdValue.setText(sessionId);
        startBalanceValue.setText(startBalance);
        endedBalanceValue.setText(endedBalance);
        depositValue.setText(deposits);
        paidOutValue.setText(paidOut);
    }
}
