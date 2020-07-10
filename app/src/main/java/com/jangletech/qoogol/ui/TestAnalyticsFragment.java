package com.jangletech.qoogol.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.FragmentTestAnalyticsBinding;
import com.jangletech.qoogol.model.DashBoard;
import com.jangletech.qoogol.model.TestAnalytics;

import java.util.ArrayList;

public class TestAnalyticsFragment extends BaseFragment {

    private static final String TAG = "TestAnalyticsFragment";
    private FragmentTestAnalyticsBinding mBinding;
    private ArrayList<TestAnalytics> testAnalytics = new ArrayList<>();
    private DashBoard dashBoard;

    public TestAnalyticsFragment(DashBoard dashBoard) {
        this.dashBoard = dashBoard;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_test_analytics, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setTestAnalytics(dashBoard);
        //Log.d(TAG, "Name : " + dashBoard.getFirstName());
        mBinding.toggleChart.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
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
        double total = getTotal(dashBoard);
        mBinding.pieChart.setUsePercentValues(true);
        mBinding.pieChart.getDescription().setEnabled(false);
        mBinding.pieChart.setExtraOffsets(5, 10, 5, 5);
        mBinding.pieChart.setDragDecelerationFrictionCoef(0.95f);

        mBinding.pieChart.setDrawHoleEnabled(true);
        mBinding.pieChart.setHoleColor(Color.WHITE);
        mBinding.pieChart.setTransparentCircleRadius(61f);

        ArrayList<PieEntry> yValues = new ArrayList<>();
        if (dashBoard != null) {
            yValues.add(new PieEntry(calculatePercentage(total, dashBoard.getUp_tests_likes()), "Like"));
            yValues.add(new PieEntry(calculatePercentage(total, dashBoard.getTestShares()), "Share"));
            yValues.add(new PieEntry(calculatePercentage(total, dashBoard.getUp_fav_tests()), "Favourite"));
            yValues.add(new PieEntry(calculatePercentage(total, dashBoard.getTestTaken()), "Test Taken"));
            yValues.add(new PieEntry(calculatePercentage(total, dashBoard.getUp_tests_ratings_given()), "Rating Given"));
        }

        mBinding.pieChart.animateY(1000, Easing.EaseInOutCubic);

        PieDataSet pieDataSet = new PieDataSet(yValues, "Test");
        pieDataSet.setSliceSpace(3f);
        pieDataSet.setSelectionShift(5f);
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueTextSize(10f);
        pieData.setValueTextColor(Color.WHITE);

        mBinding.pieChart.setData(pieData);
    }


    private void setTestAnalytics(DashBoard dashBoard) {
        if (dashBoard != null) {
            testAnalytics.clear();
            testAnalytics.add(new TestAnalytics("Like", dashBoard.getUp_tests_likes()));
            testAnalytics.add(new TestAnalytics("Share", dashBoard.getTestShares()));
            testAnalytics.add(new TestAnalytics("Taken", dashBoard.getTestTaken()));
            testAnalytics.add(new TestAnalytics("Favourites", dashBoard.getUp_fav_tests()));
            setBarChartData();
        }
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
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueTextSize(10f);
        pieData.setValueTextColor(Color.WHITE);
        mBinding.pieChartDetailed.setData(pieData);
        mBinding.pieChartDetailed.invalidate();
    }

    private float calculatePercentage(double total, double value) {
        return (float) ((value / total) * 100);
    }

    private double getTotal(DashBoard board) {
        double sum = 0;
        if (board != null) {
            sum = board.getUp_tests_likes() + board.getUp_fav_tests()
                    + board.getUp_tests_ratings_given()
                    + board.getTestShares() + board.getTestTaken();
        }
        return sum;
    }


    private void setBarChartData() {
        ArrayList<String> labels = new ArrayList<>();
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        for (int i = 0; i < testAnalytics.size(); i++) {
            String featureName = testAnalytics.get(i).getFeatureName();
            int count = (int) testAnalytics.get(i).getCount();
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
}