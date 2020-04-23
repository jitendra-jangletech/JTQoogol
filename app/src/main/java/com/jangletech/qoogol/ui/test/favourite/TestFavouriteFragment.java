package com.jangletech.qoogol.ui.test.favourite;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jangletech.qoogol.activities.StartTestActivity;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.TestAdapter;
import com.jangletech.qoogol.databinding.TestFavouriteFragmentBinding;
import com.jangletech.qoogol.model.TestModel;
import com.jangletech.qoogol.ui.BaseFragment;

import java.util.ArrayList;
import java.util.List;

public class TestFavouriteFragment extends BaseFragment implements TestAdapter.TestClickListener {

    private TestFavouriteViewModel mViewModel;
    private TestFavouriteFragmentBinding mBinding;

    public static TestFavouriteFragment newInstance() {
        return new TestFavouriteFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.test_favourite_fragment, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(TestFavouriteViewModel.class);
        setMyTestList();
    }

    public void setMyTestList() {

        List<TestModel> testList = new ArrayList<>();
        testList.clear();

        TestModel testModel = new TestModel("Shapes and Angles","Maths","40",
                "30","Hard","88/100","219","Jan 2020","2093",
                true,false,"Mr. Sharan","Phd. Maths","Unit Test-Final",
                "4.3","100",true,2,false);

        TestModel testModel2 = new TestModel("When the Earth Shook!","Evs","40 Mins",
                "60","Medium","12/100","10","Jul 2019","8353",
                true,false,"Mr. Narayan","Phd. Evs",
                "Unit Test-Final","2","30",false,8,true);

        testList.add(testModel);
        testList.add(testModel2);

        TestAdapter testAdapter = new TestAdapter(new TestFavouriteFragment(), testList, this);
        mBinding.favTestListRecyclerView.setHasFixedSize(true);
        mBinding.favTestListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.favTestListRecyclerView.setAdapter(testAdapter);
    }

    @Override
    public void onTestItemClick(TestModel testModel) {

    }

    @Override
    public void onStartTestClick(TestModel testModel) {
        startActivity(new Intent(getActivity(), StartTestActivity.class));
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
