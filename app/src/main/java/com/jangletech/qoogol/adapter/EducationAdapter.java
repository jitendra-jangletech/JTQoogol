package com.jangletech.qoogol.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.ItemEducationBinding;
import com.jangletech.qoogol.model.Education;
import com.jangletech.qoogol.util.DateUtils;

import java.util.List;

import static com.jangletech.qoogol.util.Constant.fetch_loged_in_user;

public class EducationAdapter extends RecyclerView.Adapter<EducationAdapter.ViewHolder> {

    private List<Education> educationList;
    private Context mContext;
    private ItemEducationBinding itemEducationBinding;
    private EducationItemClickListener educationItemClickListener;
    private int call_from;
    private int lastPosition = -1;

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.fall_down);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    public EducationAdapter(Context mContext, List<Education> educationList, EducationItemClickListener educationItemClickListener, int call_from) {
        this.mContext = mContext;
        this.educationList = educationList;
        this.educationItemClickListener = educationItemClickListener;
        this.call_from = call_from;
    }

    public void updateList(List<Education> educationList){
        this.educationList = educationList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EducationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemEducationBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_education, parent, false);
        return new ViewHolder(itemEducationBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull EducationAdapter.ViewHolder holder, int position) {
        Education education = educationList.get(position);

        holder.itemEducationBinding.tvUniversity.setText(education.getUbm_board_name());
        holder.itemEducationBinding.tvInstitute.setText(education.getIom_name());
        holder.itemEducationBinding.tvDegree.setText(education.getDm_degree_name());
        holder.itemEducationBinding.tvCourse.setText(education.getCo_name());
        holder.itemEducationBinding.tvCourseYear.setText(education.getUe_cy_num());
        holder.itemEducationBinding.tvStartDate.setText(education.getUe_startdate() != null ? DateUtils.getFormattedDate(education.getUe_startdate()) : "");
        holder.itemEducationBinding.tvEndDate.setText(education.getUe_enddate() != null ? DateUtils.getFormattedDate(education.getUe_enddate()) : "");

        if (call_from == fetch_loged_in_user) {
            holder.itemEducationBinding.rootLayout.setOnClickListener(v -> {
                educationItemClickListener.onItemClick(education);
            });
            holder.itemEducationBinding.delete.setOnClickListener(v -> {
                if (educationList.size() == 1) {
                    androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialogStyle);
                    builder.setTitle("Alert")
                            .setMessage("You can't delete this item, Atleast one education is mandatory.")
                            .setPositiveButton("Ok", null)
                            .show();
                } else {
                    educationItemClickListener.onDeleteClick(education, position);
                }
            });
        } else {
            itemEducationBinding.delete.setVisibility(View.GONE);
        }

        setAnimation(holder.itemEducationBinding.getRoot(), position);
    }

    @Override
    public int getItemCount() {
        return educationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemEducationBinding itemEducationBinding;

        public ViewHolder(@NonNull ItemEducationBinding itemView) {
            super(itemView.getRoot());
            this.itemEducationBinding = itemView;
        }
    }

    public interface EducationItemClickListener {
        void onItemClick(Education education);

        void onDeleteClick(Education education, int position);
    }

    public void deleteEducation(int pos) {
        educationList.remove(pos);
        notifyItemRemoved(pos);
    }
}
