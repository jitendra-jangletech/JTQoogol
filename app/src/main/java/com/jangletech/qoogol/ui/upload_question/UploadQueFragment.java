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
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.model.SubjectClass;
import com.jangletech.qoogol.model.SubjectResponse;
import com.jangletech.qoogol.model.UploadQuestion;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.util.AppUtils;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.ItemOffsetDecoration;
import com.jangletech.qoogol.util.PreferenceManager;
import com.jangletech.qoogol.util.TinyDB;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

/**
 * A simple {@link Fragment} subclass.
 */
public class UploadQueFragment extends BaseFragment implements SubjectAdapter.SubjectTileClickListener {

    private FragmentUploadQueBinding mBinding;
    private SubjectAdapter subjectAdapter;
    private List<SubjectClass> subjects = new ArrayList<>();
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
        getData();
    }

    private void getData() {
        subjects.clear();
        ProgressDialog.getInstance().show(getActivity());
        Call<SubjectResponse> call = getApiService().fetchSubjectList(
                new PreferenceManager(getActivity()).getUserId(),
                TinyDB.getInstance(getActivity()).getString(Constant.selected_ue_id),
                getDeviceId(getActivity()),
                "Q",
                "L");
        call.enqueue(new Callback<SubjectResponse>() {
            @Override
            public void onResponse(Call<SubjectResponse> call, retrofit2.Response<SubjectResponse> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.body().getResponse().equalsIgnoreCase("200")) {
                    subjects = response.body().getSubjectList();
                    initRecycler();
                } else {
                    AppUtils.showToast(getActivity(), null, response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<SubjectResponse> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                AppUtils.showToast(getActivity(), t, "");
                t.printStackTrace();
            }
        });
    }

    private void initRecycler() {
        itemDecoration = new ItemOffsetDecoration(requireActivity(), R.dimen.item_offset);
        subjectAdapter = new SubjectAdapter(getContext(), subjects, this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        mBinding.subjectRecyclerView.setLayoutManager(gridLayoutManager);
        mBinding.subjectRecyclerView.setHasFixedSize(true);
        mBinding.subjectRecyclerView.addItemDecoration(itemDecoration);
        mBinding.subjectRecyclerView.setAdapter(subjectAdapter);
    }

    @Override
    public void onSubjectSelected(SubjectClass subjectClass) {
        UploadQuestion uploadQuestion = new UploadQuestion();
        uploadQuestion.setSubjectId(subjectClass.getSubjectId());
        uploadQuestion.setSubjectName(subjectClass.getSubjectName());
        Bundle bundle = new Bundle();
        bundle.putSerializable("Question", uploadQuestion);
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.nav_scan_quest, bundle);
    }
}


