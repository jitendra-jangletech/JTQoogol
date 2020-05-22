package com.jangletech.qoogol.ui.practise;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.activities.PracticeTestActivity;
import com.jangletech.qoogol.databinding.PracticeSubjectiveBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.model.LearningQuestionsNew;
import com.jangletech.qoogol.model.ProcessQuestion;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;
import com.jangletech.qoogol.util.UtilHelper;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;

public class PractiseSubjectiveFragment extends BaseFragment {

    private SubjectivePractiseViewModel mViewModel;
    private PracticeSubjectiveBinding mBinding;
    private static final String TAG = "ScqFragment";
    private static final String ARG_COUNT = "param1";
    private CountDownTimer countDownTimer;
    ApiInterface apiService = ApiClient.getInstance().getApi();
    private Integer counter;

    public static PractiseSubjectiveFragment newInstance(Integer counter) {
        PractiseSubjectiveFragment fragment = new PractiseSubjectiveFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COUNT, counter);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            counter = getArguments().getInt(ARG_COUNT);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.practice_subjective, container, false);
        mViewModel = ViewModelProviders.of(this).get(SubjectivePractiseViewModel.class);
        initViews();
        return mBinding.getRoot();
    }

    private void initViews() {
        LearningQuestionsNew learningQuestionsNew = PracticeTestActivity.questionsNewList.get(counter);
        if (learningQuestionsNew.getType().equals(Constant.SHORT_ANSWER)) {
            mBinding.categoryTextview.setText("Short Answer");
            mBinding.multiLineAnswer.setVisibility(View.VISIBLE);
        } else if (learningQuestionsNew.getType().equals(Constant.LONG_ANSWER)) {
            mBinding.categoryTextview.setText("Long Answer");
            mBinding.multiLineAnswer.setVisibility(View.VISIBLE);
        } else if (learningQuestionsNew.getType().equals(Constant.ONE_LINE_ANSWER)) {
            mBinding.categoryTextview.setText("One Line Answer");
            mBinding.fillInTheBlanksLayout.setVisibility(View.VISIBLE);
        } else if (learningQuestionsNew.getType().equals(Constant.FILL_THE_BLANKS)) {
            mBinding.categoryTextview.setText("Fill in the Blanks");
            mBinding.fillInTheBlanksLayout.setVisibility(View.VISIBLE);
        }

        mBinding.idTextview.setText(learningQuestionsNew.getQuestion_id());
        mBinding.timeTextview.setText("Time: " + learningQuestionsNew.getRecommended_time() + " Sec");
        mBinding.difflevelValue.setText(learningQuestionsNew.getDifficulty_level());
        mBinding.likeValue.setText(learningQuestionsNew.getLikes());
        mBinding.commentValue.setText(learningQuestionsNew.getComments());
        mBinding.shareValue.setText(learningQuestionsNew.getShares());
        mBinding.subjectTextview.setText(learningQuestionsNew.getSubject());
        mBinding.marksTextview.setText("Marks : " + UtilHelper.formatMarks(Float.parseFloat(learningQuestionsNew.getMarks())));

        mBinding.chapterTextview.setText(learningQuestionsNew.getChapter());
        mBinding.topicTextview.setText(learningQuestionsNew.getTopic());
        mBinding.postedValue.setText(learningQuestionsNew.getPosted_on() != null ? learningQuestionsNew.getPosted_on().substring(0, 10) : "");
        mBinding.lastUsedValue.setText(learningQuestionsNew.getLastused_on() != null ? learningQuestionsNew.getLastused_on().substring(0, 10) : "");
        mBinding.tvQuestion.setText(learningQuestionsNew.getQuestion());
        mBinding.tvMathQuestion.setText(learningQuestionsNew.getQuestiondesc());
        mBinding.attemptedValue.setText(learningQuestionsNew.getAttended_by() != null ? learningQuestionsNew.getAttended_by() : "0");
        mBinding.ratingvalue.setText(learningQuestionsNew.getRating());
        mBinding.solutionOption.setText("Answer : " + learningQuestionsNew.getAnswer());
        mBinding.solutionDesc.setText(learningQuestionsNew.getAnswerDesc());
        answerCharCounter(mBinding.multiLine, mBinding.tvWordCounter, 400);
        setTimer(mBinding.tvtimer,0,0);

        mBinding.submit.setOnClickListener(v -> {
            //todo submit
        });

//        mBinding.saveQue.setOnClickListener(v ->
//                //saveToDb(learningQuestionsList.get(getAdapterPosition()))
//        );

        mBinding.like.setOnClickListener(v -> {
            //LearningQuestionsNew learningQuestions = learningQuestionsList.get(getAdapterPosition());
            int likes = Integer.parseInt(learningQuestionsNew.getLikes());
            if (learningQuestionsNew.getIs_liked().equalsIgnoreCase("true")) {
                //onIconClick.onLikeClick(learningQuestionsNew.getQuestion_id(),0);
                //new PreferenceManager(getApplicationContext()).getInt(Constant.USER_ID), questionId,"I",isLiked, "like"
                HashMap<String, String> params = new HashMap<>();
                params.put(Constant.u_user_id, String.valueOf(new PreferenceManager(requireActivity()).getInt(Constant.USER_ID)));
                params.put(Constant.q_id, learningQuestionsNew.getQuestion_id());
                params.put(Constant.CASE, "I");
                params.put(Constant.isLike, String.valueOf(0));
                params.put(Constant.CALL_FROM, "Like");
                processQuestionAPI(params);
                Glide.with(requireActivity()).load(requireActivity().getResources().getDrawable(R.drawable.ic_like)).into(mBinding.like);
                mBinding.likeValue.setText(likes == 0 ? "0" : likes - 1 + "");
            } else {
               // onIconClick.onLikeClick(learningQuestions.getQuestion_id(), 1);
                Glide.with(requireActivity()).load(requireActivity().getResources().getDrawable(R.drawable.ic_thumb_up_black_24dp)).into(mBinding.like);
                mBinding.likeValue.setText(likes + 1 + "");
            }
        });

        mBinding.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //LearningQuestionsNew learningQuestions = learningQuestionsList.get(getAdapterPosition());
                //onIconClick.onShareClick(learningQuestions.getQuestion_id());
            }
        });

        mBinding.favorite.setOnClickListener(v -> {
            //LearningQuestionsNew learningQuestions = learningQuestionsList.get(getAdapterPosition());
            if (learningQuestionsNew.getIs_fav().equalsIgnoreCase("true")) {
               //onIconClick.onFavouriteClick(learningQuestionsNew.getQuestion_id(), 0);
                Glide.with(requireActivity()).load(requireActivity().getResources().getDrawable(R.drawable.ic_fav)).into(mBinding.favorite);
            } else {
                //onIconClick.onFavouriteClick(learningQuestionsNew.getQuestion_id(), 1);
                Glide.with(requireActivity()).load(requireActivity().getResources().getDrawable(R.drawable.ic_favorite_black_24dp)).into(mBinding.favorite);
            }
        });


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

    private void processQuestionAPI(HashMap<String, String> params) {
        Log.d(TAG, "processQuestionAPI Params : " + params);
        ProgressDialog.getInstance().show(getActivity());
        Call<ProcessQuestion> call;

        if (params.get(Constant.CALL_FROM).equalsIgnoreCase("Like"))
            call = apiService.likeApi(Integer.parseInt(params.get(Constant.u_user_id)), params.get(Constant.q_id),
                    params.get(Constant.CASE), Integer.parseInt(params.get(Constant.isLike)));
        else
            call = apiService.favApi(Integer.parseInt(params.get(Constant.u_user_id)), params.get(Constant.q_id),
                    params.get(Constant.CASE), Integer.parseInt(params.get(Constant.isLike)));

        call.enqueue(new Callback<ProcessQuestion>() {
            @Override
            public void onResponse(Call<ProcessQuestion> call, retrofit2.Response<ProcessQuestion> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.body() != null && response.body().getResponse().equalsIgnoreCase("200")) {
                } else {
                    Toast.makeText(getActivity(), UtilHelper.getAPIError(String.valueOf(response.body())), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProcessQuestion> call, Throwable t) {
                t.printStackTrace();
                ProgressDialog.getInstance().dismiss();
            }
        });
    }
}
