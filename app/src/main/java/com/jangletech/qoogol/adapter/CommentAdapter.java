package com.jangletech.qoogol.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.CommentItemBinding;
import com.jangletech.qoogol.enums.Module;
import com.jangletech.qoogol.model.Comments;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.util.AESSecurities;
import com.jangletech.qoogol.util.AppUtils;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.DateUtils;
import com.jangletech.qoogol.util.TinyDB;

import org.apache.commons.lang3.StringUtils;

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
    private int lastPosition = -1;
    private ApiInterface apiService = ApiClient.getInstance().getApi();
    private onCommentItemClickListener commentItemClickListener;
    private String tmId = "";
    private boolean isReply = false;

    public CommentAdapter(Activity activity, List<Comments> commentList,
                          String callingFrom, String tmId, boolean isReply, onCommentItemClickListener commentItemClickListener) {
        this.activity = activity;
        this.commentList = commentList;
        this.callingFrom = callingFrom;
        this.commentItemClickListener = commentItemClickListener;
        this.tmId = tmId;
        this.isReply = isReply;
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

        holder.commentItemBinding.tvSenderName.setText(
                AESSecurities.getInstance().decrypt(TinyDB.getInstance(activity).getString(Constant.cf_key1), comments.getUserFirstName()) + " "
                        + AESSecurities.getInstance().decrypt(TinyDB.getInstance(activity).getString(Constant.cf_key2), comments.getUserLastName())
        );

        if (comments.getReplyCommentCount() > 0) {
            holder.commentItemBinding.tvCommentCount.setVisibility(View.VISIBLE);
        } else {
            holder.commentItemBinding.tvCommentCount.setVisibility(View.GONE);
        }

        if (comments.getReplyLikeCount() > 0) {
            holder.commentItemBinding.tvLikes.setVisibility(View.VISIBLE);
        } else {
            holder.commentItemBinding.tvLikes.setVisibility(View.GONE);
        }

        if (callingFrom.equals(Module.Test.toString()))
            if (comments.getTlc_user_id().equalsIgnoreCase(AppUtils.getUserId())) {
                //self comment
                holder.commentItemBinding.tvDelete.setVisibility(View.VISIBLE);
            } else {
                //other user comment
                holder.commentItemBinding.tvDelete.setVisibility(View.GONE);
            }

        if (callingFrom.equals(Module.Learning.toString()))
            if (comments.getUserId().equalsIgnoreCase(AppUtils.getUserId())) {
                holder.commentItemBinding.tvDelete.setVisibility(View.VISIBLE);
            } else {
                //other user comment
                holder.commentItemBinding.tvDelete.setVisibility(View.GONE);
            }

        if (callingFrom.equals(Module.Learning.toString())) {
            String decoded = AppUtils.decodedString(comments.getComment());
            holder.commentItemBinding.textCommentBody.setText(decoded);
            holder.commentItemBinding.textCommentTime.setText(DateUtils.localeDateFormat(comments.getTime()));
            //commentItemBinding.textCommentTime.setText(DateUtils.getFormattedDate(comments.getTime().substring(0, 10)));
        }

        if (callingFrom.equals(Module.Test.toString())) {
            //Log.d(TAG, "Without Decoding : " + comments.getTlc_comment_text());
            String decoded = AppUtils.decodedString(comments.getTlc_comment_text());
            Log.d(TAG, "decoded  :  " + StringUtils.stripAccents(decoded));
            //String decoded = AppUtils.decodedMessage(StringEscapeUtils.unescapeJava(comments.getTlc_comment_text()));
            //holder.commentItemBinding.tvSenderName.setText(comments.getUserFirstName() + " " + comments.getUserLastName());
            holder.commentItemBinding.textCommentBody.setText(decoded);
            holder.commentItemBinding.textCommentTime.setText(DateUtils.localeDateFormat(comments.getTlc_cdatetime()));
            //commentItemBinding.textCommentTime.setText(DateUtils.getFormattedDate(comments.getTlc_cdatetime().substring(0, 10)));//todo change data & time format
        }

        holder.commentItemBinding.tvLikes.setText(String.valueOf(comments.getReplyLikeCount()));
        holder.commentItemBinding.tvCommentCount.setText(String.valueOf(comments.getReplyCommentCount()));

        if (comments.isLiked()) {
            AppUtils.bounceAnim(activity, holder.commentItemBinding.tvLikes);
            //holder.commentItemBinding.tvLikes.setVisibility(View.VISIBLE);
            //holder.commentItemBinding.tvLikes.setText(String.valueOf(comments.getReplyLikeCount()));
            holder.commentItemBinding.tvLike.setTextColor(activity.getResources().getColor(R.color.colorSkyBlue));
        } else {
            AppUtils.bounceAnim(activity, holder.commentItemBinding.tvLikes);
            //holder.commentItemBinding.tvLikes.setVisibility(View.GONE);
            //holder.commentItemBinding.tvLikes.setText("0");
            holder.commentItemBinding.tvLike.setTextColor(activity.getResources().getColor(android.R.color.tab_indicator_text));
        }

        if (isReply) {
            //hide Like and Reply For Replies Elements
            holder.commentItemBinding.tvLike.setVisibility(View.GONE);
            holder.commentItemBinding.tvReply.setVisibility(View.GONE);
            holder.commentItemBinding.tvLikes.setVisibility(View.GONE);
            holder.commentItemBinding.tvCommentCount.setVisibility(View.GONE);
        }

        Glide.with(activity)
                .load(getProfileImageUrl(comments))
                .apply(RequestOptions.circleCropTransform())
                .into(holder.commentItemBinding.profilePic);

        holder.commentItemBinding.tvDelete.setOnClickListener(v -> {
            //delete comment callback
            commentItemClickListener.onCommentDelete(position, comments);
        });

        holder.commentItemBinding.commentlayouyt.setOnClickListener(v -> {
            //Comments comments = commentList.get(getAdapterPosition());
            if (callingFrom.equals(Module.Learning.toString())) {
                commentItemClickListener.onItemClick(comments.getUserId());
            }
            if (callingFrom.equals(Module.Test.toString())) {
                commentItemClickListener.onItemClick(comments.getTlc_user_id());
            }
        });

        holder.commentItemBinding.tvLike.setOnClickListener(v -> {
            AppUtils.bounceAnim(activity, holder.commentItemBinding.tvLike);
            commentItemClickListener.onLikeClick(position, comments);
        });

        holder.commentItemBinding.tvReply.setOnClickListener(v -> {
            commentItemClickListener.onReplyClick(position, comments);
        });

        holder.commentItemBinding.tvCommentCount.setOnClickListener(v -> {
            commentItemClickListener.onCommentsClick(position, comments);
        });
        setAnimation(holder.commentItemBinding.getRoot(), position);
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(activity, R.anim.fall_down);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }


    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public interface onCommentItemClickListener {
        void onItemClick(String userId);

        void onCommentDelete(int pos, Comments comments);

        void onCommentsClick(int pos, Comments comments);

        void onLikeClick(int pos, Comments comments);

        void onReplyClick(int pos, Comments comments);
    }

    public void deleteComment(int pos) {
        commentList.remove(pos);
        notifyItemRemoved(pos);
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


    public void updateLikeCount(int pos, Comments comments) {
        notifyItemChanged(pos, comments);
    }

    public void updateList(List<Comments> comments) {
        commentList = comments;
        notifyDataSetChanged();
    }

    private String getProfileImageUrl(Comments comments) {
        return Constant.PRODUCTION_BASE_FILE_API + comments.getProfile_image();
    }

    /*public void removeReplyItems(int position) {
        // Remove specified position
        commentList.remove(position);
        // Notify adapter to remove the position
        notifyItemRemoved(position);
        // Notify adapter about data changed
        notifyItemChanged(position);
        // Notify adapter about item range changed
        notifyItemRangeChanged(position, commentList.size());
    }*/
}
