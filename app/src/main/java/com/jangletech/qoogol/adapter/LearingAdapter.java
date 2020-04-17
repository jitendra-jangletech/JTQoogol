package com.jangletech.qoogol.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.jangletech.qoogol.MainActivity;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.activities.PracticeTestActivity;
import com.jangletech.qoogol.databinding.LearningItemBinding;
import com.jangletech.qoogol.model.LearningQuestions;
import com.jangletech.qoogol.ui.learning.SlideshowDialogFragment;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.jangletech.qoogol.util.Constant.learning;
import static com.jangletech.qoogol.util.Constant.test;

/**
 * Created by Pritali on 3/18/2020.
 */
public class LearingAdapter extends RecyclerView.Adapter<LearingAdapter.ViewHolder> {
    List<LearningQuestions> learningQuestionsList;
    Activity activity;
    LearningItemBinding learningItemBinding;
    onIconClick onIconClick;
    int call_from;
    MaterialCardView.LayoutParams params;


    public LearingAdapter(Activity activity, List<LearningQuestions> learningQuestionsList, onIconClick onIconClick, int call_from) {
        this.activity = activity;
        this.learningQuestionsList = learningQuestionsList;
        this.onIconClick = onIconClick;
        this.call_from = call_from;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        learningItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.learning_item, parent, false);
        if (call_from==learning) {
            params = new MaterialCardView.LayoutParams(MaterialCardView.LayoutParams.MATCH_PARENT, MaterialCardView.LayoutParams.WRAP_CONTENT);
            int margin = activity.getResources().getDimensionPixelSize(R.dimen._10sdp);
            params.setMargins(0,margin,0,margin);
            learningItemBinding.container.setLayoutParams(params);
        }
        return new ViewHolder(learningItemBinding);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LearningQuestions learningQuestions = learningQuestionsList.get(position);
        hideLayouts();

        if (learningQuestions.getQuestiondesc() == null || learningQuestions.getQuestiondesc() == "")
            learningItemBinding.questiondescTextview.setVisibility(View.GONE);

        learningItemBinding.favorite.setImageDrawable(learningQuestions.isIs_fav() ? activity.getResources().getDrawable(R.drawable.ic_favorite_black_24dp) : activity.getResources().getDrawable(R.drawable.ic_fav));
        learningItemBinding.like.setImageDrawable(learningQuestions.isIs_liked() ? activity.getResources().getDrawable(R.drawable.ic_thumb_up_black_24dp) : activity.getResources().getDrawable(R.drawable.ic_thumb_up_black_24dp));
        learningItemBinding.idTextview.setText(learningQuestions.getQuestion_id());
        learningItemBinding.timeTextview.setText("Time: " + learningQuestions.getRecommended_time() + " Sec");
        learningItemBinding.difflevelValue.setText(learningQuestions.getDifficulty_level());
        learningItemBinding.likeValue.setText(learningQuestions.getLikes());
        learningItemBinding.commentValue.setText(learningQuestions.getComments());
        learningItemBinding.shareValue.setText(learningQuestions.getShares());
        learningItemBinding.subjectTextview.setText(learningQuestions.getSubject());
        learningItemBinding.marksTextview.setText("Marks : " + learningQuestions.getMarks());

        learningItemBinding.chapterTextview.setText(learningQuestions.getChapter());
        learningItemBinding.topicTextview.setText(learningQuestions.getTopic());
        learningItemBinding.postedValue.setText(learningQuestions.getPosted_on());
        learningItemBinding.lastUsedValue.setText(learningQuestions.getLastused_on());
        learningItemBinding.questionTextview.setText(learningQuestions.getQuestion());
        learningItemBinding.questiondescTextview.setText(learningQuestions.getQuestiondesc());
        learningItemBinding.attemptedValue.setText(learningQuestions.getAttended_by() + " users");
        learningItemBinding.ratingvalue.setText(learningQuestions.getRating());

        learningItemBinding.solutionOption.setText("Answer : " + learningQuestions.getAnswer());
        learningItemBinding.solutionDesc.setText(learningQuestions.getAnswerDesc());


        if (learningQuestions.getCategory().equalsIgnoreCase("SCQ")) {
            learningItemBinding.categoryTextview.setText(learningQuestions.getCategory());
            learningItemBinding.singleChoice.setVisibility(View.VISIBLE);
            learningItemBinding.scq1.setText(learningQuestions.getMcq1());
            learningItemBinding.scq2.setText(learningQuestions.getMcq2());
            learningItemBinding.scq3.setText(learningQuestions.getMcq3());
            learningItemBinding.scq4.setText(learningQuestions.getMcq4());
        } else if (learningQuestions.getCategory().equalsIgnoreCase("MCQ")) {
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
            learningItemBinding.singleLineCounter.setVisibility(View.VISIBLE);
            learningItemBinding.categoryTextview.setText("Answer in Short");
            answerCharCounter(learningItemBinding.singleLine,learningItemBinding.singleLineCounter,200);
        } else if (learningQuestions.getCategory().equalsIgnoreCase("AIB")) {
            learningItemBinding.multiLine.setVisibility(View.VISIBLE);
            learningItemBinding.multiLineCounter.setVisibility(View.VISIBLE);
            answerCharCounter(learningItemBinding.multiLine,learningItemBinding.multiLineCounter,400);
            learningItemBinding.categoryTextview.setText("Answer in Brief");
        } else if (learningQuestions.getCategory().equalsIgnoreCase("MTP")) {
            learningItemBinding.matchThePairs.setVisibility(View.VISIBLE);
            learningItemBinding.categoryTextview.setText("Match the Pairs");
            learningItemBinding.a1text.setText(learningQuestions.getA1());
            learningItemBinding.a2text.setText(learningQuestions.getA2());
            learningItemBinding.a3text.setText(learningQuestions.getA3());
            learningItemBinding.a4text.setText(learningQuestions.getA4());
            learningItemBinding.b1text.setText(learningQuestions.getB1());
            learningItemBinding.b2text.setText(learningQuestions.getB2());
            learningItemBinding.b3text.setText(learningQuestions.getB3());
            learningItemBinding.b4text.setText(learningQuestions.getB4());
        }

        if (learningQuestions.getAttchment() != null && learningQuestions.getAttchment().size() != 0) {
            ArrayList<String> tempimgList = new ArrayList<>();
            tempimgList = learningQuestions.getAttchment();
            if (tempimgList.size() == 1) {
                try {
                    learningItemBinding.queImg1.setVisibility(View.VISIBLE);
                    Glide.with(activity).load(new URL(tempimgList.get(0))).into(learningItemBinding.queImg1);
                    ArrayList<String> finalTempimgList = tempimgList;
                    learningItemBinding.queImg1.setOnClickListener(v -> {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("images", (Serializable) finalTempimgList);
                        bundle.putInt("position", 0);
                        FragmentTransaction fragmentTransaction = null;
                        if(activity instanceof MainActivity){
                            fragmentTransaction =((MainActivity) activity).getSupportFragmentManager().beginTransaction();
                        }

                        if(activity instanceof PracticeTestActivity){
                            fragmentTransaction =((PracticeTestActivity) activity).getSupportFragmentManager().beginTransaction();
                        }

                        SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
                        newFragment.setArguments(bundle);
                        newFragment.show(fragmentTransaction, "slideshow");
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (tempimgList.size() > 1) {
                learningItemBinding.queImg1.setVisibility(View.GONE);
                learningItemBinding.imgRecycler.setVisibility(View.VISIBLE);
                ImageAdapter imageAdapter = new ImageAdapter(activity, tempimgList);
                learningItemBinding.imgRecycler.setHasFixedSize(true);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
                learningItemBinding.imgRecycler.setLayoutManager(linearLayoutManager);
                learningItemBinding.imgRecycler.setAdapter(imageAdapter);
            }
        }
    }


    private void answerCharCounter(EditText etAnswer, TextView tvCounter, int maxWordLength) {

        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                return null;
            }
        };
        etAnswer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int wordCount = 0;
                if (s.toString().contains(" ")) {
                    String[] words = s.toString().split(" ", -1);
                    wordCount = words.length;
                }

                if (wordCount == maxWordLength) {
                    etAnswer.setFilters(new InputFilter[]{new InputFilter.LengthFilter(s.length() - 1)});
                }

                tvCounter.setText((maxWordLength - wordCount + "/" + String.valueOf(maxWordLength)));
            }
        });
    }

    public void hideLayouts() {
        if (call_from==test) {
            learningItemBinding.expandableLayout.setVisibility(View.VISIBLE);
            learningItemBinding.expand.setVisibility(View.GONE);
            learningItemBinding.close.setVisibility(View.GONE);
        }
        learningItemBinding.queImg1.setVisibility(View.GONE);
        learningItemBinding.imgRecycler.setVisibility(View.GONE);
        learningItemBinding.matchThePairs.setVisibility(View.GONE);
        learningItemBinding.trueFalse.setVisibility(View.GONE);
        learningItemBinding.fillInTheBlanks.setVisibility(View.GONE);
        learningItemBinding.singleLine.setVisibility(View.GONE);
        learningItemBinding.singleLineCounter.setVisibility(View.GONE);
        learningItemBinding.multiChoice.setVisibility(View.GONE);
        learningItemBinding.multiLine.setVisibility(View.GONE);
        learningItemBinding.multiLineCounter.setVisibility(View.GONE);
        learningItemBinding.singleChoice.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return learningQuestionsList.size();
    }

    public interface onIconClick {
        void onCommentClick(String questionId);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        LearningItemBinding learningItemBinding;
        String scq_ans = "", mcq_ans = "", tfAns = "";
        HashMap<String, String> paired = new HashMap<String, String>();
        HashMap<String, String> MTP_ans = new HashMap<String, String>();
        boolean isB1Selected = false, isB2Selected = false, isB3Selected = false, isB4Selected = false;

        public ViewHolder(@NonNull LearningItemBinding itemView) {
            super(itemView.getRoot());
            this.learningItemBinding = itemView;
            MTP_ans.clear();
            MTP_ans.put("a1", "b3");
            MTP_ans.put("a2", "b4");
            MTP_ans.put("a3", "b1");
            MTP_ans.put("a4", "b2");

            learningItemBinding.questiondescTextview.setShowingLine(2);
            learningItemBinding.questiondescTextview.addShowLessText("Show Less");
            learningItemBinding.questiondescTextview.addShowMoreText("Show More");
            learningItemBinding.questiondescTextview.setShowMoreColor(Color.RED);
            learningItemBinding.questiondescTextview.setShowLessTextColor(Color.RED);


            //set touch listeners
            learningItemBinding.a1.setOnTouchListener(new ChoiceTouchListener());
            learningItemBinding.a2.setOnTouchListener(new ChoiceTouchListener());
            learningItemBinding.a3.setOnTouchListener(new ChoiceTouchListener());
            learningItemBinding.a4.setOnTouchListener(new ChoiceTouchListener());

            //set drag listeners
            learningItemBinding.b1.setOnDragListener(new ChoiceDragListener());
            learningItemBinding.b2.setOnDragListener(new ChoiceDragListener());
            learningItemBinding.b3.setOnDragListener(new ChoiceDragListener());
            learningItemBinding.b4.setOnDragListener(new ChoiceDragListener());
            learningItemBinding.b4.setOnDragListener(new ChoiceDragListener());
            learningItemBinding.b1text.setOnDragListener(new ChoiceDragListener());
            learningItemBinding.b2text.setOnDragListener(new ChoiceDragListener());
            learningItemBinding.b3text.setOnDragListener(new ChoiceDragListener());
            learningItemBinding.b4text.setOnDragListener(new ChoiceDragListener());


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

            learningItemBinding.reset.setOnClickListener(v -> reset());

            learningItemBinding.commentLayout.setOnClickListener(v -> onIconClick.onCommentClick(learningQuestionsList.get(getAdapterPosition()).getQuestion_id()));

            learningItemBinding.mcq1Layout.setOnClickListener(v -> {
                setMCQAnsIndicator();
                if (!mcq_ans.contains("a")) {
                    if (mcq_ans.equalsIgnoreCase(""))
                        mcq_ans = "a";
                    else
                        mcq_ans = mcq_ans + " a";
                    learningItemBinding.mcq1Layout.setBackground(activity.getResources().getDrawable(R.drawable.grey_border_grey_bg));
                } else {
                    mcq_ans = mcq_ans.replace("a", "");
                    learningItemBinding.mcq1Layout.setBackground(activity.getResources().getDrawable(R.drawable.grey_round_order));
                }
            });

            learningItemBinding.mcq2Layout.setOnClickListener(v -> {
                setMCQAnsIndicator();
                if (!mcq_ans.contains("b")) {
                    if (mcq_ans.equalsIgnoreCase(""))
                        mcq_ans = "b";
                    else
                        mcq_ans = mcq_ans + " b";
                    learningItemBinding.mcq2Layout.setBackground(activity.getResources().getDrawable(R.drawable.grey_border_grey_bg));
                } else {
                    mcq_ans = mcq_ans.replace("b", "");
                    learningItemBinding.mcq2Layout.setBackground(activity.getResources().getDrawable(R.drawable.grey_round_order));
                }
            });

            learningItemBinding.mcq3Layout.setOnClickListener(v -> {
                setMCQAnsIndicator();
                if (!mcq_ans.contains("c")) {
                    if (mcq_ans.equalsIgnoreCase(""))
                        mcq_ans = "c";
                    else
                        mcq_ans = mcq_ans + " c";
                    learningItemBinding.mcq3Layout.setBackground(activity.getResources().getDrawable(R.drawable.grey_border_grey_bg));
                } else {
                    mcq_ans = mcq_ans.replace("c", "");
                    learningItemBinding.mcq3Layout.setBackground(activity.getResources().getDrawable(R.drawable.grey_round_order));
                }
            });

            learningItemBinding.mcq4Layout.setOnClickListener(v -> {
                setMCQAnsIndicator();
                if (!mcq_ans.contains("d")) {
                    if (mcq_ans.equalsIgnoreCase(""))
                        mcq_ans = "d";
                    else
                        mcq_ans = mcq_ans + " d";
                    learningItemBinding.mcq4Layout.setBackground(activity.getResources().getDrawable(R.drawable.grey_border_grey_bg));
                } else {
                    mcq_ans = mcq_ans.replace("d", "");
                    learningItemBinding.mcq4Layout.setBackground(activity.getResources().getDrawable(R.drawable.grey_round_order));
                }
            });

            learningItemBinding.scq1Layout.setOnClickListener(v -> {
                setSCQAnsIndicator();
                setLayoutBg();
                scq_ans = "a";
                learningItemBinding.scq1Layout.setBackground(activity.getResources().getDrawable(R.drawable.grey_border_grey_bg));
            });

            learningItemBinding.scq2Layout.setOnClickListener(v -> {
                setLayoutBg();
                setSCQAnsIndicator();
                scq_ans = "b";
                learningItemBinding.scq2Layout.setBackground(activity.getResources().getDrawable(R.drawable.grey_border_grey_bg));
            });

            learningItemBinding.scq3Layout.setOnClickListener(v -> {
                setLayoutBg();
                setSCQAnsIndicator();
                scq_ans = "c";
                learningItemBinding.scq3Layout.setBackground(activity.getResources().getDrawable(R.drawable.grey_border_grey_bg));
            });

            learningItemBinding.scq4Layout.setOnClickListener(v -> {
                setLayoutBg();
                setSCQAnsIndicator();
                scq_ans = "d";
                learningItemBinding.scq4Layout.setBackground(activity.getResources().getDrawable(R.drawable.grey_border_grey_bg));
            });

            learningItemBinding.btntrue.setOnClickListener(v -> {
                setTFLayoutBg();
                tfAns = "true";
                learningItemBinding.btnfalse.setTextColor(activity.getResources().getColor(R.color.black));
                learningItemBinding.btntrue.setBackground(activity.getResources().getDrawable(R.drawable.grey_border_grey_bg));

            });

            learningItemBinding.btnfalse.setOnClickListener(v -> {
                setTFLayoutBg();
                tfAns = "false";
                learningItemBinding.btntrue.setTextColor(activity.getResources().getColor(R.color.black));
                learningItemBinding.btnfalse.setBackground(activity.getResources().getDrawable(R.drawable.grey_border_grey_bg));

            });

            learningItemBinding.submit.setOnClickListener(v -> {
                LearningQuestions learningQuestions = learningQuestionsList.get(getAdapterPosition());
                if (learningQuestions.getCategory().equalsIgnoreCase("SCQ")) {
                    if (!scq_ans.trim().equalsIgnoreCase("")) {
                        setSCQAnsIndicator();
                        if (scq_ans.equalsIgnoreCase(learningQuestions.getAnswer())) {
                            setRightSCQ(scq_ans);
                        } else {
                            setRightSCQ(learningQuestions.getAnswer());
                            setWrongSCQ(scq_ans);
                        }
                        learningItemBinding.solutionLayout.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(activity, "Please select atleast one option.", Toast.LENGTH_SHORT).show();
                    }
                } else if (learningQuestions.getCategory().equalsIgnoreCase("MCQ")) {
                    if (!mcq_ans.trim().equalsIgnoreCase("")) {
                        setMCQAnsIndicator();
                        String[] selected_mcq = mcq_ans.split("\\s+");
                        String[] right_mcq = learningQuestions.getAnswer().split(",");
                        for (int i = 0; i < selected_mcq.length; i++) {
                            if (learningQuestions.getAnswer().contains(selected_mcq[i])) {
                                setRightMCQ(selected_mcq[i]);
                            } else {
                                setWrongMCQ(selected_mcq[i]);
                            }
                        }
                        for (int i = 0; i < right_mcq.length; i++) {
                            setRightMCQ(right_mcq[i]);
                        }
                        learningItemBinding.solutionLayout.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(activity, "Please select atleast one option.", Toast.LENGTH_SHORT).show();
                    }

                } else if (learningQuestions.getCategory().equalsIgnoreCase("TF")) {
                    if (!tfAns.equalsIgnoreCase("")) {
                        if (tfAns.equalsIgnoreCase(learningQuestions.getAnswer())) {
                            setRightTF("true");
                            learningItemBinding.btntrue.setTextColor(activity.getResources().getColor(R.color.white));
                        } else {
                            setWrongTF(tfAns);
                            learningItemBinding.btnfalse.setTextColor(activity.getResources().getColor(R.color.white));
                        }
                        learningItemBinding.solutionLayout.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(activity, "Please select atleast one option.", Toast.LENGTH_SHORT).show();
                    }
                } else if (learningQuestions.getCategory().equalsIgnoreCase("FIB")) {
                    learningItemBinding.fillInTheBlanks.setBackground(activity.getResources().getDrawable(R.drawable.grey_border));
                    if (!learningItemBinding.fillInTheBlanks.getText().toString().trim().equalsIgnoreCase("")) {
                        if (learningItemBinding.fillInTheBlanks.getText().toString().trim().equalsIgnoreCase(learningQuestions.getAnswer().toString().trim())) {
                            learningItemBinding.fibImg.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right));
                            learningItemBinding.fillInTheBlanks.setBackground(activity.getResources().getDrawable(R.drawable.green_border));
                        } else {
                            learningItemBinding.fillInTheBlanks.setBackground(activity.getResources().getDrawable(R.drawable.red_border));
                            learningItemBinding.fibImg.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong));
                        }
                        learningItemBinding.solutionLayout.setVisibility(View.VISIBLE);
                        learningItemBinding.fibImg.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(activity, "Please enter answer first.", Toast.LENGTH_SHORT).show();
                    }
                } else if (learningQuestions.getCategory().equalsIgnoreCase("MTP")) {

                    if (!isB1Selected || !isB2Selected || !isB3Selected || !isB4Selected) {
                        Toast.makeText(activity, "Select all pairs first.", Toast.LENGTH_SHORT).show();
                    } else {
                        boolean isFound = false;
                        for (Map.Entry<String, String> entry : paired.entrySet()) {
                            Iterator ansIterator = MTP_ans.entrySet().iterator();
                            String value = entry.getValue();
                            for (Map.Entry<String, String> ansentry : MTP_ans.entrySet()) {
                                if (entry.equals(ansentry)) {
                                    isFound = true;
                                    break;
                                }
                            }
                            if (isFound) {
                                isFound = false;
                                setRightPair(value);
                            } else {
                                setWrongPair(value);
                            }
                        }
                    }
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

        public void setTFLayoutBg() {
            learningItemBinding.btntrue.setBackground(activity.getResources().getDrawable(R.drawable.grey_round_order));
            learningItemBinding.btnfalse.setBackground(activity.getResources().getDrawable(R.drawable.grey_round_order));
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

        public void setRightMCQ(String option) {
            switch (option) {
                case "a":
                    learningItemBinding.mcq1Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right));
                    learningItemBinding.mcq1Img.setVisibility(View.VISIBLE);
                    break;
                case "b":
                    learningItemBinding.mcq2Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right));
                    learningItemBinding.mcq2Img.setVisibility(View.VISIBLE);
                    break;
                case "c":
                    learningItemBinding.mcq3Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right));
                    learningItemBinding.mcq3Img.setVisibility(View.VISIBLE);
                    break;
                case "d":
                    learningItemBinding.mcq4Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right));
                    learningItemBinding.mcq4Img.setVisibility(View.VISIBLE);
                    break;
            }
        }

        public void setWrongMCQ(String option) {
            switch (option) {
                case "a":
                    learningItemBinding.mcq1Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong));
                    learningItemBinding.mcq1Img.setVisibility(View.VISIBLE);
                    break;
                case "b":
                    learningItemBinding.mcq2Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong));
                    learningItemBinding.mcq2Img.setVisibility(View.VISIBLE);
                    break;
                case "c":
                    learningItemBinding.mcq3Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong));
                    learningItemBinding.mcq3Img.setVisibility(View.VISIBLE);
                    break;
                case "d":
                    learningItemBinding.mcq4Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong));
                    learningItemBinding.mcq4Img.setVisibility(View.VISIBLE);
                    break;
            }
        }

        public void setRightTF(String option) {
            switch (option) {
                case "true":
                    learningItemBinding.btntrue.setBackground(activity.getResources().getDrawable(R.drawable.bg_green_round));
                    break;
                case "false":
                    learningItemBinding.btnfalse.setBackground(activity.getResources().getDrawable(R.drawable.bg_green_round));
                    break;
            }
        }

        public void setWrongTF(String option) {
            switch (option) {
                case "true":
                    learningItemBinding.btntrue.setBackground(activity.getResources().getDrawable(R.drawable.bg_red_round));
                    break;
                case "false":
                    learningItemBinding.btnfalse.setBackground(activity.getResources().getDrawable(R.drawable.bg_red_round));
                    break;
            }
        }

        private final class ChoiceTouchListener implements View.OnTouchListener {
            @SuppressLint("NewApi")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    /*
                     * Drag details: we only need default behavior
                     * - clip data could be set to pass data as part of drag
                     * - shadow can be tailored
                     */
                    ClipData data = ClipData.newPlainText("", "");
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                    //start dragging the item touched
                    view.startDrag(data, shadowBuilder, view, 0);
                    return true;
                } else {
                    return false;
                }
            }
        }

        private void setPairs() {

        }

        @SuppressLint("NewApi")
        private class ChoiceDragListener implements View.OnDragListener {

            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        break;
                    case DragEvent.ACTION_DRAG_ENTERED:
                        break;
                    case DragEvent.ACTION_DRAG_EXITED:
                        break;
                    case DragEvent.ACTION_DROP:
                        View view = (View) event.getLocalState();
                        LinearLayout dropped = (LinearLayout) view;

                        //make it bold to highlight the fact that an item has been dropped
                        switch (v.getId()) {
                            case R.id.b1:
                            case R.id.b1text:
                                checkAvailability(getNameFromId(dropped.getId()), "b1");
                                paired.put(getNameFromId(dropped.getId()), "b1");
                                isB1Selected = true;
                                dropped.setBackgroundColor(activity.getResources().getColor(R.color.hotpink));
                                learningItemBinding.b1.setBackgroundColor(activity.getResources().getColor(R.color.hotpink));
                                if (paired.size()==MTP_ans.size()-1) {
                                    setLastPair();
                                }
                                break;
                            case R.id.b2:
                            case R.id.b2text:
                                checkAvailability(getNameFromId(dropped.getId()), "b2");
                                paired.put(getNameFromId(dropped.getId()), "b2");
                                isB2Selected = true;
                                dropped.setBackgroundColor(activity.getResources().getColor(R.color.slateblue));
                                learningItemBinding.b2.setBackgroundColor(activity.getResources().getColor(R.color.slateblue));
                                if (paired.size()==MTP_ans.size()-1) {
                                    setLastPair();
                                }
                                break;
                            case R.id.b3:
                            case R.id.b3text:
                                checkAvailability(getNameFromId(dropped.getId()), "b3");
                                paired.put(getNameFromId(dropped.getId()), "b3");
                                isB3Selected = true;
                                dropped.setBackgroundColor(activity.getResources().getColor(R.color.steelblue));
                                learningItemBinding.b3.setBackgroundColor(activity.getResources().getColor(R.color.steelblue));
                                if (paired.size()==MTP_ans.size()-1) {
                                    setLastPair();
                                }
                                break;
                            case R.id.b4:
                            case R.id.b4text:
                                checkAvailability(getNameFromId(dropped.getId()), "b4");
                                paired.put(getNameFromId(dropped.getId()), "b4");
                                isB4Selected = true;
                                dropped.setBackgroundColor(activity.getResources().getColor(R.color.cadetblue));
                                learningItemBinding.b4.setBackgroundColor(activity.getResources().getColor(R.color.cadetblue));
                                if (paired.size()==MTP_ans.size()-1) {
                                    setLastPair();
                                }
                                break;
                            default:
                                break;
                        }

                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        break;
                    default:
                        break;
                }
                return true;
            }
        }

        private void setLastPair() {
            String a_group = "", b_group="";
            if (!paired.containsKey("a1")) {
                a_group="a1";
            } else  if (!paired.containsKey("a2")) {
                a_group="a2";
            } else  if (!paired.containsKey("a3")) {
                a_group="a3";
            } else  if (!paired.containsKey("a4")) {
                a_group="a4";
            }

            if (!paired.containsValue("b1")) {
                b_group="b1";
            } else  if (!paired.containsValue("b2")) {
                b_group="b2";
            } else  if (!paired.containsValue("b3")) {
                b_group="b3";
            } else  if (!paired.containsValue("b4")) {
                b_group="b4";
            }

            if (!a_group.equalsIgnoreCase("") && !b_group.equalsIgnoreCase("")) {
                paired.put(a_group,b_group);
                setmatchedPair(a_group,activity.getResources().getColor(R.color.yellow));
                setmatchedPair(b_group,activity.getResources().getColor(R.color.yellow));
            }
        }

        public void setmatchedPair(String option, int color) {
            switch (option) {
                case "b1":
                    learningItemBinding.b1.setBackgroundColor(color);
                    break;
                case "b2":
                    learningItemBinding.b2.setBackgroundColor(color);
                    break;
                case "b3":
                    learningItemBinding.b3.setBackgroundColor(color);
                    break;
                case "b4":
                    learningItemBinding.b4.setBackgroundColor(color);
                    break;
                case "a1":
                    learningItemBinding.a1.setBackgroundColor(color);
                    break;
                case "a2":
                    learningItemBinding.a2.setBackgroundColor(color);
                    break;
                case "a3":
                    learningItemBinding.a3.setBackgroundColor(color);
                    break;
                case "a4":
                    learningItemBinding.a4.setBackgroundColor(color);
                    break;
                default:
                    break;
            }
        }

        private void setRightPair(String option) {
            switch (option) {
                case "b1":
                    learningItemBinding.img1.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right_mtp));
                    learningItemBinding.img1.setVisibility(View.VISIBLE);
                    break;
                case "b2":
                    learningItemBinding.img2.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right_mtp));
                    learningItemBinding.img2.setVisibility(View.VISIBLE);
                    break;
                case "b3":
                    learningItemBinding.img3.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right_mtp));
                    learningItemBinding.img3.setVisibility(View.VISIBLE);
                    break;
                case "b4":
                    learningItemBinding.img4.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right_mtp));
                    learningItemBinding.img4.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }


        private void setWrongPair(String option) {
            switch (option) {
                case "b1":
                    learningItemBinding.img1.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                    learningItemBinding.img1.setVisibility(View.VISIBLE);
                    break;
                case "b2":
                    learningItemBinding.img2.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                    learningItemBinding.img2.setVisibility(View.VISIBLE);
                    break;
                case "b3":
                    learningItemBinding.img3.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                    learningItemBinding.img3.setVisibility(View.VISIBLE);
                    break;
                case "b4":
                    learningItemBinding.img4.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                    learningItemBinding.img4.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }


        private void setListeners() {

        }

        private void checkAvailability(String option_1, String option_b) {
            Iterator myVeryOwnIterator = paired.keySet().iterator();
            while (myVeryOwnIterator.hasNext()) {
                String key = (String) myVeryOwnIterator.next();
                String value = (String) paired.get(key);
                if (key.equalsIgnoreCase(option_1)) {
                    setLayoutBg(option_1);
                    setLayoutBg(value);
                    paired.remove(key);
                    break;
                }
            }

            Iterator myVeryOwnIterator1 = paired.keySet().iterator();
            while (myVeryOwnIterator1.hasNext()) {
                String key = (String) myVeryOwnIterator1.next();
                String value = (String) paired.get(key);
                if (value.equalsIgnoreCase(option_b)) {
                    setLayoutBg(option_b);
                    setLayoutBg(key);
                    paired.remove(key);
                    break;
                }
            }
        }

        private void setLayoutBg(String option) {
            switch (option) {
                case "a1":
                    learningItemBinding.a1.setBackground(activity.getResources().getDrawable(R.drawable.grey_round_border));
                    break;
                case "a2":
                    learningItemBinding.a2.setBackground(activity.getResources().getDrawable(R.drawable.grey_round_border));
                    break;
                case "a3":
                    learningItemBinding.a3.setBackground(activity.getResources().getDrawable(R.drawable.grey_round_border));
                    break;
                case "a4":
                    learningItemBinding.a4.setBackground(activity.getResources().getDrawable(R.drawable.grey_round_border));
                    break;
                case "b1":
                    isB1Selected = false;
                    learningItemBinding.b1.setBackground(activity.getResources().getDrawable(R.drawable.grey_round_border));
                    break;
                case "b2":
                    isB2Selected = false;
                    learningItemBinding.b2.setBackground(activity.getResources().getDrawable(R.drawable.grey_round_border));
                    break;
                case "b3":
                    isB3Selected = false;
                    learningItemBinding.b3.setBackground(activity.getResources().getDrawable(R.drawable.grey_round_border));
                    break;
                case "b4":
                    isB4Selected = false;
                    learningItemBinding.b4.setBackground(activity.getResources().getDrawable(R.drawable.grey_round_border));
                    break;
                default:
                    break;

            }
        }

        private String getNameFromId(int id) {
            switch (id) {
                case R.id.a1:
                    return "a1";
                case R.id.a2:
                    return "a2";
                case R.id.a3:
                    return "a3";
                case R.id.a4:
                    return "a4";
                case R.id.b1:
                    return "b1";
                case R.id.b2:
                    return "b2";
                case R.id.b3:
                    return "b3";
                case R.id.b4:
                    return "b4";
                default:
                    return "";
            }
        }

        public void reset() {
            paired.clear();
            learningItemBinding.b1.setTag(null);
            learningItemBinding.b2.setTag(null);
            learningItemBinding.b3.setTag(null);

            setListeners();
            learningItemBinding.img1.setVisibility(View.GONE);
            learningItemBinding.img2.setVisibility(View.GONE);
            learningItemBinding.img3.setVisibility(View.GONE);
            learningItemBinding.img4.setVisibility(View.GONE);

            learningItemBinding.a1.setBackground(activity.getResources().getDrawable(R.drawable.grey_round_border));
            learningItemBinding.a2.setBackground(activity.getResources().getDrawable(R.drawable.grey_round_border));
            learningItemBinding.a3.setBackground(activity.getResources().getDrawable(R.drawable.grey_round_border));
            learningItemBinding.a4.setBackground(activity.getResources().getDrawable(R.drawable.grey_round_border));
            learningItemBinding.b1.setBackground(activity.getResources().getDrawable(R.drawable.grey_round_border));
            learningItemBinding.b2.setBackground(activity.getResources().getDrawable(R.drawable.grey_round_border));
            learningItemBinding.b3.setBackground(activity.getResources().getDrawable(R.drawable.grey_round_border));
            learningItemBinding.b4.setBackground(activity.getResources().getDrawable(R.drawable.grey_round_border));
        }
    }
}
