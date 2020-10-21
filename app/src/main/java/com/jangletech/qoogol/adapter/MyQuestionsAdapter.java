package com.jangletech.qoogol.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.MyquestionsItemBinding;
import com.jangletech.qoogol.model.LearningQuestionsNew;

import java.util.List;

/**
 * Created by Pritali on 10/21/2020.
 */
public class MyQuestionsAdapter extends RecyclerView.Adapter<MyQuestionsAdapter.ViewHolder> {
    MyquestionsItemBinding itemBinding;
    Activity activity;
    List<LearningQuestionsNew> questionsList;
    ItemClickListener itemClickListener;
    private Boolean isScrolling = false;
    private boolean isSearching = false;
    private int currentItems, scrolledOutItems, totalItems;
    LinearLayoutManager linearLayoutManager;

    public MyQuestionsAdapter(Activity activity, List<LearningQuestionsNew> questionsList, ItemClickListener itemClickListener) {
        this.activity = activity;
        this.questionsList = questionsList;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public MyQuestionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.myquestions_item, parent, false);
        return new MyQuestionsAdapter.ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyQuestionsAdapter.ViewHolder holder, int position) {
        LearningQuestionsNew learningQuestionsNew = questionsList.get(position);
        holder.itemBinding.question.setText(learningQuestionsNew.getQuestion());
        holder.itemBinding.type.setText("Type : " + learningQuestionsNew.getCategory());
        holder.itemBinding.marks.setText(learningQuestionsNew.getFormatedMarks());
        holder.itemBinding.question.setText(learningQuestionsNew.getQuestion());

        holder.itemBinding.edit.setOnClickListener(v -> {
            itemClickListener.onEditClick(learningQuestionsNew);
        });
    }

    public interface ItemClickListener {
        void onEditClick(LearningQuestionsNew learningQuestionsNew);
    }

    @Override
    public int getItemCount() {
        return questionsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        MyquestionsItemBinding itemBinding;

        public ViewHolder(MyquestionsItemBinding itemBinding) {
            super(itemBinding.getRoot());
            this.itemBinding =itemBinding;
        }
    }
}
