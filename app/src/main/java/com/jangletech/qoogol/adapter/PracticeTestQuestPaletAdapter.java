package com.jangletech.qoogol.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.ItemQuestPracticeBinding;
import com.jangletech.qoogol.model.LearningQuestionsNew;

import java.util.List;

public class PracticeTestQuestPaletAdapter extends RecyclerView.Adapter<PracticeTestQuestPaletAdapter.PracticeQuestionViewHolder> {

    Activity activity;
    List<LearningQuestionsNew> questionList;
    ItemQuestPracticeBinding itemBinding;
    QuestClickListener questClickListener;

    public PracticeTestQuestPaletAdapter(Activity activity, List<LearningQuestionsNew> questionList, QuestClickListener questClickListener) {
        this.activity = activity;
        this.questionList = questionList;
        this.questClickListener = questClickListener;
    }


    @NonNull
    @Override
    public PracticeTestQuestPaletAdapter.PracticeQuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_quest_practice, parent, false);
        return new PracticeQuestionViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull PracticeTestQuestPaletAdapter.PracticeQuestionViewHolder holder, int position) {
        LearningQuestionsNew practiceQuestion = questionList.get(position);
        int questNo = position + 1;
        holder.itemBinding.tvQuestNo.setText(practiceQuestion.getQuestion_id());
        holder.itemBinding.tvQuest.setText(practiceQuestion.getQuestion());

        holder.itemBinding.questLayout.setOnClickListener(v -> {
            questClickListener.onQuestionSelected(holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    public class PracticeQuestionViewHolder extends RecyclerView.ViewHolder {
        ItemQuestPracticeBinding itemBinding;

        public PracticeQuestionViewHolder(@NonNull ItemQuestPracticeBinding itemView) {
            super(itemView.getRoot());
            this.itemBinding = itemView;
        }
    }

    public interface QuestClickListener {
        void onQuestionSelected(int pos);
    }
}
