package com.jangletech.qoogol.ui.learning;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jangletech.qoogol.CommentAdapter;
import com.jangletech.qoogol.MainActivity;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.LearingAdapter;
import com.jangletech.qoogol.databinding.CommentViewBinding;
import com.jangletech.qoogol.model.Comments;

import java.util.ArrayList;
import java.util.List;

import static com.jangletech.qoogol.MainActivity.navController;

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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        commentViewBinding = DataBindingUtil.inflate(inflater, R.layout.comment_view, container, false);
        initView();
        setData();
        return commentViewBinding.getRoot();
    }

    private void setData() {
        commentList.clear();

        Comments comments = new Comments();
        comments.setCommentId("110");
        comments.setComment("Useful question..");
        comments.setTime("4/6/2020");
        comments.setUserId("34");
        comments.setUserName("Neha K.");
        commentList.add(comments);

        Comments comments1 = new Comments();
        comments1.setCommentId("110");
        comments1.setComment("Most asked question..");
        comments1.setTime("1/6/2020");
        comments1.setUserId("23");
        comments1.setUserName("Ankit L.");
        commentList.add(comments1);

        Comments comments2 = new Comments();
        comments2.setCommentId("110");
        comments2.setComment("Popular question..");
        comments2.setTime("4/3/2020");
        comments2.setUserId("11");
        comments2.setUserName("Swara K.");
        commentList.add(comments2);

        Comments comments3 = new Comments();
        comments3.setCommentId("110");
        comments3.setComment("Useful question..");
        comments3.setTime("4/2/2020");
        comments3.setUserId("67");
        comments3.setUserName("Anushri P.");
        commentList.add(comments3);


        commentAdapter.notifyDataSetChanged();

        if (commentList.size()==0)
            commentViewBinding.emptytv.setVisibility(View.VISIBLE);
        else
            commentViewBinding.emptytv.setVisibility(View.GONE);
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

        OnBackPressedCallback callback = new OnBackPressedCallback(
                true // default to enabled
        ) {
            @Override
            public void handleOnBackPressed() {
                if (navController.getCurrentDestination().getId()==R.id.nav_comments) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        };
        getActivity().getOnBackPressedDispatcher().addCallback(
                this, // LifecycleOwner
                callback);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(CommentViewModel.class);
    }

}
