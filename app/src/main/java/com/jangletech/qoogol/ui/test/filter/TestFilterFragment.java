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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.androidbuts.multispinnerfilter.KeyPairBoolData;
import com.androidbuts.multispinnerfilter.SpinnerListener;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.activities.MainActivity;
import com.jangletech.qoogol.databinding.TestFilterFragmentBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.model.Chapter;
import com.jangletech.qoogol.model.ChapterResponse;
import com.jangletech.qoogol.model.Contacts;
import com.jangletech.qoogol.model.FetchSubjectResponse;
import com.jangletech.qoogol.model.FetchSubjectResponseList;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.ui.test.my_test.MyTestViewModel;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;

import java.lang.reflect.Type;
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
    ApiInterface apiService = ApiClient.getInstance().getApi();
    private PreferenceManager mSettings;
    Set subjectset = new HashSet<String>();
    Set chapterset = new HashSet<String>();
    Set ratingset = new HashSet<String>();
    String call_from="";

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
        mSettings = new PreferenceManager(getActivity());
        Bundle bundle = getArguments();
        if (bundle!=null) {
            if (bundle.getString("call_from").equalsIgnoreCase("learning")) {
                mBinding.chapterLayout.setVisibility(View.VISIBLE);
                mBinding.ratingLayout.setVisibility(View.VISIBLE);
                call_from = "learning";
            } else if (bundle.getString("call_from").equalsIgnoreCase("test")) {
                mBinding.autherLayout.setVisibility(View.VISIBLE);
                mBinding.testcatLayout.setVisibility(View.VISIBLE);
                mBinding.durationLayout.setVisibility(View.VISIBLE);
                mBinding.totalMarksLayout.setVisibility(View.VISIBLE);
                call_from = "test";
            }
        }
    }

    private void fetchSubjectList() {
        ProgressDialog.getInstance().show(getActivity());
        Call<FetchSubjectResponseList> call = apiService.fetchSubjectList("");//todo change userId
        call.enqueue(new Callback<FetchSubjectResponseList>() {
            @Override
            public void onResponse(Call<FetchSubjectResponseList> call, Response<FetchSubjectResponseList> response) {
                ProgressDialog.getInstance().dismiss();
                mViewModel.setAllSubjectList(response.body().getFetchSubjectResponseList());
            }

            @Override
            public void onFailure(Call<FetchSubjectResponseList> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MyTestViewModel.class);
        fetchSubjectList();
        fetchchapterList();


        mViewModel.getAllSubjects().observe(getActivity(), subjects -> {
            Log.d(TAG, "onChanged Subjects Size : " + subjects.size());
            prepareSubjectChips(subjects);
        });


        mViewModel.getAllChapter().observe(getActivity(), chapters -> {
            Log.d(TAG, "onChanged Subjects Size : " + chapters.size());
            prepareChapterChips(chapters);
        });

        prepareRatingChips();
        prepareTestCategoryChips();
        prepareDiffLevelChips();

        mBinding.btnApply.setOnClickListener(v -> {
            try {
                Toast.makeText(getActivity(), "Filters Applied", Toast.LENGTH_SHORT).show();
                if (subjectset!=null && subjectset.size()>0)
                    mSettings.setSubjectFilter(subjectset);

                if (chapterset!=null && chapterset.size()>0)
                    mSettings.setChapterFilter(chapterset);

                if (ratingset != null && ratingset.size()>0)
                    mSettings.setRatingsFilter(ratingset);

                if (call_from.equalsIgnoreCase("test")) {
                    Navigation.findNavController(requireActivity(),R.id.nav_host_fragment).navigate(R.id.nav_test_my);
                    //MainActivity.navController.navigate(R.id.nav_test_my);
                } else  {
                    Bundle bundle = new Bundle();
                    bundle.putString("call_from", "learning");
                    Navigation.findNavController(requireActivity(),R.id.nav_host_fragment).navigate(R.id.nav_learning,bundle);
                    //MainActivity.navController.navigate(R.id.nav_learning,bundle);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

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


    private void prepareRatingChips() {
        Set ratingset = new HashSet<String>();
        if (mSettings.getRatingsFilter()!=null) {
            ratingset = mSettings.getRatingsFilter();
        }
        List ratingList = new ArrayList();
        ratingList.add("1");
        ratingList.add("1.5");
        ratingList.add("2");
        ratingList.add("2.5");
        ratingList.add("3");
        ratingList.add("3.5");
        ratingList.add("4");
        ratingList.add("4.5");
        ratingList.add("5");

        mBinding.ratingChipGrp.removeAllViews();
        for (int i = 0; i < ratingList.size(); i++) {
            Chip chip = (Chip) LayoutInflater.from(mBinding.ratingChipGrp.getContext()).inflate(R.layout.chip_layout, mBinding.ratingChipGrp, false);
            chip.setText(ratingList .get(i).toString());
            chip.setTag("Ratings");
            chip.setId(i);
            if (ratingset.contains(ratingList.get(i).toString()))
                chip.setChecked(true);
            chip.setClickable(true);
            chip.setCheckable(true);
            mapRatingsChips.put(i,chip);
            chip.setOnClickListener(this);
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
            chip.setChecked(true);
            chip.setClickable(true);
            chip.setCheckable(true);
            mBinding.testDifficultyLevelChipGrp.addView(chip);
        }
    }

    private void prepareChapterChips(List<Chapter> chapters) {
        try {
            Set chapterset = new HashSet<String>();
            if (mSettings.getChapterFilter()!=null) {
                chapterset = mSettings.getChapterFilter();
            }
            mBinding.chapterChipGrp.removeAllViews();
            for (int i = 0; i < chapters.size(); i++) {
                Chip chip = (Chip) LayoutInflater.from(mBinding.chapterChipGrp.getContext()).inflate(R.layout.chip_layout, mBinding.chapterChipGrp, false);
                chip.setText(chapters.get(i).getChapter_name());
                chip.setTag("Chapters");
                chip.setId(Integer.parseInt(chapters.get(i).getChapter_id()));
                if (chapterset.contains(chapters.get(i).getChapter_id()))
                    chip.setChecked(true);
                mapChapterChips.put(i, chip);
                chip.setOnClickListener(this);
                chip.setClickable(true);
                chip.setCheckable(true);
                mBinding.chapterChipGrp.addView(chip);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void prepareSubjectChips(List<FetchSubjectResponse> subjects) {
        try {
            Set subjectset = new HashSet<String>();
            if (mSettings.getSubjectFilter()!=null) {
                subjectset = mSettings.getSubjectFilter();
            }
            mBinding.subjectsChipGrp.removeAllViews();
            for (int i = 0; i < subjects.size(); i++) {
                Chip chip = (Chip) LayoutInflater.from(mBinding.subjectsChipGrp.getContext()).inflate(R.layout.chip_layout, mBinding.subjectsChipGrp, false);
                chip.setText(subjects.get(i).getSm_sub_name());
                chip.setTag("Subjects");
                chip.setId(Integer.parseInt(subjects.get(i).getScr_sm_id()));
                if (subjectset.contains(subjects.get(i).getScr_sm_id()))
                    chip.setChecked(true);
                mapSubjectChips.put(i, chip);
                chip.setOnClickListener(this);
                chip.setClickable(true);
                chip.setCheckable(true);
                mBinding.subjectsChipGrp.addView(chip);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setSelectedSubjectsChips(Chip chip) {
        try {
            subjectset.clear();
            if (mSettings.getSubjectFilter()!=null) {
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
            if (mSettings.getSubjectFilter()!=null) {
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

    private void setSelectedRatingChips(Chip chip) {
       try {
           ratingset.clear();
           if (mSettings.getRatingsFilter()!=null) {
               ratingset = mSettings.getRatingsFilter();
           }
           for (int i = 0; i < mapRatingsChips.size(); i++) {
               String text = mapRatingsChips.get(i).getText().toString();
               if (mapRatingsChips.get(i).isChecked()) {
                   mapRatingsChips.get(i).setTextColor(Color.WHITE);
                   if (!ratingset.contains(text))
                       ratingset.add(text);
               } else {
                   mapRatingsChips.get(i).setTextColor(Color.BLACK);
                   if (ratingset.contains(text))
                       ratingset.remove(text);
               }
           }
       }catch (Exception e) {
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
