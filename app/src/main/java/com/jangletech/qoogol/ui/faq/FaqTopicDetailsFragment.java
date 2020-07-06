package com.jangletech.qoogol.ui.faq;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.FAQListAdapter;
import com.jangletech.qoogol.databinding.FragmentFaqTopicDetailsBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.model.FAQModel;
import com.jangletech.qoogol.model.FaqResponse;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.util.Constant;

import java.util.HashMap;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class FaqTopicDetailsFragment extends BaseFragment implements FAQListAdapter.OnItemClickListener {

    private static final String TAG = "FaqTopicDetailsFragment";
    private FaqViewModel mViewModel;
    private FAQListAdapter faqListAdapter;
    private FragmentFaqTopicDetailsBinding mBinding;
    private FAQModel faqModel;
    ApiInterface apiService = ApiClient.getInstance().getApi();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_faq_topic_details, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            faqModel = (FAQModel) getArguments().getSerializable("PARAMS");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(FaqViewModel.class);
        initViews();
    }

    private void initViews() {
        getActionBar().setTitle(faqModel.getFaqt_name());
        HashMap<String, String> params = new HashMap<>();
        params.put(Constant.u_user_id, getUserId());
        params.put(Constant.CASE, "");
        params.put(Constant.faq_app, Constant.APP_NAME);
        params.put(Constant.faqt_id, faqModel.getFaqt_id());
        fetchFaq(params);
        mViewModel.getFaqQuestions().observe(requireActivity(), faqModels -> {
            if(faqModels!=null) {
                faqListAdapter = new FAQListAdapter(faqModels, requireActivity(), this);
                mBinding.faqDetailsRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
                mBinding.faqDetailsRecyclerView.setAdapter(faqListAdapter);
            }
        });
    }

    private void fetchFaq(HashMap<String, String> params) {
        Log.d(TAG, "fetchFaq Params : " + params);
        ProgressDialog.getInstance().show(getActivity());
        Call<FaqResponse> call = apiService.fetchFaq(
                params.get(Constant.u_user_id),
                params.get(Constant.CASE),
                params.get(Constant.faq_app),
                params.get(Constant.faqt_id)
        );
        call.enqueue(new Callback<FaqResponse>() {
            @Override
            public void onResponse(Call<FaqResponse> call, Response<FaqResponse> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.isSuccessful()) {
                    if (params.get(Constant.CASE).equalsIgnoreCase("T")) {
                        mViewModel.setFaqTopics(response.body().getList());
                    } else {
                        mViewModel.setFaqQuestions(response.body().getList());
                    }
                } else {
                    showToast("Something went wrong!!");
                }
            }

            @Override
            public void onFailure(Call<FaqResponse> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                showToast("Something went wrong!!");
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onViewImage(String path) {
        showFullScreen(path);
    }
}