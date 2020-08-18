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
    boolean isFilterApplied =false;



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
        mBinding.reset.setOnClickListener(v -> {
            mBinding.testDifficultyLevelChipGrp.clearCheck();
            mSettings.setQueCategoryFilter(null);
            mSettings.setTypeFilter(null);
            mSettings.setQueDiffLevelFilter(null);
            filterClickListener.onResetClick();
            dismiss();
        });

        mBinding.done.setOnClickListener(v -> {
            String type = getSelectedChipValues(mBinding.queTypeChipGrp);

                if (type.contains(Constant.trending))
                    params.put(Constant.q_trending,"1");
                else
                    params.put(Constant.q_trending,"");

                if (type.contains(Constant.popular))
                    params.put(Constant.q_popular,"1");
                else
                    params.put(Constant.q_popular,"");

                if (type.contains(Constant.recent))
                    params.put(Constant.q_recent,"1");
                else
                    params.put(Constant.q_recent,"");

             String diffLevel = getSelectedChipValues(mBinding.testDifficultyLevelChipGrp).replace("Easy","E").replace("Medium","M").replace("Hard","H");
             params.put(Constant.q_diff_level,diffLevel);

             String catg = getSelectedChipValues(mBinding.queCategoryChipGrp);

            StringBuilder results = new StringBuilder();
            if (catg.contains(Constant.short_ans))
                results.append(",'" + Constant.SHORT_ANSWER + "'");
            if (catg.contains(Constant.long_ans))
                results.append(",'" + Constant.LONG_ANSWER + "'");
            if (catg.contains(Constant.fill_the_blanks))
                results.append(",'" + Constant.FILL_THE_BLANKS + "'");

            if (results.toString().startsWith(",")) {
                params.put(Constant.q_type,results.toString().substring(1));
            } else {
                params.put(Constant.q_type,results.toString());
            }

            if (results.toString().startsWith(",")) {
                params.put(Constant.q_type,results.toString().substring(1));
            } else {
                params.put(Constant.q_type,results.toString());
            }

            StringBuilder option_results = new StringBuilder();
            if (catg.contains(Constant.scq)) {
                option_results.append(",'" + Constant.SCQ + "'");
                option_results.append(",'" + Constant.SCQ_IMAGE + "'");
                option_results.append(",'" + Constant.SCQ_IMAGE_WITH_TEXT + "'");
            }
            if (catg.contains(Constant.mcq)) {
                option_results.append(",'" + Constant.MCQ + "'");
                option_results.append(",'" + Constant.MCQ_IMAGE + "'");
                option_results.append(",'" + Constant.MCQ_IMAGE_WITH_TEXT + "'");
            }
            if (catg.contains(Constant.true_false))
                option_results.append(",'" + Constant.TRUE_FALSE + "'");

            if (catg.contains(Constant.match_pair)) {
                option_results.append(",'" + Constant.MATCH_PAIR + "'");
                option_results.append(",'" + Constant.MATCH_PAIR_IMAGE + "'");
            }
            if (option_results.toString().startsWith(",")) {
                params.put(Constant.q_option_type,option_results.toString().substring(1));
            } else {
                params.put(Constant.q_option_type,option_results.toString());
            }

            if (mBinding.rating.getRating()>0)
                params.put(Constant.q_avg_ratings,String.valueOf(mBinding.rating.getRating()));
            else
                params.put(Constant.q_avg_ratings,"");


            filterClickListener.onDoneClick(params);
            dismiss();

        });



        mBinding.rating.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            Log.d(TAG, "onRatingChanged: " + rating);
        });
    }

    private String getSelectedChipValues(ChipGroup chipGroup) {
        StringBuilder results = new StringBuilder();
        if (chipGroup != null) {
            int selectedChipCount = chipGroup.getChildCount();
            for (int i = 0; i < selectedChipCount; i++) {
                Chip chip = (Chip) chipGroup.getChildAt(i);
                if (chip.isChecked()) {
                    //results = results + "','" + chip.getTag().toString();
                    results.append(",'" + chip.getText().toString() + "'");
                }
            }
        }
        String res = results.toString();
        if (res.startsWith(",")) {
            Log.d(TAG, "getSelectedChipValues : " + res.substring(1));
            return res.substring(1);
        } else {
            Log.d(TAG, "getSelectedChipValues : " + res);
            return res;
        }
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
        List typeList = new ArrayList();
        typeList.add(Constant.trending);
        typeList.add(Constant.popular);
        typeList.add(Constant.recent);

        mBinding.queTypeChipGrp.removeAllViews();
        for (int i = 0; i < typeList.size(); i++) {
            Chip chip = (Chip) LayoutInflater.from(mBinding.queTypeChipGrp.getContext()).inflate(R.layout.chip_layout, mBinding.queTypeChipGrp, false);
            chip.setText(typeList.get(i).toString());
            chip.setTag("Type");
            chip.setId(i);
            chip.setClickable(true);
            mapTypeChips.put(i, chip);
            chip.setCheckable(true);
            chip.setOnClickListener(this);
            mBinding.queTypeChipGrp.addView(chip);
        }
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

        mBinding.queCategoryChipGrp.removeAllViews();
        for (int i = 0; i < que_categoryList.size(); i++) {
            Chip chip = (Chip) LayoutInflater.from(mBinding.queCategoryChipGrp.getContext()).inflate(R.layout.chip_layout, mBinding.queCategoryChipGrp, false);
            chip.setText(que_categoryList.get(i).toString());
            chip.setTag("Question_Category");
            chip.setId(i);
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

        void onDoneClick(HashMap<String, String> params);
    }
}
