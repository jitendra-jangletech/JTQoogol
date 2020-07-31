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
import com.jangletech.qoogol.databinding.DoubtGroupItemBinding;
import com.jangletech.qoogol.model.DoubtGroups;
import com.jangletech.qoogol.util.UtilHelper;

import java.util.List;

/**
 * Created by Pritali on 7/31/2020.
 */
public class DoubtGroupAdapter extends RecyclerView.Adapter<DoubtGroupAdapter.ViewHolder> {
    private Activity activity;
    private List<DoubtGroups> doubtGroupsList;
    DoubtGroupItemBinding doubtGroupItemBinding;

    public DoubtGroupAdapter(Activity activity, List<DoubtGroups> doubtGroupsList) {
        this.activity = activity;
        this.doubtGroupsList = doubtGroupsList;
    }

    @NonNull
    @Override
    public DoubtGroupAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        doubtGroupItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.doubt_group_item, parent, false);
        return new DoubtGroupAdapter.ViewHolder(doubtGroupItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull DoubtGroupAdapter.ViewHolder holder, int position) {

        DoubtGroups doubtGroups = doubtGroupsList.get(position);
        if (doubtGroups.getProfile() != null && !doubtGroups.getProfile().isEmpty()) {
            Glide.with(activity).load(UtilHelper.getProfileImageUrl(doubtGroups.getProfile().trim())).circleCrop().placeholder(R.drawable.profile).into(doubtGroupItemBinding.userProfileImage);
        }

        doubtGroupItemBinding.tvUserName.setText(doubtGroups.getDoubt_group());
        if (!doubtGroups.getIs_joined().equalsIgnoreCase("true")) {
            doubtGroupItemBinding.join.setVisibility(View.VISIBLE);
        } else {
            doubtGroupItemBinding.join.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return doubtGroupsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull DoubtGroupItemBinding itemView) {
            super(itemView.getRoot());
        }
    }
}
