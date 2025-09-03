package com.example.top_up;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;

import org.json.JSONObject;

public class home_page extends AppCompatActivity {

    private HomePageHelper helper;
    private TextView epose, tvBalance, address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);

        // Initialize helper
        helper = new HomePageHelper(this);

        // Bind views
        epose = findViewById(R.id.epose);         // workplace
        tvBalance = findViewById(R.id.tvBalance); // balance
        address = findViewById(R.id.address);     // address

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

        // Save to SessionCache for future navigation
        SessionCache.userId = userId;
        SessionCache.password = password;
        SessionCache.workplace = workplace;

        // Fetch user-specific data
        fetchUserData(userId, password, workplace);
    }

    @Override
    protected void onResume() {
        super.onResume();
        helper.markHomeItem();
    }

    private void fetchUserData(String userId, String password, String workplace) {
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
                        epose.setText("EPOS: "+workplaceName);
                    } else {
                        tvBalance.setText("Balance not found");
                        address.setText("Address not found");
                        epose.setText("Workplace not found");
                        Toast.makeText(home_page.this, jsonObject.optString("message", "User not found"), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    tvBalance.setText("Parsing error");
                    address.setText("Parsing error");
                    epose.setText("Parsing error");
                }
            }

            @Override
            public void onError(String error) {
                tvBalance.setText("Network error");
                address.setText("Network error");
                epose.setText("Network error");
                Toast.makeText(home_page.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}