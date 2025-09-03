package com.example.top_up;

import android.app.Activity;
import android.app.Dialog;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

public class DialogHelper {

    public static void showTopUpDialog(Activity activity) {
        Dialog dialog = new Dialog(activity);
        View contentView = LayoutInflater.from(activity).inflate(R.layout.layout_bottom_sheet, null);
        dialog.setContentView(contentView);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            window.setGravity(Gravity.BOTTOM);
            window.setBackgroundDrawable(ContextCompat.getDrawable(activity, android.R.color.transparent));
            window.getAttributes().windowAnimations = R.anim.dialog_animation;
        }

        EditText edtUserId = contentView.findViewById(R.id.edit_user_id);
        AppCompatButton btnSearch = contentView.findViewById(R.id.btn_search);
        LinearLayout layoutLoadingArea = contentView.findViewById(R.id.layout_loading_area);
        LinearLayout layoutUserDetails = contentView.findViewById(R.id.layout_user_details);
        layoutUserDetails.setVisibility(View.GONE);
        layoutLoadingArea.setVisibility(View.GONE); // Initially hidden

        EditText edtCustomerId = contentView.findViewById(R.id.customer_id_edit);
        TextView txtName = contentView.findViewById(R.id.txt_name);
        EditText edtAmount = contentView.findViewById(R.id.amount_edit);
        AppCompatButton btnOk = contentView.findViewById(R.id.btn_ok);

        btnSearch.setOnClickListener(v -> {
            String userId = edtUserId.getText().toString().trim();
            if (userId.isEmpty()) {
                edtUserId.setError("Please enter User ID");
                return;
            }

            // Show loading animation and hide search button immediately
            layoutLoadingArea.setVisibility(View.VISIBLE);
            btnSearch.setEnabled(false);
            btnSearch.setVisibility(View.GONE); // 🔥 Moved outside delay
            edtUserId.setEnabled(false);

            // Simulate 2-second delay before showing form
            new Handler().postDelayed(() -> {
                layoutUserDetails.setVisibility(View.VISIBLE);
                layoutLoadingArea.setVisibility(View.GONE);
                edtUserId.setVisibility(View.GONE);

                edtCustomerId.setText(userId);
                txtName.setText("Unknown user");
                edtAmount.setText("");
                edtAmount.setTag("N/A");
            }, 2000);
        });

        btnOk.setOnClickListener(okView -> {
            String customerId = edtCustomerId.getText().toString().trim();
            String amount = edtAmount.getText().toString().trim();
            String workplace = edtAmount.getTag().toString();

            if (customerId.isEmpty()) {
                edtCustomerId.setError("Customer ID required");
                return;
            }
            if (amount.isEmpty()) {
                edtAmount.setError("Amount required");
                return;
            }

            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            if (activity.getCurrentFocus() != null)
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);

            String url = "https://sbetshopbd.xyz/api/top_up.php";

            VollyHelper.getInstance(activity).submitTopUp(url, customerId, amount, workplace, new VollyHelper.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    dialog.dismiss();
                    AlertNotification.show(activity, "TOP UP Success: " + customerId + " - Amount: ৳" + amount);
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(activity, "TOP UP Failed: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }
}