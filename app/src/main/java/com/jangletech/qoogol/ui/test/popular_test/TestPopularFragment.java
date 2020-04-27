package com.jangletech.qoogol.ui.test.popular_test;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jangletech.qoogol.activities.MainActivity;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.TestAdapter;
import com.jangletech.qoogol.databinding.FragmentTestPopularBinding;
import com.jangletech.qoogol.model.TestModel;
import com.jangletech.qoogol.ui.BaseFragment;

import java.util.ArrayList;
import java.util.List;

public class TestPopularFragment extends BaseFragment implements TestAdapter.TestClickListener {

    private TestPopularViewModel mViewModel;
    private FragmentTestPopularBinding mBinding;

    public static TestPopularFragment newInstance() {
        return new TestPopularFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_test_popular, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(TestPopularViewModel.class);
        setPopularTestList();
    }

    public void setPopularTestList(){

        List<TestModel> testList = new ArrayList<>();
        testList.clear();

        TestModel testModel = new TestModel(1,"Shapes and Angles","Maths","40",
                "30","Hard","88/100","219","Jan 2020","2093",
                true,false,"Mr. Sharan","Phd. Maths","Unit Test-Final",
                "4.3","100",true,12,false);

        TestModel testModel1 = new TestModel(2,"Reading Comprehension","English","120 Mins",
                "40","Easy","53/100","102","Mar 2019","1633",
                false,true,"Mr. Goswami","Phd. English","Unit Test-Final",
                "2.7","60",false,11,true);

        TestModel testModel2 = new TestModel(3,"When the Earth Shook!","Evs","40 Mins",
                "60","Medium","12/100","10","Jul 2019","8353",
                true,false,"Mr. Narayan","Phd. Evs","Unit Test-Final",
                "2","30",false,9,false);

        testList.add(testModel);
        testList.add(testModel1);
        testList.add(testModel2);

        TestAdapter testAdapter = new TestAdapter(new TestPopularFragment(), testList,this);
        mBinding.testListRecyclerView.setHasFixedSize(true);
        mBinding.testListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.testListRecyclerView.setAdapter(testAdapter);
    }

    @Override
    public void onTestItemClick(TestModel testModel) {
        Toast.makeText(getActivity(), ""+testModel.getName(), Toast.LENGTH_SHORT).show();
        MainActivity.navController.navigate(R.id.nav_test_details);
    }

    @Override
    public void onStartTestClick(TestModel testModel) {
       // MainActivity.navController.navigate(R.id.nav_course);
    }

    @Override
    public void onCommentClick(TestModel testModel) {

    }

    @Override
    public void onShareClick(TestModel testModel) {

    }

    @Override
    public void onLikeClick(TestModel testModel, int pos) {

    }


    @Override
    public void onFavouriteClick(TestModel testModel, boolean isChecked) {

    }


}
