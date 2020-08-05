package com.jangletech.qoogol.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.DoubtItemBinding;
import com.jangletech.qoogol.model.Doubts;
import com.jangletech.qoogol.util.UtilHelper;

import java.util.List;

import static com.jangletech.qoogol.util.Constant.my_doubts;

/**
 * Created by Pritali on 7/31/2020.
 */
public class DoubtAdapter extends RecyclerView.Adapter<DoubtAdapter.ViewHolder> {
    private Activity activity;
    private List<Doubts> doubtsList;
    DoubtItemBinding doubtItemBinding;
    int call_from;

    public DoubtAdapter(Activity activity, List<Doubts> doubtsList, int call_from) {
        this.activity = activity;
        this.doubtsList = doubtsList;
        this.call_from = call_from;
    }

    @NonNull
    @Override
    public DoubtAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        doubtItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.doubt_item, parent, false);
        return new DoubtAdapter.ViewHolder(doubtItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull DoubtAdapter.ViewHolder holder, int position) {
        Doubts doubts = doubtsList.get(position);
        if (doubts.getProfile() != null && !doubts.getProfile().isEmpty()) {
            Glide.with(activity).load(UtilHelper.getProfileImageUrl(doubts.getProfile().trim())).circleCrop().placeholder(R.drawable.profile).into(doubtItemBinding.imgUserProfile);
        }
        if (call_from==my_doubts)
            doubtItemBinding.tvName.setText(doubts.getNameWithPage());
        else
            doubtItemBinding.tvName.setText(doubts.getName());

        doubtItemBinding.tvTimeStamp.setText(doubts.getPosted_date());
        doubtItemBinding.doubtText.setText(doubts.getDoubt());
        doubtItemBinding.doubtLink.setText(doubts.getDoubt_link());
    }

    @Override
    public int getItemCount() {
        return doubtsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull DoubtItemBinding itemView) {
            super(itemView.getRoot());
        }
    }
}