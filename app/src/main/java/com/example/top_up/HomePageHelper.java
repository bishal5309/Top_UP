package com.example.top_up;

import android.content.res.Configuration;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class HomePageHelper {

    private final AppCompatActivity activity;
    private final float maxAmount = 100f;

    // Views
    public DrawerLayout drawerLayout;
    public ImageView menuIcon;
    public androidx.drawerlayout.widget.DrawerLayout getDrawerLayout() {
        return drawerLayout;
    }
    public NavigationView navigationView;
    public TextView tvAmount;
    public ImageButton btnRefresh;
    public FrameLayout progressContainer;
    public View whiteOverlay;
    public LinearLayout btn_withdraw, btn_top_up;
    public CardView exit;
    public AppNavigationController navController;

    public HomePageHelper(AppCompatActivity activity) {
        this.activity = activity;
        setupStatusBar();
        initViews();
        setupNavigation();
        setupListeners();
        handleBackGesture();
    }

    private void setupStatusBar() {
        int nightModeFlags = activity.getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK;

        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            activity.getWindow().setStatusBarColor(android.graphics.Color.parseColor("#112740"));
            activity.getWindow().getDecorView().setSystemUiVisibility(0);
        } else {
            activity.getWindow().setStatusBarColor(android.graphics.Color.parseColor("#FFFFFF"));
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    private void initViews() {
        drawerLayout = activity.findViewById(R.id.drawerLayout);
        navigationView = activity.findViewById(R.id.nav_view);
        menuIcon = activity.findViewById(R.id.menu_icon);

        tvAmount = activity.findViewById(R.id.tvAmount);
        btnRefresh = activity.findViewById(R.id.btnRefresh);
        progressContainer = activity.findViewById(R.id.progressContainer);
        whiteOverlay = activity.findViewById(R.id.whiteOverlay);

        btn_withdraw = activity.findViewById(R.id.btn_withdraw);
        btn_top_up = activity.findViewById(R.id.btn_top_up);
        exit = activity.findViewById(R.id.exit);

        navController = new AppNavigationController(activity, drawerLayout, navigationView);
    }

    private void setupNavigation() {
        menuIcon.setOnClickListener(v -> navController.openDrawer());
    }

    private void setupListeners() {
        btnRefresh.setOnClickListener(v -> refreshAmount());
        btn_top_up.setOnClickListener(v -> DialogHelper.showTopUpDialog(activity));
        btn_withdraw.setOnClickListener(v -> WithdrawDialog2.show(activity));
        exit.setOnClickListener(v -> LogoutDialog.show(activity));
    }

    public void refreshAmount() {
        btnRefresh.animate().rotationBy(360f).setDuration(600).start();
        float newAmount = (float) (Math.random() * maxAmount);
        tvAmount.setText(String.format("%.2f ৳", newAmount));
        whiteOverlay.post(() -> {
            int containerWidth = progressContainer.getWidth();
            int overlayWidth = (int) (containerWidth * (newAmount / maxAmount));
            whiteOverlay.getLayoutParams().width = overlayWidth;
            whiteOverlay.requestLayout();
        });
    }

    private void handleBackGesture() {
        activity.getOnBackPressedDispatcher().addCallback(activity, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    new androidx.appcompat.app.AlertDialog.Builder(activity)
                            .setTitle("Exit App")
                            .setMessage("Do you want to exit the app?")
                            .setPositiveButton("Yes", (dialog, which) -> activity.finishAffinity())
                            .setNegativeButton("No", null)
                            .show();
                }
            }
        });
    }

    // Optional: Call this in onResume() to mark home menu item
    public void markHomeItem() {
        navController.markCurrentItem(R.id.nav_home);
    }
}
