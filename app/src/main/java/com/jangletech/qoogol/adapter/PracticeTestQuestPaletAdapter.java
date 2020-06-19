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
import com.jangletech.qoogol.databinding.QuestPaletItemBinding;
import com.jangletech.qoogol.enums.QuestionSortType;
import com.jangletech.qoogol.model.TestQuestionNew;

import java.util.List;

public class PracticeTestQuestPaletAdapter extends RecyclerView.Adapter<PracticeTestQuestPaletAdapter.PracticeQuestionViewHolder> {

    private Activity activity;
    private List<TestQuestionNew> questionList;
    private ItemQuestPracticeBinding itemBinding;
    private QuestPaletItemBinding itemGridQuestBinding;
    private QuestClickListener questClickListener;
    private String strSortType;

    public PracticeTestQuestPaletAdapter(Activity activity, List<TestQuestionNew> questionList,
                                         QuestClickListener questClickListener, String sortType) {
        this.activity = activity;
        this.questionList = questionList;
        this.questClickListener = questClickListener;
        this.strSortType = sortType;
    }

    @NonNull
    @Override
    public PracticeTestQuestPaletAdapter.PracticeQuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (strSortType.equalsIgnoreCase(QuestionSortType.LIST.toString())) {
            itemBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()),
                    R.layout.item_quest_practice, parent, false);
            return new PracticeQuestionViewHolder(itemBinding);
        }

        if (strSortType.equalsIgnoreCase(QuestionSortType.GRID.toString())) {
            itemGridQuestBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()),
                    R.layout.quest_palet_item, parent, false);
            return new PracticeQuestionViewHolder(itemGridQuestBinding);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull PracticeTestQuestPaletAdapter.PracticeQuestionViewHolder holder, int position) {
        TestQuestionNew practiceQuestion = questionList.get(position);

        if (strSortType.equalsIgnoreCase(QuestionSortType.LIST.toString())) {
            holder.itemBinding.tvQuestNo.setText(String.valueOf(practiceQuestion.getTq_quest_seq_num()));
            holder.itemBinding.tvQuest.setText(practiceQuestion.getQ_quest());

            if (practiceQuestion.isTtqa_visited()) {
                holder.itemBinding.tvQuestNo.setBackground(activity.getResources().getDrawable(R.drawable.bg_quest_visited));
                holder.itemBinding.tvQuestNo.setTextColor(activity.getResources().getColor(R.color.colorWhite));
            }

            if (practiceQuestion.isTtqa_attempted()) {
                holder.itemBinding.tvQuestNo.setBackground(activity.getResources().getDrawable(R.drawable.bg_quest_attempted));
                holder.itemBinding.tvQuestNo.setTextColor(activity.getResources().getColor(R.color.colorWhite));
            }

            if (practiceQuestion.isTtqa_marked()) {
                holder.itemBinding.marked.setVisibility(View.VISIBLE);
            }

            holder.itemBinding.questLayout.setOnClickListener(v -> {
                questClickListener.onQuestionSelected(practiceQuestion,holder.getAdapterPosition());
            });
        }

        if (strSortType.equalsIgnoreCase(QuestionSortType.GRID.toString())) {
            holder.itemBindingGrid.tvQuestNo.setText(String.valueOf(practiceQuestion.getTq_quest_seq_num()));

            if (practiceQuestion.isTtqa_visited()) {
                holder.itemBindingGrid.tvQuestNo.setBackground(activity.getResources().getDrawable(R.drawable.bg_quest_visited));
                holder.itemBindingGrid.tvQuestNo.setTextColor(activity.getResources().getColor(R.color.colorWhite));
            }

            if (practiceQuestion.isTtqa_attempted()) {
                holder.itemBindingGrid.tvQuestNo.setBackground(activity.getResources().getDrawable(R.drawable.bg_quest_attempted));
                holder.itemBindingGrid.tvQuestNo.setTextColor(activity.getResources().getColor(R.color.colorWhite));
            }

            if (practiceQuestion.isTtqa_marked()) {
                holder.itemBindingGrid.marked.setVisibility(View.VISIBLE);
            }

            holder.itemBindingGrid.tvQuestNo.setOnClickListener(v -> {
                questClickListener.onQuestionSelected(practiceQuestion,holder.getAdapterPosition());
            });
        }
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    public class PracticeQuestionViewHolder extends RecyclerView.ViewHolder {
        ItemQuestPracticeBinding itemBinding;
        QuestPaletItemBinding itemBindingGrid;

        public PracticeQuestionViewHolder(@NonNull ItemQuestPracticeBinding itemView) {
            super(itemView.getRoot());
            this.itemBinding = itemView;
        }

        public PracticeQuestionViewHolder(@NonNull QuestPaletItemBinding itemView) {
            super(itemView.getRoot());
            this.itemBindingGrid = itemView;
        }
    }

    public void setSortedQuestList(List<TestQuestionNew> sortedQuest){
        this.questionList = sortedQuest;
        notifyDataSetChanged();
    }

    public interface QuestClickListener {
        void onQuestionSelected(TestQuestionNew testQuestionNew,int pos);
    }
}
