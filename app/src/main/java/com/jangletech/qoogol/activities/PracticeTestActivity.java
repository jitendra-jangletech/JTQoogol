package com.jangletech.qoogol.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.viewpager2.widget.ViewPager2;

import com.jangletech.qoogol.MainActivity;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.LearingAdapter;
import com.jangletech.qoogol.adapter.PracticeTestAdapter;
import com.jangletech.qoogol.databinding.ActivityPracticeTestBinding;
import com.jangletech.qoogol.model.LearningQuestions;
import com.jangletech.qoogol.util.Constant;

import java.util.ArrayList;
import java.util.List;

public class PracticeTestActivity extends AppCompatActivity implements LearingAdapter.onIconClick{

    private ArrayList<String> arrayList = new ArrayList<>();
    private ActivityPracticeTestBinding mBinding;
    PracticeTestAdapter practiceTestAdapter;
    LearingAdapter learingAdapter;
    List<LearningQuestions> learningQuestionsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_practice_test);
        setData();
        initView();
    }

    private void initView() {

        mBinding.viewPager.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        learingAdapter = new LearingAdapter(PracticeTestActivity.this, learningQuestionsList,this, Constant.test);
        mBinding.viewPager.setAdapter(learingAdapter);
    }

    private void setData() {
        learningQuestionsList = new ArrayList<>();
        ArrayList<String> imglist = new ArrayList<>();
        ArrayList<String> imglist1 = new ArrayList<>();
        learningQuestionsList.clear();

        LearningQuestions learningQuestions0 = new LearningQuestions();
        learningQuestions0.setQuestion_id("Q1001");
        learningQuestions0.setLikes("90");
        learningQuestions0.setComments("30");
        learningQuestions0.setShares("10");
        learningQuestions0.setRecommended_time("50");
        learningQuestions0.setIs_liked(true);
        learningQuestions0.setIs_fav(true);
        learningQuestions0.setSubject("Science");
        imglist.clear();
        imglist.add("https://www.thoughtco.com/thmb/bORUDzdznV2AzCzV3jGumQBhmPI=/768x300/filters:no_upscale():max_bytes(150000):strip_icc()/GettyImages-102635591-56acf9ec5f9b58b7d00ad548.jpg");
        learningQuestions0.setAttchment(imglist);
        learningQuestions0.setChapter("5th Chapter");
        learningQuestions0.setCategory("SCQ");
        learningQuestions0.setAttended_by(25);
        learningQuestions0.setQuestion("This one is intended to be a confidence builder. These are diamonds. Diamonds are pure:");
        learningQuestions0.setAnswer("b");
        learningQuestions0.setAnswerDesc("Like other elements, carbon occurs in different forms, which are called allotropes. Allotropes of carbon include transparent diamond, gray graphite (pencil \"lead\"), and black amorphous carbon (soot).");
        learningQuestions0.setMcq1("boron");
        learningQuestions0.setMcq2("carbon");
        learningQuestions0.setMcq3("iron");
        learningQuestions0.setMcq4("nitrogen");
        learningQuestions0.setRating("5");
        learningQuestions0.setTopic("Element");
        learningQuestions0.setPosted_on("2020/03/13");
        learningQuestions0.setLastused_on("2020/03/17");
        learningQuestions0.setDifficulty_level("Easy");
        learningQuestions0.setMarks("2");
        learningQuestionsList.add(learningQuestions0);

        LearningQuestions learningQuestions00 = new LearningQuestions();
        learningQuestions00.setQuestion_id("Q1011");
        learningQuestions00.setLikes("90");
        learningQuestions00.setComments("30");
        learningQuestions00.setShares("10");
        learningQuestions00.setRecommended_time("50");
        learningQuestions00.setIs_liked(true);
        learningQuestions00.setIs_fav(true);
        learningQuestions00.setSubject("General Knowledge");
        imglist1.clear();
        imglist1.add("https://homepages.cae.wisc.edu/~ece533/images/boat.png");
        imglist1.add("https://homepages.cae.wisc.edu/~ece533/images/fruits.png");
        imglist1.add("https://homepages.cae.wisc.edu/~ece533/images/mountain.png");
        learningQuestions00.setAttchment(imglist1);
        learningQuestions00.setChapter("5th Chapter");
        learningQuestions00.setCategory("SCQ");
        learningQuestions00.setAttended_by(25);
        learningQuestions00.setQuestion("Which image contain mountains");
        learningQuestions00.setAnswer("c");
        learningQuestions00.setAnswerDesc("Like other elements, carbon occurs in different forms, which are called allotropes. Allotropes of carbon include transparent diamond, gray graphite (pencil \"lead\"), and black amorphous carbon (soot).");
        learningQuestions00.setMcq1("1");
        learningQuestions00.setMcq2("2");
        learningQuestions00.setMcq3("3");
        learningQuestions00.setMcq4("None of the above");
        learningQuestions00.setRating("5");
        learningQuestions00.setTopic("Image analysis");
        learningQuestions00.setPosted_on("2020/03/13");
        learningQuestions00.setLastused_on("2020/03/17");
        learningQuestions00.setDifficulty_level("Easy");
        learningQuestions00.setMarks("2");
        learningQuestionsList.add(learningQuestions00);


        LearningQuestions learningQuestions = new LearningQuestions();
        learningQuestions.setQuestion_id("Q1002");
        learningQuestions.setLikes("102");
        learningQuestions.setComments("30");
        learningQuestions.setShares("5");
        learningQuestions.setMcq1("\\((-2)^6\\)");
        learningQuestions.setMcq2("\\((-2)^5\\)");
        learningQuestions.setMcq3("\\((2)^5\\)");
        learningQuestions.setMcq4("\\((2)^6\\)");
        learningQuestions.setAttended_by(45);
        learningQuestions.setSubject("Mathematics");
        learningQuestions.setChapter("8th Chapter");
        learningQuestions.setRecommended_time("40");
        learningQuestions.setIs_liked(true);
        learningQuestions.setIs_fav(false);
        learningQuestions.setCategory("SCQ");
        learningQuestions.setQuestion("find the value of \\([(-2)^2]^3\\)");
        learningQuestions.setAnswer("a");
        learningQuestions.setAnswerDesc("It will multiply 2 and 3");
        learningQuestions.setRating("4.5");
        learningQuestions.setTopic("Square");
        learningQuestions.setPosted_on("2020/03/15");
        learningQuestions.setLastused_on("2020/03/17");
        learningQuestions.setDifficulty_level("Easy");
        learningQuestions.setMarks("5");
        learningQuestionsList.add(learningQuestions);

        LearningQuestions learningQuestions1 = new LearningQuestions();
        learningQuestions1.setQuestion_id("Q1003");
        learningQuestions1.setLikes("102");
        learningQuestions1.setComments("30");
        learningQuestions1.setShares("5");
        learningQuestions1.setMcq1("Direct Current");
        learningQuestions1.setMcq2("Alternate Current");
        learningQuestions1.setMcq3("Indirect Current");
        learningQuestions1.setMcq4("Electric Current");
        learningQuestions1.setAttended_by(45);
        learningQuestions1.setSubject("Chemistry");
        learningQuestions1.setChapter("8th Chapter");
        learningQuestions1.setRecommended_time("40");
        learningQuestions1.setIs_liked(true);
        learningQuestions1.setIs_fav(false);
        learningQuestions1.setCategory("MCQ");
        learningQuestions1.setQuestion("What are the types of current?");
        learningQuestions1.setAnswer("a,b");
        learningQuestions1.setAnswerDesc("There are two types of electric current: direct current (DC) and alternating current (AC). The electrons in direct current flow in one direction. The current produced by a battery is direct current. The electrons in alternating current flow in one direction, then in the opposite direction—over and over again.");
        learningQuestions1.setRating("4.5");
        learningQuestions1.setTopic("Current");
        learningQuestions1.setPosted_on("2020/03/15");
        learningQuestions1.setLastused_on("2020/03/17");
        learningQuestions1.setDifficulty_level("Easy");
        learningQuestions1.setMarks("2");
        learningQuestionsList.add(learningQuestions1);


        LearningQuestions learningQuestions3 = new LearningQuestions();
        learningQuestions3.setQuestion_id("Q1004");
        learningQuestions3.setLikes("90");
        learningQuestions3.setComments("30");
        learningQuestions3.setShares("10");
        learningQuestions3.setRecommended_time("50");
        learningQuestions3.setIs_liked(true);
        learningQuestions3.setIs_fav(true);
        learningQuestions3.setSubject("Mathematics");
        learningQuestions3.setChapter("5th Chapter");
        learningQuestions3.setCategory("TF");
        learningQuestions3.setAttended_by(25);
        learningQuestions3.setQuestion("\\(-2^3  = (-2)^3\\)");
        learningQuestions3.setAnswer("true");
        learningQuestions3.setRating("5");
        learningQuestions3.setTopic("Square");
        learningQuestions3.setPosted_on("2020/03/13");
        learningQuestions3.setLastused_on("2020/03/17");
        learningQuestions3.setDifficulty_level("Easy");
        learningQuestions3.setMarks("2");
        learningQuestionsList.add(learningQuestions3);




        LearningQuestions learningQuestions4 = new LearningQuestions();
        learningQuestions4.setQuestion_id("Q1005");
        learningQuestions4.setRecommended_time("40");
        learningQuestions4.setIs_liked(true);
        learningQuestions4.setIs_fav(false);
        learningQuestions4.setLikes("190");
        learningQuestions4.setComments("50");
        learningQuestions4.setShares("30");
        learningQuestions4.setSubject("Physics");
        learningQuestions4.setChapter("8th Chapter");
        learningQuestions4.setCategory("FIB");
        learningQuestions4.setAttended_by(65);
        learningQuestions4.setQuestion("A vector quantity has both magnitude and ....... while a scalar has only magnitude.");
        learningQuestions4.setAnswer("direction");
        learningQuestions4.setRating("3.5");
        learningQuestions4.setTopic("Direction");
        learningQuestions4.setPosted_on("2020/03/12");
        learningQuestions4.setLastused_on("2020/03/17");
        learningQuestions4.setDifficulty_level("Easy");
        learningQuestions4.setMarks("2");
        learningQuestionsList.add(learningQuestions4);


        LearningQuestions learningQuestions5 = new LearningQuestions();
        learningQuestions5.setQuestion_id("Q1006");
        learningQuestions5.setRecommended_time("50");
        learningQuestions5.setIs_liked(true);
        learningQuestions5.setIs_fav(false);
        learningQuestions5.setLikes("50");
        learningQuestions5.setComments("2");
        learningQuestions5.setShares("2");
        learningQuestions5.setSubject("Chemistry");
        learningQuestions5.setChapter("10th Chapter");
        learningQuestions5.setCategory("MTP");
        learningQuestions5.setAttended_by(20);
        learningQuestions5.setQuestion("Match the pairs");
        learningQuestions5.setA1("Photosynthesis");
        learningQuestions5.setA2("Water");
        learningQuestions5.setA3("Sodium chloride");
        learningQuestions5.setA4("Carbon");
        learningQuestions5.setB1("Ionic bond");
        learningQuestions5.setB2("Reactant in combustion process");
        learningQuestions5.setB3("Chemical change");
        learningQuestions5.setB4("Covalent bond");
        learningQuestions5.setRating("5");
        learningQuestions5.setTopic("Chemical Reactions");
        learningQuestions5.setPosted_on("2020/03/10");
        learningQuestions5.setLastused_on("2020/03/17");
        learningQuestions5.setDifficulty_level("Medium");
        learningQuestions5.setMarks("3");
        learningQuestionsList.add(learningQuestions5);



        LearningQuestions learningQuestions6 = new LearningQuestions();
        learningQuestions6.setQuestion_id("Q1007");
        learningQuestions6.setRecommended_time("40");
        learningQuestions6.setIs_liked(true);
        learningQuestions6.setIs_fav(false);
        learningQuestions6.setLikes("140");
        learningQuestions6.setComments("62");
        learningQuestions6.setShares("12");
        learningQuestions6.setSubject("Chemistry");
        learningQuestions6.setChapter("9th Chapter");
        learningQuestions6.setCategory("AIB");
        learningQuestions6.setAttended_by(80);
        learningQuestions6.setQuestion("Explain charges in the Atom.");
        learningQuestions6.setAnswer("The charges in the atom are crucial in understanding how the atom works. An electron has a negative charge, a proton has a positive charge and a neutron has no charge. Electrons and protons have the same magnitude of charge. Like charges repel, so protons repel one another as do electrons. Opposite charges attract which causes the electrons to be attracted to the protons. As the electrons and protons grow farther apart, the forces they exert on each other decrease.");
        learningQuestions6.setRating("2.5");
        learningQuestions6.setTopic("Atom");
        learningQuestions6.setPosted_on("2020/03/15");
        learningQuestions6.setLastused_on("2020/03/15");
        learningQuestions6.setDifficulty_level("Medium");
        learningQuestions6.setMarks("5");
        learningQuestionsList.add(learningQuestions6);



        LearningQuestions learningQuestions7= new LearningQuestions();
        learningQuestions7.setQuestion_id("Q1008");
        learningQuestions7.setLikes("102");
        learningQuestions7.setComments("30");
        learningQuestions7.setShares("5");
        learningQuestions7.setMcq1("coulombs/volt");
        learningQuestions7.setMcq2("joules/coulomb");
        learningQuestions7.setMcq3("coulombs/second");
        learningQuestions7.setMcq4("ohms/second");
        learningQuestions7.setAttended_by(45);
        learningQuestions7.setSubject("Physics");
        learningQuestions7.setChapter("8th Chapter");
        learningQuestions7.setRecommended_time("40");
        learningQuestions7.setIs_liked(true);
        learningQuestions7.setIs_fav(false);
        learningQuestions7.setCategory("SCQ");
        learningQuestions7.setQuestion("Electric current may be expressed in which one of the following units?");
        learningQuestions7.setQuestiondesc("An electric current is the rate of flow of electric charge.");
        learningQuestions7.setAnswer("c");
        learningQuestions7.setAnswerDesc("Electric current is most commonly measured in units of amperes (A), where 1 ampere is 1 coulomb of electric charge per second. The ampere is the SI unit of electric current. Of course, metric prefixes can be added to the ampere to make units of milliamperes, microamperes, kiloamperes, etc.");
        learningQuestions7.setRating("4.5");
        learningQuestions7.setTopic("Current");
        learningQuestions7.setPosted_on("2020/03/15");
        learningQuestions7.setLastused_on("2020/03/17");
        learningQuestions7.setDifficulty_level("Easy");
        learningQuestions7.setMarks("5");
        learningQuestionsList.add(learningQuestions7);


        LearningQuestions learningQuestions8 = new LearningQuestions();
        learningQuestions8.setQuestion_id("Q1009");
        learningQuestions8.setLikes("90");
        learningQuestions8.setComments("30");
        learningQuestions8.setShares("10");
        learningQuestions8.setRecommended_time("50");
        learningQuestions8.setIs_liked(true);
        learningQuestions8.setIs_fav(true);
        learningQuestions8.setSubject("Chemistry");
        learningQuestions8.setChapter("5th Chapter");
        learningQuestions8.setCategory("TF");
        learningQuestions8.setAttended_by(25);
        learningQuestions8.setQuestion("The properties of solids can be explained by the structure of and the bonding among the metal atoms.");
        learningQuestions8.setAnswer("a");
        learningQuestions8.setRating("5");
        learningQuestions8.setTopic("Atoms");
        learningQuestions8.setPosted_on("2020/03/13");
        learningQuestions8.setLastused_on("2020/03/17");
        learningQuestions8.setDifficulty_level("Easy");
        learningQuestions8.setMarks("2");
        learningQuestionsList.add(learningQuestions8);

        LearningQuestions learningQuestions9 = new LearningQuestions();
        learningQuestions9.setQuestion_id("Q1010");
        learningQuestions9.setRecommended_time("40");
        learningQuestions9.setIs_liked(true);
        learningQuestions9.setIs_fav(false);
        learningQuestions9.setLikes("10");
        learningQuestions9.setComments("2");
        learningQuestions9.setShares("2");
        learningQuestions9.setSubject("Physics");
        learningQuestions9.setChapter("10th Chapter");
        learningQuestions9.setCategory("AIS");
        learningQuestions9.setAttended_by(20);
        learningQuestions9.setQuestion("State Newton's Third Law.");
        learningQuestions9.setAnswer("For every action there is an equal and opposite reaction.");
        learningQuestions9.setRating("5");
        learningQuestions9.setTopic("Newton's Law");
        learningQuestions9.setPosted_on("2020/03/10");
        learningQuestions9.setLastused_on("2020/03/17");
        learningQuestions9.setDifficulty_level("Medium");
        learningQuestions9.setMarks("3");
        learningQuestionsList.add(learningQuestions9);

    }

    @Override
    public void onCommentClick(String questionId) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(this, MainActivity.class));
    }
}
