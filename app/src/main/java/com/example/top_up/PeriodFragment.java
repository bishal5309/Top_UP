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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class PeriodFragment extends Fragment {

    private boolean isExpanded = false;

    // UI elements
    private TextView selectedPeriod, selectedPeriodDate;
    private TextView depositValue, paidOutValue;
    private FrameLayout sessionBlock;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_period, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI elements
        selectedPeriod = view.findViewById(R.id.selectedPeriod);

        depositValue = view.findViewById(R.id.depositValue);
        paidOutValue = view.findViewById(R.id.paidOutValue);
        sessionBlock = view.findViewById(R.id.sessionBlock);

        selectedPeriodDate = view.findViewById(R.id.selectedPeriodDate);
        setOneMonthPeriod(selectedPeriodDate);



        // Triangle toggle & balance section
        ImageView triangleToggle = view.findViewById(R.id.triangleToggle);
        LinearLayout balanceSection = view.findViewById(R.id.balanceSection);

        sessionBlock.setOnClickListener(view1 -> {
            isExpanded = !isExpanded;

            // Rotate triangle
            triangleToggle.animate()
                    .rotation(isExpanded ? 180f : 0f)
                    .setDuration(300)
                    .start();

            // Show/hide balance section
            balanceSection.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        });



        triangleToggle.setOnClickListener(v -> {
            isExpanded = !isExpanded;

            // Rotate triangle
            triangleToggle.animate()
                    .rotation(isExpanded ? 180f : 0f)
                    .setDuration(300)
                    .start();

            // Show/hide balance section
            balanceSection.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        });

        // Load sample data or from database
        loadPeriodData();
    }

    private void setOneMonthPeriod(TextView selectedPeriodDate) {
        // আজকের তারিখ
        Calendar startCal = Calendar.getInstance();
        // মাসের 01 তারিখ সেট করা
        startCal.set(Calendar.DAY_OF_MONTH, 1);

        // ১ মাস পরে তারিখ নেওয়া
        Calendar endCal = (Calendar) startCal.clone();
        endCal.add(Calendar.MONTH, 1);

        // Format সেট করা
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());

        // দুই তারিখ format করা
        String startDate = sdf.format(startCal.getTime());
        String endDate = sdf.format(endCal.getTime());

        // TextView তে বসানো
        selectedPeriodDate.setText(startDate + " - " + endDate);
    }


    private void loadPeriodData() {
        // Example/sample values
        String period = "Selected period";
        String periodDate = "01 August 2025 - 18 August 2025";
        String deposits = "0 ৳";
        String paidOut = "0 ৳";

        // Set values to UI
        selectedPeriod.setText(period);
        depositValue.setText(deposits);
        paidOutValue.setText(paidOut);
    }
}
