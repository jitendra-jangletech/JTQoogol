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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.activities.PracticeTestActivity;
import com.jangletech.qoogol.adapter.TestListAdapter;
import com.jangletech.qoogol.databinding.TestFavouriteFragmentBinding;
import com.jangletech.qoogol.dialog.CommentDialog;
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

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TestFavouriteFragment extends BaseFragment implements TestListAdapter.TestClickListener, CommentDialog.CommentClickListener, PublicProfileDialog.PublicProfileClickListener, LikeListingDialog.onItemClickListener, ShareQuestionDialog.ShareDialogListener {

    private static final String TAG = "TestFavouriteFragment";
    private MyTestViewModel mViewModel;
    private TestFavouriteFragmentBinding mBinding;
    private List<TestModelNew> testModelNewList;
    private TestListAdapter testListAdapter;
    private int selectedPos = -1;
    private TestModelNew selectedTestCard;
    private boolean isFragmentVisible = false;
    private ApiInterface apiService = ApiClient.getInstance().getApi();

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isFragmentVisible) {
            fetchTestList();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.test_favourite_fragment, container, false);
        mViewModel = ViewModelProviders.of(this).get(MyTestViewModel.class);
        return mBinding.getRoot();
    }

    private void initViews() {

        mBinding.btnCheckNewTest.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).navigate(R.id.nav_test_my);
        });

        mBinding.swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchTestList();
            }
        });

        if (!isFragmentVisible) {
            isFragmentVisible = true;
            fetchTestList();
        }

        mViewModel.getAllTestList().observe(getActivity(), new Observer<List<TestModelNew>>() {
            @Override
            public void onChanged(@Nullable final List<TestModelNew> favTests) {
                testModelNewList = favTests;
                Log.d(TAG, "onChanged Fav Tests : " + favTests.size());
                if (testModelNewList.size() > 0) {
                    setFavTestList();
                } else {
                    mBinding.tvNoFavTest.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void fetchTestList() {
        Log.d(TAG, "fetchTestList UserId : " + getUserId(getActivity()));
        mBinding.swipeToRefresh.setRefreshing(true);
        Call<TestListResponse> call = apiService.fetchTestList(
                getUserId(getActivity()),
                "FV",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                ""
        );
        call.enqueue(new Callback<TestListResponse>() {
            @Override
            public void onResponse(Call<TestListResponse> call, Response<TestListResponse> response) {
                mBinding.swipeToRefresh.setRefreshing(false);
                if (response.body() != null && response.body().getResponse().equals("200")) {
                    Log.d(TAG, "Fav List Size is  : " + response.body().getTestList().size());
                    mViewModel.setAllTestList(response.body().getTestList());
                } else if (response.body().getResponse().equals("501")) {
                    resetSettingAndLogout();
                } else {
                    AppUtils.showToast(getActivity(), null, response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<TestListResponse> call, Throwable t) {
                mBinding.swipeToRefresh.setRefreshing(false);
                t.printStackTrace();
                apiCallFailureDialog(t);
            }
        });
    }

    private void setFavTestList() {
        testListAdapter = new TestListAdapter(requireActivity(), testModelNewList, this, "FAV");
        mBinding.favTestListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.favTestListRecyclerView.setAdapter(testListAdapter);
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
        new LikeListingDialog(true, getActivity(), testModel.getTm_id(), this)
                .show();
    }

    @Override
    public void favClick(TestModelNew testModelNew) {
        Log.d(TAG, "favClick Value : " + testModelNew.isFavourite());
        mViewModel.updateFav("PRACTICE", getUserId(getActivity()), testModelNew.getTm_id(), testModelNew.isFavourite());
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    @Override
    public void onAttemptsClick(TestModelNew testModel) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("PARAMS", testModel);
        NavHostFragment.findNavController(this).navigate(R.id.nav_test_attempt_history, bundle);
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
    public void onItemCLick(String user_id) {
        PublicProfileDialog publicProfileDialog = new PublicProfileDialog(getActivity(), user_id, this);
        publicProfileDialog.show();
    }

    @Override
    public void onBackClick(int count) {
        Log.d(TAG, "onBackClick Comment Count : " + count);
        int commentCount = Integer.parseInt(selectedTestCard.getCommentsCount()) + count;
        selectedTestCard.setCommentsCount("" + commentCount);
        testListAdapter.notifyItemChanged(selectedPos, selectedTestCard);
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
        new ShareQuestionDialog(getActivity(), String.valueOf(testModelNew.getTm_id()), getUserId(getActivity())
                , getDeviceId(getActivity()), "T", this)
                .show();
    }

    @Override
    public void onSharedSuccess(int count) {
        Log.d(TAG, "onSharedSuccess Count : " + count);
        int shareCount = Integer.parseInt(selectedTestCard.getShareCount());
        selectedTestCard.setShareCount("" + (shareCount + count));
        testListAdapter.notifyItemChanged(selectedPos, selectedTestCard);
    }

}
