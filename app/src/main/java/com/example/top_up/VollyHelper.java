package com.example.top_up;

import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class VollyHelper {

    private static VollyHelper instance;
    private RequestQueue requestQueue;
    private static Context ctx;

    private VollyHelper(Context context) {
        ctx = context.getApplicationContext(); // always use app context
        requestQueue = getRequestQueue();
    }

    public static synchronized VollyHelper getInstance(Context context) {
        if (instance == null) {
            instance = new VollyHelper(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(ctx);
        }
        return requestQueue;
    }

    // ================= Callback Interface =================
    public interface VolleyCallback {
        void onSuccess(String result);
        void onError(String error);
    }

    // ================= Login User =================
    public void loginUser(String url, String userId, String password, String workplace, VolleyCallback callback) {
        StringRequest request = new StringRequest(Request.Method.POST, url,
                callback::onSuccess,
                error -> callback.onError(error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userId);
                params.put("password", password);
                params.put("workplace", workplace); // ✅ এখানে সঠিক key ব্যবহার করা হয়েছে
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(5000, 2, 1));
        getRequestQueue().add(request);
    }

    // ================= Fetch GET data =================
    public void fetchData(String url, VolleyCallback callback) {
        StringRequest request = new StringRequest(Request.Method.GET,
                url,
                callback::onSuccess,
                error -> callback.onError(error.toString()));
        request.setRetryPolicy(new DefaultRetryPolicy(5000, 2, 1));
        getRequestQueue().add(request);
    }

    // ================= Submit Top-Up =================
    public void submitTopUp(String url, String userId, String amount, String workplace, VolleyCallback callback) {
        StringRequest request = new StringRequest(Request.Method.POST, url,
                callback::onSuccess,
                error -> callback.onError(error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", userId);
                params.put("amount", amount);
                params.put("workplace", workplace); // ✅ এখানে ও workplace ব্যবহার
                params.put("type", "TOP UP");
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(5000, 2, 1));
        getRequestQueue().add(request);
    }
}
