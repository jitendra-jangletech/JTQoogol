package com.jangletech.qoogol.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.DummyAdapter;
import com.jangletech.qoogol.databinding.ActivityDynamicViewPagerBinding;
import com.jangletech.qoogol.enums.QuestionType;
import com.jangletech.qoogol.model.TestQuestion;

import java.util.ArrayList;
import java.util.List;

public class DynamicViewPagerActivity extends AppCompatActivity {

    private ActivityDynamicViewPagerBinding mBinding;
    public static List<TestQuestion> testQuestionList = new ArrayList<>();
    DummyAdapter dummyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_dynamic_view_pager);
        //setQuestionList();
        dummyAdapter = new DummyAdapter(this);
        mBinding.courseViewpager.setAdapter(dummyAdapter);

    }

   /* private ViewPagerAdapterNew createCardAdapter() {
        ViewPagerAdapterNew adapter = new ViewPagerAdapterNew(this);
        return adapter;
    }*/
/*
    private List<TestQuestion> setQuestionList() {

        TestQuestion testQuestion = new TestQuestion(0, 1, "MCQ",
                "Web Pages are saved in which of the following format?", "http://", "HTML", "DOC", "URL", "BMP");

        TestQuestion testQuestion1 = new TestQuestion(1, 2, "SCQ",
                "System software acts as a bridge between the hardware and _____ software", "Management", "Processing",
                "Utility", "Application", "Embedded");


        TestQuestion testQuestion2 = new TestQuestion(2, 3, "SCQ",
                "Who is appointed as the brand ambassador of VISA recently?",
                "Ram Sing Yadav", "Arpinder Singh", "PV Sindhu", "Sania Mirza", "Sachin Tendulkar");


        TestQuestion testQuestion3 = new TestQuestion(3, 4, "MCQ",
                "Who built Jama Masjid at Delhi?",
                "Jahangir", "Qutub-ud-din-Albak", "Akbar", "Birbal", "Aurangjeb");
        TestQuestion testQuestion4 = new TestQuestion(4, 5, QuestionType.MCQ.toString(),
                "Which of the following helps in the blood clotting?", "Vitamin A", "Vitamin D", "Vitamin K", "Folic acid",
                "Calcium");

        TestQuestion testQuestion5 = new TestQuestion(5, 6, QuestionType.TRUE_FALSE.toString(),
                "Narendra Modi is Prime Minister of india?",
                "", "", "", "", "");


        TestQuestion testQuestion6 = new TestQuestion(6, 7, QuestionType.FILL_THE_BLANKS.toString(),
                "____ is origin of COVID-19 outbreak?",
                "", "", "", "", "");

        TestQuestion testQuestion7 = new TestQuestion(7, 8, QuestionType.MULTI_LINE_ANSWER.toString(),
                "Explain Android Activity Lifecycle?",
                "", "", "", "", "");

        testQuestionList.add(testQuestion);
        testQuestionList.add(testQuestion1);
        testQuestionList.add(testQuestion2);
        testQuestionList.add(testQuestion3);
        testQuestionList.add(testQuestion4);
        testQuestionList.add(testQuestion5);
        testQuestionList.add(testQuestion6);
        testQuestionList.add(testQuestion7);

        return testQuestionList;
    }*/

}
