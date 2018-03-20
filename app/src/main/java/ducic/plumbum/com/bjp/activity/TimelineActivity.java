package ducic.plumbum.com.bjp.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

        final Button button_goto  = findViewById(R.id.go_to_top);

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

        final int[] counter = {0};
        final long[] time_current = {0};
        final long[] time_after_five = {0};

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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        getData();

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
                        Toast.makeText(TimelineActivity.this, "Your fingers are not stopping, Please try again after some time", Toast.LENGTH_SHORT).show();
                    }

                    if (time_after_five[0] - time_current[0] > 7000) {
                        counter[0] = 0;
                    }
                }
                }
            });
    }


    private void init() {
        mAdapter = new TimelineAdapter(this, mItems, this);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.getRecycledViewPool().setMaxRecycledViews(0, 0);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);
    }


    void getData(){
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
                            mAdapter.timelineList = mItems;
                            mAdapter.notifyDataSetChanged();
                        }
                        fb_start_id = mItems.get(mItems.size() - 3).getId()-1;
                        youtube_start_id = mItems.get(mItems.size() - 1).getId()-1;
                        swipeRefreshLayout.setRefreshing(false);
                    } catch (JSONException e) {
                        Toast.makeText(TimelineActivity.this, "Error loading timeline", Toast.LENGTH_SHORT).show();
                        Log.e(TimelineActivity.class.getSimpleName(), e.toString());
                    }
                }else{
                    Toast.makeText(TimelineActivity.this, "Server is no longer speaking to you", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(TimelineActivity.this, "Couldn't connect to server", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
                if (number_of_retries < 3){
                    number_of_retries++;
                    getData();
                }else
                    Toast.makeText(TimelineActivity.this, "Please Check Internet connection", Toast.LENGTH_SHORT).show();

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

    @Override
    public void loadMorePosts() {
        if (!Constants.updating) {
            Constants.updating = true;
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

    private void sendActions() {
        final JSONArray jA = new JSONArray();
        for (int i = 0; i < Constants.actions.size(); i++){
            JSONObject jO = new JSONObject();
            try {
                jO.put("action_id", Constants.actions.get(i));
                jO.put("post_id", Constants.post_id.get(i));
                jO.put("user_id", Constants.user_id);
                jA.put(jO);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        StringRequest request = new StringRequest(Request.Method.POST, Constants.url_action, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length()>0)
                    Toast.makeText(TimelineActivity.this, "Couldn't submit response", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(TimelineActivity.this, "Couldn't connect to server", Toast.LENGTH_SHORT).show();
                error.printStackTrace();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("actions", jA.toString());
                return params;
            }
        };

        BhartiyaApplication.getInstance().addToRequestQueue(request, "signin");
    }

    @Override
    public void onRefresh() {
        fb_start_id = 0;
        youtube_start_id = 0;
        getData();
    }
}