package com.jangletech.qoogol.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Window;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.chip.Chip;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.AddTestQuestionAdapter;
import com.jangletech.qoogol.databinding.DialogAddQuestionBinding;
import com.jangletech.qoogol.model.LearningQuestResponse;
import com.jangletech.qoogol.model.LearningQuestionsNew;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.util.AppUtils;
import com.jangletech.qoogol.util.Constant;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddQuestionDialog extends Dialog implements AddTestQuestionAdapter.AddTestQuestionListener {

    private static final String TAG = "AddQuestionDialog";
    private Activity mContext;
    private DialogAddQuestionBinding mBinding;
    private AddTestQuestionAdapter addTestQuestionAdapter;
    private AddQuestionDialogClickListener listener;
    private int pos;
    private FragmentManager fragmentManager;
    private List<LearningQuestionsNew> learningQuestionsNewList;

    public AddQuestionDialog(Activity mContext, int pos, FragmentManager fragmentManager, AddQuestionDialogClickListener listener) {
        super(mContext, android.R.style.Theme_DeviceDefault_Light_DarkActionBar);
        this.mContext = mContext;
        this.listener = listener;
        this.pos = pos;
        this.fragmentManager = fragmentManager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.dialog_add_question, null, false);
        setContentView(mBinding.getRoot());
        prepareQueCategory();
        fetchTestQuestList();

        mBinding.btnClose.setOnClickListener(v -> {
            dismiss();
        });

        mBinding.btnSave.setOnClickListener(v -> {
            listener.onTestQuestSaveClick(learningQuestionsNewList, pos);
            dismiss();
        });
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
                t.printStackTrace();
            }
        });
    }

    private void setTestQuestList(List<LearningQuestionsNew> question_list) {
        addTestQuestionAdapter = new AddTestQuestionAdapter(mContext, question_list, true, this);
        mBinding.questionRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.questionRecyclerView.setAdapter(addTestQuestionAdapter);
    }

    private void prepareQueCategory() {
        List que_categoryList = new ArrayList();
        que_categoryList.add(Constant.short_ans);
        que_categoryList.add(Constant.long_ans);
        que_categoryList.add(Constant.scq);
        que_categoryList.add(Constant.mcq);
        que_categoryList.add(Constant.fill_the_blanks);
        que_categoryList.add(Constant.true_false);
        que_categoryList.add(Constant.match_pair);

        mBinding.questChipGroup.removeAllViews();
        for (int i = 0; i < que_categoryList.size(); i++) {
            Chip chip = (Chip) LayoutInflater.from(mBinding.questChipGroup.getContext()).inflate(R.layout.chip_new, mBinding.questChipGroup, false);
            chip.setText(que_categoryList.get(i).toString());
            chip.setId(i);
            chip.setClickable(true);
            chip.setCheckable(true);
            mBinding.questChipGroup.addView(chip);
        }
    }

    @Override
    public void onQuestSelected(List<LearningQuestionsNew> list) {
        Log.i(TAG, "onQuestSelected Size : " + list.size());
        this.learningQuestionsNewList = list;
        //DialogFragment addSectionDialog = new AddNewSectionDialog(this);
        //addSectionDialog.show(fragmentManager, "dialog");

    }

    @Override
    public void onRemoveClick(LearningQuestionsNew learningQuestionsNew, int questPos) {

    }

    public interface AddQuestionDialogClickListener {
        void onTestQuestSaveClick(List<LearningQuestionsNew> learningQuestionsNewList, int pos);
    }
}
