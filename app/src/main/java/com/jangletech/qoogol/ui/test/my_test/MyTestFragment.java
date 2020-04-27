package com.jangletech.qoogol.ui.test.my_test;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.activities.MainActivity;
import com.jangletech.qoogol.activities.StartTestActivity;
import com.jangletech.qoogol.adapter.TestAdapter;
import com.jangletech.qoogol.databinding.FragmentTestMyBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.model.TestModel;
import com.jangletech.qoogol.model.TestingQuestionNew;
import com.jangletech.qoogol.model.TestingRequestDto;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.ui.BaseFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyTestFragment extends BaseFragment implements TestAdapter.TestClickListener, SearchView.OnQueryTextListener {

    private static final String TAG = "MyTestFragment";
    private MyTestViewModel mViewModel;
    private FragmentTestMyBinding mBinding;
    private TestAdapter testAdapter;
    private List<TestModel> testList;
    public static String testName = "";
    ApiInterface apiService = ApiClient.getInstance().getApi();

    public static MyTestFragment newInstance() {
        return new MyTestFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_test_my, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.action_search, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);

        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                Bundle bundle = new Bundle();
                bundle.putString("call_from", "test");
                MainActivity.navController.navigate(R.id.nav_test_filter, bundle);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MyTestViewModel.class);
        startResumeTest();
        mViewModel.getAllTests().observe(getActivity(), new Observer<List<TestModel>>() {
            @Override
            public void onChanged(@Nullable final List<TestModel> tests) {
                Log.d(TAG, "onChanged Size : "+tests.size());
                for (int i = 0; i < tests.size(); i++) {
                    Log.d(TAG, "onChanged: "+tests.get(i).getName());
                }
                setMyTestList(tests);
            }
        });

        prepareSubjectChips();

        mBinding.subjectsChipGrp.setOnCheckedChangeListener((chipGroup, id) -> {
            Chip chip = ((Chip) chipGroup.getChildAt(chipGroup.getCheckedChipId()));
            if (chip != null) {
                if (chip.isChecked()) {
                    setCheckedChip(mBinding.subjectsChipGrp);
                    List<TestModel> filteredModelList = filterBySubject(testList, chip.getText().toString());
                    if (filteredModelList.size() > 0) {
                        mBinding.tvNoTest.setVisibility(View.GONE);
                        testAdapter.setSearchResult(filteredModelList);
                    } else {
                        testAdapter.setSearchResult(filteredModelList);
                        mBinding.tvNoTest.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        mBinding.fabFilter.setOnClickListener(v -> {
            MainActivity.navController.navigate(R.id.nav_test_filter);
        });
    }

    public void startResumeTest() {
        ProgressDialog.getInstance().show(getActivity());
        Call<TestingQuestionNew> call = apiService.fetchTestQuestionAnswers(1,10,3);
        call.enqueue(new Callback<TestingQuestionNew>() {
            @Override
            public void onResponse(Call<TestingQuestionNew> call, Response<TestingQuestionNew> response) {
                ProgressDialog.getInstance().dismiss();
                Log.d(TAG, "onResponse: "+response.body());
                TestingQuestionNew  testingQuestionNew = response.body();
                Log.d(TAG, "onResponse Data : "+testingQuestionNew.getTestQuestAnswerList().size());

            }

            @Override
            public void onFailure(Call<TestingQuestionNew> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                t.printStackTrace();
            }
        });
    }

    public void setMyTestList(List<TestModel> testList) {
       /* testList = new ArrayList<>();
        testList.clear();

        TestModel testModel = new TestModel("Shapes and Angles", "Mathematics", "40",
                "30", "Hard", "88/100", "219", "Jan 2020", "2093",
                true, true, "Mr. Sharan",
                "Phd. Mathematics", "Unit Test-Final", "4.3",
                "100", true, 1, false);

        TestModel testModel1 = new TestModel("Reading Comprehension", "English", "120",
                "40", "Easy", "53/100", "102", "Mar 2019", "1633",
                false, true,
                "Mr. Goswami", "Phd. English", "Unit Test-Final", "2.7",
                "60", false, 9, false);

        TestModel testModel2 = new TestModel("When the Earth Shook!", "Physics", "40",
                "60", "Medium", "12/100", "10", "Jul 2019", "8353",
                true, false, "Mr. Narayan", "Phd. Physics",
                "Unit Test-Final", "2", "30", false, 0, false);

        TestModel testModel3 = new TestModel("Shapes and Angles", "Mathematics", "40",
                "30", "Hard", "88/100", "219", "Jan 2020", "2093",
                true, true, "Mr. Sharan", "Phd. Mathematics",
                "Unit Test-Final", "4.3", "100", false, 25, false);

        TestModel testModel4 = new TestModel("Reading Comprehension", "English", "120",
                "40", "Easy", "53/100", "102", "Mar 2019", "1633",
                false, true, "Mr. Goswami", "Phd. English",
                "Unit Test-Final", "2.7", "60", true, 3, false);

        TestModel testModel5 = new TestModel("When the Earth Shook!", "Evs", "40",
                "60", "Medium", "12/100", "10", "Jul 2019", "8353",
                true, false, "Mr. Narayan", "Phd. Evs",
                "Unit Test-Final", "2", "30", true, 4, true);

        testList.add(testModel);
        testList.add(testModel1);
        testList.add(testModel2);
        testList.add(testModel3);
        testList.add(testModel4);
        testList.add(testModel5);

        for (int i = 0; i < testList.size(); i++) {
            mViewModel.insert(testList.get(i));
        }*/

        testAdapter = new TestAdapter(new MyTestFragment(), testList, this);
        mBinding.testListRecyclerView.setHasFixedSize(true);
        mBinding.testListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.testListRecyclerView.setAdapter(testAdapter);
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
            if (i == 0) {
                chip.setChecked(true);
            }
            chip.setClickable(true);
            chip.setCheckable(true);
            mBinding.subjectsChipGrp.addView(chip);
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
    public void onTestItemClick(TestModel testModel) {
        Toast.makeText(getActivity(), "" + testModel.getName(), Toast.LENGTH_SHORT).show();
        testName = testModel.getName();
        Log.d(TAG, "onTestItemClick: " + testName);
        MainActivity.navController.navigate(R.id.nav_test_details);
    }

    @Override
    public void onStartTestClick(TestModel testModel) {
        startActivity(new Intent(getActivity(), StartTestActivity.class));
    }

    @Override
    public void onCommentClick(TestModel testModel) {
        showToast("Comment Clicked");
        NavHostFragment.findNavController(this).navigate(R.id.action_nav_test_my_to_nav_comments);
    }

    @Override
    public void onShareClick(TestModel testModel) {
        Toast.makeText(getActivity(), "Test shared", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLikeClick(TestModel testModel, int pos) {
        showToast("Like Clicked");
        mBinding.testListRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                testAdapter.notifyItemChanged(pos);
            }
        });
    }

    @Override
    public void onFavouriteClick(TestModel testModel, boolean isChecked) {
        if (isChecked) {
            showToast("Added To Favourite.");
        } else {
            showToast("Removed From Favourite.");
        }
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        final List<TestModel> filteredModelList = filter(testList, newText);
        testAdapter.setSearchResult(filteredModelList);
        return true;
    }

    private List<TestModel> filter(List<TestModel> models, String query) {
        query = query.toLowerCase();
        final List<TestModel> filteredModelList = new ArrayList<>();
        for (TestModel model : models) {
            String testName = model.getName().toLowerCase();
            if (testName.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    private List<TestModel> filterBySubject(List<TestModel> models, String subject) {
        subject = subject.toLowerCase();
        List<TestModel> filteredModelList = new ArrayList<>();
        if (!subject.equalsIgnoreCase("All")) {
            for (TestModel model : models) {
                String testSubject = model.getSubject().toLowerCase();
                if (testSubject.contains(subject)) {
                    filteredModelList.add(model);
                }
            }
        } else {
            filteredModelList = models;
        }

        return filteredModelList;
    }
}
