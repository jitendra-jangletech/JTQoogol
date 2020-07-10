package com.jangletech.qoogol.ui.home;

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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager.widget.ViewPager;

import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.activities.MainActivity;
import com.jangletech.qoogol.databinding.FragmentHomeBinding;
import com.jangletech.qoogol.model.DashBoard;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.ui.QuestionAnalyticsFragment;
import com.jangletech.qoogol.ui.TestAnalyticsFragment;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends BaseFragment implements View.OnClickListener,
        OnChartValueSelectedListener, SearchView.OnQueryTextListener {

    private double total;
    private HomeViewModel mViewModel;
    private FragmentHomeBinding mBinding;
    private DashBoard globalDashboard;
    private static final String TAG = "HomeFragment";
    private ApiInterface apiService = ApiClient.getInstance().getApi();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        mBinding.setLifecycleOwner(this);
        initViews();
        return mBinding.getRoot();
    }

    private void setupViewPager(ViewPager viewPager, DashBoard dashBoard) {
        mBinding.tabs.setupWithViewPager(viewPager);
        Adapter adapter = new Adapter(getChildFragmentManager());
        adapter.addFragment(new TestAnalyticsFragment(dashBoard),"Test");
        adapter.addFragment(new QuestionAnalyticsFragment(dashBoard),"Questions");
        viewPager.setAdapter(adapter);
    }

    private ArrayList<String> getXValues() {
        ArrayList<String> xValues = new ArrayList<>();
        xValues.add("Likes");
        xValues.add("Favourites");
        xValues.add("Share");
        xValues.add("Taken");
        return xValues;
    }

    private ArrayList<BarDataSet> getBarValues() {
        ArrayList<BarDataSet> barDataSets = new ArrayList<>();
        ArrayList<BarEntry> dataVals = new ArrayList<>();
        dataVals.add(new BarEntry(2.0f, 0));
        dataVals.add(new BarEntry(8.0f, 1));
        dataVals.add(new BarEntry(1.0f, 2));
        dataVals.add(new BarEntry(5.0f, 3));

        BarDataSet barDataSet = new BarDataSet(dataVals, "Test Analytics");
        barDataSet.setColor(Color.RED);
        barDataSets.add(barDataSet);

        return barDataSets;
    }


    private void initViews() {
        mBinding.connectionLayout.setOnClickListener(this);
        mBinding.friendsLayout.setOnClickListener(this);
        mBinding.followersLayout.setOnClickListener(this);
        mBinding.followingLayout.setOnClickListener(this);
        mBinding.connectionLayout.setOnClickListener(this);
        //mBinding.pieChart.setOnChartValueSelectedListener(this);
        fetchDashboardDetails();
        mViewModel.getDashboardDetails(getUserId()).observe(getViewLifecycleOwner(), new Observer<DashBoard>() {
            @Override
            public void onChanged(@Nullable final DashBoard dashBoard1) {
                if (dashBoard1 != null) {
                    globalDashboard = dashBoard1;
                    //setTestAnalytics(dashBoard1);
                    setDashBoardData(dashBoard1);
                }
            }
        });
    }


    private void setDashBoardData(DashBoard dashBoard) {
        Log.d(TAG, "setDashBoardData Followers Data : " + dashBoard.getFollowers());
        setupViewPager(mBinding.viewpager, dashBoard);
        mBinding.followers.setText(dashBoard.getFollowers());
        mBinding.following.setText(dashBoard.getFollowings());
        mBinding.friends.setText(dashBoard.getU_friends());
        mBinding.tvConnections.setText(dashBoard.getConnectionCount());
        mBinding.tvCredits.setText("Credits : " + dashBoard.getUp_credits());

        //Nav values
        MainActivity.tvNavConnections.setText(dashBoard.getConnectionCount());
        MainActivity.tvNavCredits.setText(dashBoard.getUp_credits());
        MainActivity.tvNavFollowing.setText(dashBoard.getFollowings());

        if (dashBoard.getFirstName() != null || dashBoard.getLastName() != null) {
            saveString(Constant.u_first_name, dashBoard.getFirstName());
            MainActivity.textViewDisplayName.setText(dashBoard.getFirstName() + " " + dashBoard.getLastName());
        } else {
            MainActivity.textViewDisplayName.setText("Qoogol User");
        }

        String gender = new PreferenceManager(requireActivity()).getString(Constant.GENDER).replace("'", "");
        Log.d(TAG, "setDashBoardData Img Url : " + dashBoard.getProfilePicUrl());
        Log.d(TAG, "setDashBoardData Gender : " + gender);
        if (dashBoard.getProfilePicUrl() != null && !dashBoard.getProfilePicUrl().isEmpty()) {
            new PreferenceManager(requireActivity()).saveString("PROF_IMG", getProfileImageUrl(dashBoard.getProfilePicUrl()));
            loadProfilePic(getProfileImageUrl(dashBoard.getProfilePicUrl()), MainActivity.profileImage);
        } else {
            new PreferenceManager(requireActivity()).saveString("PROF_IMG", "");
            if (gender.equalsIgnoreCase("F")) {
                loadProfilePic(Constant.PRODUCTION_FEMALE_PROFILE_API, MainActivity.profileImage);
            }
            if (gender.equalsIgnoreCase("M")) {
                loadProfilePic(Constant.PRODUCTION_MALE_PROFILE_API, MainActivity.profileImage);
            }
        }
    }

    private void fetchDashboardDetails() {
        HashMap<String, String> params = new HashMap<>();
        params.put(Constant.u_user_id, getUserId());
        params.put(Constant.device_id, getDeviceId());
        Log.d(TAG, "fetchDashboardDetails UserId : " + getUserId());
        //ProgressDialog.getInstance().show(getActivity());
        mBinding.swipeToRefresh.setRefreshing(true);
        Call<DashBoard> call = apiService.fetchDashBoardDetails(
                params.get(Constant.u_user_id),
                params.get(Constant.device_id)
        );
        call.enqueue(new Callback<DashBoard>() {
            @Override
            public void onResponse(Call<DashBoard> call, Response<DashBoard> response) {
                //ProgressDialog.getInstance().dismiss();
                mBinding.swipeToRefresh.setRefreshing(false);
                Log.d(TAG, "onResponse Done: " + response.body().getResponse());
                if (response.body() != null) {
                    DashBoard dashBoard = response.body();
                    dashBoard.setUserId(getUserId());
                    mViewModel.insert(dashBoard);
                }
            }

            @Override
            public void onFailure(Call<DashBoard> call, Throwable t) {
                mBinding.swipeToRefresh.setRefreshing(false);
                //ProgressDialog.getInstance().dismiss();
                apiCallFailureDialog(t);
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.connectionLayout:
                openConnections(0);
                break;
            case R.id.friends_layout:
                openConnections(1);
                break;
            case R.id.followers_layout:
                openConnections(2);
                break;
            case R.id.following_layout:
                openConnections(3);
                break;
        }
    }

    private void openConnections(int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        NavHostFragment.findNavController(this).navigate(R.id.nav_connections, bundle);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        PieEntry pieEntry = (PieEntry) e;
        //setDynamicPieChartData(pieEntry.getLabel());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);
        //String timeAgo = DateUtils.getTimeAgo("");
        //Log.d(TAG, "Time Ago : "+timeAgo);
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
    public void onNothingSelected() {

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    public static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}