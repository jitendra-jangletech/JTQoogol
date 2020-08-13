package com.jangletech.qoogol.dialog;

import android.app.Activity;
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

import java.util.HashMap;

public class FilterDialog extends BottomSheetDialogFragment implements View.OnClickListener {

    private static final String TAG = "FilterDialog";
    private Activity mContext;
    //private List<String> subjectList;
    private FilterClickListener filterClickListener;
    private DialogFilterBinding mBinding;
    //private HashMap<Integer, Chip> mapSubjectChips = new HashMap();
    private ApiInterface apiService = ApiClient.getInstance().getApi();
    private HashMap<String, String> params = new HashMap<>();
    //private String avgRating = "", strDiffLevel = "", subject = "";

    public FilterDialog(@NonNull Activity mContext, HashMap<String, String> params, FilterClickListener filterClickListener) {
        this.mContext = mContext;
        //this.subjectList = subjectList;
        this.params = params;
        this.filterClickListener = filterClickListener;
        Log.d(TAG, "FilterDialog: " + params);
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
        setFilters();

        mBinding.reset.setOnClickListener(v -> {
            mBinding.testDifficultyLevelChipGrp.clearCheck();
            mBinding.testCategoryChipGrp.clearCheck();
            mBinding.rating.setRating(0);
            params.put(Constant.tm_diff_level, "");
            params.put(Constant.tm_catg, "");
            params.put(Constant.tm_avg_rating, "");
            params.put(Constant.test_recent_popular, "");
            filterClickListener.onResetClick(params);
            dismiss();
        });

        mBinding.done.setOnClickListener(v -> {
            params.put(Constant.tm_diff_level, getSelectedChipValues(mBinding.testDifficultyLevelChipGrp));
            params.put(Constant.tm_catg, getSelectedChipValues(mBinding.testCategoryChipGrp));
            params.put(Constant.test_recent_popular, getSelectedChipValues(mBinding.testRecentPopularChipGrp));
            if(getSelectedChipValues(mBinding.testRecentPopularChipGrp).contains("P"))
                params.put(Constant.tm_popular_test, "1");
            else
                params.put(Constant.tm_popular_test, "");

            if(getSelectedChipValues(mBinding.testRecentPopularChipGrp).contains("R"))
                params.put(Constant.tm_recent_test, "1");
            else
                params.put(Constant.tm_recent_test, "");

            filterClickListener.onDoneClick(params);
            dismiss();
        });

       /* mBinding.testDifficultyLevelChipGrp.setOnCheckedChangeListener((chipGroup, id) -> {
            Chip chip = ((Chip) chipGroup.getChildAt(chipGroup.getCheckedChipId()));
            setCheckedChip(chip);
        });*/

        mBinding.rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                Log.d(TAG, "onRatingChanged: " + rating);
                params.put(Constant.tm_avg_rating, String.valueOf(rating));
            }
        });
    }

   /* private String getApiParamValue(String data){
        String[] result = data.split(",",-1);
        for (int i = 0; i <result.length ; i++) {
            if(!result[i].isEmpty()){
                result = result +
            }
        }
    }*/

    private String getSelectedChipValues(ChipGroup chipGroup) {
        StringBuilder results = new StringBuilder();
        if (chipGroup != null) {
            int selectedChipCount = chipGroup.getChildCount();
            for (int i = 0; i < selectedChipCount; i++) {
                Chip chip = (Chip) chipGroup.getChildAt(i);
                if (chip.isChecked()) {
                    //results = results + "','" + chip.getTag().toString();
                    results.append(",'" + chip.getTag().toString() + "'");
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

    private void setFilters() {
        if (params != null) {
            String avgRating = params.get(Constant.tm_avg_rating);
            String diffLevel = params.get(Constant.tm_diff_level);
            String category = params.get(Constant.tm_catg);
            String recentPopular = params.get(Constant.test_recent_popular);
            if (avgRating != null && !avgRating.isEmpty()) {
                mBinding.rating.setRating(Float.parseFloat(avgRating));
            }
            if (diffLevel != null && !diffLevel.isEmpty()) {
                String[] diff = diffLevel.split(",", -1);
                for (String strDiff : diff) {
                    if (!strDiff.isEmpty()) {
                        if (strDiff.equalsIgnoreCase("E"))
                            mBinding.chipEasy.setChecked(true);
                        if (strDiff.equalsIgnoreCase("M"))
                            mBinding.chipMedium.setChecked(true);
                        if (strDiff.equalsIgnoreCase("H"))
                            mBinding.chipHard.setChecked(true);
                    }
                }
            }

            if (recentPopular != null && !recentPopular.isEmpty()) {
                String[] recentPoplr = recentPopular.split(",", -1);
                for (String strData : recentPoplr) {
                    if (!strData.isEmpty()) {
                        if (strData.equalsIgnoreCase("R"))
                            mBinding.chipRecent.setChecked(true);
                        if (strData.equalsIgnoreCase("P"))
                            mBinding.chipPopular.setChecked(true);
                    }
                }
            }
            if (category != null && !category.isEmpty()) {
                String[] categories = category.split(",", -1);
                for (String categ : categories) {
                    Log.d(TAG, "categories Checked : " + categ);
                    if (!categ.isEmpty()) {
                        if (categ.equalsIgnoreCase("U"))
                            mBinding.chipUnitTest.setChecked(true);
                        if (categ.equalsIgnoreCase("S"))
                            mBinding.chipSemester.setChecked(true);
                        if (categ.equalsIgnoreCase("A"))
                            mBinding.chipAnnual.setChecked(true);
                    }
                }
            }
        }
    }

   /* private void setCheckedChip(Chip chip) {
        if (chip != null) {
            if (chip.isChecked()) {
                chip.setTextAppearanceResource(R.style.ChipTextStyleActive);
                //chip.setTextColor(Color.WHITE);
            } else {
                chip.setTextAppearanceResource(R.style.ChipTextStyle);
                //chip.setTextColor(Color.BLACK);
            }
        }
    }*/

//    private void prepareSubjectChips(List<String> subjectList) {
//        mBinding.subjectsChipGrp.removeAllViews();
//        for (int i = 0; i < subjectList.size(); i++) {
//            Chip chip = (Chip) LayoutInflater.from(mBinding.subjectsChipGrp.getContext()).inflate(R.layout.chip_layout, mBinding.subjectsChipGrp, false);
//            chip.setText(subjectList.get(i));
//            chip.setTag("Subjects");
//            chip.setId(i);
//            mapSubjectChips.put(i, chip);
//            chip.setOnClickListener(this);
//            chip.setClickable(true);
//            chip.setCheckable(true);
//            mBinding.subjectsChipGrp.addView(chip);
//        }
//    }

//    private void prepareTestCategoryChips() {
//        String[] defCat = {"U", "S", "A"};
//        List categoryList = new ArrayList();
//        categoryList.add("Unit Test-Practice");
//        categoryList.add("Semester-Practice");
//        categoryList.add("Annual-Practice");
////        categoryList.add("Unit Test-Final");
////        categoryList.add("Semester-Final");
////        categoryList.add("Annual-Final");
//
//        mBinding.testCategoryChipGrp.removeAllViews();
//        for (int i = 0; i < categoryList.size(); i++) {
//            Chip chip = (Chip) LayoutInflater.from(mBinding.testCategoryChipGrp.getContext()).inflate(R.layout.chip_layout, mBinding.testCategoryChipGrp, false);
//            chip.setText(categoryList.get(i).toString());
//            chip.setTag(defCat[i]);
//            chip.setId(i);
//            chip.setClickable(true);
//            chip.setCheckable(true);
//            mBinding.testCategoryChipGrp.addView(chip);
//        }
//    }

//    private void prepareDiffLevelChips() {
//        String[] diffLevel = {"E", "M", "H"};
//        strDiffLevel = params.get(Constant.tm_diff_level);
//        List diffLevelList = new ArrayList();
//        diffLevelList.add("Easy");
//        diffLevelList.add("Medium");
//        diffLevelList.add("Hard");
//
//        mBinding.testDifficultyLevelChipGrp.removeAllViews();
//        for (int i = 0; i < diffLevelList.size(); i++) {
//            Chip chip = (Chip) LayoutInflater.from(mBinding.testDifficultyLevelChipGrp.getContext()).inflate(R.layout.chip_layout, mBinding.testDifficultyLevelChipGrp, false);
//            chip.setText(diffLevelList.get(i).toString());
//            chip.setId(i);
//            chip.setTag(diffLevel[i]);
//            chip.setClickable(true);
//            chip.setCheckable(true);
//            if (strDiffLevel != null && strDiffLevel.equalsIgnoreCase("E") && i == 0)
//                chip.setChecked(true);
//            if (strDiffLevel != null && strDiffLevel.equalsIgnoreCase("M") && i == 1)
//                chip.setChecked(true);
//            if (strDiffLevel != null && strDiffLevel.equalsIgnoreCase("H") && i == 2)
//                chip.setChecked(true);
//            mBinding.testDifficultyLevelChipGrp.addView(chip);
//        }
//    }

    @Override
    public void onClick(View v) {

    }

    public interface FilterClickListener {
        void onResetClick(HashMap<String, String> params);
        void onDoneClick(HashMap<String, String> params);
    }
}
