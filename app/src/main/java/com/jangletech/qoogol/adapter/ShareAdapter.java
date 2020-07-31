package com.jangletech.qoogol.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

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
public class ShareAdapter extends RecyclerView.Adapter<ShareAdapter.ViewHolder> {
    private static final String TAG = "ShareAdapter";
    private ShareItemBinding shareItemBinding;
    private List<ShareModel> connectionsList;
    private List<ShareModel> filteredConnectionsList;
    private Activity activity;
    private OnItemClickListener onItemClickListener;

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


   /* @Override
    public Filter getFilter() {
        return shareFilter;
    }

    public void updateFilterList(List<ShareModel> shareModels) {
        filteredConnectionsList = shareModels;
    }

    private Filter shareFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            filteredConnectionsList = connectionsList;
            List<ShareModel> filteredList = new ArrayList<>();
            Log.d(TAG, "performFiltering : " + constraint);
            Log.d(TAG, "performFiltering List Size : " + filteredConnectionsList.size());
            if (constraint == null || constraint.length() == 0) {
                //filteredList.addAll(filteredConnectionsList);
                filteredConnectionsList = connectionsList;
            } else {
                String filterPattern = constraint.toString().toLowerCase();
                Log.d(TAG, "performFiltering Else : " + filterPattern);
                for (ShareModel item : connectionsList) {
                    Log.d(TAG, "performFiltering First Name : " + item.getU_first_name());
                    Log.d(TAG, "performFiltering Last Name : " + item.getU_last_name());
                    if (item.getU_first_name().toLowerCase().contains(filterPattern) ||
                            item.getU_last_name().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
                filteredConnectionsList = filteredList;
            }
            FilterResults results = new FilterResults();
            results.values = filteredConnectionsList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //filteredConnectionsList.clear();
            filteredConnectionsList = (List<ShareModel>) results.values;
            Log.d(TAG, "publishResults : " + filteredConnectionsList.size());
            notifyDataSetChanged();
        }
    };*/

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
