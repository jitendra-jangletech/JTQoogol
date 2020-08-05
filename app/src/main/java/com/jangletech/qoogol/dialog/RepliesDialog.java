package com.jangletech.qoogol.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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
import com.jangletech.qoogol.model.ProcessQuestion;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.util.AppUtils;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.DateUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RepliesDialog extends Dialog implements CommentAdapter.onCommentItemClickListener {
    private static final String TAG = "RepliesDialog";
    private Context mContext;
    private Activity activity;
    private DialogCommentNewBinding mBinding;
    private Comments comments;
    private boolean isCallFromTest;
    private Call<ProcessQuestion> call;
    private int id;
    private List<Comments> commentList;
    private ApiInterface apiService = ApiClient.getInstance().getApi();

    public RepliesDialog(@NonNull Context mContext, Activity activity, Comments comments, int id, boolean isCallFromTest) {
        super(mContext, android.R.style.Theme_DeviceDefault_Light_DarkActionBar);
        this.mContext = mContext;
        this.activity = activity;
        this.comments = comments;
        this.id = id;
        this.isCallFromTest = isCallFromTest;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.dialog_comment_new, null, false);
        setContentView(mBinding.getRoot());

        commentList = new ArrayList<Comments>();
        CommentAdapter mAdapter = new CommentAdapter(activity, commentList, Module.Test.toString(), String.valueOf(id), this);
        mBinding.commentRecycler.setLayoutManager(new LinearLayoutManager(mContext));
        mBinding.commentRecycler.setAdapter(mAdapter);

        mBinding.tvSenderName.setText(comments.getUserFirstName() + " " + comments.getUserLastName());
        mBinding.textCommentBody.setText(AppUtils.decodedString(comments.getTlc_comment_text()));
        mBinding.textCommentTime.setText(DateUtils.localeDateFormat(comments.getTlc_cdatetime()));
        Glide.with(mContext)
                .load(getProfileImageUrl(comments))
                .apply(RequestOptions.circleCropTransform())
                .into(mBinding.profilePic);

        if (isCallFromTest)
            likeReplyComment("L", comments.getTlc_id(), "");
        else
            likeReplyComment("L", comments.getCommentId(), "");

        mBinding.btnClose.setOnClickListener(v -> {
            dismiss();
        });
    }

    private void likeReplyComment(String strCase,
                                  String commentId, String text) {
        ProgressDialog.getInstance().show(mContext);

        Log.d(TAG, "likeReplyComment isCallFromTest : "+isCallFromTest);
        Log.d(TAG, "likeReplyComment UserId : "+AppUtils.getUserId());
        Log.d(TAG, "likeReplyComment TestQuestId : "+id);
        Log.d(TAG, "likeReplyComment strCase : "+strCase);
        Log.d(TAG, "likeReplyComment strCase : "+strCase);
        Log.d(TAG, "likeReplyComment commentId : "+commentId);
        Log.d(TAG, "likeReplyComment commentId : "+text);

        if (isCallFromTest)
            call = apiService.likeReplyTestComment(
                    AppUtils.getUserId(),
                    id,
                    strCase,
                    "1",
                    commentId,
                    text);
        else
            call = apiService.likeReplyQuestComment(
                    AppUtils.getUserId(),
                    id,
                    strCase,
                    "1",
                    commentId,
                    text);

        call.enqueue(new Callback<ProcessQuestion>() {
            @Override
            public void onResponse(Call<ProcessQuestion> call, Response<ProcessQuestion> response) {
                ProgressDialog.getInstance().dismiss();

                if (response.body() != null &&
                        response.body().getResponse().equals("200")) {

                } else {
                    AppUtils.showToast(mContext, response.body().getResponse());
                }
            }

            @Override
            public void onFailure(Call<ProcessQuestion> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                AppUtils.showToast(mContext, "Something went wrong!!");
                t.printStackTrace();
            }
        });
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
