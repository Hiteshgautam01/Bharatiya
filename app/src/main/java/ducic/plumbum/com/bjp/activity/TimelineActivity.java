package ducic.plumbum.com.bjp.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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
import ducic.plumbum.com.bjp.adapter.TimelineAdapter;
import ducic.plumbum.com.bjp.application.VolleyHandling;
import ducic.plumbum.com.bjp.interfaces.Posts;
import ducic.plumbum.com.bjp.utils.Constants;
import ducic.plumbum.com.bjp.utils.TimelineDetails;
import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;


public class TimelineActivity extends AppCompatActivity implements Posts{
    private TimelineAdapter mAdapter;
    private List<TimelineDetails> mItems = new ArrayList<>();
    private int fb_start_id = 0;
    private int youtube_start_id = 0;
    private boolean updating = false;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(0xFFFF7830);
        setSupportActionBar(toolbar);

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
                konfettiView.build()
                        .addColors(Color.YELLOW, Color.GREEN, Color.MAGENTA)
                        .setDirection(0.0, 359.0)
                        .setSpeed(1f, 3f)
                        .setFadeOutEnabled(true)
                        .setTimeToLive(2000L)
                        .addShapes(Shape.RECT, Shape.CIRCLE)
                        .addSizes(new Size(3, 5f))
                        .setPosition(-50f, konfettiView.getWidth() + 50f, -50f, -50f)
                        .stream(300, 5000L);
            }
        });
    }

    private void init() {
        mAdapter = new TimelineAdapter(this, mItems, this);

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
                    try {
                        JSONArray jA = new JSONArray(response);
                        for (int i = 0; i < jA.length(); i++){
                            mItems.add(new TimelineDetails(jA.getJSONObject(i).getInt("id"), jA.getJSONObject(i).getString("message"), jA.getJSONObject(i).getString("url"), jA.getJSONObject(i).getString("image_link"), jA.getJSONObject(i).getString("source_name"), jA.getJSONObject(i).getInt("source")));
                        }
                        if (fb_start_id == 0 && youtube_start_id == 0)
                            init();
                        else{
                            mAdapter.timelineList = mItems;
                            mAdapter.notifyDataSetChanged();
                        }
                        fb_start_id = mItems.get(mItems.size() - 3).getId()-1;
                        youtube_start_id = mItems.get(mItems.size() - 1).getId()-1;
                        updating = false;
                    } catch (JSONException e) {
                        Toast.makeText(TimelineActivity.this, "ERRORORROROR", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
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
                getData();
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

        VolleyHandling.getInstance().addToRequestQueue(request, "signin");
    }

    @Override
    public void loadMorePosts() {
        if (!updating) {
            updating = true;
            getData();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        recyclerView.scrollToPosition(Constants.paused_post_id);
    }
}