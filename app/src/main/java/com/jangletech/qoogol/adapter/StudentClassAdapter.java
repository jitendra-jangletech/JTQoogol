package com.jangletech.qoogol.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.ItemClassBinding;
import com.jangletech.qoogol.listener.ItemClickListener;
import com.jangletech.qoogol.model.StudentCls;

import java.util.List;


public class StudentClassAdapter extends RecyclerView.Adapter<StudentClassAdapter.CustomViewHolder> {

    List<StudentCls> studentClsList;
    ItemClassBinding itemBinding;
    TestAdapter.TestClickListener testClickListener;
    int row_index = -1;

    public StudentClassAdapter(List<StudentCls> itemlist, TestAdapter.TestClickListener testClickListener) {
        this.studentClsList = itemlist;
    }

    @NonNull
    @Override
    public StudentClassAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_class, parent, false);
        return new StudentClassAdapter.CustomViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentClassAdapter.CustomViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return studentClsList.size();
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ItemClassBinding itemBinding;
        ItemClickListener itemClickListener;

        public CustomViewHolder(@NonNull ItemClassBinding itemView) {
            super(itemView.getRoot());
            this.itemBinding = itemView;
        }

        public void setItemClickListener(ItemClickListener itemClickListener){
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {

        }
    }

}
