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

        if (dashboardData.getAnswers()!=null) {
            holder.homeitemBinding.heading.setText("Q & A Log");
            holder.homeitemBinding.courseCount.setText(dashboardData.getAnswers());
            holder.homeitemBinding.courses.setText("Answers");
            holder.homeitemBinding.questionsCount.setText(dashboardData.getQuestions());
            holder.homeitemBinding.questions.setText("Questions");
            holder.homeitemBinding.examsCount.setText(dashboardData.getTests());
            holder.homeitemBinding.exams.setText("Tests");

            holder.homeitemBinding.item4.setVisibility(View.GONE);
            holder.homeitemBinding.item4Count.setVisibility(View.GONE);
        }


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
            holder.homeitemBinding.heading.setText("Favourites");
            holder.homeitemBinding.courseCount.setText(dashboardData.getFavQA());
            holder.homeitemBinding.courses.setText("Favourite Q & A");
            holder.homeitemBinding.questionsCount.setText(dashboardData.getFavTests());
            holder.homeitemBinding.questions.setText("Favourite Tests");

            holder.homeitemBinding.exams.setVisibility(View.GONE);
            holder.homeitemBinding.examsCount.setVisibility(View.GONE);
            holder.homeitemBinding.item4.setVisibility(View.GONE);
            holder.homeitemBinding.item4Count.setVisibility(View.GONE);
        }

        if (dashboardData.getAvg_user()!=null) {
                holder.homeitemBinding.heading.setText("Feed Popularity");
                holder.homeitemBinding.courseCount.setText(dashboardData.getAvg_user());
                holder.homeitemBinding.courses.setText("Average User");
                holder.homeitemBinding.questionsCount.setText(dashboardData.getFeed_tests());
                holder.homeitemBinding.questions.setText("Tests");
                holder.homeitemBinding.examsCount.setText(dashboardData.getCourse());
                holder.homeitemBinding.exams.setText("Course");
                holder.homeitemBinding.item4Count.setText(dashboardData.getExam());
                holder.homeitemBinding.item4.setText("Exam");
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
