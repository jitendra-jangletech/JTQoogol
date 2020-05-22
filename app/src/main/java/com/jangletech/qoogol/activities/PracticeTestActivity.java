package com.jangletech.qoogol.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.LearingAdapter;
import com.jangletech.qoogol.adapter.PracticeTestQuestPaletAdapter;
import com.jangletech.qoogol.databinding.ActivityPracticeTestBinding;
import com.jangletech.qoogol.databinding.PracticeScqImageTextBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.enums.Module;
import com.jangletech.qoogol.model.LearningQuestResponse;
import com.jangletech.qoogol.model.LearningQuestionsNew;
import com.jangletech.qoogol.model.ProcessQuestion;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.ui.practise.PractiseMcqFragment;
import com.jangletech.qoogol.ui.practise.PractiseMtpFragment;
import com.jangletech.qoogol.ui.practise.PractiseScqFragment;
import com.jangletech.qoogol.ui.practise.PractiseScqImageText;
import com.jangletech.qoogol.ui.practise.PractiseSubjectiveFragment;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.EndDrawerToggle;
import com.jangletech.qoogol.util.PreferenceManager;
import com.jangletech.qoogol.util.UtilHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

import static com.jangletech.qoogol.util.Constant.MATCH_PAIR;
import static com.jangletech.qoogol.util.Constant.SCQ;
import static com.jangletech.qoogol.util.Constant.SCQ_IMAGE;
import static com.jangletech.qoogol.util.Constant.SCQ_IMAGE_WITH_TEXT;
import static com.jangletech.qoogol.util.Constant.test;

public class PracticeTestActivity extends BaseActivity implements LearingAdapter.onIconClick, PracticeTestQuestPaletAdapter.QuestClickListener {

    private static final String TAG = "PracticeTestActivity";
    public static List<LearningQuestionsNew> questionsNewList = new ArrayList<>();
    private PracticeTestViewModel mViewModel;
    private ActivityPracticeTestBinding mBinding;
    ApiInterface apiService = ApiClient.getInstance().getApi();
    LearingAdapter learingAdapter;
    private EndDrawerToggle endDrawerToggle;
    private Toolbar toolbar;
    private ViewPager2 practiceViewPager;
    private PracticeTestQuestPaletAdapter adapter;
    int currentPos = 0;
    int pos =0;
    private boolean isQuestionSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_practice_test);
        mViewModel = new ViewModelProvider(this).get(PracticeTestViewModel.class);
        toolbar = findViewById(R.id.toolbar);
        practiceViewPager = findViewById(R.id.practice_viewpager);
        questionsNewList = new ArrayList<>();
        setupNavigationDrawer();
        setMargins(mBinding.marginLayout);
        setTitle("Practice Test");
        getPractiseTestQuestions();
        mViewModel.getPracticeQA().observe(this, new Observer<List<LearningQuestionsNew>>() {
            @Override
            public void onChanged(@Nullable final List<LearningQuestionsNew> practiceQAList) {
                questionsNewList = practiceQAList;
                practiceViewPager.setAdapter(createCardAdapter(questionsNewList));
                //setPracticeTestAdapter(questionsNewList);
            }
        });

        mBinding.appBarTest.btnNext.setOnClickListener(v -> {
            currentPos = practiceViewPager.getCurrentItem();
            if (currentPos < questionsNewList.size() - 1) {
                practiceViewPager.setCurrentItem(currentPos + 1, true);
            } else {
                Toast.makeText(this, "This is last page", Toast.LENGTH_SHORT).show();
            }
        });

        mBinding.appBarTest.btnPrevious.setOnClickListener(v -> {
            currentPos = practiceViewPager.getCurrentItem();
            if (currentPos > 0) {
                practiceViewPager.setCurrentItem(currentPos - 1, true);
            } else {
                Toast.makeText(this, "This is first Page", Toast.LENGTH_SHORT).show();
            }
        });

        practiceViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Log.d(TAG, "onPageSelected: ");
            }

        });

        practiceViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });

        mBinding.drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                prepareQuestPaletList();
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                if (isQuestionSelected) {
                    //Log.d(TAG, "Question Position : " + currentPos);
                    //Log.d(TAG, "Current Position: " + mBinding.appBarTest.practiceViewpager.getCurrentItem());
                    practiceViewPager.setCurrentItem(pos);
                    isQuestionSelected = false;
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    private void setPracticeTestAdapter(List<LearningQuestionsNew> questionsNewList) {
        Log.d(TAG, "setPracticeTestAdapter List : " + questionsNewList.size());
        learingAdapter = new LearingAdapter(PracticeTestActivity.this, questionsNewList, this, test);
        practiceViewPager.setAdapter(learingAdapter);
        //learingAdapter.updateList(questionsNewList);
    }

    private void prepareQuestPaletList() {
        Log.d(TAG, "prepareQuestPaletList Size : " + questionsNewList.size());
        mBinding.paletQuestListRecyclerView.setNestedScrollingEnabled(false);
        adapter = new PracticeTestQuestPaletAdapter(this, questionsNewList, this);
        mBinding.paletQuestListRecyclerView.setHasFixedSize(true);
        mBinding.paletQuestListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mBinding.paletQuestListRecyclerView.setAdapter(adapter);
    }

    private void getPractiseTestQuestions() {
        ProgressDialog.getInstance().show(this);
        Call<LearningQuestResponse> call = apiService.fetchQAApi(new PreferenceManager(getApplicationContext()).getInt(Constant.USER_ID));
        call.enqueue(new Callback<LearningQuestResponse>() {
            @Override
            public void onResponse(Call<LearningQuestResponse> call, retrofit2.Response<LearningQuestResponse> response) {
                try {
                    ProgressDialog.getInstance().dismiss();
                    if (response.body() != null && response.body().getResponse().equalsIgnoreCase("200")) {
                        questionsNewList = response.body().getQuestion_list();
                        mViewModel.setPracticeQAList(questionsNewList);
                    } else {
                        showErrorDialog(PracticeTestActivity.this,response.body().getResponse(),response.body().getMessage());
                        showToast(UtilHelper.getAPIError(String.valueOf(response.body())));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ProgressDialog.getInstance().dismiss();
                }
            }

            @Override
            public void onFailure(Call<LearningQuestResponse> call, Throwable t) {
                t.printStackTrace();
                ProgressDialog.getInstance().dismiss();
            }
        });
    }


    private void setupNavigationDrawer() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        endDrawerToggle = new EndDrawerToggle(this, mBinding.drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mBinding.drawerLayout.addDrawerListener(endDrawerToggle);
        endDrawerToggle.syncState();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onCommentClick(String questionId) {
        Bundle bundle = new Bundle();
        bundle.putString(Constant.CALL_FROM, Module.Learning.toString());
        bundle.putString("QuestionId", questionId);
        MainActivity.navController.navigate(R.id.nav_comments, bundle);
    }

    @Override
    public void onLikeClick(String questionId, int isLiked) {
        ProcessQuestionAPI(new PreferenceManager(getApplicationContext()).getInt(Constant.USER_ID), questionId, "I", isLiked, "like");
    }

    @Override
    public void onShareClick(String questionId) {
        Bundle bundle = new Bundle();
        bundle.putString("QuestionId", questionId);
        MainActivity.navController.navigate(R.id.nav_share, bundle);
    }

    @Override
    public void onFavouriteClick(String questionId, int isFav) {
        ProcessQuestionAPI(new PreferenceManager(getApplicationContext()).getInt(Constant.USER_ID), questionId, "I", isFav, "fav");
    }

    @Override
    public void onBackPressed() {
        if (mBinding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
            mBinding.drawerLayout.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
            finish();
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    @Override
    public void onQuestionSelected(int position) {
        Log.d(TAG, "onQuestionSelected Position : "+position);
        isQuestionSelected = true;
        mBinding.drawerLayout.closeDrawer(Gravity.RIGHT);
        pos = position;
    }

    public class ViewPagerAdapter extends FragmentStateAdapter {
        private List<LearningQuestionsNew> testQuestionNewList;

        public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<LearningQuestionsNew> testQuestionNewList) {
            super(fragmentActivity);
            this.testQuestionNewList = testQuestionNewList;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            LearningQuestionsNew testQuestionNew = testQuestionNewList.get(position);
            if (testQuestionNew.getType().equals(Constant.SHORT_ANSWER) ||
                    testQuestionNew.getType().equals(Constant.ONE_LINE_ANSWER) ||
                    testQuestionNew.getType().equals(Constant.LONG_ANSWER) ||
                    testQuestionNew.getType().equals(Constant.FILL_THE_BLANKS)) {
                return PractiseSubjectiveFragment.newInstance(position);
            } else {
                if (testQuestionNew.getQue_option_type().equalsIgnoreCase(SCQ)) {
                    return PractiseScqFragment.newInstance(position);
                }else  if (testQuestionNew.getQue_option_type().equalsIgnoreCase(SCQ_IMAGE_WITH_TEXT)) {
                    return PractiseScqImageText.newInstance(position);
                }
                else if (testQuestionNew.getQue_option_type().equalsIgnoreCase(Constant.MCQ)) {
                    return PractiseMcqFragment.newInstance(position);
                } else if (testQuestionNew.getQue_option_type().equals(MATCH_PAIR)) {
                    return PractiseMtpFragment.newInstance(position);
                }else{
                    return PractiseSubjectiveFragment.newInstance(position);
                }

            }
        }

        @Override
        public int getItemCount() {
            return testQuestionNewList.size();
        }
    }

    private ViewPagerAdapter createCardAdapter(List<LearningQuestionsNew> testQuestionNewList) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(this, testQuestionNewList);
        return adapter;
    }

    private void ProcessQuestionAPI(int user_id, String que_id, String api_case, int flag, String call_from) {
        ProgressDialog.getInstance().show(this);
        Call<ProcessQuestion> call;

        if (call_from.equalsIgnoreCase("like"))
            call = apiService.likeApi(user_id, que_id, api_case, flag);
        else
            call = apiService.favApi(user_id, que_id, api_case, flag);

        call.enqueue(new Callback<ProcessQuestion>() {
            @Override
            public void onResponse(Call<ProcessQuestion> call, retrofit2.Response<ProcessQuestion> response) {
                try {
                    questionsNewList.clear();
                    if (response.body() != null && response.body().getResponse().equalsIgnoreCase("200")) {
                        getPractiseTestQuestions();
                    } else {
                        Toast.makeText(getApplicationContext(), UtilHelper.getAPIError(String.valueOf(response.body())), Toast.LENGTH_SHORT).show();
                    }
                    ProgressDialog.getInstance().dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                    ProgressDialog.getInstance().dismiss();
                }
            }

            @Override
            public void onFailure(Call<ProcessQuestion> call, Throwable t) {
                t.printStackTrace();
                ProgressDialog.getInstance().dismiss();
            }
        });
    }
}
