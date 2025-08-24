package com.example.top_up;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import android.animation.ObjectAnimator;

public class NotificationsActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private AppNavigationController navController;
    private ImageView menuIcon,refreshIcon,clockIcon;
    private ObjectAnimator rotateAnimator;
    private boolean isFetching = false; // track server request status
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notifications);

        // ✅ Edge-to-edge handling
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // ✅ Status bar color for dark/light mode
        int nightModeFlags = getResources().getConfiguration().uiMode &
                android.content.res.Configuration.UI_MODE_NIGHT_MASK;

        if (nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
            getWindow().setStatusBarColor(Color.BLACK);
            getWindow().getDecorView().setSystemUiVisibility(0);
        } else {
            getWindow().setStatusBarColor(Color.WHITE);
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        // ✅ Drawer & Navigation
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.nav_view);
        menuIcon = findViewById(R.id.menu_icon);
        clockIcon = findViewById(R.id.clockIcon);
        refreshIcon = findViewById(R.id.refreshIcon);




        refreshIcon.setOnClickListener(view -> {
            if (!isFetching) {
                // ✅ Start spinner
                rotateAnimator = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f);
                rotateAnimator.setDuration(1000);
                rotateAnimator.setRepeatCount(ObjectAnimator.INFINITE);
                rotateAnimator.start();

                isFetching = true;

                // ✅ Simulate fetching notifications from server
                fetchNotificationsFromServer();
            } else {
                // Already fetching, ignore click or show a message
            }
        });


        clockIcon.setOnClickListener(view -> {

        });





        navController = new AppNavigationController(this, drawerLayout, navigationView);

        if (menuIcon != null) {
            menuIcon.setOnClickListener(v -> navController.openDrawer());
        }

        // ✅ Tabs + ViewPager
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);

        NotificationsPagerAdapter adapter = new NotificationsPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(position == 0 ? "New" : "Read");
        }).attach();

        // ✅ Back gesture for drawer
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                }
            }
        });
    }
    // Example server fetch simulation
    private void fetchNotificationsFromServer() {
        // Here you call your server API asynchronously
        // For demo, we'll just simulate delay with Handler
        new android.os.Handler().postDelayed(() -> {
            // Server response received → stop spinner
            stopRefreshAnimation();

            // TODO: Update notifications UI here

        }, 2000); // simulate 2 seconds server delay
    }

    private void stopRefreshAnimation() {
        if (rotateAnimator != null && rotateAnimator.isRunning()) {
            rotateAnimator.end(); // stop rotation
        }
        isFetching = false; // reset fetching status
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Highlight current menu item in navigation drawer
        navController.markCurrentItem(R.id.nav_notification); // ensure this ID exists in drawer_menu.xml
    }
}
