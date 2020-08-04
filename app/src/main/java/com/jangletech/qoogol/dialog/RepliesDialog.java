package com.jangletech.qoogol.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.CommentAdapter;
import com.jangletech.qoogol.databinding.DialogCommentNewBinding;
import com.jangletech.qoogol.enums.Module;
import com.jangletech.qoogol.model.Comments;
import com.jangletech.qoogol.util.AppUtils;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class RepliesDialog extends Dialog implements CommentAdapter.onCommentItemClickListener {
    private Context mContext;
    private Activity activity;
    private DialogCommentNewBinding mBinding;
    private Comments comments;

    public RepliesDialog(@NonNull Context mContext, Activity activity, Comments comments) {
        super(mContext, android.R.style.Theme_DeviceDefault_Light_DarkActionBar);
        this.mContext = mContext;
        this.activity = activity;
        this.comments = comments;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.dialog_comment_new, null, false);
        setContentView(mBinding.getRoot());
        setCommentList();
        mBinding.tvSenderName.setText(comments.getUserFirstName() + " " + comments.getUserLastName());
        mBinding.textCommentBody.setText(AppUtils.decodedString(comments.getTlc_comment_text()));
        mBinding.textCommentTime.setText(DateUtils.localeDateFormat(comments.getTlc_cdatetime()));
        Glide.with(mContext)
                .load(getProfileImageUrl(comments))
                .apply(RequestOptions.circleCropTransform())
                .into(mBinding.profilePic);

        mBinding.btnClose.setOnClickListener(v -> {
            dismiss();
        });
    }

    private void setCommentList() {
        List<Comments> commentList = new ArrayList<Comments>();
        commentList.add(new Comments("Pritali K", "Nice"));
        commentList.add(new Comments("Kiran Kumar", "Good"));
        commentList.add(new Comments("Nakul Sharma", "Excellent"));
        CommentAdapter mAdapter = new CommentAdapter(activity, commentList, Module.Test.toString(), this);
        mBinding.commentRecycler.setLayoutManager(new LinearLayoutManager(mContext));
        mBinding.commentRecycler.setAdapter(mAdapter);
    }

    private String getProfileImageUrl(Comments comments) {
        return Constant.PRODUCTION_BASE_FILE_API + comments.getProfile_image();
    }

    @Override
    public void onItemClick(String userId) {

    }

    @Override
    public void onLikeClick(int pos, Comments comments) {

    }

    @Override
    public void onReplyClick(int pos, Comments comments) {

    }
}
