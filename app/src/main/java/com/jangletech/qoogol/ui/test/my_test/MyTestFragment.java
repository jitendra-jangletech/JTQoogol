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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.activities.MainActivity;
import com.jangletech.qoogol.activities.PracticeTestActivity;
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
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jangletech.qoogol.util.Constant.test;

public class MyTestFragment extends BaseFragment implements TestListAdapter.TestClickListener, SearchView.OnQueryTextListener {

    private static final String TAG = "MyTestFragment";
    private MyTestViewModel mViewModel;
    private FragmentTestMyBinding mBinding;
    private TestListAdapter mAdapter;
    private List<TestModelNew> testList;
    private Context mContext;
    private int tmId;
    ApiInterface apiService = ApiClient.getInstance().getApi();

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
        Log.d(TAG, "Android Id : " + getDeviceId());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_test_my, container, false);
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
                Navigation.findNavController(requireActivity(),R.id.nav_host_fragment).navigate(R.id.nav_test_filter,bundle);
                //MainActivity.navController.navigate(R.id.nav_test_filter, bundle);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initViews() {

        if(getArguments()!=null){
            if(!getArguments().getString("CALL_FROM").equalsIgnoreCase("MY_TEST")){
                getActionBar().setTitle(getArguments().getString("CALL_FROM"));
                mBinding.horizontalScrollView.setVisibility(View.GONE);
            }
        }


        mBinding.swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchTestList("");
            }
        });

        fetchSubjectList(new PreferenceManager(requireActivity()).getString(Constant.scr_co_id));
        fetchTestList("");
        mViewModel.getAllSubjects().observe(getViewLifecycleOwner(), new Observer<List<FetchSubjectResponse>>() {
            @Override
            public void onChanged(@Nullable final List<FetchSubjectResponse> subjects) {
                if (subjects != null) {
                    Log.d(TAG, "onChanged Subjects Size : " + subjects.size());
                    ArrayList<String> subjectList = new ArrayList<>();
                    for(FetchSubjectResponse obj : subjects){
                        if(!subjectList.contains(obj.getSm_sub_name()))
                        subjectList.add(obj.getSm_sub_name());
                    }
                    prepareSubjectChips(subjectList);
                }
            }
        });

//        } else {
//            fetchTestList("FV");
//            mBinding.horizontalScrollView.setVisibility(View.GONE);
//        }

        mViewModel.getAllTests().observe(getViewLifecycleOwner(), new Observer<List<TestModelNew>>() {
            @Override
            public void onChanged(@Nullable final List<TestModelNew> tests) {
                if (tests != null) {
                    Log.d(TAG, "onChanged Size : " + tests.size());
                    testList = tests;
                    setMyTestList(tests);
                }
            }
        });


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
        mAdapter = new TestListAdapter(requireActivity(), testList, this);
        mBinding.testListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.testListRecyclerView.setAdapter(mAdapter);
    }

    private void prepareSubjectChips(ArrayList<String> subjects) {
        mBinding.subjectsChipGrp.removeAllViews();
        int idCounter = 0;
        for (String subject: subjects) {
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
        //ProgressDialog.getInstance().show(getActivity());
        mBinding.swipeToRefresh.setRefreshing(true);
        Call<FetchSubjectResponseList> call = apiService.fetchSubjectList(scrCoId);
        call.enqueue(new Callback<FetchSubjectResponseList>() {
            @Override
            public void onResponse(Call<FetchSubjectResponseList> call, Response<FetchSubjectResponseList> response) {
                //ProgressDialog.getInstance().dismiss();
                mBinding.swipeToRefresh.setRefreshing(false);
                mViewModel.setAllSubjectList(response.body().getFetchSubjectResponseList());
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

    private void fetchTestList(String caseFav) {
        mBinding.swipeToRefresh.setRefreshing(true);
        Call<TestListResponse> call = apiService.fetchTestList(new PreferenceManager(getActivity()).getInt(Constant.USER_ID), caseFav);//todo change userId
        call.enqueue(new Callback<TestListResponse>() {
            @Override
            public void onResponse(Call<TestListResponse> call, Response<TestListResponse> response) {
                //ProgressDialog.getInstance().dismiss();
                mBinding.swipeToRefresh.setRefreshing(false);
                if (response.body() != null && response.body().getResponse().equals("200")) {
                    //mViewModel.setAllTestList(response.body().getTestList());
                    mViewModel.insert(response.body().getTestList());
                    Log.d(TAG, "onResponse: " + response.body().getTestList());
                    Log.d(TAG, "onResponse: " + response.body().getTestList().size());
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
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onTestItemClick(TestModelNew testModel) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.TEST_NAME, testModel);
        Navigation.findNavController(requireActivity(),R.id.nav_host_fragment).navigate(R.id.nav_test_details,bundle);
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
    }

    @Override
    public void onShareClick(int testid) {
        Bundle bundle = new Bundle();
        bundle.putString("testId", String.valueOf(testid));
        bundle.putInt("call_from", test);
        NavHostFragment.findNavController(this).navigate(R.id.nav_share, bundle);
    }

    @Override
    public void onLikeClick(TestModelNew testModel, int pos, boolean isChecked) {
        callApi(isChecked ? 1 : 0, pos);
    }

    @Override
    public void onFavouriteClick(TestModelNew testModel, boolean isChecked, int pos) {
        HashMap<String, Integer> params = new HashMap<>();
        if (isChecked) {
            params.put(Constant.isFavourite, 1);
            params.put(Constant.tm_id, testModel.getTm_id());
            favTest(params);
        } else {
            params.put(Constant.isFavourite, 0);
            params.put(Constant.tm_id, testModel.getTm_id());
            favTest(params);
        }
    }

    @Override
    public void onAttemptsClick(TestModelNew testModel) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("PARAMS", testModel);
        NavHostFragment.findNavController(this).navigate(R.id.nav_test_attempt_history, bundle);
        //MainActivity.navController.navigate(R.id.nav_test_attempt_history,bundle);
    }

    private void callApi(int like, int pos) {
        doLikeTest(like, pos, testList.get(pos).getTm_id());
    }

    private void favTest(HashMap<String, Integer> map) {
        Log.d(TAG, "favTest params : " + map);
        ProgressDialog.getInstance().show(getActivity());
        Call<ProcessQuestion> call = apiService.addFavTest(new PreferenceManager(getActivity()).getInt(Constant.USER_ID), map.get(Constant.tm_id), "I", map.get(Constant.isFavourite));
        call.enqueue(new Callback<ProcessQuestion>() {
            @Override
            public void onResponse(Call<ProcessQuestion> call, Response<ProcessQuestion> response) {
                ProgressDialog.getInstance().dismiss();
                try {
                    if (response.body() != null && response.body().getResponse().equals("200")) {
                        if (map.get(Constant.isFavourite) == 0) {
                            showToast("Removed from favourites");
                        } else {
                            showToast("Added to favourites");
                        }
                    } else {
                        showErrorDialog(getActivity(), response.body().getResponse(), response.body().getMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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

    private void doLikeTest(int like, int pos, int testId) {
        ProgressDialog.getInstance().show(getActivity());
        Call<ProcessQuestion> call = apiService.addTestLike(new PreferenceManager(getActivity()).getInt(Constant.USER_ID), testId, "I", like);
        call.enqueue(new Callback<ProcessQuestion>() {
            @Override
            public void onResponse(Call<ProcessQuestion> call, Response<ProcessQuestion> response) {
                ProgressDialog.getInstance().dismiss();
                try {
                    if (response.body() != null && response.body().getResponse().equals("200")) {
                        int likeCount = Integer.parseInt(testList.get(pos).getLikeCount());
                        if (like == 1) {
                            Log.e(TAG, "like 0 ");
                            testList.get(pos).setLike(false);
                            if (likeCount <= 0)
                                testList.get(pos).setLikeCount("0");
                            else
                                testList.get(pos).setLikeCount("" + (likeCount - 1));
                        }
                        if (like == 0) {
                            testList.get(pos).setLike(true);
                            testList.get(pos).setLikeCount("" + (likeCount + 1));
                        }
                        //testList.set(pos, testList.get(pos));
                        mAdapter.updateList(testList);

                    } else {
                        showErrorDialog(getActivity(), response.body().getResponse(), response.body().getMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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

    @Override
    public void onDetach() {
        super.onDetach();
        mAdapter = null;
    }
}
