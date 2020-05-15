package com.jangletech.qoogol.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.ContactFilterItemBinding;

import java.util.List;
import java.util.Objects;

/**
 * Created by Pritali on 5/12/2020.
 */
public class ContactFilterAdapter extends RecyclerView.Adapter<ContactFilterAdapter.ViewHolder> {

    ContactFilterItemBinding itemBinding;
    Activity activity;
    List<String> filterList;
    OnFilterItemClickListener listener;
    int previous=0;
    RecyclerView recyclerview;
    TextView previousTextview = null;
    boolean isFirst = true;

    public ContactFilterAdapter(Activity activity, List<String> filterList, OnFilterItemClickListener listener, RecyclerView recyclerview) {
        this.activity = activity;
        this.filterList = filterList;
        this.listener = listener;
        this.recyclerview = recyclerview;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.contact_filter_item, parent, false);

        return new ViewHolder(itemBinding);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final String letter = filterList.get(position);
        itemBinding.alphabet.setText(letter);

        if (position==previous)
            holder.itemView.findViewById(R.id.alphabet).setBackgroundResource(R.drawable.contactfilter_active_bg);


        itemBinding.alphabet.setOnClickListener(v -> {
            try {
                listener.onFilterClick(letter,position);
                holder.itemView.findViewById(R.id.alphabet).setBackgroundResource(R.drawable.contactfilter_active_bg);

                if (previousTextview !=null) {
                    previousTextview.setBackgroundResource(R.drawable.contactfilter_bg);
                    notifyItemChanged(previous);
                }
                previousTextview = Objects.requireNonNull(recyclerview.findViewHolderForAdapterPosition(position)).itemView.findViewById(R.id.alphabet);
                previous = position;

                if (isFirst) {
                    Objects.requireNonNull(recyclerview.findViewHolderForAdapterPosition(0)).itemView.findViewById(R.id.alphabet).setBackgroundResource(R.drawable.contactfilter_bg);
                    isFirst = false;
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
        public ViewHolder(@NonNull ContactFilterItemBinding itemView) {
            super(itemView.getRoot());
//            if (selected.equalsIgnoreCase(filterList.get(getAdapterPosition())))
//                itemBinding.alphabet.setBackgroundResource(R.drawable.contactfilter_active_bg);
        }
    }
}
