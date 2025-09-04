package com.example.top_up;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class CurrentSessionFragment extends Fragment {

    private LinearLayout emptyStateLayout, balanceSection;
    private RecyclerView transactionRecyclerView;
    private TransactionAdapter transactionAdapter;
    private ArrayList<Transaction> transactionList = new ArrayList<>();
    private ImageView triangleToggle;
    private FrameLayout sessionBlock;
    private TextView sessionTimeDate;
    private boolean isExpanded = false;
    private Handler handler = new Handler();
    private Runnable runnable;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_current_session, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        emptyStateLayout = view.findViewById(R.id.emptyStateLayout);
        transactionRecyclerView = view.findViewById(R.id.transactionRecyclerView);
        balanceSection = view.findViewById(R.id.balanceSection);
        triangleToggle = view.findViewById(R.id.triangleToggle);
        sessionBlock = view.findViewById(R.id.sessionBlock);
        sessionTimeDate = view.findViewById(R.id.sessionTimeDate);

        startDateTimeUpdater(sessionTimeDate);

        transactionAdapter = new TransactionAdapter(transactionList);
        transactionRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        transactionRecyclerView.setAdapter(transactionAdapter);

        // ================= Toggle listener =================
        View.OnClickListener toggleListener = v -> {
            isExpanded = !isExpanded;

            triangleToggle.animate()
                    .rotation(isExpanded ? 180f : 0f)
                    .setDuration(300)
                    .start();

            balanceSection.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        };
        triangleToggle.setOnClickListener(toggleListener);
        sessionBlock.setOnClickListener(toggleListener);

        fetchCurrentSessionTransactions();
    }

    private void fetchCurrentSessionTransactions() {
        String url = "https://sbetshopbd.xyz/api/get_topup_logs.php";

        String[] keys = {"workplace"};
        String[] values = {SessionCache.workplace};

        VollyHelper.getInstance(getContext()).postData(url, new VollyHelper.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    String status = obj.getString("status");
                    if (status.equals("success")) {
                        JSONArray transactionsArray = obj.getJSONArray("transactions");
                        transactionList.clear();

                        for (int i = 0; i < transactionsArray.length(); i++) {
                            JSONObject transactionObj = transactionsArray.getJSONObject(i);
                            String transactionId = transactionObj.getString("user_id");
                            String amount = transactionObj.getString("amount") + " ৳";
                            String time = transactionObj.getString("created_at");
                            transactionList.add(new Transaction(transactionId, time, amount));
                        }
                    } else {
                        transactionList.clear();
                    }
                    updateUI();
                } catch (Exception e) {
                    Log.e("CurrentSessionFragment", "JSON Error", e);
                    transactionList.clear();
                    updateUI();
                }
            }

            @Override
            public void onError(String error) {
                Log.e("CurrentSessionFragment", "Volley Error: " + error);
                transactionList.clear();
                updateUI();
            }
        }, keys, values);
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


    private void updateUI() {
        if (transactionList.isEmpty()) {
            emptyStateLayout.setVisibility(View.VISIBLE);
            transactionRecyclerView.setVisibility(View.GONE);
        } else {
            emptyStateLayout.setVisibility(View.GONE);
            transactionRecyclerView.setVisibility(View.VISIBLE);
            transactionAdapter.notifyDataSetChanged();
        }
    }
}
