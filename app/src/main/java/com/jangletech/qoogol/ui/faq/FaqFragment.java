package com.jangletech.qoogol.ui.faq;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.FAQListAdapter;
import com.jangletech.qoogol.adapter.FAQTilesAdapter;
import com.jangletech.qoogol.databinding.FaqFragmentBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.model.FAQModel;
import com.jangletech.qoogol.model.FaqResponse;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.ItemOffsetDecoration;
import com.jangletech.qoogol.util.PreferenceManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FaqFragment extends BaseFragment implements
        FAQTilesAdapter.TilesClickListener {

    private static final String TAG = "FaqFragment";
    private FaqViewModel mViewModel;
    private FaqFragmentBinding mBinding;
    private ArrayAdapter<String> adapter;
    private Map<String, String> topicsMap;
    private FAQListAdapter faqListAdapter;
    private List<String> topics;
    private FAQTilesAdapter faqTilesAdapter;
    private GridLayoutManager gridLayoutManager;
    private ItemOffsetDecoration itemDecoration;
    private ApiInterface apiService = ApiClient.getInstance().getApi();

    public static FaqFragment newInstance() {
        return new FaqFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.faq_fragment, container, false);
        topicsMap = new HashMap<>();
        topics = new ArrayList<>();
        itemDecoration = new ItemOffsetDecoration(requireActivity(), R.dimen.item_offset);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(FaqViewModel.class);
        initViews();
    }

    private void initViews() {
        getActionBar().setTitle("FAQ Topics");
        HashMap<String, String> params = new HashMap<>();
        params.put(Constant.u_user_id, String.valueOf(new PreferenceManager(requireActivity()).getInt(Constant.USER_ID)));
        params.put(Constant.CASE, "T");
        params.put(Constant.faq_app, Constant.APP_NAME);
        if (mViewModel.getFaqTopics().getValue() == null) {
            fetchFaq(params);
        }
        mViewModel.getFaqTopics().observe(requireActivity(), faqModels -> {
            if (faqModels != null) {
                setFaqTiles(faqModels);
            }
        });
    }

    private void setFaqTiles(List<FAQModel> faqModels) {
        faqTilesAdapter = new FAQTilesAdapter(faqModels, getActivity(), this);
        gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        mBinding.faqRecyclerView.setLayoutManager(gridLayoutManager);
        mBinding.faqRecyclerView.addItemDecoration(itemDecoration);
        mBinding.faqRecyclerView.setAdapter(faqTilesAdapter);
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
    public void onItemClick(FAQModel faqModel) {
        //showToast(""+faqModel.getFaqt_name());
        Bundle bundle = new Bundle();
        bundle.putSerializable("PARAMS", faqModel);
        NavController navController = Navigation.findNavController(mBinding.getRoot());
        navController.navigate(R.id.nav_faq_details, bundle);
    }
}
