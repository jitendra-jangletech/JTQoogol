package com.jangletech.qoogol.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.ItemEducationBinding;
import com.jangletech.qoogol.model.FetchEducationsObject;

import java.util.List;

public class EducationAdapter extends RecyclerView.Adapter<EducationAdapter.ViewHolder>  {

    Activity activity;
    List<FetchEducationsObject> educationList;
    ItemEducationBinding itemEducationBinding;

    public EducationAdapter(Activity activity, List<FetchEducationsObject> itemlist) {
        this.activity = activity;
        this.educationList = itemlist;
    }

    @NonNull
    @Override
    public EducationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemEducationBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_education, null, false);

        return  new EducationAdapter.ViewHolder(itemEducationBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull EducationAdapter.ViewHolder holder, int position) {
        FetchEducationsObject fetchEducationsObject = educationList.get(position);
        holder.itemEducationBinding.board.setText(fetchEducationsObject.getUnivBrdName());
        holder.itemEducationBinding.institute.setText(fetchEducationsObject.getInstOrgName());
        holder.itemEducationBinding.startDate.setText(fetchEducationsObject.getStartDateStr());
        holder.itemEducationBinding.endDate.setText(fetchEducationsObject.getEndDateStr());

        holder.itemEducationBinding.country.setText(fetchEducationsObject.getCountryName());
        holder.itemEducationBinding.state.setText(fetchEducationsObject.getStateName());
        holder.itemEducationBinding.city.setText(fetchEducationsObject.getCityName());
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
}
