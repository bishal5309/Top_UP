package com.example.top_up;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.checkbox.MaterialCheckBox;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    EditText csid, password, wc;
    CardView login;
    MaterialCheckBox checkBoxRemember;

    SharedPreferences sharedPreferences;
    private TextView version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this); // Apply theme
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        csid = findViewById(R.id.csid);
        password = findViewById(R.id.password);
        wc = findViewById(R.id.workplace);
        login = findViewById(R.id.login);
        checkBoxRemember = findViewById(R.id.checkBoxRemember);
        version = findViewById(R.id.version);

        sharedPreferences = getSharedPreferences("login_prefs", MODE_PRIVATE);
        loadSavedData();

        fetchVersionCode(); // Fetch latest app version

        login.setOnClickListener(v -> {
            String inputCustomerId = csid.getText().toString().trim();
            String inputPassword = password.getText().toString().trim();
            String inputWorkplace = wc.getText().toString().trim();

            if (inputCustomerId.isEmpty() || inputPassword.isEmpty() || inputWorkplace.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            String url = "https://sbetshopbd.xyz/api/login.php";

            VollyHelper.getInstance(MainActivity.this).loginUser(
                    url,
                    inputCustomerId,
                    inputPassword,
                    inputWorkplace,
                    new VollyHelper.VolleyCallback() {
                        @Override
                        public void onSuccess(String result) {
                            Log.d("LOGIN_RESPONSE", result);
                            try {
                                JSONObject jsonObject = new JSONObject(result);
                                String status = jsonObject.optString("status", "error");
                                String message = jsonObject.optString("message", "Unknown error");

                                if ("success".equals(status)) {
                                    // Check user data & status
                                    JSONObject userData = jsonObject.optJSONObject("user");
                                    String userStatus = "active"; // default
                                    if (userData != null) {
                                        userStatus = userData.optString("status", "active");
                                    }

                                    if ("blocked".equalsIgnoreCase(userStatus)) {
                                        new AlertDialog.Builder(MainActivity.this)
                                                .setTitle("Blocked")
                                                .setMessage("Your account is blocked. Contact admin.")
                                                .setPositiveButton("OK", (d, w) -> password.setText(""))
                                                .show();
                                        return;
                                    }

                                    // Normal login flow
                                    Intent intent = new Intent(MainActivity.this, home_page.class);
                                    if (userData != null) {
                                        intent.putExtra("user_id", userData.optString("user_id"));
                                        intent.putExtra("password", userData.optString("password"));
                                        intent.putExtra("workplace", userData.optString("workplace"));
                                        intent.putExtra("balance", userData.optString("balance"));
                                        intent.putExtra("address", userData.optString("address"));
                                    } else {
                                        intent.putExtra("user_id", inputCustomerId);
                                        intent.putExtra("password", inputPassword);
                                        intent.putExtra("workplace", inputWorkplace);
                                    }

                                    // Remember login if checked
                                    if (checkBoxRemember.isChecked()) {
                                        sharedPreferences.edit()
                                                .putString("csid", inputCustomerId)
                                                .putString("password", inputPassword)
                                                .putString("wc", inputWorkplace)
                                                .putBoolean("remember", true)
                                                .apply();
                                    } else {
                                        sharedPreferences.edit().clear().apply();
                                    }

                                    startActivity(intent);
                                    finish();

                                } else {
                                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(MainActivity.this, "Parsing error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(String error) {
                            Toast.makeText(MainActivity.this, "Network error: " + error, Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        });
    }

    private void fetchVersionCode() {
        String url = "https://sbetshopbd.xyz/api/get_version.php";

        VollyHelper.getInstance(this).fetchData(url, new VollyHelper.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                Log.d("VERSION_RESPONSE", result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if ("success".equals(jsonObject.optString("status"))) {
                        String versionCode = jsonObject.optString("version_code", "N/A");
                        version.setText("Version: " + versionCode);
                    } else {
                        Toast.makeText(MainActivity.this,
                                jsonObject.optString("message", "Version not found"),
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Parsing error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                Log.e("VERSION_ERROR", error);
                Toast.makeText(MainActivity.this, "Network error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadSavedData() {
        boolean isRemembered = sharedPreferences.getBoolean("remember", false);
        if (isRemembered) {
            csid.setText(sharedPreferences.getString("csid", ""));
            password.setText(sharedPreferences.getString("password", ""));
            wc.setText(sharedPreferences.getString("wc", ""));
            checkBoxRemember.setChecked(true);
        }
    }
}
