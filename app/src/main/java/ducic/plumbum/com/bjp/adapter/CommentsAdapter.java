package ducic.plumbum.com.bjp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ducic.plumbum.com.bjp.R;
import ducic.plumbum.com.bjp.utils.CommentDetails;

/**
 * Created by pankaj on 18/3/18.
 */

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {
    public List<CommentDetails> commentDetails;
    Context ctx;

    public CommentsAdapter(Context context, List<CommentDetails> commentDetails) {
        this.ctx = context;
        this.commentDetails = commentDetails;
    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_comment_item, parent, false);
        return new CommentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CommentViewHolder holder, int position) {
        CommentDetails commentDetail = commentDetails.get(position);
        holder.message.setText(commentDetail.getMessage());
        holder.name.setText(commentDetail.getUser_name());
    }

    @Override
    public int getItemCount() {
        return commentDetails.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {

        TextView name, message;

        public CommentViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            message = itemView.findViewById(R.id.message);
        }
    }
}