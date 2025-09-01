package com.example.top_up;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class PartnerCheckActivity extends AppCompatActivity {

    private AppCompatButton btn_send;
    private EditText edit_user_id;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView menuIcon;

    private AppNavigationController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_partner_check);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Dark / Light mode for status bar
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


        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.nav_view);
        menuIcon = findViewById(R.id.menu_icon);

        navController = new AppNavigationController(this, drawerLayout, navigationView);

        menuIcon.setOnClickListener(v -> navController.openDrawer());

        btn_send = findViewById(R.id.btn_send);
        edit_user_id = findViewById(R.id.edit_user_id);

        btn_send.setOnClickListener(view -> {
            String userId = edit_user_id.getText().toString().trim();
            if (userId.isEmpty()) {
                edit_user_id.setError("Please enter Player ID");
            }

            edit_user_id.setText("");
            Toast.makeText(this, "Partner Check not Enabled", Toast.LENGTH_SHORT).show();
        });

        // ✅ Back gesture handling
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        navController.markCurrentItem(R.id.nav_partner_check);
    }
}
