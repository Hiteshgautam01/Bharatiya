package ducic.plumbum.com.bjp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.rey.material.widget.ProgressView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import ducic.plumbum.com.bjp.R;
import ducic.plumbum.com.bjp.application.BhartiyaApplication;
import ducic.plumbum.com.bjp.utils.Constants;
import ducic.plumbum.com.bjp.utils.Utils;

public class SignUp extends AppCompatActivity {

    int GOOGLE_RESULT = 200;
    LoginButton fb_login;
    SignInButton google_login;
    CallbackManager callbackManager;
    RelativeLayout loading;
    ProgressView progress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
//        temp_signup();
        setUpFacebook();
//        setupGoogle();
    }

    private void temp_signup() {
        Constants.user_name = "Pankaj Baranwal";
        SharedPreferences sp = BhartiyaApplication.getInstance().getSharedPreferences();
        final SharedPreferences.Editor editor = sp.edit();
        editor.putString("user_name", Constants.user_name);
        editor.apply();
        final String user_id = "123456";
        loadActivity(TimelineActivity.class);
        loading = findViewById(R.id.progress_rl);
        progress = findViewById(R.id.progress);
        progress.start();
        loading.setVisibility(View.VISIBLE);
        StringRequest request = new StringRequest(Request.Method.POST, Constants.url_signup, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length() > 0) {
                    editor.putBoolean("signed_in", true);
                    editor.putString("user_id", response);
                    editor.apply();
                    Constants.user_id = response;
//                    Log.e("user_id_SignUp", response);
                    loading.setVisibility(View.GONE);
                    progress.stop();
                    loadActivity(SplashScreenActivity.class);
                } else {
                    loading.setVisibility(View.GONE);
                    progress.stop();
                    makeToast("Server response incorrect");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.setVisibility(View.GONE);
                progress.stop();
                makeToast("Error connecting to server");
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_name", Constants.user_name);
                params.put("user_id", user_id);
                return params;
            }
        };

        BhartiyaApplication.getInstance().addToRequestQueue(request, "signin");
    }


    // setupGoogle()
    private void setupGoogle() {
//        google_login = findViewById(R.id.google_login);
//        // Configure sign-in to request the user's ID, email address, and basic
//        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .build();
//        // Build a GoogleSignInClient with the options specified by gso.
//        final GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
//
//        google_login.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
//                startActivityForResult(signInIntent, 200);
//            }
//        });
    }

    //setUpFacebook
    private void setUpFacebook() {

        fb_login = findViewById(R.id.fb_login);
        fb_login.setReadPermissions(Arrays.asList("public_profile", "email"));
        // Other app specific specialization
        callbackManager = CallbackManager.Factory.create();

        // Callback registration
        fb_login.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                getUserProfile(loginResult);
            }

            @Override
            public void onCancel() {
                makeToast("Request Cancelled? TRY AGAIN!");
            }

            @Override
            public void onError(FacebookException exception) {
//                Log.e(SignUp.class.getSimpleName(), exception.toString());
                makeToast("Facebook login failed");
            }
        });
    }

    // FB Response Handling
    private void getUserProfile(LoginResult loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        if (response != null && response.getError() == null) {
                            try {
                                String user_id;
                                if (response.getJSONObject().has("name")) {
                                    Constants.user_name = response.getJSONObject().getString("name");
                                } else {
                                    Constants.user_name = "Anonymous User";
                                }
                                if (response.getJSONObject().has("email")) {
                                    user_id = response.getJSONObject().getString("email");
                                } else {
                                    user_id = response.getJSONObject().getString("id");
                                }
                                submitData(user_id);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            makeToast("Couldn't submit response");
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,picture,link");
        request.setParameters(parameters);
        request.executeAsync();
    }

    // onActivityResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GOOGLE_RESULT) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        } else
            callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Constants.user_name = account.getEmail();
            submitData(account.getEmail());
        } catch (ApiException e) {
//            Log.e("Google SignIn Error", e + "");e
            makeToast("Google behaving weirdly!");
        }
    }

    void submitData(final String username) {
        SharedPreferences sp = BhartiyaApplication.getInstance().getSharedPreferences();
        final SharedPreferences.Editor editor = sp.edit();
        loadActivity(TimelineActivity.class);
        loading = findViewById(R.id.progress_rl);
        progress = findViewById(R.id.progress);
        progress.start();
        loading.setVisibility(View.VISIBLE);
        StringRequest request = new StringRequest(Request.Method.POST, Constants.url_signup, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length() > 0) {
                    editor.putBoolean("signed_in", true);
                    editor.putString("user_id", response);
                    editor.putString("user_name", Constants.user_name);
                    editor.apply();
                    Constants.user_id = response;
                    loading.setVisibility(View.GONE);
                    progress.stop();
                    loadActivity(SplashScreenActivity.class);
                } else {
                    loading.setVisibility(View.GONE);
                    progress.stop();
                    makeToast("Server response incorrect");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loading.setVisibility(View.GONE);
                progress.stop();
                makeToast("Error connecting to server");
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("user_name", Constants.user_name);
                params.put("user_id", username);
                return params;
            }
        };

        BhartiyaApplication.getInstance().addToRequestQueue(request, "signin");
    }

    @Override
    protected void onResume() {
        super.onResume();
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
