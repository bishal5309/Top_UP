package com.example.top_up;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class NavigationHandler implements NavigationView.OnNavigationItemSelectedListener {

    private final Activity activity;
    private final DrawerLayout drawerLayout;
    private final NavigationView navigationView;

    public NavigationHandler(Activity activity, DrawerLayout drawerLayout, NavigationView navigationView) {
        this.activity = activity;
        this.drawerLayout = drawerLayout;
        this.navigationView = navigationView;

        // ✅ Drawer menu load হওয়ার পর dark theme switch attach করা
        setupDarkThemeSwitch();
    }

    private void setupDarkThemeSwitch() {
        navigationView.post(() -> {
            MenuItem darkItem = navigationView.getMenu().findItem(R.id.nav_dark);
            if (darkItem == null) return;

            View switchLayout = LayoutInflater.from(activity).inflate(R.layout.switch_item_layout, null);
            SwitchCompat themeSwitch = switchLayout.findViewById(R.id.theme_switch);

            // SharedPreferences init
            SharedPreferences prefs = activity.getSharedPreferences("theme_prefs", Activity.MODE_PRIVATE);
            boolean savedState = prefs.getBoolean("is_dark_switch_on", false); // default OFF

            // Restore previous state
            themeSwitch.setChecked(savedState);

            // On/Off logic
            themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("is_dark_switch_on", isChecked);
                editor.apply();

                // এখানেই চাইলে AppCompatDelegate দিয়ে Light/Dark theme switch করতে পারো
                // if (isChecked) {
                //     AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                // } else {
                //     AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                // }
            });

            darkItem.setActionView(switchLayout);

            // Optional styling
            ColorStateList trackColor = new ColorStateList(
                    new int[][]{
                            new int[]{android.R.attr.state_checked},
                            new int[]{-android.R.attr.state_checked}
                    },
                    new int[]{
                            ContextCompat.getColor(activity, R.color.blue_500),
                            ContextCompat.getColor(activity, R.color.gray_400)
                    }
            );

            ColorStateList thumbColor = new ColorStateList(
                    new int[][]{
                            new int[]{android.R.attr.state_checked},
                            new int[]{-android.R.attr.state_checked}
                    },
                    new int[]{
                            ContextCompat.getColor(activity, R.color.white),
                            ContextCompat.getColor(activity, R.color.white)
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
            // ✅ এখন Home item চাপলে সরাসরি home_page Activity open হবে
            activity.startActivity(new Intent(activity, home_page.class));

        } else if (id == R.id.nav_profile) {
            navigationView.setCheckedItem(R.id.nav_profile);
            activity.startActivity(new Intent(activity, NotificationsActivity.class));

        } else if (id == R.id.nav_settings) {
            navigationView.setCheckedItem(R.id.nav_settings);
            activity.startActivity(new Intent(activity, TransactionsActivity.class));

        } else if (id == R.id.nav_partner_check) {
            navigationView.setCheckedItem(R.id.nav_partner_check);
            activity.startActivity(new Intent(activity, PartnerCheckActivity.class));

        } else if (id == R.id.nav_dark) {
            navigationView.setCheckedItem(R.id.nav_dark);
            // Dark theme toggle (handled separately)

        } else if (id == R.id.nav_cst) {
            navigationView.setCheckedItem(R.id.nav_cst);
            activity.startActivity(new Intent(activity, CustomerSupportActivity.class));
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
