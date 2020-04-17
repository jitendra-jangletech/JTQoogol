package com.jangletech.qoogol.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.HomeitemBinding;
import com.jangletech.qoogol.databinding.SingleChoiceQuestionNewBinding;

import java.util.List;

public class DummyAdapter extends RecyclerView.Adapter<DummyAdapter.MyViewHolder> {

    private Context context;
    private SingleChoiceQuestionNewBinding homeitemBinding;
    private List<String> list;

    public DummyAdapter(Context context) {
        this.context = context;
        //this.list = strings;
    }

    @NonNull
    @Override
    public DummyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        homeitemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.single_choice_question_new, parent, false);
        return new DummyAdapter.MyViewHolder(homeitemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        SingleChoiceQuestionNewBinding homeitemBinding;

        public MyViewHolder(@NonNull SingleChoiceQuestionNewBinding itemView) {
            super(itemView.getRoot());
        }
    }
}
