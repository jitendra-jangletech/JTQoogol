package com.jangletech.qoogol.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.CommentAdapter;
import com.jangletech.qoogol.databinding.CommentDialogBinding;
import com.jangletech.qoogol.enums.Module;
import com.jangletech.qoogol.model.Comments;
import com.jangletech.qoogol.model.ProcessQuestion;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.ui.learning.CommentViewModel;
import com.jangletech.qoogol.util.AppUtils;
import com.jangletech.qoogol.util.UtilHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class CommentDialog extends Dialog implements CommentAdapter.onCommentItemClickListener {

    private static final String TAG = "CommentDialog";
    private CommentDialogBinding mBinding;
    private Activity mContext;
    private List<Comments> commentList;
    private CommentAdapter commentAdapter;
    private CommentViewModel mViewModel;
    private int id;
    private boolean isCallFromTest;
    private CommentClickListener commentClickListener;
    private ApiInterface apiService = ApiClient.getInstance().getApi();

    public CommentDialog(@NonNull Activity mContext, int id, boolean isCallFromTest, CommentClickListener commentClickListener) {
        super(mContext, android.R.style.Theme_DeviceDefault_Light_DarkActionBar);
        this.mContext = mContext;
        this.id = id;
        this.isCallFromTest = isCallFromTest;
        this.commentClickListener = commentClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.comment_dialog, null, false);
        setContentView(mBinding.getRoot());
        commentList = new ArrayList<>();
        setCommentAdapter();
        fetchCommentsAPI(Integer.parseInt(AppUtils.getUserId()),
                id, "L", "");

        mBinding.btnClose.setOnClickListener(v -> {
            dismiss();
        });

        mBinding.btnSend.setOnClickListener(v -> {
            if (mBinding.etComment.getText().toString().isEmpty()) {
                Toast.makeText(mContext, "Please enter comment", Toast.LENGTH_SHORT).show();
                return;
            }
            fetchCommentsAPI(Integer.parseInt(AppUtils.getUserId()),
                    id, "I", mBinding.etComment.getText().toString());
        });
    }

    private void fetchCommentsAPI(int user_id, int que_id, String api_case, String comment_text) {
        ProgressDialog.getInstance().show(mContext);
        Call<ProcessQuestion> call;

        Log.d(TAG, "fetchCommentsAPI userId : " + user_id);
        Log.d(TAG, "fetchCommentsAPI Case : " + api_case);

        //String encoded = Base64.encodeToString(comment_text.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
        //String encodedComment = StringUtils.stripAccents(encoded);

        try {
            if (isCallFromTest) {
                if (api_case.equalsIgnoreCase("L"))
                    call = apiService.fetchTestComments(user_id, que_id, api_case);
                else
                    call = apiService.addTestCommentApi(user_id, que_id, "I", AppUtils.encodedString(comment_text));
            } else {
                if (api_case.equalsIgnoreCase("L"))
                    call = apiService.fetchComments(user_id, que_id, api_case, 1);
                else
                    call = apiService.addCommentApi(String.valueOf(user_id), que_id, "I", AppUtils.encodedString(comment_text));
            }

            call.enqueue(new Callback<ProcessQuestion>() {
                @Override
                public void onResponse(Call<ProcessQuestion> call, retrofit2.Response<ProcessQuestion> response) {
                    try {
                        ProgressDialog.getInstance().dismiss();
                        commentList.clear();
                        if (response.body() != null && response.body().getResponse().equalsIgnoreCase("200")) {
                            commentList = response.body().getCommentList();
                            Log.d(TAG, "onResponse commentList : " + commentList.size());
                            emptyView();
                        } else {
                            Toast.makeText(mContext, UtilHelper.getAPIError(String.valueOf(response.body())), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        mBinding.shimmerViewContainer.hideShimmer();
                        ProgressDialog.getInstance().dismiss();
                    }
                }

                @Override
                public void onFailure(Call<ProcessQuestion> call, Throwable t) {
                    t.printStackTrace();
                    mBinding.shimmerViewContainer.hideShimmer();
                    ProgressDialog.getInstance().dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            ProgressDialog.getInstance().dismiss();
        }
    }

    private void setCommentAdapter() {
        if (isCallFromTest)
            commentAdapter = new CommentAdapter(mContext, commentList, Module.Test.toString(), this);
        else
            commentAdapter = new CommentAdapter(mContext, commentList, Module.Learning.toString(), this);

        mBinding.commentRecycler.setHasFixedSize(true);
        mBinding.commentRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.commentRecycler.setAdapter(commentAdapter);
        mBinding.shimmerViewContainer.hideShimmer();
    }

    private void emptyView() {
        setCommentAdapter();
        mBinding.etComment.setText("");
        if (commentList.size() == 0)
            mBinding.emptytv.setVisibility(View.VISIBLE);
        else
            mBinding.emptytv.setVisibility(View.GONE);
    }

    public interface CommentClickListener {
        void onCommentClick(String userId);
    }

    @Override
    public void onItemClick(String userId) {
        commentClickListener.onCommentClick(userId);
    }

    @Override
    public void onLikeClick(int pos, Comments comments) {
        AppUtils.showToast(mContext, "Like");
        comments.setLikeCount(1);
        commentAdapter.updateLikeCount(pos,comments);
    }

    @Override
    public void onReplyClick(int pos, Comments comments) {
        //AppUtils.showToast(mContext, "Reply");
        new RepliesDialog(mContext,mContext,comments)
                .show();
    }
}
