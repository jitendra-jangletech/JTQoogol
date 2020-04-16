package com.jangletech.qoogol.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jangletech.qoogol.CourseActivity;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.ItemQuestionBinding;
import com.jangletech.qoogol.databinding.TestDetailQuestionItemBinding;
import com.jangletech.qoogol.model.Question;

import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.ViewHolder> {

    Activity activity;
    List<Question> questionList;
    TestDetailQuestionItemBinding itemBinding;
    ItemQuestionBinding itemQuestionBinding;

    public QuestionAdapter(Activity activity, List<Question> questionList) {
        this.activity = activity;
        this.questionList = questionList;
    }

    @NonNull
    @Override
    public QuestionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (activity instanceof CourseActivity) {
            itemQuestionBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()),
                    R.layout.item_question, parent, false);

            return new QuestionAdapter.ViewHolder(itemQuestionBinding);
        } else {
            itemBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()),
                    R.layout.test_detail_question_item, parent, false);

            return new QuestionAdapter.ViewHolder(itemBinding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionAdapter.ViewHolder holder, int position) {
        Question question = questionList.get(position);
        if (activity instanceof CourseActivity) {
            holder.itemQuestionBindingForPalet.tvQuestNo.setText(String.valueOf(question.getId()));
            holder.itemQuestionBindingForPalet.tvQuestDesc.setText(question.getStrQuestion());
        } else {
            holder.itemQuestionBinding.tvQuestNo.setText("" + question.getId());
            holder.itemQuestionBinding.tvQuestDesc.setText(question.getStrQuestion());
            holder.itemQuestionBinding.tvQuestInfo.setText(question.getStrQuestInfo());
        }
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
