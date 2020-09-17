package com.jangletech.qoogol.ui.learning;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.activities.MainActivity;
import com.jangletech.qoogol.adapter.LearningAdapter;
import com.jangletech.qoogol.databinding.LearningFragmentBinding;
import com.jangletech.qoogol.dialog.CommentDialog;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.dialog.PublicProfileDialog;
import com.jangletech.qoogol.dialog.QuestionFilterDialog;
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
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

import static com.jangletech.qoogol.util.Constant.CALL_FROM;
import static com.jangletech.qoogol.util.Constant.learning;
import static com.jangletech.qoogol.util.Constant.profile;

public class LearningFragment extends BaseFragment implements LearningAdapter.onIconClick, PublicProfileDialog.PublicProfileClickListener,
        CommentDialog.CommentClickListener, QuestionFilterDialog.FilterClickListener, SearchView.OnQueryTextListener, ShareQuestionDialog.ShareDialogListener {

    private static final String TAG = "LearningFragment";
    private LearningViewModel mViewModel;
    LearningFragmentBinding learningFragmentBinding;
    LearningAdapter learningAdapter;
    private LearningQuestionsNew selectedLearningQuest;
    private int selectedPos = -1;
    List<LearningQuestionsNew> learningQuestionsList;
    List<LearningQuestionsNew> questionsNewList;
    List<LearningQuestionsNew> questionsFilteredList;
    ApiInterface apiService = ApiClient.getInstance().getApi();
    String userId = "";
    private HashMap<String, String> params;
    private Menu filterMenu;
    boolean isFilterApplied = false;
    boolean isSettingsApplied = false;
    private Boolean isScrolling = false;
    private boolean isSearching = false;
    private int currentItems, scrolledOutItems, totalItems;
    LinearLayoutManager linearLayoutManager;

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
        mViewModel.activity = getActivity();
        MainActivity.isTestScreenEnabled = false;
        initView();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.action_search, menu);
        super.onCreateOptionsMenu(menu, inflater);
        filterMenu = menu;
        MenuItem item = menu.findItem(R.id.action_search);
        if (isFilterApplied) {
            setFilterIcon(menu, getActivity(), true);
        }

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);

        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return true;
            }
        });
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
        isFilterApplied = getFilter(Constant.QUESTION_FILTER_APPLIED);

        learningFragmentBinding.learningSwiperefresh.setRefreshing(true);
        learningQuestionsList = new ArrayList<>();
        questionsNewList = new ArrayList<>();
        questionsFilteredList = new ArrayList<>();
        userId = String.valueOf(new PreferenceManager(getActivity()).getInt(Constant.USER_ID));
        if (getString(Constant.subjectName) != null &&
                !getString(Constant.subjectName).isEmpty()) {
            String ch1 = !getString(Constant.chapterName1).isEmpty() ? "," + getString(Constant.chapterName1) : "";
            String ch2 = !getString(Constant.chapterName2).isEmpty() ? "," + getString(Constant.chapterName2) : "";
            String ch3 = !getString(Constant.chapterName3).isEmpty() ? "," + getString(Constant.chapterName3) : "";
            String chapters = ch1 + ch2 + ch3;
            learningFragmentBinding.tvSubjectValue.setText(getString(Constant.subjectName));
            learningFragmentBinding.tvChapterValue.setText(!chapters.isEmpty() ? chapters.substring(1) : "");
            isSettingsApplied = true;
        } else {
            learningFragmentBinding.topLayout.setVisibility(View.GONE);
            isSettingsApplied = false;
        }
        params = new HashMap<>();
        params = AppUtils.loadQueFilterHashMap(getActivity());

        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.getBoolean("fromNotification")) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Learning");
                if (bundle.getString(Constant.FB_MS_ID) != null)
                    mViewModel.fetchQuestionData(bundle.getString(Constant.FB_MS_ID), params);

                learningFragmentBinding.learningSwiperefresh.setRefreshing(true);
                mViewModel.getQuestion(bundle.getString(Constant.FB_MS_ID)).observe(getViewLifecycleOwner(), questionsList -> {
                    setData(questionsList);
                });
            } else {
                learningFragmentBinding.learningSwiperefresh.setRefreshing(true);
                if (bundle.getString("call_from").equalsIgnoreCase("saved_questions")) {
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Saved Questions");
                } else {
                    ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Practice Questions");
                }
                mViewModel.fetchQuestionData("", params);

                mViewModel.getQuestionList().observe(getViewLifecycleOwner(), questionsList -> {
                    if (!isFilterApplied && !isSettingsApplied)
                        setData(questionsList);
                });
                mViewModel.getFilterQuestionList().observe(getViewLifecycleOwner(), questionsList -> {
                    if (isFilterApplied || isSettingsApplied)
                        setData(questionsList);
                });
            }
        }

        learningFragmentBinding.learningSwiperefresh.setOnRefreshListener(() -> {
            mViewModel.pageCount = "0";
            questionsNewList.clear();
            mViewModel.fetchQuestionData("", params);
            dismissRefresh(learningFragmentBinding.learningSwiperefresh);
            mViewModel.getQuestionList().observe(getViewLifecycleOwner(), questionsList -> {
                if (!isFilterApplied && !isSettingsApplied)
                    setData(questionsList);
            });
            mViewModel.getFilterQuestionList().observe(getViewLifecycleOwner(), questionsList -> {
                if (isFilterApplied || isSettingsApplied)
                    setData(questionsList);
            });
        });

        learningFragmentBinding.syllabusLayout.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigate(R.id.nav_syllabus, Bundle.EMPTY);
        });


        learningFragmentBinding.learningRecycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = linearLayoutManager.getChildCount();
                totalItems = linearLayoutManager.getItemCount();
                scrolledOutItems = linearLayoutManager.findFirstVisibleItemPosition();
                if (dy > 0) {
                    if (!isSearching) {
                        if (isScrolling && (currentItems + scrolledOutItems == totalItems)) {
                            isScrolling = false;
                            mViewModel.fetchQuestionData("", params);
                        }
                    }
                }
            }
        });
    }

    private void setData(List<LearningQuestionsNew> questionsList) {
        if (questionsList != null && questionsList.size() > 0) {
            learningFragmentBinding.tvNoQuest.setVisibility(View.GONE);
        } else {
            dismissRefresh(learningFragmentBinding.learningSwiperefresh);
            learningFragmentBinding.tvNoQuest.setVisibility(View.VISIBLE);
        }
        if (isFilterApplied || isSettingsApplied)
        questionsNewList.clear();
        questionsNewList.addAll(questionsList);
        initRecycler();

    }

    private void initRecycler() {
        learningAdapter = new LearningAdapter(getActivity(), questionsNewList, this, learning);
        learningFragmentBinding.learningRecycler.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getContext());
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
    public void onCommentClick(LearningQuestionsNew learningQuestionsNew, int pos) {
        Log.d(TAG, "onCommentClick questionId : " + learningQuestionsNew.getQuestion_id());
        selectedPos = pos;
        selectedLearningQuest = learningQuestionsNew;
        CommentDialog commentDialog = new CommentDialog(getActivity(), learningQuestionsNew.getQuestion_id(), false, this);
        commentDialog.show();
    }


    @Override
    public void onShareClick(LearningQuestionsNew learningQuestionsNew, int pos) {
        selectedLearningQuest = learningQuestionsNew;
        selectedPos = pos;
        new ShareQuestionDialog(getActivity(), String.valueOf(learningQuestionsNew.getQuestion_id()), AppUtils.getUserId()
                , getDeviceId(getActivity()), "Q", this)
                .show();
    }

    @Override
    public void onBackClick(int count) {
        Log.d(TAG, "onBackClick Comment Count : " + count);
        int commentCount = Integer.parseInt(selectedLearningQuest.getComments()) + count;
        selectedLearningQuest.setComments(String.valueOf(commentCount));
        learningAdapter.notifyItemChanged(selectedPos, selectedLearningQuest);
    }

    @Override
    public void onSharedSuccess(int count) {
        Log.d(TAG, "onSharedSuccess Count : " + count);
        int shareCount = Integer.parseInt(selectedLearningQuest.getShares()) + count;
        selectedLearningQuest.setShares(String.valueOf(shareCount));
        learningAdapter.notifyItemChanged(selectedPos, selectedLearningQuest);
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
        }
    }

    @Override
    public void onFavClick() {

    }

    @Override
    public void onFriendUnFriendClick() {

    }

    @Override
    public void onFollowUnfollowClick() {

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



    @Override
    public void onResetClick() {
        isFilterApplied = false;
        setFilterIcon(filterMenu, getActivity(), false);
        params.put(Constant.q_trending, "");
        params.put(Constant.q_popular, "");
        params.put(Constant.q_recent, "");
        params.put(Constant.q_option_type, "");
        params.put(Constant.q_type, "");
        params.put(Constant.q_avg_ratings, "");
        params.put(Constant.q_diff_level, "");
        mViewModel.pageCount = "0";
        questionsFilteredList.clear();
        mViewModel.fetchQuestionData("", params);
    }

    @Override
    public void onDoneClick(HashMap<String, String> map) {
        params = map;
        isFilterApplied = true;
        mViewModel.pageCount = "0";
        mViewModel.fetchQuestionData("", params);
        questionsFilteredList.clear();
        setFilterIcon(filterMenu, getActivity(), true);
        mViewModel.getFilterQuestionList().observe(getViewLifecycleOwner(), questionsList -> {
            if (isFilterApplied) {
                questionsNewList.clear();
                setData(questionsList);

            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.trim().toLowerCase().isEmpty()) {
            isSearching = false;
            learningAdapter.updateList(questionsNewList);
        } else {
            isSearching = true;
            learningAdapter.updateList(searchQuestion(newText.trim().toLowerCase(), questionsNewList));
        }
        return true;
    }
}
