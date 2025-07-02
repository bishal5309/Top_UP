package com.example.top_up;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class home_page extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView tvAmount;
    private ImageButton btnRefresh;
    private FrameLayout progressContainer;
    private View whiteOverlay;

    private final float maxAmount = 100f;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView menuIcon;

    CardView exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);

        // Drawer Initialization
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.nav_view);
        menuIcon = findViewById(R.id.menu_icon);

        navigationView.setNavigationItemSelectedListener(this);

        // Drawer toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // DrawerListener দিয়ে drawer open হলে home menu সিলেক্ট করা
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {}

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                Menu menu = navigationView.getMenu();
                MenuItem homeItem = menu.findItem(R.id.nav_home);
                if (homeItem != null) {
                    homeItem.setChecked(true);
                }
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {}

            @Override
            public void onDrawerStateChanged(int newState) {}
        });

        // Menu icon click → drawer open
        menuIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Progress and Amount views
        tvAmount = findViewById(R.id.tvAmount);
        btnRefresh = findViewById(R.id.btnRefresh);
        progressContainer = findViewById(R.id.progressContainer);
        whiteOverlay = findViewById(R.id.whiteOverlay);

        btnRefresh.setOnClickListener(v -> {
            btnRefresh.animate().rotationBy(360f).setDuration(600).start();

            float newAmount = (float) (Math.random() * maxAmount);
            tvAmount.setText(String.format("%.2f ৳", newAmount));

            whiteOverlay.post(() -> {
                int containerWidth = progressContainer.getWidth();
                int overlayWidth = (int) (containerWidth * (newAmount / maxAmount));

                ViewGroup.LayoutParams params = whiteOverlay.getLayoutParams();
                params.width = overlayWidth;
                whiteOverlay.setLayoutParams(params);
            });
        });

        exit = findViewById(R.id.exit);
        exit.setOnClickListener(v -> new AlertDialog.Builder(home_page.this)
                .setTitle("Exit App")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", (dialog, which) -> finishAffinity())
                .setNegativeButton("No", null)
                .show());

        // Drawer menu তে home সিলেক্টেড দেখাবে
        navigationView.setCheckedItem(R.id.nav_home);

        navigationView.post(() -> {
            MenuItem darkItem = navigationView.getMenu().findItem(R.id.nav_dark);
            View switchLayout = getLayoutInflater().inflate(R.layout.switch_item_layout, null);
            SwitchCompat themeSwitch = switchLayout.findViewById(R.id.theme_switch);

            // SharedPreferences init
            SharedPreferences prefs = home_page.this.getSharedPreferences("theme_prefs", MODE_PRIVATE);
            boolean savedState = prefs.getBoolean("is_dark_switch_on", false); // default OFF

            // Restore previous state
            themeSwitch.setChecked(savedState);

            // On/Off logic
            themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("is_dark_switch_on", isChecked);
                editor.apply();
            });

            darkItem.setActionView(switchLayout);

            // Optional styling
            ColorStateList trackColor = new ColorStateList(
                    new int[][]{
                            new int[]{android.R.attr.state_checked},
                            new int[]{-android.R.attr.state_checked}
                    },
                    new int[]{
                            ContextCompat.getColor(home_page.this, R.color.blue_500),
                            ContextCompat.getColor(home_page.this, R.color.gray_400)
                    }
            );

            ColorStateList thumbColor = new ColorStateList(
                    new int[][]{
                            new int[]{android.R.attr.state_checked},
                            new int[]{-android.R.attr.state_checked}
                    },
                    new int[]{
                            ContextCompat.getColor(home_page.this, R.color.white),
                            ContextCompat.getColor(home_page.this, R.color.white)
                    }
            );

            themeSwitch.setThumbTintList(thumbColor);
            themeSwitch.setTrackTintList(trackColor);
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            navigationView.setCheckedItem(R.id.nav_home);
        } else if (id == R.id.nav_profile) {
            navigationView.setCheckedItem(R.id.nav_profile);
            // startActivity(new Intent(this, ProfileActivity.class));
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
