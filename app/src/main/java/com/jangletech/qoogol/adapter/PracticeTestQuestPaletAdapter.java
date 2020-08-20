package com.jangletech.qoogol.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.ItemQuestPracticeBinding;
import com.jangletech.qoogol.databinding.QuestPaletItemBinding;
import com.jangletech.qoogol.enums.QuestionSortType;
import com.jangletech.qoogol.model.TestQuestionNew;
import com.jangletech.qoogol.util.Constant;

import java.util.List;

public class PracticeTestQuestPaletAdapter extends RecyclerView.Adapter<PracticeTestQuestPaletAdapter.PracticeQuestionViewHolder> {

    private static final String TAG = "PracticeTestQuestPaletA";
    private Activity activity;
    private List<TestQuestionNew> questionList;
    private ItemQuestPracticeBinding itemBinding;
    private QuestPaletItemBinding itemGridQuestBinding;
    private QuestClickListener questClickListener;
    private String strSortType;
    private int lastPosition = -1;

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(activity, R.anim.fall_down);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

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

            if (practiceQuestion.getQ_quest().contains("\\")) {
                holder.itemBinding.tvQuest.setVisibility(View.GONE);
                holder.itemBinding.tvQuestMath.setVisibility(View.VISIBLE);
                holder.itemBinding.tvQuestMath.setText(practiceQuestion.getQ_quest());
            } else {
                holder.itemBinding.tvQuestMath.setVisibility(View.GONE);
                holder.itemBinding.tvQuest.setVisibility(View.VISIBLE);
                holder.itemBinding.tvQuest.setText(practiceQuestion.getQ_quest());
            }

            if (practiceQuestion.isTtqa_visited()) {
                holder.itemBinding.tvQuestNo.setBackground(activity.getResources().getDrawable(R.drawable.bg_quest_visited));
                holder.itemBinding.tvQuestNo.setTextColor(activity.getResources().getColor(R.color.colorWhite));
            } else {
                holder.itemBinding.tvQuestNo.setBackground(activity.getResources().getDrawable(R.drawable.bg_quest_default));
                holder.itemBinding.tvQuestNo.setTextColor(activity.getResources().getColor(R.color.colorBlack));
            }

           /* if (practiceQuestion.isTtqa_attempted()) {
                holder.itemBinding.tvQuestNo.setBackground(activity.getResources().getDrawable(R.drawable.bg_quest_attempted));
                holder.itemBinding.tvQuestNo.setTextColor(activity.getResources().getColor(R.color.colorWhite));
            }*/

            if (practiceQuestion.isTtqa_attempted()) {
                if (!practiceQuestion.getType().equalsIgnoreCase(Constant.SHORT_ANSWER) ||
                        !practiceQuestion.getType().equalsIgnoreCase(Constant.LONG_ANSWER) ||
                        !practiceQuestion.getType().equalsIgnoreCase(Constant.MATCH_PAIR) ||
                        !practiceQuestion.getType().equalsIgnoreCase(Constant.MATCH_PAIR_IMAGE)) {
                    if (practiceQuestion.getA_sub_ans().equalsIgnoreCase(practiceQuestion.getTtqa_sub_ans())) {
                        holder.itemBinding.tvQuestNo.setBackground(activity.getResources().getDrawable(R.drawable.bg_ans_right));
                        holder.itemBinding.tvQuestNo.setTextColor(activity.getResources().getColor(R.color.colorWhite));
                    } else {
                        holder.itemBinding.tvQuestNo.setBackground(activity.getResources().getDrawable(R.drawable.bg_ans_wrong));
                        holder.itemBinding.tvQuestNo.setTextColor(activity.getResources().getColor(R.color.colorWhite));
                    }
                }
            }
            if (practiceQuestion.isTtqa_marked()) {
                Log.d(TAG, "onBindViewHolder: " + practiceQuestion.isTtqa_marked());
                holder.itemBinding.marked.setVisibility(View.VISIBLE);
            } else {
                holder.itemBinding.marked.setVisibility(View.GONE);
            }

            holder.itemBinding.questLayout.setOnClickListener(v -> {
                questClickListener.onQuestionSelected(practiceQuestion, holder.getAdapterPosition());
            });

            setAnimation(holder.itemBinding.getRoot(), position);
        }

        if (strSortType.equalsIgnoreCase(QuestionSortType.GRID.toString())) {
            holder.itemBindingGrid.tvQuestNo.setText(String.valueOf(practiceQuestion.getTq_quest_seq_num()));

            if (practiceQuestion.isTtqa_visited()) {
                holder.itemBindingGrid.tvQuestNo.setBackground(activity.getResources().getDrawable(R.drawable.bg_quest_visited));
                holder.itemBindingGrid.tvQuestNo.setTextColor(activity.getResources().getColor(R.color.colorWhite));
            } else {
                holder.itemBindingGrid.tvQuestNo.setBackground(activity.getResources().getDrawable(R.drawable.bg_quest_default));
                holder.itemBindingGrid.tvQuestNo.setTextColor(activity.getResources().getColor(R.color.colorBlack));
            }

            if (practiceQuestion.isTtqa_attempted()) {
                if (!practiceQuestion.getType().equalsIgnoreCase(Constant.SHORT_ANSWER) ||
                        !practiceQuestion.getType().equalsIgnoreCase(Constant.LONG_ANSWER) ||
                        !practiceQuestion.getType().equalsIgnoreCase(Constant.MATCH_PAIR) ||
                        !practiceQuestion.getType().equalsIgnoreCase(Constant.MATCH_PAIR_IMAGE)) {
                    if (practiceQuestion.getA_sub_ans().equalsIgnoreCase(practiceQuestion.getTtqa_sub_ans())) {
                        holder.itemBindingGrid.tvQuestNo.setBackground(activity.getResources().getDrawable(R.drawable.bg_ans_right));
                        holder.itemBindingGrid.tvQuestNo.setTextColor(activity.getResources().getColor(R.color.colorWhite));
                    } else {
                        holder.itemBindingGrid.tvQuestNo.setBackground(activity.getResources().getDrawable(R.drawable.bg_ans_wrong));
                        holder.itemBindingGrid.tvQuestNo.setTextColor(activity.getResources().getColor(R.color.colorWhite));
                    }
                } else {
                    if (practiceQuestion.getType().equalsIgnoreCase(Constant.SHORT_ANSWER) ||
                            practiceQuestion.getType().equalsIgnoreCase(Constant.LONG_ANSWER)) {
                        //subjective question
                    } else {
                        //other than subjective type Question
                    }
                }
            } else {
                //Question Not Attempted
            }


            if (practiceQuestion.isTtqa_marked()) {
                holder.itemBindingGrid.marked.setVisibility(View.VISIBLE);
            } else {
                holder.itemBindingGrid.marked.setVisibility(View.GONE);
            }

            holder.itemBindingGrid.tvQuestNo.setOnClickListener(v -> {
                questClickListener.onQuestionSelected(practiceQuestion, holder.getAdapterPosition());
            });
            setAnimation(holder.itemBindingGrid.getRoot(), position);
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

    public void setSortedQuestList(List<TestQuestionNew> sortedQuest) {
        this.questionList = sortedQuest;
        notifyDataSetChanged();
    }

    public interface QuestClickListener {
        void onQuestionSelected(TestQuestionNew testQuestionNew, int pos);
    }
}
