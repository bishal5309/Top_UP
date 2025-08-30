package com.example.top_up;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class NotificationsActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private AppNavigationController navController;
    private ObjectAnimator rotateAnimator;
    private boolean isFetching = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable edge-to-edge display
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notifications);

        // Handle system bar insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

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

        // Drawer and Navigation
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.nav_view);
        navController = new AppNavigationController(this, drawerLayout, navigationView);

        // Tabs and ViewPager2
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);

        NotificationsPagerAdapter adapter = new NotificationsPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Get colors from theme (dark/light aware)
        final int colorSelected = getThemeColor(R.attr.customTextColor);
        final int colorUnselected = getThemeColor(R.attr.customTextColor5);

        // Notification count array, only New tab used, default 0
        final int[] notificationCounts = {0};

        // Set up tabs with custom TextView and badge
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            LinearLayout tabContainer = new LinearLayout(this);
            tabContainer.setOrientation(LinearLayout.HORIZONTAL);
            tabContainer.setGravity(Gravity.CENTER);

            TextView tabText = new TextView(this);
            tabText.setText(position == 0 ? "New" : "Read");
            tabText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14); // bigger text
            tabText.setAllCaps(false);
            tabText.setIncludeFontPadding(false);
            tabContainer.addView(tabText);

            if (position == 0) {
                // Badge for New tab
                TextView badge = new TextView(this);
                badge.setText(String.valueOf(notificationCounts[position]));
                badge.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
                badge.setTextColor(getResources().getColor(android.R.color.white));
                badge.setGravity(Gravity.CENTER);
                badge.setMinWidth(dpToPx(20));
                badge.setHeight(dpToPx(20));

                GradientDrawable bg = new GradientDrawable();
                bg.setColor(getThemeColor(R.attr.customTextColor5));
                bg.setCornerRadius(dpToPx(10));
                badge.setBackground(bg);

                LinearLayout.LayoutParams badgeParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                badgeParams.leftMargin = dpToPx(4);
                badge.setLayoutParams(badgeParams);

                tabContainer.addView(badge);
            }

            tab.setCustomView(tabContainer);

            // Default color: first tab selected
            if (position == 0) {
                tabText.setTextColor(colorSelected);
            } else {
                tabText.setTextColor(colorUnselected);
            }

        }).attach();

        // Listener to dynamically handle selected/unselected tab colors
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                updateTabColor(tab, true, colorSelected, colorUnselected);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                updateTabColor(tab, false, colorSelected, colorUnselected);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                updateTabColor(tab, true, colorSelected, colorUnselected);
            }
        });

        // Drawer menu icon click
        findViewById(R.id.menu_icon).setOnClickListener(v -> navController.openDrawer());

        // Refresh icon click
        findViewById(R.id.refreshIcon).setOnClickListener(view -> {
            if (!isFetching) {
                rotateAnimator = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f);
                rotateAnimator.setDuration(1000);
                rotateAnimator.setRepeatCount(ObjectAnimator.INFINITE);
                rotateAnimator.start();
                isFetching = true;
                fetchNotificationsFromServer();
            }
        });

        // Clock icon click: show period dialog
        findViewById(R.id.clockIcon).setOnClickListener(view -> showBottomPeriodDialog());

        // Handle back gesture for drawer
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
            }
        });
    }

    // Update tab text color dynamically
    private void updateTabColor(TabLayout.Tab tab, boolean isSelected, int colorSelected, int colorUnselected) {
        if (tab == null || tab.getCustomView() == null) return;
        LinearLayout container = (LinearLayout) tab.getCustomView();
        if (container.getChildCount() > 0) {
            TextView tabText = (TextView) container.getChildAt(0);
            tabText.setTextColor(isSelected ? colorSelected : colorUnselected);
        }
    }

    // Helper: dp to px conversion
    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }

    // Period selection dialog with calendar show/hide
    private void showBottomPeriodDialog() {
        Dialog dialog = new Dialog(this);
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_select_period, null);
        dialog.setContentView(contentView);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.getAttributes().windowAnimations = R.anim.dialog_animation;
        }

        TextView day = contentView.findViewById(R.id.dayOption);
        TextView week = contentView.findViewById(R.id.weekOption);
        TextView month = contentView.findViewById(R.id.monthOption);
        TextView period = contentView.findViewById(R.id.periodOption);

        LinearLayout periodOptionsContainer = contentView.findViewById(R.id.periodOptionsContainer);
        LinearLayout calendarContainer = contentView.findViewById(R.id.calendarContainer);

        CalendarView calendarView = contentView.findViewById(R.id.calendarView);
        Button btnNext = contentView.findViewById(R.id.btnNext);

        TextView dateRangeText = findViewById(R.id.dateRangeText);
        LinearLayout dateRangeContainer = findViewById(R.id.dateRangeContainer);

        // Close button
        ImageView closeDateRange = findViewById(R.id.closeDateRange);
        closeDateRange.setOnClickListener(v -> dateRangeContainer.setVisibility(View.GONE));

        // Day click → today
        day.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
            String range = sdf.format(cal.getTime());
            dateRangeText.setText(range);
            dateRangeContainer.setVisibility(View.VISIBLE);
            dialog.dismiss();
        });

        // Week click → current week
        week.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
            String start = sdf.format(cal.getTime());
            cal.add(Calendar.DAY_OF_WEEK, 6); // 7-day week
            String end = sdf.format(cal.getTime());
            String range = start + " - " + end;
            dateRangeText.setText(range);
            dateRangeContainer.setVisibility(View.VISIBLE);
            dialog.dismiss();
        });

        // Month click → current month
        month.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_MONTH, 1);
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
            String start = sdf.format(cal.getTime());
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            String end = sdf.format(cal.getTime());
            String range = start + " - " + end;
            dateRangeText.setText(range);
            dateRangeContainer.setVisibility(View.VISIBLE);
            dialog.dismiss();
        });

        // Period click → show calendar
        period.setOnClickListener(v -> {
            periodOptionsContainer.setVisibility(View.GONE);
            calendarContainer.setVisibility(View.VISIBLE);
        });

        // Next button → calendar date selection
        btnNext.setOnClickListener(v -> {
            long selectedDate = calendarView.getDate();
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(selectedDate);
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault());
            String startDate = sdf.format(cal.getTime());

            Calendar endCal = (Calendar) cal.clone();
            endCal.add(Calendar.DAY_OF_MONTH, 6); // 7-day range for calendar
            String endDate = sdf.format(endCal.getTime());

            String range = startDate + " - " + endDate;
            dateRangeText.setText(range);
            dateRangeContainer.setVisibility(View.VISIBLE);
            dialog.dismiss();
        });

        dialog.show();
    }


    // Simulate server fetch for notifications
    private void fetchNotificationsFromServer() {
        new android.os.Handler().postDelayed(this::stopRefreshAnimation, 2000);
    }

    // Stop refresh spinner
    private void stopRefreshAnimation() {
        if (rotateAnimator != null && rotateAnimator.isRunning()) {
            rotateAnimator.end();
        }
        isFetching = false;
    }

    // Get color from theme attribute (dark/light aware)
    private int getThemeColor(int attrRes) {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(attrRes, typedValue, true);
        return typedValue.data;
    }

    @Override
    protected void onResume() {
        super.onResume();
        navController.markCurrentItem(R.id.nav_notification);
    }
}
