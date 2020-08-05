package com.jangletech.qoogol.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.TestingItemBinding;

import java.util.List;

public class TestingAdapter extends RecyclerView.Adapter<TestingAdapter.ViewHolder> {

    private List<String> strings;
    private Context mContext;
    private TestingItemBinding itemBinding;

    public TestingAdapter(Context mContext, List<String> strings) {
        this.mContext = mContext;
        this.strings = strings;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.testing_item, parent, false);
        return new ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String s = strings.get(position);
        holder.itemBinding.tvText.setText(s);
    }

    public void updateList(List<String> stringList){
        strings = stringList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return strings.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TestingItemBinding itemBinding;

        public ViewHolder(@NonNull TestingItemBinding itemView) {
            super(itemView.getRoot());
            this.itemBinding = itemView;
        }
    }
}
