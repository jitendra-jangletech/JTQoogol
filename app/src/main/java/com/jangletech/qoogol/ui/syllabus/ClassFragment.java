package com.jangletech.qoogol.ui.syllabus;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.ClassAdapter;
import com.jangletech.qoogol.adapter.SpacesItemDecoration;
import com.jangletech.qoogol.databinding.FragmentClassBinding;
import com.jangletech.qoogol.model.Employee;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.ui.syllabus.board.BoardFragment;
import com.jangletech.qoogol.ui.syllabus.stream.StreamFragment;

import java.util.ArrayList;

public class ClassFragment extends BaseFragment {

    private ClassViewModel mViewModel;
    private FragmentClassBinding mBinding;
    ClassAdapter classAdapter;
    ArrayList<Employee> employees;

    public static ClassFragment newInstance() {
        return new ClassFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_class, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ClassViewModel.class);
        prepareClassList();
        mBinding.btnNext.setOnClickListener(v->{
            showToast(""+classAdapter.getSelected().getName());
            if(classAdapter.getSelected().getName().equals("11") || classAdapter.getSelected().getName().equals("12")){
                //addFragment(new StreamFragment());
            }else{
               // addFragment(new BoardFragment());
            }

        });
    }

    private void prepareClassList() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(),3);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL); // set Horizontal Orientation
        mBinding.recyclerView.setLayoutManager(gridLayoutManager);
        int spanCount = 3; // 3 columns
        int spacing = 50; // 50px
        boolean includeEdge = true;
        mBinding.recyclerView.addItemDecoration(new SpacesItemDecoration(spanCount, spacing, includeEdge));
        classAdapter = new ClassAdapter(getActivity(), employees,new ClassFragment());
        mBinding.recyclerView.setAdapter(classAdapter);

        employees = new ArrayList<>();
        for (int i = 5; i <= 12; i++) {
            Employee employee = new Employee();
            employee.setName(""+ i);
            employees.add(employee);
        }
        classAdapter.setEmployees(employees);
    }
}
