package com.jangletech.qoogol.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.ItemQuestPracticeBinding;
import com.jangletech.qoogol.model.PracticeQuestion;
import java.util.List;

public class PracticeTestQuestPaletAdapter extends RecyclerView.Adapter<PracticeTestQuestPaletAdapter.PracticeQuestionViewHolder>  {

    Activity activity;
    List<PracticeQuestion> questionList;
    ItemQuestPracticeBinding itemBinding;
    QuestClickListener questClickListener;

    public PracticeTestQuestPaletAdapter(Activity activity,List<PracticeQuestion> questionList,QuestClickListener questClickListener) {
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
        PracticeQuestion practiceQuestion = questionList.get(position);
        int questNo = position + 1;
        itemBinding.tvQuestNo.setText(String.valueOf(questNo));

        if(practiceQuestion.isContainsMath()){
            itemBinding.tvQuestMath.setVisibility(View.VISIBLE);
            itemBinding.tvQuestMath.setText(practiceQuestion.getQuestion());
            itemBinding.tvQuest.setVisibility(View.GONE);
        }else{
            itemBinding.tvQuest.setText(practiceQuestion.getQuestion());
        }

        itemBinding.questLayout.setOnClickListener(v->{
            questClickListener.onQuestionSelected(position);
        });
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    public class PracticeQuestionViewHolder extends RecyclerView.ViewHolder {

        public PracticeQuestionViewHolder(@NonNull ItemQuestPracticeBinding itemView) {
            super(itemView.getRoot());
        }
    }

    public interface QuestClickListener {
        void onQuestionSelected(int position);
    }
}
