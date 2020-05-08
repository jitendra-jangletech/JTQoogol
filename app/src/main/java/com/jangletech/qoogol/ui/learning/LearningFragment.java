package com.jangletech.qoogol.ui.learning;

import android.os.AsyncTask;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jangletech.qoogol.activities.MainActivity;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.LearingAdapter;
import com.jangletech.qoogol.database.QoogolDatabase;
import com.jangletech.qoogol.databinding.LearningFragmentBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.enums.Module;
import com.jangletech.qoogol.model.LearningQuestResponse;
import com.jangletech.qoogol.model.LearningQuestions;
import com.jangletech.qoogol.model.LearningQuestionsNew;
import com.jangletech.qoogol.model.ProcessQuestion;
import com.jangletech.qoogol.model.ResponseObj;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;
import com.jangletech.qoogol.util.UtilHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.jangletech.qoogol.util.Constant.learning;

public class LearningFragment extends Fragment implements LearingAdapter.onIconClick {

    private LearningViewModel mViewModel;
    LearningFragmentBinding learningFragmentBinding;
    LearingAdapter learingAdapter;
    List<LearningQuestions> learningQuestionsList;
    List<LearningQuestionsNew> questionsNewList;
    ApiInterface apiService = ApiClient.getInstance().getApi();
    String userId = "";

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

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.action_search, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                Bundle bundle = new Bundle();
                bundle.putString("call_from", "learning");
                MainActivity.navController.navigate(R.id.nav_test_filter, bundle);
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
        questionsNewList = new ArrayList<>();
        learingAdapter = new LearingAdapter(getActivity(), questionsNewList, this, learning);
        learningFragmentBinding.learningRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        learningFragmentBinding.learningRecycler.setLayoutManager(linearLayoutManager);
        learningFragmentBinding.learningRecycler.setAdapter(learingAdapter);
        userId = String.valueOf(new PreferenceManager(getActivity()).getInt(Constant.USER_ID));

        Bundle bundle = getArguments();
        if (bundle.getString("call_from").equalsIgnoreCase("saved_questions")) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Saved Questions");
//            learningQuestionsList.clear();
//            new getDataFromDb().execute();
        } else {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Learning");
        }
        getDataFromApi();

        learningFragmentBinding.learningSwiperefresh.setOnRefreshListener(() -> getDataFromApi());
    }
    public void checkRefresh () {
        if ( learningFragmentBinding.learningSwiperefresh.isRefreshing()) {
            learningFragmentBinding.learningSwiperefresh.setRefreshing(false);
        }
    }

    private void getDataFromApi() {
        ProgressDialog.getInstance().show(getActivity());
        Call<LearningQuestResponse> call = apiService.fetchQAApi(userId);
        call.enqueue(new Callback<LearningQuestResponse>() {
            @Override
            public void onResponse(Call<LearningQuestResponse> call, retrofit2.Response<LearningQuestResponse> response) {
                try {
                    ProgressDialog.getInstance().dismiss();
                    questionsNewList.clear();
                    if (response.body()!=null && response.body().getResponse().equalsIgnoreCase("200")){
                        questionsNewList = response.body().getQuestion_list();
                        learingAdapter.updateList(questionsNewList);
                        checkRefresh();
                    } else {
                        checkRefresh();
                        Toast.makeText(getActivity(), UtilHelper.getAPIError(String.valueOf(response.body())),Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    checkRefresh();
                    ProgressDialog.getInstance().dismiss();
                }
            }

            @Override
            public void onFailure(Call<LearningQuestResponse> call, Throwable t) {
                t.printStackTrace();
                checkRefresh();
                ProgressDialog.getInstance().dismiss();
            }
        });
    }

    private void setAdapter() {
        learingAdapter = new LearingAdapter(getActivity(), questionsNewList, this, learning);
        learningFragmentBinding.learningRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        learningFragmentBinding.learningRecycler.setLayoutManager(linearLayoutManager);
        learningFragmentBinding.learningRecycler.setAdapter(learingAdapter);
    }

    private void ProcessQuestionAPI(String user_id, String que_id, String api_case, int flag, String call_from) {
        ProgressDialog.getInstance().show(getActivity());
        Call<ProcessQuestion> call;

        if (call_from.equalsIgnoreCase("like"))
         call = apiService.likeApi(user_id, que_id, api_case, flag);
        else
            call = apiService.favApi(user_id, que_id, api_case, flag);

        call.enqueue(new Callback<ProcessQuestion>() {
            @Override
            public void onResponse(Call<ProcessQuestion> call, retrofit2.Response<ProcessQuestion> response) {
                try {
                    questionsNewList.clear();
                    if (response.body()!=null && response.body().getResponse().equalsIgnoreCase("200")){
                        getDataFromApi();
                    } else {
                        Toast.makeText(getActivity(), UtilHelper.getAPIError(String.valueOf(response.body())),Toast.LENGTH_SHORT).show();
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


    class getDataFromDb extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            //adding to database
            try {
                learningQuestionsList = QoogolDatabase.getDatabase(getApplicationContext())
                        .learningQuestionDao()
                        .getAll();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            learingAdapter.notifyDataSetChanged();
        }
    }

    private void setData() {
        ArrayList<String> imglist = new ArrayList<>();
        ArrayList<String> imglist1 = new ArrayList<>();
        learningQuestionsList.clear();

        LearningQuestions learningQuestions = new LearningQuestions();
        learningQuestions.setQuestion_id("Q1001");
        learningQuestions.setLikes("90");
        learningQuestions.setComments("30");
        learningQuestions.setShares("10");
        learningQuestions.setRecommended_time("50");
        learningQuestions.setIs_liked(false);
        learningQuestions.setIs_fav(false);
        learningQuestions.setSubject("Mathematics");
        learningQuestions.setChapter("5th Chapter");
        learningQuestions.setCategory("SCQ_img");
        learningQuestions.setAttended_by(25);
        learningQuestions.setQuestion("Select Cartesian grapgh.");
        learningQuestions.setAnswer("d");
        learningQuestions.setAnswerDesc("Cartesian graphs have numbers on both axes, which therefore allow you to show how changes in one thing affect another.");
        learningQuestions.setMcq1("https://www.skillsyouneed.com/images/pie-chart.png");
        learningQuestions.setMcq2("https://www.skillsyouneed.com/images/graph1.png");
        learningQuestions.setMcq3("https://www.skillsyouneed.com/images/line-chart-sales.png");
        learningQuestions.setMcq4("https://www.skillsyouneed.com/images/cartesian-graph.png");
        learningQuestions.setRating("5");
        learningQuestions.setTopic("Charts");
        learningQuestions.setPosted_on("2020/03/13");
        learningQuestions.setLastused_on("2020/03/17");
        learningQuestions.setDifficulty_level("Easy");
        learningQuestions.setMarks("2");
        learningQuestionsList.add(learningQuestions);

        LearningQuestions learningQuestions1 = new LearningQuestions();
        learningQuestions1.setQuestion_id("Q1002");
        learningQuestions1.setLikes("90");
        learningQuestions1.setComments("30");
        learningQuestions1.setShares("10");
        learningQuestions1.setRecommended_time("50");
        learningQuestions1.setIs_liked(true);
        learningQuestions1.setIs_fav(true);
        learningQuestions1.setSubject("Geometry");
        learningQuestions1.setChapter("5th Chapter");
        learningQuestions1.setCategory("MCQ_img");
        learningQuestions1.setAttended_by(25);
        learningQuestions1.setQuestion("Which Three-Dimensional Shapes?");
        learningQuestions1.setAnswer("a,b");
        learningQuestions1.setMcq1("https://www.skillsyouneed.com/images/geo/cylinder.png");
        learningQuestions1.setMcq2("https://www.skillsyouneed.com/images/geo/cone.png");
        learningQuestions1.setMcq3("https://www.skillsyouneed.com/images/geo/area-hexagon.png");
        learningQuestions1.setMcq4("https://www.skillsyouneed.com/images/geo/symbols.png");
        learningQuestions1.setRating("5");
        learningQuestions1.setTopic("Dimensions");
        learningQuestions1.setPosted_on("2020/03/13");
        learningQuestions1.setLastused_on("2020/03/17");
        learningQuestions1.setDifficulty_level("Easy");
        learningQuestions1.setMarks("2");
        learningQuestionsList.add(learningQuestions1);


        LearningQuestions learningQuestions2 = new LearningQuestions();
        learningQuestions2.setQuestion_id("Q1003");
        learningQuestions2.setLikes("90");
        learningQuestions2.setComments("30");
        learningQuestions2.setShares("10");
        learningQuestions2.setRecommended_time("50");
        learningQuestions2.setIs_liked(true);
        learningQuestions2.setIs_fav(true);
        learningQuestions2.setSubject("Science");
        imglist.clear();
        imglist.add("https://www.thoughtco.com/thmb/bORUDzdznV2AzCzV3jGumQBhmPI=/768x300/filters:no_upscale():max_bytes(150000):strip_icc()/GettyImages-102635591-56acf9ec5f9b58b7d00ad548.jpg");
        learningQuestions2.setAttchment(imglist);
        learningQuestions2.setChapter("5th Chapter");
        learningQuestions2.setCategory("SCQ_text");
        learningQuestions2.setAttended_by(25);
        learningQuestions2.setQuestion("This one is intended to be a confidence builder. These are diamonds. Diamonds are pure:");
        learningQuestions2.setAnswer("b");
        learningQuestions2.setAnswerDesc("Like other elements, carbon occurs in different forms, which are called allotropes. Allotropes of carbon include transparent diamond, gray graphite (pencil \"lead\"), and black amorphous carbon (soot).");
        learningQuestions2.setMcq1("boron");
        learningQuestions2.setMcq2("carbon");
        learningQuestions2.setMcq3("iron");
        learningQuestions2.setMcq4("nitrogen");
        learningQuestions2.setRating("5");
        learningQuestions2.setTopic("Element");
        learningQuestions2.setPosted_on("2020/03/13");
        learningQuestions2.setLastused_on("2020/03/17");
        learningQuestions2.setDifficulty_level("Easy");
        learningQuestions2.setMarks("2");
        learningQuestionsList.add(learningQuestions2);

        LearningQuestions learningQuestions3 = new LearningQuestions();
        learningQuestions3.setQuestion_id("Q1004");
        learningQuestions3.setLikes("90");
        learningQuestions3.setComments("30");
        learningQuestions3.setShares("10");
        learningQuestions3.setRecommended_time("50");
        learningQuestions3.setIs_liked(true);
        learningQuestions3.setIs_fav(true);
        learningQuestions3.setSubject("General Knowledge");
        imglist1.clear();
        imglist1.add("https://homepages.cae.wisc.edu/~ece533/images/boat.png");
        imglist1.add("https://homepages.cae.wisc.edu/~ece533/images/fruits.png");
        imglist1.add("https://homepages.cae.wisc.edu/~ece533/images/mountain.png");
        learningQuestions3.setAttchment(imglist1);
        learningQuestions3.setChapter("5th Chapter");
        learningQuestions3.setCategory("SCQ_text");
        learningQuestions3.setAttended_by(25);
        learningQuestions3.setQuestion("Which image contain mountains");
        learningQuestions3.setAnswer("c");
        learningQuestions3.setAnswerDesc("Like other elements, carbon occurs in different forms, which are called allotropes. Allotropes of carbon include transparent diamond, gray graphite (pencil \"lead\"), and black amorphous carbon (soot).");
        learningQuestions3.setMcq1("1");
        learningQuestions3.setMcq2("2");
        learningQuestions3.setMcq3("3");
        learningQuestions3.setMcq4("None of the above");
        learningQuestions3.setRating("5");
        learningQuestions3.setTopic("Image analysis");
        learningQuestions3.setPosted_on("2020/03/13");
        learningQuestions3.setLastused_on("2020/03/17");
        learningQuestions3.setDifficulty_level("Easy");
        learningQuestions3.setMarks("2");
        learningQuestionsList.add(learningQuestions3);


        LearningQuestions learningQuestions4 = new LearningQuestions();
        learningQuestions4.setQuestion_id("Q1005");
        learningQuestions4.setLikes("102");
        learningQuestions4.setComments("30");
        learningQuestions4.setShares("5");
        learningQuestions4.setMcq1("\\((-2)^6\\)");
        learningQuestions4.setMcq2("\\((-2)^5\\)");
        learningQuestions4.setMcq3("\\((2)^5\\)");
        learningQuestions4.setMcq4("\\((2)^6\\)");
        learningQuestions4.setAttended_by(45);
        learningQuestions4.setSubject("Mathematics");
        learningQuestions4.setChapter("8th Chapter");
        learningQuestions4.setRecommended_time("40");
        learningQuestions4.setIs_liked(true);
        learningQuestions4.setIs_fav(false);
        learningQuestions4.setCategory("SCQ_text");
        learningQuestions4.setQuestion("find the value of \\([(-2)^2]^3\\)");
        learningQuestions4.setAnswer("a");
        learningQuestions4.setAnswerDesc("It will multiply 2 and 3");
        learningQuestions4.setRating("4.5");
        learningQuestions4.setTopic("Square");
        learningQuestions4.setPosted_on("2020/03/15");
        learningQuestions4.setLastused_on("2020/03/17");
        learningQuestions4.setDifficulty_level("Easy");
        learningQuestions4.setMarks("5");
        learningQuestionsList.add(learningQuestions4);

        LearningQuestions learningQuestions5 = new LearningQuestions();
        learningQuestions5.setQuestion_id("Q1006");
        learningQuestions5.setLikes("102");
        learningQuestions5.setComments("30");
        learningQuestions5.setShares("5");
        learningQuestions5.setMcq1("Direct Current");
        learningQuestions5.setMcq2("Alternate Current");
        learningQuestions5.setMcq3("Indirect Current");
        learningQuestions5.setMcq4("Electric Current");
        learningQuestions5.setAttended_by(45);
        learningQuestions5.setSubject("Chemistry");
        learningQuestions5.setChapter("8th Chapter");
        learningQuestions5.setRecommended_time("40");
        learningQuestions5.setIs_liked(true);
        learningQuestions5.setIs_fav(false);
        learningQuestions5.setCategory("MCQ_text");
        learningQuestions5.setQuestion("What are the types of current?");
        learningQuestions5.setAnswer("a,b");
        learningQuestions5.setAnswerDesc("There are two types of electric current: direct current (DC) and alternating current (AC). The electrons in direct current flow in one direction. The current produced by a battery is direct current. The electrons in alternating current flow in one direction, then in the opposite direction—over and over again.");
        learningQuestions5.setRating("4.5");
        learningQuestions5.setTopic("Current");
        learningQuestions5.setPosted_on("2020/03/15");
        learningQuestions5.setLastused_on("2020/03/17");
        learningQuestions5.setDifficulty_level("Easy");
        learningQuestions5.setMarks("2");
        learningQuestionsList.add(learningQuestions5);


        LearningQuestions learningQuestions6 = new LearningQuestions();
        learningQuestions6.setQuestion_id("Q1007");
        learningQuestions6.setLikes("90");
        learningQuestions6.setComments("30");
        learningQuestions6.setShares("10");
        learningQuestions6.setRecommended_time("50");
        learningQuestions6.setIs_liked(true);
        learningQuestions6.setIs_fav(true);
        learningQuestions6.setSubject("Mathematics");
        learningQuestions6.setChapter("5th Chapter");
        learningQuestions6.setCategory("TF");
        learningQuestions6.setAttended_by(25);
        learningQuestions6.setQuestion("\\(-2^3  = (-2)^3\\)");
        learningQuestions6.setAnswer("true");
        learningQuestions6.setRating("5");
        learningQuestions6.setTopic("Square");
        learningQuestions6.setPosted_on("2020/03/13");
        learningQuestions6.setLastused_on("2020/03/17");
        learningQuestions6.setDifficulty_level("Easy");
        learningQuestions6.setMarks("2");
        learningQuestionsList.add(learningQuestions6);


        LearningQuestions learningQuestions7 = new LearningQuestions();
        learningQuestions7.setQuestion_id("Q1008");
        learningQuestions7.setRecommended_time("40");
        learningQuestions7.setIs_liked(true);
        learningQuestions7.setIs_fav(false);
        learningQuestions7.setLikes("190");
        learningQuestions7.setComments("50");
        learningQuestions7.setShares("30");
        learningQuestions7.setSubject("Physics");
        learningQuestions7.setChapter("8th Chapter");
        learningQuestions7.setCategory("FIB");
        learningQuestions7.setAttended_by(65);
        learningQuestions7.setQuestion("A vector quantity has both magnitude and ....... while a scalar has only magnitude.");
        learningQuestions7.setAnswer("direction");
        learningQuestions7.setRating("3.5");
        learningQuestions7.setTopic("Direction");
        learningQuestions7.setPosted_on("2020/03/12");
        learningQuestions7.setLastused_on("2020/03/17");
        learningQuestions7.setDifficulty_level("Easy");
        learningQuestions7.setMarks("2");
        learningQuestionsList.add(learningQuestions7);


        LearningQuestions learningQuestions8 = new LearningQuestions();
        learningQuestions8.setQuestion_id("Q1009");
        learningQuestions8.setRecommended_time("50");
        learningQuestions8.setIs_liked(true);
        learningQuestions8.setIs_fav(false);
        learningQuestions8.setLikes("50");
        learningQuestions8.setComments("2");
        learningQuestions8.setShares("2");
        learningQuestions8.setSubject("Chemistry");
        learningQuestions8.setChapter("10th Chapter");
        learningQuestions8.setCategory("MTP");
        learningQuestions8.setAttended_by(20);
        learningQuestions8.setQuestion("Match the pairs");
        learningQuestions8.setA1("Photosynthesis");
        learningQuestions8.setA2("Water");
        learningQuestions8.setA3("Sodium chloride");
        learningQuestions8.setA4("Carbon");
        learningQuestions8.setB1("Ionic bond");
        learningQuestions8.setB2("Reactant in combustion process");
        learningQuestions8.setB3("Chemical change");
        learningQuestions8.setB4("Covalent bond");
        learningQuestions8.setRating("5");
        learningQuestions8.setTopic("Chemical Reactions");
        learningQuestions8.setPosted_on("2020/03/10");
        learningQuestions8.setLastused_on("2020/03/17");
        learningQuestions8.setDifficulty_level("Medium");
        learningQuestions8.setMarks("3");
        learningQuestionsList.add(learningQuestions8);


        LearningQuestions learningQuestions9 = new LearningQuestions();
        learningQuestions9.setQuestion_id("Q1010");
        learningQuestions9.setRecommended_time("40");
        learningQuestions9.setIs_liked(true);
        learningQuestions9.setIs_fav(false);
        learningQuestions9.setLikes("140");
        learningQuestions9.setComments("62");
        learningQuestions9.setShares("12");
        learningQuestions9.setSubject("Chemistry");
        learningQuestions9.setChapter("9th Chapter");
        learningQuestions9.setCategory("AIB");
        learningQuestions9.setAttended_by(80);
        learningQuestions9.setQuestion("Explain charges in the Atom.");
        learningQuestions9.setAnswer("The charges in the atom are crucial in understanding how the atom works. An electron has a negative charge, a proton has a positive charge and a neutron has no charge. Electrons and protons have the same magnitude of charge. Like charges repel, so protons repel one another as do electrons. Opposite charges attract which causes the electrons to be attracted to the protons. As the electrons and protons grow farther apart, the forces they exert on each other decrease.");
        learningQuestions9.setRating("2.5");
        learningQuestions9.setTopic("Atom");
        learningQuestions9.setPosted_on("2020/03/15");
        learningQuestions9.setLastused_on("2020/03/15");
        learningQuestions9.setDifficulty_level("Medium");
        learningQuestions9.setMarks("5");
        learningQuestionsList.add(learningQuestions9);


        LearningQuestions learningQuestions10 = new LearningQuestions();
        learningQuestions10.setQuestion_id("Q1011");
        learningQuestions10.setLikes("102");
        learningQuestions10.setComments("30");
        learningQuestions10.setShares("5");
        learningQuestions10.setMcq1("coulombs/volt");
        learningQuestions10.setMcq2("joules/coulomb");
        learningQuestions10.setMcq3("coulombs/second");
        learningQuestions10.setMcq4("ohms/second");
        learningQuestions10.setAttended_by(45);
        learningQuestions10.setSubject("Physics");
        learningQuestions10.setChapter("8th Chapter");
        learningQuestions10.setRecommended_time("40");
        learningQuestions10.setIs_liked(true);
        learningQuestions10.setIs_fav(false);
        learningQuestions10.setCategory("SCQ_text");
        learningQuestions10.setQuestion("Electric current may be expressed in which one of the following units?");
        learningQuestions10.setQuestiondesc("An electric current is the rate of flow of electric charge.");
        learningQuestions10.setAnswer("c");
        learningQuestions10.setAnswerDesc("Electric current is most commonly measured in units of amperes (A), where 1 ampere is 1 coulomb of electric charge per second. The ampere is the SI unit of electric current. Of course, metric prefixes can be added to the ampere to make units of milliamperes, microamperes, kiloamperes, etc.");
        learningQuestions10.setRating("4.5");
        learningQuestions10.setTopic("Current");
        learningQuestions10.setPosted_on("2020/03/15");
        learningQuestions10.setLastused_on("2020/03/17");
        learningQuestions10.setDifficulty_level("Easy");
        learningQuestions10.setMarks("5");
        learningQuestionsList.add(learningQuestions10);


        LearningQuestions learningQuestions11 = new LearningQuestions();
        learningQuestions11.setQuestion_id("Q1012");
        learningQuestions11.setLikes("90");
        learningQuestions11.setComments("30");
        learningQuestions11.setShares("10");
        learningQuestions11.setRecommended_time("50");
        learningQuestions11.setIs_liked(true);
        learningQuestions11.setIs_fav(true);
        learningQuestions11.setSubject("Chemistry");
        learningQuestions11.setChapter("5th Chapter");
        learningQuestions11.setCategory("TF");
        learningQuestions11.setAttended_by(25);
        learningQuestions11.setQuestion("The properties of solids can be explained by the structure of and the bonding among the metal atoms.");
        learningQuestions11.setAnswer("true");
        learningQuestions11.setRating("5");
        learningQuestions11.setTopic("Atoms");
        learningQuestions11.setPosted_on("2020/03/13");
        learningQuestions11.setLastused_on("2020/03/17");
        learningQuestions11.setDifficulty_level("Easy");
        learningQuestions11.setMarks("2");
        learningQuestionsList.add(learningQuestions11);

        LearningQuestions learningQuestions12 = new LearningQuestions();
        learningQuestions12.setQuestion_id("Q1013");
        learningQuestions12.setRecommended_time("40");
        learningQuestions12.setIs_liked(true);
        learningQuestions12.setIs_fav(false);
        learningQuestions12.setLikes("10");
        learningQuestions12.setComments("2");
        learningQuestions12.setShares("2");
        learningQuestions12.setSubject("Physics");
        learningQuestions12.setChapter("10th Chapter");
        learningQuestions12.setCategory("AIS");
        learningQuestions12.setAttended_by(20);
        learningQuestions12.setQuestion("State Newton's Third Law.");
        learningQuestions12.setAnswer("For every action there is an equal and opposite reaction.");
        learningQuestions12.setRating("5");
        learningQuestions12.setTopic("Newton's Law");
        learningQuestions12.setPosted_on("2020/03/10");
        learningQuestions12.setLastused_on("2020/03/17");
        learningQuestions12.setDifficulty_level("Medium");
        learningQuestions12.setMarks("3");
        learningQuestionsList.add(learningQuestions12);

        learingAdapter.notifyDataSetChanged();
    }


    @Override
    public void onCommentClick(String questionId) {
        Bundle bundle = new Bundle();
        bundle.putString(Constant.CALL_FROM, Module.Learning.toString());
        bundle.putString("QuestionId", questionId);
        NavHostFragment.findNavController(this).navigate(R.id.nav_comments, bundle);
    }

    @Override
    public void onLikeClick(String questionId, int isLiked) {
        ProcessQuestionAPI(userId, questionId,"I",isLiked, "like");
    }

    @Override
    public void onShareClick(String questionId) {
        Bundle bundle = new Bundle();
        bundle.putString("QuestionId", questionId);
        NavHostFragment.findNavController(this).navigate(R.id.nav_share, bundle);
    }

    @Override
    public void onFavouriteClick(String questionId, int isFav) {
        ProcessQuestionAPI(userId, questionId,"I", isFav, "fav");
    }
}
