package com.jangletech.qoogol.adapter;

import android.app.Activity;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.google.gson.Gson;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.activities.PracticeTestActivity;
import com.jangletech.qoogol.dialog.CommentDialog;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.model.ProcessQuestion;
import com.jangletech.qoogol.model.SubmitTest;
import com.jangletech.qoogol.model.TestQuestionNew;
import com.jangletech.qoogol.model.VerifyResponse;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;
import com.jangletech.qoogol.util.UtilHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PractiseViewPagerAdapter extends PagerAdapter {
    private static final String TAG = "PractiseViewPagerAdapte";
    private Activity context;
    private List<TestQuestionNew> testModelNewList;
    private ViewPagerClickListener viewPagerClickListener;
    private CommentDialog commentDialog;
    private ApiInterface apiService = ApiClient.getInstance().getApi();
    private int isSolvedRight, isAttempted = 0;
    private CountDownTimer countDownTimer;
    private String tmId;
    private Gson gson;
    private String flag;
    public static String scq_ans = "", scqimgtext_ans = "";

    public PractiseViewPagerAdapter(Activity context, List<TestQuestionNew> testModelNewList,
                                    ViewPagerClickListener viewPagerClickListener, String tmId, String flag) {
        this.context = context;
        this.testModelNewList = PracticeTestActivity.questionsNewList;
        this.viewPagerClickListener = viewPagerClickListener;
        this.tmId = tmId;
        this.flag = flag;
        gson = new Gson();
    }

    @Override
    public Object instantiateItem(@NonNull ViewGroup collection, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        Log.d(TAG, "instantiateItem Position : " + position);
        TestQuestionNew testQuestionNew = testModelNewList.get(position);
        if (testQuestionNew.getType().equalsIgnoreCase(Constant.LONG_ANSWER) ||
                testQuestionNew.getType().equalsIgnoreCase(Constant.SHORT_ANSWER) ||
                testQuestionNew.getType().equalsIgnoreCase(Constant.FILL_THE_BLANKS) ||
                testQuestionNew.getType().equalsIgnoreCase(Constant.ONE_LINE_ANSWER)) {
            ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.practice_subjective, collection, false);
            initViews(layout, testQuestionNew);
            initSubjective(layout, testQuestionNew);
            collection.addView(layout);
            layout.setTag(position);
            return layout;
        } else {

            if (testQuestionNew.getQue_option_type().equalsIgnoreCase(Constant.SCQ)) {
                ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.practice_scq, collection, false);
                //learningItemBinding = DataBindingUtil.inflate(inflater, R.layout.practice_scq, collection, false);
                initViews(layout, testQuestionNew);
                initScq(layout, testQuestionNew);
                collection.addView(layout);
                layout.setTag(position);
                return layout;
            }

            if (testQuestionNew.getQue_option_type().equalsIgnoreCase(Constant.SCQ_IMAGE_WITH_TEXT)) {
                ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.practice_scq_image_text, collection, false);
                initViews(layout, testQuestionNew);
                initScqImageText(layout, testQuestionNew);
                collection.addView(layout);
                layout.setTag(position);
                return layout;
            }

            if (testQuestionNew.getQue_option_type().equalsIgnoreCase(Constant.MCQ)) {
                ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.practice_mcq, collection, false);
                initViews(layout, testQuestionNew);
                collection.addView(layout);
                layout.setTag(position);
                return layout;
            }
        }
        /*ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.practice_subjective, collection, false);
        initViews(layout, testQuestionNew);
        collection.addView(layout);*/
        return null;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object view) {
        container.removeView((View) view);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return this.testModelNewList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    private void ProcessQuestionAPI(String que_id, int flag, String call_from, int prevLikes) {
        ProgressDialog.getInstance().show(context);
        Call<ProcessQuestion> call;
        int user_id = new PreferenceManager(context).getInt(Constant.USER_ID);

        if (call_from.equalsIgnoreCase("like"))
            call = apiService.likeApi(user_id, que_id, "I", flag);
        else
            call = apiService.favApi(user_id, que_id, "I", flag);

        call.enqueue(new Callback<ProcessQuestion>() {
            @Override
            public void onResponse(Call<ProcessQuestion> call, retrofit2.Response<ProcessQuestion> response) {
                try {
                    if (response.body() != null && response.body().getResponse().equals("200")) {

                        if (call_from.equalsIgnoreCase("fav")) {
                            if (flag == 1) {
                                showToast("Added to favourites.");
                            } else {
                                showToast("Deleted from favourites.");
                            }
                        }

                        if (call_from.equalsIgnoreCase("like") && flag == 1) {
                            Log.d(TAG, "onResponse Like Flag : 1");
                            int likeCount = prevLikes + 1;
                            viewPagerClickListener.onLikeClick(true, likeCount);
                        } else if (call_from.equalsIgnoreCase("like") && flag == 0) {
                            Log.d(TAG, "onResponse Like Flag : 0");
                            int likeCount = prevLikes - 1;
                            viewPagerClickListener.onLikeClick(false, likeCount);
                        }
                    } else {
                        Toast.makeText(context, UtilHelper.getAPIError(String.valueOf(response.body())), Toast.LENGTH_SHORT).show();
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

    private void setScqRightAns(ImageView img){
        img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_right));
    }

    private void setScqWrongAns(ImageView img){
        img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wrong));
    }

    private void initScq(ViewGroup layout, TestQuestionNew testModelNew) {
        Log.d(TAG, "initScq Item Position : " + getItemPosition(testModelNew));
        TextView scq1 = layout.findViewById(R.id.scq1);
        TextView scq2 = layout.findViewById(R.id.scq2);
        TextView scq3 = layout.findViewById(R.id.scq3);
        TextView scq4 = layout.findViewById(R.id.scq4);
        TextView scq5 = layout.findViewById(R.id.scq5);

        ImageView img1 = layout.findViewById(R.id.scq1_img);
        ImageView img2 = layout.findViewById(R.id.scq2_img);
        ImageView img3 = layout.findViewById(R.id.scq3_img);
        ImageView img4 = layout.findViewById(R.id.scq4_img);
        ImageView img5 = layout.findViewById(R.id.scq5_img);

        ConstraintLayout scq1Layout = layout.findViewById(R.id.scq1_layout);
        ConstraintLayout scq2Layout = layout.findViewById(R.id.scq2_layout);
        ConstraintLayout scq3Layout = layout.findViewById(R.id.scq3_layout);
        ConstraintLayout scq4Layout = layout.findViewById(R.id.scq4_layout);
        ConstraintLayout scq5Layout = layout.findViewById(R.id.scq5_layout);

        ConstraintLayout solutionLayout = layout.findViewById(R.id.solution_layout);

        scq1.setText(testModelNew.getQ_mcq_op_1());
        scq2.setText(testModelNew.getQ_mcq_op_2());
        scq3.setText(testModelNew.getQ_mcq_op_3());
        scq4.setText(testModelNew.getQ_mcq_op_4());
        scq5.setText(testModelNew.getQ_mcq_op_5());

        if (flag.equalsIgnoreCase("ATTEMPTED")) {
            solutionLayout.setVisibility(View.VISIBLE);

            if(testModelNew.isTtqa_mcq_ans_1()){
                img1.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wrong));
                img1.setVisibility(View.VISIBLE);
            }
            if(testModelNew.isTtqa_mcq_ans_2()){
                img2.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wrong));
                img2.setVisibility(View.VISIBLE);
            }
            if(testModelNew.isTtqa_mcq_ans_3()){
                img3.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wrong));
                img3.setVisibility(View.VISIBLE);
            }
            if(testModelNew.isTtqa_mcq_ans_4()){
                img4.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wrong));
                img4.setVisibility(View.VISIBLE);
            }
            if(testModelNew.isTtqa_mcq_ans_5()){
                img5.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wrong));
                img5.setVisibility(View.VISIBLE);
            }


            if (testModelNew.getA_sub_ans().equalsIgnoreCase("A")) {
                img1.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_right));
                img1.setVisibility(View.VISIBLE);
            }
            else if (testModelNew.getA_sub_ans().equalsIgnoreCase("B")) {
                img2.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_right));
                img2.setVisibility(View.VISIBLE);
            }
            else if (testModelNew.getA_sub_ans().equalsIgnoreCase("C")) {
                img3.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_right));
                img3.setVisibility(View.VISIBLE);
            }
            else if (testModelNew.getA_sub_ans().equalsIgnoreCase("D")) {
                img4.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_right));
                img4.setVisibility(View.VISIBLE);
            }
            else if (testModelNew.getA_sub_ans().equalsIgnoreCase("E")) {
                img5.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_right));
                img5.setVisibility(View.VISIBLE);
            }
        }

        scq1Layout.setOnClickListener(v -> {
            setSCQAnsIndicator(img1, img2, img3, img4);
            setLayoutBg(scq1Layout, scq2Layout, scq3Layout, scq4Layout);
            scq_ans = "A";
            //showToast(scq_ans.toString());
            scq1Layout.setBackground(context.getResources().getDrawable(R.drawable.grey_border_grey_bg));
        });

        scq2Layout.setOnClickListener(v -> {
            setLayoutBg(scq1Layout, scq2Layout, scq3Layout, scq4Layout);
            setSCQAnsIndicator(img1, img2, img3, img4);
            scq_ans = "B";
            scq2Layout.setBackground(context.getResources().getDrawable(R.drawable.grey_border_grey_bg));
        });

        scq3Layout.setOnClickListener(v -> {
            setLayoutBg(scq1Layout, scq2Layout, scq3Layout, scq4Layout);
            setSCQAnsIndicator(img1, img2, img3, img4);
            scq_ans = "C";
            scq3Layout.setBackground(context.getResources().getDrawable(R.drawable.grey_border_grey_bg));
        });

        scq4Layout.setOnClickListener(v -> {
            setLayoutBg(scq1Layout, scq2Layout, scq3Layout, scq4Layout);
            setSCQAnsIndicator(img1, img2, img3, img4);
            scq_ans = "D";
            scq4Layout.setBackground(context.getResources().getDrawable(R.drawable.grey_border_grey_bg));
        });

        layout.findViewById(R.id.submit).setOnClickListener(v -> {
            if (!scq_ans.trim().equalsIgnoreCase("")) {
                isAttempted = 1;
                setSCQAnsIndicator(img1, img2, img3, img4);
                Log.d(TAG, "scq_ans : " + scq_ans);
                Log.d(TAG, "getTtqa_sub_ans : " + testModelNew.getTtqa_sub_ans());
                Log.d(TAG, "initScq: " + scq_ans.equalsIgnoreCase(testModelNew.getTtqa_sub_ans()));
                if (scq_ans.equalsIgnoreCase(testModelNew.getA_sub_ans())) {
                    setRightSCQ(scq_ans, img1, img2, img3, img4);
                    if (scq_ans.equalsIgnoreCase("A")) {
                        testModelNew.setTtqa_mcq_ans_1(true);
                        testModelNew.setTtqa_mcq_ans_2(false);
                        testModelNew.setTtqa_mcq_ans_3(false);
                        testModelNew.setTtqa_mcq_ans_4(false);
                    } else if (scq_ans.equalsIgnoreCase("B")) {
                        testModelNew.setTtqa_mcq_ans_2(true);
                        testModelNew.setTtqa_mcq_ans_1(false);
                        testModelNew.setTtqa_mcq_ans_3(false);
                        testModelNew.setTtqa_mcq_ans_4(false);
                    } else if (scq_ans.equalsIgnoreCase("C")) {
                        testModelNew.setTtqa_mcq_ans_3(true);
                        testModelNew.setTtqa_mcq_ans_1(false);
                        testModelNew.setTtqa_mcq_ans_2(false);
                        testModelNew.setTtqa_mcq_ans_4(false);
                    } else {
                        testModelNew.setTtqa_mcq_ans_4(true);
                        testModelNew.setTtqa_mcq_ans_3(false);
                        testModelNew.setTtqa_mcq_ans_1(false);
                        testModelNew.setTtqa_mcq_ans_2(false);
                    }
                } else {
                    isSolvedRight = 0;
                    setRightSCQ(testModelNew.getA_sub_ans(), img1, img2, img3, img4);
                    setWrongSCQ(scq_ans, img1, img2, img3, img4);
                }
                solutionLayout.setVisibility(View.VISIBLE);
                testModelNew.setTtqa_attempted(true);
                submitSubjectiveAnsToServer(testModelNew, scq_ans, "SCQ");
            } else {
                showToast("Please select atleast one option.");
                //Toast.makeText(activity, "Please select atleast one option.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitSubjectiveAnsToServer(TestQuestionNew question, String scq_ans, String flag) {
        SubmitTest submitTest = new SubmitTest();
        List<TestQuestionNew> submitTestQuestionList = new ArrayList<>();
        submitTest.setTm_id(tmId);
        if (flag.equalsIgnoreCase("SCQ")) {
            Log.d(TAG, "submitSubjectiveAnsToServer SCQ Ans : " + scq_ans);
            question.setTtqa_marked(false);
            question.setTtqa_mcq_ans_1(false);
            question.setTtqa_mcq_ans_2(false);
            question.setTtqa_mcq_ans_3(false);
            question.setTtqa_mcq_ans_4(false);
            question.setTtqa_mcq_ans_5(false);

            if (scq_ans.equalsIgnoreCase("A"))
                question.setTtqa_mcq_ans_1(true);

            if (scq_ans.equalsIgnoreCase("B"))
                question.setTtqa_mcq_ans_2(true);

            if (scq_ans.equalsIgnoreCase("C"))
                question.setTtqa_mcq_ans_3(true);

            if (scq_ans.equalsIgnoreCase("D"))
                question.setTtqa_mcq_ans_4(true);
        }

        submitTestQuestionList.add(question);
        submitTest.setTestQuestionNewList(submitTestQuestionList);
        String json = gson.toJson(submitTest);

        Log.d(TAG, "submitSubjectiveAnsToServer JSON : " + json);

        HashMap<String, String> params = new HashMap<>();
        params.put(Constant.u_user_id, String.valueOf(new PreferenceManager(context).getInt(Constant.USER_ID)));
        params.put(Constant.DataList, json);

        Log.d(TAG, "submitSubjectiveAnsToServer Params : " + params);
        ProgressDialog.getInstance().show(context);
        Call<VerifyResponse> call = apiService.submitTestQuestion(
                params.get(Constant.DataList),
                params.get(Constant.u_user_id));
        call.enqueue(new Callback<VerifyResponse>() {
            @Override
            public void onResponse(Call<VerifyResponse> call, Response<VerifyResponse> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.body() != null && response.body().getResponse().equals("200")) {
                    showToast("Answer Submitted");
                    viewPagerClickListener.onSubmitClick();
                } else {
                    Log.e(TAG, "onResponse Error : " + response.body().getResponse());
                }
            }

            @Override
            public void onFailure(Call<VerifyResponse> call, Throwable t) {
                showToast("Something went wrong!!");
                t.printStackTrace();
            }
        });
    }

    public void setRightSCQ(String option, ImageView scq1Img, ImageView scq2Img, ImageView scq3Img, ImageView scq4Img) {
        try {
            Log.d(TAG, "setRightSCQ: " + option);
            switch (option) {
                case "A":
                    scq1Img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_right));
                    scq1Img.setVisibility(View.VISIBLE);
                    break;
                case "B":
                    scq2Img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_right));
                    scq2Img.setVisibility(View.VISIBLE);
                    break;
                case "C":
                    scq3Img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_right));
                    scq3Img.setVisibility(View.VISIBLE);
                    break;
                case "D":
                    scq4Img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_right));
                    scq4Img.setVisibility(View.VISIBLE);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initScqImageText(ViewGroup layout, TestQuestionNew testModelNew) {

        ImageView scqImgtextImg1 = layout.findViewById(R.id.scq_imgtext_img1);
        ImageView scqImgtextImg2 = layout.findViewById(R.id.scq_imgtext_img2);
        ImageView scqImgtextImg3 = layout.findViewById(R.id.scq_imgtext_img3);
        ImageView scqImgtextImg4 = layout.findViewById(R.id.scq_imgtext_img4);

        ImageView scqimgImgtextChck1 = layout.findViewById(R.id.scqimg_imgtext_chck1);
        ImageView scqimgImgtextChck2 = layout.findViewById(R.id.scqimg_imgtext_chck2);
        ImageView scqimgImgtextChck3 = layout.findViewById(R.id.scqimg_imgtext_chck3);
        ImageView scqimgImgtextChck4 = layout.findViewById(R.id.scqimg_imgtext_chck4);

        ConstraintLayout solutionLayout = layout.findViewById(R.id.solution_layout);


        scqImgtextImg1.setOnClickListener(v -> {
            setSCQImgTextAnsIndicator(scqimgImgtextChck1, scqimgImgtextChck2, scqimgImgtextChck3, scqimgImgtextChck4);
            setSCQImgTextLayout(scqImgtextImg1, scqImgtextImg2, scqImgtextImg3, scqImgtextImg4);
            scqimgtext_ans = "A";
            scqImgtextImg1.setAlpha(130);
            scqimgImgtextChck1.setVisibility(View.VISIBLE);
            scqimgImgtextChck1.setImageDrawable(context.getResources().getDrawable(R.drawable.selectmark));
        });

        scqImgtextImg2.setOnClickListener(v -> {
            setSCQImgTextAnsIndicator(scqimgImgtextChck1, scqimgImgtextChck2, scqimgImgtextChck3, scqimgImgtextChck4);
            setSCQImgTextLayout(scqImgtextImg1, scqImgtextImg2, scqImgtextImg3, scqImgtextImg4);
            scqimgtext_ans = "B";
            scqImgtextImg2.setAlpha(130);
            scqimgImgtextChck2.setVisibility(View.VISIBLE);
            scqimgImgtextChck2.setImageDrawable(context.getResources().getDrawable(R.drawable.selectmark));
        });

        scqImgtextImg3.setOnClickListener(v -> {
            setSCQImgTextAnsIndicator(scqimgImgtextChck1, scqimgImgtextChck2, scqimgImgtextChck3, scqimgImgtextChck4);
            setSCQImgTextLayout(scqImgtextImg1, scqImgtextImg2, scqImgtextImg3, scqImgtextImg4);
            scqimgtext_ans = "C";
            scqImgtextImg3.setAlpha(130);
            scqimgImgtextChck3.setVisibility(View.VISIBLE);
            scqimgImgtextChck3.setImageDrawable(context.getResources().getDrawable(R.drawable.selectmark));
        });

        scqImgtextImg4.setOnClickListener(v -> {
            setSCQImgTextAnsIndicator(scqimgImgtextChck1, scqimgImgtextChck2, scqimgImgtextChck3, scqimgImgtextChck4);
            setSCQImgTextLayout(scqImgtextImg1, scqImgtextImg2, scqImgtextImg3, scqImgtextImg4);
            scqimgtext_ans = "D";
            scqImgtextImg4.setAlpha(130);
            scqimgImgtextChck4.setVisibility(View.VISIBLE);
            scqimgImgtextChck4.setImageDrawable(context.getResources().getDrawable(R.drawable.selectmark));
        });

        layout.findViewById(R.id.submit).setOnClickListener(v -> {

            if (!scqimgtext_ans.trim().equalsIgnoreCase("")) {
                isAttempted = 1;
                setSCQImgTextAnsIndicator(scqimgImgtextChck1, scqimgImgtextChck2, scqimgImgtextChck3, scqimgImgtextChck4);
                if (scqimgtext_ans.equalsIgnoreCase(testModelNew.getA_sub_ans())) {
                    setRightSCQImgText(scqimgtext_ans, scqimgImgtextChck1, scqimgImgtextChck2, scqimgImgtextChck3, scqimgImgtextChck4);
                } else {
                    isSolvedRight = 0;
                    setRightSCQImgText(testModelNew.getA_sub_ans(), scqimgImgtextChck1, scqimgImgtextChck2, scqimgImgtextChck3, scqimgImgtextChck4);
                    setWrongSCQImgText(scqimgtext_ans, scqimgImgtextChck1, scqimgImgtextChck2, scqimgImgtextChck3, scqimgImgtextChck4);
                }
                solutionLayout.setVisibility(View.VISIBLE);
            } else {
                showToast("Please select atleast one option.");
                //Toast.makeText(activity, "Please select atleast one option.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initSubjective(ViewGroup layout, TestQuestionNew testModelNew) {
        SubmitTest submitTest = new SubmitTest();
        List<TestQuestionNew> submitTestQuestionList = new ArrayList<>();
        TextView tvWordCounter = layout.findViewById(R.id.tvWordCounter);
        TextView multi_lineCounter = layout.findViewById(R.id.multi_lineCounter);
        Button btnSubmit = layout.findViewById(R.id.submit);
        ConstraintLayout multiLineAnswerLayout = layout.findViewById(R.id.multi_line_answer);
        ConstraintLayout fillTheBlanksLayout = layout.findViewById(R.id.fill_in_the_blanks_layout);

        EditText etMultiLineAns = layout.findViewById(R.id.multi_line);
        EditText etSinlgeineAns = layout.findViewById(R.id.fill_in_the_blanks);

        if (testModelNew.getType().equalsIgnoreCase(Constant.ONE_LINE_ANSWER) ||
                testModelNew.getType().equalsIgnoreCase(Constant.FILL_THE_BLANKS)) {
            fillTheBlanksLayout.setVisibility(View.VISIBLE);
            etSinlgeineAns.setText(testModelNew.getTtqa_sub_ans());
        }
        if (testModelNew.getType().equalsIgnoreCase(Constant.SHORT_ANSWER) ||
                testModelNew.getType().equalsIgnoreCase(Constant.LONG_ANSWER)) {
            multiLineAnswerLayout.setVisibility(View.VISIBLE);
            answerCharCounter(etMultiLineAns, tvWordCounter, 200, testModelNew);
            etMultiLineAns.setText(testModelNew.getTtqa_sub_ans());
        }

        if(flag.equalsIgnoreCase("ATTEMPTED")){

        }

        btnSubmit.setOnClickListener(v -> {
            if (multiLineAnswerLayout.getVisibility() == View.VISIBLE) {
                if (etMultiLineAns.getText().toString().isEmpty()) {
                    showToast("Please Enter Answer");
                } else {
                    testModelNew.setTtqa_saved(false);
                    testModelNew.setTtqa_marked(false);
                    testModelNew.setTtqa_attempted(true);
                    testModelNew.setTtqa_sub_ans(etMultiLineAns.getText().toString());
                    //submitTestQuestionList.add(testModelNew);
                    //submitTest.setTestQuestionNewList(submitTestQuestionList);
                    submitSubjectiveAnsToServer(testModelNew, "", "SUBJECTIVE");
                }
            }
        });
    }

    private void setSCQImgTextAnsIndicator(ImageView scqimgImgtextChck1, ImageView scqimgImgtextChck2,
                                           ImageView scqimgImgtextChck3, ImageView scqimgImgtextChck4) {
        scqimgImgtextChck1.setVisibility(View.GONE);
        scqimgImgtextChck2.setVisibility(View.GONE);
        scqimgImgtextChck3.setVisibility(View.GONE);
        scqimgImgtextChck4.setVisibility(View.GONE);
    }

    private void setSCQImgTextLayout(ImageView scqImgtextImg1, ImageView scqImgtextImg2,
                                     ImageView scqImgtextImg3, ImageView scqImgtextImg4) {
        scqImgtextImg1.setAlpha(255);
        scqImgtextImg2.setAlpha(255);
        scqImgtextImg3.setAlpha(255);
        scqImgtextImg4.setAlpha(255);
    }

    private void showToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public void setWrongSCQImgText(String option, ImageView scqimgImgtextChck1, ImageView scqimgImgtextChck2,
                                   ImageView scqimgImgtextChck3, ImageView scqimgImgtextChck4) {
        switch (option) {
            case "A":
                scqimgImgtextChck1.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                scqimgImgtextChck1.setVisibility(View.VISIBLE);
                break;
            case "B":
                scqimgImgtextChck2.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                scqimgImgtextChck2.setVisibility(View.VISIBLE);
                break;
            case "C":
                scqimgImgtextChck3.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                scqimgImgtextChck3.setVisibility(View.VISIBLE);
                break;
            case "D":
                scqimgImgtextChck4.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                scqimgImgtextChck4.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void setRightSCQImgText(String option, ImageView scqimgImgtextChck1, ImageView scqimgImgtextChck2,
                                   ImageView scqimgImgtextChck3, ImageView scqimgImgtextChck4) {
        switch (option) {
            case "A":
                scqimgImgtextChck1.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_right_mtp));
                scqimgImgtextChck1.setVisibility(View.VISIBLE);
                break;
            case "B":
                scqimgImgtextChck2.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_right_mtp));
                scqimgImgtextChck2.setVisibility(View.VISIBLE);
                break;
            case "C":
                scqimgImgtextChck3.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_right_mtp));
                scqimgImgtextChck3.setVisibility(View.VISIBLE);
                break;
            case "D":
                scqimgImgtextChck4.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_right_mtp));
                scqimgImgtextChck4.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void initViews(ViewGroup layout, TestQuestionNew testModelNew) {
        TextView tvQuestNo = layout.findViewById(R.id.id_textview);
        TextView tvCategory = layout.findViewById(R.id.category_textview);
        TextView tvtimer = layout.findViewById(R.id.tvtimer);
        TextView tvQuestion = layout.findViewById(R.id.question_textview);
        TextView tvMarks = layout.findViewById(R.id.marks_textview);
        ImageView imgComment = layout.findViewById(R.id.comment);
        ImageView imgQueDownload = layout.findViewById(R.id.save_que);
        TextView tvDiffLevel = layout.findViewById(R.id.difflevel_value);
        TextView tvPostedValue = layout.findViewById(R.id.posted_value);
        TextView tvAttemptedValue = layout.findViewById(R.id.attempted_value);
        TextView tvLastUsedValue = layout.findViewById(R.id.last_used_value);
        TextView tvRatingValue = layout.findViewById(R.id.ratingvalue);
        TextView tvLikeValue = layout.findViewById(R.id.like_value);
        TextView tvCommentValue = layout.findViewById(R.id.comment_value);
        TextView tvShareValue = layout.findViewById(R.id.share_value);


        //Toggle Buttons
        ToggleButton favorite = layout.findViewById(R.id.favorite);
        ToggleButton like = layout.findViewById(R.id.like);

        //set the values
        tvQuestion.setText(testModelNew.getQ_quest());
        tvQuestNo.setText(String.valueOf(testModelNew.getTq_quest_seq_num()));
        tvMarks.setText("Marks : " + testModelNew.getTq_marks());
        tvCategory.setText(testModelNew.getTq_quest_catg());
        tvDiffLevel.setText(testModelNew.getQ_diff_level());
        tvPostedValue.setText(testModelNew.getTq_marks());
        tvAttemptedValue.setText(testModelNew.getAttended_by());
        tvLastUsedValue.setText("");
        tvRatingValue.setText(testModelNew.getRating());
        tvLikeValue.setText(testModelNew.getLikes());
        tvCommentValue.setText(testModelNew.getComments());
        tvShareValue.setText(testModelNew.getShares());
        if (favorite != null)
            favorite.setChecked(testModelNew.isQlc_fav_flag() ? true : false);
        if (like != null)
            like.setChecked(testModelNew.isQlc_like_flag() ? true : false);
        setTimer(tvtimer, 0, 0);

        imgQueDownload.setOnClickListener(v -> {
            //todo save offline code
            showToast("Added to saved questions List.");
        });

        imgComment.setOnClickListener(v -> {
            commentDialog = new CommentDialog(context, testModelNew);
            commentDialog.show();
        });

        if (favorite != null)
            favorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    ProcessQuestionAPI(testModelNew.getTq_q_id(), isChecked ? 1 : 0, "fav", 0);
                }
            });

        if (like != null)
            like.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int prevLikeCount = Integer.parseInt(tvLikeValue.getText().toString());
                    ProcessQuestionAPI(testModelNew.getTq_q_id(), isChecked ? 1 : 0, "like", prevLikeCount);
                }
            });


    }

    private void answerCharCounter(EditText etAnswer, TextView tvCounter, int maxWordLength, TestQuestionNew testQuestionNew) {

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
                            testQuestionNew.setTtqa_sub_ans(s.toString());
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

    public void setLayoutBg(ConstraintLayout scq1Layout, ConstraintLayout scq2Layout, ConstraintLayout scq3Layout,
                            ConstraintLayout scq4Layout) {
        scq1Layout.setBackground(context.getResources().getDrawable(R.drawable.grey_round_order));
        scq2Layout.setBackground(context.getResources().getDrawable(R.drawable.grey_round_order));
        scq3Layout.setBackground(context.getResources().getDrawable(R.drawable.grey_round_order));
        scq4Layout.setBackground(context.getResources().getDrawable(R.drawable.grey_round_order));
    }

    private void setSCQAnsIndicator(ImageView scq1Img, ImageView scq2Img, ImageView scq3Img, ImageView scq4Img) {
        scq1Img.setVisibility(View.GONE);
        scq2Img.setVisibility(View.GONE);
        scq3Img.setVisibility(View.GONE);
        scq4Img.setVisibility(View.GONE);
    }

    public void setWrongSCQ(String option, ImageView scq1Img, ImageView scq2Img, ImageView scq3Img, ImageView scq4Img) {
        try {
            switch (option) {
                case "A":
                    scq1Img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wrong));
                    scq1Img.setVisibility(View.VISIBLE);
                    break;
                case "B":
                    scq2Img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wrong));
                    scq2Img.setVisibility(View.VISIBLE);
                    break;
                case "C":
                    scq3Img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wrong));
                    scq3Img.setVisibility(View.VISIBLE);
                    break;
                case "D":
                    scq4Img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wrong));
                    scq4Img.setVisibility(View.VISIBLE);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public interface ViewPagerClickListener {
        void onLikeClick(boolean isChecked, int likeCount);

        void onCommentClick();

        void onShareClick();

        void onSubmitClick();

        void onFavouriteClick(boolean isChecked);
    }
}
