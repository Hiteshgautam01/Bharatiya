package ducic.plumbum.com.bjp.adapter;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import ducic.plumbum.com.bjp.R;
import ducic.plumbum.com.bjp.activity.CommentsActivity;
import ducic.plumbum.com.bjp.interfaces.Posts;
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

    public List<TimelineDetails> timelineList = new ArrayList<>();
    Context ctx;
    private static String KEY = "Add your authentication key for google";
    private Posts post;

    public TimelineAdapter(Context context, List<TimelineDetails> timelineList, Posts post) {
        this.ctx = context;
        this.timelineList.addAll(timelineList);
        this.post = post;
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
        loadMorePosts(position);
    }

    private void loadMorePosts(int position) {
        if (timelineList.size() - position < 4)
            post.loadMorePosts();
    }

    private void handleOnClicks(final TimelineViewHolder holder, final int position) {
        holder.upvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Log.e("TIMELINE DETAILS", Integer.toString(holder.mItem.getVotes()));
                holder.count_votes.setText(String.valueOf(holder.mItem.getVotes() + 1));
                recordAction(1, holder.mItem.getId());
            }
        });
        holder.downvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.count_votes.setText(String.valueOf(holder.mItem.getVotes() - 1));
                recordAction(-1, holder.mItem.getId());
            }
        });
        holder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(ctx, CommentsActivity.class);
                intent.putExtra("post_id", holder.mItem.getId());
                ctx.startActivity(intent);
//                ((Activity)ctx).overridePendingTransition(0, 0);
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
                String url = holder.mItem.getUrl();
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setPackage("com.android.chrome");
                try {
                    ctx.startActivity(i);
                } catch (ActivityNotFoundException e) {
                    i.setPackage(null);
                    ctx.startActivity(i);
                }

            }
        });
    }

    private void recordAction(int action, int post_id){
        if (Constants.post_id.contains(post_id)) {
            int position = Constants.post_id.indexOf(post_id);
            Constants.actions.remove(position);
            Constants.post_id.remove(position);
        }
        Constants.actions.add(action);
        Constants.post_id.add(post_id);
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

    private void create_UI(final TimelineViewHolder holder, final int position){
        Constants.paused_post_id = position;
        holder.mItem =timelineList.get(position);
        String text;
//        holder.source_name.setText(holder.mItem.getSource_name());
        switch (holder.mItem.getUI()){
            case 0:
                holder.itl_wrapper.setVisibility(View.VISIBLE);
                Picasso.with(ctx)
                        .load(holder.mItem.getImage_link())
                        .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                        .config(Bitmap.Config.RGB_565)
                        .into(holder.image_item, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                holder.image_item
                                        .setImageResource(R.mipmap.ic_launcher);
                            }
                        });
                break;
            case 1:
                holder.itl_wrapper.setVisibility(View.VISIBLE);
                holder.image_item.setVisibility(View.GONE);
                break;
            case 2:
                holder.itl_wrapper.setVisibility(View.VISIBLE);
                Picasso.with(ctx)
                        .load(holder.mItem.getImage_link())
                        .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                        .config(Bitmap.Config.RGB_565)
                        .into(holder.image_item, new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                holder.image_item
                                        .setImageResource(R.mipmap.ic_launcher);
                            }
                        });
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
        if (holder.mItem.getVotes()!=0)
            holder.count_votes.setText(Integer.toString(holder.mItem.getVotes()));
        else
            holder.count_votes.setText("Votes");
        text = holder.mItem.getMessage();
        holder.message_item.setText(text);
        text = holder.message_item.getText().toString();
        if (text.contains("..."))
            holder.extra.setVisibility(View.VISIBLE);
        holder.time_text.setText(holder.mItem.getTime());
    }

    public void updateList(List<TimelineDetails> list){
        timelineList.clear();
        timelineList.addAll(list);
        Log.e("myAdapter", list.size()+"");
        this.notifyDataSetChanged();
    }

    public class TimelineViewHolder extends RecyclerView.ViewHolder{

        TimelineDetails mItem;

        RelativeLayout itl_wrapper, video_wrapper, rl_over_thumbnail;
        ImageView image_item;
        TextView count_votes, time_text;
        TextView message_item, extra;
//        TextView source_name;
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
//            source_name = itemView.findViewById(R.id.page_name);
            extra = itemView.findViewById(R.id.extra);
            youtube_thumbnail = itemView.findViewById(R.id.youtube_thumbnail);
            play_button = itemView.findViewById(R.id.play_button);
            title_video = itemView.findViewById(R.id.title_video);
            rl_over_thumbnail = itemView.findViewById(R.id.rl_over_thumbnail);
            upvote = itemView.findViewById(R.id.upvote_ll);
            downvote = itemView.findViewById(R.id.downvote_ll);
            count_votes = itemView.findViewById(R.id.count_votes);
            time_text = itemView.findViewById(R.id.time_text);
            comment = itemView.findViewById(R.id.comment_ll);
            share = itemView.findViewById(R.id.share_ll);
        }

        public void clickPlayButton(String url) {
            Intent intent = YouTubeStandalonePlayer.createVideoIntent((Activity) ctx, KEY, url);
            ctx.startActivity(intent);
        }
    }
}