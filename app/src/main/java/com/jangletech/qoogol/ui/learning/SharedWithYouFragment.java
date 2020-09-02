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
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.LearningAdapter;
import com.jangletech.qoogol.databinding.LearningFragmentBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.dialog.QuestionFilterDialog;
import com.jangletech.qoogol.dialog.ShareQuestionDialog;
import com.jangletech.qoogol.enums.Module;
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
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

import static com.jangletech.qoogol.util.Constant.CALL_FROM;
import static com.jangletech.qoogol.util.Constant.SHARED_WITH_ME;
import static com.jangletech.qoogol.util.Constant.connectonId;
import static com.jangletech.qoogol.util.Constant.learning;
import static com.jangletech.qoogol.util.Constant.profile;
import static com.jangletech.qoogol.util.Constant.sharedto;

public class SharedWithYouFragment extends BaseFragment implements LearningAdapter.onIconClick, QuestionFilterDialog.FilterClickListener {

    private LearningViewModel mViewModel;
    LearningFragmentBinding learningFragmentBinding;
    LearningAdapter learningAdapter;
    List<LearningQuestionsNew> learningQuestionsList;
    List<LearningQuestionsNew> questionsNewList;
    ApiInterface apiService = ApiClient.getInstance().getApi();
    String userId = "";
    private HashMap<String, String> params;
    boolean isFilterApplied = false;
    List<LearningQuestionsNew> questionsFilteredList;
    private Menu filterMenu;
    ;


    public static SharedWithYouFragment newInstance() {
        return new SharedWithYouFragment();
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
        mViewModel.activity = getActivity();
        initView();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.action_search, menu);
        super.onCreateOptionsMenu(menu, inflater);
        filterMenu = menu;
        if (isFilterApplied) {
            setFilterIcon(menu, getActivity(), true);
        }
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                QuestionFilterDialog bottomSheetFragment = new QuestionFilterDialog(getActivity(), this, params);
                bottomSheetFragment.setCancelable(false);
                bottomSheetFragment.show(getActivity().getSupportFragmentManager(), bottomSheetFragment.getTag());
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
        learningFragmentBinding.topLayout.setVisibility(View.GONE);
        isFilterApplied = getFilter(Constant.QUESTION_FILTER_APPLIED);
        learningFragmentBinding.learningSwiperefresh.setRefreshing(true);
        learningQuestionsList = new ArrayList<>();
        questionsNewList = new ArrayList<>();
        userId = String.valueOf(new PreferenceManager(getActivity()).getInt(Constant.USER_ID));
        params = new HashMap<>();
        params = new HashMap<>();
        params = AppUtils.loadQueFilterHashMap(getActivity());
        Bundle bundle = getArguments();
        if (bundle != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Shared with You");
        }

        mViewModel.fetchQuestionData("", SHARED_WITH_ME, params);

        mViewModel.getSharedQuestionList(SHARED_WITH_ME).observe(getViewLifecycleOwner(), questionsList -> {
            if (!isFilterApplied)
                setData(questionsList);
        });
        mViewModel.getFilterQuestionList().observe(getViewLifecycleOwner(), questionsList -> {
            if (isFilterApplied)
                setData(questionsList);
        });

        learningFragmentBinding.learningSwiperefresh.setOnRefreshListener(() -> {
            mViewModel.pageCount="0";
            mViewModel.fetchQuestionData("", SHARED_WITH_ME, params);
            mViewModel.getSharedQuestionList(SHARED_WITH_ME).observe(getViewLifecycleOwner(), questionsList -> {
                if (!isFilterApplied)
                    setData(questionsList);
            });
            mViewModel.getFilterQuestionList().observe(getViewLifecycleOwner(), questionsList -> {
                if (isFilterApplied)
                    setData(questionsList);
            });

        });

    }

    private void setData(List<LearningQuestionsNew> questionsList) {
        if (questionsList!=null && questionsList.size()>0) {
            questionsNewList.clear();
            questionsNewList.addAll(questionsList);
            initRecycler();
            learningFragmentBinding.tvNoQuest.setVisibility(View.GONE);
        } else {
            dismissRefresh(learningFragmentBinding.learningSwiperefresh);
            learningFragmentBinding.tvNoQuest.setVisibility(View.VISIBLE);
        }
    }

    private void initRecycler() {
        learningAdapter = new LearningAdapter(getActivity(), questionsNewList, this, sharedto);
        learningFragmentBinding.learningRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setAutoMeasureEnabled(false);
        learningFragmentBinding.learningRecycler.setLayoutManager(linearLayoutManager);
        learningFragmentBinding.learningRecycler.setAdapter(learningAdapter);
        learningFragmentBinding.learningRecycler.setNestedScrollingEnabled(false);
        learningFragmentBinding.learningRecycler.setItemViewCacheSize(20);
        learningFragmentBinding.learningRecycler.setDrawingCacheEnabled(true);
        learningFragmentBinding.learningRecycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
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
        Bundle bundle = new Bundle();
        bundle.putString(Constant.CALL_FROM, Module.Learning.toString());
        bundle.putInt("QuestionId", questionId);
        NavHostFragment.findNavController(this).navigate(R.id.nav_comments, bundle);
    }


    @Override
    public void onShareClick(int questionId) {
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
        } else {
            bundle.putInt(CALL_FROM, connectonId);
            bundle.putString(Constant.fetch_profile_id, userId);
        }

        NavHostFragment.findNavController(this).navigate(R.id.nav_edit_profile, bundle);
    }

    @Override
    public void onFavClick() {

    }


    @Override
    public void onResetClick() {
        isFilterApplied = false;
        setFilterIcon(filterMenu, getActivity(), false);
    }

    @Override
    public void onDoneClick(HashMap<String, String> map) {
        params = map;
        isFilterApplied = true;
        mViewModel.pageCount="0";
        setFilterIcon(filterMenu, getActivity(), true);
        mViewModel.fetchQuestionData("", SHARED_WITH_ME, params);
        questionsFilteredList.clear();
        mViewModel.getFilterQuestionList().observe(getViewLifecycleOwner(), questionsList -> {
            if (isFilterApplied)
                setData(questionsList);
        });
    }

}
