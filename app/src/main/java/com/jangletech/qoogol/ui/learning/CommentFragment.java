package com.jangletech.qoogol.ui.learning;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.CommentAdapter;
import com.jangletech.qoogol.databinding.CommentViewBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.model.Comments;
import com.jangletech.qoogol.model.ProcessQuestion;
import com.jangletech.qoogol.model.ResponseObj;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.UtilHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;

import static com.jangletech.qoogol.activities.MainActivity.navController;


public class CommentFragment extends Fragment implements View.OnClickListener {

    private CommentViewModel mViewModel;

    public static CommentFragment newInstance() {
        return new CommentFragment();
    }

    CommentViewBinding commentViewBinding;
    Bundle bundle;
    String questionId;
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


    private void fetchCommentsAPI(String user_id, String que_id, String api_case, String comment_text) {
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
                    if (response.body()!=null && response.body().getResponse().equalsIgnoreCase("200")){
                        commentList = response.body().getCommentList();
                        commentAdapter.updateList(commentList);
                        emptyView();
                    } else {
                        Toast.makeText(getActivity(), UtilHelper.getAPIError(String.valueOf(response.body())),Toast.LENGTH_SHORT).show();
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
        if (bundle != null) {
            questionId = bundle.getString("QuestionId");
        }
        commentAdapter = new CommentAdapter(getActivity(), commentList);
        commentViewBinding.commentRecycler.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        commentViewBinding.commentRecycler.setLayoutManager(linearLayoutManager);
        commentViewBinding.commentRecycler.setAdapter(commentAdapter);
        emptyView();

        fetchCommentsAPI("1069",questionId,"L" ,"");
    }

    private void emptyView() {
        if (commentList.size()==0)
            commentViewBinding.emptytv.setVisibility(View.VISIBLE);
        else
            commentViewBinding.emptytv.setVisibility(View.GONE);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(CommentViewModel.class);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSend:
                fetchCommentsAPI("1069", questionId,"I",commentViewBinding.etComment.getText().toString().trim());
                commentViewBinding.etComment.setText("");
                break;
        }
    }
}
