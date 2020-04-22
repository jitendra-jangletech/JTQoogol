package com.jangletech.qoogol.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.ItemQuestionBinding;
import com.jangletech.qoogol.databinding.QuestPaletItemBinding;
import com.jangletech.qoogol.enums.QuestionSortType;
import com.jangletech.qoogol.model.TestQuestion;
import java.util.List;

public class QuestionPaletAdapter extends RecyclerView.Adapter<QuestionPaletAdapter.QuestionPaletViewHolder> {

    private static final String TAG = "QuestionPaletAdapter";
    List<TestQuestion> questions;
    QuestPaletItemBinding itemGridQuestBinding;
    ItemQuestionBinding itemListQuestBinding;
    QuestClickListener questClickListener;
    String strSortType;
    Context context;

    private void printList() {
        if (questions != null) {
            for (int i = 0; i < questions.size(); i++) {
                TestQuestion question = questions.get(i);
                Log.d(TAG, "Quest ID: " + question.getQuestId());
                Log.d(TAG, "Quest NO: " + question.getQuestNo());
                Log.d(TAG, "Quest Desc: " + question.getQuestionDesc());
                Log.d(TAG, "Quest Type : " + question.getQuestType());
                Log.d(TAG, "Marked : " + question.isMarked());
                Log.d(TAG, "Saved : " + question.isSaved());
                Log.d(TAG, "Visited : " + question.isVisited());
                Log.d(TAG, "Descriptive : " + question.getDescriptiveAns());
            }
        }
    }

    public QuestionPaletAdapter(List<TestQuestion> itemlist, QuestClickListener questClickListener,
                                String sortType, Context mContext) {
        this.questions = itemlist;
        this.questClickListener = questClickListener;
        this.strSortType = sortType;
        this.context = mContext;
        //printList();
    }

    @NonNull
    @Override
    public QuestionPaletAdapter.QuestionPaletViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (strSortType.equalsIgnoreCase(QuestionSortType.GRID.toString())) {
            itemGridQuestBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()),
                    R.layout.quest_palet_item, parent, false);
            return new QuestionPaletAdapter.QuestionPaletViewHolder(itemGridQuestBinding);
        }

        if (strSortType.equalsIgnoreCase(QuestionSortType.LIST.toString())) {
            itemListQuestBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()),
                    R.layout.item_question, parent, false);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            itemListQuestBinding.getRoot().setLayoutParams(lp);
            return new QuestionPaletAdapter.QuestionPaletViewHolder(itemListQuestBinding);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionPaletAdapter.QuestionPaletViewHolder holder, int position) {
        TestQuestion question = questions.get(position);
        if (strSortType.equalsIgnoreCase(QuestionSortType.GRID.toString())) {
            holder.itemGridBinding.tvQuestNo.setText(String.valueOf(question.getQuestNo()));

            if (question.isVisited()) {
                holder.itemGridBinding.tvQuestNo.setTextColor(context.getResources().getColor(R.color.colorWhite));
                holder.itemGridBinding.tvQuestNo.setBackground(context.getResources().getDrawable(R.drawable.bg_quest_visited));
            }

            if (question.isMarked() && question.isVisited()) {
                holder.itemGridBinding.tvQuestNo.setTextColor(context.getResources().getColor(R.color.colorWhite));
                holder.itemGridBinding.tvQuestNo.setBackground(context.getResources().getDrawable(R.drawable.bg_quest_visited));
                holder.itemGridBinding.marked.setVisibility(View.VISIBLE);
            }

            if (question.isMarked() && question.isAttempted()) {
                holder.itemGridBinding.tvQuestNo.setTextColor(context.getResources().getColor(R.color.colorWhite));
                holder.itemGridBinding.tvQuestNo.setBackground(context.getResources().getDrawable(R.drawable.bg_quest_attempted));
                holder.itemGridBinding.marked.setVisibility(View.VISIBLE);
            }

            if (question.isAttempted()) {
                holder.itemGridBinding.tvQuestNo.setTextColor(context.getResources().getColor(R.color.colorWhite));
                holder.itemGridBinding.tvQuestNo.setBackground(context.getResources().getDrawable(R.drawable.bg_quest_attempted));
            }

            holder.itemGridBinding.questLayout.setOnClickListener(v -> {
                questClickListener.onQuestionSelected(question.getQuestId());
            });
        }

        if (strSortType.equalsIgnoreCase(QuestionSortType.LIST.toString())) {
            holder.itemQuestionListBinding.tvQuestNo.setText(String.valueOf(question.getQuestNo()));
            holder.itemQuestionListBinding.tvQuestDesc.setText(question.getQuestionDesc());

            if (question.isVisited()) {
                holder.itemQuestionListBinding.tvQuestNo.setTextColor(context.getResources().getColor(R.color.colorWhite));
                holder.itemQuestionListBinding.tvQuestNo.setBackground(context.getResources().getDrawable(R.drawable.bg_quest_visited));
            }

            if (question.isMarked() && question.isVisited()) {
                holder.itemQuestionListBinding.tvQuestNo.setTextColor(context.getResources().getColor(R.color.colorWhite));
                holder.itemQuestionListBinding.tvQuestNo.setBackground(context.getResources().getDrawable(R.drawable.bg_quest_visited));
                holder.itemQuestionListBinding.marked.setVisibility(View.VISIBLE);
            }

            if (question.isMarked() && question.isAttempted()) {
                holder.itemQuestionListBinding.tvQuestNo.setTextColor(context.getResources().getColor(R.color.colorWhite));
                holder.itemQuestionListBinding.tvQuestNo.setBackground(context.getResources().getDrawable(R.drawable.bg_quest_attempted));
                holder.itemQuestionListBinding.marked.setVisibility(View.VISIBLE);
            }

            if (question.isAttempted()) {
                holder.itemQuestionListBinding.tvQuestNo.setTextColor(context.getResources().getColor(R.color.colorWhite));
                holder.itemQuestionListBinding.tvQuestNo.setBackground(context.getResources().getDrawable(R.drawable.bg_quest_attempted));
            }

            holder.itemQuestionListBinding.questLayout.setOnClickListener(v -> {
                questClickListener.onQuestionSelected(question.getQuestId());
            });
        }
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }


    public class QuestionPaletViewHolder extends RecyclerView.ViewHolder {
        QuestPaletItemBinding itemGridBinding;
        ItemQuestionBinding itemQuestionListBinding;

        public QuestionPaletViewHolder(@NonNull QuestPaletItemBinding itemView) {
            super(itemView.getRoot());
            this.itemGridBinding = itemView;
        }

        public QuestionPaletViewHolder(@NonNull ItemQuestionBinding itemView) {
            super(itemView.getRoot());
            this.itemQuestionListBinding = itemView;
        }
    }

    public void setPaletFilterResults(List<TestQuestion> result) {
        questions = result;
        notifyDataSetChanged();
    }

    public interface QuestClickListener {
        void onQuestionSelected(int questNo);
    }
}
