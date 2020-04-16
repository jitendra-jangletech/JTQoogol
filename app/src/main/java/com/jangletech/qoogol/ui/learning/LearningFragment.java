package com.jangletech.qoogol.ui.learning;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.LearingAdapter;
import com.jangletech.qoogol.databinding.LearningFragmentBinding;
import com.jangletech.qoogol.model.LearningQuestions;

import java.util.ArrayList;
import java.util.List;

public class LearningFragment extends Fragment implements LearingAdapter.onIconClick {

    private LearningViewModel mViewModel;
    LearningFragmentBinding learningFragmentBinding;
    LearingAdapter learingAdapter;
    List<LearningQuestions> learningQuestionsList;

    public static LearningFragment newInstance() {
        return new LearningFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        learningFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.learning_fragment, container, false);
        setHasOptionsMenu(true);
        return learningFragmentBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(LearningViewModel.class);
        initView();
        setData();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.filter_menus, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_subject:
                Toast.makeText(getActivity(), "Subject Icon Click", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_chapter:
                Toast.makeText(getActivity(), "Chapter Click", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_rating:
                Toast.makeText(getActivity(), "Rating  Click", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_difflevel:
                Toast.makeText(getActivity(), "Diff level Click", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        learingAdapter.notifyDataSetChanged();
    }

    private void initView() {
        learningQuestionsList = new ArrayList<>();
        learingAdapter = new LearingAdapter(getActivity(), learningQuestionsList,this);
        learningFragmentBinding.learningRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        learningFragmentBinding.learningRecycler.setLayoutManager(linearLayoutManager);
        learningFragmentBinding.learningRecycler.setAdapter(learingAdapter);
    }

    private void setData() {
        learningQuestionsList.clear();
        LearningQuestions learningQuestions = new LearningQuestions();
        learningQuestions.setQuestion_id("Q1000");
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
        learningQuestions1.setQuestion_id("Q1044");
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
        learningQuestions3.setQuestion_id("Q1006");
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
        learningQuestions4.setQuestion_id("Q1004");
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
        learningQuestions5.setQuestion_id("Q1003");
        learningQuestions5.setRecommended_time("40");
        learningQuestions5.setIs_liked(true);
        learningQuestions5.setIs_fav(false);
        learningQuestions5.setLikes("10");
        learningQuestions5.setComments("2");
        learningQuestions5.setShares("2");
        learningQuestions5.setSubject("Physics");
        learningQuestions5.setChapter("10th Chapter");
        learningQuestions5.setCategory("AIS");
        learningQuestions5.setAttended_by(20);
        learningQuestions5.setQuestion("State Newton's Third Law.");
        learningQuestions5.setAnswer("For every action there is an equal and opposite reaction.");
        learningQuestions5.setRating("5");
        learningQuestions5.setTopic("Newton's Law");
        learningQuestions5.setPosted_on("2020/03/10");
        learningQuestions5.setLastused_on("2020/03/17");
        learningQuestions5.setDifficulty_level("Medium");
        learningQuestions5.setMarks("3");
        learningQuestionsList.add(learningQuestions5);


        LearningQuestions learningQuestions6 = new LearningQuestions();
        learningQuestions6.setQuestion_id("Q1002");
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
        learningQuestions7.setQuestion_id("Q1001");
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
        learningQuestions8.setQuestion_id("Q1006");
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
    }


    @Override
    public void onCommentClick(String questionId) {
        Bundle bundle = new Bundle();
        bundle.putString("QuestionId", questionId);
        NavHostFragment.findNavController(this).navigate(R.id.action_nav_learning_to_nav_comments, bundle);
    }
}
