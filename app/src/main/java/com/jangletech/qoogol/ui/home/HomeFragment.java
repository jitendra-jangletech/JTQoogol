package com.jangletech.qoogol.ui.home;

import android.app.Activity;
import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.MenuItemCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
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
import com.jangletech.qoogol.model.Education;
import com.jangletech.qoogol.model.FetchEducationResponse;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.ui.QuestionAnalyticsFragment;
import com.jangletech.qoogol.ui.TestAnalyticsFragment;
import com.jangletech.qoogol.ui.educational_info.AddEduDialog;
import com.jangletech.qoogol.util.AESSecurities;
import com.jangletech.qoogol.util.AppUtils;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;
import com.jangletech.qoogol.util.TinyDB;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends BaseFragment implements View.OnClickListener,
        OnChartValueSelectedListener, SearchView.OnQueryTextListener, AddEduDialog.ApiCallListener {

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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated IS_EDUCATION_ADDED : " + TinyDB.getInstance(getActivity()).getBoolean(Constant.IS_EDUCATION_ADDED));
        if (!TinyDB.getInstance(getActivity()).getBoolean(Constant.IS_EDUCATION_ADDED)) {
            getEducationInfoList();
        }

        mBinding.swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchDashboardDetails();
            }
        });
    }

    private void setupViewPager(ViewPager viewPager, DashBoard dashBoard) {
        mBinding.tabs.setupWithViewPager(viewPager);
        Adapter adapter = new Adapter(getChildFragmentManager());
        adapter.addFragment(new TestAnalyticsFragment(dashBoard), "Test");
        adapter.addFragment(new QuestionAnalyticsFragment(dashBoard), "Questions");
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
        fetchDashboardDetails();
        mViewModel.getDashboardDetails(getUserId(getActivity())).observe(getViewLifecycleOwner(), new Observer<DashBoard>() {
            @Override
            public void onChanged(@Nullable final DashBoard dashBoard1) {
                if (dashBoard1 != null) {
                    globalDashboard = dashBoard1;
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
            saveString(getActivity(), Constant.u_first_name, dashBoard.getFirstName());
            String uName = AESSecurities.getInstance().decrypt(TinyDB.getInstance(getActivity()).getString(Constant.cf_key1), dashBoard.getFirstName()) + " " + AESSecurities.getInstance().decrypt(getLocalString(Constant.cf_key2), dashBoard.getLastName());
            MainActivity.textViewDisplayName.setText(uName);
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
        try {
            HashMap<String, String> params = new HashMap<>();
            params.put(Constant.u_user_id, getUserId(getActivity()));
            params.put(Constant.device_id, getDeviceId(getActivity()));
            Log.d(TAG, "fetchDashboardDetails UserId : " + getUserId(getActivity()));
            //ProgressDialog.getInstance().show(getActivity());
            mBinding.swipeToRefresh.setRefreshing(true);
            Call<DashBoard> call = apiService.fetchDashBoardDetails(
                    params.get(Constant.u_user_id),
                    params.get(Constant.device_id));

            Activity activity = getActivity();
            call.enqueue(new Callback<DashBoard>() {
                @Override
                public void onResponse(Call<DashBoard> call, Response<DashBoard> response) {
                    mBinding.swipeToRefresh.setRefreshing(false);
                    if (response.body() != null) {
                        DashBoard dashBoard = response.body();
                        dashBoard.setUserId(getUserId(activity));
                        mViewModel.insert(dashBoard);
                    }
                }

                @Override
                public void onFailure(Call<DashBoard> call, Throwable t) {
                    mBinding.swipeToRefresh.setRefreshing(false);
                    apiCallFailureDialog(t);
                    t.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            mBinding.swipeToRefresh.setRefreshing(false);
        }
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

    private void showAddEducationDialog(Education education) {
        AddEduDialog addEduDialog = new AddEduDialog(getActivity(), education, true, this, 0);
        addEduDialog.show();
    }

    private void getEducationInfoList() {
        Call<FetchEducationResponse> call = apiService.fetchUserEdu(AppUtils.getUserId(), "L", getDeviceId(getActivity()), Constant.APP_NAME);
        call.enqueue(new Callback<FetchEducationResponse>() {
            @Override
            public void onResponse(Call<FetchEducationResponse> call, Response<FetchEducationResponse> response) {
                if (response.body() != null && response.body().getResponseCode().equals("200")) {
                    if (response.body().getEducationList().size() == 0) {
                        androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle);
                        builder.setTitle("Alert")
                                .setMessage("You have Not Added Education Details, To explore this app more you need" +
                                        " to add atleast one education details.")
                                .setPositiveButton("Proceed To Add Education", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        showAddEducationDialog(null);
                                    }
                                })
                                .setCancelable(false).show();
                    } else {
                        //education list is not empty set flag
                        TinyDB.getInstance(getActivity()).putBoolean(Constant.IS_EDUCATION_ADDED, true);
                    }
                } else {
                    showErrorDialog(getActivity(), response.body().getResponseCode(), "");
                }
            }

            @Override
            public void onFailure(Call<FetchEducationResponse> call, Throwable t) {
                showToast("Something went wrong!!");
                //ProgressDialog.getInstance().dismiss();
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        PieEntry pieEntry = (PieEntry) e;
        //setDynamicPieChartData(pieEntry.getLabel());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    @Override
    public void onSuccess() {
        //Successfully Added new Education
        TinyDB.getInstance(getActivity()).putBoolean(Constant.IS_EDUCATION_ADDED, true);
    }

    @Override
    public void onDialogEduDelete(Education education, int pos) {

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