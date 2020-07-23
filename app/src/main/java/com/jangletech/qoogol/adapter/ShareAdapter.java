package com.jangletech.qoogol.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.ShareItemBinding;
import com.jangletech.qoogol.model.ShareModel;
import com.jangletech.qoogol.util.UtilHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pritali on 5/4/2020.
 */
public class ShareAdapter extends RecyclerView.Adapter<ShareAdapter.ViewHolder> implements Filterable {
    private static final String TAG = "ShareAdapter";
    private ShareItemBinding shareItemBinding;
    private List<ShareModel> connectionsList, shareList;
    private List<ShareModel> filteredConnectionsList;
    private Activity activity;
    private OnItemClickListener onItemClickListener;

    public ShareAdapter(Activity activity, List<ShareModel> connectionsList, OnItemClickListener onItemClickListener) {
        this.activity = activity;
        this.connectionsList = connectionsList;
        this.onItemClickListener = onItemClickListener;
        shareList = new ArrayList<>(connectionsList);
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
        if (connections.getRecordType().equalsIgnoreCase("G")) {
            holder.shareItemBinding.tvUserName.setText(connections.getU_first_name());
            if (connections.getProf_pic() != null && !connections.getProf_pic().isEmpty()) {
                Log.d(TAG, "onBindViewHolder Image Url : " + UtilHelper.getProfileImageUrl(connections.getProf_pic().trim()));
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

    public void updateShareList(List<ShareModel> updatedList){
        connectionsList = updatedList;
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return shareFilter;
    }

    private Filter shareFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ShareModel> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(shareList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (ShareModel item : shareList) {
                    if (item.getU_first_name().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            connectionsList.clear();
            connectionsList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public interface OnItemClickListener {
        void actionPerformed(ShareModel connections, int position);
        void onBottomReached(int position);
    }

    public void filterList(List<ShareModel> resultShareModelList) {
        connectionsList = resultShareModelList;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ShareItemBinding shareItemBinding;

        public ViewHolder(@NonNull ShareItemBinding itemView) {
            super(itemView.getRoot());
            this.shareItemBinding = itemView;
        }
    }
}
