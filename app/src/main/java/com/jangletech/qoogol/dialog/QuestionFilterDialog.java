package com.jangletech.qoogol.dialog;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.QuestionDialogFilterBinding;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class QuestionFilterDialog extends BottomSheetDialogFragment implements View.OnClickListener {

    private static final String TAG = "FilterDialog";
    private FilterClickListener filterClickListener;
    private QuestionDialogFilterBinding mBinding;
    private ApiInterface apiService = ApiClient.getInstance().getApi();
    private HashMap<String, String> params = new HashMap<>();
    private String avgRating = "", strDiffLevel = "", subject = "";
    private Set typeset = new HashSet<String>();
    private Set que_categoryset = new HashSet<String>();
    private Set que_difflevelset = new HashSet<String>();
    private PreferenceManager mSettings;
    private HashMap<Integer, Chip> mapTypeChips = new HashMap();
    private HashMap<Integer, Chip> mapQueCategoryChips = new HashMap();
    private HashMap<Integer, Chip> mapDiffLevelChips = new HashMap();


    public QuestionFilterDialog(@NonNull Activity mContext, FilterClickListener filterClickListener) {
        this.filterClickListener = filterClickListener;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.question_dialog_filter, container, false);
        initViews();
        return mBinding.getRoot();
    }

    private void initViews() {
        mSettings = new PreferenceManager(getActivity());
        prepareDiffLevelChips();
        prepareTypeChips();
        prepareQueCategory();
        setFilters();
        setRatings();
        mBinding.reset.setOnClickListener(v -> {
            mBinding.testDifficultyLevelChipGrp.clearCheck();
            mSettings.setQueCategoryFilter(null);
            mSettings.setTypeFilter(null);
            mSettings.setQueDiffLevelFilter(null);
            filterClickListener.onResetClick();
            dismiss();
        });

        mBinding.done.setOnClickListener(v -> {
            if (que_difflevelset != null && que_difflevelset.size() > 0) {
                mSettings.setQueDiffLevelFilter(que_difflevelset);
            }

            if (typeset != null && typeset.size() > 0) {
                mSettings.setTypeFilter(typeset);
            }

            if (que_categoryset != null && que_categoryset.size() > 0) {
                mSettings.setQueCategoryFilter(que_categoryset);
            }

            if (mBinding.rating!=null) {
                mSettings.setRatingsFilter(String.valueOf(mBinding.rating.getRating()));
            }

            filterClickListener.onDoneClick();
            dismiss();
        });



        mBinding.rating.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            Log.d(TAG, "onRatingChanged: " + rating);
            params.put(Constant.q_avg_ratings, String.valueOf(rating));
        });
    }

    private void setRatings() {
        String rating = mSettings.getRatingsFilter();
        if (rating!=null&& !rating.isEmpty()) {
            mBinding.rating.setRating(Float.parseFloat(rating));
        }
    }

    private void setFilters() {
        if (params != null) {
            String avgRating = params.get(Constant.q_avg_ratings);
            String diffLevel = params.get(Constant.q_diff_level);
            if (avgRating != null && !avgRating.isEmpty()) {
                mBinding.rating.setRating(Float.parseFloat(avgRating));
            }
        }
    }

    private void prepareTypeChips() {
        if (mSettings.getTypeFilter() != null) {
            typeset = mSettings.getTypeFilter();
        }
        List typeList = new ArrayList();
        typeList.add("Trending");
        typeList.add("Popular");
        typeList.add("Recent");

        mBinding.queTypeChipGrp.removeAllViews();
        for (int i = 0; i < typeList.size(); i++) {
            Chip chip = (Chip) LayoutInflater.from(mBinding.queTypeChipGrp.getContext()).inflate(R.layout.chip_layout, mBinding.queTypeChipGrp, false);
            chip.setText(typeList.get(i).toString());
            chip.setTag("Type");
            chip.setId(i);
            if (typeset.contains(typeList.get(i).toString()))
                chip.setChecked(true);
            chip.setClickable(true);
            mapTypeChips.put(i, chip);
            chip.setCheckable(true);
            chip.setOnClickListener(this);
            mBinding.queTypeChipGrp.addView(chip);
        }
    }

    private void prepareQueCategory() {
        if (mSettings.getQueCategoryFilter() != null) {
            que_categoryset = mSettings.getQueCategoryFilter();
        }
        List que_categoryList = new ArrayList();
        que_categoryList.add(Constant.short_ans);
        que_categoryList.add(Constant.long_ans);
        que_categoryList.add(Constant.scq);
        que_categoryList.add(Constant.mcq);
        que_categoryList.add(Constant.fill_the_blanks);
        que_categoryList.add(Constant.true_false);
        que_categoryList.add(Constant.match_pair);

        mBinding.queCategoryChipGrp.removeAllViews();
        for (int i = 0; i < que_categoryList.size(); i++) {
            Chip chip = (Chip) LayoutInflater.from(mBinding.queCategoryChipGrp.getContext()).inflate(R.layout.chip_layout, mBinding.queCategoryChipGrp, false);
            chip.setText(que_categoryList.get(i).toString());
            chip.setTag("Question_Category");
            chip.setId(i);
            if (que_categoryset.contains(que_categoryList.get(i).toString()))
                chip.setChecked(true);
            chip.setClickable(true);
            mapQueCategoryChips.put(i, chip);
            chip.setCheckable(true);
            chip.setOnClickListener(this);
            mBinding.queCategoryChipGrp.addView(chip);
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


    private void prepareDiffLevelChips() {
        if (mSettings.getQueDiffLevelFilter() != null) {
            que_difflevelset = mSettings.getQueDiffLevelFilter();
        }
        List que_difflevelList = new ArrayList();
        que_difflevelList.add("Easy");
        que_difflevelList.add("Medium");
        que_difflevelList.add("Hard");

        mBinding.testDifficultyLevelChipGrp.removeAllViews();
        for (int i = 0; i < que_difflevelList.size(); i++) {
            Chip chip = (Chip) LayoutInflater.from(mBinding.testDifficultyLevelChipGrp.getContext()).inflate(R.layout.chip_layout, mBinding.testDifficultyLevelChipGrp, false);
            chip.setText(que_difflevelList.get(i).toString());
            chip.setTag("Diff_Level");
            chip.setId(i);
            if (que_difflevelset.contains(que_difflevelList.get(i).toString()))
                chip.setChecked(true);
            chip.setClickable(true);
            mapDiffLevelChips.put(i, chip);
            chip.setCheckable(true);
            chip.setOnClickListener(this);
            mBinding.testDifficultyLevelChipGrp.addView(chip);
        }
    }

    @Override
    public void onClick(View v) {
        if (v != null) {
            if (v.getTag().toString().equalsIgnoreCase("Diff_Level")) {
                Chip chip = (Chip) v;
                setSelectedDiffLevelChips(chip);
            } else if (v.getTag().toString().equalsIgnoreCase("Type")) {
                Chip chip = (Chip) v;
                setSelectedTypeChips(chip);
            } else if (v.getTag().toString().equalsIgnoreCase("Question_Category")) {
                Chip chip = (Chip) v;
                setSelectedQueCategoryChips(chip);
            }
        }
    }

    private void setSelectedDiffLevelChips(Chip chip) {
        try {
            que_difflevelset.clear();
            if (mSettings.getQueDiffLevelFilter() != null) {
                que_difflevelset = mSettings.getQueDiffLevelFilter(); }

            for (int i = 0; i < mapDiffLevelChips.size(); i++) {
                String text = mapDiffLevelChips.get(i).getText().toString();
                if (mapDiffLevelChips.get(i).isChecked()) {
                    if (!que_difflevelset.contains(text)) {
                        que_difflevelset.add(text);
                        mapDiffLevelChips.get(i).setTextColor(Color.WHITE);
                    } else {
                        que_difflevelset.remove(text);
                        mapDiffLevelChips.get(i).setTextColor(Color.BLACK);
                    }
                } else {
                    mapDiffLevelChips.get(i).setTextColor(Color.BLACK);
                    if (que_difflevelset.contains(text))
                        que_difflevelset.remove(text);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setSelectedQueCategoryChips(Chip chip) {
        try {
            que_categoryset.clear();
            if (mSettings.getQueCategoryFilter() != null) {
                que_categoryset = mSettings.getQueCategoryFilter(); }

            for (int i = 0; i < mapQueCategoryChips.size(); i++) {
                String text = mapQueCategoryChips.get(i).getText().toString();
                if (mapQueCategoryChips.get(i).isChecked()) {
                    if (!que_categoryset.contains(text)) {
                        que_categoryset.add(text);
                        mapQueCategoryChips.get(i).setTextColor(Color.WHITE);
                    } else {
                        que_categoryset.remove(text);
                        mapQueCategoryChips.get(i).setTextColor(Color.BLACK);
                    }
                } else {
                    mapQueCategoryChips.get(i).setTextColor(Color.BLACK);
                    if (que_categoryset.contains(text))
                        que_categoryset.remove(text);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setSelectedTypeChips(Chip chip) {
        try {
            typeset.clear();
            if (mSettings.getTypeFilter() != null) {
                typeset = mSettings.getTypeFilter(); }

            for (int i = 0; i < mapTypeChips.size(); i++) {
                String text = mapTypeChips.get(i).getText().toString();
                if (mapTypeChips.get(i).isChecked()) {
                    if (!typeset.contains(text)) {
                        typeset.add(text);
                        mapTypeChips.get(i).setTextColor(Color.WHITE);
                    } else {
                        typeset.remove(text);
                        mapTypeChips.get(i).setTextColor(Color.BLACK);
                    }
                } else {
                    mapTypeChips.get(i).setTextColor(Color.BLACK);
                    if (typeset.contains(text))
                        typeset.remove(text);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface FilterClickListener {
        void onResetClick();

        void onDoneClick();
    }
}
