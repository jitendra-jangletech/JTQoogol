package com.jangletech.qoogol.ui.syllabus.board;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.BoardAdapter;
import com.jangletech.qoogol.databinding.BoardFragmentBinding;
import com.jangletech.qoogol.model.BoardItem;
import com.jangletech.qoogol.ui.BaseFragment;

import java.util.ArrayList;

public class BoardFragment extends BaseFragment implements Filterable{

    private BoardViewModel mViewModel;
    private BoardFragmentBinding mBinding;
    ArrayList<BoardItem> boardItems;
    BoardAdapter boardAdapter;

    public static BoardFragment newInstance() {
        return new BoardFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater,R.layout.board_fragment, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(BoardViewModel.class);
        prepareBoardList();
        mBinding.btnNext.setOnClickListener(v -> {

        });
    }

    private void prepareBoardList() {
        mBinding.recyclerView.setHasFixedSize(true);
        mBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        boardAdapter = new BoardAdapter(getActivity(), boardItems);
        mBinding.recyclerView.setAdapter(boardAdapter);

        boardItems = new ArrayList<>();
        BoardItem boardItem = new BoardItem("CBSE","Central Board of Secondary Education");
        BoardItem boardItem1 = new BoardItem("ICSE","Indian Certificate of Secondary Education");
        BoardItem boardItem2 = new BoardItem("Maharashtra State Board","Maharashtra State Board of Secondary and Higher Secondary Education");
        BoardItem boardItem3 = new BoardItem("Telangana State Board","Telangana Board of Secondary Education");
        boardItems.add(boardItem);
        boardItems.add(boardItem1);
        boardItems.add(boardItem2);
        boardItems.add(boardItem3);
        boardAdapter.setBoardItems(boardItems);
    }

    @Override
    public Filter getFilter() {
        return null;
    }
}
