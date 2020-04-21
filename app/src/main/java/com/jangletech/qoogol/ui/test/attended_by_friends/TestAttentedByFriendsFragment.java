package com.jangletech.qoogol.ui.test.attended_by_friends;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jangletech.qoogol.MainActivity;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.TestAdapter;
import com.jangletech.qoogol.databinding.FragmentTestAttentedByFriendsBinding;
import com.jangletech.qoogol.model.TestModel;
import com.jangletech.qoogol.ui.BaseFragment;

import java.util.ArrayList;
import java.util.List;

public class TestAttentedByFriendsFragment extends BaseFragment implements TestAdapter.TestClickListener {

    private TestAttentedByFriendsViewModel mViewModel;
    private FragmentTestAttentedByFriendsBinding mBinding;

    public static TestAttentedByFriendsFragment newInstance() {
        return new TestAttentedByFriendsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_test_attented_by_friends, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(TestAttentedByFriendsViewModel.class);
        setAttendedByFriendsTestList();
    }

    public void setAttendedByFriendsTestList(){

        List<TestModel> testList = new ArrayList<>();
        testList.clear();

        TestModel testModel = new TestModel("Shapes and Angles","Maths","40",
                "30","Hard","88/100","219","Jan 2020","2093",
                true,false,"Mr. Sharan","Phd. Maths","Unit Test-Final","4.3","100",true);
        TestModel testModel2 = new TestModel("When the Earth Shook!","Evs","40 Mins",
                "60","Medium","12/100","10","Jul 2019","8353",
                true,false,"Mr. Narayan","Phd. Evs","Unit Test-Final","2","30",false);

        testList.add(testModel);
        testList.add(testModel2);

        TestAdapter testAdapter = new TestAdapter(new TestAttentedByFriendsFragment(), testList,this);
        mBinding.testListRecyclerView.setHasFixedSize(true);
        mBinding.testListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.testListRecyclerView.setAdapter(testAdapter);
    }

    @Override
    public void onTestItemClick(TestModel testModel) {
        Toast.makeText(getActivity(), ""+testModel.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStartTestClick(TestModel testModel) {
        //MainActivity.navController.navigate(R.id.nav_course);
    }

    @Override
    public void onCommentClick(TestModel testModel) {
        showToast("Comment Clicked");
    }

    @Override
    public void onShareClick(TestModel testModel) {
        showToast("Share clicked");
    }

    @Override
    public void onLikeClick(TestModel testModel) {
        showToast("Like Clicked");
    }

}
