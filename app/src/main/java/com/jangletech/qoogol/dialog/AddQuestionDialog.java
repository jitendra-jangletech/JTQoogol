package com.jangletech.qoogol.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.AddTestQuestionAdapter;
import com.jangletech.qoogol.databinding.DialogAddQuestionBinding;
import com.jangletech.qoogol.model.LearningQuestResponse;
import com.jangletech.qoogol.model.LearningQuestionsNew;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.util.AppUtils;
import com.jangletech.qoogol.util.Constant;

import java.util.ArrayList;
import java.util.HashMap;
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
    private List<LearningQuestionsNew> learningQuestionsNewList = new ArrayList<>();
    private List<LearningQuestionsNew> filteredList = new ArrayList<>();
    private HashMap<String, String> categoryMap = new HashMap<>();
    private Boolean isScrolling = false;
    private int currentItems, scrolledOutItems, totalItems;
    private LinearLayoutManager linearLayoutManager;


    public AddQuestionDialog(Activity mContext, int pos, FragmentManager fragmentManager, AddQuestionDialogClickListener listener) {
        super(mContext, android.R.style.Theme_DeviceDefault_Light_DarkActionBar);
        this.mContext = mContext;
        this.listener = listener;
        this.pos = pos;
        this.fragmentManager = fragmentManager;
        initCategoryMap();
    }

    private void initCategoryMap() {
        categoryMap.put("All", "0");
        categoryMap.put(Constant.short_ans, "21");
        categoryMap.put(Constant.long_ans, "22");
        categoryMap.put(Constant.scq, "1");
        categoryMap.put(Constant.mcq, "4");
        categoryMap.put(Constant.fill_the_blanks, "23");
        categoryMap.put(Constant.true_false, "8");
        categoryMap.put(Constant.match_pair, "11");
    }

    private void filterList(String text, String id) {
        Log.i(TAG, "filterList Text : " + text);
        Log.i(TAG, "filterList Id : " + id);
        Log.i(TAG, "learningQuestionsNewList Size : " + learningQuestionsNewList.size());
        filteredList.clear();
        if (text.equalsIgnoreCase(Constant.short_ans) ||
                text.equalsIgnoreCase(Constant.long_ans)
                || text.equalsIgnoreCase(Constant.fill_the_blanks)) {
            for (LearningQuestionsNew learningQuestionsNew : learningQuestionsNewList) {
                Log.i(TAG, "filterList Category : " + learningQuestionsNew.getType());
                if (learningQuestionsNew.getQue_option_type().equalsIgnoreCase(id))
                    filteredList.add(learningQuestionsNew);
            }
        } else {
            for (LearningQuestionsNew learningQuestionsNew : learningQuestionsNewList) {
                Log.i(TAG, "filterList Category : " + learningQuestionsNew.getType());
                if (learningQuestionsNew.getQue_option_type().equalsIgnoreCase(id))
                    filteredList.add(learningQuestionsNew);
            }
        }

        if (text.equalsIgnoreCase("All")) {
            filteredList.addAll(learningQuestionsNewList);
        }

        Log.i(TAG, "filterList Size : " + filteredList.size());
        addTestQuestionAdapter.updateQuestList(filteredList);
        if (filteredList.size() > 0) {
            mBinding.tvEmptyview.setVisibility(View.GONE);
            mBinding.questionRecyclerView.setVisibility(View.VISIBLE);
        } else {
            mBinding.questionRecyclerView.setVisibility(View.GONE);
            mBinding.tvEmptyview.setVisibility(View.VISIBLE);
            mBinding.tvEmptyview.setText("No Questions Found.");
        }
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

        mBinding.questChipGroup.setOnCheckedChangeListener(new ChipGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ChipGroup group, int checkedId) {
                Chip chip = group.findViewById(checkedId);
                if (chip != null) {
                    try {
                        filterList(chip.getText().toString(), categoryMap.get(chip.getText().toString()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //AppUtils.showToast(mContext, categoryMap.get(chip.getText().toString()));
                }
            }
        });

        mBinding.btnSave.setOnClickListener(v -> {
            listener.onTestQuestSaveClick(learningQuestionsNewList, pos);
            dismiss();
        });

//        mBinding.questionRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
//                    isScrolling = true;
//                }
//            }
//
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                currentItems = linearLayoutManager.getChildCount();
//                totalItems = linearLayoutManager.getItemCount();
//                scrolledOutItems = linearLayoutManager.findFirstVisibleItemPosition();
//                if (dy > 0) {
//                    if (isScrolling && !isMoreDataAvailable &&
//                            (currentItems + scrolledOutItems == totalItems)) {
//                        isScrolling = false;
//                        getData(pageCount, "");
//                    }
//                }
//            }
//        });
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
                        learningQuestionsNewList.clear();
                        learningQuestionsNewList.addAll(response.body().getQuestion_list());
                        Log.i(TAG, "onResponse List Size : " + learningQuestionsNewList.size());
                        setTestQuestList(response.body().getQuestion_list());
                    } else {
                        AppUtils.showToast(getContext(), response.body().getMessage());
                        //mBinding.questionRecyclerView.setVisibility(View.GONE);
                        //mBinding.tvEmptyview.setVisibility(View.VISIBLE);
                        //mBinding.tvEmptyview.setText("No Questions Found.");
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
        if (question_list.size() > 0) {
            mBinding.questionRecyclerView.setVisibility(View.VISIBLE);
            mBinding.tvEmptyview.setVisibility(View.GONE);
            addTestQuestionAdapter = new AddTestQuestionAdapter(mContext, question_list, true, this);
            mBinding.questionRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            mBinding.questionRecyclerView.setAdapter(addTestQuestionAdapter);
        } else {
            mBinding.tvEmptyview.setVisibility(View.VISIBLE);
            mBinding.questionRecyclerView.setVisibility(View.GONE);
        }
    }

    private void prepareQueCategory() {
        List que_categoryList = new ArrayList();
        que_categoryList.add("All");
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
    }

    @Override
    public void onRemoveClick(LearningQuestionsNew learningQuestionsNew, int questPos) {

    }

    @Override
    public void onSectionMarks(LearningQuestionsNew learningQuestionsNew, int quesPos) {

    }

    public interface AddQuestionDialogClickListener {
        void onTestQuestSaveClick(List<LearningQuestionsNew> learningQuestionsNewList, int pos);
    }
}
