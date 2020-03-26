package com.jangletech.qoogol.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.HomeAdapter;
import com.jangletech.qoogol.databinding.FragmentHomeBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.model.DashboardData;
import com.jangletech.qoogol.model.DashboardInfo;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    FragmentHomeBinding fragmentHomeBinding;
    private static final String TAG = "HomeFragment";
    List<DashboardData> itemlist = new ArrayList();
    ApiInterface apiService = ApiClient.getInstance().getApi();
    HomeAdapter homeAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        fragmentHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);

        getStatisticsData();
        homeAdapter = new HomeAdapter(getActivity(), itemlist);
        fragmentHomeBinding.itemRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true));
        fragmentHomeBinding.itemRecycler.setHasFixedSize(true);
        fragmentHomeBinding.itemRecycler.setAdapter(homeAdapter);
        //fragmentHomeBinding.itemRecycler.addOnScrollListener(new CenterScrollListener());
        return fragmentHomeBinding.getRoot();
    }

    private void getStatisticsData() {
        ProgressDialog.getInstance().show(getActivity());
        Map<String, String> arguments = new HashMap<>();
        Log.d(TAG, "fetchEducationDetails userId : "+new PreferenceManager(getContext()).getUserId());
        arguments.put(Constant.user_id, new PreferenceManager(getContext()).getUserId());
        Call<DashboardInfo> call = apiService.getDashboardStatistics(arguments);
        call.enqueue(new Callback<DashboardInfo>() {
            @Override
            public void onResponse(Call<DashboardInfo> call, Response<DashboardInfo> response) {
                if(response.isSuccessful()){
                    DashboardData dashboardData = (DashboardData)response.body().getObject();
                    setData(dashboardData);

                }else{
                    Log.e(TAG, "onResponse Failed : ");
                    ProgressDialog.getInstance().dismiss();
                }

                Log.d(TAG, "onResponse: "+response.body());
            }

            @Override
            public void onFailure(Call<DashboardInfo> call, Throwable t) {
                t.printStackTrace();
                ProgressDialog.getInstance().dismiss();
            }
        });
    }

    private void setData(DashboardData dashboardData) {
        ProgressDialog.getInstance().dismiss();
        fragmentHomeBinding.creditPoints.setText(dashboardData.getCreditPoints());
        fragmentHomeBinding.friends.setText(dashboardData.getFriends());
        fragmentHomeBinding.followers.setText(dashboardData.getFollowers());
        fragmentHomeBinding.following.setText(dashboardData.getFollowing());

        itemlist.clear();


        DashboardData dashboardData1 = new DashboardData();
        dashboardData1.setAttendedTests(dashboardData.getAttendedTests());
        dashboardData1.setCreatedTests(dashboardData.getCreatedTests());
        itemlist.add(dashboardData1);

        DashboardData dashboardData2 = new DashboardData();
        dashboardData2.setFavQA(dashboardData.getFavQA());
        dashboardData2.setFavTests(dashboardData.getFavTests());
        itemlist.add(dashboardData2);

        DashboardData dashboardData3 = new DashboardData();
        dashboardData3.setAvgRating(dashboardData.getAvgRating());
        itemlist.add(dashboardData3);



        homeAdapter.notifyDataSetChanged();
    }
}