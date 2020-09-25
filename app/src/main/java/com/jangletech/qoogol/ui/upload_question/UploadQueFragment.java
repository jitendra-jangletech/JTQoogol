package com.jangletech.qoogol.ui.upload_question;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.SubjectAdapter;
import com.jangletech.qoogol.databinding.FragmentUploadQueBinding;
import com.jangletech.qoogol.model.SubjectClass;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.util.ItemOffsetDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class UploadQueFragment extends BaseFragment implements SubjectAdapter.SubjectTileClickListener {

    private FragmentUploadQueBinding mBinding;
    private SubjectAdapter subjectAdapter;
    private List<SubjectClass> subjects;
    private ItemOffsetDecoration itemDecoration;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_upload_que, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        prepareSubjects();
        itemDecoration = new ItemOffsetDecoration(requireActivity(), R.dimen.item_offset);
        subjectAdapter = new SubjectAdapter(getContext(), subjects, this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        mBinding.subjectRecyclerView.setLayoutManager(gridLayoutManager);
        mBinding.subjectRecyclerView.setHasFixedSize(true);
        mBinding.subjectRecyclerView.addItemDecoration(itemDecoration);
        mBinding.subjectRecyclerView.setAdapter(subjectAdapter);

    }

    private void prepareSubjects() {
        subjects = new ArrayList<>();
        subjects.add(new SubjectClass("1", "Science"));
        subjects.add(new SubjectClass("2", "English"));
        subjects.add(new SubjectClass("3", "History"));
    }

    @Override
    public void onSubjectSelected(SubjectClass subjectClass) {
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.nav_scq_question, Bundle.EMPTY);
//        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.nav_scan_quest, Bundle.EMPTY);
    }
}


