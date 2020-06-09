package com.jangletech.qoogol.ui.test.shared_by_you;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jangletech.qoogol.activities.MainActivity;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.TestAdapter;
import com.jangletech.qoogol.databinding.FragmentTestSharedByYouBinding;
import com.jangletech.qoogol.model.TestModel;
import com.jangletech.qoogol.ui.BaseFragment;

import java.util.ArrayList;
import java.util.List;

public class TestSharedByYouFragment extends BaseFragment implements TestAdapter.TestClickListener {

    private TestSharedByYouViewModel mViewModel;
    private FragmentTestSharedByYouBinding mBinding;

    public static TestSharedByYouFragment newInstance() {
        return new TestSharedByYouFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_test_shared_by_you, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(TestSharedByYouViewModel.class);
        setSharedByYouTestList();
    }

    public void setSharedByYouTestList() {

        List<TestModel> testList = new ArrayList<>();
        testList.clear();

        TestModel testModel = new TestModel(1,"Shapes and Angles","Maths","40",
                "30","Hard","88/100","219","Jan 2020","2093",
                true,false,"Mr. Sharan","Phd. Maths",
                "Unit Test-Final","4.3","100",true,7,false);

        TestModel testModel1 = new TestModel(2,"Reading Comprehension","English","120 Mins",
                "40","Easy","53/100","102","Mar 2019","1633",
                false,true,"Mr. Goswami","Phd. English",
                "Unit Test-Final","2.7","60",false,3,false);

        TestModel testModel2 = new TestModel(3,"When the Earth Shook!","Evs","40 Mins",
                "60","Medium","12/100","10","Jul 2019","8353",
                true,false,"Mr. Narayan","Phd. Evs",
                "Unit Test-Final","2","30",false,43,false);

        testList.add(testModel);
        testList.add(testModel1);
        testList.add(testModel2);

        TestAdapter testAdapter = new TestAdapter(new TestSharedByYouFragment(), testList, this);
        mBinding.testListRecyclerView.setHasFixedSize(true);
        mBinding.testListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mBinding.testListRecyclerView.setAdapter(testAdapter);
    }

    @Override
    public void onTestItemClick(TestModel testModel) {
        Toast.makeText(getActivity(), ""+testModel.getName(), Toast.LENGTH_SHORT).show();
        Navigation.findNavController(requireActivity(),R.id.nav_host_fragment).navigate(R.id.nav_test_details);
        //MainActivity.navController.navigate(R.id.nav_test_details);
    }

    @Override
    public void onStartTestClick(TestModel testModel) {
        //MainActivity.navController.navigate(R.id.nav_course);
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
