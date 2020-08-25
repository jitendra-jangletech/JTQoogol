package com.jangletech.qoogol.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

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
import com.jangletech.qoogol.util.AESSecurities;
import com.jangletech.qoogol.util.AppUtils;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.DateUtils;
import com.jangletech.qoogol.util.TinyDB;

import java.util.ArrayList;
import java.util.HashMap;
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
    private HashMap<String, String> params = new HashMap<>();
    private CommentAdapter mAdapter;
    private String commentId = "";
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

        if (isCallFromTest) {
            likeReplyComment("L", comments.getTlc_id(), "");
            mAdapter = new CommentAdapter(activity, commentList, Module.Test.toString(), String.valueOf(id), true, this);
            mBinding.textCommentBody.setText(AppUtils.decodedString(comments.getTlc_comment_text()));
            mBinding.textCommentTime.setText(DateUtils.localeDateFormat(comments.getTlc_cdatetime()));
        } else {
            likeReplyComment("L", comments.getCommentId(), "");
            mAdapter = new CommentAdapter(activity, commentList, Module.Learning.toString(), String.valueOf(id), true, this);
            mBinding.textCommentBody.setText(AppUtils.decodedString(comments.getComment()));
            mBinding.textCommentTime.setText(DateUtils.localeDateFormat(comments.getTime()));
        }
        mBinding.commentRecycler.setLayoutManager(new LinearLayoutManager(mContext));
        mBinding.commentRecycler.setAdapter(mAdapter);

//        mBinding.tvSenderName.setText(
//                AESSecurities.getInstance().decrypt(TinyDB.getInstance(activity).getString(Constant.cf_key1),comments.getUserFirstName() + " "
//                        + AESSecurities.getInstance().decrypt(TinyDB.getInstance(activity).getString(Constant.cf_key2),comments.getUserLastName())));

        mBinding.tvSenderName.setText(comments.getUserFirstName() + " " + comments.getUserLastName());

        Glide.with(mContext)
                .load(getProfileImageUrl(comments))
                .apply(RequestOptions.circleCropTransform())
                .into(mBinding.profilePic);

        mBinding.btnClose.setOnClickListener(v -> {
            dismiss();
        });

        mBinding.btnSend.setOnClickListener(v -> {
            if (mBinding.etComment.getText().toString().isEmpty()) {
                Toast.makeText(mContext, "Please enter comment", Toast.LENGTH_SHORT).show();
                return;
            }
            Log.d(TAG, "Replied To Id : " + comments.getCommentId());
            likeReplyComment("I", comments.getTlc_id(), AppUtils.encodedString(mBinding.etComment.getText().toString().trim()));
        });
    }


    private String getProfileImageUrl(Comments comments) {
        return Constant.PRODUCTION_BASE_FILE_API + comments.getProfile_image();
    }

    @Override
    public void onItemClick(String userId) {

    }

    @Override
    public void onCommentDelete(int pos, Comments comments) {
        if (isCallFromTest)
            deleteComment(comments.getTlc_id(), pos);
        else
            deleteComment(comments.getCommentId(), pos);
    }

    @Override
    public void onCommentsClick(int pos, Comments comments) {

    }

    @Override
    public void onLikeClick(int pos, Comments comments) {

    }

    @Override
    public void onReplyClick(int pos, Comments comments) {

    }

    private void deleteComment(String commentId, int pos) {
        ProgressDialog.getInstance().show(mContext);

        if (isCallFromTest)
            call = apiService.deleteTestComment(
                    AppUtils.getUserId(),
                    id,
                    "D",
                    "1",
                    commentId
            );
        else
            call = apiService.deleteTestComment(
                    AppUtils.getUserId(),
                    id,
                    "D",
                    "1",
                    commentId
            );

        call.enqueue(new Callback<ProcessQuestion>() {
            @Override
            public void onResponse(Call<ProcessQuestion> call, Response<ProcessQuestion> response) {
                ProgressDialog.getInstance().dismiss();

                if (response.body() != null &&
                        response.body().getResponse().equals("200")) {
                    //remove deleted item from recyclerview
                    mAdapter.deleteComment(pos);
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


    private void setCommentAdapter() {
        mBinding.etComment.setText("");
        mBinding.btnSend.setClickable(true);
        if (commentList.size() > 0) {
            mBinding.tvNoReplies.setVisibility(View.GONE);
            mAdapter.updateList(commentList);
        } else {
            //no replies added
            mBinding.tvNoReplies.setText("No Replies Added Yet.");
            mBinding.tvNoReplies.setVisibility(View.VISIBLE);
        }
    }

    private void likeReplyComment(String strCase,
                                  String commentId, String text) {
        mBinding.btnSend.setClickable(false);
        //ProgressDialog.getInstance().show(mContext);
        mBinding.tvNoReplies.setText("Fetching Replies...");
        mBinding.tvNoReplies.setVisibility(View.VISIBLE);

        Log.d(TAG, "likeReplyComment u_user_id : " + AppUtils.getUserId());
        Log.d(TAG, "likeReplyComment tm_id : " + id);
        Log.d(TAG, "likeReplyComment strCase : " + strCase);
        Log.d(TAG, "likeReplyComment commentId : " + commentId);
        Log.d(TAG, "likeReplyComment text : " + text);

        if (isCallFromTest)
            call = apiService.replyTestComment(
                    AppUtils.getUserId(),
                    id,
                    strCase,
                    "1",
                    commentId,
                    text);
        else
            call = apiService.replyQuestComment(
                    AppUtils.getUserId(),
                    id,
                    strCase,
                    "1",
                    commentId,
                    text);

        call.enqueue(new Callback<ProcessQuestion>() {
            @Override
            public void onResponse(Call<ProcessQuestion> call, Response<ProcessQuestion> response) {
                //ProgressDialog.getInstance().dismiss();
                mBinding.tvNoReplies.setVisibility(View.GONE);
                if (response.body() != null &&
                        response.body().getResponse().equals("200")) {
                    List<Comments> newCommentList = response.body().getCommentList();
                    for (Comments comments : newCommentList) {
                        comments.setUserFirstName(AESSecurities.getInstance().decrypt(TinyDB.getInstance(activity).getString(Constant.cf_key1), comments.getUserFirstName()));
                        comments.setUserLastName(AESSecurities.getInstance().decrypt(TinyDB.getInstance(activity).getString(Constant.cf_key2), comments.getUserLastName()));
                        commentList.add(comments);
                    }

                    if (strCase.equalsIgnoreCase("L")) {
                        setCommentAdapter();
                    } else {
                        if (isCallFromTest)
                            likeReplyComment("L", comments.getTlc_id(), "");
                        else
                            likeReplyComment("L", comments.getCommentId(), "");
                    }

                } else {
                    mBinding.btnSend.setClickable(true);
                    AppUtils.showToast(mContext, response.body().getResponse());
                }
            }

            @Override
            public void onFailure(Call<ProcessQuestion> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                mBinding.etComment.setText("");
                mBinding.btnSend.setClickable(true);
                mBinding.tvNoReplies.setVisibility(View.GONE);
                AppUtils.showToast(mContext, "Something went wrong!!");
                t.printStackTrace();
            }
        });
    }
}
