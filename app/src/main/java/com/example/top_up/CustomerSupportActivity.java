package com.example.top_up;

import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class CustomerSupportActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private ImageView back;

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

        // Initialize views
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        back = findViewById(R.id.back);

        back.setOnClickListener(view -> {
            startActivity(new Intent(CustomerSupportActivity.this, home_page.class));
            finish();
        });

        // Setup fragments
        FragmentStateAdapter adapter = new FragmentStateAdapter(this) {
            @Override
            public int getItemCount() { return 2; }

            @Override
            public Fragment createFragment(int position) {
                return position == 0 ? new SupportChatFragment() : new FAQFragment();
            }
        };
        viewPager.setAdapter(adapter);

        // Get theme colors
        final int colorSelected = getThemeColor(R.attr.customTextColor);
        final int colorUnselected = getThemeColor(R.attr.customTextColor5);

        // Attach TabLayout with ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            TextView tabText = new TextView(this);
            tabText.setText(position == 0 ? "Support chat" : "FAQ");
            tabText.setAllCaps(false);
            tabText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            tabText.setGravity(Gravity.CENTER);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            tabText.setLayoutParams(params);

            // Default color
            tabText.setTextColor(position == 0 ? colorSelected : colorUnselected);

            tab.setCustomView(tabText);
        }).attach();

        // Tab selection listener to change colors dynamically
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
    }

    private void updateTabColor(TabLayout.Tab tab, boolean isSelected, int colorSelected, int colorUnselected) {
        if (tab == null || tab.getCustomView() == null) return;
        TextView tabText = (TextView) tab.getCustomView();
        tabText.setTextColor(isSelected ? colorSelected : colorUnselected);
    }

    // Helper to get color from theme
    private int getThemeColor(int attrRes) {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(attrRes, typedValue, true);
        return typedValue.data;
    }
}
