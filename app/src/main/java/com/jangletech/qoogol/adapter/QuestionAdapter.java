package com.jangletech.qoogol.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.ItemQuestionBinding;
import com.jangletech.qoogol.databinding.TestDetailQuestionItemBinding;
import com.jangletech.qoogol.model.QSet;

import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {

    Activity activity;
    List<QSet> questionList;
    TestDetailQuestionItemBinding itemBinding;
    ItemQuestionBinding itemQuestionBinding;

    public QuestionAdapter(Activity activity, List<QSet> questionList) {
        this.activity = activity;
        this.questionList = questionList;
    }

    @NonNull
    @Override
    public QuestionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.test_detail_question_item, parent, false);
        return new QuestionAdapter.ViewHolder(itemBinding);

    }

    @Override
    public void onBindViewHolder(@NonNull QuestionAdapter.ViewHolder holder, int position) {
        QSet question = questionList.get(position);
        holder.itemQuestionBinding.tvQuestNo.setText("" + question.getQ_id());
        holder.itemQuestionBinding.tvQuestDesc.setText(question.getQ_quest());
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TestDetailQuestionItemBinding itemQuestionBinding;
        ItemQuestionBinding itemQuestionBindingForPalet;

        public ViewHolder(@NonNull TestDetailQuestionItemBinding itemView) {
            super(itemView.getRoot());
            this.itemQuestionBinding = itemView;
        }

        public ViewHolder(@NonNull ItemQuestionBinding itemView) {
            super(itemView.getRoot());
            this.itemQuestionBindingForPalet = itemView;
        }
    }
}
