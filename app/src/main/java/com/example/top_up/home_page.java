package com.example.top_up;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class home_page extends AppCompatActivity {

    private TextView tvAmount;
    private ImageButton btnRefresh;
    private FrameLayout progressContainer;
    private View whiteOverlay;
    private final float maxAmount = 100f;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView menuIcon;

    private CardView exit;
    private AppNavigationController navController;
    private LinearLayout btn_withdraw,btn_top_up;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);

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



        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.nav_view);
        menuIcon = findViewById(R.id.menu_icon);

        navController = new AppNavigationController(this, drawerLayout, navigationView);

        menuIcon.setOnClickListener(v -> navController.openDrawer());

        tvAmount = findViewById(R.id.tvAmount);
        btnRefresh = findViewById(R.id.btnRefresh);
        progressContainer = findViewById(R.id.progressContainer);
        whiteOverlay = findViewById(R.id.whiteOverlay);


        btn_withdraw = findViewById(R.id.btn_withdraw);
        btn_withdraw.setOnClickListener(v -> {
            showWithdrawDialog();
        });



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
        exit.setOnClickListener(v ->
                new AlertDialog.Builder(home_page.this)
                        .setTitle("Log Out")
                        .setIcon(R.drawable.ticket)
                        .setMessage("Are you sure you want to Log out?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            Intent intent = new Intent(home_page.this, MainActivity.class); // Replace with your target activity
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Optional: clears back stack
                            startActivity(intent);
                        })
                        .setNegativeButton("No", null)
                        .show()
        );

        btn_top_up = findViewById(R.id.btn_top_up);
        btn_top_up.setOnClickListener(v -> showTopUpDialog());
        // ✅ Back gesture handling
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    new AlertDialog.Builder(home_page.this)
                            .setTitle("Exit App")
                            .setMessage("Do you want to exit the app?")
                            .setPositiveButton("Yes", (dialog, which) -> finishAffinity())
                            .setNegativeButton("No", null)
                            .show();
                }
            }
        });
    }

    private void showTopUpDialog() {
        Dialog dialog = new Dialog(home_page.this);
        View contentView = LayoutInflater.from(home_page.this).inflate(R.layout.layout_bottom_sheet, null);
        dialog.setContentView(contentView);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.getAttributes().windowAnimations = R.anim.dialog_animation;
        }

        EditText edtUserId = contentView.findViewById(R.id.edit_user_id);
        AppCompatButton btnSearch = contentView.findViewById(R.id.btn_search);
        LinearLayout layoutLoadingArea = contentView.findViewById(R.id.layout_loading_area);
        LinearLayout layoutUserDetails = contentView.findViewById(R.id.layout_user_details);
        layoutUserDetails.setVisibility(View.GONE);

        EditText edtCustomerId = contentView.findViewById(R.id.customer_id_edit);
        TextView txtName = contentView.findViewById(R.id.txt_name);
        EditText edtAmount = contentView.findViewById(R.id.amount_edit);
        AppCompatButton btnOk = contentView.findViewById(R.id.btn_ok);

        btnSearch.setOnClickListener(v -> {
            String userId = edtUserId.getText().toString().trim();
            if (!userId.isEmpty()) {
                btnSearch.setEnabled(false);
                btnSearch.setText("Loading...");
                layoutLoadingArea.setVisibility(View.VISIBLE);
                btnSearch.setVisibility(View.GONE);

                new Handler().postDelayed(() -> {
                    if (userId.equals("123456789")) {
                        layoutUserDetails.setVisibility(View.VISIBLE);
                        edtUserId.setVisibility(View.GONE);
                        layoutLoadingArea.setVisibility(View.GONE);
                        btnSearch.setVisibility(View.GONE);

                        edtCustomerId.setText(userId);
                        txtName.setText("Unknown user");
                        edtAmount.setText("");
                    } else {
                        edtUserId.setError("Invalid User ID");
                        layoutLoadingArea.setVisibility(View.GONE);
                        btnSearch.setText("Search");
                        btnSearch.setEnabled(true);
                        btnSearch.setVisibility(View.VISIBLE);
                    }
                }, 2000);
            } else {
                edtUserId.setError("Please enter User ID");
            }
        });

        btnOk.setOnClickListener(okView -> {
            String customerId = edtCustomerId.getText().toString().trim();
            String amount = edtAmount.getText().toString().trim();

            if (customerId.isEmpty()) {
                edtCustomerId.setError("Customer ID required");
                return;
            }
            if (amount.isEmpty()) {
                edtAmount.setError("Amount required");
                return;
            }

            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            if (getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }

            dialog.dismiss();
            AlertNotification.show(home_page.this, "Submitting: " + customerId + " - Amount: " + amount);
        });

        dialog.show();
    }

    private void showWithdrawDialog() {
        Dialog dialog = new Dialog(home_page.this);
        View contentView = LayoutInflater.from(home_page.this).inflate(R.layout.layout_bottom_sheet2, null);
        dialog.setContentView(contentView);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.getAttributes().windowAnimations = R.anim.dialog_animation; // নিচ থেকে আসবে
        }

        EditText edtCustomerId = contentView.findViewById(R.id.customer_id_edit);
        EditText edtAmount = contentView.findViewById(R.id.amount_edit);
        AppCompatButton btnOk = contentView.findViewById(R.id.btn_ok);

        btnOk.setOnClickListener(okView -> {
            String customerId = edtCustomerId.getText().toString().trim();
            String amount = edtAmount.getText().toString().trim();

            if (customerId.isEmpty()) {
                edtCustomerId.setError("Recipient ID required");
                return;
            }
            if (amount.isEmpty()) {
                edtAmount.setError("Code required");
                return;
            }

            // Keyboard hide
            InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            if (getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }

            dialog.dismiss();
            AlertNotification.show(home_page.this,
                    "Your withdraw address active 72 hours\nRecipient: " + customerId + "\nCode: " + amount);
            Toast.makeText(this, "Your withdraw address active 72 hours", Toast.LENGTH_SHORT).show();
        });

        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        navController.markCurrentItem(R.id.nav_home);
    }
}
