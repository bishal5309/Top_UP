package com.example.top_up;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;

public class home_page extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private TextView tvAmount;
    private ImageButton btnRefresh;
    private FrameLayout progressContainer;
    private View whiteOverlay;

    private final float maxAmount = 100f;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView menuIcon;

    CardView exit;
    AppCompatButton btn_top_up;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);

        // Drawer Initialization
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.nav_view);
        menuIcon = findViewById(R.id.menu_icon);

        navigationView.setNavigationItemSelectedListener(this);

        // Drawer toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, R.string.open_drawer, R.string.close_drawer);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // DrawerListener দিয়ে drawer open হলে home menu সিলেক্ট করা
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {}

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                Menu menu = navigationView.getMenu();
                MenuItem homeItem = menu.findItem(R.id.nav_home);
                if (homeItem != null) {
                    homeItem.setChecked(true);
                }
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {}

            @Override
            public void onDrawerStateChanged(int newState) {}
        });

        // Menu icon click → drawer open
        menuIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Progress and Amount views
        tvAmount = findViewById(R.id.tvAmount);
        btnRefresh = findViewById(R.id.btnRefresh);
        progressContainer = findViewById(R.id.progressContainer);
        whiteOverlay = findViewById(R.id.whiteOverlay);

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
        exit.setOnClickListener(v -> new AlertDialog.Builder(home_page.this)
                .setTitle("Exit App")
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", (dialog, which) -> finishAffinity())
                .setNegativeButton("No", null)
                .show());

        // Drawer menu তে home সিলেক্টেড দেখাবে
        navigationView.setCheckedItem(R.id.nav_home);

        navigationView.post(() -> {
            MenuItem darkItem = navigationView.getMenu().findItem(R.id.nav_dark);
            View switchLayout = getLayoutInflater().inflate(R.layout.switch_item_layout, null);
            SwitchCompat themeSwitch = switchLayout.findViewById(R.id.theme_switch);

            // SharedPreferences init
            SharedPreferences prefs = home_page.this.getSharedPreferences("theme_prefs", MODE_PRIVATE);
            boolean savedState = prefs.getBoolean("is_dark_switch_on", false); // default OFF

            // Restore previous state
            themeSwitch.setChecked(savedState);

            // On/Off logic
            themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("is_dark_switch_on", isChecked);
                editor.apply();
            });

            darkItem.setActionView(switchLayout);

            // Optional styling
            ColorStateList trackColor = new ColorStateList(
                    new int[][]{
                            new int[]{android.R.attr.state_checked},
                            new int[]{-android.R.attr.state_checked}
                    },
                    new int[]{
                            ContextCompat.getColor(home_page.this, R.color.blue_500),
                            ContextCompat.getColor(home_page.this, R.color.gray_400)
                    }
            );

            ColorStateList thumbColor = new ColorStateList(
                    new int[][]{
                            new int[]{android.R.attr.state_checked},
                            new int[]{-android.R.attr.state_checked}
                    },
                    new int[]{
                            ContextCompat.getColor(home_page.this, R.color.white),
                            ContextCompat.getColor(home_page.this, R.color.white)
                    }
            );

            themeSwitch.setThumbTintList(thumbColor);
            themeSwitch.setTrackTintList(trackColor);
        });



        //top up function code========================================================================
        btn_top_up = findViewById(R.id.btn_top_up);
        btn_top_up.setOnClickListener(view -> {
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

            // Access UI components from layout_bottom_sheet.xml
            EditText edtUserId = contentView.findViewById(R.id.edit_user_id);
            MaterialButton btnSearch = contentView.findViewById(R.id.btn_search);
            LinearLayout layoutLoadingArea = contentView.findViewById(R.id.layout_loading_area);
            ProgressBar progressBar = contentView.findViewById(R.id.loading_spinner);
            TextView loadingText = contentView.findViewById(R.id.loading_text);

            // 2nd hidden layout
            LinearLayout layoutUserDetails = contentView.findViewById(R.id.layout_user_details); // Add ID in XML for this layout
            layoutUserDetails.setVisibility(View.GONE); // initially gone

            // Access fields from 2nd layout
            EditText edtCustomerId = contentView.findViewById(R.id.customer_id_edit); // add id in xml: android:id="@+id/customer_id_edit"
            TextView txtName = contentView.findViewById(R.id.txt_name); // add id in xml: android:id="@+id/txt_name"
            EditText edtAmount = contentView.findViewById(R.id.amount_edit); // add id in xml: android:id="@+id/amount_edit"
            MaterialButton btnOk = contentView.findViewById(R.id.btn_ok);

            btnSearch.setOnClickListener(v -> {
                String userId = edtUserId.getText().toString().trim();

                if (!userId.isEmpty()) {
                    btnSearch.setEnabled(false);
                    btnSearch.setText("Loading...");
                    layoutLoadingArea.setVisibility(View.VISIBLE);
                    btnSearch.setVisibility(View.GONE);

                    new Handler().postDelayed(() -> {
                        if (userId.equals("123456789")) {
                            // Show 2nd layout
                            layoutUserDetails.setVisibility(View.VISIBLE);

                            // Hide input and loading section
                            edtUserId.setVisibility(View.GONE);
                            layoutLoadingArea.setVisibility(View.GONE);
                            btnSearch.setVisibility(View.GONE);

                            // Set sample data (You can load from server here)=========================
                            edtCustomerId.setText(userId);
                            txtName.setText("M. Md Shoel Rana");
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

            // OK button access নিচ্ছো এখানে ====================================================
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

                // ✅ Hide keyboard before closing the form
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                if (getCurrentFocus() != null) {
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }

                // ✅ Close the form dialog
                dialog.dismiss();

                // ✅ Show bottom alert with custom message
                AlertNotification.show(home_page.this, "Submitting: " + customerId + " - Amount: " + amount);
            });
            dialog.show();
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            navigationView.setCheckedItem(R.id.nav_home);
        } else if (id == R.id.nav_profile) {
            navigationView.setCheckedItem(R.id.nav_profile);
            // startActivity(new Intent(this, ProfileActivity.class));
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
