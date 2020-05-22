package com.jangletech.qoogol.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.ItemEducationBinding;
import com.jangletech.qoogol.model.Education;
import com.jangletech.qoogol.util.DateUtils;

import java.util.List;

public class EducationAdapter extends RecyclerView.Adapter<EducationAdapter.ViewHolder>  {

    private List<Education> educationList;
    private Context mContext;
    private ItemEducationBinding itemEducationBinding;
    private EducationItemClickListener educationItemClickListener;

    public EducationAdapter(Context mContext,List<Education> educationList,EducationItemClickListener educationItemClickListener){
        this.mContext = mContext;
        this.educationList = educationList;
        this.educationItemClickListener = educationItemClickListener;
    }

    @NonNull
    @Override
    public EducationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemEducationBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.item_education,parent,false);
        return new ViewHolder(itemEducationBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull EducationAdapter.ViewHolder holder, int position) {
        Education education = educationList.get(position);

        holder.itemEducationBinding.tvUniversity.setText(education.getUbm_board_name());
        holder.itemEducationBinding.tvInstitute.setText(education.getIom_name());
        holder.itemEducationBinding.tvDegree.setText(education.getDm_degree_name());
        holder.itemEducationBinding.tvCourse.setText(education.getCo_name());
        holder.itemEducationBinding.tvStartDate.setText(DateUtils.getFormattedDate(education.getUe_startdate()));
        holder.itemEducationBinding.tvEndDate.setText(DateUtils.getFormattedDate(education.getUe_enddate()));

        holder.itemEducationBinding.rootLayout.setOnClickListener(v->{
            educationItemClickListener.onItemClick(education);
        });

        holder.itemEducationBinding.delete.setOnClickListener(v->{
            educationItemClickListener.onDeleteClick(education);
        });
    }

    @Override
    public int getItemCount() {
        return educationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ItemEducationBinding itemEducationBinding;
        public ViewHolder(@NonNull ItemEducationBinding itemView) {
            super(itemView.getRoot());
            this.itemEducationBinding = itemView;
        }
    }

    public interface EducationItemClickListener{
        void onItemClick(Education education);
        void onDeleteClick(Education education);
    }
}
