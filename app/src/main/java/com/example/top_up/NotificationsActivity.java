package com.example.top_up;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import android.animation.ObjectAnimator;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

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

        int nightModeFlags =
                getResources().getConfiguration().uiMode &
                        android.content.res.Configuration.UI_MODE_NIGHT_MASK;

        if (nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
            // Dark theme
            getWindow().setStatusBarColor(Color.parseColor("#112740")); // Dark Gray/Black
            getWindow().getDecorView().setSystemUiVisibility(0); // হোয়াইট আইকন
        } else {
            // Light theme
            getWindow().setStatusBarColor(Color.parseColor("#FFFFFF")); // হালকা কাস্টম হোয়াইট
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR); // কালো আইকন
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
            showBottomPeriodDialog();
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




    private void showBottomPeriodDialog() {
        Dialog dialog = new Dialog(NotificationsActivity.this);
        View contentView = LayoutInflater.from(NotificationsActivity.this).inflate(R.layout.dialog_select_period, null);
        dialog.setContentView(contentView);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.getAttributes().windowAnimations = R.anim.dialog_animation;
        }

        // Period options
        TextView day = contentView.findViewById(R.id.dayOption);
        TextView week = contentView.findViewById(R.id.weekOption);
        TextView month = contentView.findViewById(R.id.monthOption);
        TextView period = contentView.findViewById(R.id.periodOption);

        // Containers
        LinearLayout periodOptionsContainer = contentView.findViewById(R.id.periodOptionsContainer);
        LinearLayout calendarContainer = contentView.findViewById(R.id.calendarContainer);

        // CalendarView and Next button
        CalendarView calendarView = contentView.findViewById(R.id.calendarView);
        Button btnNext = contentView.findViewById(R.id.btnNext);

        View.OnClickListener listener = v -> {
            String selected = ((TextView) v).getText().toString();
            Toast.makeText(NotificationsActivity.this, "Selected: " + selected, Toast.LENGTH_SHORT).show();

            // Hide options and show calendar
            periodOptionsContainer.setVisibility(View.GONE);
            calendarContainer.setVisibility(View.VISIBLE);
        };

        day.setOnClickListener(listener);
        week.setOnClickListener(listener);
        month.setOnClickListener(listener);
        period.setOnClickListener(listener);

        ImageView closeDateRange = findViewById(R.id.closeDateRange);
        closeDateRange.setOnClickListener(v -> {
            findViewById(R.id.dateRangeContainer).setVisibility(View.GONE);
        });

        // Optional: handle calendar date selection or Next button
        btnNext.setOnClickListener(v -> {
            long selectedDate = calendarView.getDate();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(selectedDate);

            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
            String startDate = sdf.format(calendar.getTime());

            Calendar endCalendar = (Calendar) calendar.clone();
            endCalendar.add(Calendar.DAY_OF_MONTH, 6); // 7-day range
            String endDate = sdf.format(endCalendar.getTime());

            String rangeText = startDate + " - " + endDate;

            TextView dateRangeText = findViewById(R.id.dateRangeText);
            LinearLayout dateRangeContainer = findViewById(R.id.dateRangeContainer);
            dateRangeText.setText(rangeText);
            dateRangeContainer.setVisibility(View.VISIBLE);

            dialog.dismiss();
        });

        dialog.show();
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
