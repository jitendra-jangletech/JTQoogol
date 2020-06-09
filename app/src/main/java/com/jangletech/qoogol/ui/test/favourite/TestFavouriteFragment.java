package com.jangletech.qoogol.ui.test.favourite;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.activities.MainActivity;
import com.jangletech.qoogol.activities.PracticeTestActivity;
import com.jangletech.qoogol.adapter.TestListAdapter;
import com.jangletech.qoogol.databinding.TestFavouriteFragmentBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.enums.Module;
import com.jangletech.qoogol.model.ProcessQuestion;
import com.jangletech.qoogol.model.StartResumeTestResponse;
import com.jangletech.qoogol.model.TestListResponse;
import com.jangletech.qoogol.model.TestModelNew;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.ui.test.my_test.MyTestViewModel;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jangletech.qoogol.util.Constant.test;

public class TestFavouriteFragment extends BaseFragment implements TestListAdapter.TestClickListener {

    private static final String TAG = "TestFavouriteFragment";
    private MyTestViewModel mViewModel;
    private TestFavouriteFragmentBinding mBinding;
    private List<TestModelNew> testModelNewList;
    private TestListAdapter testListAdapter;
    ApiInterface apiService = ApiClient.getInstance().getApi();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.test_favourite_fragment, container, false);
        mViewModel = ViewModelProviders.of(this).get(MyTestViewModel.class);
        initViews();
        return mBinding.getRoot();
    }

    private void initViews() {

        mBinding.btnCheckNewTest.setOnClickListener(v->{
            NavHostFragment.findNavController(this).navigate(R.id.nav_test_my);
        });

        fetchTestList("FV");
        mViewModel.getAllTestList().observe(getActivity(), new Observer<List<TestModelNew>>() {
            @Override
            public void onChanged(@Nullable final List<TestModelNew> favTests) {
                testModelNewList = favTests;
                Log.d(TAG, "onChanged Fav Tests : " + testModelNewList);
                if(testModelNewList.size() > 0) {
                    setFavTestList();
                }else{
                    mBinding.tvNoFavTest.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void fetchTestList(String caseFav) {
        ProgressDialog.getInstance().show(getActivity());
        Call<TestListResponse> call = apiService.fetchTestList(new PreferenceManager(getActivity()).getInt(Constant.USER_ID), caseFav);
        call.enqueue(new Callback<TestListResponse>() {
            @Override
            public void onResponse(Call<TestListResponse> call, Response<TestListResponse> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.body() != null && response.body().getResponse().equals("200")) {
                    mViewModel.setAllTestList(response.body().getTestList());
                } else if (response.body().getResponse().equals("501")) {
                    resetSettingAndLogout();
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

    private void setFavTestList() {
        testListAdapter = new TestListAdapter(requireActivity(), testModelNewList, this);
        mBinding.favTestListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.favTestListRecyclerView.setAdapter(testListAdapter);
    }


    @Override
    public void onTestItemClick(TestModelNew testModel) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.TEST_NAME, testModel);
        Navigation.findNavController(requireActivity(),R.id.nav_host_fragment).navigate(R.id.nav_test_details);
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
        bundle.putInt("tmId", testModel.getTm_id());
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
        if (isChecked) {
            //callApi(1, pos);
        } else {
            //callApi(0, pos);
        }
    }

    @Override
    public void onFavouriteClick(TestModelNew testModel, boolean isChecked,int pos) {
        HashMap<String, Integer> params = new HashMap<>();
        params.put(Constant.tm_id, testModel.getTm_id());
        params.put(Constant.isFavourite,0);
        params.put("POS",pos);
        if (isChecked)
            removeFavTest(params);
        else
            removeFavTest(params);
    }

    @Override
    public void onAttemptsClick(TestModelNew testModel) {

    }

    private void removeFavTest(HashMap<String, Integer> map) {
        Log.d(TAG, "favTest params : " + map);
        ProgressDialog.getInstance().show(getActivity());
        Call<ProcessQuestion> call = apiService.addFavTest(new PreferenceManager(getActivity()).getInt(Constant.USER_ID), map.get(Constant.tm_id), "I", map.get(Constant.isFavourite));
        call.enqueue(new Callback<ProcessQuestion>() {
            @Override
            public void onResponse(Call<ProcessQuestion> call, Response<ProcessQuestion> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.body() != null && response.body().getResponse().equals("200")) {
                    if(testModelNewList.size() == 1)
                        mBinding.tvNoFavTest.setVisibility(View.VISIBLE);
                    testListAdapter.deleteFav(map.get("POS"));
                } else {
                    showErrorDialog(getActivity(), response.body().getResponse(), response.body().getMessage());
                }
            }
        @Override
        public void onFailure (Call < ProcessQuestion > call, Throwable t){
            ProgressDialog.getInstance().dismiss();
            showToast("Something went wrong!!");
            t.printStackTrace();
        }
    });
}
}
