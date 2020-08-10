package com.jangletech.qoogol.dialog;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.DialogFilterBinding;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.util.Constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FilterDialog extends BottomSheetDialogFragment implements View.OnClickListener {

    private static final String TAG = "FilterDialog";
    private Activity mContext;
    private List<String> subjectList;
    private FilterClickListener filterClickListener;
    private DialogFilterBinding mBinding;
    private HashMap<Integer, Chip> mapSubjectChips = new HashMap();
    private ApiInterface apiService = ApiClient.getInstance().getApi();
    private HashMap<String, String> params = new HashMap<>();
    private String avgRating = "", strDiffLevel = "", subject = "";

    public FilterDialog(@NonNull Activity mContext, List<String> subjectList, HashMap<String, String> params, FilterClickListener filterClickListener) {
        this.mContext = mContext;
        this.subjectList = subjectList;
        this.params = params;
        this.filterClickListener = filterClickListener;
    }

    public FilterDialog() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_filter, container, false);
        initViews();
        return mBinding.getRoot();
    }

    private void initViews() {
        prepareSubjectChips(subjectList);
        prepareDiffLevelChips();
        setFilters();

        mBinding.reset.setOnClickListener(v -> {
            mBinding.testDifficultyLevelChipGrp.clearCheck();
            mBinding.subjectsChipGrp.clearCheck();
            mBinding.rating.setRating(0);
            params.put(Constant.tm_diff_level, "");
            params.put(Constant.sm_sub_name, "");
            params.put(Constant.tm_avg_rating, "");
            filterClickListener.onResetClick(params);
            dismiss();
        });

        mBinding.done.setOnClickListener(v -> {
            filterClickListener.onDoneClick(params);
            dismiss();
        });

        mBinding.testDifficultyLevelChipGrp.setOnCheckedChangeListener((chipGroup, id) -> {
            Chip chip = ((Chip) chipGroup.getChildAt(chipGroup.getCheckedChipId()));
            if (chip != null) {
                if (chip.getText().toString().equalsIgnoreCase("Hard"))
                    params.put(Constant.tm_diff_level, "H");
                if (chip.getText().toString().equalsIgnoreCase("Medium"))
                    params.put(Constant.tm_diff_level, "M");
                if (chip.getText().toString().equalsIgnoreCase("Easy"))
                    params.put(Constant.tm_diff_level, "E");
                setCheckedChip(mBinding.testDifficultyLevelChipGrp);
            } else {
                setCheckedChip(mBinding.testDifficultyLevelChipGrp);
                params.put(Constant.tm_diff_level, "");
            }
        });

        mBinding.subjectsChipGrp.setOnCheckedChangeListener((chipGroup, id) -> {
            Chip chip = ((Chip) chipGroup.getChildAt(chipGroup.getCheckedChipId()));
            if (chip != null) {
                setCheckedChip(mBinding.subjectsChipGrp);
            } else {
                setCheckedChip(mBinding.subjectsChipGrp);
            }
        });

        mBinding.rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                Log.d(TAG, "onRatingChanged: " + rating);
                params.put(Constant.tm_avg_rating, String.valueOf(rating));
            }
        });
    }

    private void setFilters() {
        if (params != null) {
            String avgRating = params.get(Constant.tm_avg_rating);
            String diffLevel = params.get(Constant.tm_diff_level);
            String subject = params.get(Constant.sm_sub_name);
            if (avgRating != null && !avgRating.isEmpty()) {
                mBinding.rating.setRating(Float.parseFloat(avgRating));
            }
        }
    }

    private void setCheckedChip(ChipGroup chipGroup) {
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) chipGroup.getChildAt(i);
            if (chip.isChecked()) {
                chip.setTextColor(Color.WHITE);
            } else {
                chip.setTextColor(Color.BLACK);
            }
        }
    }

    private void prepareSubjectChips(List<String> subjectList) {
        mBinding.subjectsChipGrp.removeAllViews();
        for (int i = 0; i < subjectList.size(); i++) {
            Chip chip = (Chip) LayoutInflater.from(mBinding.subjectsChipGrp.getContext()).inflate(R.layout.chip_layout, mBinding.subjectsChipGrp, false);
            chip.setText(subjectList.get(i));
            chip.setTag("Subjects");
            chip.setId(i);
            mapSubjectChips.put(i, chip);
            chip.setOnClickListener(this);
            chip.setClickable(true);
            chip.setCheckable(true);
            mBinding.subjectsChipGrp.addView(chip);
        }
    }

    private void prepareDiffLevelChips() {
        strDiffLevel = params.get(Constant.tm_diff_level);
        List diffLevelList = new ArrayList();
        diffLevelList.add("Easy");
        diffLevelList.add("Medium");
        diffLevelList.add("Hard");

        mBinding.testDifficultyLevelChipGrp.removeAllViews();
        for (int i = 0; i < diffLevelList.size(); i++) {
            Chip chip = (Chip) LayoutInflater.from(mBinding.testDifficultyLevelChipGrp.getContext()).inflate(R.layout.chip_layout, mBinding.testDifficultyLevelChipGrp, false);
            chip.setText(diffLevelList.get(i).toString());
            chip.setId(i);
            chip.setTag("Difficulty");
            chip.setClickable(true);
            chip.setCheckable(true);
            if (strDiffLevel != null && strDiffLevel.equalsIgnoreCase("E") && i == 0)
                chip.setChecked(true);
            if (strDiffLevel != null && strDiffLevel.equalsIgnoreCase("M") && i == 1)
                chip.setChecked(true);
            if (strDiffLevel != null && strDiffLevel.equalsIgnoreCase("H") && i == 2)
                chip.setChecked(true);
            mBinding.testDifficultyLevelChipGrp.addView(chip);
        }
    }

    @Override
    public void onClick(View v) {

    }


    public interface FilterClickListener {
        void onResetClick(HashMap<String, String> params);

        void onDoneClick(HashMap<String, String> params);
    }
}
