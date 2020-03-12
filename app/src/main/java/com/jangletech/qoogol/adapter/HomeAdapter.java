package com.jangletech.qoogol.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.HomeitemBinding;
import com.jangletech.qoogol.model.DashboardData;
import com.jangletech.qoogol.model.Home;

import java.util.List;

/**
 * Created by Pritali on 1/27/2020.
 */
public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {
    Activity activity;
    List<DashboardData> itemlist;
    HomeitemBinding homeitemBinding;

    public HomeAdapter(Activity activity, List<DashboardData> itemlist) {
        this.activity = activity;
        this.itemlist = itemlist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        homeitemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.homeitem, parent, false);
        return  new ViewHolder(homeitemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DashboardData dashboardData = itemlist.get(position);
        if (dashboardData.getAttendedTests()!=null) {
            holder.homeitemBinding.heading.setText("Test Log");
            holder.homeitemBinding.courseCount.setText(dashboardData.getAttendedTests());
            holder.homeitemBinding.courses.setText("Attended Tests");
            holder.homeitemBinding.questionsCount.setText(dashboardData.getCreatedTests());
            holder.homeitemBinding.questions.setText("Self Tests");

            holder.homeitemBinding.exams.setVisibility(View.GONE);
            holder.homeitemBinding.examsCount.setVisibility(View.GONE);
            holder.homeitemBinding.item4.setVisibility(View.GONE);
            holder.homeitemBinding.item4Count.setVisibility(View.GONE);
        }

        if (dashboardData.getFavQA()!=null) {
            holder.homeitemBinding.heading.setText("Faviourites");
            holder.homeitemBinding.courseCount.setText(dashboardData.getFavQA());
            holder.homeitemBinding.courses.setText("Faviourite Q & A");
            holder.homeitemBinding.questionsCount.setText(dashboardData.getFavTests());
            holder.homeitemBinding.questions.setText("Faviourite Tests");

            holder.homeitemBinding.exams.setVisibility(View.GONE);
            holder.homeitemBinding.examsCount.setVisibility(View.GONE);
            holder.homeitemBinding.item4.setVisibility(View.GONE);
            holder.homeitemBinding.item4Count.setVisibility(View.GONE);
        }

        if (dashboardData.getAvgRating()!=null) {
            holder.homeitemBinding.heading.setText("Avg Ratings");
            holder.homeitemBinding.courseCount.setText(dashboardData.getAvgRating());

            holder.homeitemBinding.questions.setVisibility(View.GONE);
            holder.homeitemBinding.questionsCount.setVisibility(View.GONE);
            holder.homeitemBinding.exams.setVisibility(View.GONE);
            holder.homeitemBinding.examsCount.setVisibility(View.GONE);
            holder.homeitemBinding.item4.setVisibility(View.GONE);
            holder.homeitemBinding.item4Count.setVisibility(View.GONE);
        }

        if (position == 0) {
            holder.homeitemBinding.container.setCardBackgroundColor(activity.getResources().getColor(R.color.colorBlue));
        } else if (position == 1) {
            holder.homeitemBinding.container.setCardBackgroundColor(activity.getResources().getColor(R.color.colorOrangePeel));
        } else if (position == 2) {
            holder.homeitemBinding.container.setCardBackgroundColor(activity.getResources().getColor(R.color.colorNavyBlue));
        }
    }

    @Override
    public int getItemCount() {
        return itemlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        HomeitemBinding homeitemBinding;
        public ViewHolder(@NonNull HomeitemBinding itemView) {
            super(itemView.getRoot());
            this.homeitemBinding = itemView;
        }
    }
}
