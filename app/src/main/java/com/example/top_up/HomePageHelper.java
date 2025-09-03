package com.example.top_up;

import android.graphics.Color;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class HomePageHelper {

    private final AppCompatActivity activity;
    private float maxAmount = 100f; // dynamic maxAmount

    // Views
    public DrawerLayout drawerLayout;
    public ImageView menuIcon;
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
        initViews();
        setupNavigation();
        setupListeners();
        handleBackGesture();
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

    // Refresh overlay and tvAmount proportionally
    public void refreshAmount(float totalTopUp) {
        float newAmount = totalTopUp; // show total top-up
        tvAmount.setText(String.format("%.2f ৳", newAmount));

        whiteOverlay.post(() -> {
            int containerWidth = progressContainer.getWidth();
            int overlayWidth = (int) (containerWidth * (newAmount / maxAmount));
            whiteOverlay.getLayoutParams().width = overlayWidth;
            whiteOverlay.requestLayout();
        });
    }

    // Animate refresh button (only visual)
    public void animateRefreshButton() {
        btnRefresh.animate().rotationBy(360f).setDuration(600).start();
    }

    private void setupNavigation() {
        menuIcon.setOnClickListener(v -> navController.openDrawer());
    }

    private void setupListeners() {
        btn_top_up.setOnClickListener(v ->
                DialogHelper.showTopUpDialog(activity, () -> {
                    if (activity instanceof home_page) {
                        ((home_page) activity).fetchTotalTopUp(SessionCache.workplace);
                    }
                })
        );

        btn_withdraw.setOnClickListener(v -> WithdrawDialog2.show(activity));
        exit.setOnClickListener(v -> LogoutDialog.show(activity));
    }

    public void setMaxAmount(float amount) {
        this.maxAmount = amount;
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

    public void markHomeItem() {
        navController.markCurrentItem(R.id.nav_home);
    }
}
