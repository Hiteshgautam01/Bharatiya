package ducic.plumbum.com.bjp.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import ducic.plumbum.com.bjp.R;
import ducic.plumbum.com.bjp.application.BhartiyaApplication;
import ducic.plumbum.com.bjp.utils.Constants;
import ducic.plumbum.com.bjp.utils.Utils;

/**
 * Project Name: 	<bjp>
 * Author List: 		Pankaj Baranwal
 * Filename: 		<>
 * Functions: 		<>
 * Global Variables:	<>
 * Date of Creation:    <14/02/2018>
 */
public class SplashScreenActivity extends AppCompatActivity {
    SharedPreferences sp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (sp.contains("signed_in")) {
            setContentView(R.layout.activity_splash_screen);
            Constants.user_id = sp.getString("user_id", null);
            Constants.user_name = sp.getString("user_name", null);
//            Log.e("user_name", Constants.user_name);
//            Log.e("user_id", Constants.user_id);
            loadActivity(TimelineActivity.class);
//            checkIfExists();
        } else {

            loadActivity(SignUp.class);
        }

    }

    private void checkIfExists() {
        final int[] status_code = new int[1];
        StringRequest request = new StringRequest(Request.Method.GET, Constants.url_verify_user, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (status_code[0] == 200) {
                    if (response.contentEquals(Constants.user_id)) {
                        loadActivity(TimelineActivity.class);
                    } else {
                        makeToast("Couldn't verify ID. Please login again");
                        SharedPreferences.Editor editor = sp.edit();
                        editor.remove("signed_in");
                        editor.apply();
                        loadActivity(SignUp.class);
                    }
                } else {
                    makeToast("Please restart the app");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                makeToast("Please restart the app");
                error.printStackTrace();
                finish();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", Constants.user_id);
                return params;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                status_code[0] = response.statusCode;
                return super.parseNetworkResponse(response);
            }
        };

        BhartiyaApplication.getInstance().addToRequestQueue(request, "verify_user");
    }

    private void makeToast(String s) {
        View view = findViewById(android.R.id.content);
        Utils.makeToast(view, s);
    }

    private void loadActivity(Class activity) {
        Utils.loadActivity(this, activity);
        finish();
    }
}
