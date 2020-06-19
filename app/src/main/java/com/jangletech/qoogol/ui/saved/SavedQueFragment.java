package com.jangletech.qoogol.ui.saved;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.SavedQueAdapter;
import com.jangletech.qoogol.databinding.LearningFragmentBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.enums.Module;
import com.jangletech.qoogol.model.LearningQuestions;
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
import static com.jangletech.qoogol.util.Constant.CALL_FROM;
import static com.jangletech.qoogol.util.Constant.connectonId;
import static com.jangletech.qoogol.util.Constant.learning;
import static com.jangletech.qoogol.util.Constant.profile;

public class SavedQueFragment extends BaseFragment implements SavedQueAdapter.onIconClick {

    private SavedViewModel mViewModel;
    LearningFragmentBinding learningFragmentBinding;
    SavedQueAdapter learingAdapter;
    List<LearningQuestions> learningQuestionsList;
    List<LearningQuestions> questionsNewList;
    ApiInterface apiService = ApiClient.getInstance().getApi();


    public static SavedQueFragment newInstance() {
        return new SavedQueFragment();
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
        mViewModel = ViewModelProviders.of(this).get(SavedViewModel.class);
        initView();
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    private void initView() {
        learningFragmentBinding.learningSwiperefresh.setRefreshing(true);
        learningQuestionsList = new ArrayList<>();
        questionsNewList = new ArrayList<>();

        mViewModel.fetchQuestionData();

        mViewModel.getQuestionList().observe(getViewLifecycleOwner(), questionsList -> {
            questionsNewList.clear();
            questionsNewList.addAll(questionsList);
            initRecycler();
        });

        SavedQueFragment savedQueFragment = this;
        learningFragmentBinding.learningSwiperefresh.setOnRefreshListener(() -> getFragmentManager().beginTransaction().detach(savedQueFragment).attach(savedQueFragment).commit());
    }


    private void initRecycler() {
        learingAdapter = new SavedQueAdapter(getActivity(), questionsNewList, this, learning);
        learningFragmentBinding.learningRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setAutoMeasureEnabled(false);
        learningFragmentBinding.learningRecycler.setLayoutManager(linearLayoutManager);
        learningFragmentBinding.learningRecycler.setAdapter(learingAdapter);
        learningFragmentBinding.learningRecycler.setNestedScrollingEnabled(false);
        learningFragmentBinding.learningRecycler.setItemViewCacheSize(20);
        learningFragmentBinding.learningRecycler.setDrawingCacheEnabled(true);
        learningFragmentBinding.learningRecycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        if (learningFragmentBinding.learningSwiperefresh.isRefreshing())
            learningFragmentBinding.learningSwiperefresh.setRefreshing(false);
    }


    private void ProcessQuestionAPI(String que_id, int flag, String call_from, String rating, String feedback) {
        ProgressDialog.getInstance().show(getActivity());
        Call<ProcessQuestion> call;
        int user_id = new PreferenceManager(getApplicationContext()).getInt(Constant.USER_ID);

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
            }
        });
    }


    @Override
    public void onCommentClick(String questionId) {
        Bundle bundle = new Bundle();
        bundle.putString(Constant.CALL_FROM, Module.Learning.toString());
        bundle.putString("QuestionId", questionId);
        NavHostFragment.findNavController(this).navigate(R.id.nav_comments, bundle);
    }


    @Override
    public void onShareClick(String questionId) {
        Bundle bundle = new Bundle();
        bundle.putString("QuestionId", questionId);
        bundle.putInt("call_from", learning);
        NavHostFragment.findNavController(this).navigate(R.id.nav_share, bundle);
    }


    @Override
    public void onSubmitClick(String questionId, int isRight) {
        ProcessQuestionAPI(questionId, isRight, "submit", "", "");
    }

    @Override
    public void onLikeClick(String userId) {
        Bundle bundle = new Bundle();
        if (userId.equalsIgnoreCase(new PreferenceManager(getActivity()).getUserId())) {
            bundle.putInt(CALL_FROM, profile);
        } else {
            bundle.putInt(CALL_FROM, connectonId);
            bundle.putString(Constant.fetch_profile_id, userId);
        }

        NavHostFragment.findNavController(this).navigate(R.id.nav_edit_profile, bundle);
    }


}
