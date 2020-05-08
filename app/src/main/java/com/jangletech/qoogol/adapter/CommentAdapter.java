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
    CommentItemBinding commentItemBinding;
    Activity activity;
    List<Comments> commentList;
    String callingFrom;

    public CommentAdapter(Activity activity, List<Comments> commentList, String callingFrom) {
        this.activity = activity;
        this.commentList = commentList;
        this.callingFrom = callingFrom;
    }

    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        commentItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.comment_item, parent, false);

        return new ViewHolder(commentItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.ViewHolder holder, int position) {
        Comments comments = commentList.get(position);
        if (callingFrom.equals(Module.Learning.toString())) {
            commentItemBinding.tvSenderName.setText(comments.getUserFirstName() + " " + comments.getUserLastName());
            commentItemBinding.textCommentBody.setText(comments.getComment());
            if(comments.getTime()!=null)
            commentItemBinding.textCommentTime.setText(DateUtils.getFormattedDate(comments.getTime().substring(0, 10)));
        }

        if (callingFrom.equals(Module.Test.toString())) {
            commentItemBinding.tvSenderName.setText(comments.getUserFirstName() + " " + comments.getUserLastName());
            commentItemBinding.textCommentBody.setText(comments.getTlc_comment_text());
            if (comments.getTlc_cdatetime() != null)
            commentItemBinding.textCommentTime.setText(DateUtils.getFormattedDate(comments.getTlc_cdatetime().substring(0,10)));//todo change data & time format
        }

        Glide.with(activity)
                .load(getProfileImageUrl(comments))
                .apply(RequestOptions.circleCropTransform())
                .into(commentItemBinding.profilePic);

    }

    public void updateList(List<Comments> commentList) {
        this.commentList.clear();
        this.commentList = commentList;
        notifyDataSetChanged();
    }


    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull CommentItemBinding itemView) {
            super(itemView.getRoot());
        }
    }

    private String getProfileImageUrl(Comments comments) {
        if (callingFrom.equals(Module.Test.toString()))
            return Constant.PRODUCTION_BASE_FILE_API + "000000" + comments.getTlc_user_id() + "/" + comments.getProfile_image();

        else
            return Constant.PRODUCTION_BASE_FILE_API + "000000" + comments.getUserId() + "/" + comments.getProfile_image();
    }
}
