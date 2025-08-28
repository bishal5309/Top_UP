package com.example.top_up;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.top_up.views.AnimatedStatusView;

public class activity_splash extends AppCompatActivity {

    private AnimatedStatusView status1, status2, status3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ✅ Theme apply korte hobe sob theke age
        ThemeHelper.applyTheme(this);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Bind views
        status1 = findViewById(R.id.status1);
        status2 = findViewById(R.id.status2);
        status3 = findViewById(R.id.status3);

        // Step 1: Receiving data
        status1.setState(AnimatedStatusView.State.LOADING);
        new Handler().postDelayed(() -> {
            status1.setState(AnimatedStatusView.State.DONE);

            // Step 2: Sending app data
            status2.setState(AnimatedStatusView.State.LOADING);
            new Handler().postDelayed(() -> {
                status2.setState(AnimatedStatusView.State.DONE);

                // Step 3: Preparing to launch
                status3.setState(AnimatedStatusView.State.LOADING);
                new Handler().postDelayed(() -> {
                    status3.setState(AnimatedStatusView.State.DONE);

                    // Launch MainActivity
                    startActivity(new Intent(activity_splash.this, MainActivity.class));
                    finish();

                }, 600); // Step 3 duration

            }, 800); // Step 2 duration

        }, 800); // Step 1 duration
    }
}