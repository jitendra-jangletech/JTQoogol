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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.azoft.carousellayoutmanager.CenterScrollListener;
import com.jangletech.qoogol.util.CenterZoomLayoutManager;
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

import static com.facebook.FacebookSdk.getApplicationContext;

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

//        getStatisticsData();
        homeAdapter = new HomeAdapter(getActivity(), itemlist);
//        final CarouselLayoutManager layoutManager = new CarouselLayoutManager(CarouselLayoutManager.HORIZONTAL, true);
//        layoutManager.setPostLayoutListener(new CarouselZoomPostLayoutListener());
//
//        layoutManager.setMaxVisibleItems(1);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        fragmentHomeBinding.itemRecycler.setLayoutManager(new CenterZoomLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));


        //        fragmentHomeBinding.itemRecycler.addItemDecoration(new RecyclerView.ItemDecoration() {
//            @Override
//            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
//                int position = parent.getChildAdapterPosition(view); // item position
//                int spanCount = 2;
//                int spacing = 30;//spacing between views in grid
//
//                if (position >= 0) {
//                    int column = position % spanCount; // item column
//
//                    outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
//                    outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)
//
//                    if (position < spanCount) { // top edge
//                        outRect.top = spacing;
//                    }
//                    outRect.bottom = spacing; // item bottom
//                } else {
//                    outRect.left = 0;
//                    outRect.right = 0;
//                    outRect.top = 0;
//                    outRect.bottom = 0;
//                }
//            }
//        });
//        fragmentHomeBinding.itemRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true));
        fragmentHomeBinding.itemRecycler.setHasFixedSize(true);
        fragmentHomeBinding.itemRecycler.setAdapter(homeAdapter);
        fragmentHomeBinding.itemRecycler.addOnScrollListener(new CenterScrollListener());

        setDummyData();
        return fragmentHomeBinding.getRoot();
    }

    private void setDummyData() {

        ProgressDialog.getInstance().dismiss();
        fragmentHomeBinding.creditPoints.setText("100");
        fragmentHomeBinding.friends.setText("0");
        fragmentHomeBinding.followers.setText("0");
        fragmentHomeBinding.following.setText("0");

        itemlist.clear();

        DashboardData dashboardData1 = new DashboardData();
        dashboardData1.setAnswers("80");
        dashboardData1.setQuestions("90");
        dashboardData1.setTests("90");
        itemlist.add(dashboardData1);

        DashboardData dashboardData2 = new DashboardData();
        dashboardData2.setAttendedTests("80");
        dashboardData2.setCreatedTests("90");
        itemlist.add(dashboardData2);

        DashboardData dashboardData3 = new DashboardData();
        dashboardData3.setAvg_user("80");
        dashboardData3.setFeed_tests("90");
        dashboardData3.setCourse("90");
        dashboardData3.setExam("90");
        itemlist.add(dashboardData3);

        DashboardData dashboardData4 = new DashboardData();
        dashboardData4.setFavQA("99");
        dashboardData4.setFavTests("900");
        itemlist.add(dashboardData4);
        homeAdapter.notifyDataSetChanged();
    }


    private void getStatisticsData() {
        ProgressDialog.getInstance().show(getActivity());
        Map<String, String> arguments = new HashMap<>();
        Log.d(TAG, "fetchEducationDetails userId : " + new PreferenceManager(getContext()).getUserId());
        arguments.put(Constant.user_id, new PreferenceManager(getContext()).getUserId());
        Call<DashboardInfo> call = apiService.getDashboardStatistics(arguments);
        call.enqueue(new Callback<DashboardInfo>() {
            @Override
            public void onResponse(Call<DashboardInfo> call, Response<DashboardInfo> response) {
                if (response.isSuccessful()) {
                    DashboardData dashboardData = (DashboardData) response.body().getObject();
                    setData(dashboardData);

                } else {
                    Log.e(TAG, "onResponse Failed : ");
                    ProgressDialog.getInstance().dismiss();
                }

                Log.d(TAG, "onResponse: " + response.body());
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