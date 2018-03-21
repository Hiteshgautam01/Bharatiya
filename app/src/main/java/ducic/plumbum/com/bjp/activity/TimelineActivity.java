package ducic.plumbum.com.bjp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ducic.plumbum.com.bjp.R;
import ducic.plumbum.com.bjp.adapter.TimelineAdapter;
import ducic.plumbum.com.bjp.application.BhartiyaApplication;
import ducic.plumbum.com.bjp.interfaces.Posts;
import ducic.plumbum.com.bjp.utils.Constants;
import ducic.plumbum.com.bjp.utils.TimelineDetails;
import ducic.plumbum.com.bjp.utils.Utils;
import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;


public class TimelineActivity extends AppCompatActivity implements Posts, SwipeRefreshLayout.OnRefreshListener{
    private TimelineAdapter mAdapter;
    private List<TimelineDetails> mItems = new ArrayList<>();
    private int fb_start_id = 0;
    private int youtube_start_id = 0;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int number_of_retries = 0;
    long previous_millis;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(0xFFFF7830);
        setSupportActionBar(toolbar);

        getData();
    }


    private void init() {
        mAdapter = new TimelineAdapter(this, mItems, this);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        recyclerView.getRecycledViewPool().setMaxRecycledViews(0, 0);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);
        initAppBarLayout();
    }


    void getData(){
        final ProgressBar loading = findViewById(R.id.progress_bar);
        loading.setIndeterminate(true);
        StringRequest request = new StringRequest(Request.Method.POST, Constants.url_event_details, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length() > 0){
                    number_of_retries = 0;
                    try {
                        JSONArray jA = new JSONArray(response);
                        for (int i = 0; i < jA.length(); i++){
                            mItems.add(new TimelineDetails(jA.getJSONObject(i).getInt("id"), jA.getJSONObject(i).getString("message"), jA.getJSONObject(i).getString("url"), jA.getJSONObject(i).getString("image_link"), jA.getJSONObject(i).getString("source_name"), jA.getJSONObject(i).getInt("source"), jA.getJSONObject(i).getString("counter").contentEquals("null")?0:jA.getJSONObject(i).getInt("counter"), jA.getJSONObject(i).getString("timedate")));
                        }
                        if (fb_start_id == 0 && youtube_start_id == 0)
                            init();
                        else{
                            mAdapter.notifyDataSetChanged();
                        }
                        previous_millis = System.currentTimeMillis();
                        fb_start_id = mItems.get(mItems.size() - 3).getId()-1;
                        youtube_start_id = mItems.get(mItems.size() - 1).getId()-1;
                        swipeRefreshLayout.setRefreshing(false);
                        loading.setIndeterminate(false);
                    } catch (JSONException e) {
                        makeToast("Error loading timeline");
                        Log.e(TimelineActivity.class.getSimpleName(), e.toString());
                    }
                }else{
                    makeToast("No response from server");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TimelineActivity.class.getSimpleName(), error.toString());
                if (number_of_retries < 3){
                    number_of_retries++;
                    getData();
                }else
                    makeToast("No response from server");

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("page_first_source", String.valueOf(fb_start_id));
                params.put("page_second_source", String.valueOf(youtube_start_id));
                return params;
            }
        };

        BhartiyaApplication.getInstance().addToRequestQueue(request, "signin");
    }

    private void sendActions() {
        final JSONArray jA = new JSONArray();
        for (int i = 0; i < Constants.actions.size(); i++){
            JSONObject jO = new JSONObject();
            try {
                jO.put("action_id", Constants.actions.get(i));
                jO.put("post_id", Constants.post_id.get(i));
                jA.put(jO);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        StringRequest request = new StringRequest(Request.Method.POST, Constants.url_action, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length()>0)
                    makeToast("Couldn't submit response");
                else{
                    Constants.actions.clear();
                    Constants.post_id.clear();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                makeToast("Couldn't connect to server");
                error.printStackTrace();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("actions", jA.toString());
                if (Constants.user_id == null) {
                    SharedPreferences sp = BhartiyaApplication.getInstance().getSharedPreferences();
                    Constants.user_id = sp.getString("user_id", "-1");
                }
                params.put("user_id", Constants.user_id);
                return params;
            }
        };
        BhartiyaApplication.getInstance().addToRequestQueue(request, "record_actions");
    }

    @Override
    public void onRefresh() {
        fb_start_id = 0;
        youtube_start_id = 0;
        getData();
    }

    private void initAppBarLayout() {
        final Button button_goto = findViewById(R.id.go_to_top);
        final Button button_filter_posts = findViewById(R.id.filter_posts);

        AppBarLayout appBarLayout = findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
                    button_goto.setVisibility(View.VISIBLE);
                } else if (verticalOffset == 0) {
                    button_goto.setVisibility(View.GONE);
                }
            }
        });
        button_goto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.smoothScrollToPosition(0);
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TimelineActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        button_filter_posts.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(TimelineActivity.this)

                        .setIcon(R.drawable.ic_filter_list_black_1_24dp)
                        .setTitle("FILTER POSTS")
                        .setMessage("What you want to see ?")
                        .setNeutralButton("Cancel", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                Toast.makeText(TimelineActivity.this, "You Clicked on Cancel", Toast.LENGTH_SHORT).show();
                            }
                        })

                        .setPositiveButton("Only Images", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                Toast.makeText(TimelineActivity.this, "Now you can only see Images posts", Toast.LENGTH_SHORT).show();

                            }
                        })

                        .setNegativeButton("Only Videos", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                Toast.makeText(TimelineActivity.this, "Now you can only see Videos posts", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show();
            }
        });

        final int[] counter = {0};
        final long[] time_current = {0};
        final long[] time_after_five = {0};

        final KonfettiView konfettiView = findViewById(R.id.konfettiView);
        konfettiView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                counter[0]++;
                if(counter[0] < 5){
                    konfettiView.build()
                            .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                            .setDirection(0.0, 359.0)
                            .setSpeed(1f, 3f)
                            .setFadeOutEnabled(true)
                            .setTimeToLive(1500L)
                            .addShapes(Shape.RECT, Shape.CIRCLE)
                            .addSizes(new Size(7, 5f))
                            .setPosition(-50f, konfettiView.getWidth() + 50f, -50f, -50f)
                            .stream(50, 4000L);

                    time_current[0] = System.currentTimeMillis();

                }

                else{
                    time_after_five[0] = System.currentTimeMillis();
                    if (counter[0] == 5) {
                        makeToast("Your fingers are not stopping, Please try again after some time");
                    }

                    if (time_after_five[0] - time_current[0] > 7000) {
                        counter[0] = 0;
                    }
                }
            }
        });
    }

    private void makeToast(String s){
        View view = findViewById(android.R.id.content);
        Utils.makeToast(view, s);
    }

    @Override
    public void loadMorePosts() {
        if (System.currentTimeMillis() - previous_millis > 5000) {
            getData();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (recyclerView != null)
            recyclerView.scrollToPosition(Constants.paused_post_id);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Constants.actions.size() > 0){
            sendActions();
        }
    }
}