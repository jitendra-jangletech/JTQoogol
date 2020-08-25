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
import com.jangletech.qoogol.util.AESSecurities;
import com.jangletech.qoogol.util.AppUtils;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.TinyDB;
import com.jangletech.qoogol.util.UtilHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentDialog extends Dialog implements CommentAdapter.onCommentItemClickListener {

    private static final String TAG = "CommentDialog";
    private CommentDialogBinding mBinding;
    private Activity mContext;
    private List<Comments> commentList;
    private CommentAdapter commentAdapter;
    private CommentViewModel mViewModel;
    private int id;
    private int itemPosition;
    private Call<ProcessQuestion> call;
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
        mBinding.emptytv.setText("Fetching Comments...");
        mBinding.emptytv.setVisibility(View.VISIBLE);
        Log.d(TAG, "fetchCommentsAPI userId : " + user_id);
        Log.d(TAG, "fetchCommentsAPI Case : " + api_case);

        //String encoded = Base64.encodeToString(comment_text.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
        //String encodedComment = StringUtils.stripAccents(encoded);

        try {
            if (isCallFromTest) {
                if (api_case.equalsIgnoreCase("L"))
                    call = apiService.fetchTestComments(user_id, que_id, api_case, 1);
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
                        mBinding.emptytv.setVisibility(View.GONE);
                        commentList.clear();
                        if (response.body() != null && response.body().getResponse().equalsIgnoreCase("200")) {
                            List<Comments> newCommentList = response.body().getCommentList();
                            for (Comments comments : newCommentList){
                                comments.setUserFirstName(AESSecurities.getInstance().decrypt(TinyDB.getInstance(mContext).getString(Constant.cf_key1), comments.getUserFirstName()));
                                comments.setUserLastName(AESSecurities.getInstance().decrypt(TinyDB.getInstance(mContext).getString(Constant.cf_key2), comments.getUserLastName()));
                                commentList.add(comments);
                            }
                            emptyView();
                        } else {
                            Toast.makeText(mContext, UtilHelper.getAPIError(String.valueOf(response.body())), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        mBinding.emptytv.setVisibility(View.GONE);
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
            mBinding.shimmerViewContainer.hideShimmer();
            ProgressDialog.getInstance().dismiss();
        }
    }

    private void setCommentAdapter() {
        if (isCallFromTest)
            commentAdapter = new CommentAdapter(mContext, commentList, Module.Test.toString(), "", false, this);
        else
            commentAdapter = new CommentAdapter(mContext, commentList, Module.Learning.toString(), "", false, this);

        mBinding.commentRecycler.setHasFixedSize(true);
        mBinding.commentRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.commentRecycler.setAdapter(commentAdapter);
        mBinding.shimmerViewContainer.hideShimmer();
    }

    private void emptyView() {
        setCommentAdapter();
        mBinding.etComment.setText("");
        if (commentList.size() == 0) {
            mBinding.emptytv.setText("No Comments added yet.");
            mBinding.emptytv.setVisibility(View.VISIBLE);
        } else {
            mBinding.emptytv.setVisibility(View.GONE);
        }
    }

    public interface CommentClickListener {
        void onCommentClick(String userId);
    }

    @Override
    public void onItemClick(String userId) {
        commentClickListener.onCommentClick(userId);
    }

    @Override
    public void onCommentDelete(int pos, Comments comments) {
        //AppUtils.showToast(mContext, "Delete " + pos);
        if (isCallFromTest)
            deleteComment(comments.getTlc_id(), pos);
        else
            deleteComment(comments.getCommentId(), pos);
    }

    @Override
    public void onCommentsClick(int pos, Comments comments) {
        itemPosition = pos;
        new RepliesDialog(mContext, mContext, comments, id, isCallFromTest).show();
    }

    @Override
    public void onLikeClick(int pos, Comments comments) {
        itemPosition = pos;
        if (isCallFromTest)
            likeReplyComment("I", comments.getTlc_id(), "");
        else
            likeReplyComment("I", comments.getCommentId(), "");

    }

    @Override
    public void onReplyClick(int pos, Comments comments) {
        //AppUtils.showToast(mContext, "Reply");
        itemPosition = pos;
        new RepliesDialog(mContext, mContext, comments, id, isCallFromTest).show();
        //likeReplyComment("I", comments.getCommentId(), "");
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
                    commentAdapter.deleteComment(pos);
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

    private void likeReplyComment(String strCase,
                                  String commentId, String text) {
        ProgressDialog.getInstance().show(mContext);

        Log.d(TAG, "likeReplyComment isCallFromTest : " + isCallFromTest);
        Log.d(TAG, "likeReplyComment UserId : " + AppUtils.getUserId());
        Log.d(TAG, "likeReplyComment TestQuestId : " + id);
        Log.d(TAG, "likeReplyComment strCase : " + strCase);
        Log.d(TAG, "likeReplyComment strCase : " + strCase);
        Log.d(TAG, "likeReplyComment commentId : " + commentId);
        Log.d(TAG, "likeReplyComment commentId : " + text);

        if (isCallFromTest)
            call = apiService.likeTestComment(
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
                    updateCommentList(commentList);
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

    private void updateCommentList(List<Comments> list) {
        Comments comments = list.get(itemPosition);
        int preLikeCount = comments.getReplyLikeCount();
        if (comments.isLiked()) {
            comments.setLiked(false);
            comments.setReplyLikeCount(preLikeCount - 1);
        } else {
            comments.setLiked(true);
            comments.setReplyLikeCount(preLikeCount + 1);
        }
        commentAdapter.notifyItemChanged(itemPosition, comments);
    }
}
