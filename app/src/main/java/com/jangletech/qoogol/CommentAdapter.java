package com.jangletech.qoogol;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.jangletech.qoogol.databinding.CommentItemBinding;
import com.jangletech.qoogol.model.Comments;

import java.util.List;

/**
 * Created by Pritali on 4/6/2020.
 */
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    CommentItemBinding commentItemBinding;
    Activity activity;
    List<Comments> commentList;

    public CommentAdapter(Activity activity, List<Comments> commentList) {
        this.activity = activity;
        this.commentList = commentList;
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
        commentItemBinding.tvSenderName.setText(comments.getUserName());
        commentItemBinding.textCommentBody.setText(comments.getComment());
        commentItemBinding.textCommentTime.setText(comments.getTime());
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
}
