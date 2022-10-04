package application.aku.volleyclass.METHOD;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class VolleyClass {
    IResult result = null;
    Context context;

    public VolleyClass(IResult resultx, Context contextx) {
        result = resultx;
        context = contextx;
    }


    public void restAPI(int method, String url, JSONObject data, String token, String type) {
        result.notifyLoad(type, true);
        Log.e("method "+method, "0=GET, 1=POST, 2=PUT, 3=DELETE");
        Log.e("url", url);
        Log.e("data", "" + data);
        Log.e("type", type);
        JsonObjectRequest request = new JsonObjectRequest(method, url, data,
                response -> {
                    Log.e("success", response.toString());
                    result.notifySuccess(type, response);
                    result.notifyLoad(type, false);
                }, error -> {
            NetworkResponse response = error.networkResponse;
            if (response != null) {
                result.notifyLoad(type, false);
                try {
                    String res = new String(response.data, HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                    JSONObject object = new JSONObject(res);
                    Log.e("error", object.toString());
                    if (object.getString("statusCode").equals("401")) {
                        result.notifyUnauth(type, "Unauthorized");
                    } else {
                        result.notifyError(type, "Error Request: "+object.getString("message"));
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    result.notifyFailed(type, "Failed Request");
                } catch (JSONException e) {
                    e.printStackTrace();
                    result.notifyFailed(type, "Failed Request");
                }
            } else {
                result.notifyLoad(type, false);
                result.notifyFailed(type, "Failed Request");
            }
        }) {
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                Log.e("header", "" + headers);
                return headers;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(0, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(context).add(request);
    }
}

