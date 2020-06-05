package com.jangletech.qoogol.dialog;


import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
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
import com.jangletech.qoogol.model.TestQuestionNew;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.ui.learning.CommentViewModel;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;
import com.jangletech.qoogol.util.UtilHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class CommentDialog extends Dialog {

    private CommentDialogBinding mBinding;
    private Activity mContext;
    private List<Comments> commentList;
    private CommentAdapter commentAdapter;
    private CommentViewModel mViewModel;
    private TestQuestionNew testQuestionNew;
    ApiInterface apiService = ApiClient.getInstance().getApi();

    public CommentDialog(@NonNull Activity mContext, TestQuestionNew testQuestionNew) {
        super(mContext, android.R.style.Theme_DeviceDefault_Light_DarkActionBar);
        this.mContext = mContext;
        this.testQuestionNew = testQuestionNew;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.comment_dialog, null, false);
        setContentView(mBinding.getRoot());
        commentList = new ArrayList<>();
        fetchCommentsAPI(new PreferenceManager(mContext).getInt(Constant.USER_ID),
                testQuestionNew.getTq_q_id(), "L", "");

        mBinding.btnClose.setOnClickListener(v -> {
            dismiss();
        });

        mBinding.btnSend.setOnClickListener(v -> {
            if (mBinding.etComment.getText().toString().isEmpty()) {
                Toast.makeText(mContext, "Please enter comment", Toast.LENGTH_SHORT).show();
                return;
            }
            fetchCommentsAPI(new PreferenceManager(mContext).getInt(Constant.USER_ID),
                    testQuestionNew.getTq_q_id(), "I", mBinding.etComment.getText().toString());
        });
    }

    private void fetchCommentsAPI(int user_id, String que_id, String api_case, String comment_text) {
        ProgressDialog.getInstance().show(mContext);
        Call<ProcessQuestion> call;

        if (api_case.equalsIgnoreCase("L"))
            call = apiService.fetchComments(user_id, que_id, api_case);
        else
            call = apiService.addCommentApi(String.valueOf(user_id), que_id, api_case, comment_text);

        call.enqueue(new Callback<ProcessQuestion>() {
            @Override
            public void onResponse(Call<ProcessQuestion> call, retrofit2.Response<ProcessQuestion> response) {
                try {
                    ProgressDialog.getInstance().dismiss();
                    commentList.clear();
                    if (response.body() != null && response.body().getResponse().equalsIgnoreCase("200")) {
                        commentList = response.body().getCommentList();
                        //setCommentAdapter();
                        emptyView();
                    } else {
                        Toast.makeText(mContext, UtilHelper.getAPIError(String.valueOf(response.body())), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ProgressDialog.getInstance().dismiss();
                }
            }

            @Override
            public void onFailure(Call<ProcessQuestion> call, Throwable t) {
                t.printStackTrace();
                ProgressDialog.getInstance().dismiss();
            }
        });
    }

   /* private void setCommentAdapter() {
        commentAdapter = new CommentAdapter(mContext, commentList, Module.Learning.toString());
        mBinding.commentRecycler.setHasFixedSize(true);
        mBinding.commentRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.commentRecycler.setAdapter(commentAdapter);
    }*/

    private void emptyView() {
        mBinding.etComment.setText("");
        if (commentList.size() == 0)
            mBinding.emptytv.setVisibility(View.VISIBLE);
        else
            mBinding.emptytv.setVisibility(View.GONE);
    }

}
