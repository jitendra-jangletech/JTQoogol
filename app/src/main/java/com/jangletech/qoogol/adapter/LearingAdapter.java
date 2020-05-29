package com.jangletech.qoogol.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import android.widget.ImageView;
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
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.activities.MainActivity;
import com.jangletech.qoogol.activities.PracticeTestActivity;
import com.jangletech.qoogol.database.QoogolDatabase;
import com.jangletech.qoogol.databinding.LearningItemBinding;
import com.jangletech.qoogol.databinding.RatingFeedbackBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.model.LearningQuestionsNew;
import com.jangletech.qoogol.model.ProcessQuestion;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.ui.learning.SlideshowDialogFragment;
import com.jangletech.qoogol.ui.test.my_test.MyTestFragment;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;
import com.jangletech.qoogol.util.UtilHelper;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.jangletech.qoogol.util.Constant.FILL_THE_BLANKS;
import static com.jangletech.qoogol.util.Constant.IMAGE;
import static com.jangletech.qoogol.util.Constant.LONG_ANSWER;
import static com.jangletech.qoogol.util.Constant.MATCH_PAIR;
import static com.jangletech.qoogol.util.Constant.MATCH_PAIR_IMAGE;
import static com.jangletech.qoogol.util.Constant.MCQ;
import static com.jangletech.qoogol.util.Constant.MCQ_IMAGE;
import static com.jangletech.qoogol.util.Constant.MCQ_IMAGE_WITH_TEXT;
import static com.jangletech.qoogol.util.Constant.ONE_LINE_ANSWER;
import static com.jangletech.qoogol.util.Constant.SCQ;
import static com.jangletech.qoogol.util.Constant.SCQ_IMAGE;
import static com.jangletech.qoogol.util.Constant.SCQ_IMAGE_WITH_TEXT;
import static com.jangletech.qoogol.util.Constant.SHORT_ANSWER;
import static com.jangletech.qoogol.util.Constant.TRUE_FALSE;
import static com.jangletech.qoogol.util.Constant.learning;
import static com.jangletech.qoogol.util.Constant.test;

/**
 * Created by Pritali on 3/18/2020.
 */
public class LearingAdapter extends RecyclerView.Adapter<LearingAdapter.ViewHolder> {
    List<LearningQuestionsNew> learningQuestionsList;
    Activity activity;
    LearningItemBinding learningItemBinding;
    onIconClick onIconClick;
    int call_from;
    MaterialCardView.LayoutParams params;

    public LearingAdapter(Activity activity, List<LearningQuestionsNew> learningQuestionsList, onIconClick onIconClick, int call_from) {
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
        if (call_from == learning) {
            params = new MaterialCardView.LayoutParams(MaterialCardView.LayoutParams.MATCH_PARENT, MaterialCardView.LayoutParams.WRAP_CONTENT);
            int margin = activity.getResources().getDimensionPixelSize(R.dimen._10sdp);
            params.setMargins(0, margin, 0, margin);
            learningItemBinding.container.setLayoutParams(params);
        }
        return new ViewHolder(learningItemBinding);
    }


    public void updateList(List<LearningQuestionsNew> learningQuestionsList) {
        this.learningQuestionsList = learningQuestionsList;
        notifyDataSetChanged();
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
        try {
            LearningQuestionsNew learningQuestions = learningQuestionsList.get(position);
            learningItemBinding.setQuestion(learningQuestions);

            hideLayouts();


            if (learningQuestions.getQuestion()!=null && learningQuestions.getQuestion().contains("$")) {
                learningItemBinding.questionMathview.setVisibility(View.VISIBLE);
            } else {
                learningItemBinding.questionTextview.setVisibility(View.VISIBLE);
            }


            learningItemBinding.favorite.setImageDrawable(learningQuestions.getIs_fav().equalsIgnoreCase("true") ? activity.getResources().getDrawable(R.drawable.ic_favorite_black_24dp) : activity.getResources().getDrawable(R.drawable.ic_fav));
            learningItemBinding.like.setImageDrawable(learningQuestions.getIs_liked().equalsIgnoreCase("true") ? activity.getResources().getDrawable(R.drawable.ic_thumb_up_black_24dp) : activity.getResources().getDrawable(R.drawable.ic_like));

            if (learningQuestions.getQue_media_typs() != null && learningQuestions.getQue_media_typs().equalsIgnoreCase(IMAGE) && learningQuestions.getQue_images() != null) {
                String[] stringrray = learningQuestions.getQue_images().split(",");
                List<String> tempimgList = new ArrayList<>();
                tempimgList = Arrays.asList(stringrray);
                if (tempimgList != null && tempimgList.size() != 0) {
                    if (tempimgList.size() == 1) {
                        try {
                            learningItemBinding.queImg1.setVisibility(View.VISIBLE);
                            Glide.with(activity).load(new URL(tempimgList.get(0))).into(learningItemBinding.queImg1);
                            List<String> finalTempimgList = tempimgList;
                            learningItemBinding.queImg1.setOnClickListener(v -> {
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("images", (Serializable) finalTempimgList);
                                bundle.putInt("position", 0);
                                FragmentTransaction fragmentTransaction = null;
                                if (activity instanceof MainActivity) {
                                    fragmentTransaction = ((MainActivity) activity).getSupportFragmentManager().beginTransaction();
                                }

                                if (activity instanceof PracticeTestActivity) {
                                    fragmentTransaction = ((PracticeTestActivity) activity).getSupportFragmentManager().beginTransaction();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void answerCharCounter(EditText etAnswer, TextView tvCounter, int maxWordLength) {

        InputFilter filter = (source, start, end, dest, dstart, dend) -> null;
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
                    for (int i = 0; i < words.length; i++) {
                        if (!words[i].isEmpty()) {
                            wordCount++;
                        }
                    }
                }

                if (wordCount == maxWordLength) {
                    etAnswer.setFilters(new InputFilter[]{new InputFilter.LengthFilter(s.length())});
                }
                tvCounter.setText("Words Remaining : " + (maxWordLength - wordCount + "/" + String.valueOf(maxWordLength)));
            }
        });
    }

    public void hideLayouts() {
        if (call_from == test) {
            learningItemBinding.expandableLayout.setVisibility(View.VISIBLE);
            learningItemBinding.expand.setVisibility(View.GONE);
            learningItemBinding.close.setVisibility(View.GONE);
        }
        learningItemBinding.reset.setVisibility(View.GONE);
        learningItemBinding.resetLabel.setVisibility(View.GONE);
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
        learningItemBinding.scqImgLayout.setVisibility(View.GONE);
        learningItemBinding.mcqImgLayout.setVisibility(View.GONE);
        learningItemBinding.scqImgtextLayout.setVisibility(View.GONE);
        learningItemBinding.mcqImgtextLayout.setVisibility(View.GONE);
        learningItemBinding.mtpImgLayout.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return learningQuestionsList.size();
    }

    public interface onIconClick {
        void onCommentClick(String questionId);
        void onShareClick(String questionId);
        void onSubmitClick(String questionId, int isRight);

    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        LearningItemBinding learningItemBinding;
        CountDownTimer countDownTimer;
        int isSolvedRight, isAttempted = 0;
        String scq_ans = "", mcq_ans = "", tfAns = "", scqimg_ans = "", scqimgtext_ans = "", mcqimg_ans = "", mcqimgtext_ans = "";
        HashMap<String, String> paired = new HashMap<String, String>();
        HashMap<String, String> imgpaired = new HashMap<String, String>();
        HashMap<String, String> MTP_ans = new HashMap<String, String>();
        boolean isB1Selected = false, isB2Selected = false, isB3Selected = false, isB4Selected = false, isMCQImgSubmited = false,
                isMCQImgTextSubmited = false;

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

            learningItemBinding.aMtp1.setOnTouchListener(new ChoiceTouchListener());
            learningItemBinding.aMtp2.setOnTouchListener(new ChoiceTouchListener());
            learningItemBinding.aMtp3.setOnTouchListener(new ChoiceTouchListener());
            learningItemBinding.aMtp4.setOnTouchListener(new ChoiceTouchListener());

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


            learningItemBinding.bMtp1.setOnDragListener(new ImgChoiceDragListener());
            learningItemBinding.bMtp2.setOnDragListener(new ImgChoiceDragListener());
            learningItemBinding.bMtp3.setOnDragListener(new ImgChoiceDragListener());
            learningItemBinding.bMtp4.setOnDragListener(new ImgChoiceDragListener());


            learningItemBinding.expand.setOnClickListener(v -> {
                learningItemBinding.expandableLayout.setVisibility(View.VISIBLE);
                learningItemBinding.timerLayout.setVisibility(View.VISIBLE);
                learningItemBinding.close.setVisibility(View.VISIBLE);
                learningItemBinding.expand.setVisibility(View.GONE);
                showLayout();
                setTimer(learningItemBinding.tvtimer, 0, 0);
            });

            learningItemBinding.saveQue.setOnClickListener(v -> saveToDb(learningQuestionsList.get(getAdapterPosition())));

            learningItemBinding.like.setOnClickListener(v -> {
                LearningQuestionsNew learningQuestions = learningQuestionsList.get(getAdapterPosition());
                if (learningQuestions.getIs_liked().equalsIgnoreCase("true")) {
                    ProcessQuestionAPI(learningQuestions.getQuestion_id(), 0, "like","","",getAdapterPosition(),"");
                } else {
                    ProcessQuestionAPI(learningQuestions.getQuestion_id(), 1, "like","","",getAdapterPosition(),"");
                }
            });

            learningItemBinding.share.setOnClickListener(v -> {
                LearningQuestionsNew learningQuestions = learningQuestionsList.get(getAdapterPosition());
                onIconClick.onShareClick(learningQuestions.getQuestion_id());
            });

            learningItemBinding.favorite.setOnClickListener(v -> {
                LearningQuestionsNew learningQuestions = learningQuestionsList.get(getAdapterPosition());
                if (learningQuestions.getIs_fav().equalsIgnoreCase("true")) {
                    ProcessQuestionAPI(learningQuestions.getQuestion_id(), 0, "fav","","",getAdapterPosition(),"");
                } else {
                    ProcessQuestionAPI(learningQuestions.getQuestion_id(), 1, "fav","","",getAdapterPosition(),"");
                }
            });

            learningItemBinding.close.setOnClickListener(v -> {
                countDownTimer.cancel();
                learningItemBinding.expandableLayout.setVisibility(View.GONE);
                learningItemBinding.timerLayout.setVisibility(View.GONE);
                learningItemBinding.expand.setVisibility(View.VISIBLE);
                learningItemBinding.close.setVisibility(View.GONE);
            });

            learningItemBinding.reset.setOnClickListener(v -> reset());
            learningItemBinding.resetLabel.setOnClickListener(v -> reset());

            learningItemBinding.commentLayout.setOnClickListener(v -> onIconClick.onCommentClick(learningQuestionsList.get(getAdapterPosition()).getQuestion_id()));

            learningItemBinding.mcqImgtextImg1.setOnClickListener(v -> {
                if (isMCQImgSubmited)
                    setMCQImgTextAnsIndicator();
                if (!mcqimgtext_ans.contains("A")) {
                    if (mcqimgtext_ans.equalsIgnoreCase(""))
                        mcqimgtext_ans = "A";
                    else
                        mcqimgtext_ans = mcqimgtext_ans + " A";
                    learningItemBinding.mcqImgtextImg1.setAlpha(130);
                    learningItemBinding.mcqimgImgtextChck1.setVisibility(View.VISIBLE);
                    learningItemBinding.mcqimgImgtextChck1.setImageDrawable(activity.getResources().getDrawable(R.drawable.selectmark));
                } else {
                    mcqimgtext_ans = mcqimgtext_ans.replace("A", "");
                    learningItemBinding.mcqImgtextImg1.setAlpha(255);
                    learningItemBinding.mcqimgImgtextChck1.setVisibility(View.GONE);
                }
            });

            learningItemBinding.mcqImgtextImg2.setOnClickListener(v -> {
                if (isMCQImgSubmited)
                    setMCQImgTextAnsIndicator();
                if (!mcqimgtext_ans.contains("B")) {
                    if (mcqimgtext_ans.equalsIgnoreCase(""))
                        mcqimgtext_ans = "B";
                    else
                        mcqimgtext_ans = mcqimgtext_ans + " B";
                    learningItemBinding.mcqImgtextImg2.setAlpha(130);
                    learningItemBinding.mcqimgImgtextChck2.setVisibility(View.VISIBLE);
                    learningItemBinding.mcqimgImgtextChck2.setImageDrawable(activity.getResources().getDrawable(R.drawable.selectmark));
                } else {
                    mcqimgtext_ans = mcqimgtext_ans.replace("B", "");
                    learningItemBinding.mcqImgtextImg2.setAlpha(255);
                    learningItemBinding.mcqimgImgtextChck2.setVisibility(View.GONE);
                }
            });

            learningItemBinding.mcqImgtextImg3.setOnClickListener(v -> {
                if (isMCQImgSubmited)
                    setMCQImgTextAnsIndicator();
                if (!mcqimgtext_ans.contains("C")) {
                    if (mcqimgtext_ans.equalsIgnoreCase(""))
                        mcqimgtext_ans = "C";
                    else
                        mcqimgtext_ans = mcqimgtext_ans + " C";
                    learningItemBinding.mcqImgtextImg3.setAlpha(130);
                    learningItemBinding.mcqimgImgtextChck3.setVisibility(View.VISIBLE);
                    learningItemBinding.mcqimgImgtextChck3.setImageDrawable(activity.getResources().getDrawable(R.drawable.selectmark));
                } else {
                    mcqimgtext_ans = mcqimgtext_ans.replace("C", "");
                    learningItemBinding.mcqImgtextImg3.setAlpha(255);
                    learningItemBinding.mcqimgImgtextChck3.setVisibility(View.GONE);
                }
            });

            learningItemBinding.mcqImgtextImg4.setOnClickListener(v -> {
                if (isMCQImgSubmited)
                    setMCQImgTextAnsIndicator();
                if (!mcqimgtext_ans.contains("D")) {
                    if (mcqimgtext_ans.equalsIgnoreCase(""))
                        mcqimgtext_ans = "D";
                    else
                        mcqimgtext_ans = mcqimgtext_ans + " D";
                    learningItemBinding.mcqImgtextImg4.setAlpha(130);
                    learningItemBinding.mcqimgImgtextChck4.setVisibility(View.VISIBLE);
                    learningItemBinding.mcqimgImgtextChck4.setImageDrawable(activity.getResources().getDrawable(R.drawable.selectmark));
                } else {
                    mcqimgtext_ans = mcqimgtext_ans.replace("D", "");
                    learningItemBinding.mcqImgtextImg4.setAlpha(255);
                    learningItemBinding.mcqimgImgtextChck4.setVisibility(View.GONE);
                }
            });

            learningItemBinding.mcqImg1.setOnClickListener(v -> {
                if (isMCQImgSubmited)
                    setMCQImgAnsIndicator();
                if (!mcqimg_ans.contains("A")) {
                    if (mcqimg_ans.equalsIgnoreCase(""))
                        mcqimg_ans = "A";
                    else
                        mcqimg_ans = mcqimg_ans + " A";
                    learningItemBinding.mcqImg1.setAlpha(130);
                    learningItemBinding.mcqimgChck1.setVisibility(View.VISIBLE);
                    learningItemBinding.mcqimgChck1.setImageDrawable(activity.getResources().getDrawable(R.drawable.selectmark));
                } else {
                    mcqimg_ans = mcqimg_ans.replace("A", "");
                    learningItemBinding.mcqImg1.setAlpha(255);
                    learningItemBinding.mcqimgChck1.setVisibility(View.GONE);
                }
            });

            learningItemBinding.mcqImg2.setOnClickListener(v -> {
                if (isMCQImgSubmited)
                    setMCQImgAnsIndicator();
                if (!mcqimg_ans.contains("B")) {
                    if (mcqimg_ans.equalsIgnoreCase(""))
                        mcqimg_ans = "B";
                    else
                        mcqimg_ans = mcqimg_ans + "B";
                    learningItemBinding.mcqImg2.setAlpha(130);
                    learningItemBinding.mcqimgChck2.setVisibility(View.VISIBLE);
                    learningItemBinding.mcqimgChck2.setImageDrawable(activity.getResources().getDrawable(R.drawable.selectmark));
                } else {
                    mcqimg_ans = mcqimg_ans.replace("B", "");
                    learningItemBinding.mcqImg2.setAlpha(255);
                    learningItemBinding.mcqimgChck2.setVisibility(View.GONE);
                }
            });

            learningItemBinding.mcqImg3.setOnClickListener(v -> {
                if (isMCQImgSubmited)
                    setMCQImgAnsIndicator();
                if (!mcqimg_ans.contains("C")) {
                    if (mcqimg_ans.equalsIgnoreCase(""))
                        mcqimg_ans = "C";
                    else
                        mcqimg_ans = mcqimg_ans + " C";
                    learningItemBinding.mcqImg3.setAlpha(130);
                    learningItemBinding.mcqimgChck3.setVisibility(View.VISIBLE);
                    learningItemBinding.mcqimgChck3.setImageDrawable(activity.getResources().getDrawable(R.drawable.selectmark));
                } else {
                    mcqimg_ans = mcqimg_ans.replace("C", "");
                    learningItemBinding.mcqImg3.setAlpha(255);
                    learningItemBinding.mcqimgChck3.setVisibility(View.GONE);
                }
            });

            learningItemBinding.mcqImg4.setOnClickListener(v -> {
                if (isMCQImgSubmited)
                    setMCQImgAnsIndicator();
                if (!mcqimg_ans.contains("D")) {
                    if (mcqimg_ans.equalsIgnoreCase(""))
                        mcqimg_ans = "D";
                    else
                        mcqimg_ans = mcqimg_ans + " D";
                    learningItemBinding.mcqImg4.setAlpha(130);
                    learningItemBinding.mcqimgChck4.setVisibility(View.VISIBLE);
                    learningItemBinding.mcqimgChck4.setImageDrawable(activity.getResources().getDrawable(R.drawable.selectmark));
                } else {
                    mcqimg_ans = mcqimg_ans.replace("D", "");
                    learningItemBinding.mcqImg4.setAlpha(255);
                    learningItemBinding.mcqimgChck4.setVisibility(View.GONE);
                }
            });


            learningItemBinding.mcq1Layout.setOnClickListener(v -> {
                setMCQAnsIndicator();
                if (!mcq_ans.contains("A")) {
                    if (mcq_ans.equalsIgnoreCase(""))
                        mcq_ans = "A";
                    else
                        mcq_ans = mcq_ans + " A";
                    learningItemBinding.mcq1Layout.setBackground(activity.getResources().getDrawable(R.drawable.grey_border_grey_bg));
                } else {
                    mcq_ans = mcq_ans.replace("A", "");
                    learningItemBinding.mcq1Layout.setBackground(activity.getResources().getDrawable(R.drawable.grey_round_order));
                }
            });

            learningItemBinding.mcq2Layout.setOnClickListener(v -> {
                setMCQAnsIndicator();
                if (!mcq_ans.contains("B")) {
                    if (mcq_ans.equalsIgnoreCase(""))
                        mcq_ans = "B";
                    else
                        mcq_ans = mcq_ans + " B";
                    learningItemBinding.mcq2Layout.setBackground(activity.getResources().getDrawable(R.drawable.grey_border_grey_bg));
                } else {
                    mcq_ans = mcq_ans.replace("B", "");
                    learningItemBinding.mcq2Layout.setBackground(activity.getResources().getDrawable(R.drawable.grey_round_order));
                }
            });

            learningItemBinding.mcq3Layout.setOnClickListener(v -> {
                setMCQAnsIndicator();
                if (!mcq_ans.contains("C")) {
                    if (mcq_ans.equalsIgnoreCase(""))
                        mcq_ans = "C";
                    else
                        mcq_ans = mcq_ans + " C";
                    learningItemBinding.mcq3Layout.setBackground(activity.getResources().getDrawable(R.drawable.grey_border_grey_bg));
                } else {
                    mcq_ans = mcq_ans.replace("C", "");
                    learningItemBinding.mcq3Layout.setBackground(activity.getResources().getDrawable(R.drawable.grey_round_order));
                }
            });

            learningItemBinding.mcq4Layout.setOnClickListener(v -> {
                setMCQAnsIndicator();
                if (!mcq_ans.contains("D")) {
                    if (mcq_ans.equalsIgnoreCase(""))
                        mcq_ans = "D";
                    else
                        mcq_ans = mcq_ans + " D";
                    learningItemBinding.mcq4Layout.setBackground(activity.getResources().getDrawable(R.drawable.grey_border_grey_bg));
                } else {
                    mcq_ans = mcq_ans.replace("D", "");
                    learningItemBinding.mcq4Layout.setBackground(activity.getResources().getDrawable(R.drawable.grey_round_order));
                }
            });

            learningItemBinding.scqImgtextImg1.setOnClickListener(v -> {
                setSCQImgTextAnsIndicator();
                setSCQImgTextLayout();
                scqimgtext_ans = "A";
                learningItemBinding.scqImgtextImg1.setAlpha(130);
                learningItemBinding.scqimgImgtextChck1.setVisibility(View.VISIBLE);
                learningItemBinding.scqimgImgtextChck1.setImageDrawable(activity.getResources().getDrawable(R.drawable.selectmark));
            });

            learningItemBinding.scqImgtextImg2.setOnClickListener(v -> {
                setSCQImgTextAnsIndicator();
                setSCQImgTextLayout();
                scqimgtext_ans = "B";
                learningItemBinding.scqImgtextImg2.setAlpha(130);
                learningItemBinding.scqimgImgtextChck2.setVisibility(View.VISIBLE);
                learningItemBinding.scqimgImgtextChck2.setImageDrawable(activity.getResources().getDrawable(R.drawable.selectmark));
            });

            learningItemBinding.scqImgtextImg3.setOnClickListener(v -> {
                setSCQImgTextAnsIndicator();
                setSCQImgTextLayout();
                scqimgtext_ans = "C";
                learningItemBinding.scqImgtextImg3.setAlpha(130);
                learningItemBinding.scqimgImgtextChck3.setVisibility(View.VISIBLE);
                learningItemBinding.scqimgImgtextChck3.setImageDrawable(activity.getResources().getDrawable(R.drawable.selectmark));
            });

            learningItemBinding.scqImgtextImg4.setOnClickListener(v -> {
                setSCQImgTextAnsIndicator();
                setSCQImgTextLayout();
                scqimgtext_ans = "D";
                learningItemBinding.scqImgtextImg4.setAlpha(130);
                learningItemBinding.scqimgImgtextChck4.setVisibility(View.VISIBLE);
                learningItemBinding.scqimgImgtextChck4.setImageDrawable(activity.getResources().getDrawable(R.drawable.selectmark));
            });


            learningItemBinding.scqImg1.setOnClickListener(v -> {
                setSCQImgAnsIndicator();
                setSCQImgLayout();
                scqimg_ans = "A";
                learningItemBinding.scqImg1.setAlpha(130);
                learningItemBinding.scqimgChck1.setVisibility(View.VISIBLE);
                learningItemBinding.scqimgChck1.setImageDrawable(activity.getResources().getDrawable(R.drawable.selectmark));
            });

            learningItemBinding.scqImg2.setOnClickListener(v -> {
                setSCQImgAnsIndicator();
                setSCQImgLayout();
                scqimg_ans = "B";
                learningItemBinding.scqImg2.setAlpha(130);
                learningItemBinding.scqimgChck2.setVisibility(View.VISIBLE);
                learningItemBinding.scqimgChck2.setImageDrawable(activity.getResources().getDrawable(R.drawable.selectmark));
            });

            learningItemBinding.scqImg3.setOnClickListener(v -> {
                setSCQImgAnsIndicator();
                setSCQImgLayout();
                scqimg_ans = "C";
                learningItemBinding.scqImg3.setAlpha(130);
                learningItemBinding.scqimgChck3.setVisibility(View.VISIBLE);
                learningItemBinding.scqimgChck3.setImageDrawable(activity.getResources().getDrawable(R.drawable.selectmark));
            });

            learningItemBinding.scqImg4.setOnClickListener(v -> {
                setSCQImgAnsIndicator();
                setSCQImgLayout();
                scqimg_ans = "D";
                learningItemBinding.scqImg4.setAlpha(130);
                learningItemBinding.scqimgChck4.setVisibility(View.VISIBLE);
                learningItemBinding.scqimgChck4.setImageDrawable(activity.getResources().getDrawable(R.drawable.selectmark));
            });

            learningItemBinding.scq1Layout.setOnClickListener(v -> {
                setSCQAnsIndicator();
                setLayoutBg();
                scq_ans = "A";
                learningItemBinding.scq1Layout.setBackground(activity.getResources().getDrawable(R.drawable.grey_border_grey_bg));
            });

            learningItemBinding.scq2Layout.setOnClickListener(v -> {
                setLayoutBg();
                setSCQAnsIndicator();
                scq_ans = "B";
                learningItemBinding.scq2Layout.setBackground(activity.getResources().getDrawable(R.drawable.grey_border_grey_bg));
            });

            learningItemBinding.scq3Layout.setOnClickListener(v -> {
                setLayoutBg();
                setSCQAnsIndicator();
                scq_ans = "C";
                learningItemBinding.scq3Layout.setBackground(activity.getResources().getDrawable(R.drawable.grey_border_grey_bg));
            });

            learningItemBinding.scq4Layout.setOnClickListener(v -> {
                setLayoutBg();
                setSCQAnsIndicator();
                scq_ans = "D";
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
                submitCall();
            });

            learningItemBinding.submitAndRate.setOnClickListener(v -> {
                submitCall();
                displayRatingDialog(learningQuestionsList.get(getAdapterPosition()).getQuestion_id(), getAdapterPosition());
            });
        }

        private void showLayout() {
            LearningQuestionsNew learningQuestions = learningQuestionsList.get(getAdapterPosition());
            if (learningQuestions.getType().equalsIgnoreCase(FILL_THE_BLANKS)) {
                learningItemBinding.fillInTheBlanks.setVisibility(View.VISIBLE);
                learningItemBinding.categoryTextview.setText("Fill in the Blanks");
            } else if (learningQuestions.getType().equalsIgnoreCase(ONE_LINE_ANSWER) || learningQuestions.getType().equalsIgnoreCase(SHORT_ANSWER)) {
                learningItemBinding.singleLine.setVisibility(View.VISIBLE);
                learningItemBinding.singleLineCounter.setVisibility(View.VISIBLE);
                learningItemBinding.categoryTextview.setText("Short Answer");
                answerCharCounter(learningItemBinding.singleLine, learningItemBinding.singleLineCounter, 200);
            } else if (learningQuestions.getType().equalsIgnoreCase(LONG_ANSWER)) {
                learningItemBinding.multiLine.setVisibility(View.VISIBLE);
                learningItemBinding.multiLineCounter.setVisibility(View.VISIBLE);
                answerCharCounter(learningItemBinding.multiLine, learningItemBinding.multiLineCounter, 400);
                learningItemBinding.categoryTextview.setText("Long Answer");
            } else {
                if (learningQuestions.getQue_option_type().equalsIgnoreCase(SCQ)) {
                    learningItemBinding.categoryTextview.setText("SCQ");
                    learningItemBinding.singleChoice.setVisibility(View.VISIBLE);
                    learningItemBinding.scq1.setText(learningQuestions.getMcq1());
                    learningItemBinding.scq2.setText(learningQuestions.getMcq2());
                    learningItemBinding.scq3.setText(learningQuestions.getMcq3());
                    learningItemBinding.scq4.setText(learningQuestions.getMcq4());
                } else if (learningQuestions.getQue_option_type().equalsIgnoreCase(SCQ_IMAGE)) {
                    learningItemBinding.categoryTextview.setText("SCQ");
                    learningItemBinding.scqImgLayout.setVisibility(View.VISIBLE);
                    try {
                        Glide.with(activity).load(new URL(learningQuestions.getMcq1())).into(learningItemBinding.scqImg1);
                        Glide.with(activity).load(new URL(learningQuestions.getMcq2())).into(learningItemBinding.scqImg2);
                        Glide.with(activity).load(new URL(learningQuestions.getMcq3())).into(learningItemBinding.scqImg3);
                        Glide.with(activity).load(new URL(learningQuestions.getMcq4())).into(learningItemBinding.scqImg4);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (learningQuestions.getQue_option_type().equalsIgnoreCase(SCQ_IMAGE_WITH_TEXT)) {
                    learningItemBinding.categoryTextview.setText("SCQ");
                    learningItemBinding.scqImgtextLayout.setVisibility(View.VISIBLE);
                    try {
                        Glide.with(activity).load(new URL(Constant.QUESTION_IMAGES_API + learningQuestions.getMcq1().split(":")[0].trim())).into(learningItemBinding.scqImgtextImg1);
                        Glide.with(activity).load(new URL(Constant.QUESTION_IMAGES_API + learningQuestions.getMcq2().split(":")[0].trim())).into(learningItemBinding.scqImgtextImg2);
                        Glide.with(activity).load(new URL(Constant.QUESTION_IMAGES_API + learningQuestions.getMcq3().split(":")[0].trim())).into(learningItemBinding.scqImgtextImg3);
                        Glide.with(activity).load(new URL(Constant.QUESTION_IMAGES_API + learningQuestions.getMcq4().split(":")[0].trim())).into(learningItemBinding.scqImgtextImg4);

                        learningItemBinding.scqImgtextText1.setText(learningQuestions.getMcq1().split(":")[1]);
                        learningItemBinding.scqImgtextText2.setText(learningQuestions.getMcq2().split(":")[1]);
                        learningItemBinding.scqImgtextText3.setText(learningQuestions.getMcq3().split(":")[1]);
                        learningItemBinding.scqImgtextText4.setText(learningQuestions.getMcq4().split(":")[1]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (learningQuestions.getQue_option_type().equalsIgnoreCase(MCQ_IMAGE_WITH_TEXT)) {
                    learningItemBinding.categoryTextview.setText("MCQ");
                    learningItemBinding.mcqImgtextLayout.setVisibility(View.VISIBLE);
                    try {
                        Glide.with(activity).load(new URL(Constant.QUESTION_IMAGES_API + learningQuestions.getMcq1().split(":")[0].trim())).into(learningItemBinding.mcqImgtextImg1);
                        Glide.with(activity).load(new URL(Constant.QUESTION_IMAGES_API + learningQuestions.getMcq2().split(":")[0].trim())).into(learningItemBinding.mcqImgtextImg2);
                        Glide.with(activity).load(new URL(Constant.QUESTION_IMAGES_API + learningQuestions.getMcq3().split(":")[0].trim())).into(learningItemBinding.mcqImgtextImg3);
                        Glide.with(activity).load(new URL(Constant.QUESTION_IMAGES_API + learningQuestions.getMcq4().split(":")[0].trim())).into(learningItemBinding.mcqImgtextImg4);


                        learningItemBinding.mcqImgtextText1.setText(learningQuestions.getMcq1().split(":")[1]);
                        learningItemBinding.mcqImgtextText2.setText(learningQuestions.getMcq2().split(":")[1]);
                        learningItemBinding.mcqImgtextText3.setText(learningQuestions.getMcq3().split(":")[1]);
                        learningItemBinding.mcqImgtextText4.setText(learningQuestions.getMcq4().split(":")[1]);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (learningQuestions.getQue_option_type().equalsIgnoreCase(MCQ_IMAGE)) {
                    learningItemBinding.categoryTextview.setText("MCQ");
                    learningItemBinding.mcqImgLayout.setVisibility(View.VISIBLE);
                    try {
                        Glide.with(activity).load(new URL(learningQuestions.getMcq1())).into(learningItemBinding.mcqImg1);
                        Glide.with(activity).load(new URL(learningQuestions.getMcq2())).into(learningItemBinding.mcqImg2);
                        Glide.with(activity).load(new URL(learningQuestions.getMcq3())).into(learningItemBinding.mcqImg3);
                        Glide.with(activity).load(new URL(learningQuestions.getMcq4())).into(learningItemBinding.mcqImg4);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (learningQuestions.getQue_option_type().equalsIgnoreCase(MCQ)) {
                    learningItemBinding.categoryTextview.setText("MCQ");
                    learningItemBinding.multiChoice.setVisibility(View.VISIBLE);
                    learningItemBinding.mcq1.setText(learningQuestions.getMcq1());
                    learningItemBinding.mcq2.setText(learningQuestions.getMcq2());
                    learningItemBinding.mcq3.setText(learningQuestions.getMcq3());
                    learningItemBinding.mcq4.setText(learningQuestions.getMcq4());
                } else if (learningQuestions.getQue_option_type().equalsIgnoreCase(TRUE_FALSE)) {
                    learningItemBinding.categoryTextview.setText("True False");
                    learningItemBinding.trueFalse.setVisibility(View.VISIBLE);
                } else if (learningQuestions.getQue_option_type().equalsIgnoreCase(MATCH_PAIR)) {
                    learningItemBinding.matchThePairs.setVisibility(View.VISIBLE);
                    learningItemBinding.reset.setVisibility(View.VISIBLE);
                    learningItemBinding.resetLabel.setVisibility(View.VISIBLE);
                    learningItemBinding.categoryTextview.setText("Match the Pairs");
//            learningItemBinding.a1text.setText(learningQuestions.getA1());
//            learningItemBinding.a2text.setText(learningQuestions.getA2());
//            learningItemBinding.a3text.setText(learningQuestions.getA3());
//            learningItemBinding.a4text.setText(learningQuestions.getA4());
//            learningItemBinding.b1text.setText(learningQuestions.getB1());
//            learningItemBinding.b2text.setText(learningQuestions.getB2());
//            learningItemBinding.b3text.setText(learningQuestions.getB3());
//            learningItemBinding.b4text.setText(learningQuestions.getB4());
                } else if (learningQuestions.getQue_option_type().equalsIgnoreCase(MATCH_PAIR_IMAGE)) {
                    learningItemBinding.mtpImgLayout.setVisibility(View.VISIBLE);
                    learningItemBinding.reset.setVisibility(View.VISIBLE);
                    learningItemBinding.resetLabel.setVisibility(View.VISIBLE);
                    learningItemBinding.categoryTextview.setText("Match the Pairs");
                }
            }
        }

        private void submitCall() {
            try {
                isSolvedRight = 1;
                isAttempted = 0;
                LearningQuestionsNew learningQuestions = learningQuestionsList.get(getAdapterPosition());
                submitFunction(learningQuestions);
                if (isAttempted == 1) {
                    onIconClick.onSubmitClick(learningQuestions.getQuestion_id(), isSolvedRight);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void submitFunction(LearningQuestionsNew learningQuestions) {
            if (learningQuestions.getType().equalsIgnoreCase(FILL_THE_BLANKS)) {
                if (learningQuestions.getQue_option_type().equalsIgnoreCase(FILL_THE_BLANKS)) {
                    learningItemBinding.fillInTheBlanks.setBackground(activity.getResources().getDrawable(R.drawable.grey_border));
                    if (!learningItemBinding.fillInTheBlanks.getText().toString().trim().equalsIgnoreCase("")) {
                        isAttempted = 1;
                        if (learningItemBinding.fillInTheBlanks.getText().toString().trim().equalsIgnoreCase(learningQuestions.getAnswer().toString().trim())) {
                            learningItemBinding.fibImg.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right));
                            learningItemBinding.fillInTheBlanks.setBackground(activity.getResources().getDrawable(R.drawable.green_border));
                        } else {
                            isSolvedRight = 0;
                            learningItemBinding.fillInTheBlanks.setBackground(activity.getResources().getDrawable(R.drawable.red_border));
                            learningItemBinding.fibImg.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong));
                        }
                        learningItemBinding.solutionLayout.setVisibility(View.VISIBLE);
                        learningItemBinding.fibImg.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(activity, "Please enter answer first.", Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (learningQuestions.getType().equalsIgnoreCase(ONE_LINE_ANSWER) || learningQuestions.getType().equalsIgnoreCase(SHORT_ANSWER)) {
                if (learningItemBinding.singleLine.getText().toString().isEmpty()) {
                    Toast.makeText(activity, "Please enter answer first.", Toast.LENGTH_SHORT).show();
                } else {
                    isAttempted = 1;
                    ProcessQuestionAPI(learningQuestions.getQuestion_id(), 0, ONE_LINE_ANSWER,"","",getAdapterPosition(),learningItemBinding.singleLine.getText().toString());
                }

            } else if (learningQuestions.getType().equalsIgnoreCase(LONG_ANSWER)) {
                if (learningItemBinding.multiLine.getText().toString().isEmpty()) {
                    Toast.makeText(activity, "Please enter answer first.", Toast.LENGTH_SHORT).show();
                } else {
                    isAttempted = 1;
                    ProcessQuestionAPI(learningQuestions.getQuestion_id(), 0, LONG_ANSWER,"","",getAdapterPosition(), learningItemBinding.multiLine.getText().toString());
                }

            } else {
                if (learningQuestions.getQue_option_type().equalsIgnoreCase(SCQ)) {
                    if (!scq_ans.trim().equalsIgnoreCase("")) {
                        isAttempted = 1;
                        setSCQAnsIndicator();
                        if (scq_ans.equalsIgnoreCase(learningQuestions.getAnswer())) {
                            setRightSCQ(scq_ans);
                        } else {
                            isSolvedRight = 0;
                            setRightSCQ(learningQuestions.getAnswer());
                            setWrongSCQ(scq_ans);
                        }
                        learningItemBinding.solutionLayout.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(activity, "Please select atleast one option.", Toast.LENGTH_SHORT).show();
                    }
                } else if (learningQuestions.getQue_option_type().equalsIgnoreCase(SCQ_IMAGE)) {
                    if (!scqimg_ans.trim().equalsIgnoreCase("")) {
                        isAttempted = 1;
                        setSCQImgAnsIndicator();
                        if (scqimg_ans.equalsIgnoreCase(learningQuestions.getAnswer())) {
                            setRightSCQImg(scqimg_ans);
                        } else {
                            isSolvedRight = 0;
                            setRightSCQImg(learningQuestions.getAnswer());
                            setWrongSCQImg(scqimg_ans);
                        }
                        learningItemBinding.solutionLayout.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(activity, "Please select atleast one option.", Toast.LENGTH_SHORT).show();
                    }
                } else if (learningQuestions.getQue_option_type().equalsIgnoreCase(SCQ_IMAGE_WITH_TEXT)) {
                    if (!scqimgtext_ans.trim().equalsIgnoreCase("")) {
                        isAttempted = 1;
                        setSCQImgTextAnsIndicator();
                        if (scqimgtext_ans.equalsIgnoreCase(learningQuestions.getAnswer())) {
                            setRightSCQImgText(scqimgtext_ans);
                        } else {
                            isSolvedRight = 0;
                            setRightSCQImgText(learningQuestions.getAnswer());
                            setWrongSCQImgText(scqimgtext_ans);
                        }
                        learningItemBinding.solutionLayout.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(activity, "Please select atleast one option.", Toast.LENGTH_SHORT).show();
                    }
                } else if (learningQuestions.getQue_option_type().equalsIgnoreCase(MCQ_IMAGE)) {
                    if (!mcqimg_ans.trim().equalsIgnoreCase("")) {
                        isAttempted = 1;
                        isMCQImgSubmited = true;
                        String[] selected_mcq = mcqimg_ans.split("\\s+");
                        String[] right_mcq = learningQuestions.getAnswer().split(",");
                        for (int i = 0; i < selected_mcq.length; i++) {
                            if (learningQuestions.getAnswer().contains(selected_mcq[i])) {
                                setRightMCQImg(selected_mcq[i]);
                            } else {
                                isSolvedRight = 0;
                                setWrongMCQImg(selected_mcq[i]);
                            }
                        }
                        for (int i = 0; i < right_mcq.length; i++) {
                            setRightMCQImg(right_mcq[i]);
                        }
                        learningItemBinding.solutionLayout.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(activity, "Please select atleast one option.", Toast.LENGTH_SHORT).show();
                    }

                } else if (learningQuestions.getQue_option_type().equalsIgnoreCase(MCQ_IMAGE_WITH_TEXT)) {
                    if (!mcqimgtext_ans.trim().equalsIgnoreCase("")) {
                        isAttempted = 1;
                        isMCQImgTextSubmited = true;
                        String[] selected_mcq = mcqimgtext_ans.split("\\s+");
                        String[] right_mcq = learningQuestions.getAnswer().split(",");
                        for (int i = 0; i < selected_mcq.length; i++) {
                            if (learningQuestions.getAnswer().contains(selected_mcq[i])) {
                                setRightMCQImgText(selected_mcq[i]);
                            } else {
                                isSolvedRight = 0;
                                setWrongMCQImgText(selected_mcq[i]);
                            }
                        }
                        for (int i = 0; i < right_mcq.length; i++) {
                            setRightMCQImgText(right_mcq[i]);
                        }
                        learningItemBinding.solutionLayout.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(activity, "Please select atleast one option.", Toast.LENGTH_SHORT).show();
                    }

                } else if (learningQuestions.getQue_option_type().equalsIgnoreCase(MCQ)) {
                    if (!mcq_ans.trim().equalsIgnoreCase("")) {
                        isAttempted = 1;
                        setMCQAnsIndicator();
                        String[] selected_mcq = mcq_ans.split("\\s+");
                        String[] right_mcq = learningQuestions.getAnswer().split(",");
                        for (int i = 0; i < selected_mcq.length; i++) {
                            if (learningQuestions.getAnswer().contains(selected_mcq[i])) {
                                setRightMCQ(selected_mcq[i]);
                            } else {
                                isSolvedRight = 0;
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

                } else if (learningQuestions.getQue_option_type().equalsIgnoreCase(TRUE_FALSE)) {
                    if (!tfAns.equalsIgnoreCase("")) {
                        isAttempted = 1;
                        if (tfAns.equalsIgnoreCase(learningQuestions.getAnswer())) {
                            setRightTF("true");
                            learningItemBinding.btntrue.setTextColor(activity.getResources().getColor(R.color.white));
                        } else {
                            isSolvedRight = 0;
                            setWrongTF(tfAns);
                            learningItemBinding.btnfalse.setTextColor(activity.getResources().getColor(R.color.white));
                        }
                        learningItemBinding.solutionLayout.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(activity, "Please select atleast one option.", Toast.LENGTH_SHORT).show();
                    }
                } else if (learningQuestions.getQue_option_type().equalsIgnoreCase(MATCH_PAIR)) {
                    if (!isB1Selected || !isB2Selected || !isB3Selected || !isB4Selected) {
                        Toast.makeText(activity, "Select all pairs first.", Toast.LENGTH_SHORT).show();
                    } else {
                        isAttempted = 1;
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
                                isSolvedRight = 0;
                                setWrongPair(value);
                            }
                        }
                    }
                } else if (learningQuestions.getQue_option_type().equalsIgnoreCase(MATCH_PAIR_IMAGE)) {

                    if (!isB1Selected || !isB2Selected || !isB3Selected || !isB4Selected) {
                        Toast.makeText(activity, "Select all pairs first.", Toast.LENGTH_SHORT).show();
                    } else {
                        isAttempted = 1;
                        boolean isFound = false;
                        for (Map.Entry<String, String> entry : imgpaired.entrySet()) {
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
                                setImgRightPair(value);
                            } else {
                                isSolvedRight = 0;
                                setImgWrongPair(value);
                            }
                        }
                    }
                }
            }

        }

        private void saveToDb(LearningQuestionsNew learningQuestions) {
//            class SaveTask extends AsyncTask<Void, Void, Void> {
//
//                @Override
//                protected Void doInBackground(Void... voids) {
//                    //adding to database
//                    try {
//                        QoogolDatabase.getDatabase(getApplicationContext())
//                                .learningQuestionDao()
//                                .insert(learningQuestions);
//
//                        List<LearningQuestionsNew> learningQuestions1 = QoogolDatabase.getDatabase(getApplicationContext())
//                                .learningQuestionDao()
//                                .getAll();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    return null;
//                }
//
//                @Override
//                protected void onPostExecute(Void aVoid) {
//                    super.onPostExecute(aVoid);
//                    Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_LONG).show();
//                }
//            }
//
//            SaveTask st = new SaveTask();
//            st.execute();

            throw new RuntimeException("Test Crash");
        }

        private void setTimer(TextView timer, int seconds, int minutes) {
            countDownTimer = new CountDownTimer(60 * 1000 * 60, 1000) {
                int timerCountSeconds = seconds;
                int timerCountMinutes = minutes;

                public void onTick(long millisUntilFinished) {
                    // timer.setText(new SimpleDateFormat("mm:ss").format(new Date( millisUntilFinished)));
                    if (timerCountSeconds < 59) {
                        timerCountSeconds++;
                    } else {
                        timerCountSeconds = 0;
                        timerCountMinutes++;
                    }
                    if (timerCountMinutes < 10) {
                        if (timerCountSeconds < 10) {
                            timer.setText(String.valueOf("0" + timerCountMinutes + ":0" + timerCountSeconds));
                        } else {
                            timer.setText(String.valueOf("0" + timerCountMinutes + ":" + timerCountSeconds));
                        }
                    } else {
                        if (timerCountSeconds < 10) {
                            timer.setText(String.valueOf(timerCountMinutes + ":0" + timerCountSeconds));
                        } else {
                            timer.setText(String.valueOf(timerCountMinutes + ":" + timerCountSeconds));
                        }
                    }
                }

                public void onFinish() {
                    timer.setText("00:00");
                }
            }.start();
        }


        private void setSCQImgAnsIndicator() {
            learningItemBinding.scqimgChck1.setVisibility(View.GONE);
            learningItemBinding.scqimgChck2.setVisibility(View.GONE);
            learningItemBinding.scqimgChck3.setVisibility(View.GONE);
            learningItemBinding.scqimgChck4.setVisibility(View.GONE);
        }

        private void setSCQImgTextAnsIndicator() {
            learningItemBinding.scqimgImgtextChck1.setVisibility(View.GONE);
            learningItemBinding.scqimgImgtextChck2.setVisibility(View.GONE);
            learningItemBinding.scqimgImgtextChck3.setVisibility(View.GONE);
            learningItemBinding.scqimgImgtextChck4.setVisibility(View.GONE);
        }

        private void setMCQImgTextAnsIndicator() {
            isMCQImgTextSubmited = false;
            mcqimg_ans = "";
            learningItemBinding.mcqimgImgtextChck1.setVisibility(View.GONE);
            learningItemBinding.mcqimgImgtextChck2.setVisibility(View.GONE);
            learningItemBinding.mcqimgImgtextChck3.setVisibility(View.GONE);
            learningItemBinding.mcqimgImgtextChck4.setVisibility(View.GONE);
            learningItemBinding.mcqImgtextImg1.setAlpha(250);
            learningItemBinding.mcqImgtextImg2.setAlpha(250);
            learningItemBinding.mcqImgtextImg3.setAlpha(250);
            learningItemBinding.mcqImgtextImg4.setAlpha(250);
        }

        private void setMCQImgAnsIndicator() {
            isMCQImgSubmited = false;
            mcqimg_ans = "";
            learningItemBinding.mcqimgChck1.setVisibility(View.GONE);
            learningItemBinding.mcqimgChck2.setVisibility(View.GONE);
            learningItemBinding.mcqimgChck3.setVisibility(View.GONE);
            learningItemBinding.mcqimgChck4.setVisibility(View.GONE);
            learningItemBinding.mcqImg1.setAlpha(250);
            learningItemBinding.mcqImg2.setAlpha(250);
            learningItemBinding.mcqImg3.setAlpha(250);
            learningItemBinding.mcqImg4.setAlpha(250);
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

        private void setSCQImgLayout() {
            learningItemBinding.scqImg1.setAlpha(255);
            learningItemBinding.scqImg2.setAlpha(255);
            learningItemBinding.scqImg3.setAlpha(255);
            learningItemBinding.scqImg4.setAlpha(255);
        }

        private void setSCQImgTextLayout() {
            learningItemBinding.scqImgtextImg1.setAlpha(255);
            learningItemBinding.scqImgtextImg2.setAlpha(255);
            learningItemBinding.scqImgtextImg3.setAlpha(255);
            learningItemBinding.scqImgtextImg4.setAlpha(255);
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

        public void setRightMCQImg(String option) {
            switch (option) {
                case "A":
                    learningItemBinding.mcqimgChck1.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right_mtp));
                    learningItemBinding.mcqimgChck1.setVisibility(View.VISIBLE);
                    break;
                case "B":
                    learningItemBinding.mcqimgChck2.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right_mtp));
                    learningItemBinding.mcqimgChck2.setVisibility(View.VISIBLE);
                    break;
                case "C":
                    learningItemBinding.mcqimgChck3.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right_mtp));
                    learningItemBinding.mcqimgChck3.setVisibility(View.VISIBLE);
                    break;
                case "D":
                    learningItemBinding.mcqimgChck4.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right_mtp));
                    learningItemBinding.mcqimgChck4.setVisibility(View.VISIBLE);
                    break;
            }
        }

        public void setWrongMCQImg(String option) {
            switch (option) {
                case "A":
                    learningItemBinding.mcqimgChck1.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                    learningItemBinding.mcqimgChck1.setVisibility(View.VISIBLE);
                    break;
                case "B":
                    learningItemBinding.mcqimgChck2.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                    learningItemBinding.mcqimgChck2.setVisibility(View.VISIBLE);
                    break;
                case "C":
                    learningItemBinding.mcqimgChck3.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                    learningItemBinding.mcqimgChck3.setVisibility(View.VISIBLE);
                    break;
                case "D":
                    learningItemBinding.mcqimgChck4.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                    learningItemBinding.mcqimgChck4.setVisibility(View.VISIBLE);
                    break;
            }
        }

        public void setRightMCQImgText(String option) {
            switch (option) {
                case "A":
                    learningItemBinding.mcqimgImgtextChck1.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right_mtp));
                    learningItemBinding.mcqimgImgtextChck1.setVisibility(View.VISIBLE);
                    break;
                case "B":
                    learningItemBinding.mcqimgImgtextChck2.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right_mtp));
                    learningItemBinding.mcqimgImgtextChck2.setVisibility(View.VISIBLE);
                    break;
                case "C":
                    learningItemBinding.mcqimgImgtextChck3.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right_mtp));
                    learningItemBinding.mcqimgImgtextChck3.setVisibility(View.VISIBLE);
                    break;
                case "D":
                    learningItemBinding.mcqimgImgtextChck4.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right_mtp));
                    learningItemBinding.mcqimgImgtextChck4.setVisibility(View.VISIBLE);
                    break;
            }
        }

        public void setWrongMCQImgText(String option) {
            switch (option) {
                case "A":
                    learningItemBinding.mcqimgImgtextChck1.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                    learningItemBinding.mcqimgImgtextChck1.setVisibility(View.VISIBLE);
                    break;
                case "B":
                    learningItemBinding.mcqimgImgtextChck2.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                    learningItemBinding.mcqimgImgtextChck2.setVisibility(View.VISIBLE);
                    break;
                case "C":
                    learningItemBinding.mcqimgImgtextChck3.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                    learningItemBinding.mcqimgImgtextChck3.setVisibility(View.VISIBLE);
                    break;
                case "D":
                    learningItemBinding.mcqimgImgtextChck4.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                    learningItemBinding.mcqimgImgtextChck4.setVisibility(View.VISIBLE);
                    break;
            }
        }

        public void setRightSCQImgText(String option) {
            switch (option) {
                case "A":
                    learningItemBinding.scqimgImgtextChck1.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right_mtp));
                    learningItemBinding.scqimgImgtextChck1.setVisibility(View.VISIBLE);
                    break;
                case "B":
                    learningItemBinding.scqimgImgtextChck2.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right_mtp));
                    learningItemBinding.scqimgImgtextChck2.setVisibility(View.VISIBLE);
                    break;
                case "C":
                    learningItemBinding.scqimgImgtextChck3.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right_mtp));
                    learningItemBinding.scqimgImgtextChck3.setVisibility(View.VISIBLE);
                    break;
                case "D":
                    learningItemBinding.scqimgImgtextChck4.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right_mtp));
                    learningItemBinding.scqimgImgtextChck4.setVisibility(View.VISIBLE);
                    break;
            }
        }

        public void setWrongSCQImgText(String option) {
            switch (option) {
                case "A":
                    learningItemBinding.scqimgImgtextChck1.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                    learningItemBinding.scqimgImgtextChck1.setVisibility(View.VISIBLE);
                    break;
                case "B":
                    learningItemBinding.scqimgImgtextChck2.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                    learningItemBinding.scqimgImgtextChck2.setVisibility(View.VISIBLE);
                    break;
                case "C":
                    learningItemBinding.scqimgImgtextChck3.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                    learningItemBinding.scqimgImgtextChck3.setVisibility(View.VISIBLE);
                    break;
                case "D":
                    learningItemBinding.scqimgImgtextChck4.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                    learningItemBinding.scqimgImgtextChck4.setVisibility(View.VISIBLE);
                    break;
            }
        }


        public void setRightSCQImg(String option) {
            switch (option) {
                case "A":
                    learningItemBinding.scqimgChck1.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right_mtp));
                    learningItemBinding.scqimgChck1.setVisibility(View.VISIBLE);
                    break;
                case "B":
                    learningItemBinding.scqimgChck2.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right_mtp));
                    learningItemBinding.scqimgChck2.setVisibility(View.VISIBLE);
                    break;
                case "C":
                    learningItemBinding.scqimgChck3.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right_mtp));
                    learningItemBinding.scqimgChck3.setVisibility(View.VISIBLE);
                    break;
                case "D":
                    learningItemBinding.scqimgChck4.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right_mtp));
                    learningItemBinding.scqimgChck4.setVisibility(View.VISIBLE);
                    break;
            }
        }

        public void setWrongSCQImg(String option) {
            switch (option) {
                case "A":
                    learningItemBinding.scqimgChck1.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                    learningItemBinding.scqimgChck1.setVisibility(View.VISIBLE);
                    break;
                case "B":
                    learningItemBinding.scqimgChck2.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                    learningItemBinding.scqimgChck2.setVisibility(View.VISIBLE);
                    break;
                case "C":
                    learningItemBinding.scqimgChck3.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                    learningItemBinding.scqimgChck3.setVisibility(View.VISIBLE);
                    break;
                case "D":
                    learningItemBinding.scqimgChck4.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                    learningItemBinding.scqimgChck4.setVisibility(View.VISIBLE);
                    break;
            }
        }

        public void setRightSCQ(String option) {
            switch (option) {
                case "A":
                    learningItemBinding.scq1Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right));
                    learningItemBinding.scq1Img.setVisibility(View.VISIBLE);
                    break;
                case "B":
                    learningItemBinding.scq2Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right));
                    learningItemBinding.scq2Img.setVisibility(View.VISIBLE);
                    break;
                case "C":
                    learningItemBinding.scq3Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right));
                    learningItemBinding.scq3Img.setVisibility(View.VISIBLE);
                    break;
                case "D":
                    learningItemBinding.scq4Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right));
                    learningItemBinding.scq4Img.setVisibility(View.VISIBLE);
                    break;
            }
        }

        public void setWrongSCQ(String option) {
            switch (option) {
                case "A":
                    learningItemBinding.scq1Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong));
                    learningItemBinding.scq1Img.setVisibility(View.VISIBLE);
                    break;
                case "B":
                    learningItemBinding.scq2Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong));
                    learningItemBinding.scq2Img.setVisibility(View.VISIBLE);
                    break;
                case "C":
                    learningItemBinding.scq3Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong));
                    learningItemBinding.scq3Img.setVisibility(View.VISIBLE);
                    break;
                case "D":
                    learningItemBinding.scq4Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong));
                    learningItemBinding.scq4Img.setVisibility(View.VISIBLE);
                    break;
            }
        }

        public void setRightMCQ(String option) {
            switch (option) {
                case "A":
                    learningItemBinding.mcq1Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right));
                    learningItemBinding.mcq1Img.setVisibility(View.VISIBLE);
                    break;
                case "B":
                    learningItemBinding.mcq2Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right));
                    learningItemBinding.mcq2Img.setVisibility(View.VISIBLE);
                    break;
                case "C":
                    learningItemBinding.mcq3Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right));
                    learningItemBinding.mcq3Img.setVisibility(View.VISIBLE);
                    break;
                case "D":
                    learningItemBinding.mcq4Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right));
                    learningItemBinding.mcq4Img.setVisibility(View.VISIBLE);
                    break;
            }
        }

        public void setWrongMCQ(String option) {
            switch (option) {
                case "A":
                    learningItemBinding.mcq1Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong));
                    learningItemBinding.mcq1Img.setVisibility(View.VISIBLE);
                    break;
                case "B":
                    learningItemBinding.mcq2Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong));
                    learningItemBinding.mcq2Img.setVisibility(View.VISIBLE);
                    break;
                case "C":
                    learningItemBinding.mcq3Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong));
                    learningItemBinding.mcq3Img.setVisibility(View.VISIBLE);
                    break;
                case "D":
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

        @SuppressLint("NewApi")
        private class ImgChoiceDragListener implements View.OnDragListener {

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
                        ImageView dropped = (ImageView) view;

                        //make it bold to highlight the fact that an item has been dropped
                        switch (v.getId()) {
                            case R.id.b_mtp1:
                                checkImgPairAvailability(getNameFromId(dropped.getId()), "b1");
                                imgpaired.put(getNameFromId(dropped.getId()), "b1");
                                isB1Selected = true;
                                learningItemBinding.bMtpChck1.setVisibility(View.VISIBLE);
                                learningItemBinding.bMtpChck1.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_mtp1));
                                setMtpMatchedPair(getNameFromId(dropped.getId()), activity.getResources().getDrawable(R.drawable.ic_mtp1));
                                if (imgpaired.size() == MTP_ans.size() - 1) {
                                    setImgMtpLastPair();
                                }
                                break;
                            case R.id.b_mtp2:
                                checkImgPairAvailability(getNameFromId(dropped.getId()), "b2");
                                imgpaired.put(getNameFromId(dropped.getId()), "b2");
                                isB2Selected = true;
                                learningItemBinding.bMtpChck2.setVisibility(View.VISIBLE);
                                learningItemBinding.bMtpChck2.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_mtp2));
                                setMtpMatchedPair(getNameFromId(dropped.getId()), activity.getResources().getDrawable(R.drawable.ic_mtp2));
                                if (imgpaired.size() == MTP_ans.size() - 1) {
                                    setImgMtpLastPair();
                                }
                                break;
                            case R.id.b_mtp3:
                                checkImgPairAvailability(getNameFromId(dropped.getId()), "b3");
                                imgpaired.put(getNameFromId(dropped.getId()), "b3");
                                isB3Selected = true;
                                learningItemBinding.bMtpChck3.setVisibility(View.VISIBLE);
                                learningItemBinding.bMtpChck3.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_mtp3));
                                setMtpMatchedPair(getNameFromId(dropped.getId()), activity.getResources().getDrawable(R.drawable.ic_mtp3));
                                if (imgpaired.size() == MTP_ans.size() - 1) {
                                    setImgMtpLastPair();
                                }
                                break;
                            case R.id.b_mtp4:
                                checkImgPairAvailability(getNameFromId(dropped.getId()), "b4");
                                imgpaired.put(getNameFromId(dropped.getId()), "b4");
                                isB3Selected = true;
                                learningItemBinding.bMtpChck4.setVisibility(View.VISIBLE);
                                learningItemBinding.bMtpChck4.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_mtp4));
                                setMtpMatchedPair(getNameFromId(dropped.getId()), activity.getResources().getDrawable(R.drawable.ic_mtp4));
                                if (imgpaired.size() == MTP_ans.size() - 1) {
                                    setImgMtpLastPair();
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
                                setmatchedPair(getNameFromId(dropped.getId()), activity.getResources().getDrawable(R.drawable.ic_mtp1));
                                learningItemBinding.b1Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_mtp1));
                                if (paired.size() == MTP_ans.size() - 1) {
                                    setLastPair();
                                }
                                break;
                            case R.id.b2:
                            case R.id.b2text:
                                checkAvailability(getNameFromId(dropped.getId()), "b2");
                                paired.put(getNameFromId(dropped.getId()), "b2");
                                isB2Selected = true;
                                setmatchedPair(getNameFromId(dropped.getId()), activity.getResources().getDrawable(R.drawable.ic_mtp2));
                                learningItemBinding.b2Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_mtp2));
                                if (paired.size() == MTP_ans.size() - 1) {
                                    setLastPair();
                                }
                                break;
                            case R.id.b3:
                            case R.id.b3text:
                                checkAvailability(getNameFromId(dropped.getId()), "b3");
                                paired.put(getNameFromId(dropped.getId()), "b3");
                                isB3Selected = true;
                                setmatchedPair(getNameFromId(dropped.getId()), activity.getResources().getDrawable(R.drawable.ic_mtp3));
                                learningItemBinding.b3Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_mtp3));
                                if (paired.size() == MTP_ans.size() - 1) {
                                    setLastPair();
                                }
                                break;
                            case R.id.b4:
                            case R.id.b4text:
                                checkAvailability(getNameFromId(dropped.getId()), "b4");
                                paired.put(getNameFromId(dropped.getId()), "b4");
                                isB4Selected = true;
                                setmatchedPair(getNameFromId(dropped.getId()), activity.getResources().getDrawable(R.drawable.ic_mtp4));
                                learningItemBinding.b4Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_mtp4));
                                if (paired.size() == MTP_ans.size() - 1) {
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

        private void setImgMtpLastPair() {
            String a_group = "", b_group = "";
            if (!imgpaired.containsKey("a1")) {
                a_group = "a1";
            } else if (!imgpaired.containsKey("a2")) {
                a_group = "a2";
            } else if (!imgpaired.containsKey("a3")) {
                a_group = "a3";
            } else if (!imgpaired.containsKey("a4")) {
                a_group = "a4";
            }

            if (!imgpaired.containsValue("b1")) {
                b_group = "b1";
            } else if (!imgpaired.containsValue("b2")) {
                b_group = "b2";
            } else if (!imgpaired.containsValue("b3")) {
                b_group = "b3";
            } else if (!imgpaired.containsValue("b4")) {
                b_group = "b4";
            }

            if (!a_group.equalsIgnoreCase("") && !b_group.equalsIgnoreCase("")) {
                imgpaired.put(a_group, b_group);
                setMtpMatchedPair(a_group, getPairColor(b_group));
                setMtpMatchedPair(b_group, getPairColor(b_group));
            }
        }

        private void setLastPair() {
            String a_group = "", b_group = "";
            if (!paired.containsKey("a1")) {
                a_group = "a1";
            } else if (!paired.containsKey("a2")) {
                a_group = "a2";
            } else if (!paired.containsKey("a3")) {
                a_group = "a3";
            } else if (!paired.containsKey("a4")) {
                a_group = "a4";
            }

            if (!paired.containsValue("b1")) {
                b_group = "b1";
            } else if (!paired.containsValue("b2")) {
                b_group = "b2";
            } else if (!paired.containsValue("b3")) {
                b_group = "b3";
            } else if (!paired.containsValue("b4")) {
                b_group = "b4";
            }

            if (!a_group.equalsIgnoreCase("") && !b_group.equalsIgnoreCase("")) {
                paired.put(a_group, b_group);
                setmatchedPair(a_group, getPairColor(b_group));
                setmatchedPair(b_group, getPairColor(b_group));
            }
        }

        private Drawable getPairColor(String option) {
            switch (option) {
                case "b1":
                    return activity.getResources().getDrawable(R.drawable.ic_mtp1);
                case "b2":
                    return activity.getResources().getDrawable(R.drawable.ic_mtp2);
                case "b3":
                    return activity.getResources().getDrawable(R.drawable.ic_mtp3);
                case "b4":
                    return activity.getResources().getDrawable(R.drawable.ic_mtp4);
                default:
                    return activity.getResources().getDrawable(R.drawable.ic_mtp_grey);
            }
        }

        public void setMtpMatchedPair(String option, Drawable drawable) {
            switch (option) {
                case "b1":
                    learningItemBinding.bMtpChck1.setImageDrawable(drawable);
                    learningItemBinding.bMtpChck1.setVisibility(View.VISIBLE);
                    learningItemBinding.bMtp1.setAlpha(150);
                    break;
                case "b2":
                    learningItemBinding.bMtpChck2.setImageDrawable(drawable);
                    learningItemBinding.bMtpChck2.setVisibility(View.VISIBLE);
                    learningItemBinding.bMtp2.setAlpha(150);
                    break;
                case "b3":
                    learningItemBinding.bMtpChck3.setImageDrawable(drawable);
                    learningItemBinding.bMtpChck3.setVisibility(View.VISIBLE);
                    learningItemBinding.bMtp3.setAlpha(150);
                    break;
                case "b4":
                    learningItemBinding.bMtpChck4.setImageDrawable(drawable);
                    learningItemBinding.bMtpChck4.setVisibility(View.VISIBLE);
                    learningItemBinding.bMtp4.setAlpha(150);
                    break;
                case "a1":
                    learningItemBinding.aMtpChck1.setImageDrawable(drawable);
                    learningItemBinding.aMtpChck1.setVisibility(View.VISIBLE);
                    learningItemBinding.aMtp1.setAlpha(150);
                    break;
                case "a2":
                    learningItemBinding.aMtpChck2.setImageDrawable(drawable);
                    learningItemBinding.aMtpChck2.setVisibility(View.VISIBLE);
                    learningItemBinding.aMtp2.setAlpha(150);
                    break;
                case "a3":
                    learningItemBinding.aMtpChck3.setImageDrawable(drawable);
                    learningItemBinding.aMtpChck3.setVisibility(View.VISIBLE);
                    learningItemBinding.aMtp3.setAlpha(150);
                    break;
                case "a4":
                    learningItemBinding.aMtpChck4.setImageDrawable(drawable);
                    learningItemBinding.aMtpChck4.setVisibility(View.VISIBLE);
                    learningItemBinding.aMtp4.setAlpha(150);
                    break;
                default:
                    break;
            }
        }

        public void setmatchedPair(String option, Drawable drawable) {
            switch (option) {
                case "b1":
                    learningItemBinding.b1Img.setImageDrawable(drawable);
                    break;
                case "b2":
                    learningItemBinding.b2Img.setImageDrawable(drawable);
                    break;
                case "b3":
                    learningItemBinding.b3Img.setImageDrawable(drawable);
                    break;
                case "b4":
                    learningItemBinding.b4Img.setImageDrawable(drawable);
                    break;
                case "a1":
                    learningItemBinding.a1Img.setImageDrawable(drawable);
                    break;
                case "a2":
                    learningItemBinding.a2Img.setImageDrawable(drawable);
                    break;
                case "a3":
                    learningItemBinding.a3Img.setImageDrawable(drawable);
                    break;
                case "a4":
                    learningItemBinding.a4Img.setImageDrawable(drawable);
                    break;
                default:
                    break;
            }
        }

        private void setImgRightPair(String option) {
            switch (option) {
                case "b1":
                    learningItemBinding.bMtpChck1.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right_mtp));
                    learningItemBinding.bMtpChck1.setVisibility(View.VISIBLE);
                    break;
                case "b2":
                    learningItemBinding.bMtpChck2.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right_mtp));
                    learningItemBinding.bMtpChck2.setVisibility(View.VISIBLE);
                    break;
                case "b3":
                    learningItemBinding.bMtpChck3.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right_mtp));
                    learningItemBinding.bMtpChck3.setVisibility(View.VISIBLE);
                    break;
                case "b4":
                    learningItemBinding.bMtpChck4.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right_mtp));
                    learningItemBinding.bMtpChck4.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }


        private void setImgWrongPair(String option) {
            switch (option) {
                case "b1":
                    learningItemBinding.bMtpChck1.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                    learningItemBinding.bMtpChck1.setVisibility(View.VISIBLE);
                    break;
                case "b2":
                    learningItemBinding.bMtpChck2.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                    learningItemBinding.bMtpChck2.setVisibility(View.VISIBLE);
                    break;
                case "b3":
                    learningItemBinding.bMtpChck3.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                    learningItemBinding.bMtpChck3.setVisibility(View.VISIBLE);
                    break;
                case "b4":
                    learningItemBinding.bMtpChck4.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                    learningItemBinding.bMtpChck4.setVisibility(View.VISIBLE);
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


        private void checkImgPairAvailability(String option_1, String option_b) {
            Iterator myVeryOwnIterator = imgpaired.keySet().iterator();
            while (myVeryOwnIterator.hasNext()) {
                String key = (String) myVeryOwnIterator.next();
                String value = (String) imgpaired.get(key);
                if (key.equalsIgnoreCase(option_1)) {
                    setMtpImgtBg(option_1);
                    setMtpImgtBg(value);
                    imgpaired.remove(key);
                    break;
                }
            }

            Iterator myVeryOwnIterator1 = imgpaired.keySet().iterator();
            while (myVeryOwnIterator1.hasNext()) {
                String key = (String) myVeryOwnIterator1.next();
                String value = (String) imgpaired.get(key);
                if (value.equalsIgnoreCase(option_b)) {
                    setMtpImgtBg(option_b);
                    setMtpImgtBg(key);
                    imgpaired.remove(key);
                    break;
                }
            }
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

        private void setMtpImgtBg(String option) {
            switch (option) {
                case "a1":
                    learningItemBinding.aMtp1.setAlpha(255);
                    learningItemBinding.aMtpChck1.setVisibility(View.GONE);
                    break;
                case "a2":
                    learningItemBinding.aMtp2.setAlpha(255);
                    learningItemBinding.aMtpChck2.setVisibility(View.GONE);
                    break;
                case "a3":
                    learningItemBinding.aMtp3.setAlpha(255);
                    learningItemBinding.aMtpChck3.setVisibility(View.GONE);
                    break;
                case "a4":
                    learningItemBinding.aMtp4.setAlpha(255);
                    learningItemBinding.aMtpChck4.setVisibility(View.GONE);
                    break;
                case "b1":
                    isB1Selected = false;
                    learningItemBinding.bMtp1.setAlpha(255);
                    learningItemBinding.bMtpChck1.setVisibility(View.GONE);
                    break;
                case "b2":
                    isB2Selected = false;
                    learningItemBinding.bMtp2.setAlpha(255);
                    learningItemBinding.bMtpChck2.setVisibility(View.GONE);
                    break;
                case "b3":
                    isB3Selected = false;
                    learningItemBinding.bMtp3.setAlpha(255);
                    learningItemBinding.bMtpChck3.setVisibility(View.GONE);
                    break;
                case "b4":
                    isB4Selected = false;
                    learningItemBinding.bMtp4.setAlpha(255);
                    learningItemBinding.bMtpChck4.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }

        private void setLayoutBg(String option) {
            switch (option) {
                case "a1":
                    learningItemBinding.a1Img.setBackground(activity.getResources().getDrawable(R.drawable.ic_mtp_grey));
                    break;
                case "a2":
                    learningItemBinding.a2Img.setBackground(activity.getResources().getDrawable(R.drawable.ic_mtp_grey));
                    break;
                case "a3":
                    learningItemBinding.a3Img.setBackground(activity.getResources().getDrawable(R.drawable.ic_mtp_grey));
                    break;
                case "a4":
                    learningItemBinding.a4Img.setBackground(activity.getResources().getDrawable(R.drawable.ic_mtp_grey));
                    break;
                case "b1":
                    isB1Selected = false;
                    learningItemBinding.b1Img.setBackground(activity.getResources().getDrawable(R.drawable.ic_mtp_grey));
                    break;
                case "b2":
                    isB2Selected = false;
                    learningItemBinding.b2Img.setBackground(activity.getResources().getDrawable(R.drawable.ic_mtp_grey));
                    break;
                case "b3":
                    isB3Selected = false;
                    learningItemBinding.b3Img.setBackground(activity.getResources().getDrawable(R.drawable.ic_mtp_grey));
                    break;
                case "b4":
                    isB4Selected = false;
                    learningItemBinding.b4Img.setBackground(activity.getResources().getDrawable(R.drawable.ic_mtp_grey));
                    break;
                default:
                    break;
            }
        }

        private String getNameFromId(int id) {
            switch (id) {
                case R.id.a1:
                case R.id.a_mtp1:
                    return "a1";
                case R.id.a2:
                case R.id.a_mtp2:
                    return "a2";
                case R.id.a3:
                case R.id.a_mtp3:
                    return "a3";
                case R.id.a4:
                case R.id.a_mtp4:
                    return "a4";
                case R.id.b1:
                case R.id.b_mtp1:
                    return "b1";
                case R.id.b2:
                case R.id.b_mtp2:
                    return "b2";
                case R.id.b3:
                case R.id.b_mtp3:
                    return "b3";
                case R.id.b4:
                case R.id.b_mtp4:
                    return "b4";
                default:
                    return "";
            }
        }

        public void reset() {
            paired.clear();
            learningItemBinding.img1.setVisibility(View.GONE);
            learningItemBinding.img2.setVisibility(View.GONE);
            learningItemBinding.img3.setVisibility(View.GONE);
            learningItemBinding.img4.setVisibility(View.GONE);

            learningItemBinding.a1Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_mtp_grey));
            learningItemBinding.a2Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_mtp_grey));
            learningItemBinding.a3Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_mtp_grey));
            learningItemBinding.a4Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_mtp_grey));
            learningItemBinding.b1Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_mtp_grey));
            learningItemBinding.b2Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_mtp_grey));
            learningItemBinding.b3Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_mtp_grey));
            learningItemBinding.b4Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_mtp_grey));

        }

        private void ProcessQuestionAPI(String que_id, int flag, String call_from, String rating, String feedback, int position, String answer) {
            ProgressDialog.getInstance().show(activity);
            ApiInterface apiService = ApiClient.getInstance().getApi();
            Call<ProcessQuestion> call;
            int user_id = new PreferenceManager(getApplicationContext()).getInt(Constant.USER_ID);

            if (call_from.equalsIgnoreCase("like"))
                call = apiService.likeApi(user_id, que_id, "I", flag);
            else if (call_from.equalsIgnoreCase("fav"))
                call = apiService.favApi(user_id, que_id, "I", flag);
            else if (call_from.equalsIgnoreCase("submit"))
                call = apiService.questionAttemptApi(user_id, que_id, "I", 1, flag);
            else if (call_from.equalsIgnoreCase("rating"))
                call = apiService.addRatingsApi(user_id, que_id, "I", rating, feedback);
            else
                call = apiService.submitSubjectiveQueApi(user_id, que_id, "I",answer);

            call.enqueue(new Callback<ProcessQuestion>() {
                @Override
                public void onResponse(Call<ProcessQuestion> call, retrofit2.Response<ProcessQuestion> response) {
                    try {
                        if (response.body() != null && response.body().getResponse().equalsIgnoreCase("200")) {
                            LearningQuestionsNew learningQuestionsNew = learningQuestionsList.get(position);
                            learningQuestionsNew.setRating(response.body().getRatings() != null ? UtilHelper.roundAvoid(response.body().getRatings()) : "0");
                            learningQuestionsNew.setLikes(response.body().getLikeCount());
                            learningQuestionsNew.setComments(response.body().getQ_comments());
                            learningQuestionsNew.setShares(response.body().getQ_shares());
                            learningQuestionsNew.setAttended_by(response.body().getAttmpted_count() != null ? response.body().getAttmpted_count() : "0");

                            learningItemBinding.commentValue.setText(response.body().getQ_comments());
                            learningItemBinding.shareValue.setText(response.body().getQ_shares());
                            learningItemBinding.attemptedValue.setText(response.body().getAttmpted_count() != null ? response.body().getAttmpted_count() : "0");
                            learningItemBinding.likeValue.setText(response.body().getLikeCount());
                            learningItemBinding.ratingvalue.setText(response.body().getRatings() != null ? UtilHelper.roundAvoid(response.body().getRatings()) : "0");

                            if (call_from.equalsIgnoreCase("like")) {
                                if (flag==0) {
                                    Glide.with(activity).load(activity.getResources().getDrawable(R.drawable.ic_like)).into(learningItemBinding.like);
                                    learningQuestionsNew.setIs_liked("false");
                                } else {
                                    learningQuestionsNew.setIs_liked("true");
                                    Glide.with(activity).load(activity.getResources().getDrawable(R.drawable.ic_thumb_up_black_24dp)).into(learningItemBinding.like);
                                }
                            } else if (call_from.equalsIgnoreCase("fav")) {
                                if (flag==0) {
                                    learningQuestionsNew.setIs_fav("false");
                                    Glide.with(activity).load(activity.getResources().getDrawable(R.drawable.ic_fav)).into(learningItemBinding.favorite);
                                } else {
                                    Glide.with(activity).load(activity.getResources().getDrawable(R.drawable.ic_favorite_black_24dp)).into(learningItemBinding.favorite);
                                    learningQuestionsNew.setIs_fav("true");
                                }
                            } else if (call_from.equalsIgnoreCase(ONE_LINE_ANSWER)) {
                                if (response.body().getSolved_right().equalsIgnoreCase("true")) {
                                    learningItemBinding.singleLine.setBackground(activity.getResources().getDrawable(R.drawable.green_border));
                                } else {
                                    learningItemBinding.singleLine.setBackground(activity.getResources().getDrawable(R.drawable.red_border));
                                }
                                learningItemBinding.solutionLayout.setVisibility(View.VISIBLE);
                                learningItemBinding.solutionDesc.setText(response.body().getA_sub_ans());
                            } else if  (call_from.equalsIgnoreCase(LONG_ANSWER)) {
                                if (response.body().getSolved_right().equalsIgnoreCase("true")) {
                                    learningItemBinding.multiLine.setBackground(activity.getResources().getDrawable(R.drawable.green_border));
                                } else {
                                    learningItemBinding.multiLine.setBackground(activity.getResources().getDrawable(R.drawable.red_border));
                                }
                                learningItemBinding.solutionLayout.setVisibility(View.VISIBLE);
                                learningItemBinding.solutionDesc.setText(response.body().getA_sub_ans());
                            }

                            learningQuestionsList.set(position, learningQuestionsNew);


                        } else {
                            Toast.makeText(activity, UtilHelper.getAPIError(String.valueOf(response.body())), Toast.LENGTH_SHORT).show();
                        }
                        ProgressDialog.getInstance().dismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                        ProgressDialog.getInstance().dismiss();
                    }
                }

                @Override
                public void onFailure(Call<ProcessQuestion> call, Throwable t) {
                    t.printStackTrace();
                    ProgressDialog.getInstance().dismiss();
                }
            });
        }


        private void displayRatingDialog(String questionid, int position) {
            try {
                Dialog dialog = new Dialog(activity);
                RatingFeedbackBinding ratingFeedbackBinding = DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.rating_feedback, null, false);
                dialog.setContentView(ratingFeedbackBinding.getRoot());
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
                dialog.show();

                ratingFeedbackBinding.submitRating.setOnClickListener(v -> {
                    dialog.dismiss();
                    if (ratingFeedbackBinding.rating.getRating() != 0) {
                        ProcessQuestionAPI(questionid, 0, "rating", String.valueOf(ratingFeedbackBinding.rating.getRating()), ratingFeedbackBinding.feedback.getText().toString(), position,"");
                    } else {
                        Toast.makeText(activity, "Please add ratings", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
