package com.example.top_up;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class CustomerSupportActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_customer_support);

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
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);} // কালো আইকন

        // Initialize views
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        back = findViewById(R.id.back);

        back.setOnClickListener(view -> {
            Intent intent = new Intent(CustomerSupportActivity.this,home_page.class);
            startActivity(intent);
            finish();
        });

        // Setup fragments
        FragmentStateAdapter adapter = new FragmentStateAdapter(this) {
            @Override
            public int getItemCount() {
                return 2;
            }

            @Override
            public Fragment createFragment(int position) {
                return position == 0 ? new SupportChatFragment() : new FAQFragment();
            }
        };

        viewPager.setAdapter(adapter);

        // Attach TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            TextView tabText = new TextView(this);
            tabText.setText(position == 0 ? "Support chat" : "FAQ");
            tabText.setAllCaps(false);
            tabText.setTextSize(16);
            tabText.setTextColor(Color.parseColor("#FFFFFF")); // Hex color code

            tabText.setGravity(Gravity.CENTER);

            // Ensure full width so gravity works
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            tabText.setLayoutParams(params);

            tab.setCustomView(tabText);
        }).attach();
    }
}