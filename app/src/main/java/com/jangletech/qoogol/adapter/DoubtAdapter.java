package com.jangletech.qoogol.adapter;

import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.DoubtItemBinding;
import com.jangletech.qoogol.model.DoubtInfo;
import com.jangletech.qoogol.model.Doubts;
import com.jangletech.qoogol.util.UtilHelper;

import java.util.List;

import static com.jangletech.qoogol.util.Constant.my_doubts;

/**
 * Created by Pritali on 7/31/2020.
 */
public class DoubtAdapter extends RecyclerView.Adapter<DoubtAdapter.ViewHolder> {
    private Activity activity;
    private List<DoubtInfo> doubtsList;
    DoubtItemBinding doubtItemBinding;
    int call_from;

    public DoubtAdapter(Activity activity, List<DoubtInfo> doubtsList, int call_from) {
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
        DoubtInfo doubts = doubtsList.get(position);
        if (doubts.getProfile() != null && !doubts.getProfile().isEmpty()) {
            Glide.with(activity).load(UtilHelper.getProfileImageUrl(doubts.getProfile().trim())).circleCrop().placeholder(R.drawable.profile).into(doubtItemBinding.imgUserProfile);
        }
            doubtItemBinding.tvName.setText(doubts.getName());

        doubtItemBinding.tvTimeStamp.setText(doubts.getDate_time());
        doubtItemBinding.doubtText.setText(doubts.getDoubt_text());
        doubtItemBinding.doubtLink.setText(createDynamicLink(doubts.getDoubt_id()));
    }

    private String createDynamicLink(String crId) {
        final String[] resultLink = {""};
        String link = "https://www.chatchilli.com/?postId=" + crId;
        FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(link))
                .setDomainUriPrefix("https://chatchilli.page.link")
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                .setIosParameters(new DynamicLink.IosParameters.Builder("com.jangletech.chatchilli")
                        .setAppStoreId("1524294565").build())
                .buildShortDynamicLink()
                .addOnCompleteListener(activity, task -> {
                    if (task.isSuccessful()) {
                        // Short link created
                        Uri shortLink = task.getResult().getShortLink();
                        Uri flowchartLink = task.getResult().getPreviewLink();
                        resultLink[0] = shortLink.toString();
                    } else {
                        Toast.makeText(activity, "Something went wrong try again later.", Toast.LENGTH_LONG).show();
                    }
                });
        return resultLink[0];
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
