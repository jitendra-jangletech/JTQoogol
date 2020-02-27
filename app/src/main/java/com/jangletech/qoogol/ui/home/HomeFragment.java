package com.jangletech.qoogol.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.azoft.carousellayoutmanager.CarouselLayoutManager;
import com.azoft.carousellayoutmanager.CarouselZoomPostLayoutListener;
import com.azoft.carousellayoutmanager.CenterScrollListener;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.HomeAdapter;
import com.jangletech.qoogol.databinding.FragmentHomeBinding;
import com.jangletech.qoogol.model.Home;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    FragmentHomeBinding fragmentHomeBinding;

    List<Home> itemlist = new ArrayList();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        fragmentHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);

        itemlist.clear();
        Home home = new Home("100", "2200", "50");
        itemlist.add(home);

        Home home1 = new Home("200", "2000",null);
        itemlist.add(home1);

        Home home2 = new Home("150", "1500", null);
        itemlist.add(home2);

        Home home3 = new Home("150", "1500", null);
        itemlist.add(home3);

        Home home4 = new Home("150", "1500", null);
        itemlist.add(home4);


        HomeAdapter homeAdapter = new HomeAdapter(getActivity(), itemlist);
        final CarouselLayoutManager layoutManager = new CarouselLayoutManager(CarouselLayoutManager.HORIZONTAL, true);
        layoutManager.setPostLayoutListener(new CarouselZoomPostLayoutListener());

        layoutManager.setMaxVisibleItems(10);
        fragmentHomeBinding.itemRecycler.setLayoutManager(layoutManager);
        fragmentHomeBinding.itemRecycler.setHasFixedSize(true);
        fragmentHomeBinding.itemRecycler.setAdapter(homeAdapter);
        fragmentHomeBinding.itemRecycler.addOnScrollListener(new CenterScrollListener());
        return fragmentHomeBinding.getRoot();
    }
}