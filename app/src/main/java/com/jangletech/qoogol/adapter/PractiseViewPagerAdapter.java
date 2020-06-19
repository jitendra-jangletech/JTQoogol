package com.jangletech.qoogol.adapter;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Vibrator;
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

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.activities.PracticeTestActivity;
import com.jangletech.qoogol.dialog.CommentDialog;
import com.jangletech.qoogol.dialog.LikeListingDialog;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.dialog.PublicProfileDialog;
import com.jangletech.qoogol.model.ProcessQuestion;
import com.jangletech.qoogol.model.SubmitTest;
import com.jangletech.qoogol.model.TestQuestionNew;
import com.jangletech.qoogol.model.VerifyResponse;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;
import com.jangletech.qoogol.util.UtilHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.github.kexanie.library.MathView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.facebook.FacebookSdk.getApplicationContext;

public class PractiseViewPagerAdapter extends PagerAdapter implements LikeListingDialog.onItemClickListener, CommentDialog.CommentClickListener {
    private static final String TAG = "PractiseViewPagerAdapte";
    private Activity context;
    private List<TestQuestionNew> testQuestionNewList;
    private ViewPagerClickListener viewPagerClickListener;
    private CommentDialog commentDialog;
    private PublicProfileDialog publicProfileDialog;
    private ApiInterface apiService = ApiClient.getInstance().getApi();
    private int isSolvedRight, isAttempted = 0;
    private CountDownTimer countDownTimer;
    private String tmId;
    private Gson gson;
    private String flag;
    public static String scq_ans = "", scqimgtext_ans = "";

    public PractiseViewPagerAdapter(Activity context, List<TestQuestionNew> testQuestionNewList,
                                    ViewPagerClickListener viewPagerClickListener, String tmId, String flag) {
        this.context = context;
        this.testQuestionNewList = PracticeTestActivity.questionsNewList;
        this.viewPagerClickListener = viewPagerClickListener;
        this.tmId = tmId;
        this.flag = flag;
        gson = new Gson();
    }

    @Override
    public Object instantiateItem(@NonNull ViewGroup collection, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        Log.d(TAG, "instantiateItem Position : " + position);
        TestQuestionNew testQuestionNew = testQuestionNewList.get(position);
        if (testQuestionNew.getType().equalsIgnoreCase(Constant.LONG_ANSWER) ||
                testQuestionNew.getType().equalsIgnoreCase(Constant.SHORT_ANSWER) ||
                testQuestionNew.getType().equalsIgnoreCase(Constant.FILL_THE_BLANKS) ||
                testQuestionNew.getType().equalsIgnoreCase(Constant.ONE_LINE_ANSWER)) {
            ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.practice_subjective, collection, false);
            initViews(layout, testQuestionNew, position);
            initSubjective(layout, testQuestionNew);
            collection.addView(layout);
            layout.setTag(position);
            return layout;
        } else {

            if (testQuestionNew.getQue_option_type().equalsIgnoreCase(Constant.SCQ)) {
                ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.practice_scq, collection, false);
                initViews(layout, testQuestionNew, position);
                initScq(layout, testQuestionNew);
                collection.addView(layout);
                layout.setTag(position);
                return layout;
            }

            if (testQuestionNew.getQue_option_type().equalsIgnoreCase(Constant.SCQ_IMAGE_WITH_TEXT)) {
                ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.practice_scq_image_text, collection, false);
                initViews(layout, testQuestionNew, position);
                initScqImageText(layout, testQuestionNew);
                collection.addView(layout);
                layout.setTag(position);
                return layout;
            }

            if (testQuestionNew.getQue_option_type().equalsIgnoreCase(Constant.MCQ)) {
                ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.practice_mcq, collection, false);
                initViews(layout, testQuestionNew, position);
                collection.addView(layout);
                layout.setTag(position);
                return layout;
            }
        }
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
        return this.testQuestionNewList.size();
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

    private void initScq(ViewGroup layout, TestQuestionNew testQuestionNew) {
        Log.d(TAG, "initScq Item Position : " + getItemPosition(testQuestionNew));
       /* TextView scq1 = layout.findViewById(R.id.scq1);
        TextView scq2 = layout.findViewById(R.id.scq2);
        TextView scq3 = layout.findViewById(R.id.scq3);
        TextView scq4 = layout.findViewById(R.id.scq4);
        TextView scq5 = layout.findViewById(R.id.scq5);*/


        MathView scq1 = layout.findViewById(R.id.scq1);
        MathView scq2 = layout.findViewById(R.id.scq2);
        MathView scq3 = layout.findViewById(R.id.scq3);
        MathView scq4 = layout.findViewById(R.id.scq4);
        MathView scq5 = layout.findViewById(R.id.scq5);

        TextView solutionAns = layout.findViewById(R.id.solution_option);

        ImageView img1 = layout.findViewById(R.id.scq1_img);
        ImageView img2 = layout.findViewById(R.id.scq2_img);
        ImageView img3 = layout.findViewById(R.id.scq3_img);
        ImageView img4 = layout.findViewById(R.id.scq4_img);
        ImageView img5 = layout.findViewById(R.id.scq5_img);

        Button btnSubmit = layout.findViewById(R.id.submit);

        ConstraintLayout scq1Layout = layout.findViewById(R.id.scq1_layout);
        ConstraintLayout scq2Layout = layout.findViewById(R.id.scq2_layout);
        ConstraintLayout scq3Layout = layout.findViewById(R.id.scq3_layout);
        ConstraintLayout scq4Layout = layout.findViewById(R.id.scq4_layout);
        ConstraintLayout scq5Layout = layout.findViewById(R.id.scq5_layout);

        ConstraintLayout solutionLayout = layout.findViewById(R.id.solution_layout);

        scq1.setText(testQuestionNew.getQ_mcq_op_1());
        scq2.setText(testQuestionNew.getQ_mcq_op_2());
        scq3.setText(testQuestionNew.getQ_mcq_op_3());
        scq4.setText(testQuestionNew.getQ_mcq_op_4());
        scq5.setText(testQuestionNew.getQ_mcq_op_5());

        if (testQuestionNew.getQ_mcq_op_5() != null && testQuestionNew.getQ_mcq_op_5().isEmpty()) {
            scq5Layout.setVisibility(View.GONE);
        }

        solutionAns.setText(testQuestionNew.getA_sub_ans());

        if (flag.equalsIgnoreCase("ATTEMPTED")) {
            solutionLayout.setVisibility(View.VISIBLE);
            //btnSubmit.setEnabled(false);
            //btnSubmit.setAlpha(0.5f);

            if (testQuestionNew.isTtqa_mcq_ans_1()) {
                img1.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wrong));
                img1.setVisibility(View.VISIBLE);
            }
            if (testQuestionNew.isTtqa_mcq_ans_2()) {
                img2.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wrong));
                img2.setVisibility(View.VISIBLE);
            }
            if (testQuestionNew.isTtqa_mcq_ans_3()) {
                img3.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wrong));
                img3.setVisibility(View.VISIBLE);
            }
            if (testQuestionNew.isTtqa_mcq_ans_4()) {
                img4.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wrong));
                img4.setVisibility(View.VISIBLE);
            }
            if (testQuestionNew.isTtqa_mcq_ans_5()) {
                img5.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wrong));
                img5.setVisibility(View.VISIBLE);
            }

            if (testQuestionNew.getA_sub_ans().equalsIgnoreCase("A")) {
                img1.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_right));
                img1.setVisibility(View.VISIBLE);
            } else if (testQuestionNew.getA_sub_ans().equalsIgnoreCase("B")) {
                img2.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_right));
                img2.setVisibility(View.VISIBLE);
            } else if (testQuestionNew.getA_sub_ans().equalsIgnoreCase("C")) {
                img3.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_right));
                img3.setVisibility(View.VISIBLE);
            } else if (testQuestionNew.getA_sub_ans().equalsIgnoreCase("D")) {
                img4.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_right));
                img4.setVisibility(View.VISIBLE);
            } else if (testQuestionNew.getA_sub_ans().equalsIgnoreCase("E")) {
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

        btnSubmit.setOnClickListener(v -> {
            if (!scq_ans.trim().equalsIgnoreCase("")) {
                isAttempted = 1;
                setSCQAnsIndicator(img1, img2, img3, img4);
                Log.d(TAG, "scq_ans : " + scq_ans);
                Log.d(TAG, "getTtqa_sub_ans : " + testQuestionNew.getTtqa_sub_ans());
                Log.d(TAG, "initScq: " + scq_ans.equalsIgnoreCase(testQuestionNew.getTtqa_sub_ans()));
                if (scq_ans.equalsIgnoreCase(testQuestionNew.getA_sub_ans())) {
                    rightAnswerFeebBack();
                    testQuestionNew.setAnsweredRight(true);
                    setRightSCQ(scq_ans, img1, img2, img3, img4, img5);
                } else {
                    testQuestionNew.setAnsweredRight(false);
                    isSolvedRight = 0;
                    setRightSCQ(testQuestionNew.getA_sub_ans(), img1, img2, img3, img4, img5);
                    setWrongSCQ(scq_ans, img1, img2, img3, img4, img5);
                }
                solutionLayout.setVisibility(View.VISIBLE);

                //disable answer selection
                scq1Layout.setEnabled(false);
                scq2Layout.setEnabled(false);
                scq3Layout.setEnabled(false);
                scq4Layout.setEnabled(false);
                btnSubmit.setEnabled(false);

                submitSubjectiveAnsToServer(testQuestionNew, scq_ans, "SCQ");
            } else {
                showToast("Please select atleast one option.");
            }
        });
    }

    private void submitSubjectiveAnsToServer(TestQuestionNew question, String scq_ans, String flag) {
        SubmitTest submitTest = new SubmitTest();
        List<TestQuestionNew> submitTestQuestionList = new ArrayList<>();
        submitTest.setTm_id(tmId);
        if (flag.equalsIgnoreCase("SCQ")) {
            Log.d(TAG, "submitSubjectiveAnsToServer SCQ Ans : " + scq_ans);
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

            if (scq_ans.equalsIgnoreCase("E"))
                question.setTtqa_mcq_ans_5(true);
        }

        question.setTtqa_attempted(true);
        question.setTtqa_visited(false);

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

    public void setRightSCQ(String option, ImageView scq1Img, ImageView scq2Img, ImageView scq3Img, ImageView scq4Img, ImageView scq5Img) {
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
                case "E":
                    scq5Img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_right));
                    scq5Img.setVisibility(View.VISIBLE);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initScqImageText(ViewGroup layout, TestQuestionNew testQuestionNew) {

        ImageView scqImgtextImg1 = layout.findViewById(R.id.scq_imgtext_img1);
        ImageView scqImgtextImg2 = layout.findViewById(R.id.scq_imgtext_img2);
        ImageView scqImgtextImg3 = layout.findViewById(R.id.scq_imgtext_img3);
        ImageView scqImgtextImg4 = layout.findViewById(R.id.scq_imgtext_img4);

        ImageView scqimgImgtextChck1 = layout.findViewById(R.id.scqimg_imgtext_chck1);
        ImageView scqimgImgtextChck2 = layout.findViewById(R.id.scqimg_imgtext_chck2);
        ImageView scqimgImgtextChck3 = layout.findViewById(R.id.scqimg_imgtext_chck3);
        ImageView scqimgImgtextChck4 = layout.findViewById(R.id.scqimg_imgtext_chck4);

        TextView scqText1 = layout.findViewById(R.id.scq_imgtext_text1);
        TextView scqText2 = layout.findViewById(R.id.scq_imgtext_text2);
        TextView scqText3 = layout.findViewById(R.id.scq_imgtext_text3);
        TextView scqText4 = layout.findViewById(R.id.scq_imgtext_text4);

        TextView solAns = layout.findViewById(R.id.solution_option);
        Button submit = layout.findViewById(R.id.submit);

        ConstraintLayout solutionLayout = layout.findViewById(R.id.solution_layout);

        loadImage(testQuestionNew.getQ_mcq_op_1().split(":")[0], scqImgtextImg1);
        loadImage(testQuestionNew.getQ_mcq_op_2().split(":")[0], scqImgtextImg2);
        loadImage(testQuestionNew.getQ_mcq_op_3().split(":")[0], scqImgtextImg3);
        loadImage(testQuestionNew.getQ_mcq_op_4().split(":")[0], scqImgtextImg4);

        scqText1.setText(testQuestionNew.getQ_mcq_op_1().split(":")[1]);
        scqText2.setText(testQuestionNew.getQ_mcq_op_2().split(":")[1]);
        scqText3.setText(testQuestionNew.getQ_mcq_op_3().split(":")[1]);
        scqText4.setText(testQuestionNew.getQ_mcq_op_4().split(":")[1]);

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

        submit.setOnClickListener(v -> {
            if (!scqimgtext_ans.trim().equalsIgnoreCase("")) {
                isAttempted = 1;
                setSCQImgTextAnsIndicator(scqimgImgtextChck1, scqimgImgtextChck2, scqimgImgtextChck3, scqimgImgtextChck4);
                if (scqimgtext_ans.equalsIgnoreCase(testQuestionNew.getA_sub_ans())) {
                    rightAnswerFeebBack();
                    testQuestionNew.setAnsweredRight(true);
                    setRightSCQImgText(scqimgtext_ans, scqimgImgtextChck1, scqimgImgtextChck2, scqimgImgtextChck3, scqimgImgtextChck4);
                } else {
                    isSolvedRight = 0;
                    wrongAnswerFeebBack();
                    testQuestionNew.setAnsweredRight(false);
                    setRightSCQImgText(testQuestionNew.getA_sub_ans(), scqimgImgtextChck1, scqimgImgtextChck2, scqimgImgtextChck3, scqimgImgtextChck4);
                    setWrongSCQImgText(scqimgtext_ans, scqimgImgtextChck1, scqimgImgtextChck2, scqimgImgtextChck3, scqimgImgtextChck4);
                }
                solutionLayout.setVisibility(View.VISIBLE);
                solAns.setText(testQuestionNew.getA_sub_ans());

                //Disable answer selection
                scqImgtextImg1.setEnabled(false);
                scqImgtextImg2.setEnabled(false);
                scqImgtextImg3.setEnabled(false);
                scqImgtextImg4.setEnabled(false);
                submit.setEnabled(false);

            } else {
                showToast("Please select atleast one option.");
                //Toast.makeText(activity, "Please select atleast one option.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initSubjective(ViewGroup layout, TestQuestionNew testQuestionNew) {
        TextView tvWordCounter = layout.findViewById(R.id.tvWordCounter);
        TextView multi_lineCounter = layout.findViewById(R.id.multi_lineCounter);
        TextView solutionDesc = layout.findViewById(R.id.solution_option);
        Button btnSubmit = layout.findViewById(R.id.submit);
        ConstraintLayout multiLineAnswerLayout = layout.findViewById(R.id.multi_line_answer);
        ConstraintLayout fillTheBlanksLayout = layout.findViewById(R.id.fill_in_the_blanks_layout);
        ConstraintLayout solutionLayout = layout.findViewById(R.id.solution_layout);

        EditText etMultiLineAns = layout.findViewById(R.id.multi_line);
        EditText etSinlgeineAns = layout.findViewById(R.id.fill_in_the_blanks);

        solutionDesc.setText(testQuestionNew.getA_sub_ans());

        if (testQuestionNew.getType().equalsIgnoreCase(Constant.ONE_LINE_ANSWER) ||
                testQuestionNew.getType().equalsIgnoreCase(Constant.FILL_THE_BLANKS)) {
            fillTheBlanksLayout.setVisibility(View.VISIBLE);
            //etSinlgeineAns.setText(testQuestionNew.getTtqa_sub_ans());
        }
        if (testQuestionNew.getType().equalsIgnoreCase(Constant.SHORT_ANSWER) ||
                testQuestionNew.getType().equalsIgnoreCase(Constant.LONG_ANSWER)) {
            multiLineAnswerLayout.setVisibility(View.VISIBLE);
            answerCharCounter(etMultiLineAns, tvWordCounter, 200, testQuestionNew);
            //etMultiLineAns.setText(testQuestionNew.getTtqa_sub_ans());
        }

        if (flag.equalsIgnoreCase("ATTEMPTED")) {
            solutionLayout.setVisibility(View.VISIBLE);
            etSinlgeineAns.setText(testQuestionNew.getTtqa_sub_ans());
            etMultiLineAns.setText(testQuestionNew.getTtqa_sub_ans());
            //btnSubmit.setEnabled(false);
            //btnSubmit.setAlpha(0.5f);
        }

        btnSubmit.setOnClickListener(v -> {
            if (multiLineAnswerLayout.getVisibility() == View.VISIBLE) {
                if (etMultiLineAns.getText().toString().isEmpty()) {
                    showToast("Please Enter Answer");
                } else {
                    if (etMultiLineAns.getText().toString().equalsIgnoreCase(testQuestionNew.getA_sub_ans())) {
                        testQuestionNew.setAnsweredRight(true);
                    } else {
                        testQuestionNew.setAnsweredRight(false);
                    }
                    solutionLayout.setVisibility(View.VISIBLE);
                    testQuestionNew.setTtqa_saved(false);
                    testQuestionNew.setTtqa_marked(false);
                    testQuestionNew.setTtqa_attempted(true);
                    testQuestionNew.setTtqa_sub_ans(etMultiLineAns.getText().toString());
                    submitSubjectiveAnsToServer(testQuestionNew, "", "SUBJECTIVE");
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

    private void initViews(ViewGroup layout, TestQuestionNew testQuestionNew, int pos) {
        TextView tvQuestNo = layout.findViewById(R.id.id_textview);
        TextView tvQuestion = layout.findViewById(R.id.question_textview);
        TextView tvMarks = layout.findViewById(R.id.marks_textview);
        ImageView imgComment = layout.findViewById(R.id.comment);
        ImageView imgQueDownload = layout.findViewById(R.id.save_que);
        TextView tvDiffLevel = layout.findViewById(R.id.difflevel_value);
        //TextView tvPostedValue = layout.findViewById(R.id.posted_value);
        TextView tvAttemptedValue = layout.findViewById(R.id.attempted_value);
        //TextView tvLastUsedValue = layout.findViewById(R.id.last_used_value);
        TextView tvRatingValue = layout.findViewById(R.id.ratingvalue);
        TextView tvLikeValue = layout.findViewById(R.id.like_value);
        TextView tvCommentValue = layout.findViewById(R.id.comment_value);
        TextView tvShareValue = layout.findViewById(R.id.share_value);

        //Toggle Buttons
        ToggleButton favorite = layout.findViewById(R.id.favorite);
        ToggleButton like = layout.findViewById(R.id.like);
        ToggleButton mark = layout.findViewById(R.id.markToggle);

        //set the values
        tvQuestion.setText(testQuestionNew.getQ_quest());
        tvQuestNo.setText(String.valueOf(testQuestionNew.getTq_quest_seq_num()));
        tvMarks.setText("Marks : " + testQuestionNew.getTq_marks());
        tvDiffLevel.setText(testQuestionNew.getQ_diff_level());
        tvAttemptedValue.setText(testQuestionNew.getAttended_by());
        tvRatingValue.setText(testQuestionNew.getRating());
        tvLikeValue.setText(testQuestionNew.getLikes());
        tvCommentValue.setText(testQuestionNew.getComments());
        tvShareValue.setText(testQuestionNew.getShares());

        if (favorite != null)
            favorite.setChecked(testQuestionNew.isQlc_fav_flag() ? true : false);
        if (like != null)
            like.setChecked(testQuestionNew.isQlc_like_flag() ? true : false);
        if (mark != null)
            mark.setChecked(testQuestionNew.isTtqa_marked() ? true : false);

        tvLikeValue.setOnClickListener(v -> {
            if (!testQuestionNew.getLikes().equalsIgnoreCase("0")) {
                LikeListingDialog listingDialog = new LikeListingDialog(context, testQuestionNew.getTq_q_id(), this);
                listingDialog.show();
            }
        });

        imgQueDownload.setOnClickListener(v -> {
            showToast("Added to saved questions List.");
        });

        imgComment.setOnClickListener(v -> {
            commentDialog = new CommentDialog(context, testQuestionNew, this);
            commentDialog.show();
        });

        if (mark != null)
            mark.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        testQuestionNew.setTtqa_marked(true);
                    } else {
                        testQuestionNew.setTtqa_marked(false);
                    }
                    viewPagerClickListener.onMarkQuestion(pos);
                }
            });

        if (favorite != null)
            favorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    ProcessQuestionAPI(testQuestionNew.getTq_q_id(), isChecked ? 1 : 0, "fav", 0);
                }
            });

        if (like != null)
            like.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int prevLikeCount = Integer.parseInt(tvLikeValue.getText().toString());
                    ProcessQuestionAPI(testQuestionNew.getTq_q_id(), isChecked ? 1 : 0, "like", prevLikeCount);
                }
            });
    }

    private void rightAnswerFeebBack() {
        MediaPlayer mp = MediaPlayer.create(context, R.raw.right_ans);
        mp.start();
    }

    private void wrongAnswerFeebBack() {
        Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibe.vibrate(100);
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

    public void setWrongSCQ(String option, ImageView scq1Img, ImageView scq2Img, ImageView scq3Img, ImageView scq4Img, ImageView scq5Img) {
        try {
            switch (option) {
                case "A":
                    scq1Img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wrong));
                    scq1Img.setVisibility(View.VISIBLE);
                    wrongAnswerFeebBack();
                    break;
                case "B":
                    scq2Img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wrong));
                    scq2Img.setVisibility(View.VISIBLE);
                    wrongAnswerFeebBack();
                    break;
                case "C":
                    scq3Img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wrong));
                    scq3Img.setVisibility(View.VISIBLE);
                    wrongAnswerFeebBack();
                    break;
                case "D":
                    scq4Img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wrong));
                    scq4Img.setVisibility(View.VISIBLE);
                    wrongAnswerFeebBack();
                    break;
                case "E":
                    scq5Img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wrong));
                    scq5Img.setVisibility(View.VISIBLE);
                    wrongAnswerFeebBack();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemCLick(String user_id) {
        publicProfileDialog = new PublicProfileDialog(context, user_id);
        publicProfileDialog.show();
    }

    @Override
    public void onCommentClick(String userId) {
        publicProfileDialog = new PublicProfileDialog(context, userId);
        publicProfileDialog.show();
    }

    private void loadImage(String img, ImageView imageView) {
        String fileName = img.substring(img.lastIndexOf('/') + 1);
        File file = new File(UtilHelper.getDirectory(getApplicationContext()), fileName);
        if (file.exists()) {
            Glide.with(context).load(file).into(imageView);
        } else {
            Glide.with(context).load(R.drawable.profile).into(imageView);
        }
    }

    public interface ViewPagerClickListener {
        void onLikeClick(boolean isChecked, int likeCount);

        void onCommentClick();

        void onShareClick();

        void onSubmitClick();

        void onMarkQuestion(int pos);

        void onFavouriteClick(boolean isChecked);
    }
}
