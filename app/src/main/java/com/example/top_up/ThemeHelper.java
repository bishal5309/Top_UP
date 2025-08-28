package com.example.top_up;

import android.app.Activity;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public class ThemeHelper {

    public static void applyTheme(Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences("theme_prefs", Activity.MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean("is_dark_switch_on", false);

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
