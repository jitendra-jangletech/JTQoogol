package com.jangletech.qoogol.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.ShareItemBinding;
import com.jangletech.qoogol.model.Connections;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pritali on 5/4/2020.
 */
public class ShareAdapter extends RecyclerView.Adapter<ShareAdapter.ViewHolder> implements Filterable {
    ShareItemBinding shareItemBinding;
    List<Connections> connectionsList;
    List<Connections> filteredConnectionsList;
    Activity activity;

    public ShareAdapter(Activity activity, List<Connections> connectionsList) {
        this.activity = activity;
        this.connectionsList = connectionsList;
        this.filteredConnectionsList = connectionsList;
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
        Connections connections = connectionsList.get(position);
        shareItemBinding.tvUserName.setText(connections.getU_first_name() + " " + connections.getU_last_name());
//        Glide.with(activity).load(UtilHelper.getProfilePath("1069", connections.getProf_pic().trim()).trim()).into(shareItemBinding.userProfileImage);


    }

    @Override
    public int getItemCount() {
        return connectionsList.size();
    }

    public void updateList(List<Connections> connectionsList) {
        this.connectionsList.clear();
        this.connectionsList = connectionsList;
        this.filteredConnectionsList = connectionsList;
        notifyDataSetChanged();
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
        public ViewHolder(@NonNull ShareItemBinding itemView) {
            super(itemView.getRoot());
        }
    }
}
