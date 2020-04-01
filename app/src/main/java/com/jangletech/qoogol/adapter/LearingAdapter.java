package com.jangletech.qoogol.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.LearningItemBinding;
import com.jangletech.qoogol.model.LearningQuestions;

import java.util.List;

/**
 * Created by Pritali on 3/18/2020.
 */
public class LearingAdapter extends RecyclerView.Adapter<LearingAdapter.ViewHolder> {
    List<LearningQuestions> learningQuestionsList;
    Activity activity;
    LearningItemBinding learningItemBinding;
    String scq_ans="", mcq_ans="";

    public LearingAdapter(Activity activity, List<LearningQuestions> learningQuestionsList) {
        this.activity = activity;
        this.learningQuestionsList = learningQuestionsList;
    }

    @NonNull
    @Override
    public LearingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        learningItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.learning_item, parent, false);

        return new LearingAdapter.ViewHolder(learningItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull LearingAdapter.ViewHolder holder, int position) {
        LearningQuestions learningQuestions = learningQuestionsList.get(position);

        if (learningQuestions.getAttchment()!=null && learningQuestions.getAttchment()!="")
            learningItemBinding.attachment.setVisibility(View.VISIBLE);

        if (learningQuestions.getQuestiondesc()==null || learningQuestions.getQuestiondesc()=="")
            learningItemBinding.questiondescTextview.setVisibility(View.GONE);

        learningItemBinding.favorite.setImageDrawable(learningQuestions.isIs_fav()?activity.getResources().getDrawable(R.drawable.ic_favorite_black_24dp):activity.getResources().getDrawable(R.drawable.ic_fav));
        learningItemBinding.like.setImageDrawable(learningQuestions.isIs_liked()?activity.getResources().getDrawable(R.drawable.ic_thumb_up_black_24dp):activity.getResources().getDrawable(R.drawable.ic_thumb_up_black_24dp));
        learningItemBinding.idTextview.setText(learningQuestions.getQuestion_id());
        learningItemBinding.timeTextview.setText("Time: "+learningQuestions.getRecommended_time() + " Sec");
        learningItemBinding.difflevelValue.setText(learningQuestions.getDifficulty_level());
        learningItemBinding.likeValue.setText(learningQuestions.getLikes());
        learningItemBinding.commentValue.setText(learningQuestions.getComments());
        learningItemBinding.shareValue.setText(learningQuestions.getShares());
        learningItemBinding.subjectTextview.setText(learningQuestions.getSubject());
        learningItemBinding.marksTextview.setText("Marks : "+learningQuestions.getMarks());

        learningItemBinding.chapterTextview.setText(learningQuestions.getChapter());
        learningItemBinding.topicTextview.setText(learningQuestions.getTopic());
        learningItemBinding.postedValue.setText(learningQuestions.getPosted_on());
        learningItemBinding.lastUsedValue.setText(learningQuestions.getLastused_on());
        learningItemBinding.questionTextview.setText(learningQuestions.getQuestion());
        learningItemBinding.questiondescTextview.setText(learningQuestions.getQuestiondesc());
        learningItemBinding.attemptedValue.setText(learningQuestions.getAttended_by() + " users");
        learningItemBinding.ratingvalue.setText(learningQuestions.getRating());

        learningItemBinding.solutionOption.setText("Answer : "+learningQuestions.getAnswer());
        learningItemBinding.solutionDesc.setText(learningQuestions.getAnswerDesc());

        if (learningQuestions.getCategory().equalsIgnoreCase("SCQ")) {
            learningItemBinding.categoryTextview.setText(learningQuestions.getCategory());
            learningItemBinding.singleChoice.setVisibility(View.VISIBLE);
            learningItemBinding.scq1.setText(learningQuestions.getMcq1());
            learningItemBinding.scq2.setText(learningQuestions.getMcq2());
            learningItemBinding.scq3.setText(learningQuestions.getMcq3());
            learningItemBinding.scq4.setText(learningQuestions.getMcq4());
        } else  if (learningQuestions.getCategory().equalsIgnoreCase("MCQ")) {
            learningItemBinding.categoryTextview.setText(learningQuestions.getCategory());
            learningItemBinding.multiChoice.setVisibility(View.VISIBLE);
            learningItemBinding.mcq1.setText(learningQuestions.getMcq1());
            learningItemBinding.mcq2.setText(learningQuestions.getMcq2());
            learningItemBinding.mcq3.setText(learningQuestions.getMcq3());
            learningItemBinding.mcq4.setText(learningQuestions.getMcq4());
        } else if (learningQuestions.getCategory().equalsIgnoreCase("TF")) {
            learningItemBinding.categoryTextview.setText("True False");
            learningItemBinding.trueFalse.setVisibility(View.VISIBLE);
        } else if (learningQuestions.getCategory().equalsIgnoreCase("FIB")) {
            learningItemBinding.fillInTheBlanks.setVisibility(View.VISIBLE);
            learningItemBinding.categoryTextview.setText("Fill in the Blanks");
        } else if (learningQuestions.getCategory().equalsIgnoreCase("AIS")) {
            learningItemBinding.singleLine.setVisibility(View.VISIBLE);
            learningItemBinding.categoryTextview.setText("Answer in Short");
        } else if (learningQuestions.getCategory().equalsIgnoreCase("AIB")) {
            learningItemBinding.multiLine.setVisibility(View.VISIBLE);
            learningItemBinding.categoryTextview.setText("Answer in Brief");
        }
    }

    @Override
    public int getItemCount() {
        return learningQuestionsList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {
        LearningItemBinding learningItemBinding;

        public ViewHolder(@NonNull LearningItemBinding itemView) {
            super(itemView.getRoot());
            this.learningItemBinding = itemView;
            learningItemBinding.questiondescTextview.setShowingLine(2);
            learningItemBinding.questiondescTextview.addShowLessText("Show Less");
            learningItemBinding.questiondescTextview.addShowMoreText("Show More");
            learningItemBinding.questiondescTextview.setShowMoreColor(Color.RED);
            learningItemBinding.questiondescTextview.setShowLessTextColor(Color.RED);

            learningItemBinding.expand.setOnClickListener(v -> {
                learningItemBinding.expandableLayout.setVisibility(View.VISIBLE);
                learningItemBinding.close.setVisibility(View.VISIBLE);
                learningItemBinding.expand.setVisibility(View.GONE);
            });

            learningItemBinding.close.setOnClickListener(v -> {
                learningItemBinding.expandableLayout.setVisibility(View.GONE);
                learningItemBinding.expand.setVisibility(View.VISIBLE);
                learningItemBinding.close.setVisibility(View.GONE);
            });

            learningItemBinding.mcq1Layout.setOnClickListener(v -> {
                setMCQAnsIndicator();
                setLayoutBg();
                if (!mcq_ans.contains("a")) {
                    if (mcq_ans.equalsIgnoreCase(""))
                        mcq_ans= "a";
                    else
                        mcq_ans= mcq_ans + " a";
                    learningItemBinding.mcq1Layout.setBackground(activity.getResources().getDrawable(R.drawable.grey_border_grey_bg));
                }
                else {
                    mcq_ans.replace("a","");
                    learningItemBinding.mcq1Layout.setBackground(activity.getResources().getDrawable(R.drawable.grey_round_order));
                }
            });

            learningItemBinding.mcq2Layout.setOnClickListener(v -> {
                setMCQAnsIndicator();
                setLayoutBg();
                if (!mcq_ans.contains("b")) {
                    if (mcq_ans.equalsIgnoreCase(""))
                        mcq_ans= "b";
                    else
                        mcq_ans= mcq_ans + " b";
                    learningItemBinding.mcq2Layout.setBackground(activity.getResources().getDrawable(R.drawable.grey_border_grey_bg));
                }
                else {
                    mcq_ans.replace("b","");
                    learningItemBinding.mcq2Layout.setBackground(activity.getResources().getDrawable(R.drawable.grey_round_order));
                }
            });

            learningItemBinding.mcq3Layout.setOnClickListener(v -> {
                setMCQAnsIndicator();
                setLayoutBg();
                if (!mcq_ans.contains("c")) {
                    if (mcq_ans.equalsIgnoreCase(""))
                        mcq_ans= "c";
                    else
                        mcq_ans= mcq_ans + " c";
                    learningItemBinding.mcq3Layout.setBackground(activity.getResources().getDrawable(R.drawable.grey_border_grey_bg));
                }
                else {
                    mcq_ans.replace("c","");
                    learningItemBinding.mcq3Layout.setBackground(activity.getResources().getDrawable(R.drawable.grey_round_order));
                }
            });

            learningItemBinding.mcq4Layout.setOnClickListener(v -> {
                setMCQAnsIndicator();
                setLayoutBg();
                if (!mcq_ans.contains("d")) {
                    if (mcq_ans.equalsIgnoreCase(""))
                        mcq_ans= "d";
                    else
                        mcq_ans= mcq_ans + " d";
                    learningItemBinding.mcq4Layout.setBackground(activity.getResources().getDrawable(R.drawable.grey_border_grey_bg));
                }
                else {
                    mcq_ans.replace("d","");
                    learningItemBinding.mcq4Layout.setBackground(activity.getResources().getDrawable(R.drawable.grey_round_order));
                }
            });

            learningItemBinding.scq1Layout.setOnClickListener(v -> {
                setSCQAnsIndicator();
                setLayoutBg();
                scq_ans="a";
                learningItemBinding.scq1Layout.setBackground(activity.getResources().getDrawable(R.drawable.grey_border_grey_bg));
            });

            learningItemBinding.scq2Layout.setOnClickListener(v -> {
                setLayoutBg();
                setSCQAnsIndicator();
                scq_ans="b";
                learningItemBinding.scq2Layout.setBackground(activity.getResources().getDrawable(R.drawable.grey_border_grey_bg));
            });

            learningItemBinding.scq3Layout.setOnClickListener(v -> {
                setLayoutBg();
                setSCQAnsIndicator();
                scq_ans="c";
                learningItemBinding.scq3Layout.setBackground(activity.getResources().getDrawable(R.drawable.grey_border_grey_bg));
            });

            learningItemBinding.scq4Layout.setOnClickListener(v -> {
                setLayoutBg();
                setSCQAnsIndicator();
                scq_ans="d";
                learningItemBinding.scq4Layout.setBackground(activity.getResources().getDrawable(R.drawable.grey_border_grey_bg));
            });

            learningItemBinding.submit.setOnClickListener(v -> {
                LearningQuestions learningQuestions = learningQuestionsList.get(getAdapterPosition());
                if (learningQuestions.getCategory().equalsIgnoreCase("SCQ")) {
                   setSCQAnsIndicator();
                    if(scq_ans.equalsIgnoreCase(learningQuestions.getAnswer())) {
                        setRightSCQ(scq_ans);
                    } else {
                        setRightSCQ(learningQuestions.getAnswer());
                        setWrongSCQ(scq_ans);
                    }
                    learningItemBinding.solutionLayout.setVisibility(View.VISIBLE);
                }
            });
        }

        private void setSCQAnsIndicator() {
            learningItemBinding.scq1Img.setVisibility(View.GONE);
            learningItemBinding.scq2Img.setVisibility(View.GONE);
            learningItemBinding.scq3Img.setVisibility(View.GONE);
            learningItemBinding.scq4Img.setVisibility(View.GONE);
        }

        private void setMCQAnsIndicator() {
            learningItemBinding.mcq1Img.setVisibility(View.GONE);
            learningItemBinding.mcq2Img.setVisibility(View.GONE);
            learningItemBinding.mcq3Img.setVisibility(View.GONE);
            learningItemBinding.mcq4Img.setVisibility(View.GONE);
        }

        public void setLayoutBg() {
            learningItemBinding.scq1Layout.setBackground(activity.getResources().getDrawable(R.drawable.grey_round_order));
            learningItemBinding.scq2Layout.setBackground(activity.getResources().getDrawable(R.drawable.grey_round_order));
            learningItemBinding.scq3Layout.setBackground(activity.getResources().getDrawable(R.drawable.grey_round_order));
            learningItemBinding.scq4Layout.setBackground(activity.getResources().getDrawable(R.drawable.grey_round_order));
        }

        public void setRightSCQ(String option) {
            switch (option) {
                case "a":
                    learningItemBinding.scq1Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right));
                    learningItemBinding.scq1Img.setVisibility(View.VISIBLE);
                    break;
                case "b":
                    learningItemBinding.scq2Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right));
                    learningItemBinding.scq2Img.setVisibility(View.VISIBLE);
                    break;
                case "c":
                    learningItemBinding.scq3Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right));
                    learningItemBinding.scq3Img.setVisibility(View.VISIBLE);
                    break;
                case "d":
                    learningItemBinding.scq4Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right));
                    learningItemBinding.scq4Img.setVisibility(View.VISIBLE);
                    break;
            }
        }

        public void setWrongSCQ(String option) {
            switch (option) {
                case "a":
                    learningItemBinding.scq1Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong));
                    learningItemBinding.scq1Img.setVisibility(View.VISIBLE);
                    break;
                case "b":
                    learningItemBinding.scq2Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong));
                    learningItemBinding.scq2Img.setVisibility(View.VISIBLE);
                    break;
                case "c":
                    learningItemBinding.scq3Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong));
                    learningItemBinding.scq3Img.setVisibility(View.VISIBLE);
                    break;
                case "d":
                    learningItemBinding.scq4Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong));
                    learningItemBinding.scq4Img.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }
}
