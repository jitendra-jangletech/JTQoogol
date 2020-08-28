package com.jangletech.qoogol.adapter;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.ShareuseritemBinding;
import com.jangletech.qoogol.model.SharedQuestions;
import com.jangletech.qoogol.util.AESSecurities;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.TinyDB;
import com.jangletech.qoogol.util.UtilHelper;

import java.util.List;

import static com.jangletech.qoogol.util.Constant.sharedby;

/**
 * Created by Pritali on 7/23/2020.
 */
public class ShareUserAdapter extends RecyclerView.Adapter<ShareUserAdapter.ViewHolder> implements Filterable {

    private static final String TAG = "ShareAdapter";
    private ShareuseritemBinding shareuseritemBinding;
    private List<SharedQuestions> sharedQuestionsList;
    private Activity activity;
    private onItemClickListener onItemClickListener;
    int call_from;

    @Override
    public Filter getFilter() {
        return null;
    }


    public ShareUserAdapter(Activity activity, List<SharedQuestions> sharedQuestionsList, onItemClickListener onItemClickListener, int call_from) {
        this.activity = activity;
        this.sharedQuestionsList = sharedQuestionsList;
        this.onItemClickListener = onItemClickListener;
        this.call_from = call_from;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        shareuseritemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.shareuseritem, parent, false);
        return new ShareUserAdapter.ViewHolder(shareuseritemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SharedQuestions sharedQuestions = sharedQuestionsList.get(position);
        try {
            Log.d(TAG, "onBindViewHolder Url : " + UtilHelper.getProfileImageUrl(sharedQuestions.getW_user_profile_image_name().trim()));
            if (sharedQuestions.getGroup_name() != null) {
                shareuseritemBinding.tvUserName.setText(sharedQuestions.getGroup_name());
                if (sharedQuestions.getW_user_profile_image_name() != null && !sharedQuestions.getW_user_profile_image_name().isEmpty()) {
                    Glide.with(activity).load(UtilHelper.getProfileImageUrl(sharedQuestions.getW_user_profile_image_name().trim())).circleCrop().placeholder(R.drawable.profile).into(shareuseritemBinding.userProfileImage);
                }
            } else {
                String userName = AESSecurities.getInstance().decrypt(TinyDB.getInstance(activity).getString(Constant.cf_key1), sharedQuestions.getU_first_name()) +
                        " " + AESSecurities.getInstance().decrypt(TinyDB.getInstance(activity).getString(Constant.cf_key2), sharedQuestions.getU_last_name());
                shareuseritemBinding.tvUserName.setText(userName);
                if (sharedQuestions.getW_user_profile_image_name() != null && !sharedQuestions.getW_user_profile_image_name().isEmpty()) {
                    Glide.with(activity).load(UtilHelper.getProfileImageUrl(sharedQuestions.getW_user_profile_image_name().trim())).circleCrop().placeholder(R.drawable.profile).into(shareuseritemBinding.userProfileImage);
                }
            }

            shareuseritemBinding.tvdate.setText(sharedQuestions.getStq_cdatetime());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface onItemClickListener {
        void onItemCLick(String user_id);
    }

    @Override
    public int getItemCount() {
        return sharedQuestionsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull ShareuseritemBinding itemView) {
            super(itemView.getRoot());

            shareuseritemBinding.container.setOnClickListener(v -> {
                SharedQuestions sharedQuestions = sharedQuestionsList.get(getAdapterPosition());
                if (sharedQuestions.getGroup_name() == null) {
                    if (call_from == sharedby)
                        onItemClickListener.onItemCLick(sharedQuestions.getStq_to_user_id());
                    else
                        onItemClickListener.onItemCLick(sharedQuestions.getStq_by_user_id());
                }
            });
        }
    }
}
