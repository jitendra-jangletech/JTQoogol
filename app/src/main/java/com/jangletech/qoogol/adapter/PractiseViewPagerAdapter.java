package com.jangletech.qoogol.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager.widget.PagerAdapter;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.activities.PracticeTestActivity;
import com.jangletech.qoogol.databinding.PracticeMcqBinding;
import com.jangletech.qoogol.databinding.PracticeMtpBinding;
import com.jangletech.qoogol.databinding.PracticeMtpImageBinding;
import com.jangletech.qoogol.databinding.PracticeScqBinding;
import com.jangletech.qoogol.databinding.PracticeScqImageBinding;
import com.jangletech.qoogol.dialog.CommentDialog;
import com.jangletech.qoogol.dialog.LikeListingDialog;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.dialog.PublicProfileDialog;
import com.jangletech.qoogol.dialog.ShareQuestionDialog;
import com.jangletech.qoogol.dialog.SubjectiveAnsDialog;
import com.jangletech.qoogol.model.ProcessQuestion;
import com.jangletech.qoogol.model.StartResumeTestResponse;
import com.jangletech.qoogol.model.SubmitTest;
import com.jangletech.qoogol.model.TestQuestionNew;
import com.jangletech.qoogol.model.VerifyResponse;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.util.AppUtils;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;
import com.jangletech.qoogol.util.UtilHelper;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.github.kexanie.library.MathView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class PractiseViewPagerAdapter extends PagerAdapter
        implements LikeListingDialog.onItemClickListener,
        CommentDialog.CommentClickListener,
        PublicProfileDialog.PublicProfileClickListener,
        SubjectiveAnsDialog.GetAnsListener
{
    private static final String TAG = "PractiseViewPagerAdapte";
    private Activity context;
    private List<TestQuestionNew> testQuestionNewList;
    private ViewPagerClickListener viewPagerClickListener;
    private CommentDialog commentDialog;
    private PublicProfileDialog publicProfileDialog;
    private ApiInterface apiService = ApiClient.getInstance().getApi();
    private CountDownTimer countDownTimer;
    private Gson gson;
    private String flag;
    private PracticeScqBinding practiceScqBinding;
    private PracticeMtpBinding practiceMtpBinding;
    private PracticeMtpImageBinding practiceMtpImageBinding;
    private PracticeMcqBinding practiceMcqBinding;
    private PracticeScqImageBinding practiceScqImageBinding;
    private StartResumeTestResponse startResumeTestResponse;
    private HashMap<String, String> paired = new HashMap<String, String>();
    private HashMap<String, String> imgpaired = new HashMap<String, String>();

    private HashMap<String, String> initPaired = new HashMap<>();
    private HashMap<String, String> initMtpAns = new HashMap<>();

    private HashMap<String, String> MTP_ans = new HashMap<String, String>();
    private ImageView aMtp1, aMtp2, aMtp3, aMtp4, bMtp1, bMtp2, bMtp3, bMtp4;
    private boolean isB1Selected = false, isB2Selected = false, isB3Selected = false, isB4Selected = false;
    public static String scq_ans = "", scqimgtext_ans = "", scqimg_ans = "", mcq_ans = "";


    public PractiseViewPagerAdapter(Activity context,
                                    ViewPagerClickListener viewPagerClickListener, StartResumeTestResponse startResumeTestResponse, String flag) {
        this.context = context;
        this.testQuestionNewList = PracticeTestActivity.questionsNewList;
        this.viewPagerClickListener = viewPagerClickListener;
        this.startResumeTestResponse = startResumeTestResponse;
        this.flag = flag;
        gson = new Gson();
        Log.d(TAG, "PractiseViewPagerAdapter Size : " + testQuestionNewList.size());
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
            if (testQuestionNew.getQue_option_type().equalsIgnoreCase(Constant.SCQ) ||
                    testQuestionNew.getQue_option_type().equalsIgnoreCase(Constant.TRUE_FALSE)) {
                ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.practice_scq, collection, false);
                practiceScqBinding = DataBindingUtil.inflate(inflater, R.layout.practice_scq, collection, false);
                initViews(layout, testQuestionNew, position);
                initScq(layout, testQuestionNew);
                collection.addView(layout);
                layout.setTag(position);
                return layout;
            }
            if (testQuestionNew.getQue_option_type().equalsIgnoreCase(Constant.SCQ_IMAGE)) {
                ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.practice_scq_image, collection, false);
                initViews(layout, testQuestionNew, position);
                initScqImage(layout, testQuestionNew);
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
                practiceMcqBinding = DataBindingUtil.inflate(inflater, R.layout.practice_mcq, collection, false);
                initViews(layout, testQuestionNew, position);
                initMcq(layout, testQuestionNew);
                collection.addView(layout);
                layout.setTag(position);
                return layout;
            }

            if (testQuestionNew.getQue_option_type().equalsIgnoreCase(Constant.MATCH_PAIR)) {
                ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.practice_mtp, collection, false);
                practiceMtpBinding = DataBindingUtil.inflate(inflater, R.layout.practice_mtp, collection, false);
                initViews(practiceMtpBinding.getRoot(), testQuestionNew, position);
                initMtp(practiceMtpBinding.getRoot(), testQuestionNew);
                collection.addView(practiceMtpBinding.getRoot());
                practiceMtpBinding.getRoot().setTag(position);
                return practiceMtpBinding.getRoot();
            }
            if (testQuestionNew.getQue_option_type().equalsIgnoreCase(Constant.MATCH_PAIR_IMAGE)) {
                ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.practice_mtp_image, collection, false);
                practiceMtpImageBinding = DataBindingUtil.inflate(inflater, R.layout.practice_mtp_image, collection, false);
                initViews(practiceMtpImageBinding.getRoot(), testQuestionNew, position);
                initMtpImage(testQuestionNew);
                collection.addView(practiceMtpImageBinding.getRoot());
                practiceMtpImageBinding.getRoot().setTag(position);
                return practiceMtpImageBinding.getRoot();
            }
            /*if (testQuestionNew.getQue_option_type().equalsIgnoreCase(Constant.TRUE_FALSE)) {
                ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.practice_true_false, collection, false);
                trueFalseBinding = DataBindingUtil.inflate(inflater, R.layout.practice_true_false, collection, false);
                initViews(layout, testQuestionNew, position);
                initTrueFalse(layout, testQuestionNew);
                collection.addView(layout);
                layout.setTag(position);
                return layout;
            }*/
            if (testQuestionNew.getQue_option_type().equalsIgnoreCase(Constant.FILL_THE_BLANKS)) {
                ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.practice_fill_the_blanks, collection, false);
                initViews(layout, testQuestionNew, position);
                initFillTheBlanks(layout, testQuestionNew);
                collection.addView(layout);
                layout.setTag(position);
                return layout;
            }
            /*if (testQuestionNew.getQue_option_type().equalsIgnoreCase(Constant.LONG_ANSWER) ||
                    testQuestionNew.getQue_option_type().equalsIgnoreCase(Constant.SHORT_ANSWER) ||
                    testQuestionNew.getQue_option_type().equalsIgnoreCase(Constant.ONE_LINE_ANSWER)) {
                ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.practice_subjective, collection, false);
                initViews(layout, testQuestionNew, position);
                initSubjective(layout, testQuestionNew);
                collection.addView(layout);
                layout.setTag(position);
                return layout;
            }*/
        }
        return null;
    }

    private void initMtp(View view, TestQuestionNew testQuestionNew) {
        isB1Selected = false;
        isB2Selected = false;
        isB3Selected = false;
        isB4Selected = false;
        initPaired.clear();
        initMtpAns.clear();
        setPairAnswers(testQuestionNew);
        practiceMtpBinding.a1.setOnTouchListener(new ChoiceTouchListener());
        practiceMtpBinding.a2.setOnTouchListener(new ChoiceTouchListener());
        practiceMtpBinding.a3.setOnTouchListener(new ChoiceTouchListener());
        practiceMtpBinding.a4.setOnTouchListener(new ChoiceTouchListener());

        practiceMtpBinding.b1.setOnDragListener(new ChoiceDragListener());
        practiceMtpBinding.b2.setOnDragListener(new ChoiceDragListener());
        practiceMtpBinding.b3.setOnDragListener(new ChoiceDragListener());
        practiceMtpBinding.b4.setOnDragListener(new ChoiceDragListener());

        practiceMtpBinding.b1text.setOnDragListener(new ChoiceDragListener());
        practiceMtpBinding.b2text.setOnDragListener(new ChoiceDragListener());
        practiceMtpBinding.b3text.setOnDragListener(new ChoiceDragListener());
        practiceMtpBinding.b4text.setOnDragListener(new ChoiceDragListener());

        practiceMtpBinding.a1text.setText(testQuestionNew.getQ_mcq_op_1().split("::")[0]);
        practiceMtpBinding.a2text.setText(testQuestionNew.getQ_mcq_op_2().split("::")[0]);
        practiceMtpBinding.a3text.setText(testQuestionNew.getQ_mcq_op_3().split("::")[0]);
        practiceMtpBinding.a4text.setText(testQuestionNew.getQ_mcq_op_4().split("::")[0]);

        practiceMtpBinding.b1text.setText(testQuestionNew.getQ_mcq_op_1().split("::")[1]);
        practiceMtpBinding.b2text.setText(testQuestionNew.getQ_mcq_op_2().split("::")[1]);
        practiceMtpBinding.b3text.setText(testQuestionNew.getQ_mcq_op_3().split("::")[1]);
        practiceMtpBinding.b4text.setText(testQuestionNew.getQ_mcq_op_4().split("::")[1]);

        //set the pairs Text Only
        if (testQuestionNew.isTtqa_attempted() && !testQuestionNew.getTtqa_sub_ans().isEmpty()) {
            String[] selectedOptions = testQuestionNew.getTtqa_sub_ans().split(",", -1);
            String[] answers = testQuestionNew.getA_sub_ans().split(",", -1);
            for (String s : selectedOptions) {
                if (s != null && !s.isEmpty())
                    initPaired.put(s.split("-", -1)[0].trim(), s.split("-", -1)[1].trim());
            }
            for (String s : answers) {
                if (s != null && !s.isEmpty())
                    initMtpAns.put(s.split("-", -1)[0].trim(), s.split("-", -1)[1].trim());
            }
            Log.d(TAG, "initMtp initPaired : " + initPaired);
            Log.d(TAG, "initMtp  initMtpAns : " + initMtpAns);
            for (Map.Entry<String, String> entry : initPaired.entrySet()) {
                for (Map.Entry<String, String> entryAns : initMtpAns.entrySet()) {
                    Log.d(TAG, "initMtp Entry key : " + entry.getKey() + entryAns.getKey());
                    Log.d(TAG, "initMtp Entry value : " + entry.getValue() + entryAns.getValue());
                    if (entry.getKey().equalsIgnoreCase(entryAns.getKey()) &&
                            entry.getValue().equalsIgnoreCase(entryAns.getValue())) {
                        Log.d(TAG, "initMtp Matched : ");
                        if (entryAns.getValue().equalsIgnoreCase("B1")) {
                            setRightPair(entryAns.getValue().toLowerCase());
                        }
                        if (entryAns.getValue().equalsIgnoreCase("B2")) {
                            setRightPair(entryAns.getValue().toLowerCase());
                        }
                        if (entryAns.getValue().equalsIgnoreCase("B3")) {
                            setRightPair(entryAns.getValue().toLowerCase());
                            Log.d(TAG, "Matched : " + entry.getKey() + "-" + entry.getValue() + " = " + entryAns.getKey() + " - " + entryAns.getValue());
                        }
                        if (entryAns.getValue().equalsIgnoreCase("B4")) {
                            setRightPair(entryAns.getValue().toLowerCase());
                        }
                    } else {
                        setWrongPair(entryAns.getValue().toLowerCase());
                    }
                }

                Log.e(TAG, "initMtp Value : " + entry.getValue());
                if (entry.getValue().equalsIgnoreCase("B1")) {
                    //setmatchedPair(entry.getKey().toLowerCase(), context.getResources().getDrawable(R.drawable.ic_mtp1));
                    Log.e(TAG, "Pairs :  " + entry.getKey() + " => " + entry.getValue());
                    practiceMtpBinding.a2Img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_mtp1));
                    practiceMtpBinding.b1Img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_mtp1));
                }
                if (entry.getValue().equalsIgnoreCase("B2")) {
                    Log.e(TAG, "Pairs :  " + entry.getKey() + " => " + entry.getValue());
                    //setmatchedPair(entry.getKey().toLowerCase(), context.getResources().getDrawable(R.drawable.ic_mtp2));
                    practiceMtpBinding.a3Img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_mtp2));
                    practiceMtpBinding.b2Img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_mtp2));
                }
                if (entry.getValue().equalsIgnoreCase("B3")) {
                    Log.e(TAG, "Pairs :  " + entry.getKey() + " => " + entry.getValue());
                    //setmatchedPair(entry.getKey().toLowerCase(), context.getResources().getDrawable(R.drawable.ic_mtp3));
                    practiceMtpBinding.b3Img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_mtp3));
                    practiceMtpBinding.a1Img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_mtp3));
                }
                if (entry.getValue().equalsIgnoreCase("B4")) {
                    Log.e(TAG, "Pairs :  " + entry.getKey() + " => " + entry.getValue());
                    //setmatchedPair(entry.getKey().toLowerCase(), context.getResources().getDrawable(R.drawable.ic_mtp4));
                    practiceMtpBinding.a1Img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_mtp3));
                    practiceMtpBinding.b4Img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_mtp4));
                }
            }
        }

        practiceMtpBinding.resetLabel.setOnClickListener(v -> {
            reset(Constant.MATCH_PAIR);
        });

        practiceMtpBinding.submit.setOnClickListener(v -> {
            Log.e(TAG, "initMtp Selection : " + isB1Selected + "," + isB2Selected + "," + isB3Selected + "," + isB4Selected);
            String selectedPairs = "";
            if (!isB1Selected || !isB2Selected || !isB3Selected || !isB4Selected) {
                showToast("Select all pairs first.");
                //Toast.makeText(x, "Select all pairs first.", Toast.LENGTH_SHORT).show();
            } else {
                //isAttempted = 1;
                boolean isFound = false;
                Log.d(TAG, "initMtp Paired : " + paired);
                for (Map.Entry<String, String> entry : paired.entrySet()) {
                    //Iterator ansIterator = MTP_ans.entrySet().iterator();
                    String value = entry.getValue();
                    String key = entry.getKey();
                    selectedPairs = selectedPairs + key.toUpperCase() + "-" + value.toUpperCase() + ",";
                    Log.d(TAG, "initMtp Value : " + key + "-" + value);
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
                        // isSolvedRight = 0;
                        setWrongPair(value);
                    }
                }
                Log.d(TAG, "initMtp Ends with Comma : " + selectedPairs.endsWith(","));
                if (selectedPairs.startsWith(","))
                    selectedPairs.replaceFirst(",", "");
                if (selectedPairs.endsWith(","))
                    selectedPairs = selectedPairs.substring(0, selectedPairs.length() - 1);

                submitAnswerToServer(testQuestionNew, selectedPairs, Constant.MATCH_PAIR);
            }
        });
    }

    private void setPairAnswers(TestQuestionNew testQuestionNew) {
        String[] options;
        MTP_ans.clear();
        options = testQuestionNew.getA_sub_ans().split(",");
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
        Log.e(TAG, "initMtp Options Map : " + MTP_ans);
    }

    private void initMtpImage(TestQuestionNew testQuestionNew) {

        initPaired.clear();
        initMtpAns.clear();

        practiceMtpImageBinding.aMtp1.setOnTouchListener(new ChoiceTouchListener());
        practiceMtpImageBinding.aMtp2.setOnTouchListener(new ChoiceTouchListener());
        practiceMtpImageBinding.aMtp3.setOnTouchListener(new ChoiceTouchListener());
        practiceMtpImageBinding.aMtp4.setOnTouchListener(new ChoiceTouchListener());

        practiceMtpImageBinding.bMtp1.setOnDragListener(new ImgChoiceDragListener());
        practiceMtpImageBinding.bMtp2.setOnDragListener(new ImgChoiceDragListener());
        practiceMtpImageBinding.bMtp3.setOnDragListener(new ImgChoiceDragListener());
        practiceMtpImageBinding.bMtp4.setOnDragListener(new ImgChoiceDragListener());

        Log.d(TAG, "initMtpImage: " + Constant.PRODUCTION_BASE_FILE_API + testQuestionNew.getQ_mcq_op_1().split("::", -1)[0]);
        Glide.with(context).load(Constant.PRODUCTION_BASE_FILE_API + testQuestionNew.getQ_mcq_op_1().split("::", -1)[0].replace("png", "PNG")).into(practiceMtpImageBinding.aMtp1);
        Glide.with(context).load(Constant.PRODUCTION_BASE_FILE_API + testQuestionNew.getQ_mcq_op_2().split("::", -1)[0].replace("png", "PNG")).into(practiceMtpImageBinding.aMtp2);
        Glide.with(context).load(Constant.PRODUCTION_BASE_FILE_API + testQuestionNew.getQ_mcq_op_3().split("::", -1)[0].replace("png", "PNG")).into(practiceMtpImageBinding.aMtp3);

        Glide.with(context).load(Constant.PRODUCTION_BASE_FILE_API + testQuestionNew.getQ_mcq_op_1().split("::", -1)[1].replace("png", "PNG")).into(practiceMtpImageBinding.bMtp1);
        Glide.with(context).load(Constant.PRODUCTION_BASE_FILE_API + testQuestionNew.getQ_mcq_op_2().split("::", -1)[1].replace("png", "PNG")).into(practiceMtpImageBinding.bMtp2);
        Glide.with(context).load(Constant.PRODUCTION_BASE_FILE_API + testQuestionNew.getQ_mcq_op_3().split("::", -1)[1].replace("png", "PNG")).into(practiceMtpImageBinding.bMtp3);

        if (testQuestionNew.isTtqa_attempted() && !testQuestionNew.getTtqa_sub_ans().isEmpty()) {
            String[] selectedOptions = testQuestionNew.getTtqa_sub_ans().split(",", -1);
            String[] answers = testQuestionNew.getA_sub_ans().split(",", -1);
            for (String s : selectedOptions) {
                if (s != null && !s.isEmpty())
                    initPaired.put(s.split("-", -1)[0].trim(), s.split("-", -1)[1].trim());
            }
            for (String s : answers) {
                if (s != null && !s.isEmpty())
                    initMtpAns.put(s.split("-", -1)[0].trim(), s.split("-", -1)[1].trim());
            }
            Log.d(TAG, "initMtp initPaired : " + initPaired);
            Log.d(TAG, "initMtp  initMtpAns : " + initMtpAns);
            for (Map.Entry<String, String> entry : initPaired.entrySet()) {

                for (Map.Entry<String, String> entryAns : initMtpAns.entrySet()) {
                    Log.d(TAG, "initMtp Entry key : " + entry.getKey() + entryAns.getKey());
                    Log.d(TAG, "initMtp Entry value : " + entry.getValue() + entryAns.getValue());
                    if (entry.getKey().equalsIgnoreCase(entryAns.getKey()) &&
                            entry.getValue().equalsIgnoreCase(entryAns.getValue())) {
                        Log.d(TAG, "initMtp Matched : ");
                        if (entryAns.getValue().equalsIgnoreCase("B1")) {
                            setRightPair(entryAns.getValue().toLowerCase());
                        }
                        if (entryAns.getValue().equalsIgnoreCase("B2")) {
                            setRightPair(entryAns.getValue().toLowerCase());
                        }
                        if (entryAns.getValue().equalsIgnoreCase("B3")) {
                            setRightPair(entryAns.getValue().toLowerCase());
                        }
                        if (entryAns.getValue().equalsIgnoreCase("B4")) {
                            setRightPair(entryAns.getValue().toLowerCase());
                        }
                    } else {
                        setWrongPair(entryAns.getValue().toLowerCase());
                    }
                }

                Log.e(TAG, "initMtpImage Value : " + entry.getValue());
                if (entry.getValue().equalsIgnoreCase("B1")) {
                    setmatchedPair(entry.getKey().toLowerCase(), context.getResources().getDrawable(R.drawable.ic_mtp1));
                    practiceMtpBinding.b1Img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_mtp1));
                }
                if (entry.getValue().equalsIgnoreCase("B2")) {
                    setmatchedPair(entry.getKey().toLowerCase(), context.getResources().getDrawable(R.drawable.ic_mtp2));
                    practiceMtpBinding.b2Img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_mtp2));
                }
                if (entry.getValue().equalsIgnoreCase("B3")) {
                    setmatchedPair(entry.getKey().toLowerCase(), context.getResources().getDrawable(R.drawable.ic_mtp3));
                    practiceMtpBinding.b3Img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_mtp3));
                }
                if (entry.getValue().equalsIgnoreCase("B4")) {
                    setmatchedPair(entry.getKey().toLowerCase(), context.getResources().getDrawable(R.drawable.ic_mtp4));
                    practiceMtpBinding.b4Img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_mtp4));
                }
            }
        }

        practiceMtpImageBinding.resetLabel.setOnClickListener(v -> {
            reset(Constant.MATCH_PAIR_IMAGE);
        });

        practiceMtpImageBinding.submit.setOnClickListener(v -> {
            String selectedPairs = "";
            if (!isB1Selected || !isB2Selected || !isB3Selected) {
                showToast("Select all pairs first.");
                //Toast.makeText(activity, "Select all pairs first.", Toast.LENGTH_SHORT).show();
            } else {
                //isAttempted = 1;
                boolean isFound = false;
                for (Map.Entry<String, String> entry : imgpaired.entrySet()) {
                    //Iterator ansIterator = MTP_ans.entrySet().iterator();
                    String value = entry.getValue();
                    String key = entry.getKey();
                    selectedPairs = selectedPairs + key.toUpperCase() + "-" + value.toUpperCase() + ",";
                    Log.d(TAG, "initMtp Value : " + key + "-" + value);
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
                        //isSolvedRight = 0;
                        setImgWrongPair(value);
                    }
                }
                //Log.d(TAG, "initMtp Ends with Comma : " + selectedPairs.endsWith(","));
                if (!testQuestionNew.getA_sub_ans().contains("A4")) {
                    selectedPairs.replace("A4", "");
                    selectedPairs.replace("B4", "");
                }
                if (selectedPairs.startsWith(","))
                    selectedPairs.replaceFirst(",", "");
                if (selectedPairs.endsWith(","))
                    selectedPairs = selectedPairs.substring(0, selectedPairs.length() - 1);

                submitAnswerToServer(testQuestionNew, selectedPairs, Constant.MATCH_PAIR_IMAGE);
            }
        });
    }

    private void initFillTheBlanks(ViewGroup layout, TestQuestionNew testQuestionNew) {
        //ConstraintLayout fillTheBlanksLayout = layout.findViewById(R.id.fillTheBlanksLayout);
        ConstraintLayout solutionLayout = layout.findViewById(R.id.solution_layout);
        //fill the blanks attributes
        EditText etFillTheBlanks = layout.findViewById(R.id.fillTheBlanksEditText);
        ImageView fibImg = layout.findViewById(R.id.fib_img);
        Log.d(TAG, "FILL_THE_BLANKS");

        etFillTheBlanks.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                testQuestionNew.setTtqa_sub_ans(AppUtils.encodedString(s.toString()));
            }
        });

        Button btnSubmit = layout.findViewById(R.id.submit);

        if (testQuestionNew.isTtqa_attempted()) {
            etFillTheBlanks.setText(AppUtils.decodedString(testQuestionNew.getTtqa_sub_ans()));
            solutionLayout.setVisibility(View.VISIBLE);
            fibImg.setVisibility(View.VISIBLE);
            if (testQuestionNew.getA_sub_ans().equalsIgnoreCase(AppUtils.decodedString(testQuestionNew.getTtqa_sub_ans()))) {
                fibImg.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_right));
                testQuestionNew.setAnsweredRight(true);
            } else {
                fibImg.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wrong));
                testQuestionNew.setAnsweredRight(false);
            }
        }

        btnSubmit.setOnClickListener(v -> {
            String strAnswer = etFillTheBlanks.getText().toString().trim();
            if (strAnswer != null && strAnswer.isEmpty()) {
                showToast("Please enter answer.");
            } else {
                testQuestionNew.setTtqa_attempted(true);
                solutionLayout.setVisibility(View.VISIBLE);
                fibImg.setVisibility(View.VISIBLE);
                if (strAnswer.equalsIgnoreCase(testQuestionNew.getA_sub_ans())) {
                    fibImg.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_right));
                    testQuestionNew.setAnsweredRight(true);
                } else {
                    fibImg.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wrong));
                    testQuestionNew.setAnsweredRight(false);
                }
                submitAnswerToServer(testQuestionNew, testQuestionNew.getTtqa_sub_ans(), Constant.FILL_THE_BLANKS);
            }
        });
    }

    private void setSCQImgAnsIndicator(ImageView scqimgChck1, ImageView scqimgChck2, ImageView scqimgChck3, ImageView scqimgChck4) {
        scqimgChck1.setVisibility(View.GONE);
        scqimgChck2.setVisibility(View.GONE);
        scqimgChck3.setVisibility(View.GONE);
        scqimgChck4.setVisibility(View.GONE);
    }

    private void setSCQImgLayout(ImageView scqImg1, ImageView scqImg2, ImageView scqImg3, ImageView scqImg4) {
        scqImg1.setAlpha(255);
        scqImg2.setAlpha(255);
        scqImg3.setAlpha(255);
        scqImg4.setAlpha(255);
    }

    public void setRightSCQImg(String option, ImageView scqimgChck1, ImageView scqimgChck2
            , ImageView scqimgChck3, ImageView scqimgChck4) {
        switch (option) {
            case "A":
                scqimgChck1.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_right_mtp));
                scqimgChck1.setVisibility(View.VISIBLE);
                break;
            case "B":
                scqimgChck2.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_right_mtp));
                scqimgChck2.setVisibility(View.VISIBLE);
                break;
            case "C":
                scqimgChck3.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_right_mtp));
                scqimgChck3.setVisibility(View.VISIBLE);
                break;
            case "D":
                scqimgChck4.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_right_mtp));
                scqimgChck4.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void setWrongSCQImg(String option, ImageView scqimgChck1, ImageView scqimgChck2,
                               ImageView scqimgChck3, ImageView scqimgChck4) {
        switch (option) {
            case "A":
                scqimgChck1.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                scqimgChck1.setVisibility(View.VISIBLE);
                break;
            case "B":
                scqimgChck2.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                scqimgChck2.setVisibility(View.VISIBLE);
                break;
            case "C":
                scqimgChck3.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                scqimgChck3.setVisibility(View.VISIBLE);
                break;
            case "D":
                scqimgChck4.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                scqimgChck4.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void initScqImage(ViewGroup layout, TestQuestionNew testQuestionNew) {
        final String[] scqimg_ans = {""};
        scqimg_ans[0] = testQuestionNew.getTtqa_sub_ans();
        ImageView scq_img1 = layout.findViewById(R.id.scqImg1);
        ImageView scq_img2 = layout.findViewById(R.id.scqImg2);
        ImageView scq_img3 = layout.findViewById(R.id.scqImg3);
        ImageView scq_img4 = layout.findViewById(R.id.scqImg4);

        ImageView scqimg_chck1 = layout.findViewById(R.id.scqimg_chck1);
        ImageView scqimg_chck2 = layout.findViewById(R.id.scqimg_chck2);
        ImageView scqimg_chck3 = layout.findViewById(R.id.scqimg_chck3);
        ImageView scqimg_chck4 = layout.findViewById(R.id.scqimg_chck4);

        ConstraintLayout solutionLayout = layout.findViewById(R.id.solution_layout);
        Button btnSubmit = layout.findViewById(R.id.submit);

        loadImage(testQuestionNew.getQ_mcq_op_1(), scq_img1);
        loadImage(testQuestionNew.getQ_mcq_op_2(), scq_img2);
        loadImage(testQuestionNew.getQ_mcq_op_3(), scq_img3);
        loadImage(testQuestionNew.getQ_mcq_op_4(), scq_img4);

        //set answer if already attempted
        if (testQuestionNew.isTtqa_attempted()) {
            solutionLayout.setVisibility(View.VISIBLE);
            if (!testQuestionNew.getTtqa_sub_ans().isEmpty()) {
                //Right Answer
                setRightSCQImg(testQuestionNew.getA_sub_ans(), scqimg_chck1, scqimg_chck2, scqimg_chck3, scqimg_chck4);
                setWrongSCQImg(testQuestionNew.getTtqa_sub_ans(), scqimg_chck1, scqimg_chck2, scqimg_chck3, scqimg_chck4);
            }
        }

        scq_img1.setOnClickListener(v -> {
            setSCQImgAnsIndicator(scqimg_chck1, scqimg_chck2, scqimg_chck3, scqimg_chck4);
            setSCQImgLayout(scq_img1, scq_img2, scq_img3, scq_img4);
            scqimg_ans[0] = "A";
            scq_img1.setAlpha(130);
            scqimg_chck1.setVisibility(View.VISIBLE);
            scqimg_chck1.setImageDrawable(context.getResources().getDrawable(R.drawable.selectmark));
        });

        scq_img2.setOnClickListener(v -> {
            setSCQImgAnsIndicator(scqimg_chck1, scqimg_chck2, scqimg_chck3, scqimg_chck4);
            setSCQImgLayout(scq_img1, scq_img2, scq_img3, scq_img4);
            scqimg_ans[0] = "B";
            scq_img2.setAlpha(130);
            scqimg_chck2.setVisibility(View.VISIBLE);
            scqimg_chck2.setImageDrawable(context.getResources().getDrawable(R.drawable.selectmark));
        });

        scq_img3.setOnClickListener(v -> {
            setSCQImgAnsIndicator(scqimg_chck1, scqimg_chck2, scqimg_chck3, scqimg_chck4);
            setSCQImgLayout(scq_img1, scq_img2, scq_img3, scq_img4);
            scqimg_ans[0] = "C";
            scq_img3.setAlpha(130);
            scqimg_chck3.setVisibility(View.VISIBLE);
            scqimg_chck3.setImageDrawable(context.getResources().getDrawable(R.drawable.selectmark));
        });

        scq_img4.setOnClickListener(v -> {
            setSCQImgAnsIndicator(scqimg_chck1, scqimg_chck2, scqimg_chck3, scqimg_chck4);
            setSCQImgLayout(scq_img1, scq_img2, scq_img3, scq_img4);
            scqimg_ans[0] = "D";
            scq_img4.setAlpha(130);
            scqimg_chck4.setVisibility(View.VISIBLE);
            scqimg_chck4.setImageDrawable(context.getResources().getDrawable(R.drawable.selectmark));
        });

        btnSubmit.setOnClickListener(v -> {
            if (!scqimg_ans[0].trim().equalsIgnoreCase("")) {
                //isAttempted = 1;
                scqimg_chck1.setVisibility(View.GONE);
                scqimg_chck1.setVisibility(View.GONE);
                scqimg_chck1.setVisibility(View.GONE);
                scqimg_chck1.setVisibility(View.GONE);

                if (scqimg_ans[0].equalsIgnoreCase(testQuestionNew.getA_sub_ans())) {
                    testQuestionNew.setAnsweredRight(true);
                    setRightSCQImg(scqimg_ans[0], scqimg_chck1, scqimg_chck2, scqimg_chck3, scqimg_chck4);
                } else {
                    // isSolvedRight = 0;
                    testQuestionNew.setAnsweredRight(false);
                    setRightSCQImg(testQuestionNew.getA_sub_ans(), scqimg_chck1, scqimg_chck2, scqimg_chck3, scqimg_chck4);
                    setWrongSCQImg(scqimg_ans[0], scqimg_chck1, scqimg_chck2, scqimg_chck3, scqimg_chck4);
                }
                solutionLayout.setVisibility(View.VISIBLE);
                submitAnswerToServer(testQuestionNew, scqimg_ans[0], Constant.SCQ_IMAGE);

            } else {
                showToast("Please select atleast one option.");
            }
        });
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

    private void ProcessQuestionAPI(int que_id, int flag, String call_from, int prevLikes) {
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

    private void setMCQAnsIndicator(ImageView mcq1Img, ImageView mcq2Img, ImageView mcq3Img, ImageView mcq4Img) {
        mcq1Img.setVisibility(View.GONE);
        mcq2Img.setVisibility(View.GONE);
        mcq3Img.setVisibility(View.GONE);
        mcq4Img.setVisibility(View.GONE);
    }

    private void initMcq(ViewGroup layout, TestQuestionNew testQuestionNew) {
        final String[] mcq_ans = {""};
        //mcq_ans[0] = testQuestionNew.getTtqa_sub_ans();
        for (String str : testQuestionNew.getTtqa_sub_ans().split(",")) {
            Log.e(TAG, "initMcq New Option : " + str);
            if (str != null && !str.isEmpty())
                mcq_ans[0] = mcq_ans[0] + str + ",";
        }
        if (mcq_ans[0].startsWith(","))
            mcq_ans[0].replaceFirst(",", "");
        if (mcq_ans[0].endsWith(",")) {
            mcq_ans[0] = mcq_ans[0].substring(0, mcq_ans[0].length() - 1);
        }
        Log.d(TAG, "initMcq Final Option : " + mcq_ans[0]);
        TextView mcq1 = layout.findViewById(R.id.mcq1);
        TextView mcq2 = layout.findViewById(R.id.mcq2);
        TextView mcq3 = layout.findViewById(R.id.mcq3);
        TextView mcq4 = layout.findViewById(R.id.mcq4);
        TextView mcq5 = layout.findViewById(R.id.mcq5);

        MathView mcq1Math = layout.findViewById(R.id.mcq1Math);
        MathView mcq2Math = layout.findViewById(R.id.mcq2Math);
        MathView mcq3Math = layout.findViewById(R.id.mcq3Math);
        MathView mcq4Math = layout.findViewById(R.id.mcq4Math);
        MathView mcq5Math = layout.findViewById(R.id.mcq5Math);

        ImageView imgCheck1 = layout.findViewById(R.id.mcq1_img);
        ImageView imgCheck2 = layout.findViewById(R.id.mcq2_img);
        ImageView imgCheck3 = layout.findViewById(R.id.mcq3_img);
        ImageView imgCheck4 = layout.findViewById(R.id.mcq4_img);
        ImageView imgCheck5 = layout.findViewById(R.id.mcq5_img);

        Button btnSubmit = layout.findViewById(R.id.submit);
        ConstraintLayout solutionLayout = layout.findViewById(R.id.solution_layout);

        ConstraintLayout mcq1Layout = layout.findViewById(R.id.mcq1_layout);
        ConstraintLayout mcq2Layout = layout.findViewById(R.id.mcq2_layout);
        ConstraintLayout mcq3Layout = layout.findViewById(R.id.mcq3_layout);
        ConstraintLayout mcq4Layout = layout.findViewById(R.id.mcq4_layout);
        ConstraintLayout mcq5Layout = layout.findViewById(R.id.mcq5_layout);

        if (testQuestionNew.getQ_mcq_op_4().isEmpty())
            mcq4Layout.setVisibility(View.GONE);
        if (testQuestionNew.getQ_mcq_op_5().isEmpty())
            mcq5Layout.setVisibility(View.GONE);

        if (testQuestionNew.getQ_mcq_op_1().contains("\\")) {
            mcq1.setVisibility(View.GONE);
            mcq1Math.setVisibility(View.VISIBLE);
            mcq1Math.setText(testQuestionNew.getQ_mcq_op_1());
        } else {
            mcq1Math.setVisibility(View.GONE);
            mcq1.setVisibility(View.VISIBLE);
            mcq1.setText(testQuestionNew.getQ_mcq_op_1());
        }

        if (testQuestionNew.getQ_mcq_op_2().contains("\\")) {
            mcq2.setVisibility(View.GONE);
            mcq2Math.setVisibility(View.VISIBLE);
            mcq2Math.setText(testQuestionNew.getQ_mcq_op_2());
        } else {
            mcq2Math.setVisibility(View.GONE);
            mcq2.setVisibility(View.VISIBLE);
            mcq2.setText(testQuestionNew.getQ_mcq_op_2());
        }

        if (testQuestionNew.getQ_mcq_op_3().contains("\\")) {
            mcq3.setVisibility(View.GONE);
            mcq3Math.setVisibility(View.VISIBLE);
            mcq3Math.setText(testQuestionNew.getQ_mcq_op_3());
        } else {
            mcq3Math.setVisibility(View.GONE);
            mcq3.setVisibility(View.VISIBLE);
            mcq3.setText(testQuestionNew.getQ_mcq_op_3());
        }

        if (testQuestionNew.getQ_mcq_op_4().contains("\\")) {
            mcq4.setVisibility(View.GONE);
            mcq4Math.setVisibility(View.VISIBLE);
            mcq4Math.setText(testQuestionNew.getQ_mcq_op_4());
        } else {
            mcq4Math.setVisibility(View.GONE);
            mcq4.setVisibility(View.VISIBLE);
            mcq4.setText(testQuestionNew.getQ_mcq_op_4());
        }

        if (testQuestionNew.getQ_mcq_op_5().contains("\\")) {
            mcq5.setVisibility(View.GONE);
            mcq5Math.setVisibility(View.VISIBLE);
            mcq5Math.setText(testQuestionNew.getQ_mcq_op_5());
        } else {
            mcq5Math.setVisibility(View.GONE);
            mcq5.setVisibility(View.VISIBLE);
            mcq5.setText(testQuestionNew.getQ_mcq_op_5());
        }

        if (testQuestionNew.isTtqa_attempted()) {
            solutionLayout.setVisibility(View.VISIBLE);
            String[] allAns = {"A", "B", "C", "D"};
            if (!testQuestionNew.getTtqa_sub_ans().isEmpty()) {
                for (String s : allAns) {
                    if (testQuestionNew.getA_sub_ans().contains(s)) {
                        setRightMCQ(s, imgCheck1, imgCheck2, imgCheck3, imgCheck4);
                    } else {
                        setWrongMCQ(s, imgCheck1, imgCheck2, imgCheck3, imgCheck4);
                    }
                }
            }
        }

        mcq1Layout.setOnClickListener(v -> {
            setMCQAnsIndicator(imgCheck1, imgCheck2, imgCheck3, imgCheck4);
            if (!mcq_ans[0].contains("A")) {
                if (mcq_ans[0].equalsIgnoreCase(""))
                    mcq_ans[0] = "A";
                else
                    mcq_ans[0] = mcq_ans[0] + " A";
                mcq1Layout.setBackground(context.getResources().getDrawable(R.drawable.grey_border_grey_bg));
            } else {
                mcq_ans[0] = mcq_ans[0].replace("A", "");
                mcq1Layout.setBackground(context.getResources().getDrawable(R.drawable.grey_round_order));
            }
        });

        mcq2Layout.setOnClickListener(v -> {
            setMCQAnsIndicator(imgCheck1, imgCheck2, imgCheck3, imgCheck4);
            if (!mcq_ans[0].contains("B")) {
                if (mcq_ans[0].equalsIgnoreCase(""))
                    mcq_ans[0] = "B";
                else
                    mcq_ans[0] = mcq_ans[0] + " B";
                mcq2Layout.setBackground(context.getResources().getDrawable(R.drawable.grey_border_grey_bg));
            } else {
                mcq_ans[0] = mcq_ans[0].replace("B", "");
                mcq2Layout.setBackground(context.getResources().getDrawable(R.drawable.grey_round_order));
            }
        });

        mcq3Layout.setOnClickListener(v -> {
            setMCQAnsIndicator(imgCheck1, imgCheck2, imgCheck3, imgCheck4);
            if (!mcq_ans[0].contains("C")) {
                if (mcq_ans[0].equalsIgnoreCase(""))
                    mcq_ans[0] = "C";
                else
                    mcq_ans[0] = mcq_ans[0] + " C";
                mcq3Layout.setBackground(context.getResources().getDrawable(R.drawable.grey_border_grey_bg));
            } else {
                mcq_ans[0] = mcq_ans[0].replace("C", "");
                mcq3Layout.setBackground(context.getResources().getDrawable(R.drawable.grey_round_order));
            }
        });

        mcq4Layout.setOnClickListener(v -> {
            setMCQAnsIndicator(imgCheck1, imgCheck2, imgCheck3, imgCheck4);
            if (!mcq_ans[0].contains("D")) {
                if (mcq_ans[0].equalsIgnoreCase(""))
                    mcq_ans[0] = "D";
                else
                    mcq_ans[0] = mcq_ans[0] + " D";
                mcq4Layout.setBackground(context.getResources().getDrawable(R.drawable.grey_border_grey_bg));
            } else {
                mcq_ans[0] = mcq_ans[0].replace("D", "");
                mcq4Layout.setBackground(context.getResources().getDrawable(R.drawable.grey_round_order));
            }
        });

        btnSubmit.setOnClickListener(v -> {
            if (!mcq_ans[0].trim().equalsIgnoreCase("")) {
                //isAttempted = 1;
                setMCQAnsIndicator(imgCheck1, imgCheck2, imgCheck3, imgCheck4);
                String selectedOptions = "";
                String[] selected_mcq = mcq_ans[0].split("\\s+");
                String[] right_mcq = testQuestionNew.getA_sub_ans().split(",", -1);
                for (int i = 0; i < selected_mcq.length; i++) {
                    Log.e(TAG, "initMcq Selected Items : " + selected_mcq[i]);
                    if (i != selected_mcq.length - 1) {
                        selectedOptions = selectedOptions + selected_mcq[i].trim() + ",";
                    } else {
                        selectedOptions = selectedOptions + selected_mcq[i].trim();
                    }

                    if (testQuestionNew.getA_sub_ans().contains(selected_mcq[i])) {
                        setRightMCQ(selected_mcq[i], imgCheck1, imgCheck2, imgCheck3, imgCheck4);
                    } else {
                        //isSolvedRight = 0;
                        setWrongMCQ(selected_mcq[i], imgCheck1, imgCheck2, imgCheck3, imgCheck4);
                    }
                }

                for (int i = 0; i < right_mcq.length; i++) {
                    setRightMCQ(right_mcq[i], imgCheck1, imgCheck2, imgCheck3, imgCheck4);
                }
                solutionLayout.setVisibility(View.VISIBLE);
                if (selectedOptions.startsWith(","))
                    selectedOptions = selectedOptions.replaceFirst(",", "");
                if (selectedOptions.endsWith(","))
                    selectedOptions = selectedOptions.substring(0, selectedOptions.length() - 1);

                testQuestionNew.setTtqa_mcq_ans_1(false);
                testQuestionNew.setTtqa_mcq_ans_2(false);
                testQuestionNew.setTtqa_mcq_ans_3(false);
                testQuestionNew.setTtqa_mcq_ans_4(false);

                for (String s : selectedOptions.split(",", -1)) {
                    Log.d(TAG, "initMcq selectedOptions : " + s);
                    if (s != null && s.equalsIgnoreCase("A"))
                        testQuestionNew.setTtqa_mcq_ans_1(true);
                    if (s != null && s.equalsIgnoreCase("B"))
                        testQuestionNew.setTtqa_mcq_ans_2(true);
                    if (s != null && s.equalsIgnoreCase("C"))
                        testQuestionNew.setTtqa_mcq_ans_3(true);
                    if (s != null && s.equalsIgnoreCase("D"))
                        testQuestionNew.setTtqa_mcq_ans_4(true);
                }
                submitAnswerToServer(testQuestionNew, selectedOptions, Constant.MCQ);
            } else {
                showToast("Please select atleast one option.");
                //Toast.makeText(activity, "Please select atleast one option.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setRightMCQ(String option, ImageView mcq1Img, ImageView mcq2Img, ImageView mcq3Img, ImageView mcq4Img) {
        switch (option) {
            case "A":
                mcq1Img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_right));
                mcq1Img.setVisibility(View.VISIBLE);
                break;
            case "B":
                mcq2Img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_right));
                mcq2Img.setVisibility(View.VISIBLE);
                break;
            case "C":
                mcq3Img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_right));
                mcq3Img.setVisibility(View.VISIBLE);
                break;
            case "D":
                mcq4Img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_right));
                mcq4Img.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void setWrongMCQ(String option, ImageView mcq1Img, ImageView mcq2Img, ImageView mcq3Img, ImageView mcq4Img) {
        switch (option) {
            case "A":
                mcq1Img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wrong));
                mcq1Img.setVisibility(View.VISIBLE);
                break;
            case "B":
                mcq2Img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wrong));
                mcq2Img.setVisibility(View.VISIBLE);
                break;
            case "C":
                mcq3Img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wrong));
                mcq3Img.setVisibility(View.VISIBLE);
                break;
            case "D":
                mcq4Img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wrong));
                mcq4Img.setVisibility(View.VISIBLE);
                break;
        }
    }


    private void initScq(ViewGroup layout, TestQuestionNew testQuestionNew) {
        Log.d(TAG, "initScq Item Position : " + getItemPosition(testQuestionNew));
        boolean isFeedBackEnabled = true;
        final String[] scq_ans = {""};
        scq_ans[0] = testQuestionNew.getTtqa_sub_ans();
        TextView scq1 = layout.findViewById(R.id.scq11);
        TextView scq2 = layout.findViewById(R.id.scq22);
        TextView scq3 = layout.findViewById(R.id.scq33);
        TextView scq4 = layout.findViewById(R.id.scq44);
        TextView scq5 = layout.findViewById(R.id.scq55);

        MathView scq1Math = layout.findViewById(R.id.scq1Math);
        MathView scq2Math = layout.findViewById(R.id.scq2Math);
        MathView scq3Math = layout.findViewById(R.id.scq3Math);
        MathView scq4Math = layout.findViewById(R.id.scq4Math);
        MathView scq5Math = layout.findViewById(R.id.scq5Math);

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

        Log.e(TAG, "initScq Option 1 : " + testQuestionNew.getQ_mcq_op_1());
        Log.e(TAG, "initScq Option 2 : " + testQuestionNew.getQ_mcq_op_2());
        Log.e(TAG, "initScq Option 3 : " + testQuestionNew.getQ_mcq_op_3());
        Log.e(TAG, "initScq Option 4 : " + testQuestionNew.getQ_mcq_op_4());

        if (testQuestionNew.getQ_mcq_op_1().contains("\\")) {
            scq1.setVisibility(View.GONE);
            scq1Math.setVisibility(View.VISIBLE);
            scq1Math.setText(testQuestionNew.getQ_mcq_op_1());
        } else {
            scq1Math.setVisibility(View.GONE);
            scq1.setVisibility(View.VISIBLE);
            scq1.setText(testQuestionNew.getQ_mcq_op_1());
        }

        if (testQuestionNew.getQ_mcq_op_2().contains("\\")) {
            scq2.setVisibility(View.GONE);
            scq2Math.setVisibility(View.VISIBLE);
            scq2Math.setText(testQuestionNew.getQ_mcq_op_2());
        } else {
            scq2Math.setVisibility(View.GONE);
            scq2.setVisibility(View.VISIBLE);
            scq2.setText(testQuestionNew.getQ_mcq_op_2());
        }

        if (testQuestionNew.getQ_mcq_op_3().contains("\\")) {
            scq3.setVisibility(View.GONE);
            scq3Math.setVisibility(View.VISIBLE);
            scq3Math.setText(testQuestionNew.getQ_mcq_op_3());
        } else {
            scq3Math.setVisibility(View.GONE);
            scq3.setVisibility(View.VISIBLE);
            scq3.setText(testQuestionNew.getQ_mcq_op_3());
        }

        if (testQuestionNew.getQ_mcq_op_4().contains("\\")) {
            scq4.setVisibility(View.GONE);
            scq4Math.setVisibility(View.VISIBLE);
            scq4Math.setText(testQuestionNew.getQ_mcq_op_4());
        } else {
            scq4Math.setVisibility(View.GONE);
            scq4.setVisibility(View.VISIBLE);
            scq4.setText(testQuestionNew.getQ_mcq_op_4());
        }

        if (testQuestionNew.getQ_mcq_op_5().contains("\\")) {
            scq5.setVisibility(View.GONE);
            scq5Math.setVisibility(View.VISIBLE);
            scq5Math.setText(testQuestionNew.getQ_mcq_op_5());
        } else {
            scq4Math.setVisibility(View.GONE);
            scq5.setVisibility(View.VISIBLE);
            scq5.setText(testQuestionNew.getQ_mcq_op_5());
        }

        if (testQuestionNew.getQ_mcq_op_3().isEmpty())
            scq3Layout.setVisibility(View.GONE);
        if (testQuestionNew.getQ_mcq_op_4().isEmpty())
            scq4Layout.setVisibility(View.GONE);
        if (testQuestionNew.getQ_mcq_op_5().isEmpty())
            scq5Layout.setVisibility(View.GONE);

        solutionAns.setText(testQuestionNew.getA_sub_ans());
        if (testQuestionNew.isTtqa_attempted()) {
            solutionLayout.setVisibility(View.VISIBLE);
            if (!testQuestionNew.getTtqa_sub_ans().isEmpty()) {
                //Right Answer Given
                if (testQuestionNew.getTtqa_sub_ans().equalsIgnoreCase(testQuestionNew.getA_sub_ans())) {
                    setRightSCQ(testQuestionNew.getA_sub_ans(), img1, img2, img3, img4, img5);
                } else {
                    setRightSCQ(testQuestionNew.getA_sub_ans(), img1, img2, img3, img4, img5);
                    setWrongSCQ(testQuestionNew.getTtqa_sub_ans(), img1, img2, img3, img4, img5, false);
                }
            }
        }

        scq1Layout.setOnClickListener(v -> {
            setSCQAnsIndicator(img1, img2, img3, img4);
            setLayoutBg(scq1Layout, scq2Layout, scq3Layout, scq4Layout);
            scq_ans[0] = "A";
            scq1Layout.setBackground(context.getResources().getDrawable(R.drawable.grey_border_grey_bg));
        });

        scq2Layout.setOnClickListener(v -> {
            setLayoutBg(scq1Layout, scq2Layout, scq3Layout, scq4Layout);
            setSCQAnsIndicator(img1, img2, img3, img4);
            scq_ans[0] = "B";
            scq2Layout.setBackground(context.getResources().getDrawable(R.drawable.grey_border_grey_bg));
        });

        scq3Layout.setOnClickListener(v -> {
            setLayoutBg(scq1Layout, scq2Layout, scq3Layout, scq4Layout);
            setSCQAnsIndicator(img1, img2, img3, img4);
            scq_ans[0] = "C";
            scq3Layout.setBackground(context.getResources().getDrawable(R.drawable.grey_border_grey_bg));
        });

        scq4Layout.setOnClickListener(v -> {
            setLayoutBg(scq1Layout, scq2Layout, scq3Layout, scq4Layout);
            setSCQAnsIndicator(img1, img2, img3, img4);
            scq_ans[0] = "D";
            scq4Layout.setBackground(context.getResources().getDrawable(R.drawable.grey_border_grey_bg));
        });

        btnSubmit.setOnClickListener(v -> {
            Log.d(TAG, "initScq Submit Answer : " + scq_ans[0]);
            if (!scq_ans[0].trim().equalsIgnoreCase("")) {
                //isAttempted = 1;
                setSCQAnsIndicator(img1, img2, img3, img4);
                Log.d(TAG, "scq_ans : " + scq_ans[0]);
                Log.d(TAG, "getA_sub_ans : " + testQuestionNew.getA_sub_ans());
                Log.d(TAG, "initScq: " + scq_ans[0].equalsIgnoreCase(testQuestionNew.getA_sub_ans()));
                if (scq_ans[0].equalsIgnoreCase(testQuestionNew.getA_sub_ans())) {
                    rightAnswerFeedBack();
                    testQuestionNew.setAnsweredRight(true);
                    setRightSCQ(scq_ans[0], img1, img2, img3, img4, img5);
                } else {
                    testQuestionNew.setAnsweredRight(false);
                    //isSolvedRight = 0;
                    setRightSCQ(testQuestionNew.getA_sub_ans(), img1, img2, img3, img4, img5);
                    setWrongSCQ(scq_ans[0], img1, img2, img3, img4, img5, true);
                }
                solutionLayout.setVisibility(View.VISIBLE);

                //disable answer selection
                scq1Layout.setEnabled(false);
                scq2Layout.setEnabled(false);
                scq3Layout.setEnabled(false);
                scq4Layout.setEnabled(false);
                btnSubmit.setEnabled(false);

                submitAnswerToServer(testQuestionNew, scq_ans[0], Constant.SCQ);
            } else {
                showToast("Please select atleast one option.");
            }
        });
    }

    private void submitAnswerToServer(TestQuestionNew question, String scq_ans, String flag) {
        SubmitTest submitTest = new SubmitTest();
        Log.d(TAG, "submitAnswerToServer Answer given : " + scq_ans);
        List<TestQuestionNew> submitTestQuestionList = new ArrayList<>();
        submitTest.setTm_id(startResumeTestResponse.getTm_id());
        submitTest.setTt_id(String.valueOf(startResumeTestResponse.getTtId()));
        if (flag.equalsIgnoreCase(Constant.SCQ)) {
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

        if (question.isAnsweredRight())
            question.setTtqa_obtain_marks(question.getQ_marks());
        else
            question.setTtqa_obtain_marks("0");

        question.setTtqa_sub_ans(scq_ans);
        question.setTtqa_attempted(true);
        question.setTtqa_visited(false);

        submitTestQuestionList.add(question);
        submitTest.setTestQuestionNewList(submitTestQuestionList);
        //Log.d(TAG, "Get Encoded : " + StringEscapeUtils.unescapeJava(question.getTtqa_sub_ans().replace("=", "")));
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

    private void markAQuestion() {

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
        final String[] scqimgtext_ans = {""};
        scqimgtext_ans[0] = testQuestionNew.getTtqa_sub_ans();
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

        //Log.e(TAG, "initScqImageText Image Url : " + testQuestionNew.getQ_mcq_op_1().split("::")[1].split(":")[0]);
        Glide.with(context).load(Constant.PRODUCTION_BASE_FILE_API + testQuestionNew.getQ_mcq_op_1().split(":")[0]).into(scqImgtextImg1);
        Glide.with(context).load(Constant.PRODUCTION_BASE_FILE_API + testQuestionNew.getQ_mcq_op_2().split(":")[0]).into(scqImgtextImg2);
        Glide.with(context).load(Constant.PRODUCTION_BASE_FILE_API + testQuestionNew.getQ_mcq_op_3().split(":")[0]).into(scqImgtextImg3);
        Glide.with(context).load(Constant.PRODUCTION_BASE_FILE_API + testQuestionNew.getQ_mcq_op_4().split(":")[0]).into(scqImgtextImg4);

//        loadImage(testQuestionNew.getQ_mcq_op_2().split(":")[0], scqImgtextImg2);
//        loadImage(testQuestionNew.getQ_mcq_op_3().split(":")[0], scqImgtextImg3);
//        loadImage(testQuestionNew.getQ_mcq_op_4().split(":")[0], scqImgtextImg4);

        //Log.e(TAG, "initScqImageText Image Url : " + testQuestionNew.getQ_mcq_op_1().split("::")[1].split(":")[1]);
        scqText1.setText(testQuestionNew.getQ_mcq_op_1().split(":")[1]);
        scqText2.setText(testQuestionNew.getQ_mcq_op_2().split(":")[1]);
        scqText3.setText(testQuestionNew.getQ_mcq_op_3().split(":")[1]);
        scqText4.setText(testQuestionNew.getQ_mcq_op_4().split(":")[1]);

        //set Answer if already attempted
        if (testQuestionNew.isTtqa_attempted()) {
            solutionLayout.setVisibility(View.VISIBLE);
            if (!testQuestionNew.getTtqa_sub_ans().isEmpty()) {
                //Right Ans given
                if (testQuestionNew.getTtqa_sub_ans().equalsIgnoreCase(testQuestionNew.getA_sub_ans())) {
                    setRightSCQImgText(testQuestionNew.getA_sub_ans(), scqimgImgtextChck1, scqimgImgtextChck2, scqimgImgtextChck3, scqimgImgtextChck4);
                } else {
                    setRightSCQImgText(testQuestionNew.getA_sub_ans(), scqimgImgtextChck1, scqimgImgtextChck2, scqimgImgtextChck3, scqimgImgtextChck4);
                    setWrongSCQImgText(testQuestionNew.getTtqa_sub_ans(), scqimgImgtextChck1, scqimgImgtextChck2, scqimgImgtextChck3, scqimgImgtextChck4);
                }
            }
        }

        scqImgtextImg1.setOnClickListener(v ->
        {
            setSCQImgTextAnsIndicator(scqimgImgtextChck1, scqimgImgtextChck2, scqimgImgtextChck3, scqimgImgtextChck4);
            setSCQImgTextLayout(scqImgtextImg1, scqImgtextImg2, scqImgtextImg3, scqImgtextImg4);
            scqimgtext_ans[0] = "A";
            scqImgtextImg1.setAlpha(130);
            scqimgImgtextChck1.setVisibility(View.VISIBLE);
            scqimgImgtextChck1.setImageDrawable(context.getResources().getDrawable(R.drawable.selectmark));
        });

        scqImgtextImg2.setOnClickListener(v ->
        {
            setSCQImgTextAnsIndicator(scqimgImgtextChck1, scqimgImgtextChck2, scqimgImgtextChck3, scqimgImgtextChck4);
            setSCQImgTextLayout(scqImgtextImg1, scqImgtextImg2, scqImgtextImg3, scqImgtextImg4);
            scqimgtext_ans[0] = "B";
            scqImgtextImg2.setAlpha(130);
            scqimgImgtextChck2.setVisibility(View.VISIBLE);
            scqimgImgtextChck2.setImageDrawable(context.getResources().getDrawable(R.drawable.selectmark));
        });

        scqImgtextImg3.setOnClickListener(v ->
        {
            setSCQImgTextAnsIndicator(scqimgImgtextChck1, scqimgImgtextChck2, scqimgImgtextChck3, scqimgImgtextChck4);
            setSCQImgTextLayout(scqImgtextImg1, scqImgtextImg2, scqImgtextImg3, scqImgtextImg4);
            scqimgtext_ans[0] = "C";
            scqImgtextImg3.setAlpha(130);
            scqimgImgtextChck3.setVisibility(View.VISIBLE);
            scqimgImgtextChck3.setImageDrawable(context.getResources().getDrawable(R.drawable.selectmark));
        });

        scqImgtextImg4.setOnClickListener(v ->
        {
            setSCQImgTextAnsIndicator(scqimgImgtextChck1, scqimgImgtextChck2, scqimgImgtextChck3, scqimgImgtextChck4);
            setSCQImgTextLayout(scqImgtextImg1, scqImgtextImg2, scqImgtextImg3, scqImgtextImg4);
            scqimgtext_ans[0] = "D";
            scqImgtextImg4.setAlpha(130);
            scqimgImgtextChck4.setVisibility(View.VISIBLE);
            scqimgImgtextChck4.setImageDrawable(context.getResources().getDrawable(R.drawable.selectmark));
        });

        submit.setOnClickListener(v ->

        {
            Log.d(TAG, "initScqImageText Submit Answer : " + scqimgtext_ans[0]);
            if (!scqimgtext_ans[0].trim().equalsIgnoreCase("")) {
                //isAttempted = 1;
                setSCQImgTextAnsIndicator(scqimgImgtextChck1, scqimgImgtextChck2, scqimgImgtextChck3, scqimgImgtextChck4);
                if (scqimgtext_ans[0].equalsIgnoreCase(testQuestionNew.getA_sub_ans())) {
                    rightAnswerFeedBack();
                    testQuestionNew.setAnsweredRight(true);
                    setRightSCQImgText(scqimgtext_ans[0], scqimgImgtextChck1, scqimgImgtextChck2, scqimgImgtextChck3, scqimgImgtextChck4);
                } else {
                    //isSolvedRight = 0;
                    wrongAnswerFeedBack(true);
                    testQuestionNew.setAnsweredRight(false);
                    setRightSCQImgText(testQuestionNew.getA_sub_ans(), scqimgImgtextChck1, scqimgImgtextChck2, scqimgImgtextChck3, scqimgImgtextChck4);
                    setWrongSCQImgText(scqimgtext_ans[0], scqimgImgtextChck1, scqimgImgtextChck2, scqimgImgtextChck3, scqimgImgtextChck4);
                }
                solutionLayout.setVisibility(View.VISIBLE);

                //Disable answer selection
                scqImgtextImg1.setEnabled(false);
                scqImgtextImg2.setEnabled(false);
                scqImgtextImg3.setEnabled(false);
                scqImgtextImg4.setEnabled(false);
                submit.setEnabled(false);

                submitAnswerToServer(testQuestionNew, scqimgtext_ans[0], Constant.SCQ_IMAGE_WITH_TEXT);

            } else {
                showToast("Please select atleast one option.");
                //Toast.makeText(activity, "Please select atleast one option.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initSubjective(ViewGroup layout, TestQuestionNew testQuestionNew) {
        TextView tvWordCounter = layout.findViewById(R.id.tvWordCounter);
        TextView tvFtbWordCounter = layout.findViewById(R.id.tvFtbWordCounter);
        TextView multi_lineCounter = layout.findViewById(R.id.multi_lineCounter);
        TextView tvTimer = layout.findViewById(R.id.tvtimer);
        Button btnSubmit = layout.findViewById(R.id.submit);
        ConstraintLayout multiLineAnswerLayout = layout.findViewById(R.id.multi_line_answer);
        ConstraintLayout fillTheBlanksLayout = layout.findViewById(R.id.fill_in_the_blanks_layout);
        ConstraintLayout solutionLayout = layout.findViewById(R.id.solution_layout);

        EditText etMultiLineAns = layout.findViewById(R.id.multi_line);
        EditText etSinlgeineAns = layout.findViewById(R.id.fill_in_the_blanks);

        String decodedAns = AppUtils.decodedString(testQuestionNew.getTtqa_sub_ans());
        //Log.d(TAG, "Plain Text : "+testQuestionNew.getTtqa_sub_ans());
        //Log.d(TAG, "Decoded Text : " + StringUtils.stripAccents(decodedAns));
        int minutes = Integer.parseInt(tvTimer.getText().toString().split(":",-1)[0]);
        int seconds = Integer.parseInt(tvTimer.getText().toString().split(":",-1)[1]);
        SubjectiveAnsDialog subjectiveAnsDialog = new SubjectiveAnsDialog(context,decodedAns,seconds,minutes,this);

        /*etMultiLineAns.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                etMultiLineAns.requestFocus();
                return true;
            }
        });*/

        etMultiLineAns.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etMultiLineAns.requestFocus();
            }
        });

        etMultiLineAns.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    etMultiLineAns.clearFocus();
                            subjectiveAnsDialog.show();
                }
            }
        });

        if (testQuestionNew.getType().equalsIgnoreCase(Constant.ONE_LINE_ANSWER) ||
                testQuestionNew.getType().equalsIgnoreCase(Constant.FILL_THE_BLANKS)) {
            Log.d(TAG, "FILL_THE_BLANKS");
            fillTheBlanksLayout.setVisibility(View.VISIBLE);
            answerCharCounter(etSinlgeineAns, tvFtbWordCounter, 10, testQuestionNew);
            etSinlgeineAns.setText(decodedAns);
        }

        if (testQuestionNew.getType().equalsIgnoreCase(Constant.SHORT_ANSWER) ||
                testQuestionNew.getType().equalsIgnoreCase(Constant.LONG_ANSWER)) {
            Log.d(TAG, "SHORT_ANSWER OR LONG_ANSWER");
            multiLineAnswerLayout.setVisibility(View.VISIBLE);
            //decodedAns = AppUtils.decodedMessage(StringEscapeUtils.unescapeJava(testQuestionNew.getTtqa_sub_ans()));
            answerCharCounter(etMultiLineAns, tvWordCounter, 200, testQuestionNew);
            etMultiLineAns.setText(decodedAns);
        }

        Log.e(TAG, "initSubjective Condtn : " + testQuestionNew.isTtqa_attempted());
        if (testQuestionNew.isTtqa_attempted()) {
            solutionLayout.setVisibility(View.VISIBLE);
            //Log.d(TAG, "Base 64 : " + testQuestionNew.getTtqa_sub_ans());
            Log.d(TAG, "Decoded Ans : " + decodedAns);
            etMultiLineAns.setText(decodedAns);
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
                    submitAnswerToServer(testQuestionNew, testQuestionNew.getTtqa_sub_ans(), "SUBJECTIVE");
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

    private void initViews(View layout, TestQuestionNew testQuestionNew, int pos) {
        TextView tvQuestNo = layout.findViewById(R.id.id_textview);
        TextView tvQuestion = layout.findViewById(R.id.question_textview);
        TextView tvMarks = layout.findViewById(R.id.marks_textview);
        ImageView imgQueDownload = layout.findViewById(R.id.save_que);
        TextView tvDiffLevel = layout.findViewById(R.id.difflevel_value);
        TextView tvAttemptedValue = layout.findViewById(R.id.attempted_value);
        TextView tvRatingValue = layout.findViewById(R.id.ratingvalue);
        TextView tvLikeValue = layout.findViewById(R.id.like_value);
        TextView tvCommentValue = layout.findViewById(R.id.comment_value);
        TextView tvShareValue = layout.findViewById(R.id.share_value);

        ImageView share = layout.findViewById(R.id.share);

        TextView solutionDesc = layout.findViewById(R.id.solution_option);

        LinearLayout questLayout = layout.findViewById(R.id.questLayout);
        MathView questMath = layout.findViewById(R.id.questMathView);

        ConstraintLayout commentsLayout = layout.findViewById(R.id.comment_layout);

        //Toggle Buttons
        ToggleButton favorite = layout.findViewById(R.id.favorite);
        ToggleButton like = layout.findViewById(R.id.like);
        ToggleButton mark = layout.findViewById(R.id.markToggle);

        //set the values
        if (testQuestionNew.getQ_quest().contains("\\")) {
            tvQuestion.setVisibility(View.GONE);
            questMath.setVisibility(View.VISIBLE);
            questMath.setText(testQuestionNew.getQ_quest());
        } else {
            questMath.setVisibility(View.GONE);
            tvQuestion.setVisibility(View.VISIBLE);
            tvQuestion.setText(testQuestionNew.getQ_quest());
        }

        tvQuestNo.setText(String.valueOf(testQuestionNew.getTq_quest_seq_num()));
        tvMarks.setText("Marks : " + testQuestionNew.getTq_marks());
        tvDiffLevel.setText(testQuestionNew.getQ_diff_level());
        tvAttemptedValue.setText(testQuestionNew.getAttended_by());
        tvRatingValue.setText(testQuestionNew.getRating());
        tvLikeValue.setText(testQuestionNew.getLikes());
        tvCommentValue.setText(testQuestionNew.getComments());
        tvShareValue.setText(testQuestionNew.getShares());

        //set Answer in solution Layout
        solutionDesc.setText(testQuestionNew.getA_sub_ans());

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

        if (commentsLayout != null)
            commentsLayout.setOnClickListener(v -> {
                commentDialog = new CommentDialog(context, testQuestionNew.getTq_q_id(), this);
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

        if (share != null)
            share.setOnClickListener(v -> {
                //showToast("Hello");
                ShareQuestionDialog shareDialog = new ShareQuestionDialog(context,"");
                shareDialog.show();
            });
    }

    private void rightAnswerFeedBack() {
        MediaPlayer mp = MediaPlayer.create(context, R.raw.right_ans);
        mp.start();
    }

    private void wrongAnswerFeedBack(boolean isFeedBackEnabled) {
        if (isFeedBackEnabled) {
            //vibrate
            Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibe.vibrate(100);
        } else {
            //do nothing
        }
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
                            testQuestionNew.setTtqa_sub_ans(AppUtils.encodedString(s.toString()));
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

    public void setWrongSCQ(String option, ImageView scq1Img, ImageView scq2Img, ImageView scq3Img,
                            ImageView scq4Img, ImageView scq5Img, boolean isFeedBackEnabled) {
        try {
            switch (option) {
                case "A":
                    scq1Img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wrong));
                    scq1Img.setVisibility(View.VISIBLE);
                    wrongAnswerFeedBack(isFeedBackEnabled);
                    break;
                case "B":
                    scq2Img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wrong));
                    scq2Img.setVisibility(View.VISIBLE);
                    wrongAnswerFeedBack(isFeedBackEnabled);
                    break;
                case "C":
                    scq3Img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wrong));
                    scq3Img.setVisibility(View.VISIBLE);
                    wrongAnswerFeedBack(isFeedBackEnabled);
                    break;
                case "D":
                    scq4Img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wrong));
                    scq4Img.setVisibility(View.VISIBLE);
                    wrongAnswerFeedBack(isFeedBackEnabled);
                    break;
                case "E":
                    scq5Img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wrong));
                    scq5Img.setVisibility(View.VISIBLE);
                    wrongAnswerFeedBack(isFeedBackEnabled);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemCLick(String user_id) {
        publicProfileDialog = new PublicProfileDialog(context, user_id, this);
        publicProfileDialog.show();
    }

    @Override
    public void onCommentClick(String userId) {
        publicProfileDialog = new PublicProfileDialog(context, userId, this);
        publicProfileDialog.show();
    }

    private void loadImage(String img, ImageView imageView) {
        Log.d(TAG, "loadImage URL : " + img);
        if (!img.contains("http")) {
            String fileName = img.substring(img.lastIndexOf('/') + 1);
            File file = new File(UtilHelper.getDirectory(context), fileName);

            if (file.exists()) {
                Glide.with(context).load(file).into(imageView);
            } else {
                Glide.with(context).load(Constant.PRODUCTION_BASE_FILE_API + img)
                        .placeholder(R.drawable.no_image)
                        .error(R.drawable.no_image)
                        .into(imageView);
            }
        } else {
            //load image directly
            Glide.with(context).load(img).into(imageView);
        }
    }


    @Override
    public void onViewImage(String path) {
        Dialog dialog = new Dialog(context, android.R.style.Theme_Light);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.image_fullscreen_preview);
        ImageView imageView = dialog.findViewById(R.id.image_preview);
        imageView.setOnTouchListener(new ImageMatrixTouchHandler(dialog.getWindow().getContext()));

        Glide.with(context).load(path)
                .thumbnail(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontTransform()
                .dontAnimate()
                .into(imageView);
        dialog.show();
    }

    @Override
    public void onAnswerEntered(String answer) {
        //showToast(answer);
        viewPagerClickListener.onFullScreenAns(answer);
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
                            practiceMtpImageBinding.bMtpChck1.setVisibility(View.VISIBLE);
                            practiceMtpImageBinding.bMtpChck1.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_mtp1));
                            setMtpMatchedPair(getNameFromId(dropped.getId()), context.getResources().getDrawable(R.drawable.ic_mtp1));
                            if (imgpaired.size() == MTP_ans.size() - 1) {
                                setImgMtpLastPair();
                            }
                            break;
                        case R.id.b_mtp2:
                            checkImgPairAvailability(getNameFromId(dropped.getId()), "b2");
                            imgpaired.put(getNameFromId(dropped.getId()), "b2");
                            isB2Selected = true;
                            practiceMtpImageBinding.bMtpChck2.setVisibility(View.VISIBLE);
                            practiceMtpImageBinding.bMtpChck2.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_mtp2));
                            setMtpMatchedPair(getNameFromId(dropped.getId()), context.getResources().getDrawable(R.drawable.ic_mtp2));
                            if (imgpaired.size() == MTP_ans.size() - 1) {
                                setImgMtpLastPair();
                            }
                            break;
                        case R.id.b_mtp3:
                            checkImgPairAvailability(getNameFromId(dropped.getId()), "b3");
                            imgpaired.put(getNameFromId(dropped.getId()), "b3");
                            isB3Selected = true;
                            practiceMtpImageBinding.bMtpChck3.setVisibility(View.VISIBLE);
                            practiceMtpImageBinding.bMtpChck3.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_mtp3));
                            setMtpMatchedPair(getNameFromId(dropped.getId()), context.getResources().getDrawable(R.drawable.ic_mtp3));
                            if (imgpaired.size() == MTP_ans.size() - 1) {
                                setImgMtpLastPair();
                            }
                            break;
                        case R.id.b_mtp4:
                            checkImgPairAvailability(getNameFromId(dropped.getId()), "b4");
                            imgpaired.put(getNameFromId(dropped.getId()), "b4");
                            isB3Selected = true;
                            practiceMtpImageBinding.bMtpChck4.setVisibility(View.VISIBLE);
                            practiceMtpImageBinding.bMtpChck4.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_mtp4));
                            setMtpMatchedPair(getNameFromId(dropped.getId()), context.getResources().getDrawable(R.drawable.ic_mtp4));
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

                    switch (v.getId()) {
                        case R.id.b1:
                            Log.d(TAG, "onDrag b1 : ");
                        case R.id.b1text:
                            Log.d(TAG, "onDrag b1text : ");
                            checkAvailability(getNameFromId(dropped.getId()), "b1");
                            paired.put(getNameFromId(dropped.getId()), "b1");
                            isB1Selected = true;
                            setmatchedPair(getNameFromId(dropped.getId()), context.getResources().getDrawable(R.drawable.ic_mtp1));
                            practiceMtpBinding.b1Img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_mtp1));
                            if (paired.size() == MTP_ans.size() - 1) {
                                setLastPair();
                            }
                            break;
                        case R.id.b2:
                        case R.id.b2text:
                            checkAvailability(getNameFromId(dropped.getId()), "b2");
                            paired.put(getNameFromId(dropped.getId()), "b2");
                            isB2Selected = true;
                            setmatchedPair(getNameFromId(dropped.getId()), context.getResources().getDrawable(R.drawable.ic_mtp2));
                            practiceMtpBinding.b2Img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_mtp2));
                            if (paired.size() == MTP_ans.size() - 1) {
                                setLastPair();
                            }
                            break;
                        case R.id.b3:
                        case R.id.b3text:
                            checkAvailability(getNameFromId(dropped.getId()), "b3");
                            paired.put(getNameFromId(dropped.getId()), "b3");
                            isB3Selected = true;
                            setmatchedPair(getNameFromId(dropped.getId()), context.getResources().getDrawable(R.drawable.ic_mtp3));
                            practiceMtpBinding.b3Img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_mtp3));
                            if (paired.size() == MTP_ans.size() - 1) {
                                setLastPair();
                            }
                            break;
                        case R.id.b4:
                        case R.id.b4text:
                            checkAvailability(getNameFromId(dropped.getId()), "b4");
                            paired.put(getNameFromId(dropped.getId()), "b4");
                            isB4Selected = true;
                            setmatchedPair(getNameFromId(dropped.getId()), context.getResources().getDrawable(R.drawable.ic_mtp4));
                            practiceMtpBinding.b4Img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_mtp4));
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
            isB1Selected = true;
            isB2Selected = true;
            isB3Selected = true;
            isB4Selected = true;
        }
    }

    private Drawable getPairColor(String option) {
        switch (option) {
            case "b1":
                return context.getResources().getDrawable(R.drawable.ic_mtp1);
            case "b2":
                return context.getResources().getDrawable(R.drawable.ic_mtp2);
            case "b3":
                return context.getResources().getDrawable(R.drawable.ic_mtp3);
            case "b4":
                return context.getResources().getDrawable(R.drawable.ic_mtp4);
            default:
                return context.getResources().getDrawable(R.drawable.ic_mtp_grey);
        }
    }

    public void setMtpMatchedPair(String option, Drawable drawable) {
        switch (option) {
            case "b1":
                practiceMtpImageBinding.bMtpChck1.setImageDrawable(drawable);
                practiceMtpImageBinding.bMtpChck1.setVisibility(View.VISIBLE);
                practiceMtpImageBinding.bMtp1.setAlpha(150);
                break;
            case "b2":
                practiceMtpImageBinding.bMtpChck2.setImageDrawable(drawable);
                practiceMtpImageBinding.bMtpChck2.setVisibility(View.VISIBLE);
                practiceMtpImageBinding.bMtp2.setAlpha(150);
                break;
            case "b3":
                practiceMtpImageBinding.bMtpChck3.setImageDrawable(drawable);
                practiceMtpImageBinding.bMtpChck3.setVisibility(View.VISIBLE);
                practiceMtpImageBinding.bMtp3.setAlpha(150);
                break;
            case "b4":
                practiceMtpImageBinding.bMtpChck4.setImageDrawable(drawable);
                practiceMtpImageBinding.bMtpChck4.setVisibility(View.VISIBLE);
                practiceMtpImageBinding.bMtp4.setAlpha(150);
                break;
            case "a1":
                practiceMtpImageBinding.aMtpChck1.setImageDrawable(drawable);
                practiceMtpImageBinding.aMtpChck1.setVisibility(View.VISIBLE);
                practiceMtpImageBinding.aMtp1.setAlpha(150);
                break;
            case "a2":
                practiceMtpImageBinding.aMtpChck2.setImageDrawable(drawable);
                practiceMtpImageBinding.aMtpChck2.setVisibility(View.VISIBLE);
                practiceMtpImageBinding.aMtp2.setAlpha(150);
                break;
            case "a3":
                practiceMtpImageBinding.aMtpChck3.setImageDrawable(drawable);
                practiceMtpImageBinding.aMtpChck3.setVisibility(View.VISIBLE);
                practiceMtpImageBinding.aMtp3.setAlpha(150);
                break;
            case "a4":
                practiceMtpImageBinding.aMtpChck4.setImageDrawable(drawable);
                practiceMtpImageBinding.aMtpChck4.setVisibility(View.VISIBLE);
                practiceMtpImageBinding.aMtp4.setAlpha(150);
                break;
            default:
                break;
        }
    }

    public void setMatchedPairs(Drawable drawable, ImageView imageView) {
        //Log.d(TAG, "setmatchedPair Option : " + option);
        imageView.setImageDrawable(drawable);
    }

    public void setmatchedPair(String option, Drawable drawable) {
        Log.d(TAG, "setmatchedPair Option : " + option);
        switch (option) {
            case "b1":
                practiceMtpBinding.b1Img.setImageDrawable(drawable);
                break;
            case "b2":
                practiceMtpBinding.b2Img.setImageDrawable(drawable);
                break;
            case "b3":
                practiceMtpBinding.b3Img.setImageDrawable(drawable);
                break;
            case "b4":
                practiceMtpBinding.b4Img.setImageDrawable(drawable);
                break;
            case "a1":
                practiceMtpBinding.a1Img.setImageDrawable(drawable);
                break;
            case "a2":
                practiceMtpBinding.a2Img.setImageDrawable(drawable);
                break;
            case "a3":
                practiceMtpBinding.a3Img.setImageDrawable(drawable);
                break;
            case "a4":
                practiceMtpBinding.a4Img.setImageDrawable(drawable);
                break;
            default:
                break;
        }
    }

    private void setImgRightPair(String option) {
        switch (option) {
            case "b1":
                practiceMtpImageBinding.bMtpChck1.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_right_mtp));
                practiceMtpImageBinding.bMtpChck1.setVisibility(View.VISIBLE);
                break;
            case "b2":
                practiceMtpImageBinding.bMtpChck2.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_right_mtp));
                practiceMtpImageBinding.bMtpChck2.setVisibility(View.VISIBLE);
                break;
            case "b3":
                practiceMtpImageBinding.bMtpChck3.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_right_mtp));
                practiceMtpImageBinding.bMtpChck3.setVisibility(View.VISIBLE);
                break;
            case "b4":
                practiceMtpImageBinding.bMtpChck4.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_right_mtp));
                practiceMtpImageBinding.bMtpChck4.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }


    private void setImgWrongPair(String option) {
        switch (option) {
            case "b1":
                practiceMtpImageBinding.bMtpChck1.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                practiceMtpImageBinding.bMtpChck1.setVisibility(View.VISIBLE);
                break;
            case "b2":
                practiceMtpImageBinding.bMtpChck2.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                practiceMtpImageBinding.bMtpChck2.setVisibility(View.VISIBLE);
                break;
            case "b3":
                practiceMtpImageBinding.bMtpChck3.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                practiceMtpImageBinding.bMtpChck3.setVisibility(View.VISIBLE);
                break;
            case "b4":
                practiceMtpImageBinding.bMtpChck4.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                practiceMtpImageBinding.bMtpChck4.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    private void setRightPair(String option) {
        Log.d(TAG, "setRightPair Option Value : " + option);
        switch (option) {
            case "b1":
                practiceMtpBinding.img1.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_right_mtp));
                practiceMtpBinding.img1.setVisibility(View.VISIBLE);
                break;
            case "b2":
                practiceMtpBinding.img2.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_right_mtp));
                practiceMtpBinding.img2.setVisibility(View.VISIBLE);
                break;
            case "b3":
                practiceMtpBinding.img3.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_right_mtp));
                practiceMtpBinding.img3.setVisibility(View.VISIBLE);
                Log.d(TAG, "setRightPair Visible is done : ");
                break;
            case "b4":
                practiceMtpBinding.img4.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_right_mtp));
                practiceMtpBinding.img4.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }


    private void setWrongPair(String option) {
        Log.d(TAG, "setWrongPair Option Value : " + option);
        switch (option) {
            case "b1":
                practiceMtpBinding.img1.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                practiceMtpBinding.img1.setVisibility(View.VISIBLE);
                break;
            case "b2":
                practiceMtpBinding.img2.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                practiceMtpBinding.img2.setVisibility(View.VISIBLE);
                break;
            case "b3":
                practiceMtpBinding.img3.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                practiceMtpBinding.img3.setVisibility(View.VISIBLE);
                break;
            case "b4":
                practiceMtpBinding.img4.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_wrong_mtp));
                practiceMtpBinding.img4.setVisibility(View.VISIBLE);
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
                practiceMtpImageBinding.aMtp1.setAlpha(255);
                practiceMtpImageBinding.aMtpChck1.setVisibility(View.GONE);
                break;
            case "a2":
                practiceMtpImageBinding.aMtp2.setAlpha(255);
                practiceMtpImageBinding.aMtpChck2.setVisibility(View.GONE);
                break;
            case "a3":
                practiceMtpImageBinding.aMtp3.setAlpha(255);
                practiceMtpImageBinding.aMtpChck3.setVisibility(View.GONE);
                break;
            case "a4":
                practiceMtpImageBinding.aMtp4.setAlpha(255);
                practiceMtpImageBinding.aMtpChck4.setVisibility(View.GONE);
                break;
            case "b1":
                isB1Selected = false;
                practiceMtpImageBinding.bMtp1.setAlpha(255);
                practiceMtpImageBinding.bMtpChck1.setVisibility(View.GONE);
                break;
            case "b2":
                isB2Selected = false;
                practiceMtpImageBinding.bMtp2.setAlpha(255);
                practiceMtpImageBinding.bMtpChck2.setVisibility(View.GONE);
                break;
            case "b3":
                isB3Selected = false;
                practiceMtpImageBinding.bMtp3.setAlpha(255);
                practiceMtpImageBinding.bMtpChck3.setVisibility(View.GONE);
                break;
            case "b4":
                isB4Selected = false;
                practiceMtpImageBinding.bMtp4.setAlpha(255);
                practiceMtpImageBinding.bMtpChck4.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    private void setLayoutBg(String option) {
        switch (option) {
            case "a1":
                practiceMtpBinding.a1Img.setBackground(context.getResources().getDrawable(R.drawable.ic_mtp_grey));
                break;
            case "a2":
                practiceMtpBinding.a2Img.setBackground(context.getResources().getDrawable(R.drawable.ic_mtp_grey));
                break;
            case "a3":
                practiceMtpBinding.a3Img.setBackground(context.getResources().getDrawable(R.drawable.ic_mtp_grey));
                break;
            case "a4":
                practiceMtpBinding.a4Img.setBackground(context.getResources().getDrawable(R.drawable.ic_mtp_grey));
                break;
            case "b1":
                isB1Selected = false;
                practiceMtpBinding.b1Img.setBackground(context.getResources().getDrawable(R.drawable.ic_mtp_grey));
                break;
            case "b2":
                isB2Selected = false;
                practiceMtpBinding.b2Img.setBackground(context.getResources().getDrawable(R.drawable.ic_mtp_grey));
                break;
            case "b3":
                isB3Selected = false;
                practiceMtpBinding.b3Img.setBackground(context.getResources().getDrawable(R.drawable.ic_mtp_grey));
                break;
            case "b4":
                isB4Selected = false;
                practiceMtpBinding.b4Img.setBackground(context.getResources().getDrawable(R.drawable.ic_mtp_grey));
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

    private class ChoiceTouchListener implements View.OnTouchListener {
        @SuppressLint("NewApi")
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDrag(data, shadowBuilder, view, 0);
                return true;
            } else {
                return false;
            }
        }
    }

    public void reset(String flag) {
        paired.clear();

        isB1Selected = false;
        isB2Selected = false;
        isB3Selected = false;
        isB4Selected = false;

        if (flag.equalsIgnoreCase(Constant.MATCH_PAIR)) {
            practiceMtpBinding.img1.setVisibility(View.GONE);
            practiceMtpBinding.img2.setVisibility(View.GONE);
            practiceMtpBinding.img3.setVisibility(View.GONE);
            practiceMtpBinding.img4.setVisibility(View.GONE);

            practiceMtpBinding.a1Img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_mtp_grey));
            practiceMtpBinding.a2Img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_mtp_grey));
            practiceMtpBinding.a3Img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_mtp_grey));
            practiceMtpBinding.a4Img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_mtp_grey));
            practiceMtpBinding.b1Img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_mtp_grey));
            practiceMtpBinding.b2Img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_mtp_grey));
            practiceMtpBinding.b3Img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_mtp_grey));
            practiceMtpBinding.b4Img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_mtp_grey));
        }
        if (flag.equalsIgnoreCase(Constant.MATCH_PAIR_IMAGE)) {

            practiceMtpImageBinding.bMtpChck1.setVisibility(View.GONE);
            practiceMtpImageBinding.bMtpChck2.setVisibility(View.GONE);
            practiceMtpImageBinding.bMtpChck3.setVisibility(View.GONE);
            practiceMtpImageBinding.bMtpChck4.setVisibility(View.GONE);

            practiceMtpImageBinding.aMtpChck1.setVisibility(View.GONE);
            practiceMtpImageBinding.aMtpChck2.setVisibility(View.GONE);
            practiceMtpImageBinding.aMtpChck3.setVisibility(View.GONE);
            practiceMtpImageBinding.aMtpChck4.setVisibility(View.GONE);

        }
    }

    public interface ViewPagerClickListener {
        void onLikeClick(boolean isChecked, int likeCount);
        void onCommentClick();
        void onShareClick();
        void onSubmitClick();
        void onMarkQuestion(int pos);
        void onFullScreenAns(String strAns);
        void onFavouriteClick(boolean isChecked);
    }
}
