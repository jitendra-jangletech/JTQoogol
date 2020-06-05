package com.jangletech.qoogol.ui.learning;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.CommentAdapter;
import com.jangletech.qoogol.databinding.CommentViewBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.enums.Module;
import com.jangletech.qoogol.model.Comments;
import com.jangletech.qoogol.model.ProcessQuestion;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;
import com.jangletech.qoogol.util.UtilHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;


public class CommentFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "CommentFragment";
    private CommentViewModel mViewModel;

    public static CommentFragment newInstance() {
        return new CommentFragment();
    }

    CommentViewBinding commentViewBinding;
    Bundle bundle;
    String questionId;
    int tmId;
    List<Comments> commentList;
    CommentAdapter commentAdapter;
    ApiInterface apiService = ApiClient.getInstance().getApi();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        commentViewBinding = DataBindingUtil.inflate(inflater, R.layout.comment_view, container, false);
        initView();
        setListeners();
        return commentViewBinding.getRoot();
    }

    private void setListeners() {
        commentViewBinding.btnSend.setOnClickListener(this);
    }


    private void fetchCommentsAPI(int user_id, String que_id, String api_case, String comment_text) {
        ProgressDialog.getInstance().show(getActivity());
        Call<ProcessQuestion> call;

        if (api_case.equalsIgnoreCase("L"))
            call = apiService.fetchComments(user_id, que_id, api_case);
        else
            call = apiService.addCommentApi(user_id, que_id, api_case, comment_text);

        call.enqueue(new Callback<ProcessQuestion>() {
            @Override
            public void onResponse(Call<ProcessQuestion> call, retrofit2.Response<ProcessQuestion> response) {
                try {
                    ProgressDialog.getInstance().dismiss();
                    commentList.clear();
                    if (response.body() != null && response.body().getResponse().equalsIgnoreCase("200")) {
                        commentList = response.body().getCommentList();
                        mViewModel.setCommentList(commentList);
                        emptyView();
                    } else {
                        Toast.makeText(getActivity(), UtilHelper.getAPIError(String.valueOf(response.body())), Toast.LENGTH_SHORT).show();
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

    private void fetchTestCommentsAPI(int user_id, int tmId, String api_case, String comment_text) {
        ProgressDialog.getInstance().show(getActivity());
        Call<ProcessQuestion> call;

        if (api_case.equalsIgnoreCase("L"))
            call = apiService.fetchTestComments(user_id, tmId, api_case);
        else
            call = apiService.addTestCommentApi(user_id, tmId, api_case, comment_text);

        call.enqueue(new Callback<ProcessQuestion>() {
            @Override
            public void onResponse(Call<ProcessQuestion> call, retrofit2.Response<ProcessQuestion> response) {
                try {
                    ProgressDialog.getInstance().dismiss();
                    commentList.clear();
                    if (response.body() != null && response.body().getResponse().equalsIgnoreCase("200")) {
                        commentList = response.body().getCommentList();
                        mViewModel.setCommentList(commentList);
                        //commentAdapter.updateList(commentList);
                        emptyView();
                    } else {
                        Toast.makeText(getActivity(), UtilHelper.getAPIError(String.valueOf(response.body())), Toast.LENGTH_SHORT).show();
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

    private void initView() {
        bundle = getArguments();
        commentList = new ArrayList<>();
        emptyView();
        if (bundle != null && bundle.getString(Constant.CALL_FROM).equals(Module.Learning.toString())) {
            questionId = bundle.getString("QuestionId");
            fetchCommentsAPI(new PreferenceManager(getActivity()).getInt(Constant.USER_ID), questionId, "L", "");
        }

        if (bundle != null && bundle.getString(Constant.CALL_FROM).equals(Module.Test.toString())) {
            tmId = bundle.getInt("tmId");
            Log.d(TAG, "Tm Id : "+tmId);
            fetchTestCommentsAPI(new PreferenceManager(getActivity()).getInt(Constant.USER_ID), tmId, "L", "");
        }
    }

    private void emptyView() {
        if (commentList.size() == 0)
            commentViewBinding.emptytv.setVisibility(View.VISIBLE);
        else
            commentViewBinding.emptytv.setVisibility(View.GONE);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(CommentViewModel.class);
        mViewModel.getCommentList().observe(getActivity(), new Observer<List<Comments>>() {
            @Override
            public void onChanged(@Nullable final List<Comments> comments) {
                Log.d(TAG, "onChanged Size : " + comments.size());
                //Log.d(TAG, "onChanged: "+comments.get(0).getTlc_id());
                if (bundle != null && bundle.getString(Constant.CALL_FROM).equals(Module.Learning.toString())) {
                    commentAdapter = new CommentAdapter(getActivity(), comments,Module.Learning.toString());
                }
                if (bundle != null && bundle.getString(Constant.CALL_FROM).equals(Module.Test.toString())) {
                    commentAdapter = new CommentAdapter(getActivity(), comments,Module.Test.toString());
                }
                commentViewBinding.commentRecycler.setHasFixedSize(true);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
                commentViewBinding.commentRecycler.setLayoutManager(linearLayoutManager);
                commentViewBinding.commentRecycler.setAdapter(commentAdapter);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSend:
                if (!commentViewBinding.etComment.getText().toString().isEmpty()) {
                    if (bundle != null && bundle.getString(Constant.CALL_FROM).equals(Module.Learning.toString())) {
                        fetchCommentsAPI(new PreferenceManager(getActivity()).getInt(Constant.USER_ID), questionId, "I", commentViewBinding.etComment.getText().toString().trim());
                    }
                    if (bundle != null && bundle.getString(Constant.CALL_FROM).equals(Module.Test.toString())) {
                        fetchTestCommentsAPI(new PreferenceManager(getActivity()).getInt(Constant.USER_ID), tmId, "I", commentViewBinding.etComment.getText().toString().trim());
                    }
                }
                commentViewBinding.etComment.setText("");
                break;
        }
    }
}
