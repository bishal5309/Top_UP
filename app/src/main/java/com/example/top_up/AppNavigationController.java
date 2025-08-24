package com.example.top_up;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class AppNavigationController {

    private final Activity activity;
    private final DrawerLayout drawerLayout;
    private final NavigationView navigationView;

    public AppNavigationController(Activity activity, DrawerLayout drawerLayout, NavigationView navigationView) {
        this.activity = activity;
        this.drawerLayout = drawerLayout;
        this.navigationView = navigationView;

        setupNavigation();       // normal navigation
        setupDarkThemeSwitch();  // ✅ dark theme switch
    }

    private void setupNavigation() {
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                if (!(activity instanceof home_page)) {
                    activity.startActivity(new Intent(activity, home_page.class));
                    activity.finish();
                }
            } else if (id == R.id.nav_notification) {
                if (!(activity instanceof CustomerSupportActivity)) {
                    activity.startActivity(new Intent(activity, NotificationsActivity.class));
                    activity.finish();
                }
            } else if (id == R.id.nav_epos) {
                if (!(activity instanceof CustomerSupportActivity)) {
                    activity.startActivity(new Intent(activity, TransactionsActivity.class));
                    activity.finish();
                }
            } else if (id == R.id.nav_partner_check) {
                if (!(activity instanceof PartnerCheckActivity)) {
                    activity.startActivity(new Intent(activity, PartnerCheckActivity.class));
                    activity.finish();
                }
            } else if (id == R.id.nav_cst) {
                if (!(activity instanceof CustomerSupportActivity)) {
                    activity.startActivity(new Intent(activity, CustomerSupportActivity.class));
                    activity.finish();
                }
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    private void setupDarkThemeSwitch() {
        navigationView.post(() -> {
            View actionView = null;
            SwitchCompat themeSwitch = null;

            if (navigationView.getMenu().findItem(R.id.nav_dark) != null) {
                actionView = navigationView.getMenu()
                        .findItem(R.id.nav_dark)
                        .getActionView();

                if (actionView != null) {
                    themeSwitch = actionView.findViewById(R.id.theme_switch);
                }
            }

            if (themeSwitch == null) {
                Log.w("AppNavController", "theme_switch not found");
                return;
            }

            // ✅ Apply custom thumb & track tint
            themeSwitch.setThumbTintList(ContextCompat.getColorStateList(activity, R.color.switch_thumb_color));
            themeSwitch.setTrackTintList(ContextCompat.getColorStateList(activity, R.color.switch_track_color));

            SharedPreferences prefs = activity.getSharedPreferences("theme_prefs", Activity.MODE_PRIVATE);
            boolean savedState = prefs.getBoolean("is_dark_switch_on", false);

            themeSwitch.setChecked(savedState);

            themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                prefs.edit().putBoolean("is_dark_switch_on", isChecked).apply();

                // ✅ Theme logic (optional: uncomment when needed)
                // if (isChecked) {
                //     AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                // } else {
                //     AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                // }
            });
        });
    }

    public void markCurrentItem(int menuItemId) {
        navigationView.setCheckedItem(menuItemId);
    }

    public void openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public void closeDrawer() {
        drawerLayout.closeDrawer(GravityCompat.START);
    }
}