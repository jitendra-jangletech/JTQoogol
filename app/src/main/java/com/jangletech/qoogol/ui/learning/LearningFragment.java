package com.jangletech.qoogol.ui.learning;

import android.os.Bundle;
import android.util.Log;
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
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.LearningAdapter;
import com.jangletech.qoogol.databinding.LearningFragmentBinding;
import com.jangletech.qoogol.dialog.CommentDialog;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.dialog.PublicProfileDialog;
import com.jangletech.qoogol.dialog.ShareQuestionDialog;
import com.jangletech.qoogol.model.LearningQuestionsNew;
import com.jangletech.qoogol.model.ProcessQuestion;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.util.AppUtils;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;
import com.jangletech.qoogol.util.UtilHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

import static com.jangletech.qoogol.util.Constant.CALL_FROM;
import static com.jangletech.qoogol.util.Constant.learning;
import static com.jangletech.qoogol.util.Constant.profile;

public class LearningFragment extends BaseFragment implements LearningAdapter.onIconClick, PublicProfileDialog.PublicProfileClickListener, CommentDialog.CommentClickListener {

    private static final String TAG = "LearningFragment";
    private LearningViewModel mViewModel;
    LearningFragmentBinding learningFragmentBinding;
    LearningAdapter learningAdapter;
    List<LearningQuestionsNew> learningQuestionsList;
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
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.nav_test_filter, bundle);
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
        learningFragmentBinding.learningSwiperefresh.setRefreshing(true);
        learningQuestionsList = new ArrayList<>();
        questionsNewList = new ArrayList<>();
        userId = String.valueOf(new PreferenceManager(getActivity()).getInt(Constant.USER_ID));

        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.getBoolean("fromNotification")) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Learning");
                if (bundle.getString(Constant.FB_MS_ID) != null)
                    mViewModel.fetchQuestionData(bundle.getString(Constant.FB_MS_ID));

                learningFragmentBinding.learningSwiperefresh.setRefreshing(true);
                mViewModel.getQuestion(bundle.getString(Constant.FB_MS_ID)).observe(getViewLifecycleOwner(), questionsList -> {
                    setData(questionsList);
                });
            } else {
                learningFragmentBinding.learningSwiperefresh.setRefreshing(true);
                if (bundle.getString("call_from").equalsIgnoreCase("saved_questions")) {
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Saved Questions");
                }  else {
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Practice Questions");
                    mViewModel.fetchQuestionData("");
                }

                mViewModel.getQuestionList().observe(getViewLifecycleOwner(), questionsList -> {
                    setData(questionsList);
                });
            }
        }
        learningFragmentBinding.learningSwiperefresh.setOnRefreshListener(() -> {
            mViewModel.fetchQuestionData("");
            mViewModel.getQuestionList().observe(getViewLifecycleOwner(), questionsList -> {
                setData(questionsList);
            });
        });

    }

    private void setData(List<LearningQuestionsNew> questionsList) {
        questionsNewList.clear();
        questionsNewList.addAll(questionsList);
        initRecycler();
    }


    private void initRecycler() {
        learningAdapter = new LearningAdapter(getActivity(), questionsNewList, this, learning);
        learningFragmentBinding.learningRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setAutoMeasureEnabled(false);
        learningFragmentBinding.learningRecycler.setNestedScrollingEnabled(false);
        learningFragmentBinding.learningRecycler.setItemViewCacheSize(20);
        learningFragmentBinding.learningRecycler.setDrawingCacheEnabled(true);
        learningFragmentBinding.learningRecycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        learningFragmentBinding.learningRecycler.setLayoutManager(linearLayoutManager);
        learningAdapter.setHasStableIds(true);
        learningFragmentBinding.learningRecycler.setAdapter(learningAdapter);

        if (learningFragmentBinding.learningSwiperefresh.isRefreshing())
            learningFragmentBinding.learningSwiperefresh.setRefreshing(false);
    }


    private void ProcessQuestionAPI(int que_id, int flag, String call_from, String rating, String feedback) {
        ProgressDialog.getInstance().show(getActivity());
        Call<ProcessQuestion> call;
        int user_id = new PreferenceManager(getActivity()).getInt(Constant.USER_ID);

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
                    if (response.body() != null && response.body().getResponse().equalsIgnoreCase("200")) {

                    } else {
                        Toast.makeText(getActivity(), UtilHelper.getAPIError(String.valueOf(response.body())), Toast.LENGTH_SHORT).show();
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
                apiCallFailureDialog(t);
            }
        });
    }

    @Override
    public void onCommentClick(int questionId) {
        /*Bundle bundle = new Bundle();
        bundle.putString(Constant.CALL_FROM, Module.Learning.toString());
        bundle.putInt("QuestionId", questionId);
        NavHostFragment.findNavController(this).navigate(R.id.nav_comments, bundle);*/
        Log.d(TAG, "onCommentClick questionId : " + questionId);
        CommentDialog commentDialog = new CommentDialog(getActivity(), questionId, false, this);
        commentDialog.show();
    }


    @Override
    public void onShareClick(int questionId) {
        /*Bundle bundle = new Bundle();
        bundle.putInt("QuestionId", questionId);
        bundle.putInt("call_from", learning);
        NavHostFragment.findNavController(this).navigate(R.id.nav_share, bundle);*/
        new ShareQuestionDialog(getActivity(), String.valueOf(questionId), AppUtils.getUserId()
                , getDeviceId(getActivity()), "Q")
                .show();
    }


    @Override
    public void onSubmitClick(int questionId, int isRight) {
        ProcessQuestionAPI(questionId, isRight, "submit", "", "");
    }



    @Override
    public void onLikeClick(String userId) {
        Bundle bundle = new Bundle();
        if (userId.equalsIgnoreCase(new PreferenceManager(getActivity()).getUserId())) {
            bundle.putInt(CALL_FROM, profile);
            NavHostFragment.findNavController(this).navigate(R.id.nav_edit_profile, bundle);
        } else {
            PublicProfileDialog publicProfileDialog = new PublicProfileDialog(getActivity(), userId, this);
            publicProfileDialog.show();
//            bundle.putInt(CALL_FROM, connectonId);
//            bundle.putString(Constant.fetch_profile_id,userId);
        }
    }

    @Override
    public void onViewImage(String path) {
        showFullScreen(path);
    }

    @Override
    public void onCommentClick(String userId) {
        PublicProfileDialog publicProfileDialog = new PublicProfileDialog(getActivity(), userId, this);
        publicProfileDialog.show();
    }
}
