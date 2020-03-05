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
import com.jangletech.qoogol.model.Home;

import java.util.List;

/**
 * Created by Pritali on 1/27/2020.
 */
public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {
    Activity activity;
    List<Home> itemlist;
    HomeitemBinding homeitemBinding;

    public HomeAdapter(Activity activity, List<Home> itemlist) {
        this.activity = activity;
        this.itemlist = itemlist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        homeitemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.homeitem, null, false);

        return  new ViewHolder(homeitemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Home home = itemlist.get(position);
        if (home.getCourses()!=null) {
            holder.homeitemBinding.courseCount.setText(home.getCourses());
            holder.homeitemBinding.courseCount.setVisibility(View.VISIBLE);
            holder.homeitemBinding.courses.setVisibility(View.VISIBLE);
        }
        if (home.getQuestions()!=null) {
            holder.homeitemBinding.questionsCount.setText(home.getQuestions());
            holder.homeitemBinding.questionsCount.setVisibility(View.VISIBLE);
            holder.homeitemBinding.questions.setVisibility(View.VISIBLE);
        }
        if (home.getExams()!=null) {
            holder.homeitemBinding.examsCount.setText(home.getExams());
            holder.homeitemBinding.examsCount.setVisibility(View.VISIBLE);
            holder.homeitemBinding.exams.setVisibility(View.VISIBLE);
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
