package com.example.top_up;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

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
        setupDarkThemeSwitch();  // dark theme switch inside app
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
                if (!(activity instanceof NotificationsActivity)) {
                    activity.startActivity(new Intent(activity, NotificationsActivity.class));
                    activity.finish();
                }
            } else if (id == R.id.nav_epos) {
                if (!(activity instanceof TransactionsActivity)) {
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

        // ✅ Add badge to Customer Support menu item
        navigationView.post(() -> {
            View actionView = navigationView.getMenu().findItem(R.id.nav_cst).getActionView();
            if (actionView == null) {
                // Badge TextView
                TextView badge = new TextView(activity);
                badge.setText("3");
                badge.setTextColor(activity.getResources().getColor(android.R.color.white));
                badge.setTextSize(14f);
                badge.setGravity(Gravity.CENTER);

                int size = dpToPx(25);
                badge.setWidth(size);
                badge.setHeight(size);

                GradientDrawable bg = new GradientDrawable();
                bg.setColor(0xFF4CAF50);
                bg.setCornerRadius(size / 2f);
                badge.setBackground(bg);

                // FrameLayout container
                FrameLayout frame = new FrameLayout(activity);
                FrameLayout.LayoutParams badgeParams = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                );
                badgeParams.gravity = Gravity.CENTER_VERTICAL | Gravity.START;
                badgeParams.setMargins(dpToPx(0), 0, 65, 0); // Left margin কমানো → badge আরও left
                badge.setLayoutParams(badgeParams);

                frame.addView(badge);

                navigationView.getMenu().findItem(R.id.nav_cst).setActionView(frame);
            }
        });





    }

    private int dpToPx(int dp) {
        float density = activity.getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }


    private void setupDarkThemeSwitch() {
        navigationView.post(() -> {
            View actionView = null;
            SwitchCompat themeSwitch;

            if (navigationView.getMenu().findItem(R.id.nav_dark) != null) {
                actionView = navigationView.getMenu()
                        .findItem(R.id.nav_dark)
                        .getActionView();

                if (actionView != null) {
                    themeSwitch = actionView.findViewById(R.id.theme_switch);
                } else {
                    themeSwitch = null;
                }
            } else {
                themeSwitch = null;
            }

            if (themeSwitch == null) {
                Log.w("AppNavController", "theme_switch not found");
                return;
            }

            // Custom thumb & track tint
            themeSwitch.setThumbTintList(ContextCompat.getColorStateList(activity, R.color.switch_thumb_color));
            themeSwitch.setTrackTintList(ContextCompat.getColorStateList(activity, R.color.switch_track_color));

            // SharedPreferences to remember user choice
            SharedPreferences prefs = activity.getSharedPreferences("theme_prefs", Activity.MODE_PRIVATE);
            boolean isDarkMode = prefs.getBoolean("is_dark_switch_on", false);

            // Apply saved theme (default light)
            if (isDarkMode) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }

            themeSwitch.setChecked(isDarkMode);

            // ✅ Toggle theme when user switches
            themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                prefs.edit().putBoolean("is_dark_switch_on", isChecked).apply();

                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            });

            // ✅ Handle clicks on the whole nav_dark item (text or icon)
            navigationView.getMenu().findItem(R.id.nav_dark).setOnMenuItemClickListener(item -> {
                themeSwitch.toggle(); // simulate switch toggle
                return true; // don't close drawer
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
