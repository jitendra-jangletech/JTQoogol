package com.jangletech.qoogol.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
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
import com.jangletech.qoogol.database.repo.AppRepository;
import com.jangletech.qoogol.databinding.LearningFillintheblanksBinding;
import com.jangletech.qoogol.databinding.LearningItemBinding;
import com.jangletech.qoogol.databinding.LearningLongansBinding;
import com.jangletech.qoogol.databinding.LearningMatchpairBinding;
import com.jangletech.qoogol.databinding.LearningMatchpairimgBinding;
import com.jangletech.qoogol.databinding.LearningMcqBinding;
import com.jangletech.qoogol.databinding.LearningMcqimgBinding;
import com.jangletech.qoogol.databinding.LearningMcqimgtextBinding;
import com.jangletech.qoogol.databinding.LearningOnelineansBinding;
import com.jangletech.qoogol.databinding.LearningScqBinding;
import com.jangletech.qoogol.databinding.LearningScqimgBinding;
import com.jangletech.qoogol.databinding.LearningScqimgtextBinding;
import com.jangletech.qoogol.databinding.LearningTruefalseBinding;
import com.jangletech.qoogol.databinding.RatingFeedbackBinding;
import com.jangletech.qoogol.dialog.LikeListingDialog;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.dialog.ShareUserListingDialog;
import com.jangletech.qoogol.model.LearningQuestions;
import com.jangletech.qoogol.model.LearningQuestionsNew;
import com.jangletech.qoogol.model.ProcessQuestion;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.ui.doubts.DoubtListingDialog;
import com.jangletech.qoogol.ui.learning.SlideshowDialogFragment;
import com.jangletech.qoogol.util.AppUtils;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;
import com.jangletech.qoogol.util.UtilHelper;
import com.jangletech.qoogol.view.ExpandableTextView;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.github.kexanie.library.MathView;
import retrofit2.Call;
import retrofit2.Callback;

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
import static com.jangletech.qoogol.util.Constant.que_doubts;
import static com.jangletech.qoogol.util.Constant.sharedby;
import static com.jangletech.qoogol.util.Constant.sharedto;

/**
 * Created by Pritali on 3/18/2020.
 */
public class LearningAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements LikeAdapter.onItemClickListener {
    private static final String TAG = "LearningAdapter";
    private List<LearningQuestionsNew> learningQuestionsList;
    private Activity activity;
    private LearningItemBinding learningItemBinding;
    private LearningFillintheblanksBinding fillintheblanksBinding;
    private LearningLongansBinding longansBinding;
    private LearningOnelineansBinding onelineansBinding;
    private LearningTruefalseBinding truefalseBinding;
    private LearningScqBinding scqBinding;
    private LearningScqimgBinding scqimgBinding;
    private LearningScqimgtextBinding scqimgtextBinding;
    private LearningMcqBinding mcqBinding;
    private LearningMcqimgBinding mcqimgBinding;
    private LearningMcqimgtextBinding mcqimgtextBinding;
    private LearningMatchpairBinding matchpairBinding;
    private LearningMatchpairimgBinding matchpairimgBinding;
    private onIconClick onIconClick;
    private int call_from;
    private MaterialCardView.LayoutParams params;
    HashMap<String, String> MTP_ans = new HashMap<String, String>();

    public LearningAdapter(Activity activity, List<LearningQuestionsNew> learningQuestionsList, onIconClick onIconClick, int call_from) {
        this.activity = activity;
        this.learningQuestionsList = learningQuestionsList;
        this.onIconClick = onIconClick;
        this.call_from = call_from;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == Integer.parseInt(FILL_THE_BLANKS)) {
            fillintheblanksBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()),
                    R.layout.learning_fillintheblanks, parent, false);
            fillintheblanksBinding.container.setLayoutParams(getParams());
            return new FilltheBlanksHolder(fillintheblanksBinding);

        }
        if (viewType == Integer.parseInt(LONG_ANSWER)) {
            longansBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()),
                    R.layout.learning_longans, parent, false);
            longansBinding.container.setLayoutParams(getParams());
            return new LongAnsHolder(longansBinding);

        }
        if (viewType == Integer.parseInt(ONE_LINE_ANSWER)) {
            onelineansBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()),
                    R.layout.learning_onelineans, parent, false);
            onelineansBinding.container.setLayoutParams(getParams());
            return new OneLineAnsHolder(onelineansBinding);

        }
        if (viewType == Integer.parseInt(TRUE_FALSE)) {
            truefalseBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()),
                    R.layout.learning_truefalse, parent, false);
            truefalseBinding.container.setLayoutParams(getParams());
            return new TrueFalseHolder(truefalseBinding);

        }
        if (viewType == Integer.parseInt(SCQ)) {
            scqBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()),
                    R.layout.learning_scq, parent, false);
            scqBinding.container.setLayoutParams(getParams());
            return new SCQHolder(scqBinding);

        }
        if (viewType == Integer.parseInt(SCQ_IMAGE)) {
            scqimgBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()),
                    R.layout.learning_scqimg, parent, false);
            scqimgBinding.container.setLayoutParams(getParams());
            return new SCQImgHolder(scqimgBinding);

        }
        if (viewType == Integer.parseInt(SCQ_IMAGE_WITH_TEXT)) {
            scqimgtextBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()),
                    R.layout.learning_scqimgtext, parent, false);
            scqimgtextBinding.container.setLayoutParams(getParams());
            return new SCQImgTextHolder(scqimgtextBinding);

        }
        if (viewType == Integer.parseInt(MCQ)) {
            mcqBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()),
                    R.layout.learning_mcq, parent, false);
            mcqBinding.container.setLayoutParams(getParams());
            return new MCQHolder(mcqBinding);

        }
        if (viewType == Integer.parseInt(MCQ_IMAGE)) {
            mcqimgBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()),
                    R.layout.learning_mcqimg, parent, false);
            mcqimgBinding.container.setLayoutParams(getParams());
            return new MCQImgHolder(mcqimgBinding);

        }
        if (viewType == Integer.parseInt(MCQ_IMAGE_WITH_TEXT)) {
            mcqimgtextBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()),
                    R.layout.learning_mcqimgtext, parent, false);
            mcqimgtextBinding.container.setLayoutParams(getParams());
            return new MCQImgTextHolder(mcqimgtextBinding);

        }
        if (viewType == Integer.parseInt(MATCH_PAIR)) {
            matchpairBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()),
                    R.layout.learning_matchpair, parent, false);
            matchpairBinding.container.setLayoutParams(getParams());
            return new MatchPairHolder(matchpairBinding);

        }
        if (viewType == Integer.parseInt(MATCH_PAIR_IMAGE)) {
            matchpairimgBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()),
                    R.layout.learning_matchpairimg, parent, false);
            matchpairimgBinding.container.setLayoutParams(getParams());
            return new MatchPairImgHolder(matchpairimgBinding);

        } else {
            learningItemBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(parent.getContext()),
                    R.layout.learning_item, parent, false);
            learningItemBinding.container.setLayoutParams(getParams());

            return new ViewHolder(learningItemBinding);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        try {
            LearningQuestionsNew learningQuestions = learningQuestionsList.get(position);
            if (getItemViewType(position) == Integer.parseInt(FILL_THE_BLANKS)) {
                FilltheBlanksHolder filltheBlanksHolder = (FilltheBlanksHolder)holder;
                filltheBlanksHolder.mBinding.setQuestion(learningQuestions);
                filltheBlanksHolder.mBinding.categoryTextview.setText("Fill the Blanks");
                setData(learningQuestions, filltheBlanksHolder.mBinding.questionMathview, filltheBlanksHolder.mBinding.questionTextview,
                        filltheBlanksHolder.mBinding.questiondescTextview, filltheBlanksHolder.mBinding.questiondescTextviewMath,
                        filltheBlanksHolder.mBinding.saveQue, filltheBlanksHolder.mBinding.like, filltheBlanksHolder.mBinding.favorite,
                        filltheBlanksHolder.mBinding.queImg1, filltheBlanksHolder.mBinding.imgRecycler);
            } else if (getItemViewType(position) == Integer.parseInt(LONG_ANSWER)) {
                LongAnsHolder longAnsHolder = (LongAnsHolder)holder;
                longAnsHolder.mBinding.setQuestion(learningQuestions);
                longAnsHolder.mBinding.categoryTextview.setText("Long Answer");
                setData(learningQuestions, longAnsHolder.mBinding.questionMathview, longAnsHolder.mBinding.questionTextview, longAnsHolder.mBinding.questiondescTextview, longAnsHolder.mBinding.questionMathview, longAnsHolder.mBinding.saveQue, longAnsHolder.mBinding.like, longAnsHolder.mBinding.favorite, longAnsHolder.mBinding.queImg1, longAnsHolder.mBinding.imgRecycler);
            } else if (getItemViewType(position) == Integer.parseInt(ONE_LINE_ANSWER)) {
                OneLineAnsHolder oneLineAnsHolder = (OneLineAnsHolder)holder;
                oneLineAnsHolder.mBinding.setQuestion(learningQuestions);
                oneLineAnsHolder.mBinding.categoryTextview.setText("Short Answer");
                setData(learningQuestions, oneLineAnsHolder.mBinding.questionMathview, oneLineAnsHolder.mBinding.questionTextview, oneLineAnsHolder.mBinding.questiondescTextview, oneLineAnsHolder.mBinding.questionMathview, oneLineAnsHolder.mBinding.saveQue, oneLineAnsHolder.mBinding.like, oneLineAnsHolder.mBinding.favorite, oneLineAnsHolder.mBinding.queImg1, oneLineAnsHolder.mBinding.imgRecycler);
            } else if (getItemViewType(position) == Integer.parseInt(TRUE_FALSE)) {
                TrueFalseHolder trueFalseHolder = (TrueFalseHolder) holder;
                trueFalseHolder.mBinding.setQuestion(learningQuestions);
                trueFalseHolder.mBinding.categoryTextview.setText("True False");
                setData(learningQuestions, trueFalseHolder.mBinding.questionMathview, trueFalseHolder.mBinding.questionTextview, trueFalseHolder.mBinding.questiondescTextview, trueFalseHolder.mBinding.questionMathview, trueFalseHolder.mBinding.saveQue, trueFalseHolder.mBinding.like, trueFalseHolder.mBinding.favorite, trueFalseHolder.mBinding.queImg1, trueFalseHolder.mBinding.imgRecycler);
            } else if (getItemViewType(position) == Integer.parseInt(SCQ)) {
                scqBinding.setQuestion(learningQuestions);
                scqBinding.categoryTextview.setText("SCQ");
                setData(learningQuestions, scqBinding.questionMathview, scqBinding.questionTextview, scqBinding.questiondescTextview, scqBinding.questionMathview, scqBinding.saveQue, scqBinding.like, scqBinding.favorite, scqBinding.queImg1, scqBinding.imgRecycler);
            } else if (getItemViewType(position) == Integer.parseInt(SCQ_IMAGE)) {
                scqimgBinding.setQuestion(learningQuestions);
                scqimgBinding.categoryTextview.setText("SCQ");
                setData(learningQuestions, scqimgBinding.questionMathview, scqimgBinding.questionTextview, scqimgBinding.questiondescTextview, scqimgBinding.questionMathview, scqimgBinding.saveQue, scqimgBinding.like, scqimgBinding.favorite, scqimgBinding.queImg1, scqimgBinding.imgRecycler);
            } else if (getItemViewType(position) == Integer.parseInt(SCQ_IMAGE_WITH_TEXT)) {
                scqimgtextBinding.setQuestion(learningQuestions);
                scqimgtextBinding.categoryTextview.setText("SCQ");
                setData(learningQuestions, scqimgtextBinding.questionMathview, scqimgtextBinding.questionTextview, scqimgtextBinding.questiondescTextview, scqimgtextBinding.questionMathview, scqimgtextBinding.saveQue, scqimgtextBinding.like, scqimgtextBinding.favorite, scqimgtextBinding.queImg1, scqimgtextBinding.imgRecycler);
            } else if (getItemViewType(position) == Integer.parseInt(MCQ)) {
                mcqBinding.setQuestion(learningQuestions);
                mcqBinding.categoryTextview.setText("MCQ");
                setData(learningQuestions, mcqBinding.questionMathview, mcqBinding.questionTextview, mcqBinding.questiondescTextview, mcqBinding.questionMathview, mcqBinding.saveQue, mcqBinding.like, mcqBinding.favorite, mcqBinding.queImg1, mcqBinding.imgRecycler);
            } else if (getItemViewType(position) == Integer.parseInt(MCQ_IMAGE)) {
                mcqimgBinding.setQuestion(learningQuestions);
                mcqimgBinding.categoryTextview.setText("MCQ");
                setData(learningQuestions, mcqimgBinding.questionMathview, mcqimgBinding.questionTextview, mcqimgBinding.questiondescTextview, mcqimgBinding.questionMathview, mcqimgBinding.saveQue, mcqimgBinding.like, mcqimgBinding.favorite, mcqimgBinding.queImg1, mcqimgBinding.imgRecycler);
            } else if (getItemViewType(position) == Integer.parseInt(MCQ_IMAGE_WITH_TEXT)) {
                mcqimgtextBinding.setQuestion(learningQuestions);
                mcqimgtextBinding.categoryTextview.setText("MCQ");
                setData(learningQuestions, mcqimgtextBinding.questionMathview, mcqimgtextBinding.questionTextview, mcqimgtextBinding.questiondescTextview, mcqimgtextBinding.questionMathview, mcqimgtextBinding.saveQue, mcqimgtextBinding.like, mcqimgtextBinding.favorite, mcqimgtextBinding.queImg1, mcqimgtextBinding.imgRecycler);
            } else if (getItemViewType(position) == Integer.parseInt(MATCH_PAIR)) {
                matchpairBinding.setQuestion(learningQuestions);
                matchpairBinding.categoryTextview.setText("Match the Pair");
                setData(learningQuestions, matchpairBinding.questionMathview, matchpairBinding.questionTextview, matchpairBinding.questiondescTextview, matchpairBinding.questionMathview, matchpairBinding.saveQue, matchpairBinding.like, matchpairBinding.favorite, matchpairBinding.queImg1, matchpairBinding.imgRecycler);
            } else if (getItemViewType(position) == Integer.parseInt(MATCH_PAIR_IMAGE)) {
                matchpairimgBinding.setQuestion(learningQuestions);
                matchpairimgBinding.categoryTextview.setText("Match the Pair");
                setData(learningQuestions, matchpairimgBinding.questionMathview, matchpairimgBinding.questionTextview, matchpairimgBinding.questiondescTextview, matchpairimgBinding.questionMathview, matchpairimgBinding.saveQue, matchpairimgBinding.like, matchpairimgBinding.favorite, matchpairimgBinding.queImg1, matchpairimgBinding.imgRecycler);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setData(LearningQuestionsNew learningQuestions, MathView mathview, TextView questionTextview,
                         ExpandableTextView questiondescTextview, MathView questionMathview,
                         ImageView saveQue, ImageView like, ImageView favorite,
                         ImageView queImg1, RecyclerView imgRecycler) {
        try {
            if (learningQuestions.getQuestion().contains("\\")) {
                mathview.setVisibility(View.VISIBLE);
                questionTextview.setVisibility(View.GONE);

            } else {
                mathview.setVisibility(View.GONE);
                questionTextview.setVisibility(View.VISIBLE);
            }

            if (learningQuestions.getQuestiondesc().contains("\\")) {
                questiondescTextview.setVisibility(View.GONE);
                questionMathview.setVisibility(View.VISIBLE);
            } else {
                questiondescTextview.setVisibility(View.VISIBLE);
                questionMathview.setVisibility(View.GONE);
            }

            saveQue.setImageDrawable(learningQuestions.getIsSave().equalsIgnoreCase("true") ? activity.getResources().getDrawable(R.drawable.ic_save_black) : activity.getResources().getDrawable(R.drawable.ic_save_grey));
            favorite.setImageDrawable(learningQuestions.getIs_fav().equalsIgnoreCase("true") ? activity.getResources().getDrawable(R.drawable.ic_favorite_black_24dp) : activity.getResources().getDrawable(R.drawable.ic_fav));
            like.setImageDrawable(learningQuestions.getIs_liked().equalsIgnoreCase("true") ? activity.getResources().getDrawable(R.drawable.ic_thumb_up_black_24dp) : activity.getResources().getDrawable(R.drawable.ic_like));

            if (learningQuestions.getQue_media_typs() != null && learningQuestions.getQue_media_typs().equalsIgnoreCase(IMAGE) && learningQuestions.getQue_images() != null) {
                String[] stringrray = learningQuestions.getQue_images().split(",");
                List<String> tempimgList = new ArrayList<>();
                tempimgList = Arrays.asList(stringrray);
                if (tempimgList != null && tempimgList.size() != 0) {
                    if (tempimgList.size() == 1) {
                        try {
                            queImg1.setVisibility(View.VISIBLE);
                            Glide.with(activity).load(new URL(tempimgList.get(0))).into(queImg1);
                            List<String> finalTempimgList = tempimgList;
                            queImg1.setOnClickListener(v -> {
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
                        queImg1.setVisibility(View.GONE);
                        imgRecycler.setVisibility(View.VISIBLE);
                        ImageAdapter imageAdapter = new ImageAdapter(activity, tempimgList);
                        imgRecycler.setHasFixedSize(true);
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false);
                        imgRecycler.setLayoutManager(linearLayoutManager);
                        imgRecycler.setAdapter(imageAdapter);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ViewGroup.LayoutParams getParams() {
        if (call_from == learning || call_from == sharedby || call_from == sharedto) {
            params = new MaterialCardView.LayoutParams(MaterialCardView.LayoutParams.MATCH_PARENT, MaterialCardView.LayoutParams.WRAP_CONTENT);
            int margin = activity.getResources().getDimensionPixelSize(R.dimen._10sdp);
            params.setMargins(0, margin, 0, margin);
            return params;
        } else {
            params = new MaterialCardView.LayoutParams(MaterialCardView.LayoutParams.MATCH_PARENT, MaterialCardView.LayoutParams.MATCH_PARENT);
            return params;
        }
    }

    public void updateList(List<LearningQuestionsNew> learningQuestionsList) {
        this.learningQuestionsList = learningQuestionsList;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return learningQuestionsList.get(position).getQuestion_id();
    }

    @Override
    public int getItemViewType(int position) {
        LearningQuestionsNew learningQuestions = learningQuestionsList.get(position);
        if (learningQuestions.getType().equalsIgnoreCase(FILL_THE_BLANKS)) {
            return Integer.parseInt(FILL_THE_BLANKS);
        } else if (learningQuestions.getType().equalsIgnoreCase(ONE_LINE_ANSWER) || learningQuestions.getType().equalsIgnoreCase(SHORT_ANSWER)) {
            return Integer.parseInt(ONE_LINE_ANSWER);
        } else if (learningQuestions.getType().equalsIgnoreCase(LONG_ANSWER)) {
            return Integer.parseInt(LONG_ANSWER);
        } else {
            if (learningQuestions.getQue_option_type().equalsIgnoreCase(SCQ)) {
                return Integer.parseInt(SCQ);
            } else if (learningQuestions.getQue_option_type().equalsIgnoreCase(SCQ_IMAGE)) {
                return Integer.parseInt(SCQ_IMAGE);
            } else if (learningQuestions.getQue_option_type().equalsIgnoreCase(SCQ_IMAGE_WITH_TEXT)) {
                return Integer.parseInt(SCQ_IMAGE_WITH_TEXT);
            } else if (learningQuestions.getQue_option_type().equalsIgnoreCase(MCQ_IMAGE_WITH_TEXT)) {
                return Integer.parseInt(MCQ_IMAGE_WITH_TEXT);
            } else if (learningQuestions.getQue_option_type().equalsIgnoreCase(MCQ_IMAGE)) {
                return Integer.parseInt(MCQ_IMAGE);
            } else if (learningQuestions.getQue_option_type().equalsIgnoreCase(MCQ)) {
                return Integer.parseInt(MCQ);
            } else if (learningQuestions.getQue_option_type().equalsIgnoreCase(TRUE_FALSE)) {
                return Integer.parseInt(TRUE_FALSE);
            } else if (learningQuestions.getQue_option_type().equalsIgnoreCase(MATCH_PAIR)) {
                return Integer.parseInt(MATCH_PAIR);
            } else if (learningQuestions.getQue_option_type().equalsIgnoreCase(MATCH_PAIR_IMAGE)) {
                return Integer.parseInt(MATCH_PAIR_IMAGE);
            } else
                return position;
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


    @Override
    public int getItemCount() {
        return learningQuestionsList.size();
    }

    @Override
    public void onItemCLick(String user_id) {
        onIconClick.onLikeClick(user_id);
    }

    public interface onIconClick {
        void onCommentClick(LearningQuestionsNew learningQuestionsNew, int pos);

        void onShareClick(LearningQuestionsNew learningQuestionsNew, int pos);

        void onSubmitClick(int questionId, int isRight);

        void onLikeClick(String userId);

        void onFavClick();
    }

    private void loadImage(String img, ImageView imageView) {
        try {
            if (!img.contains("http")) {
                String fileName = img.substring(img.lastIndexOf('/') + 1);
                File file = new File(UtilHelper.getDirectory(activity), fileName);
                Log.d(TAG, "loadImage URL : " + file);
                if (file.exists()) {
                    Glide.with(activity).load(file).into(imageView);
                } else {
                    Glide.with(activity).load(Constant.PRODUCTION_BASE_FILE_API + img.replace(".png", ".PNG").trim())
                            .placeholder(R.drawable.no_image)
                            .error(R.drawable.no_image)
                            .into(imageView);
                }
            } else {
                Glide.with(activity).load(img).into(imageView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void askDoubts(int position) {
        LearningQuestionsNew learningQuestions = learningQuestionsList.get(position);
        DoubtListingDialog doubtListingDialog = new DoubtListingDialog(activity, String.valueOf(learningQuestions.getQuestion_id()), learningQuestions.getSubject_id(), que_doubts);
        doubtListingDialog.show();
    }

    private void saveQue(int position, onValueChhangeListener onValueChhangeListener) {
        LearningQuestionsNew learningQuestionsNew = learningQuestionsList.get(position);
        if (learningQuestionsNew.getIsSave().equalsIgnoreCase("true")) {
            learningQuestionsNew.setIs_fav("false");
            ProcessQuestionAPI(learningQuestionsNew.getQuestion_id(), 0, "save", "", "", position, "", onValueChhangeListener);
        } else {
            learningQuestionsNew.setIs_fav("true");
            ProcessQuestionAPI(learningQuestionsNew.getQuestion_id(), 1, "save", "", "", position, "", onValueChhangeListener);
        }
    }

    private void likeQue(int position, onValueChhangeListener onValueChhangeListener) {
        LearningQuestionsNew learningQuestions = learningQuestionsList.get(position);
        if (learningQuestions.getIs_liked().equalsIgnoreCase("true")) {
            ProcessQuestionAPI(learningQuestions.getQuestion_id(), 0, "like", "", "", position, "", onValueChhangeListener);
        } else {
            ProcessQuestionAPI(learningQuestions.getQuestion_id(), 1, "like", "", "", position, "", onValueChhangeListener);
        }
    }

    private void likeValueClick(int position) {
        LearningQuestionsNew learningQuestions = learningQuestionsList.get(position);
        if (!learningQuestions.getLikes().equalsIgnoreCase("0")) {
            LikeListingDialog listingDialog = new LikeListingDialog(false, activity, learningQuestions.getQuestion_id(), (LikeListingDialog.onItemClickListener) this);
            listingDialog.show();
        }
    }

    private void shareValueClick(int position) {
        if (call_from == sharedby || call_from == sharedto) {
            LearningQuestionsNew learningQuestions = learningQuestionsList.get(position);
            ShareUserListingDialog userListingDialog = new ShareUserListingDialog(activity, learningQuestions.getQuestion_id(), (ShareUserListingDialog.onItemClickListener) this, call_from);
            userListingDialog.show();
        }
    }

    private void onShareClick(int position) {
        LearningQuestionsNew learningQuestions = learningQuestionsList.get(position);
        onIconClick.onShareClick(learningQuestions, position);
    }

    private void onFavClick(int position, onValueChhangeListener onValueChhangeListener) {
        LearningQuestionsNew learningQuestions = learningQuestionsList.get(position);
        if (learningQuestions.getIs_fav().equalsIgnoreCase("true")) {
            ProcessQuestionAPI(learningQuestions.getQuestion_id(), 0, "fav", "", "", position, "", onValueChhangeListener);
        } else {
            ProcessQuestionAPI(learningQuestions.getQuestion_id(), 1, "fav", "", "", position, "", onValueChhangeListener);
        }
    }

    public interface onValueChhangeListener {

        void onValueChange(String q_comments, String q_shares, String attempted, String likeCount);

        void onRatingChange(String ratings);

        void onLikeChange(int ic_like);

        void onSaveChange(int ic_save_grey);

        void onFavChange(int ic_fav);

        void onOneLineAnsChange(int green_border, String ans);

        void onLongAnsChange(int green_border, String a_sub_ans);
    }

    private void displayRatingDialog(LearningQuestionsNew learningQuestionsNew, int position, onValueChhangeListener onValueChhangeListener) {
        try {
            Dialog dialog = new Dialog(activity);
            RatingFeedbackBinding ratingFeedbackBinding = DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.rating_feedback, null, false);
            dialog.setContentView(ratingFeedbackBinding.getRoot());
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
            dialog.show();

            ratingFeedbackBinding.rating.setRating(Float.parseFloat(learningQuestionsNew.getRating()));
            String decoded = AppUtils.decodedString(learningQuestionsNew.getFeedback());
            ratingFeedbackBinding.feedback.setText(decoded);
            ratingFeedbackBinding.submitRating.setOnClickListener(v -> {
                dialog.dismiss();
                if (ratingFeedbackBinding.rating.getRating() != 0) {
                    String encoded = Base64.encodeToString(ratingFeedbackBinding.feedback.getText().toString().getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
                    String encodedText = StringUtils.stripAccents(encoded);
                    ProcessQuestionAPI(learningQuestionsNew.getQuestion_id(), 0, "rating", String.valueOf(ratingFeedbackBinding.rating.getRating()), encodedText, position, "", onValueChhangeListener);
                } else {
                    Toast.makeText(activity, "Please add ratings", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void ProcessQuestionAPI(int que_id, int flag, String call_from, String rating, String feedback, int position, String answer, LearningAdapter.onValueChhangeListener onValueChhangeListener) {
        ProgressDialog.getInstance().show(activity);
        ApiInterface apiService = ApiClient.getInstance().getApi();
        Call<ProcessQuestion> call;
        int user_id = Integer.parseInt(new PreferenceManager(activity).getUserId());

        if (call_from.equalsIgnoreCase("like"))
            call = apiService.likeApi(user_id, que_id, "I", flag);
        else if (call_from.equalsIgnoreCase("fav"))
            call = apiService.favApi(user_id, que_id, "I", flag);
        else if (call_from.equalsIgnoreCase("submit"))
            call = apiService.questionAttemptApi(user_id, que_id, "I", 1, flag);
        else if (call_from.equalsIgnoreCase("rating"))
            call = apiService.addRatingsApi(user_id, que_id, "I", rating, feedback);
        else if (call_from.equalsIgnoreCase("save"))
            call = apiService.saveQueApi(user_id, que_id, "I", flag);
        else
            call = apiService.submitSubjectiveQueApi(user_id, que_id, "I", answer);

        call.enqueue(new Callback<ProcessQuestion>() {
            @Override
            public void onResponse(Call<ProcessQuestion> call, retrofit2.Response<ProcessQuestion> response) {
                try {
                    if (response.body() != null && response.body().getResponse().equalsIgnoreCase("200")) {
                        LearningQuestionsNew learningQuestionsNew = learningQuestionsList.get(position);
                        learningQuestionsNew.setLikes(response.body().getLikeCount());
                        learningQuestionsNew.setComments(response.body().getQ_comments());
                        learningQuestionsNew.setShares(response.body().getQ_shares());
                        learningQuestionsNew.setAttended_by(response.body().getAttmpted_count() != null ? response.body().getAttmpted_count() : "0");


                        onValueChhangeListener.onValueChange(response.body().getQ_comments(), response.body().getQ_shares(), response.body().getAttmpted_count() != null ? response.body().getAttmpted_count() : "0",
                                response.body().getLikeCount());


                        if (call_from.equalsIgnoreCase("rating")) {
                            learningQuestionsNew.setRating(rating);
                            learningQuestionsNew.setFeedback(feedback);
                            onValueChhangeListener.onRatingChange(response.body().getRatings() != null ? UtilHelper.roundAvoid(rating) : "0");
                        }
                        if (call_from.equalsIgnoreCase("like")) {
                            if (flag == 0) {
                                onValueChhangeListener.onLikeChange(R.drawable.ic_like);
                                learningQuestionsNew.setIs_liked("false");
                            } else {
                                learningQuestionsNew.setIs_liked("true");
                                onValueChhangeListener.onLikeChange(R.drawable.ic_thumb_up_black_24dp);
                            }
                        }
                        if (call_from.equalsIgnoreCase("save")) {
                            ExecutorService executor = Executors.newSingleThreadExecutor();
                            if (flag == 0) {
                                learningQuestionsNew.setIsSave("false");
                                onValueChhangeListener.onSaveChange(R.drawable.ic_save_grey);
                                executor.execute(() -> new AppRepository(activity).deleteQuestion(learningQuestionsNew.getQuestion_id()));

                            } else {
                                learningQuestionsNew.setIsSave("true");
                                onValueChhangeListener.onSaveChange(R.drawable.ic_save_black);
                                executor.execute(() -> new AppRepository(activity).insertQuestion(copyFields(learningQuestionsNew)));
                            }


                        } else if (call_from.equalsIgnoreCase("fav")) {
                            ExecutorService executor = Executors.newSingleThreadExecutor();
                            if (flag == 0) {
                                learningQuestionsNew.setIs_fav("false");
                                onValueChhangeListener.onFavChange(R.drawable.ic_fav);
                                executor.execute(() -> new AppRepository(activity).updateQuestion(learningQuestionsNew.getQuestion_id(), "false"));
                            } else {
                                onValueChhangeListener.onFavChange(R.drawable.ic_favorite_black_24dp);
                                learningQuestionsNew.setIs_fav("true");
                                executor.execute(() -> new AppRepository(activity).updateQuestion(learningQuestionsNew.getQuestion_id(), "true"));
                            }
                        } else if (call_from.equalsIgnoreCase(ONE_LINE_ANSWER)) {
                            if (response.body().getSolved_right().equalsIgnoreCase("true")) {
                                onValueChhangeListener.onOneLineAnsChange(R.drawable.green_border, response.body().getA_sub_ans());
                            } else {
                                onValueChhangeListener.onOneLineAnsChange(R.drawable.red_border, response.body().getA_sub_ans());
                            }

                        } else if (call_from.equalsIgnoreCase(LONG_ANSWER)) {
                            if (response.body().getSolved_right().equalsIgnoreCase("true")) {
                                onValueChhangeListener.onLongAnsChange(R.drawable.green_border,response.body().getA_sub_ans());
                            } else {
                                onValueChhangeListener.onLongAnsChange(R.drawable.red_border,response.body().getA_sub_ans());
                            }
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

    private LearningQuestions copyFields(LearningQuestionsNew learningQuestionsNew) {
        LearningQuestions learningQuestions = new LearningQuestions();
        learningQuestions.setAnswerDesc(learningQuestionsNew.getAnswerDesc());
        learningQuestions.setAnswer(learningQuestionsNew.getAnswer());
        learningQuestions.setMarks(learningQuestionsNew.getMarks());
        learningQuestions.setDifficulty_level(learningQuestionsNew.getDifficulty_level());
        learningQuestions.setMcq4(learningQuestionsNew.getMcq4());
        learningQuestions.setMcq3(learningQuestionsNew.getMcq3());
        learningQuestions.setMcq2(learningQuestionsNew.getMcq2());
        learningQuestions.setMcq1(learningQuestionsNew.getMcq1());
        learningQuestions.setRecommended_time(learningQuestionsNew.getRecommended_time());
        learningQuestions.setShares(learningQuestionsNew.getShares());
        learningQuestions.setComments(learningQuestionsNew.getComments());
        learningQuestions.setLikes(learningQuestionsNew.getLikes());
        learningQuestions.setLastused_on(learningQuestionsNew.getLastused_on());
        learningQuestions.setPosted_on(learningQuestionsNew.getPosted_on());
        learningQuestions.setTopic(learningQuestionsNew.getTopic());
        learningQuestions.setQuestion_id(learningQuestionsNew.getQuestion_id());
        learningQuestions.setQuestion(learningQuestionsNew.getQuestion());
        learningQuestions.setQuestiondesc(learningQuestionsNew.getQuestiondesc());

        learningQuestions.setCategory(learningQuestionsNew.getCategory());
        learningQuestions.setSubject(learningQuestionsNew.getSubject());
//            learningQuestions.setAns_media_names(learningQuestionsNew.getAns_media_names());
        learningQuestions.setAns_mediaId(learningQuestionsNew.getAns_mediaId());
        learningQuestions.setAttended_by(learningQuestionsNew.getAttended_by());
        learningQuestions.setFeedback(learningQuestionsNew.getFeedback());
        learningQuestions.setChapter_id(learningQuestionsNew.getChapter_id());
        learningQuestions.setIs_fav(learningQuestionsNew.getIs_fav());
        learningQuestions.setIs_liked(learningQuestionsNew.getIs_liked());
        learningQuestions.setIsSave(learningQuestionsNew.getIsSave());
        learningQuestions.setMcq5(learningQuestionsNew.getMcq5());
        learningQuestions.setMcq5(learningQuestionsNew.getMcq5());
        learningQuestions.setQue_images(learningQuestionsNew.getQue_images());
        learningQuestions.setQue_media_typs(learningQuestionsNew.getQue_media_typs());
        learningQuestions.setQue_option_type(learningQuestionsNew.getQue_option_type());
        learningQuestions.setMcq5(learningQuestionsNew.getMcq5());
        learningQuestions.setType(learningQuestionsNew.getType());
//            learningQuestions.setSolve_right(learningQuestionsNew.getSolve_right());
        learningQuestions.setVisited(learningQuestionsNew.isVisited());
        learningQuestions.setSubject_id(learningQuestionsNew.getSubject_id());
        learningQuestions.setRating(learningQuestionsNew.getRating());

        return learningQuestions;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        LearningItemBinding learningItemBinding;
        CountDownTimer countDownTimer;


        @SuppressLint("ClickableViewAccessibility")
        public ViewHolder(@NonNull LearningItemBinding itemView) {
            super(itemView.getRoot());
            this.learningItemBinding = itemView;
        }
    }


    private void setPairAnswers(LearningQuestionsNew learningQuestionsNew) {
        MTP_ans.clear();
        String options[] = {};
        options = learningQuestionsNew.getAnswer().split(",");
        for (int i = 0; i < options.length; i++) {
            if (i == 0)
                MTP_ans.put("a1", options[i].split("-", -1)[1].toLowerCase());
            if (i == 1)
                MTP_ans.put("a2", options[i].split("-", -1)[1].toLowerCase());
            if (i == 2)
                MTP_ans.put("a3", options[i].split("-", -1)[1].toLowerCase());
            if (i == 3)
                MTP_ans.put("a4", options[i].split("-", -1)[1].toLowerCase());
        }
    }


    public class MatchPairImgHolder extends RecyclerView.ViewHolder implements onValueChhangeListener {
        CountDownTimer countDownTimer;
        LearningMatchpairimgBinding mBinding;
        HashMap<String, String> imgpaired = new HashMap<String, String>();
        int isSolvedRight, isAttempted = 0;

        public MatchPairImgHolder(LearningMatchpairimgBinding mBinding) {
            super(mBinding.getRoot());
            this.mBinding = mBinding;
            mBinding.expand.setOnClickListener(v -> {
                LearningQuestionsNew learningQuestions = learningQuestionsList.get(getAdapterPosition());
                mBinding.expandableLayout.setVisibility(View.VISIBLE);
                mBinding.timerLayout.setVisibility(View.VISIBLE);
                mBinding.close.setVisibility(View.VISIBLE);
                mBinding.expand.setVisibility(View.GONE);
                mBinding.resetLabel.setVisibility(View.VISIBLE);
                mBinding.reset.setVisibility(View.VISIBLE);
                countDownTimer = setTimer(mBinding.tvtimer, 0, 0);


                setPairAnswers(learningQuestions);


                loadImage(learningQuestions.getMcq1().split("::", -1)[0], mBinding.aMtp1);
                loadImage(learningQuestions.getMcq1().split("::", -1)[1], mBinding.bMtp1);

                loadImage(learningQuestions.getMcq2().split("::", -1)[0], mBinding.aMtp2);
                loadImage(learningQuestions.getMcq2().split("::", -1)[1], mBinding.bMtp2);

                loadImage(learningQuestions.getMcq3().split("::", -1)[0], mBinding.aMtp3);
                loadImage(learningQuestions.getMcq3().split("::", -1)[1], mBinding.bMtp3);

            });

            mBinding.close.setOnClickListener(v ->
            {
                countDownTimer.cancel();
                mBinding.expandableLayout.setVisibility(View.GONE);
                mBinding.timerLayout.setVisibility(View.GONE);
                mBinding.expand.setVisibility(View.VISIBLE);
                mBinding.close.setVisibility(View.GONE);
                mBinding.resetLabel.setVisibility(View.GONE);
                mBinding.reset.setVisibility(View.GONE);
            });

            mBinding.askDoubt.setOnClickListener(v -> {
                askDoubts(getAdapterPosition());
            });

            mBinding.saveQue.setOnClickListener(v -> {
                saveQue(getAdapterPosition(), this);
            });


            mBinding.like.setOnClickListener(v -> {
                likeQue(getAdapterPosition(), this);
            });

            mBinding.likeValue.setOnClickListener(v -> {
                likeValueClick(getAdapterPosition());
            });

            mBinding.shareValue.setOnClickListener(v -> {
                shareValueClick(getAdapterPosition());
            });

            mBinding.share.setOnClickListener(v -> {
                onShareClick(getAdapterPosition());
            });

            mBinding.favorite.setOnClickListener(v -> {
                onFavClick(getAdapterPosition(), this);
            });


            mBinding.commentLayout.setOnClickListener(v -> onIconClick.onCommentClick(learningQuestionsList.get(
                    getAdapterPosition()), getAdapterPosition()));


            mBinding.aMtp1.setOnTouchListener(new
                    ChoiceTouchListener());

            mBinding.aMtp2.setOnTouchListener(new
                    ChoiceTouchListener());

            mBinding.aMtp3.setOnTouchListener(new
                    ChoiceTouchListener());

            mBinding.aMtp4.setOnTouchListener(new
                    ChoiceTouchListener());

            mBinding.bMtp1.setOnDragListener(new
                    ImgChoiceDragListener());

            mBinding.bMtp2.setOnDragListener(new
                    ImgChoiceDragListener());

            mBinding.bMtp3.setOnDragListener(new
                    ImgChoiceDragListener());

            mBinding.bMtp4.setOnDragListener(new
                    ImgChoiceDragListener());

            mBinding.submit.setOnClickListener(v -> {
                submitCall();
            });

            mBinding.submitAndRate.setOnClickListener(v -> {
                submitCall();
                displayRatingDialog(learningQuestionsList.get(getAdapterPosition()), getAdapterPosition(), this);
            });

        }

        private void submitCall() {
            try {
                isSolvedRight = 1;
                isAttempted = 0;
                LearningQuestionsNew learningQuestions = learningQuestionsList.get(getAdapterPosition());
                submitFunction();
                if (isAttempted == 1) {
                    onIconClick.onSubmitClick(learningQuestions.getQuestion_id(), isSolvedRight);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void submitFunction() {
            if (imgpaired.size() != MTP_ans.size()) {
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

        @Override
        public void onValueChange(String q_comments, String q_shares, String attempted, String likeCount) {
            mBinding.commentValue.setText(q_comments);
            mBinding.shareValue.setText(q_shares);
            mBinding.attemptedValue.setText(attempted);
            mBinding.likeValue.setText(likeCount);
        }

        @Override
        public void onRatingChange(String ratings) {
            mBinding.ratingvalue.setText(ratings);

        }

        @Override
        public void onLikeChange(int ic_like) {
            Glide.with(activity).load(activity.getResources().getDrawable(ic_like)).into(mBinding.like);
        }

        @Override
        public void onSaveChange(int ic_save_grey) {
            Glide.with(activity).load(activity.getResources().getDrawable(ic_save_grey)).into(mBinding.saveQue);
        }

        @Override
        public void onFavChange(int ic_fav) {
            Glide.with(activity).load(activity.getResources().getDrawable(ic_fav)).into(mBinding.favorite);
        }

        @Override
        public void onOneLineAnsChange(int green_border, String ans) {

        }

        @Override
        public void onLongAnsChange(int green_border, String a_sub_ans) {

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
                                mBinding.bMtpChck1.setVisibility(View.VISIBLE);
                                mBinding.bMtpChck1.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_mtp1));
                                setMtpMatchedPair(getNameFromId(dropped.getId()), activity.getResources().getDrawable(R.drawable.ic_mtp1));
                                if (imgpaired.size() == MTP_ans.size() - 1) {
                                    setImgMtpLastPair();
                                }
                                break;
                            case R.id.b_mtp2:
                                checkImgPairAvailability(getNameFromId(dropped.getId()), "b2");
                                imgpaired.put(getNameFromId(dropped.getId()), "b2");
                                mBinding.bMtpChck2.setVisibility(View.VISIBLE);
                                mBinding.bMtpChck2.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_mtp2));
                                setMtpMatchedPair(getNameFromId(dropped.getId()), activity.getResources().getDrawable(R.drawable.ic_mtp2));
                                if (imgpaired.size() == MTP_ans.size() - 1) {
                                    setImgMtpLastPair();
                                }
                                break;
                            case R.id.b_mtp3:
                                checkImgPairAvailability(getNameFromId(dropped.getId()), "b3");
                                imgpaired.put(getNameFromId(dropped.getId()), "b3");
                                mBinding.bMtpChck3.setVisibility(View.VISIBLE);
                                mBinding.bMtpChck3.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_mtp3));
                                setMtpMatchedPair(getNameFromId(dropped.getId()), activity.getResources().getDrawable(R.drawable.ic_mtp3));
                                if (imgpaired.size() == MTP_ans.size() - 1) {
                                    setImgMtpLastPair();
                                }
                                break;
                            case R.id.b_mtp4:
                                checkImgPairAvailability(getNameFromId(dropped.getId()), "b4");
                                imgpaired.put(getNameFromId(dropped.getId()), "b4");
                                mBinding.bMtpChck4.setVisibility(View.VISIBLE);
                                mBinding.bMtpChck4.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_mtp4));
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


        public void setMtpMatchedPair(String option, Drawable drawable) {
            switch (option) {
                case "b1":
                    mBinding.bMtpChck1.setImageDrawable(drawable);
                    mBinding.bMtpChck1.setVisibility(View.VISIBLE);
                    mBinding.bMtp1.setAlpha(150);
                    break;
                case "b2":
                    mBinding.bMtpChck2.setImageDrawable(drawable);
                    mBinding.bMtpChck2.setVisibility(View.VISIBLE);
                    mBinding.bMtp2.setAlpha(150);
                    break;
                case "b3":
                    mBinding.bMtpChck3.setImageDrawable(drawable);
                    mBinding.bMtpChck3.setVisibility(View.VISIBLE);
                    mBinding.bMtp3.setAlpha(150);
                    break;
                case "b4":
                    mBinding.bMtpChck4.setImageDrawable(drawable);
                    mBinding.bMtpChck4.setVisibility(View.VISIBLE);
                    mBinding.bMtp4.setAlpha(150);
                    break;
                case "a1":
                    mBinding.aMtpChck1.setImageDrawable(drawable);
                    mBinding.aMtpChck1.setVisibility(View.VISIBLE);
                    mBinding.aMtp1.setAlpha(150);
                    break;
                case "a2":
                    mBinding.aMtpChck2.setImageDrawable(drawable);
                    mBinding.aMtpChck2.setVisibility(View.VISIBLE);
                    mBinding.aMtp2.setAlpha(150);
                    break;
                case "a3":
                    mBinding.aMtpChck3.setImageDrawable(drawable);
                    mBinding.aMtpChck3.setVisibility(View.VISIBLE);
                    mBinding.aMtp3.setAlpha(150);
                    break;
                case "a4":
                    mBinding.aMtpChck4.setImageDrawable(drawable);
                    mBinding.aMtpChck4.setVisibility(View.VISIBLE);
                    mBinding.aMtp4.setAlpha(150);
                    break;
                default:
                    break;
            }
        }


        private void setImgRightPair(String option) {
            switch (option) {
                case "b1":
                    mBinding.bMtpChck1.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right_mtp));
                    mBinding.bMtpChck1.setVisibility(View.VISIBLE);
                    break;
                case "b2":
                    mBinding.bMtpChck2.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right_mtp));
                    mBinding.bMtpChck2.setVisibility(View.VISIBLE);
                    break;
                case "b3":
                    mBinding.bMtpChck3.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right_mtp));
                    mBinding.bMtpChck3.setVisibility(View.VISIBLE);
                    break;
                case "b4":
                    mBinding.bMtpChck4.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right_mtp));
                    mBinding.bMtpChck4.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }


        private void setImgWrongPair(String option) {
            switch (option) {
                case "b1":
                    mBinding.bMtpChck1.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                    mBinding.bMtpChck1.setVisibility(View.VISIBLE);
                    break;
                case "b2":
                    mBinding.bMtpChck2.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                    mBinding.bMtpChck2.setVisibility(View.VISIBLE);
                    break;
                case "b3":
                    mBinding.bMtpChck3.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                    mBinding.bMtpChck3.setVisibility(View.VISIBLE);
                    break;
                case "b4":
                    mBinding.bMtpChck4.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                    mBinding.bMtpChck4.setVisibility(View.VISIBLE);
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


        private void setMtpImgtBg(String option) {
            switch (option) {
                case "a1":
                    mBinding.aMtp1.setAlpha(255);
                    mBinding.aMtpChck1.setVisibility(View.GONE);
                    break;
                case "a2":
                    mBinding.aMtp2.setAlpha(255);
                    mBinding.aMtpChck2.setVisibility(View.GONE);
                    break;
                case "a3":
                    mBinding.aMtp3.setAlpha(255);
                    mBinding.aMtpChck3.setVisibility(View.GONE);
                    break;
                case "a4":
                    mBinding.aMtp4.setAlpha(255);
                    mBinding.aMtpChck4.setVisibility(View.GONE);
                    break;
                case "b1":
                    mBinding.bMtp1.setAlpha(255);
                    mBinding.bMtpChck1.setVisibility(View.GONE);
                    break;
                case "b2":
                    mBinding.bMtp2.setAlpha(255);
                    mBinding.bMtpChck2.setVisibility(View.GONE);
                    break;
                case "b3":
                    mBinding.bMtp3.setAlpha(255);
                    mBinding.bMtpChck3.setVisibility(View.GONE);
                    break;
                case "b4":
                    mBinding.bMtp4.setAlpha(255);
                    mBinding.bMtpChck4.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
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


    public class MatchPairHolder extends RecyclerView.ViewHolder implements onValueChhangeListener {
        CountDownTimer countDownTimer;
        LearningMatchpairBinding mBinding;
        HashMap<String, String> paired = new HashMap<String, String>();
        int isSolvedRight, isAttempted = 0;

        @SuppressLint("ClickableViewAccessibility")
        public MatchPairHolder(LearningMatchpairBinding mBinding) {
            super(mBinding.getRoot());
            this.mBinding = mBinding;
            mBinding.askDoubt.setOnClickListener(v -> {
                askDoubts(getAdapterPosition());
            });

            mBinding.saveQue.setOnClickListener(v -> {
                saveQue(getAdapterPosition(), this);
            });


            mBinding.like.setOnClickListener(v -> {
                likeQue(getAdapterPosition(), this);
            });

            mBinding.likeValue.setOnClickListener(v -> {
                likeValueClick(getAdapterPosition());
            });

            mBinding.shareValue.setOnClickListener(v -> {
                shareValueClick(getAdapterPosition());
            });

            mBinding.share.setOnClickListener(v -> {
                onShareClick(getAdapterPosition());
            });

            mBinding.favorite.setOnClickListener(v -> {
                onFavClick(getAdapterPosition(), this);
            });


            mBinding.commentLayout.setOnClickListener(v -> onIconClick.onCommentClick(learningQuestionsList.get(
                    getAdapterPosition()), getAdapterPosition()));


            mBinding.expand.setOnClickListener(v -> {
                LearningQuestionsNew learningQuestions = learningQuestionsList.get(getAdapterPosition());
                mBinding.expandableLayout.setVisibility(View.VISIBLE);
                mBinding.timerLayout.setVisibility(View.VISIBLE);
                mBinding.resetLabel.setVisibility(View.VISIBLE);
                mBinding.reset.setVisibility(View.VISIBLE);
                mBinding.close.setVisibility(View.VISIBLE);
                mBinding.expand.setVisibility(View.GONE);
                countDownTimer = setTimer(mBinding.tvtimer, 0, 0);
                setPairAnswers(learningQuestions);

                mBinding.a1text.setText(learningQuestions.getMcq1().split("::", -1)[0]);
                mBinding.b1text.setText(learningQuestions.getMcq1().split("::", -1)[1]);

                mBinding.a2text.setText(learningQuestions.getMcq2().split("::", -1)[0]);
                mBinding.b2text.setText(learningQuestions.getMcq2().split("::", -1)[1]);

                mBinding.a3text.setText(learningQuestions.getMcq3().split("::", -1)[0]);
                mBinding.b3text.setText(learningQuestions.getMcq3().split("::", -1)[1]);

                mBinding.a4text.setText(learningQuestions.getMcq4().split("::", -1)[0]);
                mBinding.b4text.setText(learningQuestions.getMcq4().split("::", -1)[1]);

            });

            mBinding.close.setOnClickListener(v -> {
                countDownTimer.cancel();
                mBinding.expandableLayout.setVisibility(View.GONE);
                mBinding.timerLayout.setVisibility(View.GONE);
                mBinding.expand.setVisibility(View.VISIBLE);
                mBinding.close.setVisibility(View.GONE);
                mBinding.resetLabel.setVisibility(View.GONE);
                mBinding.reset.setVisibility(View.GONE);
            });

            mBinding.reset.setOnClickListener(v -> reset());
            mBinding.resetLabel.setOnClickListener(v -> reset());
            mBinding.a1.setOnTouchListener(new
                    ChoiceTouchListener());

            mBinding.a2.setOnTouchListener(new
                    ChoiceTouchListener());

            mBinding.a3.setOnTouchListener(new
                    ChoiceTouchListener());

            mBinding.a4.setOnTouchListener(new
                    ChoiceTouchListener());


            //set drag listeners
            mBinding.b1.setOnDragListener(new
                    ChoiceDragListener());

            mBinding.b2.setOnDragListener(new
                    ChoiceDragListener());

            mBinding.b3.setOnDragListener(new
                    ChoiceDragListener());

            mBinding.b4.setOnDragListener(new
                    ChoiceDragListener());


            mBinding.b1text.setOnDragListener(new
                    ChoiceDragListener());

            mBinding.b2text.setOnDragListener(new
                    ChoiceDragListener());

            mBinding.b3text.setOnDragListener(new
                    ChoiceDragListener());

            mBinding.b4text.setOnDragListener(new
                    ChoiceDragListener());
            mBinding.submit.setOnClickListener(v -> {
                submitCall();
            });

            mBinding.submitAndRate.setOnClickListener(v -> {
                submitCall();
                displayRatingDialog(learningQuestionsList.get(getAdapterPosition()), getAdapterPosition(), this);
            });

        }

        private void submitCall() {
            try {
                isSolvedRight = 1;
                isAttempted = 0;
                LearningQuestionsNew learningQuestions = learningQuestionsList.get(getAdapterPosition());
                submitFunction();
                if (isAttempted == 1) {
                    onIconClick.onSubmitClick(learningQuestions.getQuestion_id(), isSolvedRight);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void submitFunction() {
            if (paired.size() != MTP_ans.size()) {
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
        }

        private void setRightPair(String option) {
            switch (option) {
                case "b1":
                    mBinding.img1.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right_mtp));
                    mBinding.img1.setVisibility(View.VISIBLE);
                    break;
                case "b2":
                    mBinding.img2.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right_mtp));
                    mBinding.img2.setVisibility(View.VISIBLE);
                    break;
                case "b3":
                    mBinding.img3.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right_mtp));
                    mBinding.img3.setVisibility(View.VISIBLE);
                    break;
                case "b4":
                    mBinding.img4.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right_mtp));
                    mBinding.img4.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }


        private void setWrongPair(String option) {
            switch (option) {
                case "b1":
                    mBinding.img1.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                    mBinding.img1.setVisibility(View.VISIBLE);
                    break;
                case "b2":
                    mBinding.img2.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                    mBinding.img2.setVisibility(View.VISIBLE);
                    break;
                case "b3":
                    mBinding.img3.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                    mBinding.img3.setVisibility(View.VISIBLE);
                    break;
                case "b4":
                    mBinding.img4.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                    mBinding.img4.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onValueChange(String q_comments, String q_shares, String attempted, String likeCount) {
            mBinding.commentValue.setText(q_comments);
            mBinding.shareValue.setText(q_shares);
            mBinding.attemptedValue.setText(attempted);
            mBinding.likeValue.setText(likeCount);
        }

        @Override
        public void onRatingChange(String ratings) {
            mBinding.ratingvalue.setText(ratings);

        }

        @Override
        public void onLikeChange(int ic_like) {
            Glide.with(activity).load(activity.getResources().getDrawable(ic_like)).into(mBinding.like);
        }

        @Override
        public void onSaveChange(int ic_save_grey) {
            Glide.with(activity).load(activity.getResources().getDrawable(ic_save_grey)).into(mBinding.saveQue);
        }

        @Override
        public void onFavChange(int ic_fav) {
            Glide.with(activity).load(activity.getResources().getDrawable(ic_fav)).into(mBinding.favorite);
        }

        @Override
        public void onOneLineAnsChange(int green_border, String ans) {

        }

        @Override
        public void onLongAnsChange(int green_border, String a_sub_ans) {

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
                                setmatchedPair(getNameFromId(dropped.getId()), activity.getResources().getDrawable(R.drawable.ic_mtp1));
                                mBinding.b1Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_mtp1));
                                if (paired.size() == MTP_ans.size() - 1) {
                                    setLastPair();
                                }
                                break;
                            case R.id.b2:
                            case R.id.b2text:
                                checkAvailability(getNameFromId(dropped.getId()), "b2");
                                paired.put(getNameFromId(dropped.getId()), "b2");
                                setmatchedPair(getNameFromId(dropped.getId()), activity.getResources().getDrawable(R.drawable.ic_mtp2));
                                mBinding.b2Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_mtp2));
                                if (paired.size() == MTP_ans.size() - 1) {
                                    setLastPair();
                                }
                                break;
                            case R.id.b3:
                            case R.id.b3text:
                                checkAvailability(getNameFromId(dropped.getId()), "b3");
                                paired.put(getNameFromId(dropped.getId()), "b3");
                                setmatchedPair(getNameFromId(dropped.getId()), activity.getResources().getDrawable(R.drawable.ic_mtp3));
                                mBinding.b3Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_mtp3));
                                if (paired.size() == MTP_ans.size() - 1) {
                                    setLastPair();
                                }
                                break;
                            case R.id.b4:
                            case R.id.b4text:
                                checkAvailability(getNameFromId(dropped.getId()), "b4");
                                paired.put(getNameFromId(dropped.getId()), "b4");
                                setmatchedPair(getNameFromId(dropped.getId()), activity.getResources().getDrawable(R.drawable.ic_mtp4));
                                mBinding.b4Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_mtp4));
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
                    mBinding.a1Img.setBackground(activity.getResources().getDrawable(R.drawable.ic_mtp_grey));
                    break;
                case "a2":
                    mBinding.a2Img.setBackground(activity.getResources().getDrawable(R.drawable.ic_mtp_grey));
                    break;
                case "a3":
                    mBinding.a3Img.setBackground(activity.getResources().getDrawable(R.drawable.ic_mtp_grey));
                    break;
                case "a4":
                    mBinding.a4Img.setBackground(activity.getResources().getDrawable(R.drawable.ic_mtp_grey));
                    break;
                case "b1":
                    mBinding.b1Img.setBackground(activity.getResources().getDrawable(R.drawable.ic_mtp_grey));
                    break;
                case "b2":
                    mBinding.b2Img.setBackground(activity.getResources().getDrawable(R.drawable.ic_mtp_grey));
                    break;
                case "b3":
                    mBinding.b3Img.setBackground(activity.getResources().getDrawable(R.drawable.ic_mtp_grey));
                    break;
                case "b4":
                    mBinding.b4Img.setBackground(activity.getResources().getDrawable(R.drawable.ic_mtp_grey));
                    break;
                default:
                    break;
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

        public void setmatchedPair(String option, Drawable drawable) {
            switch (option) {
                case "b1":
                    mBinding.b1Img.setImageDrawable(drawable);
                    break;
                case "b2":
                    mBinding.b2Img.setImageDrawable(drawable);
                    break;
                case "b3":
                    mBinding.b3Img.setImageDrawable(drawable);
                    break;
                case "b4":
                    mBinding.b4Img.setImageDrawable(drawable);
                    break;
                case "a1":
                    mBinding.a1Img.setImageDrawable(drawable);
                    break;
                case "a2":
                    mBinding.a2Img.setImageDrawable(drawable);
                    break;
                case "a3":
                    mBinding.a3Img.setImageDrawable(drawable);
                    break;
                case "a4":
                    mBinding.a4Img.setImageDrawable(drawable);
                    break;
                default:
                    break;
            }
        }

        public void reset() {
            paired.clear();

            mBinding.img1.setVisibility(View.GONE);
            mBinding.img2.setVisibility(View.GONE);
            mBinding.img3.setVisibility(View.GONE);
            mBinding.img4.setVisibility(View.GONE);

            mBinding.a1Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_mtp_grey));
            mBinding.a2Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_mtp_grey));
            mBinding.a3Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_mtp_grey));
            mBinding.a4Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_mtp_grey));
            mBinding.b1Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_mtp_grey));
            mBinding.b2Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_mtp_grey));
            mBinding.b3Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_mtp_grey));
            mBinding.b4Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_mtp_grey));
        }
    }

    public class MCQImgTextHolder extends RecyclerView.ViewHolder implements onValueChhangeListener {
        CountDownTimer countDownTimer;
        private String mcqimgtext_ans = "";
        private boolean isMCQImgTextSubmited = false;
        int isSolvedRight, isAttempted = 0;
        LearningMcqimgtextBinding mBinding;

        public MCQImgTextHolder(LearningMcqimgtextBinding mBinding) {
            super(mBinding.getRoot());
            this.mBinding = mBinding;

            mBinding.askDoubt.setOnClickListener(v -> {
                askDoubts(getAdapterPosition());
            });

            mBinding.saveQue.setOnClickListener(v -> {
                saveQue(getAdapterPosition(), this);
            });


            mBinding.like.setOnClickListener(v -> {
                likeQue(getAdapterPosition(), this);
            });

            mBinding.likeValue.setOnClickListener(v -> {
                likeValueClick(getAdapterPosition());
            });

            mBinding.shareValue.setOnClickListener(v -> {
                shareValueClick(getAdapterPosition());
            });

            mBinding.share.setOnClickListener(v -> {
                onShareClick(getAdapterPosition());
            });

            mBinding.favorite.setOnClickListener(v -> {
                onFavClick(getAdapterPosition(), this);
            });


            mBinding.commentLayout.setOnClickListener(v -> onIconClick.onCommentClick(learningQuestionsList.get(
                    getAdapterPosition()), getAdapterPosition()));

            mBinding.expand.setOnClickListener(v -> {
                mBinding.expandableLayout.setVisibility(View.VISIBLE);
                mBinding.timerLayout.setVisibility(View.VISIBLE);
                mBinding.close.setVisibility(View.VISIBLE);
                mBinding.expand.setVisibility(View.GONE);
                countDownTimer = setTimer(mBinding.tvtimer, 0, 0);
                try {
                    LearningQuestionsNew learningQuestions = learningQuestionsList.get(getAdapterPosition());
                    Glide.with(activity).load(new URL(Constant.QUESTION_IMAGES_API + learningQuestions.getMcq1().split(":")[0].trim())).into(mBinding.mcqImgtextImg1);
                    Glide.with(activity).load(new URL(Constant.QUESTION_IMAGES_API + learningQuestions.getMcq2().split(":")[0].trim())).into(mBinding.mcqImgtextImg2);
                    Glide.with(activity).load(new URL(Constant.QUESTION_IMAGES_API + learningQuestions.getMcq3().split(":")[0].trim())).into(mBinding.mcqImgtextImg3);
                    Glide.with(activity).load(new URL(Constant.QUESTION_IMAGES_API + learningQuestions.getMcq4().split(":")[0].trim())).into(mBinding.mcqImgtextImg4);


                    mBinding.mcqImgtextText1.setText(learningQuestions.getMcq1().split(":")[1]);
                    mBinding.mcqImgtextText2.setText(learningQuestions.getMcq2().split(":")[1]);
                    mBinding.mcqImgtextText3.setText(learningQuestions.getMcq3().split(":")[1]);
                    mBinding.mcqImgtextText4.setText(learningQuestions.getMcq4().split(":")[1]);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            mBinding.close.setOnClickListener(v ->
            {
                countDownTimer.cancel();
                mBinding.expandableLayout.setVisibility(View.GONE);
                mBinding.timerLayout.setVisibility(View.GONE);
                mBinding.expand.setVisibility(View.VISIBLE);
                mBinding.close.setVisibility(View.GONE);
            });

            mBinding.mcqImgtextImg1.setOnClickListener(v -> {
                if (isMCQImgTextSubmited)
                    setMCQImgTextAnsIndicator();
                if (!mcqimgtext_ans.contains("A")) {
                    if (mcqimgtext_ans.equalsIgnoreCase(""))
                        mcqimgtext_ans = "A";
                    else
                        mcqimgtext_ans = mcqimgtext_ans + " A";
                    mBinding.mcqImgtextImg1.setAlpha(130);
                    mBinding.mcqimgImgtextChck1.setVisibility(View.VISIBLE);
                    mBinding.mcqimgImgtextChck1.setImageDrawable(activity.getResources().getDrawable(R.drawable.selectmark));
                } else {
                    mcqimgtext_ans = mcqimgtext_ans.replace("A", "");
                    mBinding.mcqImgtextImg1.setAlpha(255);
                    mBinding.mcqimgImgtextChck1.setVisibility(View.GONE);
                }
            });

            mBinding.mcqImgtextImg2.setOnClickListener(v ->
            {
                if (isMCQImgTextSubmited)
                    setMCQImgTextAnsIndicator();
                if (!mcqimgtext_ans.contains("B")) {
                    if (mcqimgtext_ans.equalsIgnoreCase(""))
                        mcqimgtext_ans = "B";
                    else
                        mcqimgtext_ans = mcqimgtext_ans + " B";
                    mBinding.mcqImgtextImg2.setAlpha(130);
                    mBinding.mcqimgImgtextChck2.setVisibility(View.VISIBLE);
                    mBinding.mcqimgImgtextChck2.setImageDrawable(activity.getResources().getDrawable(R.drawable.selectmark));
                } else {
                    mcqimgtext_ans = mcqimgtext_ans.replace("B", "");
                    mBinding.mcqImgtextImg2.setAlpha(255);
                    mBinding.mcqimgImgtextChck2.setVisibility(View.GONE);
                }
            });

            mBinding.mcqImgtextImg3.setOnClickListener(v ->
            {
                if (isMCQImgTextSubmited)
                    setMCQImgTextAnsIndicator();
                if (!mcqimgtext_ans.contains("C")) {
                    if (mcqimgtext_ans.equalsIgnoreCase(""))
                        mcqimgtext_ans = "C";
                    else
                        mcqimgtext_ans = mcqimgtext_ans + " C";
                    mBinding.mcqImgtextImg3.setAlpha(130);
                    mBinding.mcqimgImgtextChck3.setVisibility(View.VISIBLE);
                    mBinding.mcqimgImgtextChck3.setImageDrawable(activity.getResources().getDrawable(R.drawable.selectmark));
                } else {
                    mcqimgtext_ans = mcqimgtext_ans.replace("C", "");
                    mBinding.mcqImgtextImg3.setAlpha(255);
                    mBinding.mcqimgImgtextChck3.setVisibility(View.GONE);
                }
            });

            mBinding.mcqImgtextImg4.setOnClickListener(v ->
            {
                if (isMCQImgTextSubmited)
                    setMCQImgTextAnsIndicator();
                if (!mcqimgtext_ans.contains("D")) {
                    if (mcqimgtext_ans.equalsIgnoreCase(""))
                        mcqimgtext_ans = "D";
                    else
                        mcqimgtext_ans = mcqimgtext_ans + " D";
                    mBinding.mcqImgtextImg4.setAlpha(130);
                    mBinding.mcqimgImgtextChck4.setVisibility(View.VISIBLE);
                    mBinding.mcqimgImgtextChck4.setImageDrawable(activity.getResources().getDrawable(R.drawable.selectmark));
                } else {
                    mcqimgtext_ans = mcqimgtext_ans.replace("D", "");
                    mBinding.mcqImgtextImg4.setAlpha(255);
                    mBinding.mcqimgImgtextChck4.setVisibility(View.GONE);
                }
            });

            mBinding.submit.setOnClickListener(v -> {
                submitCall();
            });

            mBinding.submitAndRate.setOnClickListener(v -> {
                submitCall();
                displayRatingDialog(learningQuestionsList.get(getAdapterPosition()), getAdapterPosition(), this);
            });

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
                mBinding.solutionLayout.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(activity, "Please select atleast one option.", Toast.LENGTH_SHORT).show();
            }
        }

        public void setRightMCQImgText(String option) {
            switch (option) {
                case "A":
                    mBinding.mcqimgImgtextChck1.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right_mtp));
                    mBinding.mcqimgImgtextChck1.setVisibility(View.VISIBLE);
                    break;
                case "B":
                    mBinding.mcqimgImgtextChck2.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right_mtp));
                    mBinding.mcqimgImgtextChck2.setVisibility(View.VISIBLE);
                    break;
                case "C":
                    mBinding.mcqimgImgtextChck3.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right_mtp));
                    mBinding.mcqimgImgtextChck3.setVisibility(View.VISIBLE);
                    break;
                case "D":
                    mBinding.mcqimgImgtextChck4.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right_mtp));
                    mBinding.mcqimgImgtextChck4.setVisibility(View.VISIBLE);
                    break;
            }
        }

        public void setWrongMCQImgText(String option) {
            switch (option) {
                case "A":
                    mBinding.mcqimgImgtextChck1.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                    mBinding.mcqimgImgtextChck1.setVisibility(View.VISIBLE);
                    break;
                case "B":
                    mBinding.mcqimgImgtextChck2.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                    mBinding.mcqimgImgtextChck2.setVisibility(View.VISIBLE);
                    break;
                case "C":
                    mBinding.mcqimgImgtextChck3.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                    mBinding.mcqimgImgtextChck3.setVisibility(View.VISIBLE);
                    break;
                case "D":
                    mBinding.mcqimgImgtextChck4.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                    mBinding.mcqimgImgtextChck4.setVisibility(View.VISIBLE);
                    break;
            }
        }

        private void setMCQImgTextAnsIndicator() {
            isMCQImgTextSubmited = false;
            mcqimgtext_ans = "";
            mBinding.mcqimgImgtextChck1.setVisibility(View.GONE);
            mBinding.mcqimgImgtextChck2.setVisibility(View.GONE);
            mBinding.mcqimgImgtextChck3.setVisibility(View.GONE);
            mBinding.mcqimgImgtextChck4.setVisibility(View.GONE);
            mBinding.mcqImgtextImg1.setAlpha(250);
            mBinding.mcqImgtextImg2.setAlpha(250);
            mBinding.mcqImgtextImg3.setAlpha(250);
            mBinding.mcqImgtextImg4.setAlpha(250);
        }


        @Override
        public void onValueChange(String q_comments, String q_shares, String attempted, String likeCount) {
            mBinding.commentValue.setText(q_comments);
            mBinding.shareValue.setText(q_shares);
            mBinding.attemptedValue.setText(attempted);
            mBinding.likeValue.setText(likeCount);
        }

        @Override
        public void onRatingChange(String ratings) {
            mBinding.ratingvalue.setText(ratings);

        }

        @Override
        public void onLikeChange(int ic_like) {
            Glide.with(activity).load(activity.getResources().getDrawable(ic_like)).into(mBinding.like);
        }

        @Override
        public void onSaveChange(int ic_save_grey) {
            Glide.with(activity).load(activity.getResources().getDrawable(ic_save_grey)).into(mBinding.saveQue);
        }

        @Override
        public void onFavChange(int ic_fav) {
            Glide.with(activity).load(activity.getResources().getDrawable(ic_fav)).into(mBinding.favorite);
        }

        @Override
        public void onOneLineAnsChange(int green_border, String ans) {

        }

        @Override
        public void onLongAnsChange(int green_border, String a_sub_ans) {

        }
    }


    public class MCQImgHolder extends RecyclerView.ViewHolder implements onValueChhangeListener {
        CountDownTimer countDownTimer;
        private boolean isMCQImgSubmited = false;
        private String mcqimg_ans = "";
        int isSolvedRight, isAttempted = 0;
        LearningMcqimgBinding mBinding;

        public MCQImgHolder(LearningMcqimgBinding mBinding) {
            super(mBinding.getRoot());

            this.mBinding = mBinding;

            mBinding.askDoubt.setOnClickListener(v -> {
                askDoubts(getAdapterPosition());
            });

            mBinding.saveQue.setOnClickListener(v -> {
                saveQue(getAdapterPosition(), this);
            });


            mBinding.like.setOnClickListener(v -> {
                likeQue(getAdapterPosition(), this);
            });

            mBinding.likeValue.setOnClickListener(v -> {
                likeValueClick(getAdapterPosition());
            });

            mBinding.shareValue.setOnClickListener(v -> {
                shareValueClick(getAdapterPosition());
            });

            mBinding.share.setOnClickListener(v -> {
                onShareClick(getAdapterPosition());
            });

            mBinding.favorite.setOnClickListener(v -> {
                onFavClick(getAdapterPosition(), this);
            });


            mBinding.commentLayout.setOnClickListener(v -> onIconClick.onCommentClick(learningQuestionsList.get(
                    getAdapterPosition()), getAdapterPosition()));

            mBinding.expand.setOnClickListener(v -> {
                mBinding.expandableLayout.setVisibility(View.VISIBLE);
                mBinding.timerLayout.setVisibility(View.VISIBLE);
                mBinding.close.setVisibility(View.VISIBLE);
                mBinding.expand.setVisibility(View.GONE);
                countDownTimer = setTimer(mBinding.tvtimer, 0, 0);
                try {
                    LearningQuestionsNew learningQuestions = learningQuestionsList.get(getAdapterPosition());
                    Glide.with(activity).load(new URL(learningQuestions.getMcq1())).into(mBinding.mcqImg1);
                    Glide.with(activity).load(new URL(learningQuestions.getMcq2())).into(mBinding.mcqImg2);
                    Glide.with(activity).load(new URL(learningQuestions.getMcq3())).into(mBinding.mcqImg3);
                    Glide.with(activity).load(new URL(learningQuestions.getMcq4())).into(mBinding.mcqImg4);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            mBinding.close.setOnClickListener(v ->
            {
                countDownTimer.cancel();
                mBinding.expandableLayout.setVisibility(View.GONE);
                mBinding.timerLayout.setVisibility(View.GONE);
                mBinding.expand.setVisibility(View.VISIBLE);
                mBinding.close.setVisibility(View.GONE);
            });

            mBinding.mcqImg1.setOnClickListener(v ->
            {
                if (isMCQImgSubmited)
                    setMCQImgAnsIndicator();
                if (!mcqimg_ans.contains("A")) {
                    if (mcqimg_ans.equalsIgnoreCase(""))
                        mcqimg_ans = "A";
                    else
                        mcqimg_ans = mcqimg_ans + " A";
                    mBinding.mcqImg1.setAlpha(130);
                    mBinding.mcqimgChck1.setVisibility(View.VISIBLE);
                    mBinding.mcqimgChck1.setImageDrawable(activity.getResources().getDrawable(R.drawable.selectmark));
                } else {
                    mcqimg_ans = mcqimg_ans.replace("A", "");
                    mBinding.mcqImg1.setAlpha(255);
                    mBinding.mcqimgChck1.setVisibility(View.GONE);
                }
            });

            mBinding.mcqImg2.setOnClickListener(v ->

            {
                if (isMCQImgSubmited)
                    setMCQImgAnsIndicator();
                if (!mcqimg_ans.contains("B")) {
                    if (mcqimg_ans.equalsIgnoreCase(""))
                        mcqimg_ans = "B";
                    else
                        mcqimg_ans = mcqimg_ans + "B";
                    mBinding.mcqImg2.setAlpha(130);
                    mBinding.mcqimgChck2.setVisibility(View.VISIBLE);
                    mBinding.mcqimgChck2.setImageDrawable(activity.getResources().getDrawable(R.drawable.selectmark));
                } else {
                    mcqimg_ans = mcqimg_ans.replace("B", "");
                    mBinding.mcqImg2.setAlpha(255);
                    mBinding.mcqimgChck2.setVisibility(View.GONE);
                }
            });

            mBinding.mcqImg3.setOnClickListener(v ->

            {
                if (isMCQImgSubmited)
                    setMCQImgAnsIndicator();
                if (!mcqimg_ans.contains("C")) {
                    if (mcqimg_ans.equalsIgnoreCase(""))
                        mcqimg_ans = "C";
                    else
                        mcqimg_ans = mcqimg_ans + " C";
                    mBinding.mcqImg3.setAlpha(130);
                    mBinding.mcqimgChck3.setVisibility(View.VISIBLE);
                    mBinding.mcqimgChck3.setImageDrawable(activity.getResources().getDrawable(R.drawable.selectmark));
                } else {
                    mcqimg_ans = mcqimg_ans.replace("C", "");
                    mBinding.mcqImg3.setAlpha(255);
                    mBinding.mcqimgChck3.setVisibility(View.GONE);
                }
            });

            mBinding.mcqImg4.setOnClickListener(v ->

            {
                if (isMCQImgSubmited)
                    setMCQImgAnsIndicator();
                if (!mcqimg_ans.contains("D")) {
                    if (mcqimg_ans.equalsIgnoreCase(""))
                        mcqimg_ans = "D";
                    else
                        mcqimg_ans = mcqimg_ans + " D";
                    mBinding.mcqImg4.setAlpha(130);
                    mBinding.mcqimgChck4.setVisibility(View.VISIBLE);
                    mBinding.mcqimgChck4.setImageDrawable(activity.getResources().getDrawable(R.drawable.selectmark));
                } else {
                    mcqimg_ans = mcqimg_ans.replace("D", "");
                    mBinding.mcqImg4.setAlpha(255);
                    mBinding.mcqimgChck4.setVisibility(View.GONE);
                }
            });

            mBinding.submit.setOnClickListener(v -> {
                submitCall();
            });

            mBinding.submitAndRate.setOnClickListener(v -> {
                submitCall();
                displayRatingDialog(learningQuestionsList.get(getAdapterPosition()), getAdapterPosition(), this);
            });

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
                mBinding.solutionLayout.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(activity, "Please select atleast one option.", Toast.LENGTH_SHORT).show();
            }
        }


        public void setRightMCQImg(String option) {
            switch (option) {
                case "A":
                    mBinding.mcqimgChck1.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right_mtp));
                    mBinding.mcqimgChck1.setVisibility(View.VISIBLE);
                    break;
                case "B":
                    mBinding.mcqimgChck2.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right_mtp));
                    mBinding.mcqimgChck2.setVisibility(View.VISIBLE);
                    break;
                case "C":
                    mBinding.mcqimgChck3.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right_mtp));
                    mBinding.mcqimgChck3.setVisibility(View.VISIBLE);
                    break;
                case "D":
                    mBinding.mcqimgChck4.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right_mtp));
                    mBinding.mcqimgChck4.setVisibility(View.VISIBLE);
                    break;
            }
        }

        public void setWrongMCQImg(String option) {
            switch (option) {
                case "A":
                    mBinding.mcqimgChck1.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                    mBinding.mcqimgChck1.setVisibility(View.VISIBLE);
                    break;
                case "B":
                    mBinding.mcqimgChck2.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                    mBinding.mcqimgChck2.setVisibility(View.VISIBLE);
                    break;
                case "C":
                    mBinding.mcqimgChck3.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                    mBinding.mcqimgChck3.setVisibility(View.VISIBLE);
                    break;
                case "D":
                    mBinding.mcqimgChck4.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                    mBinding.mcqimgChck4.setVisibility(View.VISIBLE);
                    break;
            }
        }


        private void setMCQImgAnsIndicator() {
            isMCQImgSubmited = false;
            mcqimg_ans = "";
            mBinding.mcqimgChck1.setVisibility(View.GONE);
            mBinding.mcqimgChck2.setVisibility(View.GONE);
            mBinding.mcqimgChck3.setVisibility(View.GONE);
            mBinding.mcqimgChck4.setVisibility(View.GONE);
            mBinding.mcqImg1.setAlpha(250);
            mBinding.mcqImg2.setAlpha(250);
            mBinding.mcqImg3.setAlpha(250);
            mBinding.mcqImg4.setAlpha(250);
        }

        @Override
        public void onValueChange(String q_comments, String q_shares, String attempted, String likeCount) {
            mBinding.commentValue.setText(q_comments);
            mBinding.shareValue.setText(q_shares);
            mBinding.attemptedValue.setText(attempted);
            mBinding.likeValue.setText(likeCount);
        }

        @Override
        public void onRatingChange(String ratings) {
            mBinding.ratingvalue.setText(ratings);

        }

        @Override
        public void onLikeChange(int ic_like) {
            Glide.with(activity).load(activity.getResources().getDrawable(ic_like)).into(mBinding.like);
        }

        @Override
        public void onSaveChange(int ic_save_grey) {
            Glide.with(activity).load(activity.getResources().getDrawable(ic_save_grey)).into(mBinding.saveQue);
        }

        @Override
        public void onFavChange(int ic_fav) {
            Glide.with(activity).load(activity.getResources().getDrawable(ic_fav)).into(mBinding.favorite);
        }

        @Override
        public void onOneLineAnsChange(int green_border, String ans) {

        }

        @Override
        public void onLongAnsChange(int green_border, String a_sub_ans) {

        }
    }

    public class MCQHolder extends RecyclerView.ViewHolder implements onValueChhangeListener {
        CountDownTimer countDownTimer;
        private String mcq_ans = "";
        LearningMcqBinding mBinding;
        int isSolvedRight, isAttempted = 0;

        public MCQHolder(LearningMcqBinding mBinding) {
            super(mBinding.getRoot());
            this.mBinding = mBinding;
            mBinding.askDoubt.setOnClickListener(v -> {
                askDoubts(getAdapterPosition());
            });

            mBinding.saveQue.setOnClickListener(v -> {
                saveQue(getAdapterPosition(), this);
            });


            mBinding.like.setOnClickListener(v -> {
                likeQue(getAdapterPosition(), this);
            });

            mBinding.likeValue.setOnClickListener(v -> {
                likeValueClick(getAdapterPosition());
            });

            mBinding.shareValue.setOnClickListener(v -> {
                shareValueClick(getAdapterPosition());
            });

            mBinding.share.setOnClickListener(v -> {
                onShareClick(getAdapterPosition());
            });

            mBinding.favorite.setOnClickListener(v -> {
                onFavClick(getAdapterPosition(), this);
            });


            mBinding.commentLayout.setOnClickListener(v -> onIconClick.onCommentClick(learningQuestionsList.get(
                    getAdapterPosition()), getAdapterPosition()));

            mBinding.expand.setOnClickListener(v -> {
                LearningQuestionsNew learningQuestions = learningQuestionsList.get(getAdapterPosition());
                mBinding.expandableLayout.setVisibility(View.VISIBLE);
                mBinding.timerLayout.setVisibility(View.VISIBLE);
                mBinding.close.setVisibility(View.VISIBLE);
                mBinding.expand.setVisibility(View.GONE);
                countDownTimer = setTimer(mBinding.tvtimer, 0, 0);
                mBinding.mcq1.setText(learningQuestions.getMcq1());
                mBinding.mcq2.setText(learningQuestions.getMcq2());
                mBinding.mcq3.setText(learningQuestions.getMcq3());
                mBinding.mcq4.setText(learningQuestions.getMcq4());
            });

            mBinding.close.setOnClickListener(v ->
            {
                countDownTimer.cancel();
                mBinding.expandableLayout.setVisibility(View.GONE);
                mBinding.timerLayout.setVisibility(View.GONE);
                mBinding.expand.setVisibility(View.VISIBLE);
                mBinding.close.setVisibility(View.GONE);
            });


            mBinding.mcq1Layout.setOnClickListener(v ->

            {
                setMCQAnsIndicator();
                if (!mcq_ans.contains("A")) {
                    if (mcq_ans.equalsIgnoreCase(""))
                        mcq_ans = "A";
                    else
                        mcq_ans = mcq_ans + " A";
                    mBinding.mcq1Layout.setBackground(activity.getResources().getDrawable(R.drawable.grey_border_grey_bg));
                } else {
                    mcq_ans = mcq_ans.replace("A", "");
                    mBinding.mcq1Layout.setBackground(activity.getResources().getDrawable(R.drawable.grey_round_order));
                }
            });

            mBinding.mcq2Layout.setOnClickListener(v ->

            {
                setMCQAnsIndicator();
                if (!mcq_ans.contains("B")) {
                    if (mcq_ans.equalsIgnoreCase(""))
                        mcq_ans = "B";
                    else
                        mcq_ans = mcq_ans + " B";
                    mBinding.mcq2Layout.setBackground(activity.getResources().getDrawable(R.drawable.grey_border_grey_bg));
                } else {
                    mcq_ans = mcq_ans.replace("B", "");
                    mBinding.mcq2Layout.setBackground(activity.getResources().getDrawable(R.drawable.grey_round_order));
                }
            });

            mBinding.mcq3Layout.setOnClickListener(v ->

            {
                setMCQAnsIndicator();
                if (!mcq_ans.contains("C")) {
                    if (mcq_ans.equalsIgnoreCase(""))
                        mcq_ans = "C";
                    else
                        mcq_ans = mcq_ans + " C";
                    mBinding.mcq3Layout.setBackground(activity.getResources().getDrawable(R.drawable.grey_border_grey_bg));
                } else {
                    mcq_ans = mcq_ans.replace("C", "");
                    mBinding.mcq3Layout.setBackground(activity.getResources().getDrawable(R.drawable.grey_round_order));
                }
            });

            mBinding.mcq4Layout.setOnClickListener(v ->

            {
                setMCQAnsIndicator();
                if (!mcq_ans.contains("D")) {
                    if (mcq_ans.equalsIgnoreCase(""))
                        mcq_ans = "D";
                    else
                        mcq_ans = mcq_ans + " D";
                    mBinding.mcq4Layout.setBackground(activity.getResources().getDrawable(R.drawable.grey_border_grey_bg));
                } else {
                    mcq_ans = mcq_ans.replace("D", "");
                    mBinding.mcq4Layout.setBackground(activity.getResources().getDrawable(R.drawable.grey_round_order));
                }
            });
            mBinding.submit.setOnClickListener(v -> {
                submitCall();
            });

            mBinding.submitAndRate.setOnClickListener(v -> {
                submitCall();
                displayRatingDialog(learningQuestionsList.get(getAdapterPosition()), getAdapterPosition(), this);
            });

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
                mBinding.solutionLayout.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(activity, "Please select atleast one option.", Toast.LENGTH_SHORT).show();
            }

        }

        public void setWrongMCQ(String option) {
            switch (option) {
                case "A":
                    mBinding.mcq1Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong));
                    mBinding.mcq1Img.setVisibility(View.VISIBLE);
                    break;
                case "B":
                    mBinding.mcq2Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong));
                    mBinding.mcq2Img.setVisibility(View.VISIBLE);
                    break;
                case "C":
                    mBinding.mcq3Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong));
                    mBinding.mcq3Img.setVisibility(View.VISIBLE);
                    break;
                case "D":
                    mBinding.mcq4Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong));
                    mBinding.mcq4Img.setVisibility(View.VISIBLE);
                    break;
            }
        }


        public void setRightMCQ(String option) {
            switch (option) {
                case "A":
                    mBinding.mcq1Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right));
                    mBinding.mcq1Img.setVisibility(View.VISIBLE);
                    break;
                case "B":
                    mBinding.mcq2Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right));
                    mBinding.mcq2Img.setVisibility(View.VISIBLE);
                    break;
                case "C":
                    mBinding.mcq3Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right));
                    mBinding.mcq3Img.setVisibility(View.VISIBLE);
                    break;
                case "D":
                    mBinding.mcq4Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right));
                    mBinding.mcq4Img.setVisibility(View.VISIBLE);
                    break;
            }
        }

        private void setMCQAnsIndicator() {
            mBinding.mcq1Img.setVisibility(View.GONE);
            mBinding.mcq2Img.setVisibility(View.GONE);
            mBinding.mcq3Img.setVisibility(View.GONE);
            mBinding.mcq4Img.setVisibility(View.GONE);
        }

        @Override
        public void onValueChange(String q_comments, String q_shares, String attempted, String likeCount) {
            mBinding.commentValue.setText(q_comments);
            mBinding.shareValue.setText(q_shares);
            mBinding.attemptedValue.setText(attempted);
            mBinding.likeValue.setText(likeCount);
        }

        @Override
        public void onRatingChange(String ratings) {
            mBinding.ratingvalue.setText(ratings);

        }

        @Override
        public void onLikeChange(int ic_like) {
            Glide.with(activity).load(activity.getResources().getDrawable(ic_like)).into(mBinding.like);
        }

        @Override
        public void onSaveChange(int ic_save_grey) {
            Glide.with(activity).load(activity.getResources().getDrawable(ic_save_grey)).into(mBinding.saveQue);
        }

        @Override
        public void onFavChange(int ic_fav) {
            Glide.with(activity).load(activity.getResources().getDrawable(ic_fav)).into(mBinding.favorite);
        }

        @Override
        public void onOneLineAnsChange(int green_border, String ans) {

        }

        @Override
        public void onLongAnsChange(int green_border, String a_sub_ans) {

        }
    }


    public class SCQImgTextHolder extends RecyclerView.ViewHolder implements onValueChhangeListener {
        CountDownTimer countDownTimer;
        private String scqimgtext_ans = "";
        LearningScqimgtextBinding mBinding;
        int isSolvedRight, isAttempted = 0;

        public SCQImgTextHolder(LearningScqimgtextBinding mBinding) {
            super(mBinding.getRoot());
            this.mBinding = mBinding;

            mBinding.askDoubt.setOnClickListener(v -> {
                askDoubts(getAdapterPosition());
            });

            mBinding.saveQue.setOnClickListener(v -> {
                saveQue(getAdapterPosition(), this);
            });


            mBinding.like.setOnClickListener(v -> {
                likeQue(getAdapterPosition(), this);
            });

            mBinding.likeValue.setOnClickListener(v -> {
                likeValueClick(getAdapterPosition());
            });

            mBinding.shareValue.setOnClickListener(v -> {
                shareValueClick(getAdapterPosition());
            });

            mBinding.share.setOnClickListener(v -> {
                onShareClick(getAdapterPosition());
            });

            mBinding.favorite.setOnClickListener(v -> {
                onFavClick(getAdapterPosition(), this);
            });


            mBinding.commentLayout.setOnClickListener(v -> onIconClick.onCommentClick(learningQuestionsList.get(
                    getAdapterPosition()), getAdapterPosition()));


            mBinding.expand.setOnClickListener(v -> {
                mBinding.expandableLayout.setVisibility(View.VISIBLE);
                mBinding.timerLayout.setVisibility(View.VISIBLE);
                mBinding.close.setVisibility(View.VISIBLE);
                mBinding.expand.setVisibility(View.GONE);
                countDownTimer = setTimer(mBinding.tvtimer, 0, 0);
                try {
                    LearningQuestionsNew learningQuestions = learningQuestionsList.get(getAdapterPosition());
                    Glide.with(activity).load(Constant.PRODUCTION_BASE_FILE_API + learningQuestions.getMcq1().split("::")[0]).into(mBinding.scqImgtextImg1);
                    Glide.with(activity).load(Constant.PRODUCTION_BASE_FILE_API + learningQuestions.getMcq2().split("::")[0]).into(mBinding.scqImgtextImg2);
                    Glide.with(activity).load(Constant.PRODUCTION_BASE_FILE_API + learningQuestions.getMcq3().split("::")[0]).into(mBinding.scqImgtextImg3);
                    Glide.with(activity).load(Constant.PRODUCTION_BASE_FILE_API + learningQuestions.getMcq4().split("::")[0]).into(mBinding.scqImgtextImg4);

                    mBinding.scqImgtextText1.setText(learningQuestions.getMcq1().split("::")[1]);
                    mBinding.scqImgtextText2.setText(learningQuestions.getMcq2().split("::")[1]);
                    mBinding.scqImgtextText3.setText(learningQuestions.getMcq3().split("::")[1]);
                    mBinding.scqImgtextText4.setText(learningQuestions.getMcq4().split("::")[1]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            mBinding.close.setOnClickListener(v ->
            {
                countDownTimer.cancel();
                mBinding.expandableLayout.setVisibility(View.GONE);
                mBinding.timerLayout.setVisibility(View.GONE);
                mBinding.expand.setVisibility(View.VISIBLE);
                mBinding.close.setVisibility(View.GONE);
            });


            mBinding.scqImgtextImg1.setOnClickListener(v ->

            {
                setSCQImgTextAnsIndicator();
                setSCQImgTextLayout();
                scqimgtext_ans = "A";
                mBinding.scqImgtextImg1.setAlpha(130);
                mBinding.scqimgImgtextChck1.setVisibility(View.VISIBLE);
                mBinding.scqimgImgtextChck1.setImageDrawable(activity.getResources().getDrawable(R.drawable.selectmark));
            });

            mBinding.scqImgtextImg2.setOnClickListener(v ->

            {
                setSCQImgTextAnsIndicator();
                setSCQImgTextLayout();
                scqimgtext_ans = "B";
                mBinding.scqImgtextImg2.setAlpha(130);
                mBinding.scqimgImgtextChck2.setVisibility(View.VISIBLE);
                mBinding.scqimgImgtextChck2.setImageDrawable(activity.getResources().getDrawable(R.drawable.selectmark));
            });

            mBinding.scqImgtextImg3.setOnClickListener(v ->

            {
                setSCQImgTextAnsIndicator();
                setSCQImgTextLayout();
                scqimgtext_ans = "C";
                mBinding.scqImgtextImg3.setAlpha(130);
                mBinding.scqimgImgtextChck3.setVisibility(View.VISIBLE);
                mBinding.scqimgImgtextChck3.setImageDrawable(activity.getResources().getDrawable(R.drawable.selectmark));
            });

            mBinding.scqImgtextImg4.setOnClickListener(v ->

            {
                setSCQImgTextAnsIndicator();
                setSCQImgTextLayout();
                scqimgtext_ans = "D";
                mBinding.scqImgtextImg4.setAlpha(130);
                mBinding.scqimgImgtextChck4.setVisibility(View.VISIBLE);
                mBinding.scqimgImgtextChck4.setImageDrawable(activity.getResources().getDrawable(R.drawable.selectmark));
            });
            mBinding.submit.setOnClickListener(v -> {
                submitCall();
            });

            mBinding.submitAndRate.setOnClickListener(v -> {
                submitCall();
                displayRatingDialog(learningQuestionsList.get(getAdapterPosition()), getAdapterPosition(), this);
            });

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
                mBinding.solutionLayout.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(activity, "Please select atleast one option.", Toast.LENGTH_SHORT).show();
            }
        }

        public void setRightSCQImgText(String option) {
            switch (option) {
                case "A":
                    mBinding.scqimgImgtextChck1.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right_mtp));
                    mBinding.scqimgImgtextChck1.setVisibility(View.VISIBLE);
                    break;
                case "B":
                    mBinding.scqimgImgtextChck2.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right_mtp));
                    mBinding.scqimgImgtextChck2.setVisibility(View.VISIBLE);
                    break;
                case "C":
                    mBinding.scqimgImgtextChck3.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right_mtp));
                    mBinding.scqimgImgtextChck3.setVisibility(View.VISIBLE);
                    break;
                case "D":
                    mBinding.scqimgImgtextChck4.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right_mtp));
                    mBinding.scqimgImgtextChck4.setVisibility(View.VISIBLE);
                    break;
            }
        }

        public void setWrongSCQImgText(String option) {
            switch (option) {
                case "A":
                    mBinding.scqimgImgtextChck1.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                    mBinding.scqimgImgtextChck1.setVisibility(View.VISIBLE);
                    break;
                case "B":
                    mBinding.scqimgImgtextChck2.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                    mBinding.scqimgImgtextChck2.setVisibility(View.VISIBLE);
                    break;
                case "C":
                    mBinding.scqimgImgtextChck3.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                    mBinding.scqimgImgtextChck3.setVisibility(View.VISIBLE);
                    break;
                case "D":
                    mBinding.scqimgImgtextChck4.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                    mBinding.scqimgImgtextChck4.setVisibility(View.VISIBLE);
                    break;
            }
        }


        private void setSCQImgTextLayout() {
            mBinding.scqImgtextImg1.setAlpha(255);
            mBinding.scqImgtextImg2.setAlpha(255);
            mBinding.scqImgtextImg3.setAlpha(255);
            mBinding.scqImgtextImg4.setAlpha(255);
        }

        private void setSCQImgTextAnsIndicator() {
            mBinding.scqimgImgtextChck1.setVisibility(View.GONE);
            mBinding.scqimgImgtextChck2.setVisibility(View.GONE);
            mBinding.scqimgImgtextChck3.setVisibility(View.GONE);
            mBinding.scqimgImgtextChck4.setVisibility(View.GONE);
        }

        @Override
        public void onValueChange(String q_comments, String q_shares, String attempted, String likeCount) {
            mBinding.commentValue.setText(q_comments);
            mBinding.shareValue.setText(q_shares);
            mBinding.attemptedValue.setText(attempted);
            mBinding.likeValue.setText(likeCount);
        }

        @Override
        public void onRatingChange(String ratings) {
            mBinding.ratingvalue.setText(ratings);

        }

        @Override
        public void onLikeChange(int ic_like) {
            Glide.with(activity).load(activity.getResources().getDrawable(ic_like)).into(mBinding.like);
        }

        @Override
        public void onSaveChange(int ic_save_grey) {
            Glide.with(activity).load(activity.getResources().getDrawable(ic_save_grey)).into(mBinding.saveQue);
        }

        @Override
        public void onFavChange(int ic_fav) {
            Glide.with(activity).load(activity.getResources().getDrawable(ic_fav)).into(mBinding.favorite);
        }

        @Override
        public void onOneLineAnsChange(int green_border, String ans) {

        }

        @Override
        public void onLongAnsChange(int green_border, String a_sub_ans) {

        }
    }


    public class SCQImgHolder extends RecyclerView.ViewHolder implements onValueChhangeListener {
        CountDownTimer countDownTimer;
        private String scqimg_ans = "";
        LearningScqimgBinding mBinding;
        int isSolvedRight, isAttempted = 0;

        public SCQImgHolder(LearningScqimgBinding mBinding) {
            super(mBinding.getRoot());
            this.mBinding = mBinding;

            mBinding.askDoubt.setOnClickListener(v -> {
                askDoubts(getAdapterPosition());
            });

            mBinding.saveQue.setOnClickListener(v -> {
                saveQue(getAdapterPosition(), this);
            });


            mBinding.like.setOnClickListener(v -> {
                likeQue(getAdapterPosition(), this);
            });

            mBinding.likeValue.setOnClickListener(v -> {
                likeValueClick(getAdapterPosition());
            });

            mBinding.shareValue.setOnClickListener(v -> {
                shareValueClick(getAdapterPosition());
            });

            mBinding.share.setOnClickListener(v -> {
                onShareClick(getAdapterPosition());
            });

            mBinding.favorite.setOnClickListener(v -> {
                onFavClick(getAdapterPosition(), this);
            });


            mBinding.commentLayout.setOnClickListener(v -> onIconClick.onCommentClick(learningQuestionsList.get(
                    getAdapterPosition()), getAdapterPosition()));

            mBinding.expand.setOnClickListener(v -> {
                mBinding.expandableLayout.setVisibility(View.VISIBLE);
                mBinding.timerLayout.setVisibility(View.VISIBLE);
                mBinding.close.setVisibility(View.VISIBLE);
                mBinding.expand.setVisibility(View.GONE);
                countDownTimer = setTimer(mBinding.tvtimer, 0, 0);
                try {
                    LearningQuestionsNew learningQuestions = learningQuestionsList.get(getAdapterPosition());
                    loadImage(learningQuestions.getMcq1(), mBinding.scqImg1);
                    loadImage(learningQuestions.getMcq2(), mBinding.scqImg2);
                    loadImage(learningQuestions.getMcq3(), mBinding.scqImg3);
                    loadImage(learningQuestions.getMcq4(), mBinding.scqImg4);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            mBinding.close.setOnClickListener(v ->
            {
                countDownTimer.cancel();
                mBinding.expandableLayout.setVisibility(View.GONE);
                mBinding.timerLayout.setVisibility(View.GONE);
                mBinding.expand.setVisibility(View.VISIBLE);
                mBinding.close.setVisibility(View.GONE);
            });

            mBinding.scqImg1.setOnClickListener(v -> {
                setSCQImgAnsIndicator();
                setSCQImgLayout();
                scqimg_ans = "A";
                mBinding.scqImg1.setAlpha(130);
                mBinding.scqimgChck1.setVisibility(View.VISIBLE);
                mBinding.scqimgChck1.setImageDrawable(activity.getResources().getDrawable(R.drawable.selectmark));
            });

            mBinding.scqImg2.setOnClickListener(v -> {
                setSCQImgAnsIndicator();
                setSCQImgLayout();
                scqimg_ans = "B";
                mBinding.scqImg2.setAlpha(130);
                mBinding.scqimgChck2.setVisibility(View.VISIBLE);
                mBinding.scqimgChck2.setImageDrawable(activity.getResources().getDrawable(R.drawable.selectmark));
            });

            mBinding.scqImg3.setOnClickListener(v -> {
                setSCQImgAnsIndicator();
                setSCQImgLayout();
                scqimg_ans = "C";
                mBinding.scqImg3.setAlpha(130);
                mBinding.scqimgChck3.setVisibility(View.VISIBLE);
                mBinding.scqimgChck3.setImageDrawable(activity.getResources().getDrawable(R.drawable.selectmark));
            });

            mBinding.scqImg4.setOnClickListener(v -> {
                setSCQImgAnsIndicator();
                setSCQImgLayout();
                scqimg_ans = "D";
                mBinding.scqImg4.setAlpha(130);
                mBinding.scqimgChck4.setVisibility(View.VISIBLE);
                mBinding.scqimgChck4.setImageDrawable(activity.getResources().getDrawable(R.drawable.selectmark));
            });

            mBinding.submit.setOnClickListener(v -> {
                submitCall();
            });

            mBinding.submitAndRate.setOnClickListener(v -> {
                submitCall();
                displayRatingDialog(learningQuestionsList.get(getAdapterPosition()), getAdapterPosition(), this);
            });

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
                mBinding.solutionLayout.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(activity, "Please select atleast one option.", Toast.LENGTH_SHORT).show();
            }
        }

        public void setRightSCQImg(String option) {
            switch (option) {
                case "A":
                    mBinding.scqimgChck1.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right_mtp));
                    mBinding.scqimgChck1.setVisibility(View.VISIBLE);
                    break;
                case "B":
                    mBinding.scqimgChck2.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right_mtp));
                    mBinding.scqimgChck2.setVisibility(View.VISIBLE);
                    break;
                case "C":
                    mBinding.scqimgChck3.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right_mtp));
                    mBinding.scqimgChck3.setVisibility(View.VISIBLE);
                    break;
                case "D":
                    mBinding.scqimgChck4.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right_mtp));
                    mBinding.scqimgChck4.setVisibility(View.VISIBLE);
                    break;
            }
        }

        public void setWrongSCQImg(String option) {
            switch (option) {
                case "A":
                    mBinding.scqimgChck1.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                    mBinding.scqimgChck1.setVisibility(View.VISIBLE);
                    break;
                case "B":
                    mBinding.scqimgChck2.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                    mBinding.scqimgChck2.setVisibility(View.VISIBLE);
                    break;
                case "C":
                    mBinding.scqimgChck3.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                    mBinding.scqimgChck3.setVisibility(View.VISIBLE);
                    break;
                case "D":
                    mBinding.scqimgChck4.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                    mBinding.scqimgChck4.setVisibility(View.VISIBLE);
                    break;
            }
        }

        private void setSCQImgLayout() {
            mBinding.scqImg1.setAlpha(255);
            mBinding.scqImg2.setAlpha(255);
            mBinding.scqImg3.setAlpha(255);
            mBinding.scqImg4.setAlpha(255);
        }

        private void setSCQImgAnsIndicator() {
            mBinding.scqimgChck1.setVisibility(View.GONE);
            mBinding.scqimgChck2.setVisibility(View.GONE);
            mBinding.scqimgChck3.setVisibility(View.GONE);
            mBinding.scqimgChck4.setVisibility(View.GONE);
        }


        @Override
        public void onValueChange(String q_comments, String q_shares, String attempted, String likeCount) {
            mBinding.commentValue.setText(q_comments);
            mBinding.shareValue.setText(q_shares);
            mBinding.attemptedValue.setText(attempted);
            mBinding.likeValue.setText(likeCount);
        }

        @Override
        public void onRatingChange(String ratings) {
            mBinding.ratingvalue.setText(ratings);

        }

        @Override
        public void onLikeChange(int ic_like) {
            Glide.with(activity).load(activity.getResources().getDrawable(ic_like)).into(mBinding.like);
        }

        @Override
        public void onSaveChange(int ic_save_grey) {
            Glide.with(activity).load(activity.getResources().getDrawable(ic_save_grey)).into(mBinding.saveQue);
        }

        @Override
        public void onFavChange(int ic_fav) {
            Glide.with(activity).load(activity.getResources().getDrawable(ic_fav)).into(mBinding.favorite);
        }

        @Override
        public void onOneLineAnsChange(int green_border, String ans) {

        }

        @Override
        public void onLongAnsChange(int green_border, String a_sub_ans) {

        }
    }


    public class SCQHolder extends RecyclerView.ViewHolder implements onValueChhangeListener {
        CountDownTimer countDownTimer;
        private String scq_ans = "";
        LearningScqBinding mBinding;
        int isSolvedRight, isAttempted = 0;

        public SCQHolder(LearningScqBinding mBinding) {
            super(mBinding.getRoot());
            this.mBinding = mBinding;

            mBinding.askDoubt.setOnClickListener(v -> {
                askDoubts(getAdapterPosition());
            });

            mBinding.saveQue.setOnClickListener(v -> {
                saveQue(getAdapterPosition(), this);
            });


            mBinding.like.setOnClickListener(v -> {
                likeQue(getAdapterPosition(), this);
            });

            mBinding.likeValue.setOnClickListener(v -> {
                likeValueClick(getAdapterPosition());
            });

            mBinding.shareValue.setOnClickListener(v -> {
                shareValueClick(getAdapterPosition());
            });

            mBinding.share.setOnClickListener(v -> {
                onShareClick(getAdapterPosition());
            });

            mBinding.favorite.setOnClickListener(v -> {
                onFavClick(getAdapterPosition(), this);
            });


            mBinding.commentLayout.setOnClickListener(v -> onIconClick.onCommentClick(learningQuestionsList.get(
                    getAdapterPosition()), getAdapterPosition()));

            mBinding.expand.setOnClickListener(v -> {
                mBinding.expandableLayout.setVisibility(View.VISIBLE);
                mBinding.timerLayout.setVisibility(View.VISIBLE);
                mBinding.close.setVisibility(View.VISIBLE);
                mBinding.expand.setVisibility(View.GONE);
                countDownTimer = setTimer(mBinding.tvtimer, 0, 0);
                LearningQuestionsNew learningQuestions = learningQuestionsList.get(getAdapterPosition());
                mBinding.scq1.setText(learningQuestions.getMcq1());
                mBinding.scq2.setText(learningQuestions.getMcq2());
                mBinding.scq3.setText(learningQuestions.getMcq3());
                mBinding.scq4.setText(learningQuestions.getMcq4());
            });

            mBinding.close.setOnClickListener(v ->
            {
                countDownTimer.cancel();
                mBinding.expandableLayout.setVisibility(View.GONE);
                mBinding.timerLayout.setVisibility(View.GONE);
                mBinding.expand.setVisibility(View.VISIBLE);
                mBinding.close.setVisibility(View.GONE);
            });


            mBinding.scq1Layout.setOnClickListener(v -> {
                setSCQAnsIndicator();
                setLayoutBg();
                scq_ans = "A";
                mBinding.scq1Layout.setBackground(activity.getResources().getDrawable(R.drawable.grey_border_grey_bg));
            });

            mBinding.scq2Layout.setOnClickListener(v -> {
                setLayoutBg();
                setSCQAnsIndicator();
                scq_ans = "B";
                mBinding.scq2Layout.setBackground(activity.getResources().getDrawable(R.drawable.grey_border_grey_bg));
            });

            mBinding.scq3Layout.setOnClickListener(v -> {
                setLayoutBg();
                setSCQAnsIndicator();
                scq_ans = "C";
                mBinding.scq3Layout.setBackground(activity.getResources().getDrawable(R.drawable.grey_border_grey_bg));
            });

            mBinding.scq4Layout.setOnClickListener(v ->

            {
                setLayoutBg();
                setSCQAnsIndicator();
                scq_ans = "D";
                mBinding.scq4Layout.setBackground(activity.getResources().getDrawable(R.drawable.grey_border_grey_bg));
            });

            mBinding.submit.setOnClickListener(v -> {
                submitCall();
            });

            mBinding.submitAndRate.setOnClickListener(v -> {
                submitCall();
                displayRatingDialog(learningQuestionsList.get(getAdapterPosition()), getAdapterPosition(), this);
            });

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
                mBinding.solutionLayout.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(activity, "Please select atleast one option.", Toast.LENGTH_SHORT).show();
            }
        }

        public void setRightSCQ(String option) {
            switch (option) {
                case "A":
                    mBinding.scq1Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right));
                    mBinding.scq1Img.setVisibility(View.VISIBLE);
                    break;
                case "B":
                    mBinding.scq2Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right));
                    mBinding.scq2Img.setVisibility(View.VISIBLE);
                    break;
                case "C":
                    mBinding.scq3Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right));
                    mBinding.scq3Img.setVisibility(View.VISIBLE);
                    break;
                case "D":
                    mBinding.scq4Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right));
                    mBinding.scq4Img.setVisibility(View.VISIBLE);
                    break;
            }
        }

        public void setWrongSCQ(String option) {
            switch (option) {
                case "A":
                    mBinding.scq1Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong));
                    mBinding.scq1Img.setVisibility(View.VISIBLE);
                    break;
                case "B":
                    mBinding.scq2Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong));
                    mBinding.scq2Img.setVisibility(View.VISIBLE);
                    break;
                case "C":
                    mBinding.scq3Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong));
                    mBinding.scq3Img.setVisibility(View.VISIBLE);
                    break;
                case "D":
                    mBinding.scq4Img.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong));
                    mBinding.scq4Img.setVisibility(View.VISIBLE);
                    break;
            }
        }


        private void setSCQAnsIndicator() {
            mBinding.scq1Img.setVisibility(View.GONE);
            mBinding.scq2Img.setVisibility(View.GONE);
            mBinding.scq3Img.setVisibility(View.GONE);
            mBinding.scq4Img.setVisibility(View.GONE);
        }


        public void setLayoutBg() {
            mBinding.scq1Layout.setBackground(activity.getResources().getDrawable(R.drawable.grey_round_order));
            mBinding.scq2Layout.setBackground(activity.getResources().getDrawable(R.drawable.grey_round_order));
            mBinding.scq3Layout.setBackground(activity.getResources().getDrawable(R.drawable.grey_round_order));
            mBinding.scq4Layout.setBackground(activity.getResources().getDrawable(R.drawable.grey_round_order));
        }

        @Override
        public void onValueChange(String q_comments, String q_shares, String attempted, String likeCount) {
            mBinding.commentValue.setText(q_comments);
            mBinding.shareValue.setText(q_shares);
            mBinding.attemptedValue.setText(attempted);
            mBinding.likeValue.setText(likeCount);
        }

        @Override
        public void onRatingChange(String ratings) {
            mBinding.ratingvalue.setText(ratings);

        }

        @Override
        public void onLikeChange(int ic_like) {
            Glide.with(activity).load(activity.getResources().getDrawable(ic_like)).into(mBinding.like);
        }

        @Override
        public void onSaveChange(int ic_save_grey) {
            Glide.with(activity).load(activity.getResources().getDrawable(ic_save_grey)).into(mBinding.saveQue);
        }

        @Override
        public void onFavChange(int ic_fav) {
            Glide.with(activity).load(activity.getResources().getDrawable(ic_fav)).into(mBinding.favorite);
        }

        @Override
        public void onOneLineAnsChange(int green_border, String ans) {

        }

        @Override
        public void onLongAnsChange(int green_border, String a_sub_ans) {

        }
    }


    public class TrueFalseHolder extends RecyclerView.ViewHolder implements onValueChhangeListener {
        CountDownTimer countDownTimer;
        private String tfAns = "";
        LearningTruefalseBinding mBinding;
        int isSolvedRight, isAttempted = 0;

        public TrueFalseHolder(LearningTruefalseBinding mBinding) {
            super(mBinding.getRoot());
            this.mBinding = mBinding;

            mBinding.askDoubt.setOnClickListener(v -> {
                askDoubts(getAdapterPosition());
            });

            mBinding.saveQue.setOnClickListener(v -> {
                saveQue(getAdapterPosition(), this);
            });


            mBinding.like.setOnClickListener(v -> {
                likeQue(getAdapterPosition(), this);
            });

            mBinding.likeValue.setOnClickListener(v -> {
                likeValueClick(getAdapterPosition());
            });

            mBinding.shareValue.setOnClickListener(v -> {
                shareValueClick(getAdapterPosition());
            });

            mBinding.share.setOnClickListener(v -> {
                onShareClick(getAdapterPosition());
            });

            mBinding.favorite.setOnClickListener(v -> {
                onFavClick(getAdapterPosition(), this);
            });


            mBinding.commentLayout.setOnClickListener(v -> onIconClick.onCommentClick(learningQuestionsList.get(
                    getAdapterPosition()), getAdapterPosition()));

            mBinding.expand.setOnClickListener(v -> {
                mBinding.expandableLayout.setVisibility(View.VISIBLE);
                mBinding.timerLayout.setVisibility(View.VISIBLE);
                mBinding.close.setVisibility(View.VISIBLE);
                mBinding.expand.setVisibility(View.GONE);
                countDownTimer = setTimer(mBinding.tvtimer, 0, 0);
            });

            mBinding.close.setOnClickListener(v ->
            {
                countDownTimer.cancel();
                mBinding.expandableLayout.setVisibility(View.GONE);
                mBinding.timerLayout.setVisibility(View.GONE);
                mBinding.expand.setVisibility(View.VISIBLE);
                mBinding.close.setVisibility(View.GONE);
            });

            mBinding.btntrue.setOnClickListener(v -> {
                setTFLayoutBg();
                tfAns = "true";
                mBinding.btnfalse.setTextColor(activity.getResources().getColor(R.color.black));
                mBinding.btntrue.setBackground(activity.getResources().getDrawable(R.drawable.grey_border_grey_bg));

            });

            mBinding.btnfalse.setOnClickListener(v ->
            {
                setTFLayoutBg();
                tfAns = "false";
                mBinding.btntrue.setTextColor(activity.getResources().getColor(R.color.black));
                mBinding.btnfalse.setBackground(activity.getResources().getDrawable(R.drawable.grey_border_grey_bg));
            });

            mBinding.submit.setOnClickListener(v -> {
                submitCall();
            });

            mBinding.submitAndRate.setOnClickListener(v -> {
                submitCall();
                displayRatingDialog(learningQuestionsList.get(getAdapterPosition()), getAdapterPosition(), this);
            });

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
            if (!tfAns.equalsIgnoreCase("")) {
                isAttempted = 1;
                if (tfAns.equalsIgnoreCase(learningQuestions.getAnswer())) {
                    setRightTF("true");
                } else {
                    isSolvedRight = 0;
                    setWrongTF(tfAns);
                }
                mBinding.solutionLayout.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(activity, "Please select atleast one option.", Toast.LENGTH_SHORT).show();
            }
        }

        public void setRightTF(String option) {
            switch (option) {
                case "true":
                    mBinding.btntrue.setTextColor(activity.getResources().getColor(R.color.white));
                    mBinding.btntrue.setBackground(activity.getResources().getDrawable(R.drawable.bg_green_round));
                    break;
                case "false":
                    mBinding.btnfalse.setTextColor(activity.getResources().getColor(R.color.white));
                    mBinding.btnfalse.setBackground(activity.getResources().getDrawable(R.drawable.bg_green_round));
                    break;
            }
        }

        public void setWrongTF(String option) {
            switch (option) {
                case "true":
                    mBinding.btntrue.setTextColor(activity.getResources().getColor(R.color.white));
                    mBinding.btntrue.setBackground(activity.getResources().getDrawable(R.drawable.bg_red_round));
                    break;
                case "false":
                    mBinding.btnfalse.setTextColor(activity.getResources().getColor(R.color.white));
                    mBinding.btnfalse.setBackground(activity.getResources().getDrawable(R.drawable.bg_red_round));
                    break;
            }
        }

        public void setTFLayoutBg() {
            mBinding.btntrue.setBackground(activity.getResources().getDrawable(R.drawable.grey_round_order));
            mBinding.btnfalse.setBackground(activity.getResources().getDrawable(R.drawable.grey_round_order));
        }

        @Override
        public void onValueChange(String q_comments, String q_shares, String attempted, String likeCount) {
            mBinding.commentValue.setText(q_comments);
            mBinding.shareValue.setText(q_shares);
            mBinding.attemptedValue.setText(attempted);
            mBinding.likeValue.setText(likeCount);
        }

        @Override
        public void onRatingChange(String ratings) {
            mBinding.ratingvalue.setText(ratings);

        }

        @Override
        public void onLikeChange(int ic_like) {
            Glide.with(activity).load(activity.getResources().getDrawable(ic_like)).into(mBinding.like);
        }

        @Override
        public void onSaveChange(int ic_save_grey) {
            Glide.with(activity).load(activity.getResources().getDrawable(ic_save_grey)).into(mBinding.saveQue);
        }

        @Override
        public void onFavChange(int ic_fav) {
            Glide.with(activity).load(activity.getResources().getDrawable(ic_fav)).into(mBinding.favorite);
        }

        @Override
        public void onOneLineAnsChange(int green_border, String ans) {

        }

        @Override
        public void onLongAnsChange(int green_border, String a_sub_ans) {

        }
    }

    public class OneLineAnsHolder extends RecyclerView.ViewHolder implements onValueChhangeListener {
        CountDownTimer countDownTimer;
        int isSolvedRight, isAttempted = 0;
        LearningOnelineansBinding mBinding;

        public OneLineAnsHolder(LearningOnelineansBinding mBinding) {
            super(mBinding.getRoot());
            this.mBinding = mBinding;

            mBinding.askDoubt.setOnClickListener(v -> {
                askDoubts(getAdapterPosition());
            });

            mBinding.saveQue.setOnClickListener(v -> {
                saveQue(getAdapterPosition(), this);
            });


            mBinding.like.setOnClickListener(v -> {
                likeQue(getAdapterPosition(), this);
            });

            mBinding.likeValue.setOnClickListener(v -> {
                likeValueClick(getAdapterPosition());
            });

            mBinding.shareValue.setOnClickListener(v -> {
                shareValueClick(getAdapterPosition());
            });

            mBinding.share.setOnClickListener(v -> {
                onShareClick(getAdapterPosition());
            });

            mBinding.favorite.setOnClickListener(v -> {
                onFavClick(getAdapterPosition(), this);
            });


            mBinding.commentLayout.setOnClickListener(v -> onIconClick.onCommentClick(learningQuestionsList.get(
                    getAdapterPosition()), getAdapterPosition()));

            mBinding.expand.setOnClickListener(v -> {
                mBinding.expandableLayout.setVisibility(View.VISIBLE);
                mBinding.timerLayout.setVisibility(View.VISIBLE);
                mBinding.close.setVisibility(View.VISIBLE);
                mBinding.expand.setVisibility(View.GONE);
                countDownTimer = setTimer(mBinding.tvtimer, 0, 0);
                answerCharCounter(mBinding.singleLine, mBinding.singleLineCounter, 200);
            });

            mBinding.close.setOnClickListener(v ->
            {
                countDownTimer.cancel();
                mBinding.expandableLayout.setVisibility(View.GONE);
                mBinding.timerLayout.setVisibility(View.GONE);
                mBinding.expand.setVisibility(View.VISIBLE);
                mBinding.close.setVisibility(View.GONE);
            });
            mBinding.submit.setOnClickListener(v -> {
                submitCall();
            });

            mBinding.submitAndRate.setOnClickListener(v -> {
                submitCall();
                displayRatingDialog(learningQuestionsList.get(getAdapterPosition()), getAdapterPosition(), this);
            });

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
            if (mBinding.singleLine.getText().toString().isEmpty()) {
                Toast.makeText(activity, "Please enter answer first.", Toast.LENGTH_SHORT).show();
            } else {
                isAttempted = 1;
                String encoded = Base64.encodeToString(mBinding.singleLine.getText().toString().getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
                String encodedText = StringUtils.stripAccents(encoded);
                ProcessQuestionAPI(learningQuestions.getQuestion_id(), 0, ONE_LINE_ANSWER, "", "", getAdapterPosition(), encodedText, this);
            }
        }

        @Override
        public void onValueChange(String q_comments, String q_shares, String attempted, String likeCount) {
            mBinding.commentValue.setText(q_comments);
            mBinding.shareValue.setText(q_shares);
            mBinding.attemptedValue.setText(attempted);
            mBinding.likeValue.setText(likeCount);
        }

        @Override
        public void onRatingChange(String ratings) {
            mBinding.ratingvalue.setText(ratings);

        }

        @Override
        public void onLikeChange(int ic_like) {
            Glide.with(activity).load(activity.getResources().getDrawable(ic_like)).into(mBinding.like);
        }

        @Override
        public void onSaveChange(int ic_save_grey) {
            Glide.with(activity).load(activity.getResources().getDrawable(ic_save_grey)).into(mBinding.saveQue);
        }

        @Override
        public void onFavChange(int ic_fav) {
            Glide.with(activity).load(activity.getResources().getDrawable(ic_fav)).into(mBinding.favorite);
        }

        @Override
        public void onOneLineAnsChange(int border, String ans) {
            mBinding.singleLine.setBackground(activity.getResources().getDrawable(border));
            mBinding.solutionLayout.setVisibility(View.VISIBLE);
            mBinding.solutionDesc.setText(ans);
        }

        @Override
        public void onLongAnsChange(int green_border, String a_sub_ans) {

        }
    }

    public class LongAnsHolder extends RecyclerView.ViewHolder implements onValueChhangeListener {
        CountDownTimer countDownTimer;
        int isSolvedRight, isAttempted = 0;
        LearningLongansBinding mBinding;

        public LongAnsHolder(LearningLongansBinding mBinding) {
            super(mBinding.getRoot());
            this.mBinding = mBinding;

            mBinding.askDoubt.setOnClickListener(v -> {
                askDoubts(getAdapterPosition());
            });

            mBinding.saveQue.setOnClickListener(v -> {
                saveQue(getAdapterPosition(), this);
            });


            mBinding.like.setOnClickListener(v -> {
                likeQue(getAdapterPosition(), this);
            });

            mBinding.likeValue.setOnClickListener(v -> {
                likeValueClick(getAdapterPosition());
            });

            mBinding.shareValue.setOnClickListener(v -> {
                shareValueClick(getAdapterPosition());
            });

            mBinding.share.setOnClickListener(v -> {
                onShareClick(getAdapterPosition());
            });

            mBinding.favorite.setOnClickListener(v -> {
                onFavClick(getAdapterPosition(), this);
            });


            mBinding.commentLayout.setOnClickListener(v -> onIconClick.onCommentClick(learningQuestionsList.get(
                    getAdapterPosition()), getAdapterPosition()));

            mBinding.expand.setOnClickListener(v -> {
                mBinding.expandableLayout.setVisibility(View.VISIBLE);
                mBinding.timerLayout.setVisibility(View.VISIBLE);
                mBinding.close.setVisibility(View.VISIBLE);
                mBinding.expand.setVisibility(View.GONE);
                countDownTimer = setTimer(mBinding.tvtimer, 0, 0);
                answerCharCounter(mBinding.multiLine, mBinding.multiLineCounter, 200);
            });

            mBinding.close.setOnClickListener(v ->
            {
                countDownTimer.cancel();
                mBinding.expandableLayout.setVisibility(View.GONE);
                mBinding.timerLayout.setVisibility(View.GONE);
                mBinding.expand.setVisibility(View.VISIBLE);
                mBinding.close.setVisibility(View.GONE);
            });
            mBinding.submit.setOnClickListener(v -> {
                submitCall();
            });

            mBinding.submitAndRate.setOnClickListener(v -> {
                submitCall();
                displayRatingDialog(learningQuestionsList.get(getAdapterPosition()), getAdapterPosition(), this);
            });

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
            if (mBinding.multiLine.getText().toString().isEmpty()) {
                Toast.makeText(activity, "Please enter answer first.", Toast.LENGTH_SHORT).show();
            } else {
                isAttempted = 1;
                String encoded = Base64.encodeToString(mBinding.multiLine.getText().toString().getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
                String encodedText = StringUtils.stripAccents(encoded);
                ProcessQuestionAPI(learningQuestions.getQuestion_id(), 0, LONG_ANSWER, "", "", getAdapterPosition(), encodedText, this);
            }
        }

        @Override
        public void onValueChange(String q_comments, String q_shares, String attempted, String likeCount) {
            mBinding.commentValue.setText(q_comments);
            mBinding.shareValue.setText(q_shares);
            mBinding.attemptedValue.setText(attempted);
            mBinding.likeValue.setText(likeCount);
        }

        @Override
        public void onRatingChange(String ratings) {
            mBinding.ratingvalue.setText(ratings);

        }

        @Override
        public void onLikeChange(int ic_like) {
            Glide.with(activity).load(activity.getResources().getDrawable(ic_like)).into(mBinding.like);
        }

        @Override
        public void onSaveChange(int ic_save_grey) {
            Glide.with(activity).load(activity.getResources().getDrawable(ic_save_grey)).into(mBinding.saveQue);
        }

        @Override
        public void onFavChange(int ic_fav) {
            Glide.with(activity).load(activity.getResources().getDrawable(ic_fav)).into(mBinding.favorite);
        }

        @Override
        public void onOneLineAnsChange(int green_border, String ans) {

        }

        @Override
        public void onLongAnsChange(int border, String a_sub_ans) {
            mBinding.multiLine.setBackground(activity.getResources().getDrawable(border));
            mBinding.solutionLayout.setVisibility(View.VISIBLE);
            mBinding.solutionDesc.setText(a_sub_ans);
        }
    }

    public class FilltheBlanksHolder extends RecyclerView.ViewHolder implements onValueChhangeListener {
        CountDownTimer countDownTimer;
        LearningFillintheblanksBinding mBinding;
        int isSolvedRight, isAttempted = 0;

        public FilltheBlanksHolder(LearningFillintheblanksBinding mBinding) {
            super(mBinding.getRoot());
            this.mBinding = mBinding;

            mBinding.askDoubt.setOnClickListener(v -> {
                askDoubts(getAdapterPosition());
            });

            mBinding.saveQue.setOnClickListener(v -> {
                saveQue(getAdapterPosition(), this);
            });


            mBinding.like.setOnClickListener(v -> {
                likeQue(getAdapterPosition(), this);
            });

            mBinding.likeValue.setOnClickListener(v -> {
                likeValueClick(getAdapterPosition());
            });

            mBinding.shareValue.setOnClickListener(v -> {
                shareValueClick(getAdapterPosition());
            });

            mBinding.share.setOnClickListener(v -> {
                onShareClick(getAdapterPosition());
            });

            mBinding.favorite.setOnClickListener(v -> {
                onFavClick(getAdapterPosition(), this);
            });


            mBinding.commentLayout.setOnClickListener(v -> onIconClick.onCommentClick(learningQuestionsList.get(
                    getAdapterPosition()), getAdapterPosition()));

            mBinding.expand.setOnClickListener(v -> {
                mBinding.expandableLayout.setVisibility(View.VISIBLE);
                mBinding.timerLayout.setVisibility(View.VISIBLE);
                mBinding.close.setVisibility(View.VISIBLE);
                mBinding.expand.setVisibility(View.GONE);
                countDownTimer = setTimer(mBinding.tvtimer, 0, 0);
            });

            mBinding.close.setOnClickListener(v ->
            {
                countDownTimer.cancel();
                mBinding.expandableLayout.setVisibility(View.GONE);
                mBinding.timerLayout.setVisibility(View.GONE);
                mBinding.expand.setVisibility(View.VISIBLE);
                mBinding.close.setVisibility(View.GONE);
            });
            mBinding.submit.setOnClickListener(v -> {
                submitCall();
            });

            mBinding.submitAndRate.setOnClickListener(v -> {
                submitCall();
                displayRatingDialog(learningQuestionsList.get(getAdapterPosition()), getAdapterPosition(), this);
            });

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
            mBinding.fillInTheBlanks.setBackground(activity.getResources().getDrawable(R.drawable.grey_border));
            if (!mBinding.fillInTheBlanks.getText().toString().trim().equalsIgnoreCase("")) {
                isAttempted = 1;
                if (mBinding.fillInTheBlanks.getText().toString().trim().equalsIgnoreCase(learningQuestions.getAnswer().toString().trim())) {
                    mBinding.fibImg.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_right));
                    mBinding.fillInTheBlanks.setBackground(activity.getResources().getDrawable(R.drawable.green_border));
                } else {
                    isSolvedRight = 0;
                    mBinding.fillInTheBlanks.setBackground(activity.getResources().getDrawable(R.drawable.red_border));
                    mBinding.fibImg.setImageDrawable(activity.getResources().getDrawable(R.drawable.ic_wrong));
                }
                mBinding.solutionLayout.setVisibility(View.VISIBLE);
                mBinding.fibImg.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(activity, "Please enter answer first.", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onValueChange(String q_comments, String q_shares, String attempted, String likeCount) {
            mBinding.commentValue.setText(q_comments);
            mBinding.shareValue.setText(q_shares);
            mBinding.attemptedValue.setText(attempted);
            mBinding.likeValue.setText(likeCount);
        }

        @Override
        public void onRatingChange(String ratings) {
            mBinding.ratingvalue.setText(ratings);

        }

        @Override
        public void onLikeChange(int ic_like) {
            Glide.with(activity).load(activity.getResources().getDrawable(ic_like)).into(mBinding.like);
        }

        @Override
        public void onSaveChange(int ic_save_grey) {
            Glide.with(activity).load(activity.getResources().getDrawable(ic_save_grey)).into(mBinding.saveQue);
        }

        @Override
        public void onFavChange(int ic_fav) {
            Glide.with(activity).load(activity.getResources().getDrawable(ic_fav)).into(mBinding.favorite);
        }

        @Override
        public void onOneLineAnsChange(int green_border, String ans) {

        }

        @Override
        public void onLongAnsChange(int green_border, String a_sub_ans) {

        }
    }

    private CountDownTimer setTimer(TextView timer, int seconds, int minutes) {
        CountDownTimer countDownTimer = new CountDownTimer(60 * 1000 * 60, 1000) {
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
                        timer.setText(timerCountMinutes + ":0" + timerCountSeconds);
                    } else {
                        timer.setText(String.valueOf(timerCountMinutes + ":" + timerCountSeconds));
                    }
                }
            }

            public void onFinish() {
                timer.setText("00:00");
            }
        }.start();
        return countDownTimer;
    }

}
