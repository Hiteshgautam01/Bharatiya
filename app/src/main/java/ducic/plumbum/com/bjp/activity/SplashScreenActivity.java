package ducic.plumbum.com.bjp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import ducic.plumbum.com.bjp.R;
import ducic.plumbum.com.bjp.application.VolleyHandling;
import ducic.plumbum.com.bjp.utils.Constants;

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
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (sp.contains("signed_in")){
            setContentView(R.layout.activity_splash);
            Constants.user_id = sp.getString("user_id", null);
            checkIfExists();
        }else {
            loadActivity(LoginActivity.class);
        }
    }

    private void checkIfExists() {
        final int[] status_code = new int[1];
        StringRequest request = new StringRequest(Request.Method.GET, Constants.url_verify_user, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (status_code[0] == 200) {
                    if (response.contentEquals(Constants.user_id)){
                        loadActivity(TimelineActivity.class);
                    }else{
                        makeToast("Couldn't verify ID. Please login again");
                        SharedPreferences.Editor editor = sp.edit();
                        editor.remove("signed_in");
                        editor.apply();
                        loadActivity(LoginActivity.class);
                    }
                }else{
                    makeToast("Please restart the app");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                makeToast("Please restart the app");
                error.printStackTrace();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("id", Constants.user_id);
                return params;
            }

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                status_code[0] = response.statusCode;
                return super.parseNetworkResponse(response);
            }
        };

        VolleyHandling.getInstance().addToRequestQueue(request, "verify_user");
    }

    private void makeToast(String s) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.activity_wrapper),
                s, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    private void loadActivity(Class activity) {
        Intent intent = new Intent(SplashScreenActivity.this, activity);
        startActivity(intent);
        finish();
    }
}
