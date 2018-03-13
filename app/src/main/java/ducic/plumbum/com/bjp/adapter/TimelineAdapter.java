package ducic.plumbum.com.bjp.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ducic.plumbum.com.bjp.R;
import ducic.plumbum.com.bjp.activity.CommentsActivity;
import ducic.plumbum.com.bjp.application.VolleyHandling;
import ducic.plumbum.com.bjp.utils.Constants;
import ducic.plumbum.com.bjp.utils.TimelineDetails;

/**
 * Project Name: 	<bjp>
 * Author List: 		Pankaj Baranwal
 * Filename: 		<>
 * Functions: 		<>
 * Global Variables:	<>
 * Date of Creation:    <07/02/2018>
 */
public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.TimelineViewHolder>{

    private List<TimelineDetails> timelineList = new ArrayList<>();
    Context ctx;
    private static String KEY = "Add your authentication key for google";

    public TimelineAdapter(Context context, List<TimelineDetails> timelineList) {
        this.ctx = context;
        this.timelineList = timelineList;
    }

    @Override
    public TimelineAdapter.TimelineViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_timeline_item, parent, false);
        return new TimelineAdapter.TimelineViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TimelineAdapter.TimelineViewHolder holder, int position) {
        create_UI(holder, position);
        handleOnClicks(holder, position);
    }

    private void handleOnClicks(final TimelineViewHolder holder, final int position) {
        holder.upvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recordAction(0, "na");
            }
        });
        holder.downvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recordAction(1, "na");
            }
        });
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(ctx, CommentsActivity.class);
                ctx.startActivity(intent);
                ((Activity)ctx).overridePendingTransition(0, 0);
            }
        });
        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                share(holder.mItem.getUrl());
            }
        });
        holder.itl_wrapper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void recordAction(int action, String message){
        StringRequest request = new StringRequest(Request.Method.POST, Constants.url_action, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response.length() > 0){

                }else{

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                return params;
            }
        };

        VolleyHandling.getInstance().addToRequestQueue(request, "signin");
    }

    private void share(String url) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Found a great post for you!\n"+url+"\n\nYou can find many more related posts on this cool app:\nVisit <LINE-TO-APP> now!");
        sendIntent.setType("text/plain");
        ctx.startActivity(sendIntent);
    }

    @Override
    public int getItemCount() {
        return timelineList.size();
    }

    private void create_UI(final TimelineViewHolder holder, int position){
        holder.mItem =timelineList.get(position);
        String text;
        switch (holder.mItem.getUI()){
            case 0:
                holder.itl_wrapper.setVisibility(View.VISIBLE);
                Picasso.Builder builder = new Picasso.Builder(ctx);
                builder.listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                        holder.image_item
                                .setImageResource(R.mipmap.ic_launcher);
                    }
                });
                builder.downloader(new OkHttp3Downloader(ctx));
                builder.build().load(holder.mItem.getImage_link()).into(holder.image_item);
                text = holder.mItem.getMessage();
                holder.message_item.setText(text);
                text = holder.message_item.getText().toString();
                if (text.contains("..."))
                    holder.extra.setVisibility(View.VISIBLE);
                break;
            case 1:
                holder.itl_wrapper.setVisibility(View.VISIBLE);
                holder.image_item.setVisibility(View.GONE);
                text = holder.mItem.getMessage();
                holder.message_item.setText(text);
                text = holder.message_item.getText().toString();
                if (text.contains("..."))
                    holder.extra.setVisibility(View.VISIBLE);
                break;
            case 2:
                holder.itl_wrapper.setVisibility(View.VISIBLE);
                Picasso.with(ctx).load(holder.mItem.getImage_link()).fit().error(R.mipmap.ic_launcher).into(holder.image_item);
                holder.message_item.setVisibility(View.GONE);
                break;
            case 3:
                holder.video_wrapper.setVisibility(View.VISIBLE);
                holder.play_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        holder.clickPlayButton(holder.mItem.getUrl());
                    }
                });

                holder.youtube_thumbnail.initialize(KEY, new YouTubeThumbnailView.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, final YouTubeThumbnailLoader youTubeThumbnailLoader) {

                        youTubeThumbnailLoader.setVideo(holder.mItem.getUrl());

                        youTubeThumbnailLoader.setOnThumbnailLoadedListener(new YouTubeThumbnailLoader.OnThumbnailLoadedListener() {
                            @Override
                            public void onThumbnailError(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader.ErrorReason errorReason) {
                                Log.e("YOUTUBE", "ERROR " + errorReason.toString());
                            }

                            @Override
                            public void onThumbnailLoaded(YouTubeThumbnailView youTubeThumbnailView, String s) {
                                youTubeThumbnailView.setVisibility(View.VISIBLE);
                                holder.rl_over_thumbnail.setVisibility(View.VISIBLE);
                                youTubeThumbnailLoader.release();
                            }
                        });
                        holder.title_video.setText(holder.mItem.getMessage());
                    }

                    @Override
                    public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {
                        Log.e("InitializationFailure", youTubeInitializationResult.toString());
                    }
                });
                break;
        }
    }

    public class TimelineViewHolder extends RecyclerView.ViewHolder{

        TimelineDetails mItem;

        RelativeLayout itl_wrapper, video_wrapper, rl_over_thumbnail;
        ImageView image_item;
        TextView message_item, extra;
        YouTubeThumbnailView youtube_thumbnail;
        ImageView play_button;
        TextView title_video;
        LinearLayout upvote, downvote, comment, share;


        public TimelineViewHolder(View itemView) {
            super(itemView);
            itl_wrapper = itemView.findViewById(R.id.itl_wrapper);
            video_wrapper = itemView.findViewById(R.id.video_wrapper);
            image_item = itemView.findViewById(R.id.image_item);
            message_item = itemView.findViewById(R.id.message_item);
            extra = itemView.findViewById(R.id.extra);
            youtube_thumbnail = itemView.findViewById(R.id.youtube_thumbnail);
            play_button = itemView.findViewById(R.id.play_button);
            title_video = itemView.findViewById(R.id.title_video);
            rl_over_thumbnail = itemView.findViewById(R.id.rl_over_thumbnail);
            upvote = itemView.findViewById(R.id.upvote_ll);
            downvote = itemView.findViewById(R.id.downvote_ll);
            comment = itemView.findViewById(R.id.comment_ll);
            share = itemView.findViewById(R.id.share_ll);
        }

        public void clickPlayButton(String url) {
            Intent intent = YouTubeStandalonePlayer.createVideoIntent((Activity) ctx, KEY, url);
            ctx.startActivity(intent);
        }
    }
}