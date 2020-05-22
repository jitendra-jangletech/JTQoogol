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
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.activities.MainActivity;
import com.jangletech.qoogol.adapter.LearingAdapter;
import com.jangletech.qoogol.database.QoogolDatabase;
import com.jangletech.qoogol.databinding.LearningFragmentBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.enums.Module;
import com.jangletech.qoogol.model.LearningQuestResponse;
import com.jangletech.qoogol.model.LearningQuestions;
import com.jangletech.qoogol.model.LearningQuestionsNew;
import com.jangletech.qoogol.model.ProcessQuestion;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;
import com.jangletech.qoogol.util.UtilHelper;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.jangletech.qoogol.util.Constant.learning;

public class LearningFragment extends BaseFragment implements LearingAdapter.onIconClick {

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
    }

    private void initView() {
        learningFragmentBinding.learningSwiperefresh.setRefreshing(false);
        learningQuestionsList = new ArrayList<>();
        questionsNewList = new ArrayList<>();
        userId = String.valueOf(new PreferenceManager(getActivity()).getInt(Constant.USER_ID));

        Bundle bundle = getArguments();
       if (bundle!=null) {
           if (bundle.getBoolean("fromNotification")) {
               ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Learning");
               if (bundle.getString(Constant.FB_MS_ID) != null)
               getDataFromApi(bundle.getString(Constant.FB_MS_ID));
           } else {
               if (bundle.getString("call_from").equalsIgnoreCase("saved_questions")) {
                   ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Saved Questions");
               } else {
                   ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Learning");
               }
               getDataFromApi("");
           }

       }

       learningFragmentBinding.learningSwiperefresh.setOnRefreshListener(() -> getDataFromApi(""));
    }

    private void getDataFromApi(String q_id) {
        ProgressDialog.getInstance().show(getActivity());
        Call<LearningQuestResponse> call = apiService.fetchQAApi(new PreferenceManager(getApplicationContext()).getInt(Constant.USER_ID),q_id);
        call.enqueue(new Callback<LearningQuestResponse>() {
            @Override
            public void onResponse(Call<LearningQuestResponse> call, retrofit2.Response<LearningQuestResponse> response) {
                    learningFragmentBinding.learningSwiperefresh.setRefreshing(false);
                try {
                    ProgressDialog.getInstance().dismiss();
                    questionsNewList.clear();
                    if (response.body()!=null && response.body().getResponse().equalsIgnoreCase("200")){
                        questionsNewList = response.body().getQuestion_list();
                        initRecycler();
//                        learingAdapter.updateList(questionsNewList);
                    } else {
                        Toast.makeText(getActivity(), UtilHelper.getAPIError(String.valueOf(response.body())),Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ProgressDialog.getInstance().dismiss();
                }
            }

            @Override
            public void onFailure(Call<LearningQuestResponse> call, Throwable t) {
                t.printStackTrace();
                learningFragmentBinding.learningSwiperefresh.setRefreshing(false);
                ProgressDialog.getInstance().dismiss();
            }
        });
    }

    private void initRecycler() {
        learingAdapter = new LearingAdapter(getActivity(), questionsNewList, this, learning);
        learningFragmentBinding.learningRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setAutoMeasureEnabled(false);
        learningFragmentBinding.learningRecycler.setLayoutManager(linearLayoutManager);
        learningFragmentBinding.learningRecycler.setAdapter(learingAdapter);
    }

    private void ProcessQuestionAPI(String que_id, int flag, String call_from, String rating, String feedback) {
        ProgressDialog.getInstance().show(getActivity());
        Call<ProcessQuestion> call;
        int user_id  = new PreferenceManager(getApplicationContext()).getInt(Constant.USER_ID);

        if (call_from.equalsIgnoreCase("like"))
         call = apiService.likeApi(user_id, que_id, "I", flag);
        else if (call_from.equalsIgnoreCase("fav"))
            call = apiService.favApi(user_id, que_id, "I", flag);
        else if (call_from.equalsIgnoreCase("submit"))
            call = apiService.questionAttemptApi(user_id, que_id, "I", 1, flag);
        else
            call = apiService.addRatingsApi(user_id, que_id, "I", rating, feedback);

        call.enqueue(new Callback<ProcessQuestion>() {
            @Override
            public void onResponse(Call<ProcessQuestion> call, retrofit2.Response<ProcessQuestion> response) {
                try {
                    if (response.body()!=null && response.body().getResponse().equalsIgnoreCase("200")){

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


    @Override
    public void onCommentClick(String questionId) {
        Bundle bundle = new Bundle();
        bundle.putString(Constant.CALL_FROM, Module.Learning.toString());
        bundle.putString("QuestionId", questionId);
        NavHostFragment.findNavController(this).navigate(R.id.nav_comments, bundle);
    }

    @Override
    public void onLikeClick(String questionId, int isLiked) {
        ProcessQuestionAPI(questionId, isLiked, "like","","");
    }

    @Override
    public void onShareClick(String questionId) {
        Bundle bundle = new Bundle();
        bundle.putString("QuestionId", questionId);
        NavHostFragment.findNavController(this).navigate(R.id.nav_share, bundle);
    }

    @Override
    public void onFavouriteClick(String questionId, int isFav) {
        ProcessQuestionAPI(questionId,isFav, "fav","","");
    }

    @Override
    public void onSubmitClick(String questionId, int isRight) {
        ProcessQuestionAPI(questionId, isRight, "submit","","");
    }

    @Override
    public void onRatingSubmit(String questionId, String rating, String feedbak) {
        ProcessQuestionAPI(questionId, 0, "rating",rating,feedbak);
    }
}
