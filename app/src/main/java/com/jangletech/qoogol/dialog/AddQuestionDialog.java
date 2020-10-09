package com.jangletech.qoogol.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Window;

import androidx.databinding.DataBindingUtil;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.AddTestQuestionAdapter;
import com.jangletech.qoogol.databinding.DialogAddQuestionBinding;
import com.jangletech.qoogol.model.LearningQuestResponse;
import com.jangletech.qoogol.model.LearningQuestionsNew;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.util.AppUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddQuestionDialog extends Dialog {

    private static final String TAG = "AddQuestionDialog";
    private Context mContext;
    private DialogAddQuestionBinding mBinding;
    private AddTestQuestionAdapter addTestQuestionAdapter;
    private AddQuestionDialogClickListener listener;

    public AddQuestionDialog(Context mContext, AddQuestionDialogClickListener listener) {
        super(mContext, android.R.style.Theme_DeviceDefault_Light_DarkActionBar);
        this.mContext = mContext;
        this.listener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.dialog_add_question, null, false);
        setContentView(mBinding.getRoot());
        fetchTestQuestList();
    }

    public void fetchTestQuestList() {
        ProgressDialog.getInstance().show(mContext);
        Call<LearningQuestResponse> call = ApiClient.getInstance().getApi()
                .fetchTestQuestions(AppUtils.getUserId(),
                        AppUtils.getDeviceId(), "Q");
        call.enqueue(new Callback<LearningQuestResponse>() {
            @Override
            public void onResponse(Call<LearningQuestResponse> call, Response<LearningQuestResponse> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.body() != null) {
                    if (response.body().getResponse().equals("200")) {
                        //setCreatedTestList(response.body().getTestList());
                        setTestQuestList(response.body().getQuestion_list());
                    } else if (response.body().getResponse().equals("501")) {
                        //resetSettingAndLogout();
                    } else {
                        //showErrorDialog(getActivity(), response.body().getResponse(), response.body().getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<LearningQuestResponse> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                //AppUtils.showToast(getActivity(), t, "");
                //apiCallFailureDialog(t);
                t.printStackTrace();
            }
        });
    }

    private void setTestQuestList(List<LearningQuestionsNew> question_list) {
        addTestQuestionAdapter = new AddTestQuestionAdapter(mContext, question_list);

    }

    public interface AddQuestionDialogClickListener {
        void onSaveClick();
    }
}
