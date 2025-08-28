package com.example.top_up;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

public class AlertNotification {

    public static void show(Activity activity, String messageText) {

        // ✅ Use custom style to disable black dim background
        BottomSheetDialog bottomDialog = new BottomSheetDialog(
                activity,
                R.style.TransparentBottomSheetDialog
        );

        // Inflate your custom layout
        View view = LayoutInflater.from(activity).inflate(R.layout.alert_box, null);

        // 📝 Set the alert message
        TextView alertMessage = view.findViewById(R.id.alertMessage);
        alertMessage.setText(messageText);

        // ❌ Close icon functionality
        ImageView closeIcon = view.findViewById(R.id.closeIcon);
        closeIcon.setOnClickListener(v -> bottomDialog.dismiss());

        // 🧮 Convert dp to px for margin
        int bottomMarginDp = 32;
        int bottomMarginPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                bottomMarginDp,
                activity.getResources().getDisplayMetrics()
        );

        // 📐 Set margins dynamically
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(10, 0, 10, bottomMarginPx);
        view.setLayoutParams(params);

        // 💡 Set the content view
        bottomDialog.setContentView(view);

        // 🪟 Make dialog window background transparent
        Window window = bottomDialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }

        // 🎨 Make bottom sheet background itself transparent
        View sheet = bottomDialog.getWindow().findViewById(
                com.google.android.material.R.id.design_bottom_sheet
        );
        if (sheet != null) {
            sheet.setBackgroundColor(Color.TRANSPARENT);
        }

        // 🚀 Show the dialog
        bottomDialog.show();

        // ⏳ Auto-dismiss after 2 seconds with slide-down animation
        new Handler().postDelayed(() -> {
            View sheetView = bottomDialog.getWindow().findViewById(
                    com.google.android.material.R.id.design_bottom_sheet
            );
            if (sheetView != null) {
                sheetView.animate()
                        .translationY(sheetView.getHeight())
                        .alpha(0f)
                        .setInterpolator(new AccelerateDecelerateInterpolator())
                        .setDuration(300)
                        .withEndAction(bottomDialog::dismiss)
                        .start();
            } else {
                bottomDialog.dismiss(); // fallback
            }
        }, 2000); // 2000ms = 2 seconds
    }
}