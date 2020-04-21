package com.jangletech.qoogol.ui.test.filter;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.androidbuts.multispinnerfilter.KeyPairBoolData;
import com.androidbuts.multispinnerfilter.SpinnerListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.TestFilterFragmentBinding;
import com.jangletech.qoogol.ui.BaseFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class TestFilterFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "TestFilterFragment";
    private TestFilterViewModel mViewModel;
    private TestFilterFragmentBinding mBinding;
    private HashMap<Integer, Chip> mapSubjectChips = new HashMap();
    private HashMap<Integer, Chip> mapChapterChips = new HashMap();
    private HashMap<Integer, Chip> mapRatingsChips = new HashMap();

    public static TestFilterFragment newInstance() {
        return new TestFilterFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.test_filter_fragment, container, false);
        initView();
        return mBinding.getRoot();
    }

    private void initView() {
        Bundle bundle = getArguments();
        if (bundle!=null) {
            if (bundle.getString("call_from").equalsIgnoreCase("learning")) {
                mBinding.chapterLayout.setVisibility(View.VISIBLE);
                mBinding.ratingLayout.setVisibility(View.VISIBLE);
            } else if (bundle.getString("call_from").equalsIgnoreCase("test")) {
                mBinding.autherLayout.setVisibility(View.VISIBLE);
                mBinding.testcatLayout.setVisibility(View.VISIBLE);
                mBinding.durationLayout.setVisibility(View.VISIBLE);
                mBinding.totalMarksLayout.setVisibility(View.VISIBLE);
            }
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(TestFilterViewModel.class);

        prepareSubjectChips();
        prepareChapterChips();
        prepareRatingChips();
        prepareTestCategoryChips();
        prepareDiffLevelChips();

        mBinding.btnApply.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "Filters Applied", Toast.LENGTH_SHORT).show();
            getActivity().onBackPressed();
        });

        List<String> list = Arrays.asList(getResources().getStringArray(R.array.author));
        List<KeyPairBoolData> listArray0 = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            KeyPairBoolData h = new KeyPairBoolData();
            h.setId(i + 1);
            h.setName(list.get(i));
            h.setSelected(false);
            listArray0.add(h);
        }

        mBinding.spinnerSearchAuthor.setItems(listArray0, -1, new SpinnerListener() {

            @Override
            public void onItemsSelected(List<KeyPairBoolData> items) {

                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).isSelected()) {
                        Log.i(TAG, i + " : " + items.get(i).getName() + " : " + items.get(i).isSelected());
                    }
                }
            }
        });

        mBinding.testCategoryChipGrp.setOnCheckedChangeListener((chipGroup, id) -> {
            Chip chip = ((Chip) chipGroup.getChildAt(chipGroup.getCheckedChipId()));
            if (chip != null) {
                if (chip.isChecked()) {
                    setCheckedChip(mBinding.testCategoryChipGrp);
                }
            }
        });

        mBinding.testDifficultyLevelChipGrp.setOnCheckedChangeListener((chipGroup, id) -> {
            Chip chip = ((Chip) chipGroup.getChildAt(chipGroup.getCheckedChipId()));
            if (chip != null) {
                if (chip.isChecked()) {
                    setCheckedChip(mBinding.testDifficultyLevelChipGrp);
                }
            }
        });
    }


    private void prepareRatingChips() {
        List subjectList = new ArrayList();
        subjectList.add("All");
        subjectList.add("1");
        subjectList.add("1.5");
        subjectList.add("2");
        subjectList.add("2.5");
        subjectList.add("3");
        subjectList.add("3.5");
        subjectList.add("4");
        subjectList.add("4.5");
        subjectList.add("5");

        mBinding.ratingChipGrp.removeAllViews();
        for (int i = 0; i < subjectList.size(); i++) {
            Chip chip = (Chip) LayoutInflater.from(mBinding.ratingChipGrp.getContext()).inflate(R.layout.chip_layout, mBinding.ratingChipGrp, false);
            chip.setText(subjectList.get(i).toString());
            chip.setTag("Ratings");
            chip.setId(i);
            if (i == 0) {
                chip.setChecked(true);
            }
            chip.setClickable(true);
            chip.setCheckable(true);
            mBinding.ratingChipGrp.addView(chip);
        }
    }

    private void prepareTestCategoryChips() {
        List subjectList = new ArrayList();
        subjectList.add("All");
        subjectList.add("Unit Test-Practice");
        subjectList.add("Semester-Practice");
        subjectList.add("Annual-Practice");
        subjectList.add("Unit Test-Final");
        subjectList.add("Semester-Final");
        subjectList.add("Annual-Final");

        mBinding.testCategoryChipGrp.removeAllViews();
        for (int i = 0; i < subjectList.size(); i++) {
            Chip chip = (Chip) LayoutInflater.from(mBinding.testCategoryChipGrp.getContext()).inflate(R.layout.chip_layout, mBinding.testCategoryChipGrp, false);
            chip.setText(subjectList.get(i).toString());
            chip.setTag("Subjects");
            chip.setId(i);
            if (i == 0) {
                chip.setChecked(true);
            }
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

        mBinding.testDifficultyLevelChipGrp.removeAllViews();
        for (int i = 0; i < diffLevelList.size(); i++) {
            Chip chip = (Chip) LayoutInflater.from(mBinding.testDifficultyLevelChipGrp.getContext()).inflate(R.layout.chip_layout, mBinding.testDifficultyLevelChipGrp, false);
            chip.setText(diffLevelList.get(i).toString());
            chip.setTag("Difficulty");
            chip.setId(i);
            if (i == 0)
                chip.setChecked(true);
            chip.setClickable(true);
            chip.setCheckable(true);
            mBinding.testDifficultyLevelChipGrp.addView(chip);
        }
    }

    private void prepareChapterChips() {
        List subjectList = new ArrayList();
        subjectList.add("All");
        subjectList.add("Current");
        subjectList.add("The Solid State.");
        subjectList.add("Solutions");
        subjectList.add("Solutions");
        subjectList.add("Chemical Kinetics");
        subjectList.add("Surface Chemistry");

        mBinding.chapterChipGrp.removeAllViews();
        for (int i = 0; i < subjectList.size(); i++) {
            Chip chip = (Chip) LayoutInflater.from(mBinding.chapterChipGrp.getContext()).inflate(R.layout.chip_layout, mBinding.chapterChipGrp, false);
            chip.setText(subjectList.get(i).toString());
            chip.setTag("Chapters");
            chip.setId(i);
            if (i == 0)
                chip.setChecked(true);
            mapChapterChips.put(i, chip);
            chip.setOnClickListener(this);
            chip.setClickable(true);
            chip.setCheckable(true);
            mBinding.chapterChipGrp.addView(chip);
        }
    }

    private void prepareSubjectChips() {
        List subjectList = new ArrayList();
        subjectList.add("All");
        subjectList.add("Physics");
        subjectList.add("Mathematics");
        subjectList.add("Chemistry");
        subjectList.add("English");
        subjectList.add("Networking");
        subjectList.add("DBMS");
        subjectList.add("Engineering Drawing");

        mBinding.subjectsChipGrp.removeAllViews();
        for (int i = 0; i < subjectList.size(); i++) {
            Chip chip = (Chip) LayoutInflater.from(mBinding.subjectsChipGrp.getContext()).inflate(R.layout.chip_layout, mBinding.subjectsChipGrp, false);
            chip.setText(subjectList.get(i).toString());
            chip.setTag("Subjects");
            chip.setId(i);
            if (i == 0)
                chip.setChecked(true);
            mapSubjectChips.put(i, chip);
            chip.setOnClickListener(this);
            chip.setClickable(true);
            chip.setCheckable(true);
            mBinding.subjectsChipGrp.addView(chip);
        }
    }

    private void setSelectedSubjectsChips(Chip chip) {
        showToast("Selected : " + chip.getText().toString());
        Chip selectedChip = mapSubjectChips.put(chip.getId(), chip);
        for (int i = 0; i < mapSubjectChips.size(); i++) {
            if (mapSubjectChips.get(i).isChecked()) {
                mapSubjectChips.get(i).setTextColor(Color.WHITE);
            } else {
                mapSubjectChips.get(i).setTextColor(Color.BLACK);
            }
        }
    }

    private void setSelectedChapterChips(Chip chip) {
        showToast("Selected : " + chip.getText().toString());
        Chip selectedChip = mapChapterChips.put(chip.getId(), chip);
        for (int i = 0; i < mapChapterChips.size(); i++) {
            if (mapChapterChips.get(i).isChecked()) {
                mapChapterChips.get(i).setTextColor(Color.WHITE);
            } else {
                mapChapterChips.get(i).setTextColor(Color.BLACK);
            }
        }
    }

    private void setSelectedRatingChips(Chip chip) {
        showToast("Selected : " + chip.getText().toString());
        Chip selectedChip = mapRatingsChips.put(chip.getId(), chip);
        for (int i = 0; i < mapRatingsChips.size(); i++) {
            if (mapRatingsChips.get(i).isChecked()) {
                mapRatingsChips.get(i).setTextColor(Color.WHITE);
            } else {
                mapRatingsChips.get(i).setTextColor(Color.BLACK);
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

    @Override
    public void onClick(View v) {
        if (v != null) {
            if (v.getTag().toString().equalsIgnoreCase("Subjects")) {
                Chip chip = (Chip) v;
                setSelectedSubjectsChips(chip);
            } else  if (v.getTag().toString().equalsIgnoreCase("Chapters")) {
                Chip chip = (Chip) v;
                setSelectedChapterChips(chip);
            } else  if (v.getTag().toString().equalsIgnoreCase("Ratings")) {
                Chip chip = (Chip) v;
                setSelectedRatingChips(chip);
            }
        }
    }
}
