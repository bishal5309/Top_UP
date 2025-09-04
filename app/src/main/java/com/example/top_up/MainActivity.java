package com.example.top_up;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
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
                showCustomDialog("ERROR", "Please fill in all fields");
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
                                    JSONObject userData = jsonObject.optJSONObject("user");
                                    String userStatus = "active";
                                    if (userData != null) {
                                        userStatus = userData.optString("status", "active");
                                    }

                                    if ("blocked".equalsIgnoreCase(userStatus)) {
                                        showCustomDialog("ERROR", "Epos Access Denied");
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
                                        intent.putExtra("user_id", csid.getText().toString().trim());
                                        intent.putExtra("password", password.getText().toString().trim());
                                        intent.putExtra("workplace", wc.getText().toString().trim());
                                    }

                                    if (checkBoxRemember.isChecked()) {
                                        sharedPreferences.edit()
                                                .putString("csid", csid.getText().toString().trim())
                                                .putString("password", password.getText().toString().trim())
                                                .putString("wc", wc.getText().toString().trim())
                                                .putBoolean("remember", true)
                                                .apply();
                                    } else {
                                        sharedPreferences.edit().clear().apply();
                                    }

                                    startActivity(intent);
                                    finish();

                                } else {
                                    // Handle specific error messages
                                    if ("User not found".equalsIgnoreCase(message)) {
                                        showCustomDialog("Authorize Error", "Invalid username or password!");
                                    } else {
                                        showCustomDialog("ERROR", "Epos Access Denied");
                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                                showCustomDialog("Error", "Parsing error: " + e.getMessage());
                            }
                        }

                        @Override
                        public void onError(String error) {
                            // Network error → show toast only
                            Toast.makeText(MainActivity.this, "Please check your internet connection and try again.", Toast.LENGTH_SHORT).show();
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
                    SharedPreferences.Editor editor = getSharedPreferences("login_prefs", MODE_PRIVATE).edit();

                    if ("success".equals(jsonObject.optString("status"))) {
                        String versionCode = jsonObject.optString("version_code", "N/A");
                        version.setText("Version: " + versionCode);
                        // Save to SharedPreferences
                        editor.putString("version_code", versionCode).apply();
                    } else {
                        showCustomDialog("Version Error", jsonObject.optString("message", "Version not found"));
                        // Optionally save "N/A" if fetch fails
                        editor.putString("version_code", "N/A").apply();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showCustomDialog("Parsing Error", "Version parsing error");
                    getSharedPreferences("login_prefs", MODE_PRIVATE)
                            .edit().putString("version_code", "N/A").apply();
                }
            }

            @Override
            public void onError(String error) {
                // Network error → show toast only, but keep last saved version
                Log.e("VERSION_ERROR", error);
                Toast.makeText(MainActivity.this, "Please check your internet connection and try again.", Toast.LENGTH_SHORT).show();

                // Load last saved version from SharedPreferences (optional)
                String savedVersion = getSharedPreferences("login_prefs", MODE_PRIVATE)
                        .getString("version_code", "N/A");
                version.setText("Version: " + savedVersion);
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

    // ======= THEME-AWARE CUSTOM DIALOG =======
    private void showCustomDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Create a custom title TextView
        TextView titleView = new TextView(this);
        titleView.setText(title);
        titleView.setTextSize(20);
        titleView.setPadding(65, 40, 50, 20);
        boolean isDarkMode = (getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
        titleView.setTextColor(isDarkMode ? Color.WHITE : Color.BLACK);

        builder.setCustomTitle(titleView);
        builder.setMessage(message);
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();

        // Set background based on theme
        Window window = dialog.getWindow();
        if (window != null) {
            String backgroundColor = isDarkMode ? "#1d334a" : "#FFFFFF";
            window.setBackgroundDrawable(new ColorDrawable(Color.parseColor(backgroundColor)));
        }

        dialog.show();

        // Set message color
        TextView messageView = dialog.findViewById(android.R.id.message);
        if (messageView != null) {
            messageView.setTextColor(isDarkMode ? Color.parseColor("#ECEFF1") : Color.parseColor("#212121"));
        }

        // Set button color
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(isDarkMode ? Color.parseColor("#80D8FF") : Color.parseColor("#1976D2"));
    }


    private void styleDialog(AlertDialog dialog) {
        boolean isDarkMode = (getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;

        // Title color: white in dark mode, black in light mode
        int titleColor = isDarkMode ? Color.WHITE : Color.BLACK;

        // Message color: light gray in dark mode, dark gray in light mode
        int messageColor = isDarkMode ? Color.parseColor("#ECEFF1") : Color.parseColor("#212121");

        // Button color: cyan in dark mode, deep blue in light mode
        int buttonTextColor = isDarkMode ? Color.parseColor("#80D8FF") : Color.parseColor("#1976D2");

        // Set message color
        TextView textViewMessage = dialog.findViewById(android.R.id.message);
        if (textViewMessage != null) {
            textViewMessage.setTextColor(messageColor);
        }

        // Set title color
        int titleId = getResources().getIdentifier("alertTitle", "id", "android");
        TextView textViewTitle = dialog.findViewById(titleId);
        if (textViewTitle != null) {
            textViewTitle.setTextColor(titleColor);
        }

        // Set positive button color
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(buttonTextColor);
    }

}