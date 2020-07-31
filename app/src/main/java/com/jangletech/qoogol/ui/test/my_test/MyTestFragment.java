package com.jangletech.qoogol.ui.test.my_test;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.activities.PracticeTestActivity;
import com.jangletech.qoogol.adapter.TestListAdapter;
import com.jangletech.qoogol.databinding.FragmentTestMyBinding;
import com.jangletech.qoogol.dialog.CommentDialog;
import com.jangletech.qoogol.dialog.PublicProfileDialog;
import com.jangletech.qoogol.dialog.ShareQuestionDialog;
import com.jangletech.qoogol.enums.Module;
import com.jangletech.qoogol.model.FetchSubjectResponse;
import com.jangletech.qoogol.model.FetchSubjectResponseList;
import com.jangletech.qoogol.model.TestListResponse;
import com.jangletech.qoogol.model.TestModelNew;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jangletech.qoogol.util.Constant.CASE;

public class MyTestFragment extends BaseFragment
        implements TestListAdapter.TestClickListener, SearchView.OnQueryTextListener, CommentDialog.CommentClickListener, PublicProfileDialog.PublicProfileClickListener {

    private static final String TAG = "MyTestFragment";
    private MyTestViewModel mViewModel;
    private FragmentTestMyBinding mBinding;
    private TestListAdapter mAdapter;
    private List<TestModelNew> testList;
    private Context mContext;
    private HashMap<String, String> params;
    private int tmId;
    private String flag = "";
    private String diffLevel = "";
    private LiveData<List<TestModelNew>> testModelNewLiveData;
    private ApiInterface apiService = ApiClient.getInstance().getApi();

    public static MyTestFragment newInstance() {
        return new MyTestFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context.getApplicationContext();
        params = new HashMap<>();
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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_test_my, container, false);
        mBinding.setLifecycleOwner(this);
        mViewModel = new ViewModelProvider(this).get(MyTestViewModel.class);
        initViews();
        return mBinding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.action_search, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
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
                Bundle bundle = new Bundle();
                bundle.putString("call_from", "test");
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.nav_test_filter, bundle);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initViews() {
        if (getArguments() != null && getArguments().getSerializable("PARAMS") != null) {
            HashMap<String, String> filterArgs = new HashMap<>();
            filterArgs = (HashMap<String, String>) getArguments().getSerializable("PARAMS");
            diffLevel = filterArgs.get(Constant.tm_diff_level);
            Log.d(TAG, "Filter DiffLevel : " + diffLevel);
            Log.d(TAG, "Filter Avg Rating : " + filterArgs.get(Constant.tm_avg_rating));
        }
        mBinding.swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchTestList(params);
            }
        });

        params.put(Constant.u_user_id, getUserId(getActivity()));
        params.put(CASE, "");
        params.put(Constant.tm_popular_test, "");
        params.put(Constant.tm_recent_test, "");
        if (getArguments() != null && getArguments().getBoolean("fromNotification")) {
            Log.d(TAG, "initViews msId : " + getArguments().getString(Constant.FB_MS_ID));
            params.put(Constant.tm_id, getArguments().getString(Constant.FB_MS_ID));
        }

        if (getString(Constant.tm_diff_level) != null && !getString(Constant.tm_diff_level).isEmpty())
            params.put(Constant.tm_diff_level, getString(Constant.tm_diff_level));

        if (getString(Constant.tm_avg_rating) != null && !getString(Constant.tm_avg_rating).isEmpty())
            params.put(Constant.tm_avg_rating, getString(Constant.tm_avg_rating));

        fetchSubjectList(new PreferenceManager(requireActivity()).getString(Constant.scr_co_id));
        fetchTestList(params);
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
        });


        if (params.get(Constant.tm_diff_level) != null && !params.get(Constant.tm_diff_level).isEmpty()) {
            Log.d(TAG, "initViews Diff Level: " + params.get(Constant.tm_diff_level));
            mViewModel.getAllTestByDifficultyLevel("PRACTICE", getUserId(getActivity()), params.get(Constant.tm_diff_level)).observe(getViewLifecycleOwner(), new Observer<List<TestModelNew>>() {
                @Override
                public void onChanged(@Nullable final List<TestModelNew> tests) {
                    if (tests != null) {
                        Log.d(TAG, "onChanged Size : " + tests.size());
                        testList = tests;
                        setMyTestList(tests);
                    }
                }
            });
        }
        if (params.get(Constant.tm_avg_rating) != null && !params.get(Constant.tm_avg_rating).isEmpty()) {
            Log.d(TAG, "initViews Avg Rating: " + params.get(Constant.tm_avg_rating));
            mViewModel.getAllTestByAvgRating("PRACTICE", getUserId(getActivity()), params.get(Constant.tm_avg_rating)).observe(getViewLifecycleOwner(), new Observer<List<TestModelNew>>() {
                @Override
                public void onChanged(@Nullable final List<TestModelNew> tests) {
                    if (tests != null) {
                        Log.d(TAG, "onChanged Size : " + tests.size());
                        testList = tests;
                        setMyTestList(tests);
                    }
                }
            });
        } else {
            mViewModel.getAllTests("PRACTICE", getUserId(getActivity())).observe(getViewLifecycleOwner(), new Observer<List<TestModelNew>>() {
                @Override
                public void onChanged(@Nullable final List<TestModelNew> tests) {
                    if (tests != null) {
                        Log.d(TAG, "onChanged Size : " + tests.size());
                        testList = tests;
                        setMyTestList(tests);
                    }
                }
            });
        }

        mBinding.subjectsChipGrp.setOnCheckedChangeListener((chipGroup, id) -> {
            Chip chip = ((Chip) chipGroup.getChildAt(chipGroup.getCheckedChipId()));
            if (chip != null) {
                try {
                    if (chip.isChecked()) {
                        setCheckedChip(mBinding.subjectsChipGrp);
                        List<TestModelNew> filteredModelList = filterBySubject(testList, chip.getText().toString());
                        if (filteredModelList.size() > 0) {
                            mBinding.tvNoTest.setVisibility(View.GONE);
                            mAdapter.setSearchResult(filteredModelList);
                        } else {
                            mAdapter.setSearchResult(filteredModelList);
                            mBinding.tvNoTest.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (NullPointerException npe) {
                    showToast("Something went wrong!!");
                }
            }
        });
    }

    public void setMyTestList(List<TestModelNew> testList) {
        if (testList.size() > 0) {
            mBinding.tvNoTest.setVisibility(View.GONE);
            mAdapter = new TestListAdapter(requireActivity(), testList, this, "");
            mBinding.testListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            mBinding.testListRecyclerView.setAdapter(mAdapter);
        } else {
            Log.d(TAG, "setMyTestList size : " + testList.size());
            mBinding.tvNoTest.setVisibility(View.VISIBLE);
        }
    }

    private void prepareSubjectChips(ArrayList<String> subjects) {
        mBinding.subjectsChipGrp.removeAllViews();
        int idCounter = 0;
        for (String subject : subjects) {
            Chip chip = (Chip) LayoutInflater.from(mBinding.subjectsChipGrp.getContext()).inflate(R.layout.chip_layout, mBinding.subjectsChipGrp, false);
            chip.setText(subject);
            chip.setId(idCounter);
            chip.setTag("Subjects");
            chip.setClickable(true);
            chip.setCheckable(true);
            mBinding.subjectsChipGrp.addView(chip);
            idCounter++;
        }
    }

    private void setCheckedChip(ChipGroup chipGroup) {
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) chipGroup.getChildAt(i);
            if (chip.isChecked()) {
                chip.setTextColor(Color.WHITE);
            } else {
                chip.setTextColor(Color.BLACK);
            }
        }
    }

    private String getTestType(String key) {
        return new PreferenceManager(getActivity()).getString(key);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<TestModelNew> filteredModelList = filter(testList, newText);
        mAdapter.setSearchResult(filteredModelList);
        return true;
    }

    private List<TestModelNew> filter(List<TestModelNew> models, String query) {
        query = query.toLowerCase();
        final List<TestModelNew> filteredModelList = new ArrayList<>();
        for (TestModelNew model : models) {
            String testName = model.getTm_name().toLowerCase();
            if (testName.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    private List<TestModelNew> filterBySubject(List<TestModelNew> models, String subject) {
        List<TestModelNew> filteredModelList = new ArrayList<>();
        if (!subject.equalsIgnoreCase("All")) {
            for (TestModelNew model : models) {
                String testSubject = model.getSm_sub_name();
                if (testSubject.contains(subject)) {
                    filteredModelList.add(model);
                }
            }
        } else {
            filteredModelList = models;
        }
        return filteredModelList;
    }

    private void fetchSubjectList(String scrCoId) {
        Log.d(TAG, "fetchSubjectList ScrCoId : " + scrCoId);
        mBinding.swipeToRefresh.setRefreshing(true);
        Call<FetchSubjectResponseList> call = apiService.fetchSubjectList(Constant.SCR_CO_ID);
        call.enqueue(new Callback<FetchSubjectResponseList>() {
            @Override
            public void onResponse(Call<FetchSubjectResponseList> call, Response<FetchSubjectResponseList> response) {
                //ProgressDialog.getInstance().dismiss();
                if (response.body() != null) {
                    mBinding.swipeToRefresh.setRefreshing(false);
                    mViewModel.setAllSubjectList(response.body().getFetchSubjectResponseList());
                }
            }

            @Override
            public void onFailure(Call<FetchSubjectResponseList> call, Throwable t) {
                //ProgressDialog.getInstance().dismiss();
                mBinding.swipeToRefresh.setRefreshing(false);
                showToast("Something went wrong!!");
                t.printStackTrace();
            }
        });
    }

    private void fetchTestList(HashMap<String, String> params) {
        Log.d(TAG, "fetchTestList Params : " + params);
        Log.d(TAG, "initViews Flag : " + flag);
        mBinding.swipeToRefresh.setRefreshing(true);
        Call<TestListResponse> call = apiService.fetchTestList(
                params.get(Constant.u_user_id),
                params.get(CASE),
                params.get(Constant.tm_recent_test),
                params.get(Constant.tm_popular_test),
                params.get(Constant.tm_diff_level),
                params.get(Constant.tm_avg_rating),
                params.get(Constant.tm_id)
        );
        call.enqueue(new Callback<TestListResponse>() {
            @Override
            public void onResponse(Call<TestListResponse> call, Response<TestListResponse> response) {
                //ProgressDialog.getInstance().dismiss();
                mBinding.swipeToRefresh.setRefreshing(false);
                if (response.body() != null && response.body().getResponse().equals("200")) {
                    if (params.get(Constant.tm_id) != null &&
                            !(params.get(Constant.tm_id).isEmpty())) {
                        if (response.body().getTestList().size() > 0) {
                            mAdapter.updateList(response.body().getTestList());
                        } else {
                            mBinding.tvNoTest.setVisibility(View.VISIBLE);
                        }
                    } else {
                        List<TestModelNew> testList = response.body().getTestList();
                        for (TestModelNew testModelNew : testList) {
                            testModelNew.setFlag("PRACTICE");
                            testModelNew.setUserId(getUserId(getActivity()));
                        }
                        mViewModel.insert(testList);
                    }
                } else if (response.body().getResponse().equals("501")) {
                    resetSettingAndLogout();
                } else {
                    showErrorDialog(getActivity(), response.body().getResponse(), response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<TestListResponse> call, Throwable t) {
                mBinding.swipeToRefresh.setRefreshing(false);
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
        //MainActivity.navController.navigate(R.id.nav_test_details, bundle);
    }

    @Override
    public void onStartTestClick(TestModelNew testModel) {
        Log.d(TAG, "onStartTestClick: " + testModel.getTm_id());
        Intent intent = new Intent(getActivity(), PracticeTestActivity.class);
        intent.putExtra(Constant.TM_ID, testModel.getTm_id());
        startActivity(intent);
    }

    @Override
    public void onCommentClick(TestModelNew testModel) {
        Bundle bundle = new Bundle();
        tmId = testModel.getTm_id();
        bundle.putInt("tmId", tmId);
        bundle.putString(Constant.CALL_FROM, Module.Test.toString());
        NavHostFragment.findNavController(this).navigate(R.id.nav_comments, bundle);
        Log.d(TAG, "onCommentClick TmId : " + testModel.getTm_id());
        //CommentDialog commentDialog = new CommentDialog(getActivity(), testModel.getTm_id(), this);
        //commentDialog.show();
    }

    @Override
    public void onShareClick(int testid) {
        /*Bundle bundle = new Bundle();
        bundle.putString("testId", String.valueOf(testid));
        bundle.putInt("call_from", test);
        NavHostFragment.findNavController(this).navigate(R.id.nav_share, bundle);*/
        new ShareQuestionDialog(getActivity(), String.valueOf(testid), getUserId(mContext)
                , getDeviceId(mContext), "T")
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
        //MainActivity.navController.navigate(R.id.nav_test_attempt_history,bundle);
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
    public void onViewImage(String path) {
        showFullScreen(path);
    }
}
