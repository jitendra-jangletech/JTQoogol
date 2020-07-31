package com.jangletech.qoogol.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.ContactFilterItemBinding;

import java.util.List;

/**
 * Created by Pritali on 5/12/2020.
 */
public class ContactFilterAdapter extends RecyclerView.Adapter<ContactFilterAdapter.ViewHolder> {

    private List<String> filterList;
    private OnFilterItemClickListener listener;
    private int previous = 0;

    public ContactFilterAdapter(List<String> filterList, OnFilterItemClickListener listener) {
        this.filterList = filterList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ContactFilterItemBinding itemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.contact_filter_item, parent, false);

        return new ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final String letter = filterList.get(position);
        if (position == previous) {
            holder.itemBinding.alphabet.setChecked(true);
        } else {
            holder.itemBinding.alphabet.setChecked(false);
        }
        holder.itemBinding.alphabet.setText(letter);

        holder.itemBinding.alphabet.setOnCheckedChangeListener((buttonView, isChecked) -> {
            try {
                if (isChecked) {
                    listener.onFilterClick(letter, position);
                    previous = position;
                    notifyDataSetChanged();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
    }

    public interface OnFilterItemClickListener {
        void onFilterClick(String letter, int position);
    }

    @Override
    public int getItemCount() {
        return filterList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ContactFilterItemBinding itemBinding;
        public ViewHolder(@NonNull ContactFilterItemBinding itemView) {
            super(itemView.getRoot());
            this.itemBinding = itemView;
        }
    }
}
