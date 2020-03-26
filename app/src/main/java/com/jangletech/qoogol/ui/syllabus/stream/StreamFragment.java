package com.jangletech.qoogol.ui.syllabus.stream;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.ClassAdapter;
import com.jangletech.qoogol.databinding.StreamFragmentBinding;
import com.jangletech.qoogol.model.Employee;
import com.jangletech.qoogol.ui.BaseFragment;

import java.util.ArrayList;

public class StreamFragment extends BaseFragment {

    private StreamViewModel mViewModel;
    private StreamFragmentBinding mBinding;
    ClassAdapter classAdapter;
    ArrayList<Employee> employees;

    public static StreamFragment newInstance() {
        return new StreamFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.stream_fragment, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(StreamViewModel.class);

        prepareStreamList();

        mBinding.btnNext.setOnClickListener(v -> {
            showToast("" + classAdapter.getSelected().getName());
        });

    }

    private void prepareStreamList() {

        mBinding.recyclerView.setHasFixedSize(true);
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        classAdapter = new ClassAdapter(getActivity(), employees,new StreamFragment());
        mBinding.recyclerView.setAdapter(classAdapter);

        employees = new ArrayList<>();
        Employee employee = new Employee();
        employee.setName("Science");
        employees.add(employee);
        Employee employee1 = new Employee();
        employee1.setName("Commerce");
        employees.add(employee1);
        classAdapter.setEmployees(employees);
    }

}
