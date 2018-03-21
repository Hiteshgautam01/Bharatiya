package ducic.plumbum.com.bjp.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

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
import ducic.plumbum.com.bjp.activity.TimelineActivity;
import ducic.plumbum.com.bjp.adapter.TimelineAdapter;
import ducic.plumbum.com.bjp.application.BhartiyaApplication;
import ducic.plumbum.com.bjp.interfaces.Posts;
import ducic.plumbum.com.bjp.utils.Constants;
import ducic.plumbum.com.bjp.utils.TimelineDetails;
import ducic.plumbum.com.bjp.utils.Utils;


/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class LikedPostsFragment extends Fragment implements Posts {
    private TimelineAdapter mAdapter;
    private List<TimelineDetails> mItems = new ArrayList<>();
    private int fb_start_id = 0;
    private int youtube_start_id = 0;
    private RecyclerView recyclerView;

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;

    View view;
    private int number_of_retries = 0;
    long previous_millis;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public LikedPostsFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static LikedPostsFragment newInstance(int columnCount) {
        LikedPostsFragment fragment = new LikedPostsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_savestar, container, false);

        // Set the adapter
        Context context = view.getContext();
        recyclerView = view.findViewById(R.id.list);
        if (mColumnCount <= 1) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
        }

        getData();

        return view;
    }

    private void init() {
        if (mItems.size()>0) {
            mAdapter = new TimelineAdapter(getContext(), mItems, this);

            recyclerView.setHasFixedSize(true);

            recyclerView.getRecycledViewPool().setMaxRecycledViews(0, 0);

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(mAdapter);
        }else {
            RelativeLayout empty_layout = view.findViewById(R.id.empty_layout);
            empty_layout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    void getData(){

        StringRequest request = new StringRequest(Request.Method.POST, Constants.url_voted_posts, new Response.Listener<String>() {
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
                        for (int i = mItems.size() - 1; i > 0; i--){
                            if (mItems.get(i).getSource_id() == 0) {
                                fb_start_id = mItems.get(i).getId() - 1;
                                break;
                            }
                            else if (mItems.get(i).getSource_id() == 1){
                                youtube_start_id = mItems.get(i).getId()-1;
                            }

                        }
                    } catch (JSONException e) {
                        makeToast("Error loading timeline");
//                        Log.e(TimelineActivity.class.getSimpleName(), e.toString());
                    }
                }else{
                    makeToast("No response from server");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.e(TimelineActivity.class.getSimpleName(), error.toString());
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
                params.put("user_id", Constants.user_id);
                return params;
            }
        };

        BhartiyaApplication.getInstance().addToRequestQueue(request, "likedfragment");
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

    private void makeToast(String s){
        View _view = view.findViewById(android.R.id.content);
        Utils.makeToast(_view, s);
    }

    @Override
    public void loadMorePosts() {
        if (System.currentTimeMillis() - previous_millis > 5000) {
            getData();
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (Constants.actions.size() > 0){
            sendActions();
        }
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(TimelineDetails item);
    }
}
