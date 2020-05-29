package com.jangletech.qoogol.adapter;

/*
 *
 *
 *  * /////////////////////////////////////////////////////////////////////////////////////////////////
 *  * //
 *  * //            Copyright (c) 2020. JangleTech Systems Private Limited, Thane, India
 *  * //
 *  * /////////////////////////////////////////////////////////////////////////////////////////////////
 *
 */

import android.app.Activity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.model.FAQModel;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.UtilHelper;

import java.util.List;

public class FAQListAdapter extends RecyclerView.Adapter<FAQListAdapter.FaqVH> {

    private List<FAQModel> faqModelList;
    private OnItemClickListener listener;
    private RequestManager requestManager;

    public FAQListAdapter(List<FAQModel> faqModelList, Activity activity, OnItemClickListener listener) {
        this.faqModelList = faqModelList;
        this.listener = listener;
        requestManager =  Glide.with(activity);
    }

    @NonNull
    @Override
    public FaqVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.faq_list_item, parent, false);
        return new FaqVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FaqVH holder, int position) {
        FAQModel faqModel = faqModelList.get(position);
        holder.tvQuestion.setText(Html.fromHtml(faqModel.getQuestion()));
        holder.tvQuestion.setMovementMethod(LinkMovementMethod.getInstance());
        holder.tvAnswer.setText(Html.fromHtml(faqModel.getAnswer()));
        holder.tvAnswer.setMovementMethod(LinkMovementMethod.getInstance());
        if (faqModel.getUrl() != null && faqModel.getFromType() != null) {
            holder.imageItem.setVisibility(View.VISIBLE);
            String path = Constant.PRODUCTION_BASE_FILE_API + faqModel.getUrl().trim();
            glideLoadImage(holder.imageItem, path);
            holder.imageItem.setOnClickListener(view-> listener.onViewImage(path));
        } else {
            holder.imageItem.setVisibility(View.GONE);
        }
        boolean isExpanded = faqModelList.get(position).isExpanded();
        holder.expandableLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
    }

    private void glideLoadImage(ImageView imgProfile, String profilePath) {
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.drawable.load)
                .error(R.drawable.ic_profile_default);
        requestManager.load(profilePath).apply(options).into(imgProfile);
    }

    @Override
    public int getItemCount() {
        return faqModelList.size();
    }

    public interface OnItemClickListener {
        void onViewImage(String path);
    }

    class FaqVH extends RecyclerView.ViewHolder {

        ConstraintLayout expandableLayout;
        TextView tvQuestion, tvAnswer;
        ImageView imageItem;

        FaqVH(@NonNull final View itemView) {
            super(itemView);

            tvQuestion = itemView.findViewById(R.id.titleTextView);
            tvAnswer = itemView.findViewById(R.id.tvAnswer);
            imageItem = itemView.findViewById(R.id.imgItem);
            expandableLayout = itemView.findViewById(R.id.expandableLayout);

            tvQuestion.setOnClickListener(view -> {
                FAQModel movie = faqModelList.get(getAdapterPosition());
                movie.setExpanded(!movie.isExpanded());
                notifyItemChanged(getAdapterPosition());
            });

            tvAnswer.setOnClickListener(view -> {
                FAQModel movie = faqModelList.get(getAdapterPosition());
                movie.setExpanded(!movie.isExpanded());
                notifyItemChanged(getAdapterPosition());
            });
        }
    }
}
