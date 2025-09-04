package com.example.top_up;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

public class WithdrawDialog2 {

    public static void show(Activity activity) {
        Dialog dialog = new Dialog(activity);
        View contentView = LayoutInflater.from(activity).inflate(R.layout.layout_bottom_sheet2, null);
        dialog.setContentView(contentView);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM);
            window.setBackgroundDrawable(ContextCompat.getDrawable(activity, android.R.color.transparent));
            window.getAttributes().windowAnimations = R.anim.dialog_animation;
        }

        EditText edtCustomerId = contentView.findViewById(R.id.customer_id_edit);
        EditText edtAmount = contentView.findViewById(R.id.amount_edit);
        AppCompatButton btnOk = contentView.findViewById(R.id.btn_ok);

        btnOk.setOnClickListener(okView -> {
            String customerId = edtCustomerId.getText().toString().trim();
            String amount = edtAmount.getText().toString().trim();
            if (customerId.isEmpty()) { edtCustomerId.setError("Recipient ID required"); return; }
            if (amount.isEmpty()) { edtAmount.setError("Code required"); return; }

            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            if (activity.getCurrentFocus() != null)
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);

            dialog.dismiss();
            AlertNotification.show(activity, "Your withdraw address active in 72 hours");
        });

        dialog.show();
    }
}
