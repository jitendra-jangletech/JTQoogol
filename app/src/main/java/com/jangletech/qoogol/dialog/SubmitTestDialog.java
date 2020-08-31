package com.jangletech.qoogol.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.RatingBar;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.activities.PracticeTestActivity;
import com.jangletech.qoogol.databinding.DialogSubmitTestBinding;
import com.jangletech.qoogol.model.ProcessQuestion;
import com.jangletech.qoogol.model.StartResumeTestResponse;
import com.jangletech.qoogol.model.TestQuestionNew;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.util.AppUtils;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class SubmitTestDialog extends Dialog {

    private static final String TAG = "SubmitTestDialog";
    private DialogSubmitTestBinding mBinding;
    private SubmitDialogClickListener submitDialogClickListener;
    private ApiInterface apiService = ApiClient.getInstance().getApi();
    private StartResumeTestResponse startResumeTestResponse;
    private List<TestQuestionNew> questionList;
    private String testRating = "", testFeedback = "";
    private HashMap<String, String> params = new HashMap<>();
    private Activity activity;
    private int totalQuest, wrongQuest, unAttemptedQuest, markedQuest, otherQuest, rightQuest;

    public SubmitTestDialog(@NonNull Context context, Activity activity, SubmitDialogClickListener submitDialogClickListener, StartResumeTestResponse startResumeTestResponse) {
        super(context, android.R.style.Theme_DeviceDefault_Light_DarkActionBar);
        this.activity = activity;
        this.submitDialogClickListener = submitDialogClickListener;
        this.startResumeTestResponse = startResumeTestResponse;
        this.questionList = PracticeTestActivity.questionsNewList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_submit_test, null, false);
        setContentView(mBinding.getRoot());
        String userId = AppUtils.getStringField(new PreferenceManager(getContext()).getInt(Constant.USER_ID));
        params.put(Constant.tt_id, String.valueOf(startResumeTestResponse.getTtId()));
        params.put(Constant.u_user_id, userId);
        params.put(Constant.CASE, "L");
        submitTestFeedBack(params);

        mBinding.tvYes.setOnClickListener(v -> {
            //submitDialogClickListener.onYesClick();
            testFeedback = mBinding.feedback.getText().toString();
            params.put(Constant.tlc_rating, testRating);
            params.put(Constant.tlc_feedback, AppUtils.encodedString(testFeedback));
            params.put(Constant.CASE, "I");
            submitTestFeedBack(params);
        });

        mBinding.rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                testRating = String.valueOf(rating);
            }
        });

        mBinding.tvNo.setOnClickListener(v -> {
            dismiss();
            submitDialogClickListener.onNoClick();
        });

        mBinding.wrongLayout.setOnClickListener(v -> {
            submitDialogClickListener.onWrongClick();
            dismiss();
        });

        mBinding.unattemptedLayout.setOnClickListener(v -> {
            submitDialogClickListener.onUnAttemptedClick();
            dismiss();
        });

        mBinding.markedLayout.setOnClickListener(v -> {
            submitDialogClickListener.onMarkedClick();
            dismiss();
        });

        setQuestCounts();
    }

    private void setQuestCounts() {
        double totalMarks = 0;
        double obtainMarks = 0;
        totalQuest = questionList.size();
        markedQuest = 0;
        wrongQuest = 0;
        unAttemptedQuest = 0;

        for (TestQuestionNew testQuestionNew : questionList) {
            totalMarks = totalMarks + testQuestionNew.getTq_marks();
            if (testQuestionNew.isTtqa_attempted()) {
                if (testQuestionNew.getType().equalsIgnoreCase(Constant.SHORT_ANSWER) ||
                        testQuestionNew.getType().equalsIgnoreCase(Constant.LONG_ANSWER)) {
                    if (!testQuestionNew.isRightAnswered()) {
                        wrongQuest++;
                    } else {
                        //right answer
                        rightQuest++;
                        obtainMarks = obtainMarks + testQuestionNew.getTq_marks();
                    }
                } else {
                    //Questions other than subjective
                    if (testQuestionNew.getA_sub_ans().equalsIgnoreCase(testQuestionNew.getTtqa_sub_ans())) {
                        //Right answer
                        rightQuest++;
                        obtainMarks = obtainMarks + testQuestionNew.getTq_marks();
                    } else {
                        wrongQuest++;
                    }
                }

            } else {
                if (testQuestionNew.isTtqa_visited()) {
                    unAttemptedQuest++;
                } else {
                    otherQuest++;
                }
            }
            if (testQuestionNew.isTtqa_marked()) {
                markedQuest++;
            }
        }

        //int totalCount = markedQuest + wrongQuest + unAttemptedQuest + otherQuest;
        //Log.d(TAG, "Total Quest Count : " + totalCount);

        Log.d(TAG, "setQuestCounts Wrong : " + wrongQuest);
        Log.d(TAG, "setQuestCounts UnAttempted : " + unAttemptedQuest);
        Log.d(TAG, "setQuestCounts Marked : " + markedQuest);
        Log.d(TAG, "Total Quest Count : " + otherQuest);

        //Dialog Title Test name
        mBinding.tvTitle.setText(startResumeTestResponse.getTm_name());

        Log.d(TAG, "setQuestCounts Total Marks : " + startResumeTestResponse.getTm_tot_marks());
        //set obtain Marks & Total Marks
        mBinding.tvTotalMarksValue.setText(startResumeTestResponse.getTm_tot_marks());
        mBinding.tvObtainMarksValue.setText(String.valueOf(obtainMarks));

        mBinding.tvWrongCount.setText(Html.fromHtml("<u>" + String.valueOf(wrongQuest) + "</u>"));
        mBinding.tvRightCount.setText(Html.fromHtml("<u>" + String.valueOf(rightQuest) + "</u>"));
        mBinding.tvUnAttemptedCount.setText(Html.fromHtml("<u>" + String.valueOf(unAttemptedQuest) + "</u>"));
        mBinding.tvMarkedCount.setText(Html.fromHtml("<u>" + String.valueOf(markedQuest) + "</u>"));
        mBinding.tvOthersCount.setText(Html.fromHtml("<u>" + String.valueOf(otherQuest) + "</u>"));

    }

    private void submitTestFeedBack(HashMap<String, String> params) {
        ProgressDialog.getInstance().show(activity);
        Log.d(TAG, "submitTestFeedBack Params : " + params);
        Call<ProcessQuestion> call = apiService.submitTestFeedBack(
                params.get(Constant.u_user_id),
                params.get(Constant.tlc_rating),
                params.get(Constant.tlc_feedback),
                params.get(Constant.tm_id),
                params.get(Constant.CASE)
        );
        call.enqueue(new Callback<ProcessQuestion>() {
            @Override
            public void onResponse(Call<ProcessQuestion> call, retrofit2.Response<ProcessQuestion> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.body().getResponse().equals("200")) {
                    Log.d(TAG, "onResponse Success : ");
                    if (params.get(Constant.CASE).equalsIgnoreCase("I")) {
                        dismiss();
                        AppUtils.showToast(activity, "Feedback Submitted Successfully.");
                        submitDialogClickListener.onYesClick();
                    }
                } else {
                    //todo set rating and feedback test
                }
            }

            @Override
            public void onFailure(Call<ProcessQuestion> call, Throwable t) {
                t.printStackTrace();
                ProgressDialog.getInstance().dismiss();
            }
        });
    }


    public interface SubmitDialogClickListener {
        void onYesClick();

        void onNoClick();

        void onWrongClick();

        void onUnAttemptedClick();

        void onMarkedClick();
    }
}
