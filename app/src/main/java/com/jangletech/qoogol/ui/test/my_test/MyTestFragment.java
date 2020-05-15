package com.jangletech.qoogol.ui.test.my_test;

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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.activities.MainActivity;
import com.jangletech.qoogol.activities.StartTestActivity;
import com.jangletech.qoogol.adapter.TestListAdapter;
import com.jangletech.qoogol.databinding.FragmentTestMyBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.enums.Module;
import com.jangletech.qoogol.model.FetchSubjectResponse;
import com.jangletech.qoogol.model.FetchSubjectResponseList;
import com.jangletech.qoogol.model.ProcessQuestion;
import com.jangletech.qoogol.model.TestListResponse;
import com.jangletech.qoogol.model.TestModelNew;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyTestFragment extends BaseFragment implements TestListAdapter.TestClickListener, SearchView.OnQueryTextListener {

    private static final String TAG = "MyTestFragment";
    private MyTestViewModel mViewModel;
    private FragmentTestMyBinding mBinding;
    private TestListAdapter testAdapter;
    private List<TestModelNew> testList;
    private int tmId;
    ApiInterface apiService = ApiClient.getInstance().getApi();

    public static MyTestFragment newInstance() {
        return new MyTestFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Log.d(TAG, "Android Id : " + getDeviceId());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_test_my, container, false);
        mViewModel = new ViewModelProvider(this).get(MyTestViewModel.class);
        initViews(mBinding);
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
                MainActivity.navController.navigate(R.id.nav_test_filter, bundle);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initViews(FragmentTestMyBinding mBinding) {
        fetchTestList();
        mViewModel.getAllTestList().observe(getActivity(), new Observer<List<TestModelNew>>() {
            @Override
            public void onChanged(@Nullable final List<TestModelNew> tests) {
                Log.d(TAG, "onChanged Size : " + tests.size());
                testList = tests;
                setMyTestList(tests);
            }
        });

        fetchSubjectList();
        mViewModel.getAllSubjects().observe(getActivity(), new Observer<List<FetchSubjectResponse>>() {
            @Override
            public void onChanged(@Nullable final List<FetchSubjectResponse> subjects) {
                Log.d(TAG, "onChanged Subjects Size : " + subjects.size());
                prepareSubjectChips(subjects);
            }
        });

        mBinding.subjectsChipGrp.setOnCheckedChangeListener((chipGroup, id) -> {
            Chip chip = ((Chip) chipGroup.getChildAt(chipGroup.getCheckedChipId()));
            if (chip != null) {
                if (chip.isChecked()) {
                    setCheckedChip(mBinding.subjectsChipGrp);
                    List<TestModelNew> filteredModelList = filterBySubject(testList, chip.getText().toString());
                    if (filteredModelList.size() > 0) {
                        mBinding.tvNoTest.setVisibility(View.GONE);
                        testAdapter.setSearchResult(filteredModelList);
                    } else {
                        testAdapter.setSearchResult(filteredModelList);
                        mBinding.tvNoTest.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        mBinding.fabFilter.setOnClickListener(v -> {
            MainActivity.navController.navigate(R.id.nav_test_filter);
        });
    }

    public void setMyTestList(List<TestModelNew> testList) {
        testAdapter = new TestListAdapter(new MyTestFragment(), testList, this);
        mBinding.testListRecyclerView.setHasFixedSize(true);
        mBinding.testListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.testListRecyclerView.setAdapter(testAdapter);
    }

    private void prepareSubjectChips(List<FetchSubjectResponse> subjectResponseList) {

        mBinding.subjectsChipGrp.removeAllViews();
        for (int i = 0; i < subjectResponseList.size(); i++) {
            Chip chip = (Chip) LayoutInflater.from(mBinding.subjectsChipGrp.getContext()).inflate(R.layout.chip_layout, mBinding.subjectsChipGrp, false);
            chip.setText(subjectResponseList.get(i).getSm_sub_name());
            chip.setTag("Subjects");
            chip.setId(i);
            if (i == 0) {
                chip.setChecked(true);
            }
            chip.setClickable(true);
            chip.setCheckable(true);
            mBinding.subjectsChipGrp.addView(chip);
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

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<TestModelNew> filteredModelList = filter(testList, newText);
        testAdapter.setSearchResult(filteredModelList);
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

    private void fetchSubjectList() {
        ProgressDialog.getInstance().show(getActivity());
        Call<FetchSubjectResponseList> call = apiService.fetchSubjectList(new PreferenceManager(getActivity()).getInt(Constant.USER_ID));
        call.enqueue(new Callback<FetchSubjectResponseList>() {
            @Override
            public void onResponse(Call<FetchSubjectResponseList> call, Response<FetchSubjectResponseList> response) {
                ProgressDialog.getInstance().dismiss();
                // if (response.body() != null && response.body().getResponse().equals("200")) {
                mViewModel.setAllSubjectList(response.body().getFetchSubjectResponseList());
                //}
                /*else {
                    showErrorDialog(getActivity(), response.body().getResponse(), response.body().getMessage());
                }*/
            }

            @Override
            public void onFailure(Call<FetchSubjectResponseList> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                showToast("Something went wrong!!");
                t.printStackTrace();
            }
        });
    }

    private void fetchTestList() {
        ProgressDialog.getInstance().show(getActivity());
        Call<TestListResponse> call = apiService.fetchTestList(new PreferenceManager(getActivity()).getInt(Constant.USER_ID));//todo change userId
//        Call<TestListResponse> call = apiService.fetchTestList(1003);//todo change userId
        call.enqueue(new Callback<TestListResponse>() {
            @Override
            public void onResponse(Call<TestListResponse> call, Response<TestListResponse> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.body() != null && response.body().getResponse().equals("200")) {
                    mViewModel.setAllTestList(response.body().getTestList());
                    Log.d(TAG, "onResponse: " + response.body().getTestList());
                    Log.d(TAG, "onResponse: " + response.body().getTestList().size());
                } else {
                    showErrorDialog(getActivity(), response.body().getResponse(), response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<TestListResponse> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                showToast("Something went wrong!!");
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onTestItemClick(TestModelNew testModel) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.TEST_NAME, testModel);
        MainActivity.navController.navigate(R.id.nav_test_details, bundle);
    }

    @Override
    public void onStartTestClick(TestModelNew testModel) {
        Intent intent = new Intent(getActivity(), StartTestActivity.class);
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
    }

    @Override
    public void onShareClick(TestModelNew testModel) {

    }

    @Override
    public void onLikeClick(TestModelNew testModel, int pos, boolean isChecked) {
        if (isChecked) {
            callLikeTestApi(0, pos);
        } else {
            callLikeTestApi(1, pos);
        }
    }

    @Override
    public void onFavouriteClick(TestModelNew testModel, boolean isChecked) {

    }

    private void callLikeTestApi(int like, int pos) {
        doLikeTest(like,pos);
    }

    private void doLikeTest(int like,int pos) {
        ProgressDialog.getInstance().show(getActivity());
        Call<ProcessQuestion> call = apiService.addTestLike(new PreferenceManager(getActivity()).getInt(Constant.USER_ID), tmId, "I", like);
        call.enqueue(new Callback<ProcessQuestion>() {
            @Override
            public void onResponse(Call<ProcessQuestion> call, Response<ProcessQuestion> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.body() != null && response.body().getResponse().equals("200")) {
                    Log.d(TAG, "onResponse Like : " + response.body().getResponse());
                    Log.d(TAG, "onResponse Updated Like Count: " + response.body().getLikeCount());
                    //testList.get(pos).setLikeCount();
                    testList.get(pos).setLike(false);
                    testAdapter.updateList(testList, pos);

                } else {
                    showErrorDialog(getActivity(), response.body().getResponse(), response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<ProcessQuestion> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                showToast("Something went wrong!!");
                t.printStackTrace();
            }
        });
    }
}
