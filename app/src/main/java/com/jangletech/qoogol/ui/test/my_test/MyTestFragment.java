package com.jangletech.qoogol.ui.test.my_test;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.MenuItemCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.activities.MainActivity;
import com.jangletech.qoogol.activities.PracticeTestActivity;
import com.jangletech.qoogol.adapter.TestListAdapter;
import com.jangletech.qoogol.databinding.FragmentTestMyBinding;
import com.jangletech.qoogol.dialog.CommentDialog;
import com.jangletech.qoogol.dialog.FilterDialog;
import com.jangletech.qoogol.dialog.LikeListingDialog;
import com.jangletech.qoogol.dialog.PublicProfileDialog;
import com.jangletech.qoogol.dialog.ShareQuestionDialog;
import com.jangletech.qoogol.model.TestListResponse;
import com.jangletech.qoogol.model.TestModelNew;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.util.AppUtils;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.TinyDB;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import static com.jangletech.qoogol.util.Constant.CASE;

public class MyTestFragment extends BaseFragment
        implements TestListAdapter.TestClickListener, SearchView.OnQueryTextListener, CommentDialog.CommentClickListener, PublicProfileDialog.PublicProfileClickListener, FilterDialog.FilterClickListener, LikeListingDialog.onItemClickListener, ShareQuestionDialog.ShareDialogListener {

    private static final String TAG = "MyTestFragment";
    private MyTestViewModel mViewModel;
    private FragmentTestMyBinding mBinding;
    private TestListAdapter mAdapter;
    private MenuItem filterMenuItem;
    private Menu filterMenu;
    private int selectedPos = -1;
    private TestModelNew selectedTestCard;
    private List<TestModelNew> testList = new ArrayList<>();
    private List<TestModelNew> filteredTestList = new ArrayList<>();
    private List<String> subjectList;
    private TestListResponse testListResponse;
    private Context mContext;
    private Boolean isFilterApplied = false;
    private HashMap<String, String> params;
    private HashMap<String, String> filterParams = new HashMap<>();
    private int tmId;
    private Boolean isScrolling = false;
    private LinearLayoutManager linearLayoutManager;
    private int currentItems, scrolledOutItems, totalItems;
    private String pageStart = "0";
    private String flag = "";
    private String diffLevel = "";
    private ApiInterface apiService = ApiClient.getInstance().getApi();

    public static MyTestFragment newInstance() {
        return new MyTestFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context.getApplicationContext();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_test_my, container, false);
        mBinding.setLifecycleOwner(this);
        mViewModel = new ViewModelProvider(this).get(MyTestViewModel.class);
        initViews();
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MainActivity.isTestScreenEnabled = true;
        if (mBinding.topLayout.getVisibility() == View.GONE) {
            changeConfigurationAlert();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.action_search, menu);
        filterMenu = menu;
        MenuItem item = menu.findItem(R.id.action_search);
        filterMenuItem = menu.findItem(R.id.action_filter);
        if (isFilterApplied) {
            setFilterIcon(menu, mContext, true);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                Log.d(TAG, "onOptionsItemSelected Filter : " + params);
                FilterDialog bottomSheetFragment = new FilterDialog(getActivity(), AppUtils.loadHashMap(mContext), this);
                bottomSheetFragment.setCancelable(false);
                bottomSheetFragment.show(getActivity().getSupportFragmentManager(), bottomSheetFragment.getTag());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initViews() {
        Log.d(TAG, "initViews Params : " + AppUtils.loadHashMap(mContext));
        if (getString(Constant.subjectName) != null &&
                !getString(Constant.subjectName).isEmpty()) {
            String chapters = "";
            if (getString(Constant.chapterName1) != null &&
                    !getString(Constant.chapterName1).isEmpty()) {
                chapters = chapters + getString(Constant.chapterName1);
            }
            if (getString(Constant.chapterName2) != null &&
                    !getString(Constant.chapterName2).isEmpty()) {
                chapters = chapters + "," + getString(Constant.chapterName2);
            }
            if (getString(Constant.chapterName3) != null &&
                    !getString(Constant.chapterName3).isEmpty()) {
                chapters = chapters + "," + getString(Constant.chapterName3);
            }

            if (getString(Constant.subjectName) != null && !getString(Constant.subjectName).isEmpty())
                mBinding.tvSubjectValue.setText(getString(Constant.subjectName));

            if (!chapters.isEmpty())
                mBinding.tvChapterValue.setText(chapters);

        } else {
            mBinding.topLayout.setVisibility(View.GONE);
        }

        params = new HashMap<>();
        //if (params == null)
        params = AppUtils.loadHashMap(mContext);
        //Log.d(TAG, "initViews getUserId : " + getUserId(mContext));
        //params.put(Constant.u_user_id, getUserId(mContext));
        //Log.d(TAG, "initViews ffjahsjfiafha : " + params);
        isFilterApplied = getFilter(Constant.TEST_FILTER_APPLIED);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        subjectList = new ArrayList<>();
        if (getArguments() != null && getArguments().getSerializable("PARAMS") != null) {
            HashMap<String, String> filterArgs = new HashMap<>();
            filterArgs = (HashMap<String, String>) getArguments().getSerializable("PARAMS");
            diffLevel = filterArgs.get(Constant.tm_diff_level);
            Log.d(TAG, "Filter DiffLevel : " + diffLevel);
            Log.d(TAG, "Filter Avg Rating : " + filterArgs.get(Constant.tm_avg_rating));
        }

        mBinding.tvInfo.setOnClickListener(v -> {
            navigateToSyllabus();
        });

        mBinding.topLayout.setOnClickListener(v -> {
            navigateToSyllabus();
        });

        mBinding.testListRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
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
                    if (isScrolling && (currentItems + scrolledOutItems == totalItems)) {
                        isScrolling = false;
                        mBinding.progress.setVisibility(View.VISIBLE);
                        fetchTestList(AppUtils.loadHashMap(mContext), pageStart);
                    }
                }
            }
        });

        mBinding.swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchTestList(params, pageStart);
            }
        });

        if (getArguments() != null && getArguments().getBoolean("fromNotification")) {
            Log.d(TAG, "initViews msId : " + getArguments().getString(Constant.FB_MS_ID));
            params.put(Constant.tm_id, getArguments().getString(Constant.FB_MS_ID));
        }

        if (getString(Constant.tm_diff_level) != null && !getString(Constant.tm_diff_level).isEmpty())
            params.put(Constant.tm_diff_level, getString(Constant.tm_diff_level));

        if (getString(Constant.tm_avg_rating) != null && !getString(Constant.tm_avg_rating).isEmpty())
            params.put(Constant.tm_avg_rating, getString(Constant.tm_avg_rating));

        mAdapter = new TestListAdapter(requireActivity(), testList, this, "");
        mBinding.testListRecyclerView.setLayoutManager(linearLayoutManager);
        mBinding.testListRecyclerView.setAdapter(mAdapter);

        fetchTestList(params, pageStart);
        mViewModel.getAllTests("PRACTICE", getUserId(getActivity())).observe(getViewLifecycleOwner(), new Observer<List<TestModelNew>>() {
            @Override
            public void onChanged(@Nullable final List<TestModelNew> tests) {
                if (tests != null) {
                    Log.d(TAG, "onChanged Size AllTests: " + tests.size());
                    testList = tests;
                    if (!isFilterApplied) {
                        if (testListResponse != null)
                            pageStart = testListResponse.getRow_count();
                        setMyTestList(tests);
                    }
                }
            }
        });
    }

    private void setFilteredTestList(TestListResponse response) {
        if (response != null) {
            pageStart = response.getRow_count();
            filteredTestList.addAll(response.getTestList());
            mAdapter.updateList(filteredTestList);
            if (filteredTestList.size() == 0) {
                mBinding.tvNoTest.setText("No Tests Found, Modify Filters.");
                mBinding.tvNoTest.setVisibility(View.VISIBLE);
            } else {
                mBinding.tvNoTest.setVisibility(View.GONE);
            }
        }
    }

    public void setMyTestList(List<TestModelNew> testList) {
        if (testList.size() > 0) {
            mBinding.tvNoTest.setVisibility(View.GONE);
            mAdapter.updateList(testList);
        } else {
            mBinding.tvNoTest.setText("No Tests Found.");
            mBinding.tvNoTest.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.trim().toLowerCase().isEmpty()) {
            mAdapter.updateList(testList);
        } else {
            mAdapter.updateList(searchTests(newText.trim().toLowerCase(), testList));
        }
        return true;
    }

    private void fetchTestList(HashMap<String, String> parameters, String pageStart) {
        Log.d(TAG, "fetchTestList Params : " + parameters);
        Log.d(TAG, "initViews Flag : " + flag);
        Log.d(TAG, "fetchTestList PageStart : " + pageStart);
        Log.d(TAG, "fetchTestList UEID : " + TinyDB.getInstance(getActivity()).getString(Constant.selected_ue_id));
        if (parameters == null) {
            parameters = new HashMap<>();
            parameters.put(Constant.u_user_id, getUserId(mContext));
            parameters.put(CASE, "");
            parameters.put(Constant.tm_recent_test, "");
            parameters.put(Constant.tm_popular_test, "");
            parameters.put(Constant.tm_diff_level, "");
            parameters.put(Constant.tm_avg_rating, "");
            parameters.put(Constant.tm_id, "");
            parameters.put(Constant.tm_catg, "");
        }

        mBinding.swipeToRefresh.setRefreshing(true);
        Call<TestListResponse> call = apiService.fetchTestList(
                AppUtils.getUserId(),
                parameters.get(CASE),
                parameters.get(Constant.tm_recent_test),
                parameters.get(Constant.tm_popular_test),
                parameters.get(Constant.tm_diff_level),
                parameters.get(Constant.tm_avg_rating),
                parameters.get(Constant.tm_id),
                parameters.get(Constant.tm_catg),
                pageStart,
                TinyDB.getInstance(getActivity()).getString(Constant.selected_ue_id)
        );
        HashMap<String, String> finalParameters = parameters;
        call.enqueue(new Callback<TestListResponse>() {
            @Override
            public void onResponse(Call<TestListResponse> call, Response<TestListResponse> response) {
                mBinding.swipeToRefresh.setRefreshing(false);
                mBinding.progress.setVisibility(View.GONE);
                if (response.body() != null) {
                    if (response.body().getResponse().equals("200")) {
                        testListResponse = response.body();
                        if (finalParameters.get(Constant.tm_id) != null &&
                                !(finalParameters.get(Constant.tm_id).isEmpty())) {
                            if (response.body().getTestList().size() > 0) {
                                mAdapter.updateList(response.body().getTestList());
                            } else {
                                mBinding.tvNoTest.setVisibility(View.VISIBLE);
                            }
                        } else {
                            List<TestModelNew> newList = response.body().getTestList();
                            for (TestModelNew testModelNew : newList) {
                                testModelNew.setFlag("PRACTICE");
                                Log.d(TAG, "PRACTICE UserId : " + MainActivity.userId);
                                testModelNew.setUserId(MainActivity.userId);
                            }
                            mViewModel.insert(newList);
                            if (isFilterApplied) {
                                setFilteredTestList(response.body());
                            }
                        }
                    } else if (response.body().getResponse().equals("501")) {
                        resetSettingAndLogout();
                    } else {
                        showErrorDialog(getActivity(), response.body().getResponse(), response.body().getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<TestListResponse> call, Throwable t) {
                mBinding.swipeToRefresh.setRefreshing(false);
                mBinding.progress.setVisibility(View.GONE);
                showToast("Something went wrong!!");
                apiCallFailureDialog(t);
                t.printStackTrace();
            }
        });
    }


    @Override
    public void onTestItemClick(TestModelNew testModel) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.TEST_NAME, testModel);
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.nav_test_details, bundle);
    }

    @Override
    public void onStartTestClick(TestModelNew testModel) {
        Log.d(TAG, "onStartTestClick: " + testModel.getTm_id());
        Intent intent = new Intent(getActivity(), PracticeTestActivity.class);
        intent.putExtra(Constant.TM_ID, testModel.getTm_id());
        startActivity(intent);
    }

    @Override
    public void onCommentClick(TestModelNew testModel, int pos) {
        Log.d(TAG, "onCommentClick TmId : " + testModel.getTm_id());
        selectedPos = pos;
        selectedTestCard = testModel;
        CommentDialog commentDialog = new CommentDialog(getActivity(), testModel.getTm_id(), true, this);
        commentDialog.show();
    }

    @Override
    public void onShareClick(TestModelNew testModelNew, int pos) {
        selectedPos = pos;
        selectedTestCard = testModelNew;
        new ShareQuestionDialog(getActivity(), String.valueOf(testModelNew.getTm_id()), getUserId(mContext)
                , getDeviceId(mContext), "T", this)
                .show();
    }

    @Override
    public void onLikeCountClick(TestModelNew testModel) {
        Log.d(TAG, "onLikeCountClick tm_id : " + testModel.getTm_id());
        new LikeListingDialog(true, getActivity(), testModel.getTm_id(), this)
                .show();
    }

    @Override
    public void favClick(TestModelNew testModelNew) {
        Log.d(TAG, "favClick Value : " + testModelNew.isFavourite());
        mViewModel.updateFav("PRACTICE", getUserId(getActivity()), testModelNew.getTm_id(), testModelNew.isFavourite());
    }

    @Override
    public void onAttemptsClick(TestModelNew testModel) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("PARAMS", testModel);
        NavHostFragment.findNavController(this).navigate(R.id.nav_test_attempt_history, bundle);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mAdapter = null;
    }

    @Override
    public void onCommentClick(String userId) {
        PublicProfileDialog publicProfileDialog = new PublicProfileDialog(getActivity(), userId, this);
        publicProfileDialog.show();
    }

    @Override
    public void onBackClick(int count) {
        Log.d(TAG, "onBackClick Comment Count : "+count);
        int commentCount = Integer.parseInt(selectedTestCard.getCommentsCount())+count;
        selectedTestCard.setCommentsCount("" + commentCount);
        mAdapter.notifyItemChanged(selectedPos, selectedTestCard);
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
    public void onResetClick(HashMap<String, String> map) {
        isFilterApplied = false;
        params = map;
        params.put(Constant.u_user_id, AppUtils.getUserId());
        params.put(Constant.tm_id, "");
        setFilterIcon(filterMenu, mContext, false);
        fetchTestList(params, "0");
    }

    @Override
    public void onDoneClick(HashMap<String, String> map) {
        isFilterApplied = true;
        filteredTestList.clear();
        params = map;
        setFilterIcon(filterMenu, mContext, true);
        Log.d(TAG, "onDoneClick Filtered Params : " + params);
        fetchTestList(params, "0");
    }

    @Override
    public void onItemCLick(String user_id) {
        Log.d(TAG, "onItemCLick UserId : " + user_id);
        PublicProfileDialog publicProfileDialog = new PublicProfileDialog(getActivity(), user_id, this);
        publicProfileDialog.show();
    }

    private void changeConfigurationAlert() {
        androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle);
        builder.setTitle("Alert")
                .setMessage("You have not selected education configuration (course,Subject,chapters).would you like to set it?")
                .setPositiveButton("Set Configuration", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        navigateToSyllabus();
                    }
                })
                .setCancelable(true).show();
    }

    private void navigateToSyllabus() {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
        navController.navigate(R.id.nav_syllabus, Bundle.EMPTY);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSharedSuccess(int count) {
        Log.d(TAG, "onSharedSuccess Count : "+count);
        int shareCount = Integer.parseInt(selectedTestCard.getShareCount());
        selectedTestCard.setShareCount("" + (shareCount + count));
        mAdapter.notifyItemChanged(selectedPos, selectedTestCard);
    }
}
