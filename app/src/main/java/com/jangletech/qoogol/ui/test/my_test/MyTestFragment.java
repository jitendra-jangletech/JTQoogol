package com.jangletech.qoogol.ui.test.my_test;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.jangletech.qoogol.CourseActivity;
import com.jangletech.qoogol.MainActivity;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.TestAdapter;
import com.jangletech.qoogol.databinding.FragmentTestMyBinding;
import com.jangletech.qoogol.model.TestModel;
import com.jangletech.qoogol.ui.BaseFragment;

import java.util.ArrayList;
import java.util.List;

public class MyTestFragment extends BaseFragment implements TestAdapter.TestClickListener, SearchView.OnQueryTextListener {

    private static final String TAG = "MyTestFragment";
    private com.jangletech.qoogol.ui.test.my_test.MyTestViewModel mViewModel;
    private FragmentTestMyBinding mBinding;
    private TestAdapter testAdapter;
    private List<TestModel> testList;

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
        mBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_test_my, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.action_search, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);

        item.setOnActionExpandListener( new MenuItem.OnActionExpandListener() {
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
                MainActivity.navController.navigate(R.id.nav_test_filter);
                return true;
            case R.id.action_search:
                searchTest();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(com.jangletech.qoogol.ui.test.my_test.MyTestViewModel.class);
        setMyTestList();
        prepareSubjectChips();

        mBinding.subjectsChipGrp.setOnCheckedChangeListener((chipGroup, id) -> {
            //showToast("id : " + id);
            Chip chip = ((Chip) chipGroup.getChildAt(chipGroup.getCheckedChipId()));
            if (chip != null) {
                if (chip.isChecked()) {
                    setCheckedChip(mBinding.subjectsChipGrp);
                }
            }
        });
        mBinding.fabFilter.setOnClickListener(v->{
            MainActivity.navController.navigate(R.id.nav_test_filter);
        });
    }

    public void setMyTestList(){
        testList = new ArrayList<>();
        testList.clear();

        TestModel testModel = new TestModel("Shapes and Angles","Maths","40",
                "30","Hard","88/100","219","Jan 2020","2093",
                true,true,"Mr. Sharan","Phd. Maths","Unit Test-Final","4.3","100");

        TestModel testModel1 = new TestModel("Reading Comprehension","English","120",
                "40","Easy","53/100","102","Mar 2019","1633",
                false,true,"Mr. Goswami","Phd. English","Unit Test-Final","2.7","60");

        TestModel testModel2 = new TestModel("When the Earth Shook!","Evs","40",
                "60","Medium","12/100","10","Jul 2019","8353",
                true,false,"Mr. Narayan","Phd. Evs","Unit Test-Final","2","30");

        testList.add(testModel);
        testList.add(testModel1);
        testList.add(testModel2);

        testAdapter = new TestAdapter(new MyTestFragment(), testList,this);
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
            if(i==0){
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

    private void searchTest(){
        Toast.makeText(getActivity(), "Search For The Test.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onTestItemClick(TestModel testModel) {
        Toast.makeText(getActivity(), ""+testModel.getName(), Toast.LENGTH_SHORT).show();
            MainActivity.navController.navigate(R.id.nav_test_details);
        }

    @Override
    public void onStartTestClick(TestModel testModel) {
        startActivity(new Intent(getActivity(), CourseActivity.class));
    }

    @Override
    public void onShareClick(TestModel testModel) {
        Toast.makeText(getActivity(), "Test shared", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDownloadClick(TestModel testModel) {
        Toast.makeText(getActivity(), "Test downloaded", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFavouriteClick(TestModel testModel) {
        Toast.makeText(getActivity(), "Added to favourite list", Toast.LENGTH_SHORT).show();
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
            final String text = model.getName().toLowerCase();
            if (text.contains(query)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }
}
