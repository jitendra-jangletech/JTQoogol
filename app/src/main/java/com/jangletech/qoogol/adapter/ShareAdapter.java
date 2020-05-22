package com.jangletech.qoogol.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.ShareItemBinding;
import com.jangletech.qoogol.model.Connections;
import com.jangletech.qoogol.model.ShareModel;
import com.jangletech.qoogol.util.UtilHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pritali on 5/4/2020.
 */
public class ShareAdapter extends RecyclerView.Adapter<ShareAdapter.ViewHolder> implements Filterable {
    ShareItemBinding shareItemBinding;
    List<ShareModel> connectionsList;
    List<ShareModel> filteredConnectionsList;
    Activity activity;
    OnItemClickListener onItemClickListener;

    public ShareAdapter(Activity activity, List<ShareModel> connectionsList, OnItemClickListener onItemClickListener) {
        this.activity = activity;
        this.connectionsList = connectionsList;
        this.filteredConnectionsList = connectionsList;
        this.onItemClickListener = onItemClickListener;
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
        shareItemBinding.setConnection(connections);
        try {
            if (connections.getRecordType().equalsIgnoreCase("G")) {
                shareItemBinding.tvUserName.setText(connections.getU_first_name());
                Glide.with(activity).load(UtilHelper.getGroupProfilePath(connections.getGroup_id())).circleCrop().placeholder(R.drawable.profile).into(shareItemBinding.userProfileImage);

            } else {
                shareItemBinding.tvUserName.setText(connections.getU_first_name() + " " + connections.getU_last_name());
                Glide.with(activity).load(UtilHelper.getProfilePath(connections.getCn_user_id_2(),connections.getProf_pic().trim())).circleCrop().placeholder(R.drawable.profile).into(shareItemBinding.userProfileImage);
            }

            shareItemBinding.pauseSwitch.setOnClickListener(v -> {
                onItemClickListener.actionPerformed(connections, position);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
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

        void viewProfile(Connections connections, int position);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    connectionsList = filteredConnectionsList;
                } else {
                    List<ShareModel> filteredList = new ArrayList<>();
                    for (ShareModel row : filteredConnectionsList) {
                        if (row.getU_first_name().toLowerCase().contains(charString.toLowerCase()) || row.getU_last_name().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }
                    connectionsList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = connectionsList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults filterResults) {
                connectionsList = (ArrayList<ShareModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull ShareItemBinding itemView) {
            super(itemView.getRoot());
        }
    }
}
