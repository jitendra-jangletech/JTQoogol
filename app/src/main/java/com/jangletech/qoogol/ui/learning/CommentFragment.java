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


public class CommentFragment extends Fragment {

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
        return commentViewBinding.getRoot();
    }


    private void ProcessQuestionAPI(Map<String, Object> requestBody) {
        ProgressDialog.getInstance().show(getActivity());
        Call<ProcessQuestion> call = apiService.processQuestion(requestBody);
        call.enqueue(new Callback<ProcessQuestion>() {
            @Override
            public void onResponse(Call<ProcessQuestion> call, retrofit2.Response<ProcessQuestion> response) {
                try {
                    ProgressDialog.getInstance().dismiss();
                    commentList.clear();
                    if (response.body()!=null && response.body().getResponse().equalsIgnoreCase("200")){
                        commentList = response.body().getCommentList();
                        commentAdapter.notifyDataSetChanged();
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

//    private void setData() {
//        commentList.clear();
//
//        Comments comments = new Comments();
//        comments.setCommentId("110");
//        comments.setComment("Useful question..");
//        comments.setTime("4/6/2020");
//        comments.setUserId("34");
//        comments.setUserName("Neha K.");
//        commentList.add(comments);
//
//        Comments comments1 = new Comments();
//        comments1.setCommentId("110");
//        comments1.setComment("Most asked question..");
//        comments1.setTime("1/6/2020");
//        comments1.setUserId("23");
//        comments1.setUserName("Ankit L.");
//        commentList.add(comments1);
//
//        Comments comments2 = new Comments();
//        comments2.setCommentId("110");
//        comments2.setComment("Popular question..");
//        comments2.setTime("4/3/2020");
//        comments2.setUserId("11");
//        comments2.setUserName("Swara K.");
//        commentList.add(comments2);
//
//        Comments comments3 = new Comments();
//        comments3.setCommentId("110");
//        comments3.setComment("Useful question..");
//        comments3.setTime("4/2/2020");
//        comments3.setUserId("67");
//        comments3.setUserName("Anushri P.");
//        commentList.add(comments3);
//
//
//        commentAdapter.notifyDataSetChanged();
//
//        if (commentList.size()==0)
//            commentViewBinding.emptytv.setVisibility(View.VISIBLE);
//        else
//            commentViewBinding.emptytv.setVisibility(View.GONE);
//    }

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


        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put(Constant.u_user_id,1069);
//        requestBody.put(Constant.q_id,questionId);
        requestBody.put(Constant.q_id,1);
        requestBody.put("Case","L");

        ProcessQuestionAPI(requestBody);



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

}
