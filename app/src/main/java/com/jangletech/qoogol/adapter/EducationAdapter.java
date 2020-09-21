package com.jangletech.qoogol.adapter;

import android.content.Context;
import android.util.Log;
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
import com.jangletech.qoogol.databinding.EducationItemHorizontalBinding;
import com.jangletech.qoogol.databinding.ItemEducationBinding;
import com.jangletech.qoogol.enums.Module;
import com.jangletech.qoogol.model.Education;
import com.jangletech.qoogol.util.DateUtils;
import com.jangletech.qoogol.util.TinyDB;

import java.util.List;

public class EducationAdapter extends RecyclerView.Adapter<EducationAdapter.ViewHolder> {

    private static final String TAG = "EducationAdapter";
    private List<Education> educationList;
    private Context mContext;
    private ItemEducationBinding itemEducationBinding;
    private EducationItemHorizontalBinding educationItemHorizontalBinding;
    private EducationItemClickListener educationItemClickListener;
    private String CallingFrom = "";
    private String ueId = "";
    private int lastPosition = -1;
    private int checkedPosition = -1;

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.fall_down);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    public Education getSelectedItem() {
        if (checkedPosition != -1) {
            return educationList.get(checkedPosition);
        }
        return null;
    }

    public EducationAdapter(Context mContext, List<Education> educationList, EducationItemClickListener educationItemClickListener, String callingFrom, String ueId) {
        this.mContext = mContext;
        this.educationList = educationList;
        this.educationItemClickListener = educationItemClickListener;
        this.CallingFrom = callingFrom;
        this.ueId = ueId;
        setHasStableIds(true);
        checkedPosition = setUePosition(educationList, ueId);
    }

    @Override
    public long getItemId(int position) {
        return Long.parseLong(educationList.get(position).getUe_id());
    }

    private int setUePosition(List<Education> educations, String ueId) {
        Log.d(TAG, "setUePosition UeId: " + ueId);
        for (int i = 0; i < educations.size(); i++) {
            if (educations.get(i).getUe_id().equals(ueId)) {
                return i;
            }
        }
        return -1;
    }

    public void updateList(List<Education> educations) {
        educationList = educations;
        checkedPosition = setUePosition(educations, ueId);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EducationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (CallingFrom.equalsIgnoreCase(Module.Syllabus.toString())) {
            educationItemHorizontalBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                    R.layout.education_item_horizontal, parent, false);
            return new ViewHolder(educationItemHorizontalBinding);
        } else {
            itemEducationBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                    R.layout.item_education, parent, false);
            return new ViewHolder(itemEducationBinding);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull EducationAdapter.ViewHolder holder, int position) {
        Education education = educationList.get(position);

        if (CallingFrom.equalsIgnoreCase(Module.Syllabus.toString())) {

            holder.educationItemHorizontalBinding.tvUniversity.setText(education.getUbm_board_name());
            holder.educationItemHorizontalBinding.tvInstitute.setText(education.getIom_name());
            holder.educationItemHorizontalBinding.tvDegree.setText(education.getDm_degree_name());
            holder.educationItemHorizontalBinding.tvCourse.setText(education.getCo_name());
            holder.educationItemHorizontalBinding.tvCourseYear.setText(education.getUe_cy_num());
            holder.educationItemHorizontalBinding.tvStartDate.setText(education.getUe_startdate() != null ? DateUtils.getFormattedDate(education.getUe_startdate()) : "");
            holder.educationItemHorizontalBinding.tvEndDate.setText(education.getUe_enddate() != null ? DateUtils.getFormattedDate(education.getUe_enddate()) : "");

            if (checkedPosition == -1) {
                holder.educationItemHorizontalBinding.borderLayout.setBackgroundResource(R.drawable.normal);
                //holder.educationItemHorizontalBinding.right.setVisibility(View.GONE);
            } else {
                if (checkedPosition == position) {
                    holder.educationItemHorizontalBinding.borderLayout.setBackgroundResource(R.drawable.border);
                    //holder.educationItemHorizontalBinding.right.setVisibility(View.VISIBLE);
                } else {
                    holder.educationItemHorizontalBinding.borderLayout.setBackgroundResource(R.drawable.normal);
                    //holder.educationItemHorizontalBinding.right.setVisibility(View.GONE);
                }
            }

            holder.educationItemHorizontalBinding.rootLayout.setOnClickListener(v -> {
                //holder.educationItemHorizontalBinding.right.setVisibility(View.VISIBLE);
                holder.educationItemHorizontalBinding.borderLayout.setBackgroundResource(R.drawable.border);
                if (checkedPosition != position) {
                    notifyItemChanged(checkedPosition);
                    checkedPosition = position;
                    educationItemClickListener.onItemClick(education, position);
                }
            });

        } else {
            holder.itemEducationBinding.delete.setVisibility(View.VISIBLE);
            holder.itemEducationBinding.right.setVisibility(View.GONE);

            holder.itemEducationBinding.tvUniversity.setText(education.getUbm_board_name());
            holder.itemEducationBinding.tvInstitute.setText(education.getIom_name());
            holder.itemEducationBinding.tvDegree.setText(education.getDm_degree_name());
            holder.itemEducationBinding.tvCourse.setText(education.getCo_name());
            holder.itemEducationBinding.tvCourseYear.setText(education.getUe_cy_num());
            holder.itemEducationBinding.tvStartDate.setText(education.getUe_startdate() != null ? DateUtils.getFormattedDate(education.getUe_startdate()) : "");
            holder.itemEducationBinding.tvEndDate.setText(education.getUe_enddate() != null ? DateUtils.getFormattedDate(education.getUe_enddate()) : "");

            holder.itemEducationBinding.rootLayout.setOnClickListener(v -> {
                if (CallingFrom.equalsIgnoreCase(Module.Syllabus.toString())) {
                    holder.itemEducationBinding.right.setVisibility(View.VISIBLE);
                    if (checkedPosition != position) {
                        notifyItemChanged(checkedPosition);
                        checkedPosition = position;
                        educationItemClickListener.onItemClick(education, position);
                    }
                } else {
                    educationItemClickListener.onItemClick(education, position);
                }

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

            setAnimation(holder.itemEducationBinding.getRoot(), position);

        }


    }


    @Override
    public int getItemCount() {
        return educationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemEducationBinding itemEducationBinding;
        EducationItemHorizontalBinding educationItemHorizontalBinding;

        public ViewHolder(@NonNull ItemEducationBinding itemView) {
            super(itemView.getRoot());
            this.itemEducationBinding = itemView;
        }

        public ViewHolder(@NonNull EducationItemHorizontalBinding itemView) {
            super(itemView.getRoot());
            this.educationItemHorizontalBinding = itemView;
        }
    }

    public interface EducationItemClickListener {
        void onItemClick(Education education, int position);

        void onDeleteClick(Education education, int position);
    }

    public void deleteEducation(int pos) {
        educationList.remove(pos);
        notifyItemRemoved(pos);
        notifyItemRangeChanged(pos, getItemCount());
    }
}
