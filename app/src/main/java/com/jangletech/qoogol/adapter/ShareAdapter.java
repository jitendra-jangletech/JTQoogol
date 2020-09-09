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
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.ShareItemBinding;
import com.jangletech.qoogol.model.ShareModel;
import com.jangletech.qoogol.util.AppUtils;
import com.jangletech.qoogol.util.UtilHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pritali on 5/4/2020.
 */
public class ShareAdapter extends RecyclerView.Adapter<ShareAdapter.ViewHolder> {
    private static final String TAG = "ShareAdapter";
    private ShareItemBinding shareItemBinding;
    private List<ShareModel> connectionsList;
    private List<ShareModel> filteredConnectionsList;
    private Activity activity;
    private OnItemClickListener onItemClickListener;
    private int lastPosition = -1;

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(activity, R.anim.fall_down);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    public ShareAdapter(Activity activity, List<ShareModel> connectionsList, OnItemClickListener onItemClickListener) {
        this.activity = activity;
        this.connectionsList = connectionsList;
        this.onItemClickListener = onItemClickListener;
        filteredConnectionsList = new ArrayList<>(connectionsList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        shareItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.share_item, parent, false);
        return new ViewHolder(shareItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShareModel connections = connectionsList.get(position);
        holder.shareItemBinding.setConnection(connections);
        try {
            if (connections.getRecordType().equalsIgnoreCase("G")) {
                Log.d(TAG, "onBindViewHolder First Name : "+ AppUtils.decodedString(connections.getU_first_name()));
                Log.d(TAG, "onBindViewHolder Last Name : "+AppUtils.decodedString(connections.getU_last_name()));
                holder.shareItemBinding.tvUserName.setText(connections.getU_first_name());
                if (connections.getProf_pic() != null && !connections.getProf_pic().isEmpty()) {
                    Glide.with(activity).load(UtilHelper.getProfileImageUrl(connections.getProf_pic().trim())).circleCrop().placeholder(R.drawable.profile).into(holder.shareItemBinding.userProfileImage);
                }
            } else {
                holder.shareItemBinding.tvUserName.setText(connections.getU_first_name() + " " + connections.getU_last_name());
                if (connections.getProf_pic() != null && !connections.getProf_pic().isEmpty()) {
                    Glide.with(activity).load(UtilHelper.getProfileImageUrl(connections.getProf_pic().trim())).circleCrop().placeholder(R.drawable.profile).into(holder.shareItemBinding.userProfileImage);
                }
            }

            holder.shareItemBinding.pauseSwitch.setOnClickListener(v -> {
                onItemClickListener.actionPerformed(connections, position);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        setAnimation(holder.shareItemBinding.getRoot(), position);
    }

    @Override
    public int getItemCount() {
        return connectionsList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public interface OnItemClickListener {
        void actionPerformed(ShareModel connections, int position);

        void onBottomReached(int position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ShareItemBinding shareItemBinding;

        public ViewHolder(@NonNull ShareItemBinding itemView) {
            super(itemView.getRoot());
            this.shareItemBinding = itemView;
        }
    }

    public void animateTo(List<ShareModel> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateAdditions(List<ShareModel> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final ShareModel model = newModels.get(i);
            if (!connectionsList.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<ShareModel> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            ShareModel model = newModels.get(toPosition);
            final int fromPosition = connectionsList.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public void updateList(List<ShareModel> shareModels) {
        Log.d(TAG, "updateList : " + shareModels.size());
        connectionsList = shareModels;
        notifyDataSetChanged();
    }

    public ShareModel removeItem(int position) {
        final ShareModel model = connectionsList.remove(position);
        notifyItemRemoved(position);
        return model;
    }

    public void addItem(int position, ShareModel model) {
        connectionsList.add(position, model);
        notifyItemInserted(position);
    }

    public void moveItem(int fromPosition, int toPosition) {
        ShareModel model = connectionsList.remove(fromPosition);
        connectionsList.add(toPosition, model);
        notifyItemMoved(fromPosition, toPosition);
    }

    private void applyAndAnimateRemovals(List<ShareModel> newModels) {
        for (int i = connectionsList.size() - 1; i >= 0; i--) {
            ShareModel model = connectionsList.get(i);
            if (!newModels.contains(model)) {
                removeItem(i);
            }
        }
    }
}
