package com.jangletech.qoogol.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.ItemQuestBinding;
import com.jangletech.qoogol.model.LearningQuestionsNew;

import java.util.List;

public class AddTestQuestionAdapter extends RecyclerView.Adapter<AddTestQuestionAdapter.ViewHolder>{

    private Context mContext;
    private List<LearningQuestionsNew> learningQuestionsNewList;
    private ItemQuestBinding itemQuestBinding;

    public AddTestQuestionAdapter(Context mContext, List<LearningQuestionsNew> learningQuestionsNewList) {
        this.mContext = mContext;
        this.learningQuestionsNewList = learningQuestionsNewList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemQuestBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.item_quest, parent, false);
        return new AddTestQuestionAdapter.ViewHolder(itemQuestBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return learningQuestionsNewList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ItemQuestBinding itemQuestBinding;
        public ViewHolder(@NonNull ItemQuestBinding itemView) {
            super(itemView.getRoot());
            this.itemQuestBinding = itemView;
        }
    }
}
