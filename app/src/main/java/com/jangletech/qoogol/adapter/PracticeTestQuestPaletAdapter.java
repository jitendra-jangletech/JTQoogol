package com.jangletech.qoogol.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.ItemQuestPracticeBinding;
import com.jangletech.qoogol.databinding.QuestPaletItemBinding;
import com.jangletech.qoogol.enums.QuestionSortType;
import com.jangletech.qoogol.model.LearningQuestionsNew;
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

        if(strSortType.equalsIgnoreCase(QuestionSortType.LIST.toString())) {
            holder.itemBinding.tvQuestNo.setText(practiceQuestion.getTq_quest_seq_num());
            holder.itemBinding.tvQuest.setText(practiceQuestion.getQ_quest());

            holder.itemBinding.questLayout.setOnClickListener(v -> {
                questClickListener.onQuestionSelected(holder.getAdapterPosition());
            });
        }

        if(strSortType.equalsIgnoreCase(QuestionSortType.GRID.toString())){
            holder.itemBindingGrid.tvQuestNo.setText(practiceQuestion.getTq_quest_seq_num());
            holder.itemBindingGrid.tvQuestNo.setOnClickListener(v->{
                questClickListener.onQuestionSelected(holder.getAdapterPosition());
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

    public void setPaletFilterResults(List<TestQuestionNew> result) {
        questionList = result;
        notifyDataSetChanged();
    }

    public interface QuestClickListener {
        void onQuestionSelected(int pos);
    }
}
