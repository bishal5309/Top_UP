package com.example.top_up;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;

import org.json.JSONObject;

public class home_page extends AppCompatActivity {

    private HomePageHelper helper;
    private TextView epose, tvBalance, address, tvAmount;
    private ImageButton btnRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);


        int nightModeFlags =
                getResources().getConfiguration().uiMode &
                        android.content.res.Configuration.UI_MODE_NIGHT_MASK;

        if (nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
            getWindow().setStatusBarColor(Color.parseColor("#112740")); // Dark
            getWindow().getDecorView().setSystemUiVisibility(0); // White icons
        } else {
            getWindow().setStatusBarColor(Color.parseColor("#FFFFFF")); // Light
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR); // Dark icons
        }

        // Initialize helper
        helper = new HomePageHelper(this);

        // Bind views
        epose = findViewById(R.id.epose);
        tvBalance = findViewById(R.id.tvBalance);
        address = findViewById(R.id.address);
        tvAmount = findViewById(R.id.tvAmount);
        btnRefresh = findViewById(R.id.btnRefresh);

        // Refresh button click → animation + fetch new data
        btnRefresh.setOnClickListener(view -> {
            helper.animateRefreshButton(); // only animation
            fetchUserData(SessionCache.userId, SessionCache.password, SessionCache.workplace);
            fetchTotalTopUp(SessionCache.workplace);
        });

        // Get credentials from Intent or fallback to SessionCache
        String userId = getIntent().getStringExtra("user_id");
        String password = getIntent().getStringExtra("password");
        String workplace = getIntent().getStringExtra("workplace");

        if (userId == null || password == null || workplace == null) {
            userId = SessionCache.userId;
            password = SessionCache.password;
            workplace = SessionCache.workplace;
        }

        if (userId == null || password == null || workplace == null) {
            Toast.makeText(this, "Missing user credentials", Toast.LENGTH_SHORT).show();
            return;
        }

        // Save to SessionCache
        SessionCache.userId = userId;
        SessionCache.password = password;
        SessionCache.workplace = workplace;

        // Fetch initial data
        fetchUserData(userId, password, workplace);
        fetchTotalTopUp(workplace);
    }

    @Override
    protected void onResume() {
        super.onResume();
        helper.markHomeItem();
        if (SessionCache.workplace != null) {
            fetchTotalTopUp(SessionCache.workplace);
        }
    }

    // ------------------- Fetch user info -------------------
    void fetchUserData(String userId, String password, String workplace) {
        String url = "https://sbetshopbd.xyz/api/get_my_user.php"
                + "?user_id=" + userId
                + "&password=" + password
                + "&workplace=" + workplace;

        VollyHelper.getInstance(this).fetchData(url, new VollyHelper.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);

                    if ("success".equals(jsonObject.optString("status"))) {
                        JSONObject user = jsonObject.getJSONObject("user");

                        String balance = user.optString("balance", "0");
                        String userAddress = user.optString("address", "N/A");
                        String workplaceName = user.optString("workplace", "N/A");

                        tvBalance.setText("Balance: " + balance + "৳");
                        address.setText(userAddress);
                        epose.setText("EPOS: " + workplaceName);
                        SessionCache.balance = balance;

                        try {
                            helper.setMaxAmount(Float.parseFloat(balance));
                        } catch (Exception ignored) {}

                    } else {
                        tvBalance.setText("Balance not found");
                        address.setText("Address not found");
                        epose.setText("Workplace not found");
                        tvAmount.setText("Total Top-Up not found");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {}
        });
    }

    // ------------------- Fetch total top-up -------------------
    void fetchTotalTopUp(String workplace) {
        String url = "https://sbetshopbd.xyz/api/get_total_topup.php?workplace=" + workplace;

        VollyHelper.getInstance(this).fetchData(url, new VollyHelper.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if ("success".equals(jsonObject.optString("status"))) {
                        String totalTopUp = jsonObject.optString("total_top_up", "0");

                        try {
                            helper.refreshAmount(Float.parseFloat(totalTopUp));
                        } catch (Exception ignored) {
                            tvAmount.setText(totalTopUp + "৳");
                        }

                    } else {
                        tvAmount.setText("Total Top-Up not found");
                    }
                } catch (Exception e) {
                    tvAmount.setText("Parsing error");
                }
            }

            @Override
            public void onError(String error) {
                tvAmount.setText("Network error");
            }
        });
    }
}
