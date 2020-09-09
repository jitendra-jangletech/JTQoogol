package com.jangletech.qoogol.ui.test.shared_by_you;

import android.content.Context;
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
import androidx.core.view.MenuItemCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.activities.PracticeTestActivity;
import com.jangletech.qoogol.adapter.TestListAdapter;
import com.jangletech.qoogol.databinding.FragmentTestListBinding;
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
import com.jangletech.qoogol.ui.test.my_test.MyTestViewModel;
import com.jangletech.qoogol.util.AppUtils;
import com.jangletech.qoogol.util.Constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jangletech.qoogol.util.Constant.CASE;

public class TestSharedByYouFragment extends BaseFragment
        implements TestListAdapter.TestClickListener, SearchView.OnQueryTextListener, CommentDialog.CommentClickListener, PublicProfileDialog.PublicProfileClickListener, FilterDialog.FilterClickListener, LikeListingDialog.onItemClickListener, ShareQuestionDialog.ShareDialogListener {

    private static final String TAG = "TestSharedByYouFragment";
    private ApiInterface apiService = ApiClient.getInstance().getApi();
    private MyTestViewModel mViewModel;
    private FragmentTestListBinding mBinding;
    private TestListAdapter mAdapter;
    private Menu filterMenu;
    private int selectedPos = -1;
    private TestModelNew selectedTestCard;
    private TestListResponse testListResponse;
    private List<TestModelNew> testList = new ArrayList<>();
    private List<TestModelNew> filteredTestList = new ArrayList<>();
    private boolean isFilterApplied = false;
    private Context mContext;
    private Boolean isScrolling = false;
    private LinearLayoutManager linearLayoutManager;
    private int currentItems, scrolledOutItems, totalItems;
    private String pageStart = "0";
    private HashMap<String, String> params = new HashMap<>();
    private int tmId;
    private String flag = "";

    public static TestSharedByYouFragment newInstance() {
        return new TestSharedByYouFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context.getApplicationContext();
        //params = new HashMap<>();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Log.d(TAG, "Android Id : " + getDeviceId(getActivity()));
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_test_list, container, false);
        mBinding.setLifecycleOwner(this);
        mViewModel = new ViewModelProvider(this).get(MyTestViewModel.class);
        initViews();
        return mBinding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.action_search, menu);
        filterMenu = menu;
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        if (isFilterApplied) {
            setFilterIcon(menu, mContext, true);
        }
        searchView.setOnQueryTextListener(this);
        Log.d(TAG, "onCreateOptionsMenu isFilterApplied : " + isFilterApplied);
        if (isFilterApplied) {
            setFilterIcon(menu, mContext, true);
        }

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
                FilterDialog bottomSheetFragment = new FilterDialog(getActivity(), AppUtils.loadHashMap(mContext), this);
                bottomSheetFragment.show(getActivity().getSupportFragmentManager(), bottomSheetFragment.getTag());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initViews() {
        mBinding.topLayout.setVisibility(View.GONE);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        params = AppUtils.loadHashMap(mContext);
        isFilterApplied = getFilter(Constant.TEST_FILTER_APPLIED);
        mAdapter = new TestListAdapter(requireActivity(), testList, this, "");
        mBinding.testListRecyclerView.setLayoutManager(linearLayoutManager);
        mBinding.testListRecyclerView.setAdapter(mAdapter);

        mBinding.swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchTestList(params, "0");
            }
        });

//        params.put(Constant.u_user_id, getUserId(getActivity()));
//        params.put(CASE, "SH");
//        params.put(Constant.tm_popular_test, "");
//        params.put(Constant.tm_recent_test, "");

        /*fetchSubjectList(new PreferenceManager(requireActivity()).getString(Constant.scr_co_id));
        mViewModel.getAllSubjects().observe(getViewLifecycleOwner(), new Observer<List<FetchSubjectResponse>>() {
            @Override
            public void onChanged(@Nullable final List<FetchSubjectResponse> subjects) {
                if (subjects != null) {
                    Log.d(TAG, "onChanged Subjects Size : " + subjects.size());
                    ArrayList<String> subjectList = new ArrayList<>();
                    for (FetchSubjectResponse obj : subjects) {
                        if (!subjectList.contains(obj.getSm_sub_name()))
                            subjectList.add(obj.getSm_sub_name());
                    }
                    prepareSubjectChips(subjectList);
                }
            }
        });*/

        fetchTestList(AppUtils.loadHashMap(mContext), pageStart);
        mViewModel.getAllTests("SH", getUserId(getActivity())).observe(getViewLifecycleOwner(), new Observer<List<TestModelNew>>() {
            @Override
            public void onChanged(@Nullable final List<TestModelNew> tests) {
                if (tests != null) {
                    Log.d(TAG, "onChanged Size : " + tests.size());
                    if (!isFilterApplied) {
                        if (testListResponse != null)
                            pageStart = testListResponse.getRow_count();
                        testList = tests;
                        setMyTestList(tests);
                    }
                }
            }
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

//        mBinding.subjectsChipGrp.setOnCheckedChangeListener((chipGroup, id) -> {
//            Chip chip = ((Chip) chipGroup.getChildAt(chipGroup.getCheckedChipId()));
//            if (chip != null) {
//                try {
//                    if (chip.isChecked()) {
//                        setCheckedChip(mBinding.subjectsChipGrp);
//                        List<TestModelNew> filteredModelList = filterBySubject(testList, chip.getText().toString());
//                        if (filteredModelList.size() > 0) {
//                            mAdapter.setSearchResult(filteredModelList);
//                            //mBinding.tvNoTest.setVisibility(View.GONE);
//                        } else {
//                            mAdapter.setSearchResult(filteredModelList);
//                            //mBinding.tvNoTest.setVisibility(View.VISIBLE);
//                        }
//                    }
//                } catch (NullPointerException npe) {
//                    showToast("Something went wrong!!");
//                }
//            }
//        });
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

    private void fetchTestList(HashMap<String, String> params, String pageStart) {
        Log.d(TAG, "fetchTestList Params : " + params);
        Log.d(TAG, "initViews Flag : " + flag);
        Log.d(TAG, "initViews PageStart : " + pageStart);
        if (params == null) {
            params = new HashMap<>();
            params.put(Constant.u_user_id, getUserId(mContext));
            params.put(Constant.tm_recent_test, "");
            params.put(Constant.tm_popular_test, "");
            params.put(Constant.tm_diff_level, "");
            params.put(Constant.tm_avg_rating, "");
            params.put(Constant.tm_id, "");
            params.put(Constant.tm_catg, "");
        }
        params.put(Constant.u_user_id, getUserId(mContext));
        mBinding.swipeToRefresh.setRefreshing(true);
        Call<TestListResponse> call = apiService.fetchTestList(
                params.get(Constant.u_user_id),
                "SH",
                params.get(Constant.tm_recent_test),
                params.get(Constant.tm_popular_test),
                params.get(Constant.tm_diff_level),
                params.get(Constant.tm_avg_rating),
                params.get(Constant.tm_id),
                params.get(Constant.tm_catg),
                pageStart,
                ""
        );
        call.enqueue(new Callback<TestListResponse>() {
            @Override
            public void onResponse(Call<TestListResponse> call, Response<TestListResponse> response) {
                mBinding.swipeToRefresh.setRefreshing(false);
                mBinding.progress.setVisibility(View.GONE);
                if (response.body() != null && response.body().getResponse().equals("200")) {
                    testListResponse = response.body();
                    List<TestModelNew> newList = response.body().getTestList();
                    for (TestModelNew testModelNew : newList) {
                        testModelNew.setFlag("SH");
                        testModelNew.setUserId(getUserId(getActivity()));
                    }
                    mViewModel.insert(newList);
                    if (isFilterApplied) {
                        setFilteredTestList(response.body());
                    }
                } else if (response.body().getResponse().equals("501")) {
                    resetSettingAndLogout();
                } else {
                    AppUtils.showToast(getActivity(), null, response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<TestListResponse> call, Throwable t) {
                mBinding.swipeToRefresh.setRefreshing(false);
                mBinding.progress.setVisibility(View.GONE);
                t.printStackTrace();
                apiCallFailureDialog(t);
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
    public void onLikeCountClick(TestModelNew testModel) {
        Log.d(TAG, "onLikeCountClick tm_id : " + testModel.getTm_id());
        new LikeListingDialog(true, getActivity(), testModel.getTm_id(), this)
                .show();
    }

    @Override
    public void favClick(TestModelNew testModelNew) {

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
        saveFilter(false);
        setFilterIcon(filterMenu, mContext, false);
        params.put(Constant.u_user_id, getUserId(getActivity()));
        params.put(CASE, Constant.SHARED_BY_YOU);
        fetchTestList(params, "0");
    }

    @Override
    public void onDoneClick(HashMap<String, String> map) {
        Log.d(TAG, "onDoneClick: " + map);
        filteredTestList.clear();
        isFilterApplied = true;
        setFilterIcon(filterMenu, mContext, true);
        params = map;
        params.put(Constant.u_user_id, getUserId(getActivity()));
        params.put(CASE, Constant.SHARED_BY_YOU);
        fetchTestList(params, "0");
    }

    @Override
    public void onItemCLick(String user_id) {
        Log.d(TAG, "onItemCLick UserId : " + user_id);
        PublicProfileDialog publicProfileDialog = new PublicProfileDialog(getActivity(), user_id, this);
        publicProfileDialog.show();
    }

    @Override
    public void onBackClick(int count) {
        Log.d(TAG, "onBackClick Comment Count : " + count);
        int commentCount = Integer.parseInt(selectedTestCard.getCommentsCount()) + count;
        selectedTestCard.setCommentsCount("" + commentCount);
        mAdapter.notifyItemChanged(selectedPos, selectedTestCard);
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
    public void onSharedSuccess(int count) {
        Log.d(TAG, "onSharedSuccess Count : " + count);
        int shareCount = Integer.parseInt(selectedTestCard.getShareCount());
        selectedTestCard.setShareCount("" + (shareCount + count));
        mAdapter.notifyItemChanged(selectedPos, selectedTestCard);
    }
}
