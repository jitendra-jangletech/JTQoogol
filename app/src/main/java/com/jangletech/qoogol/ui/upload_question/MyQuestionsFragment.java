package com.jangletech.qoogol.ui.upload_question;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.MyQuestionsAdapter;
import com.jangletech.qoogol.databinding.FragmentMyQuestionsBinding;
import com.jangletech.qoogol.model.LearningQuestResponse;
import com.jangletech.qoogol.model.LearningQuestionsNew;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

import static com.jangletech.qoogol.util.Constant.FILL_THE_BLANKS;
import static com.jangletech.qoogol.util.Constant.LONG_ANSWER;
import static com.jangletech.qoogol.util.Constant.MATCH_PAIR;
import static com.jangletech.qoogol.util.Constant.MATCH_PAIR_IMAGE;
import static com.jangletech.qoogol.util.Constant.MCQ;
import static com.jangletech.qoogol.util.Constant.MCQ_IMAGE;
import static com.jangletech.qoogol.util.Constant.MCQ_IMAGE_WITH_TEXT;
import static com.jangletech.qoogol.util.Constant.SCQ;
import static com.jangletech.qoogol.util.Constant.SCQ_IMAGE;
import static com.jangletech.qoogol.util.Constant.SCQ_IMAGE_WITH_TEXT;
import static com.jangletech.qoogol.util.Constant.SHORT_ANSWER;
import static com.jangletech.qoogol.util.Constant.TRUE_FALSE;
import static com.jangletech.qoogol.util.Constant.UPDATE;
import static com.jangletech.qoogol.util.Constant.qoogol;

public class MyQuestionsFragment extends BaseFragment implements MyQuestionsAdapter.ItemClickListener {

    FragmentMyQuestionsBinding mBinding;
    ApiInterface apiService;
    PreferenceManager mSettings;
    boolean isFirstTime = true;
    String pageCount = "0",prevCount = "0";
    MyQuestionsAdapter myQuestionsAdapter;
    private Boolean isScrolling = false;
    private boolean isSearching = false;
    private int currentItems, scrolledOutItems, totalItems;
    LinearLayoutManager linearLayoutManager;
    List<LearningQuestionsNew> questionsNewList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_questions, container, false);
        if (!isFirstTime)
            pageCount = prevCount;

        initView();
        getData();
        return mBinding.getRoot();
    }


    private void initView() {
        apiService = ApiClient.getInstance().getApi();
        mSettings = new PreferenceManager(getActivity());
        questionsNewList = new ArrayList<>();

        mBinding.queSwiperefresh.setOnRefreshListener(() -> {
            pageCount = "0";
            getData();
        });

        mBinding.queRecycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = linearLayoutManager.getChildCount();
                totalItems = linearLayoutManager.getItemCount();
                scrolledOutItems = linearLayoutManager.findFirstVisibleItemPosition();
                if (dy > 0) {
                    if (!isSearching) {
                        if (isScrolling && (currentItems + scrolledOutItems == totalItems)) {
                            isScrolling = false;
                            getData();
                        }
                    }
                }
            }
        });
    }



    private void getData() {
        Call<LearningQuestResponse> call = apiService.fetchMyQuestionsApi(mSettings.getUserId(), mSettings.getUserId(), "", getDeviceId(getActivity()), qoogol, mSettings.getString(Constant.ue_id), String.valueOf(pageCount));
        call.enqueue(new Callback<LearningQuestResponse>() {
            @Override
            public void onResponse(Call<LearningQuestResponse> call, retrofit2.Response<LearningQuestResponse> response) {
                try {
                    dismissRefresh(mBinding.queSwiperefresh);
                    if (response.body() != null && response.body().getResponse().equalsIgnoreCase("200")) {
                        isFirstTime = false;
                        prevCount = pageCount;
                        pageCount = response.body().getRow_count();
                        setData(response.body().getQuestion_list());
                    }
                } catch (Exception e) {
                    dismissRefresh(mBinding.queSwiperefresh);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<LearningQuestResponse> call, Throwable t) {
                dismissRefresh(mBinding.queSwiperefresh);
                t.printStackTrace();
            }
        });
    }

    private void setData(List<LearningQuestionsNew> questionsList) {
        dismissRefresh(mBinding.queSwiperefresh);

        if (questionsList != null && questionsList.size() > 0) {
            mBinding.tvNoQuest.setVisibility(View.GONE);
        } else {
            mBinding.tvNoQuest.setVisibility(View.VISIBLE);
        }
        questionsNewList.addAll(questionsList);
        initRecycler(questionsNewList);

    }

    private void initRecycler(List<LearningQuestionsNew> questionsList) {
        myQuestionsAdapter = new MyQuestionsAdapter(getActivity(), questionsList, this);
        mBinding.queRecycler.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setAutoMeasureEnabled(false);
        mBinding.queRecycler.setLayoutManager(linearLayoutManager);
        myQuestionsAdapter.setHasStableIds(true);
        mBinding.queRecycler.setAdapter(myQuestionsAdapter);

        dismissRefresh(mBinding.queSwiperefresh);
    }

    @Override
    public void onEditClick(LearningQuestionsNew learningQuestionsNew) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", learningQuestionsNew);
        bundle.putInt("call_from", UPDATE);
        if (learningQuestionsNew.getQue_option_type().equalsIgnoreCase(SHORT_ANSWER))
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.nav_mcq_question, bundle);
        else if (learningQuestionsNew.getQue_option_type().equalsIgnoreCase(LONG_ANSWER))
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.nav_longans_question, bundle);
        else if (learningQuestionsNew.getQue_option_type().equalsIgnoreCase(FILL_THE_BLANKS))
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.nav_fill_the_blanks, bundle);
        else if (learningQuestionsNew.getQue_option_type().equalsIgnoreCase(SCQ))
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.nav_scq_question, bundle);
        else if (learningQuestionsNew.getQue_option_type().equalsIgnoreCase(SCQ_IMAGE))
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.nav_scq_image, bundle);
        else if (learningQuestionsNew.getQue_option_type().equalsIgnoreCase(SCQ_IMAGE_WITH_TEXT))
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.nav_scq_imagetxt, bundle);
        else if (learningQuestionsNew.getQue_option_type().equalsIgnoreCase(MCQ))
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.nav_mcq_question, bundle);
        else if (learningQuestionsNew.getQue_option_type().equalsIgnoreCase(MCQ_IMAGE))
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.nav_mcq_image, bundle);
        else if (learningQuestionsNew.getQue_option_type().equalsIgnoreCase(MCQ_IMAGE_WITH_TEXT))
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.nav_mcq_imagetxt, bundle);
        else if (learningQuestionsNew.getQue_option_type().equalsIgnoreCase(TRUE_FALSE))
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.nav_true_false_frag, bundle);
        else if (learningQuestionsNew.getQue_option_type().equalsIgnoreCase(MATCH_PAIR))
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.nav_mtp_question, bundle);
        else if (learningQuestionsNew.getQue_option_type().equalsIgnoreCase(MATCH_PAIR_IMAGE))
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.nav_mcq_image, bundle);


    }
}