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
import android.widget.CompoundButton;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.activities.MainActivity;
import com.jangletech.qoogol.databinding.FragmentHomeBinding;
import com.jangletech.qoogol.model.DashBoard;
import com.jangletech.qoogol.model.TestAnalytics;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;

import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends BaseFragment implements View.OnClickListener,
        OnChartValueSelectedListener, SearchView.OnQueryTextListener {

    private HomeViewModel mViewModel;
    private FragmentHomeBinding mBinding;
    private static final String TAG = "HomeFragment";
    ApiInterface apiService = ApiClient.getInstance().getApi();
    ArrayList<TestAnalytics> testAnalytics = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        mBinding.setLifecycleOwner(this);
        initViews();
        testAnalytics.clear();
        testAnalytics.add(new TestAnalytics("Like", 50));
        testAnalytics.add(new TestAnalytics("Share", 30));
        testAnalytics.add(new TestAnalytics("Taken", 15));
        testAnalytics.add(new TestAnalytics("Favourites", 5));
        setBarChartData();
        return mBinding.getRoot();
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

    private void setDynamicPieChartData(String label) {
        mBinding.pieChartDetailed.setVisibility(View.VISIBLE);
        mBinding.tvLabel.setVisibility(View.VISIBLE);
        mBinding.tvLabel.setText(label);
        mBinding.pieChartDetailed.setUsePercentValues(true);
        mBinding.pieChartDetailed.getDescription().setEnabled(false);
        mBinding.pieChartDetailed.setExtraOffsets(5, 10, 5, 5);
        mBinding.pieChartDetailed.setDragDecelerationFrictionCoef(0.95f);

        mBinding.pieChartDetailed.setDrawHoleEnabled(true);
        mBinding.pieChartDetailed.setHoleColor(Color.WHITE);
        mBinding.pieChartDetailed.setTransparentCircleRadius(61f);

        ArrayList<PieEntry> yValues = new ArrayList<>();
        yValues.add(new PieEntry(30, "Maths"));
        yValues.add(new PieEntry(40, "Science"));
        yValues.add(new PieEntry(20, "English"));
        yValues.add(new PieEntry(10, "Marathi"));

        mBinding.pieChartDetailed.animateY(1000, Easing.EaseInOutCubic);

        PieDataSet pieDataSet = new PieDataSet(yValues, label);
        pieDataSet.setSliceSpace(3f);
        pieDataSet.setSelectionShift(5f);
        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueTextSize(10f);
        pieData.setValueTextColor(Color.WHITE);
        mBinding.pieChartDetailed.setData(pieData);
        mBinding.pieChartDetailed.invalidate();
    }


    private void setBarChartData() {
        ArrayList<String> labels = new ArrayList<>();
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        for (int i = 0; i < testAnalytics.size(); i++) {
            String featureName = testAnalytics.get(i).getFeatureName();
            int count = testAnalytics.get(i).getCount();
            barEntries.add(new BarEntry(i, count));
            labels.add(featureName);
        }

        BarDataSet barDataSet = new BarDataSet(barEntries, "Test Analytics");
        barDataSet.setColor(Color.RED);
        Description description = new Description();
        description.setText("Test");

        mBinding.barChart.setDescription(description);
        BarData barData = new BarData(barDataSet);
        XAxis xAxis = mBinding.barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));
        xAxis.setPosition(XAxis.XAxisPosition.TOP);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(labels.size());
        xAxis.setLabelRotationAngle(270);
        mBinding.barChart.setData(barData);
        mBinding.barChart.animateY(1000);
        mBinding.barChart.invalidate();

        /*mBinding.barChart.setBackgroundColor(Color.GRAY);
        mBinding.barChart.setGridBackgroundColor(Color.DKGRAY);
        mBinding.barChart.setData(barData);
        mBinding.barChart.animateXY(3000, 300);
        mBinding.barChart.invalidate();
*/
    }

    private void initViews() {
        mBinding.connectionLayout.setOnClickListener(this);
        mBinding.friendsLayout.setOnClickListener(this);
        mBinding.followersLayout.setOnClickListener(this);
        mBinding.followingLayout.setOnClickListener(this);
        mBinding.connectionLayout.setOnClickListener(this);
        mBinding.pieChart.setOnChartValueSelectedListener(this);
        fetchDashboardDetails();
        mViewModel.getDashboardDetails(getUserId()).observe(getViewLifecycleOwner(), new Observer<DashBoard>() {
            @Override
            public void onChanged(@Nullable final DashBoard dashBoard) {
                if (dashBoard != null) {
                    setDashBoardData(dashBoard);
                }
            }
        });

        mBinding.toggleChart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //showToast("Pie Chart");
                    mBinding.barChart.setVisibility(View.GONE);
                    mBinding.pieChart.setVisibility(View.VISIBLE);
                    setPieChartData();
                } else {
                    //showToast("Bar Chart");
                    mBinding.barChart.setVisibility(View.VISIBLE);
                    mBinding.pieChart.setVisibility(View.GONE);
                    mBinding.pieChartDetailed.setVisibility(View.GONE);
                    setBarChartData();
                }
            }
        });
    }

    private void setPieChartData() {
        //set pie chart
        mBinding.pieChart.setUsePercentValues(true);
        mBinding.pieChart.getDescription().setEnabled(false);
        mBinding.pieChart.setExtraOffsets(5, 10, 5, 5);
        mBinding.pieChart.setDragDecelerationFrictionCoef(0.95f);

        mBinding.pieChart.setDrawHoleEnabled(true);
        mBinding.pieChart.setHoleColor(Color.WHITE);
        mBinding.pieChart.setTransparentCircleRadius(61f);

        ArrayList<PieEntry> yValues = new ArrayList<>();
        yValues.add(new PieEntry(30, "Like"));
        yValues.add(new PieEntry(40, "Share"));
        yValues.add(new PieEntry(30, "Favourite"));

        mBinding.pieChart.animateY(1000, Easing.EaseInOutCubic);

        PieDataSet pieDataSet = new PieDataSet(yValues, "Test");
        pieDataSet.setSliceSpace(3f);
        pieDataSet.setSelectionShift(5f);
        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueTextSize(10f);
        pieData.setValueTextColor(Color.WHITE);

        mBinding.pieChart.setData(pieData);
    }

    private void setDashBoardData(DashBoard dashBoard) {
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
        setDynamicPieChartData(pieEntry.getLabel());
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
}