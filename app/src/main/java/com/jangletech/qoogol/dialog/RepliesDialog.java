package com.jangletech.qoogol.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
    private CommentAdapter mAdapter;
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
        if (isCallFromTest)
            mAdapter = new CommentAdapter(activity, commentList, Module.Test.toString(), String.valueOf(id), true, this);
        else
            mAdapter = new CommentAdapter(activity, commentList, Module.Learning.toString(), String.valueOf(id), true, this);

        mBinding.commentRecycler.setLayoutManager(new LinearLayoutManager(mContext));
        mBinding.commentRecycler.setAdapter(mAdapter);

        mBinding.tvSenderName.setText(comments.getUserFirstName() + " " + comments.getUserLastName());

        if (isCallFromTest) {
            mBinding.textCommentBody.setText(AppUtils.decodedString(comments.getTlc_comment_text()));
            mBinding.textCommentTime.setText(DateUtils.localeDateFormat(comments.getTlc_cdatetime()));
        } else {
            mBinding.textCommentBody.setText(AppUtils.decodedString(comments.getComment()));
            mBinding.textCommentTime.setText(DateUtils.localeDateFormat(comments.getTime()));
        }


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

        mBinding.btnSend.setOnClickListener(v -> {
            if (mBinding.etComment.getText().toString().isEmpty()) {
                Toast.makeText(mContext, "Please enter comment", Toast.LENGTH_SHORT).show();
                return;
            }
            /*fetchCommentsAPI(Integer.parseInt(AppUtils.getUserId()),
                    id, "I", mBinding.etComment.getText().toString());*/
            Log.d(TAG, "Replied To Id : "+comments.getCommentId());
            likeReplyComment("I", comments.getCommentId(), AppUtils.encodedString(mBinding.etComment.getText().toString().trim()));
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

//    private void fetchCommentsAPI(int user_id, int que_id, String api_case, String comment_text) {
//        ProgressDialog.getInstance().show(mContext);
//        //mBinding.emptytv.setText("Fetching Comments...");
//        //mBinding.emptytv.setVisibility(View.VISIBLE);
//        Log.d(TAG, "fetchCommentsAPI userId : " + user_id);
//        Log.d(TAG, "fetchCommentsAPI Case : " + api_case);
//
//        //String encoded = Base64.encodeToString(comment_text.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
//        //String encodedComment = StringUtils.stripAccents(encoded);
//
//        try {
//            if (isCallFromTest) {
//                if (api_case.equalsIgnoreCase("L"))
//                    call = apiService.fetchTestComments(user_id, que_id, api_case, 1);
//                else
//                    call = apiService.addTestCommentApi(user_id, que_id, "I", AppUtils.encodedString(comment_text));
//            } else {
//                if (api_case.equalsIgnoreCase("L"))
//                    call = apiService.fetchComments(user_id, que_id, api_case, 1);
//                else
//                    call = apiService.addCommentApi(String.valueOf(user_id), que_id, "I", AppUtils.encodedString(comment_text));
//            }
//
//            call.enqueue(new Callback<ProcessQuestion>() {
//                @Override
//                public void onResponse(Call<ProcessQuestion> call, retrofit2.Response<ProcessQuestion> response) {
//                    try {
//                        ProgressDialog.getInstance().dismiss();
//                        //mBinding.emptytv.setVisibility(View.GONE);
//                        commentList.clear();
//                        if (response.body() != null && response.body().getResponse().equalsIgnoreCase("200")) {
//                            commentList = response.body().getCommentList();
//                            setCommentAdapter();
//                            Log.d(TAG, "onResponse commentList : " + commentList.size());
//                            //emptyView();
//                        } else {
//                            Toast.makeText(mContext, UtilHelper.getAPIError(String.valueOf(response.body())), Toast.LENGTH_SHORT).show();
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        //mBinding.emptytv.setVisibility(View.GONE);
//                        //mBinding.shimmerViewContainer.hideShimmer();
//                        ProgressDialog.getInstance().dismiss();
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<ProcessQuestion> call, Throwable t) {
//                    t.printStackTrace();
//                    //mBinding.shimmerViewContainer.hideShimmer();
//                    ProgressDialog.getInstance().dismiss();
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//            //mBinding.shimmerViewContainer.hideShimmer();
//            ProgressDialog.getInstance().dismiss();
//        }
//    }

    private void setCommentAdapter() {
        mBinding.etComment.setText("");
        if (commentList != null) {
            if (isCallFromTest)
                mAdapter = new CommentAdapter(activity, commentList, Module.Test.toString(), "", true, this);
            else
                mAdapter = new CommentAdapter(activity, commentList, Module.Learning.toString(), "", true, this);

            mBinding.commentRecycler.setHasFixedSize(true);
            mBinding.commentRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
            mBinding.commentRecycler.setAdapter(mAdapter);
            //mBinding.shimmerViewContainer.hideShimmer();
        }
    }

    private void likeReplyComment(String strCase,
                                  String commentId, String text) {
        ProgressDialog.getInstance().show(mContext);

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
                ProgressDialog.getInstance().dismiss();

                if (response.body() != null &&
                        response.body().getResponse().equals("200")) {
                    commentList = response.body().getCommentList();
                    setCommentAdapter();
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
}
