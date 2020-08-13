package com.jangletech.qoogol.ui.test.filter;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.TestFilterFragmentBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.model.Chapter;
import com.jangletech.qoogol.model.ChapterResponse;
import com.jangletech.qoogol.model.FetchSubjectResponse;
import com.jangletech.qoogol.model.FetchSubjectResponseList;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.ui.test.my_test.MyTestViewModel;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TestFilterFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "TestFilterFragment";
    private MyTestViewModel mViewModel;
    private TestFilterFragmentBinding mBinding;
    private HashMap<Integer, Chip> mapSubjectChips = new HashMap();
    private HashMap<Integer, Chip> mapChapterChips = new HashMap();
    private HashMap<Integer, Chip> mapRatingsChips = new HashMap();
    private HashMap<Integer, Chip> mapTypeChips = new HashMap();
    private HashMap<Integer, Chip> mapQueCategoryChips = new HashMap();
    private ApiInterface apiService = ApiClient.getInstance().getApi();
    private PreferenceManager mSettings;
    private String diffLevel = "";
    private HashMap<String, String> params;
    private HashMap<String, String> mDiffMap = new HashMap<>();
    private HashMap<String, String> mCategoryMap = new HashMap<>();
    private Set subjectset = new HashSet<String>();
    private Set chapterset = new HashSet<String>();
    private Set ratingset = new HashSet<String>();
    private Set typeset = new HashSet<String>();
    private Set que_categoryset = new HashSet<String>();
    private String call_from = "";

    public static TestFilterFragment newInstance() {
        return new TestFilterFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        params = new HashMap<>();

        //Difficulty Map
        mDiffMap.put("Hard", "H");
        mDiffMap.put("Medium", "M");
        mDiffMap.put("Easy", "E");

        //CategoryMap
        mCategoryMap.put("Unit Test-Practice", "");
        mCategoryMap.put("Semester-Practice", "");
        mCategoryMap.put("Annual-Practice", "");
        mCategoryMap.put("Unit Test-Final", "");
        mCategoryMap.put("Semester-Final", "");
        mCategoryMap.put("Annual-Final", "");

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.test_filter_fragment, container, false);
        initView();
        return mBinding.getRoot();
    }

    private void initView() {
        mSettings = new PreferenceManager(getActivity());
        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.getString("call_from").equalsIgnoreCase("learning")) {
                mBinding.chapterLayout.setVisibility(View.VISIBLE);
                mBinding.queTypeLayout.setVisibility(View.VISIBLE);
                mBinding.queCategoryLayout.setVisibility(View.VISIBLE);
                call_from = "learning";
            } else if (bundle.getString("call_from").equalsIgnoreCase("test")) {
                //mBinding.autherLayout.setVisibility(View.VISIBLE);
                mBinding.testcatLayout.setVisibility(View.VISIBLE);
                //mBinding.durationLayout.setVisibility(View.VISIBLE);
                //mBinding.totalMarksLayout.setVisibility(View.VISIBLE);
                call_from = "test";
            }
        }
        if (getString(Constant.tm_avg_rating) != null && !getString(Constant.tm_avg_rating).isEmpty())
            mBinding.rating.setRating(Float.parseFloat(getString(Constant.tm_avg_rating)));
    }

    private void fetchSubjectList(String scrCoId) {
        mBinding.swipeToRefresh.setRefreshing(true);
        Call<FetchSubjectResponseList> call = apiService.fetchSubjectList(Constant.SCR_CO_ID);
        call.enqueue(new Callback<FetchSubjectResponseList>() {
            @Override
            public void onResponse(Call<FetchSubjectResponseList> call, Response<FetchSubjectResponseList> response) {
                //ProgressDialog.getInstance().dismiss();
                mBinding.swipeToRefresh.setRefreshing(false);
                mViewModel.setAllSubjectList(response.body().getFetchSubjectResponseList());
            }

            @Override
            public void onFailure(Call<FetchSubjectResponseList> call, Throwable t) {
                //ProgressDialog.getInstance().dismiss();
                mBinding.swipeToRefresh.setRefreshing(false);
                showToast("Something went wrong!!");
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MyTestViewModel.class);

        if (mViewModel.getAllSubjects().getValue() == null) {
            fetchSubjectList(getString(Constant.scr_co_id));
        }
        fetchchapterList();
        mViewModel.getAllSubjects().observe(getViewLifecycleOwner(), subjects -> {
            if (subjects != null) {
                Log.d(TAG, "onChanged Subjects Size : " + subjects.size());
                ArrayList<String> subjectList = new ArrayList<>();
                for (FetchSubjectResponse obj : subjects) {
                    if (!subjectList.contains(obj.getSm_sub_name()))
                        subjectList.add(obj.getSm_sub_name());
                }
                prepareSubjectChips(subjectList);
            }
        });

        mBinding.reset.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity(), R.style.AlertDialogStyle);
            builder.setTitle("Confirm")
                    .setMessage("Are you sure, you want to reset filters?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        clearFilters();
                        mBinding.rating.setRating(0);
                        mBinding.testDifficultyLevelChipGrp.clearCheck();
                        mBinding.subjectsChipGrp.clearCheck();
                        mBinding.chapterChipGrp.clearCheck();
                        mBinding.testCategoryChipGrp.clearCheck();
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        mViewModel.getAllChapter().observe(getActivity(), chapters -> {
            Log.d(TAG, "onChanged Subjects Size : " + chapters.size());
            prepareChapterChips(chapters);
        });

        prepareTypeChips();
        prepareQueCategory();
        prepareTestCategoryChips();
        prepareDiffLevelChips();

        mBinding.rating.setOnRatingBarChangeListener((ratingBar, rating, fromUser) -> {
            Log.d(TAG, "onRatingChanged: " + rating);
            if (rating == 0) {
                saveString(getActivity(),Constant.tm_avg_rating, "");
            } else {
                params.put(Constant.tm_avg_rating, String.valueOf(rating));
                saveString(getActivity(),Constant.tm_avg_rating, String.valueOf(rating));
            }
        });

        mBinding.btnApply.setOnClickListener(v -> {
            try {
                //Toast.makeText(getActivity(), "Filters Applied", Toast.LENGTH_SHORT).show();
                if (subjectset != null && subjectset.size() > 0)
                    mSettings.setSubjectFilter(subjectset);

                if (chapterset != null && chapterset.size() > 0)
                    mSettings.setChapterFilter(chapterset);



                if (typeset != null && typeset.size() > 0)
                    mSettings.setTypeFilter(typeset);

                if (que_categoryset != null && que_categoryset.size() > 0)
                    mSettings.setQueCategoryFilter(que_categoryset);

                Bundle bundle = new Bundle();

                if (call_from.equalsIgnoreCase("test")) {
                    bundle.putSerializable("PARAMS", params);
                    //bundle.putString(Constant.tm_diff_level, diffLevel);
                    Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.nav_test_my, bundle);
                    //MainActivity.navController.navigate(R.id.nav_test_my);
                } else {
                    bundle.putString("call_from", "learning");
                    Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.nav_learning, bundle);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

//        List<String> list = Arrays.asList(getResources().getStringArray(R.array.author));
//        List<KeyPairBoolData> listArray0 = new ArrayList<>();
//
//        for (int i = 0; i < list.size(); i++) {
//            KeyPairBoolData h = new KeyPairBoolData();
//            h.setId(i + 1);
//            h.setName(list.get(i));
//            h.setSelected(false);
//            listArray0.add(h);
//        }

//        mBinding.spinnerSearchAuthor.setItems(listArray0, -1, new SpinnerListener() {
//
//            @Override
//            public void onItemsSelected(List<KeyPairBoolData> items) {
//
//                for (int i = 0; i < items.size(); i++) {
//                    if (items.get(i).isSelected()) {
//                        Log.i(TAG, i + " : " + items.get(i).getName() + " : " + items.get(i).isSelected());
//                    }
//                }
//            }
//        });

        mBinding.testCategoryChipGrp.setOnCheckedChangeListener((chipGroup, id) -> {
            Chip chip = ((Chip) chipGroup.getChildAt(chipGroup.getCheckedChipId()));
            if (chip != null) {
                if (chip.isChecked()) {
                    setCheckedChip(mBinding.testCategoryChipGrp);
                    saveString(getActivity(),Constant.tm_catg, mCategoryMap.get(chip.getText()));
                } else {
                    saveString(getActivity(),Constant.tm_catg, "");
                    setCheckedChip(mBinding.testCategoryChipGrp);
                }
            }
        });

        mBinding.chapterChipGrp.setOnCheckedChangeListener((chipGroup, id) -> {
            Chip chip = ((Chip) chipGroup.getChildAt(chipGroup.getCheckedChipId()));
            if (chip != null) {
                if (chip.isChecked()) {
                    setCheckedChip(mBinding.chapterChipGrp);
                } else {
                    setCheckedChip(mBinding.chapterChipGrp);
                }
            }
        });

        mBinding.testDifficultyLevelChipGrp.setOnCheckedChangeListener((chipGroup, id) -> {
            Chip chip = ((Chip) chipGroup.getChildAt(chipGroup.getCheckedChipId()));
            if (chip != null) {
                //showToast(chip.getText().toString());
                if (chip.isChecked()) {
                    if (chip.getText().equals("Easy"))
                        diffLevel = "E";
                    if (chip.getText().equals("Hard"))
                        diffLevel = "H";
                    if (chip.getText().equals("Medium"))
                        diffLevel = "M";

                    params.put(Constant.tm_diff_level, diffLevel);
                    saveString(getActivity(),Constant.tm_diff_level, diffLevel);
                    setCheckedChip(mBinding.testDifficultyLevelChipGrp);
                }
            } else {
                saveString(getActivity(),Constant.tm_diff_level, "");
                setCheckedChip(mBinding.testDifficultyLevelChipGrp);
            }
        });
    }

    private void prepareQueCategory() {
        if (mSettings.getQueCategoryFilter() != null) {
            que_categoryset = mSettings.getQueCategoryFilter();
        }
        List que_categoryList = new ArrayList();
        que_categoryList.add("SCQ");
        que_categoryList.add("MCQ");
        que_categoryList.add("Fill in the Blanks");
        que_categoryList.add("True False");
        que_categoryList.add("Match the Pair");
        que_categoryList.add("Short Answer");
        que_categoryList.add("Long Answer");

        mBinding.queCategoryChipGrp.removeAllViews();
        for (int i = 0; i < que_categoryList.size(); i++) {
            Chip chip = (Chip) LayoutInflater.from(mBinding.queCategoryChipGrp.getContext()).inflate(R.layout.chip_layout, mBinding.queCategoryChipGrp, false);
            chip.setText(que_categoryList.get(i).toString());
            chip.setTag("Question_Category");
            chip.setId(i);
            if (que_categoryset.contains(que_categoryList.get(i).toString()))
                chip.setChecked(true);
            chip.setClickable(true);
            chip.setCheckable(true);
            mapQueCategoryChips.put(i, chip);
            chip.setOnClickListener(this);
            mBinding.queCategoryChipGrp.addView(chip);
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
            chip.setCheckable(true);
            mapTypeChips.put(i, chip);
            chip.setOnClickListener(this);
            mBinding.queTypeChipGrp.addView(chip);
        }
    }

    private void fetchchapterList() {
        ProgressDialog.getInstance().show(getActivity());
        Call<ChapterResponse> call = apiService.fetchChapterList(new PreferenceManager(getActivity()).getInt(Constant.USER_ID));//todo change userId
        call.enqueue(new Callback<ChapterResponse>() {
            @Override
            public void onResponse(Call<ChapterResponse> call, Response<ChapterResponse> response) {
                ProgressDialog.getInstance().dismiss();
                mViewModel.setAllChapterList(response.body().getChapterList());
            }

            @Override
            public void onFailure(Call<ChapterResponse> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                t.printStackTrace();
            }
        });
    }

    /*private void setFilters(){
        if(getString(Constant.tm_diff_level)!=null
                && !getString(Constant.tm_diff_level).isEmpty()){
            String strDiff = getString(Constant.tm_diff_level);
            for (int i = 0; i <mBinding.testDifficultyLevelChipGrp.getChildCount() ; i++) {
                Chip chip = mBinding.
            }
        }
    }*/



    private void prepareTestCategoryChips() {
        List categoryList = new ArrayList();
        categoryList.add("Unit Test-Practice");
        categoryList.add("Semester-Practice");
        categoryList.add("Annual-Practice");
        categoryList.add("Unit Test-Final");
        categoryList.add("Semester-Final");
        categoryList.add("Annual-Final");

        mBinding.testCategoryChipGrp.removeAllViews();
        for (int i = 0; i < categoryList.size(); i++) {
            Chip chip = (Chip) LayoutInflater.from(mBinding.testCategoryChipGrp.getContext()).inflate(R.layout.chip_layout, mBinding.testCategoryChipGrp, false);
            chip.setText(categoryList.get(i).toString());
            chip.setTag("Categories");
            chip.setId(i);
            chip.setClickable(true);
            chip.setCheckable(true);
            mBinding.testCategoryChipGrp.addView(chip);
        }
    }

    private void prepareDiffLevelChips() {
        List diffLevelList = new ArrayList();
        diffLevelList.add("Easy");
        diffLevelList.add("Medium");
        diffLevelList.add("Hard");

        String strDiffLevel = getString(Constant.tm_diff_level);

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

    private void prepareChapterChips(List<Chapter> chapters) {
        try {
            Set chapterset = new HashSet<String>();
            if (mSettings.getChapterFilter() != null) {
                chapterset = mSettings.getChapterFilter();
            }
            mBinding.chapterChipGrp.removeAllViews();
            for (int i = 0; i < chapters.size(); i++) {
                Chip chip = (Chip) LayoutInflater.from(mBinding.chapterChipGrp.getContext()).inflate(R.layout.chip_layout, mBinding.chapterChipGrp, false);
                chip.setText(chapters.get(i).getChapter_name());
                chip.setTag(chapters.get(i).getChapter_id());
                chip.setId(i);
                chip.setOnClickListener(this);
                chip.setClickable(true);
                chip.setCheckable(true);
                mBinding.chapterChipGrp.addView(chip);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void setSelectedSubjectsChips(Chip chip) {
        try {
            subjectset.clear();
            if (mSettings.getSubjectFilter() != null) {
                subjectset = mSettings.getSubjectFilter();
            }
            for (int i = 0; i < mapSubjectChips.size(); i++) {
                String id = String.valueOf(mapSubjectChips.get(i).getId());
                if (mapSubjectChips.get(i).isChecked()) {
                    mapSubjectChips.get(i).setTextColor(Color.WHITE);
                    if (!subjectset.contains(id))
                        subjectset.add(id);
                } else {
                    mapSubjectChips.get(i).setTextColor(Color.BLACK);
                    if (subjectset.contains(id))
                        subjectset.remove(id);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setSelectedChapterChips(Chip chip) {
        try {
            chapterset.clear();
            if (mSettings.getSubjectFilter() != null) {
                chapterset = mSettings.getChapterFilter();
            }
            for (int i = 0; i < mapChapterChips.size(); i++) {
                String id = String.valueOf(mapChapterChips.get(i).getId());
                if (mapChapterChips.get(i).isChecked()) {
                    mapChapterChips.get(i).setTextColor(Color.WHITE);
                    if (!chapterset.contains(id))
                        chapterset.add(id);
                } else {
                    mapChapterChips.get(i).setTextColor(Color.BLACK);
                    if (chapterset.contains(id))
                        chapterset.remove(id);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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


    @Override
    public void onClick(View v) {
        if (v != null) {
            if (v.getTag().toString().equalsIgnoreCase("Subjects")) {
                Chip chip = (Chip) v;
                setSelectedSubjectsChips(chip);
            } else if (v.getTag().toString().equalsIgnoreCase("Chapters")) {
                Chip chip = (Chip) v;
                setSelectedChapterChips(chip);
            } else if (v.getTag().toString().equalsIgnoreCase("Ratings")) {
                Chip chip = (Chip) v;
            } else if (v.getTag().toString().equalsIgnoreCase("Type")) {
                Chip chip = (Chip) v;
                setSelecteTypeChips(chip);
            } else if (v.getTag().toString().equalsIgnoreCase("Question_Category")) {
                Chip chip = (Chip) v;
                setSelecteQueCategoryChips(chip);
            }
        }
    }

    private void setSelecteQueCategoryChips(Chip chip) {
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

    private void setSelecteTypeChips(Chip chip) {
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


}
