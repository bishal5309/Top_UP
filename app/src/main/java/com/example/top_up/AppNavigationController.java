package com.example.top_up;

import android.app.Activity;
import android.content.Intent;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class AppNavigationController {

    private Activity activity;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    public AppNavigationController(Activity activity, DrawerLayout drawerLayout, NavigationView navigationView) {
        this.activity = activity;
        this.drawerLayout = drawerLayout;
        this.navigationView = navigationView;

        setupNavigation();
    }

    private void setupNavigation() {
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            // Home Activity
            if (id == R.id.nav_home) {
                if (!(activity instanceof home_page)) {
                    activity.startActivity(new Intent(activity, home_page.class));
                    activity.finish();
                }
            }
            // Partner Check Activity
            else if (id == R.id.nav_partner_check) {
                if (!(activity instanceof PartnerCheckActivity)) {
                    activity.startActivity(new Intent(activity, PartnerCheckActivity.class));
                    activity.finish();
                }
            }
            // Example for future activities
            /* else if (id == R.id.nav_profile) {
                if (!(activity instanceof ProfileActivity)) {
                    activity.startActivity(new Intent(activity, ProfileActivity.class));
                    activity.finish();
                }
            } */

            // Close drawer after click
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
    }

    // Method to mark current activity's menu item as checked
    public void markCurrentItem(int menuItemId) {
        navigationView.setCheckedItem(menuItemId);
    }

    // Optional: open drawer programmatically
    public void openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public void closeDrawer() {
        drawerLayout.closeDrawer(GravityCompat.START);
    }
}
