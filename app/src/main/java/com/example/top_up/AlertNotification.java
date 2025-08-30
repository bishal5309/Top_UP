package com.example.top_up;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
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
        if (activity == null || activity.isFinishing()) return;

        BottomSheetDialog bottomDialog = new BottomSheetDialog(
                activity,
                R.style.TransparentBottomSheetDialog
        );

        View view = LayoutInflater.from(activity).inflate(R.layout.alert_box, null);

        // Set alert message
        TextView alertMessage = view.findViewById(R.id.alertMessage);
        if (alertMessage != null) {
            alertMessage.setText(messageText);
        }

        // Close icon functionality
        ImageView closeIcon = view.findViewById(R.id.closeIcon);
        if (closeIcon != null) {
            closeIcon.setOnClickListener(v -> bottomDialog.dismiss());
        }

        // Convert dp to px
        int bottomMarginPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                32,
                activity.getResources().getDisplayMetrics()
        );

        // Set margins
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(10, 0, 10, bottomMarginPx);
        view.setLayoutParams(params);

        bottomDialog.setContentView(view);

        // Window background transparent
        Window window = bottomDialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }

        // Bottom sheet background transparent
        View sheet = bottomDialog.getWindow() != null ?
                bottomDialog.getWindow().findViewById(com.google.android.material.R.id.design_bottom_sheet) :
                null;
        if (sheet != null) {
            sheet.setBackgroundColor(Color.TRANSPARENT);
        }

        bottomDialog.show();

        // Auto-dismiss with smooth slide-down
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            View sheetView = bottomDialog.getWindow() != null ?
                    bottomDialog.getWindow().findViewById(com.google.android.material.R.id.design_bottom_sheet) :
                    null;

            if (sheetView != null) {
                sheetView.animate()
                        .translationY(sheetView.getHeight())
                        .alpha(0f)
                        .setInterpolator(new AccelerateDecelerateInterpolator())
                        .setDuration(300)
                        .withEndAction(bottomDialog::dismiss)
                        .start();
            } else {
                bottomDialog.dismiss();
            }
        }, 2000);
    }
}
