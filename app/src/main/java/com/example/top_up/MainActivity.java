package com.example.top_up;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.checkbox.MaterialCheckBox;

public class MainActivity extends AppCompatActivity {

    EditText csid, password, wc;
    CardView login;
    MaterialCheckBox checkBoxRemember;

    String correctCustomerId = "cs123";
    String correctPassword = "pass123";
    String correctWorkCode = "wc123";

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        wc = findViewById(R.id.wc);
        login = findViewById(R.id.login);
        checkBoxRemember = findViewById(R.id.checkBoxRemember);

        // 🔵 Preferences init
        sharedPreferences = getSharedPreferences("login_prefs", MODE_PRIVATE);
        loadSavedData();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputCustomerId = csid.getText().toString().trim();
                String inputPassword = password.getText().toString().trim();
                String inputWorkCode = wc.getText().toString().trim();

                if (inputCustomerId.isEmpty() || inputPassword.isEmpty() || inputWorkCode.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                } else if (inputCustomerId.equals(correctCustomerId) &&
                        inputPassword.equals(correctPassword) &&
                        inputWorkCode.equals(correctWorkCode)) {

                    // লগিন সফল: শুধু তখনই remember কাজ করবে
                    if (checkBoxRemember.isChecked()) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("csid", inputCustomerId);
                        editor.putString("password", inputPassword);
                        editor.putString("wc", inputWorkCode);
                        editor.putBoolean("remember", true);
                        editor.apply();
                    } else {
                        // টিক না থাকলে ক্লিয়ার করো
                        sharedPreferences.edit().clear().apply();
                    }

                    Intent intent = new Intent(MainActivity.this, home_page.class);
                    intent.putExtra("customer_id", inputCustomerId);
                    startActivity(intent);
                    finish();

                } else {
                    // লগিন ভুল হলে ডেটা ক্লিয়ার করো যাতে ভুল ইনপুট থেকে আগের ডেটা না থাকে
                    sharedPreferences.edit().clear().apply();

                    Toast.makeText(MainActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    // 🟢 Load data if remember was checked
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
