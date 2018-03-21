package ducic.plumbum.com.bjp.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ducic.plumbum.com.bjp.R;
import ducic.plumbum.com.bjp.adapter.CommentsAdapter;
import ducic.plumbum.com.bjp.application.BhartiyaApplication;
import ducic.plumbum.com.bjp.utils.CommentDetails;
import ducic.plumbum.com.bjp.utils.Constants;
import ducic.plumbum.com.bjp.utils.Utils;

/**
 * Created by pankaj on 18/3/18.
 */

public class CommentsActivity extends AppCompatActivity {
    private CommentsAdapter mAdapter;
    private List<CommentDetails> mItems = new ArrayList<>();
    private int post_id;
    private EditText comment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("post_id"))
            post_id = getIntent().getExtras().getInt("post_id");
        else
            makeToast("404: Couldn't find post");
        setContentView(R.layout.activity_comments);
        getData();
    }

    private void init() {
        mAdapter = new CommentsAdapter(this, mItems);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(0, 0);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);
        comment = findViewById(R.id.comment_edittext);
        Button send = findViewById(R.id.send_comment_button);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (comment.getText().toString().length() == 0){
                    makeToast("Please enter a message first");
                }
                else {
                    sendComment(comment.getText().toString());
                }
            }
        });
    }

    private void sendComment(final String s) {
        StringRequest request = new StringRequest(Request.Method.POST, Constants.url_record_comment, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length()>5){
                    makeToast("Server Error");
                }else{
                    CommentDetails item = new CommentDetails(Integer.parseInt(response), Constants.user_name, comment.getText().toString());
                    mItems.add(item);
                    mAdapter.notifyDataSetChanged();
                    comment.setText("");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.e(CommentsActivity.class.getSimpleName(), error.toString());
                makeToast("Couldn't submit response");

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("user_id", String.valueOf(Constants.user_id));
                params.put("message", s);
                params.put("post_id", String.valueOf(post_id));
                return params;
            }
        };

        BhartiyaApplication.getInstance().addToRequestQueue(request, "signin");
    }

    void getData(){
        StringRequest request = new StringRequest(Request.Method.POST, Constants.url_comment_details, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length() > 0){
                    try {
                        JSONArray jA = new JSONArray(response);
                        for (int i = 0; i < jA.length(); i++)
                            mItems.add(new CommentDetails(jA.getJSONObject(i).getInt("id"), jA.getJSONObject(i).getString("user_name"), jA.getJSONObject(i).getString("message")));
                        init();
                        mAdapter.commentDetails = mItems;
                        mAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        makeToast("Error loading comments");
//                        Log.e(CommentsActivity.class.getSimpleName(), e.toString());
                    }
                }else{
                    makeToast("No response from server");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.e(CommentsActivity.class.getSimpleName(), error.toString());
                makeToast("Couldn't fetch responses");

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("user_id", String.valueOf(Constants.user_id));
                params.put("post_id", String.valueOf(post_id));
                return params;
            }
        };

        BhartiyaApplication.getInstance().addToRequestQueue(request, "signin");
    }

    private void makeToast(String s){
        View view = findViewById(android.R.id.content);
        Utils.makeToast(view, s);
    }
}
