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
import org.json.JSONObject;
public class DialogHelper {
    public interface TopUpListener {
        void onTopUpSuccess();
    }

    public static void showTopUpDialog(Activity activity, TopUpListener listener) {
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
        layoutLoadingArea.setVisibility(View.GONE);

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

            layoutLoadingArea.setVisibility(View.VISIBLE);
            btnSearch.setEnabled(false);
            btnSearch.setVisibility(View.GONE);
            edtUserId.setEnabled(false);

            String password = SessionCache.password;
            String workplace = SessionCache.workplace;

            String url = "https://sbetshopbd.xyz/api/get_my_user.php"
                    + "?user_id=" + SessionCache.userId
                    + "&password=" + password
                    + "&workplace=" + workplace;

            VollyHelper.getInstance(activity).fetchData(url, new VollyHelper.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    String fetchedWorkplace = "N/A";
                    try {
                        JSONObject json = new JSONObject(result);
                        if (json.has("user")) {
                            JSONObject user = json.getJSONObject("user");
                            fetchedWorkplace = user.optString("workplace", "N/A");
                        }
                    } catch (Exception ignored) {}

                    String finalWorkplace = fetchedWorkplace;
                    new Handler().postDelayed(() -> {
                        layoutUserDetails.setVisibility(View.VISIBLE);
                        layoutLoadingArea.setVisibility(View.GONE);
                        edtUserId.setVisibility(View.GONE);

                        edtCustomerId.setText(userId);
                        txtName.setText("Workplace: " + finalWorkplace);
                        txtName.setVisibility(View.GONE);
                        edtAmount.setText("");
                        edtAmount.setTag(finalWorkplace);
                    }, 2000);
                }

                @Override
                public void onError(String error) {
                    edtUserId.setError("Network error");
                    layoutLoadingArea.setVisibility(View.GONE);
                    btnSearch.setVisibility(View.VISIBLE);
                    btnSearch.setEnabled(true);
                    edtUserId.setEnabled(true);
                }
            });
        });

        btnOk.setOnClickListener(okView -> {
            String customerId = edtCustomerId.getText().toString().trim();
            String amount = edtAmount.getText().toString().trim();
            String workplace = edtAmount.getTag() != null ? edtAmount.getTag().toString() : "N/A";

            if (customerId.isEmpty()) {
                edtCustomerId.setError("Customer ID required");
                return;
            }
            if (amount.isEmpty()) {
                edtAmount.setError("Amount required");
                return;
            }

            double topUpAmount = Double.parseDouble(amount);
            double currentBalance = Double.parseDouble(SessionCache.balance);

            if (topUpAmount > currentBalance) {
                Toast.makeText(activity, "Insufficient balance. Available: ৳" + currentBalance, Toast.LENGTH_LONG).show();
                return;
            }

            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            if (activity.getCurrentFocus() != null)
                imm.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);

            String url = "https://sbetshopbd.xyz/api/top_up.php";

            VollyHelper.getInstance(activity).submitTopUp(url, customerId, amount, workplace, new VollyHelper.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    deductBalance(activity, SessionCache.userId, topUpAmount, () -> {
                        dialog.dismiss();
                        AlertNotification.show(activity, "Success!\n "+ amount+".00 ৳ deposit into account\n"+ customerId );

                        String totalUrl = "https://sbetshopbd.xyz/api/update_total_topup.php";
                        VollyHelper.getInstance(activity).submitTopUp(totalUrl, customerId, amount, workplace, new VollyHelper.VolleyCallback() {
                            @Override
                            public void onSuccess(String result) {
                                if (listener != null) {
                                    listener.onTopUpSuccess();
                                }
                            }

                            @Override
                            public void onError(String error) {
                                // ignore
                            }
                        });
                    });
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(activity, "TOP UP Failed: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    private static void deductBalance(Activity activity, String userId, double amount, Runnable onSuccess) {
        String url = "https://sbetshopbd.xyz/api/update_balance.php";

        VollyHelper.getInstance(activity).postData(url, new VollyHelper.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject json = new JSONObject(result);
                    if ("success".equals(json.optString("status"))) {
                        // ✅ Update SessionCache
                        double currentBalance = Double.parseDouble(SessionCache.balance);
                        double newBalance = currentBalance - amount;
                        SessionCache.balance = String.valueOf(newBalance);

                        // ✅ Try to find tvBalance from root view
                        View rootView = activity.findViewById(android.R.id.content);
                        TextView tvBalance = rootView.findViewById(R.id.tvBalance);
                        if (tvBalance != null) {
                            tvBalance.setText("Balance: " + newBalance + "৳");
                        }

                        if (onSuccess != null) onSuccess.run();
                    } else {
                        Toast.makeText(activity, "Balance deduction failed", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(activity, "Parsing error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(activity, "Network error", Toast.LENGTH_SHORT).show();
            }
        }, new String[]{"user_id", "deduct"}, new String[]{userId, String.valueOf(amount)});
    }
}