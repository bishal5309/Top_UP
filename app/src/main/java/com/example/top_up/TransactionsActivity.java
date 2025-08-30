package com.example.top_up;

import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class TransactionsActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView menuIcon;
    private AppNavigationController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_transactions);

        // Handle system bar insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Dark / Light mode for status bar
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

        // Initialize views
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.nav_view);
        menuIcon = findViewById(R.id.menu_icon);

        navController = new AppNavigationController(this, drawerLayout, navigationView);
        menuIcon.setOnClickListener(v -> navController.openDrawer());

        // ViewPager2 Adapter
        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @Override
            public int getItemCount() {
                return 3;
            }

            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 0: return new CurrentSessionFragment();
                    case 1: return new PreviousSessionFragment();
                    case 2: return new PeriodFragment();
                    default: return new CurrentSessionFragment();
                }
            }
        });

        // Tab colors
        final int colorSelected = getThemeColor(R.attr.customTextColor);
        final int colorUnselected = getThemeColor(R.attr.customTextColor5);

        // TabLayoutMediator with custom TextView for each tab
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            LinearLayout tabContainer = new LinearLayout(this);
            tabContainer.setOrientation(LinearLayout.HORIZONTAL);
            tabContainer.setGravity(Gravity.CENTER);

            TextView tabText = new TextView(this);
            tabText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            tabText.setAllCaps(false);
            tabText.setIncludeFontPadding(false);

            // Set tab text
            switch (position) {
                case 0: tabText.setText("Current session"); break;
                case 1: tabText.setText("Previous"); break;
                case 2: tabText.setText("Period"); break;
            }

            // Default color: first tab selected
            tabText.setTextColor(position == 0 ? colorSelected : colorUnselected);

            tabContainer.addView(tabText);
            tab.setCustomView(tabContainer);

        }).attach();

        // Listener for tab selection
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

        // Back gesture handling for drawer
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    finish();
                }
            }
        });
    }

    // Helper: Update tab text color
    private void updateTabColor(TabLayout.Tab tab, boolean isSelected, int colorSelected, int colorUnselected) {
        if (tab == null || tab.getCustomView() == null) return;
        LinearLayout container = (LinearLayout) tab.getCustomView();
        if (container.getChildCount() > 0) {
            TextView tabText = (TextView) container.getChildAt(0);
            tabText.setTextColor(isSelected ? colorSelected : colorUnselected);
        }
    }

    // Helper: get theme color
    private int getThemeColor(int attrRes) {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(attrRes, typedValue, true);
        return typedValue.data;
    }

    @Override
    protected void onResume() {
        super.onResume();
        navController.markCurrentItem(R.id.nav_epos);
    }
}
