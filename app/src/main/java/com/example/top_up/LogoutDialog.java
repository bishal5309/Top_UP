package com.example.top_up;

import android.app.AlertDialog; // for BUTTON_POSITIVE, BUTTON_NEGATIVE constants
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.TextView;
import android.content.res.Configuration;

import androidx.appcompat.app.AppCompatActivity; // base activity class
import androidx.appcompat.app.AlertDialog.Builder; // dialog builder

public class LogoutDialog {

    public static void show(final AppCompatActivity activity) {
        boolean isDarkMode = (activity.getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;

        // Create a custom TextView for the message
        TextView messageView = new TextView(activity);
        messageView.setText("When logging out of the account all related info will be deleted from the device. Continue?");
        messageView.setTextSize(16);
        messageView.setPadding(65, 45, 0, 0);
        messageView.setLineSpacing(4f, 1f);
        messageView.setTextColor(isDarkMode ? Color.parseColor("#FFFFFF") : Color.parseColor("#444444"));

        // Build the dialog
        androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(activity)
                .setTitle("Log Out")
                .setView(messageView)
                .setPositiveButton("Yes", (d, which) -> {
                    Intent intent = new Intent(activity, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    activity.startActivity(intent);
                })
                .setNegativeButton("No", null)
                .create();

        dialog.show();

        // Set dialog background
        int backgroundColor = isDarkMode ? Color.parseColor("#1d334a") : Color.parseColor("#FFFFFF");
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(backgroundColor));

        // Set title color
        TextView titleView = dialog.findViewById(androidx.appcompat.R.id.alertTitle);
        if (titleView != null)
            titleView.setTextColor(isDarkMode ? Color.parseColor("#FFFFFF") : Color.parseColor("#000000"));

        // Set buttons color
        int buttonTextColor = isDarkMode ? Color.parseColor("#FFFFFF") : Color.parseColor("#000000");
        if (dialog.getButton(AlertDialog.BUTTON_POSITIVE) != null)
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(buttonTextColor);
        if (dialog.getButton(AlertDialog.BUTTON_NEGATIVE) != null)
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(buttonTextColor);
    }
}
