package com.jangletech.qoogol.ui.syllabus.board;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.SignUpViewModel;
import com.jangletech.qoogol.activities.MainActivity;
import com.jangletech.qoogol.adapter.BoardAdapterNew;
import com.jangletech.qoogol.databinding.BoardFragmentBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.model.University;
import com.jangletech.qoogol.model.UniversityResponse;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.ui.settings.SettingsFragment;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BoardFragment extends BaseFragment implements BoardAdapterNew.BoardItemClickListener {

    private static final String TAG = "BoardFragment";
    private SignUpViewModel mViewModel;
    private BoardFragmentBinding mBinding;
    List<University> universityList;
    BoardAdapterNew boardAdapter;
    ApiInterface apiService = ApiClient.getInstance().getApi();

    public static BoardFragment newInstance() {
        return new BoardFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.board_fragment, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(SignUpViewModel.class);
        fetchUniversityData();
        mViewModel.getUniversityList().observe(getActivity(), new Observer<List<University>>() {
            @Override
            public void onChanged(@Nullable final List<University> universities) {
                universityList = universities;
                prepareBoardList(universities);
            }
        });
        mBinding.btnSave.setOnClickListener(v -> {

        });

        mBinding.searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                boardAdapter.setBoardItems(searchBoard(s.toString()));
            }
        });
    }

    private void fetchUniversityData() {
        ProgressDialog.getInstance().show(getActivity());
        Call<UniversityResponse> call = apiService.getUniversity();
        call.enqueue(new Callback<UniversityResponse>() {
            @Override
            public void onResponse(Call<UniversityResponse> call, Response<UniversityResponse> response) {
                ProgressDialog.getInstance().dismiss();
                try {
                    List<University> list = response.body().getMasterDataList();
                    mViewModel.setUniversityList(list);
                    if (list != null && list.size() > 0) {
                        mViewModel.mMapUniversity = new HashMap<>();
                        for (University university : list) {
                            mViewModel.mMapUniversity.put(Integer.valueOf(university.getUnivBoardId()), university.getName());
                        }
                        //populateUniversityBoard(mViewModel.mMapUniversity);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ProgressDialog.getInstance().dismiss();
                }
            }

            @Override
            public void onFailure(Call<UniversityResponse> call, Throwable t) {
                t.printStackTrace();
                ProgressDialog.getInstance().dismiss();
            }
        });
    }


    private List<University> searchBoard(String queryTxt) {
        List<University> universities = new ArrayList<>();
        for (University university : universityList) {
            if (university.getName().toLowerCase().contains(queryTxt.toLowerCase())) {
                universities.add(university);
            }
        }
        return universities;
    }

    private void prepareBoardList(List<University> universities) {
        mBinding.boardRecyclerView.setHasFixedSize(true);
        mBinding.boardRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        boardAdapter = new BoardAdapterNew(getActivity(), universities,this);
        mBinding.boardRecyclerView.setAdapter(boardAdapter);
    }

    @Override
    public void onBoardSelected(University university) {
        showToast(university.getName());
        new PreferenceManager(getActivity()).saveString(Constant.BOARD,university.getName());
        Navigation.findNavController(requireActivity(),R.id.nav_host_fragment).navigate(R.id.nav_settings);
        //MainActivity.navController.navigate(R.id.nav_settings, Bundle.EMPTY);
    }
}
