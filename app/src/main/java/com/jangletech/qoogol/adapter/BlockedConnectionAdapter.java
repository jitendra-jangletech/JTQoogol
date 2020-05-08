package com.jangletech.qoogol.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.BlockedItemBinding;
import com.jangletech.qoogol.model.Connections;
import com.jangletech.qoogol.util.UtilHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pritali on 5/7/2020.
 */
public class BlockedConnectionAdapter extends RecyclerView.Adapter<BlockedConnectionAdapter.ViewHolder> implements Filterable {

    Activity activity;
    List<Connections> connectionsList;
    List<Connections> filteredConnectionsList;
    BlockedItemBinding blockedItemBinding;
    BlockedItemClick blockedItemClick;

    public BlockedConnectionAdapter(Activity activity, List<Connections> connectionsList, BlockedItemClick blockedItemClick) {
        this.activity = activity;
        this.connectionsList = connectionsList;
        this.filteredConnectionsList = connectionsList;
        this.blockedItemClick = blockedItemClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        blockedItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.blocked_item, parent, false);
        return new ViewHolder(blockedItemBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Connections connections = connectionsList.get(position);
        blockedItemBinding.tvUserName.setText(connections.getU_first_name() + " " + connections.getU_last_name());
        try {
            Glide.with(activity).load(UtilHelper.getProfilePath(connections.getCn_user_id_2(),connections.getProf_pic().trim())).circleCrop().placeholder(R.drawable.profile).into(blockedItemBinding.userProfileImage);

        } catch (Exception e) {
            e.printStackTrace();
        }

        blockedItemBinding.unblock.setOnClickListener(v -> blockedItemClick.unblockUser(connections.getCn_user_id_2()));
    }

    public interface BlockedItemClick {
        void unblockUser(String userId);
    }

    @Override
    public int getItemCount() {
        return connectionsList.size();
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
                    List<Connections> filteredList = new ArrayList<>();
                    for (Connections row : filteredConnectionsList) {
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
                connectionsList = (ArrayList<Connections>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
