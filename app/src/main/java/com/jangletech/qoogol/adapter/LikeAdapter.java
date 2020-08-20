package com.jangletech.qoogol.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.LikeitemBinding;
import com.jangletech.qoogol.model.Like;
import com.jangletech.qoogol.util.UtilHelper;

import java.util.List;

/**
 * Created by Pritali on 6/4/2020.
 */
public class LikeAdapter extends RecyclerView.Adapter<LikeAdapter.ViewHolder> {
    private Activity activity;
    private List<Like> likeList;
    private LikeitemBinding likeitemBinding;
    private onItemClickListener onItemClickListener;
    private int lastPosition = -1;
    private boolean isCallFromTest;

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(activity, R.anim.fall_down);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    public LikeAdapter(Activity activity, List<Like> likeList, boolean isCallFromTest, onItemClickListener onItemClickListener) {
        this.activity = activity;
        this.likeList = likeList;
        this.isCallFromTest = isCallFromTest;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public LikeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        likeitemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.likeitem, parent, false);
        return new ViewHolder(likeitemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull LikeAdapter.ViewHolder holder, int position) {
        Like like = likeList.get(position);
        try {
            if (like.getProfile_image() != null && !like.getProfile_image().isEmpty()) {
                Glide.with(activity).load(UtilHelper.getProfileImageUrl(like.getProfile_image().trim())).circleCrop().placeholder(R.drawable.profile).into(holder.likeitemBinding.userProfileImage);
            }
            holder.likeitemBinding.tvUserName.setText(like.getUserFirstName() + " " + like.getUserLastName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        likeitemBinding.container.setOnClickListener(v -> {
            if (isCallFromTest)
                onItemClickListener.onItemCLick(like.getTlcUserId());
            else
                onItemClickListener.onItemCLick(like.getUserId());
        });

        setAnimation(holder.likeitemBinding.getRoot(), position);
    }

    public interface onItemClickListener {
        void onItemCLick(String user_id);
    }

    @Override
    public int getItemCount() {
        return likeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LikeitemBinding likeitemBinding;

        public ViewHolder(@NonNull LikeitemBinding itemView) {
            super(itemView.getRoot());
            this.likeitemBinding = itemView;
        }
    }
}
