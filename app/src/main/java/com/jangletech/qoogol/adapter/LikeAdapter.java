package com.jangletech.qoogol.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.LikeitemBinding;
import com.jangletech.qoogol.model.Like;
import com.jangletech.qoogol.util.UtilHelper;

import java.net.URL;
import java.util.List;

/**
 * Created by Pritali on 6/4/2020.
 */
public class LikeAdapter  extends RecyclerView.Adapter<LikeAdapter.ViewHolder> {
    Activity activity;
    List<Like> likeList;
    LikeitemBinding likeitemBinding;
    onItemClickListener onItemClickListener;

    public LikeAdapter(Activity activity, List<Like> likeList, onItemClickListener onItemClickListener) {
        this.activity = activity;
        this.likeList = likeList;
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
        try {
            Like like = likeList.get(position);
            likeitemBinding.setLike(like);
            if (like.getProfile_image() != null && !like.getProfile_image().isEmpty()) {
                Glide.with(activity).load(UtilHelper.getProfileImageUrl(like.getProfile_image().trim())).circleCrop().placeholder(R.drawable.profile).into(likeitemBinding.userProfileImage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public interface onItemClickListener {
        void onItemCLick(String user_id);
    }

    @Override
    public int getItemCount() {
        return likeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull LikeitemBinding itemView) {
            super(itemView.getRoot());

            likeitemBinding.container.setOnClickListener(v -> {
                Like like = likeList.get(getAdapterPosition());
                onItemClickListener.onItemCLick(like.getUserId());
            });
        }
    }
}
