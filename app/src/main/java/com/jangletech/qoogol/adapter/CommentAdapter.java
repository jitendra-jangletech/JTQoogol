package com.jangletech.qoogol.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.CommentItemBinding;
import com.jangletech.qoogol.enums.Module;
import com.jangletech.qoogol.model.Comments;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.DateUtils;

import java.util.List;

/**
 * Created by Pritali on 4/6/2020.
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    private static final String TAG = "CommentAdapter";
    private CommentItemBinding commentItemBinding;
    private Activity activity;
    private List<Comments> commentList;
    private String callingFrom;
    private onCommentItemClickListener commentItemClickListener;

    public CommentAdapter(Activity activity, List<Comments> commentList, String callingFrom, onCommentItemClickListener commentItemClickListener) {
        this.activity = activity;
        this.commentList = commentList;
        this.callingFrom = callingFrom;
        this.commentItemClickListener = commentItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        commentItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.comment_item, parent, false);
        return new ViewHolder(commentItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comments comments = commentList.get(position);
        if (callingFrom.equals(Module.Learning.toString())) {
            holder.commentItemBinding.tvSenderName.setText(comments.getUserFirstName() + " " + comments.getUserLastName());
            holder.commentItemBinding.textCommentBody.setText(comments.getComment());
            if (comments.getTime() != null)
                holder.commentItemBinding.textCommentTime.setText(DateUtils.localeDateFormat(comments.getTime()));
            //commentItemBinding.textCommentTime.setText(DateUtils.getFormattedDate(comments.getTime().substring(0, 10)));
        }

        if (callingFrom.equals(Module.Test.toString())) {
            holder.commentItemBinding.tvSenderName.setText(comments.getUserFirstName() + " " + comments.getUserLastName());
            holder.commentItemBinding.textCommentBody.setText(comments.getTlc_comment_text());
            if (comments.getTlc_cdatetime() != null)
                holder.commentItemBinding.textCommentTime.setText(DateUtils.localeDateFormat(comments.getTlc_cdatetime()));
            //commentItemBinding.textCommentTime.setText(DateUtils.getFormattedDate(comments.getTlc_cdatetime().substring(0, 10)));//todo change data & time format
        }

        Glide.with(activity)
                .load(getProfileImageUrl(comments))
                .apply(RequestOptions.circleCropTransform())
                .into(holder.commentItemBinding.profilePic);

        holder.commentItemBinding.commentlayouyt.setOnClickListener(v->{
            //Comments comments = commentList.get(getAdapterPosition());
            if (callingFrom.equals(Module.Learning.toString())) {
                commentItemClickListener.onItemClick(comments.getUserId());
            }
            if (callingFrom.equals(Module.Test.toString())) {
                commentItemClickListener.onItemClick(comments.getTlc_user_id());
            }
        });

    }

    /*public void updateList(List<Comments> commentList) {
        this.commentList.clear();
        this.commentList = commentList;
        notifyDataSetChanged();
    }*/


    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public interface onCommentItemClickListener {
        void onItemClick(String userId);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CommentItemBinding commentItemBinding;
        public ViewHolder(@NonNull CommentItemBinding itemView) {
            super(itemView.getRoot());
            this.commentItemBinding = itemView;

            /*commentItemBinding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Comments comments = commentList.get(getAdapterPosition());
                    if (callingFrom.equals(Module.Learning.toString())) {
                        commentItemClickListener.onItemClick(comments.getUserId());
                    }
                    if (callingFrom.equals(Module.Test.toString())) {
                        commentItemClickListener.onItemClick(comments.getTlc_user_id());
                    }
                }
            });*/
        }
    }

    private String getProfileImageUrl(Comments comments) {
        if (callingFrom.equals(Module.Test.toString()))
            return Constant.PRODUCTION_BASE_FILE_API + comments.getProfile_image();
        else
            return Constant.PRODUCTION_BASE_FILE_API + comments.getProfile_image();
    }
}
