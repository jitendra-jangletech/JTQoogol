package com.jangletech.qoogol.ui.faq;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.FAQListAdapter;
import com.jangletech.qoogol.databinding.FaqFragmentBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.model.FAQModel;
import com.jangletech.qoogol.model.FaqResponse;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FaqFragment extends BaseFragment implements FAQListAdapter.OnItemClickListener {

    private static final String TAG = "FaqFragment";
    private FaqViewModel mViewModel;
    private FaqFragmentBinding mBinding;
    private ArrayAdapter<String> adapter;
    private Map<String,String> topicsMap;
    private FAQListAdapter faqListAdapter;
    private List<String> topics;
    ApiInterface apiService = ApiClient.getInstance().getApi();

    public static FaqFragment newInstance() {
        return new FaqFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater,R.layout.faq_fragment, container, false);
        mViewModel = ViewModelProviders.of(this).get(FaqViewModel.class);
        topicsMap = new HashMap<>();
        topics = new ArrayList<>();
        initViews();
        return mBinding.getRoot();
    }

    private void initViews() {
        HashMap<String,String> params = new HashMap<>();
        params.put(Constant.u_user_id,String.valueOf(new PreferenceManager(requireActivity()).getInt(Constant.USER_ID)));
        params.put(Constant.CASE,"T");
        params.put(Constant.faq_app,Constant.APP_NAME);
        fetchFaq(params);
        mViewModel.getFaqTopics().observe(requireActivity(),faqModels -> {
            for(FAQModel faqModel: faqModels){
                topicsMap.put(faqModel.getFaqt_name(),faqModel.getFaqt_id());
                topics.add(faqModel.getFaqt_name());
            }
            adapter = new ArrayAdapter<String>(requireContext(), R.layout.textview_dropdown, topics);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mBinding.spinnerSelection.setAdapter(adapter);
        });

        mViewModel.getFaqQuestions().observe(requireActivity(),faqModels -> {
            faqListAdapter = new FAQListAdapter(faqModels,requireActivity(),this);
            mBinding.faqRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
            mBinding.faqRecyclerView.setAdapter(faqListAdapter);
        });


        mBinding.spinnerSelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final String name = ((TextView) view).getText().toString();
                String key = topicsMap.get(name);
                if (key!=null && !key.isEmpty()) {
                    Log.i(TAG, "FAQ selected : " + key + " name: " + name);
                    HashMap<String,String> params = new HashMap<>();
                    params.put(Constant.faqt_id,key);
                    params.put(Constant.u_user_id,String.valueOf(new PreferenceManager(requireActivity()).getInt(Constant.USER_ID)));
                    params.put(Constant.CASE,"");
                    params.put(Constant.faq_app,Constant.APP_NAME);
                    fetchFaq(params);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private String getKeyFromValue(Map<String, String> map, String name) {
        Log.d(TAG, "getKeyFromValue Map : "+map);
        Log.d(TAG, "getKeyFromValue Name : "+name);
        String selectedKey = "";
        for (Map.Entry<String, String> e : map.entrySet()) {
            String key = e.getKey();
            String value = e.getValue();
            if (value.equals(name)) {
                selectedKey = key;
                break;
            }
        }
        Log.d(TAG, "getKeyFromValue: "+selectedKey);
        return selectedKey;
    }

    private void fetchFaq(HashMap<String,String> params) {
        Log.d(TAG, "fetchFaq Params : "+params);
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
                if(response.isSuccessful()){
                    if(params.get(Constant.CASE).equalsIgnoreCase("T")){
                        mViewModel.setFaqTopics(response.body().getList());
                    }else{
                        mViewModel.setFaqQuestions(response.body().getList());
                    }
                }else{
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

    @SuppressLint("ClickableViewAccessibility")
    private void showFullScreen(final String profilePath) {
        final Dialog dialog = new Dialog(requireActivity(), android.R.style.Theme_Light);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.image_fullscreen_preview);
        ImageView imageView = dialog.findViewById(R.id.image_preview);
        imageView.setOnTouchListener(new ImageMatrixTouchHandler(dialog.getWindow().getContext()));

        Glide.with(requireActivity()).load(profilePath)
                .thumbnail(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontTransform()
                .dontAnimate()
                .into(imageView);

        dialog.show();
    }

    @Override
    public void onViewImage(String path) {
        showFullScreen(path);
    }
}
