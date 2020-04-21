package com.jangletech.qoogol.ui.notifications;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.NotificationAdapter;
import com.jangletech.qoogol.databinding.FragmentNotificationsBinding;
import com.jangletech.qoogol.model.Notification;

public class NotificationsFragment extends Fragment {

    private static final String TAG = "NotificationsFragment";
    private NotificationsViewModel mViewModel;
    private FragmentNotificationsBinding mBinding;

    public static NotificationsFragment newInstance() {
        return new NotificationsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_notifications, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(NotificationsViewModel.class);
        mViewModel.getNotifications().observe(this,notifications -> {
            Log.d(TAG, "onActivityCreated: "+notifications.size());
            mBinding.notificationRecyclerView.setHasFixedSize(true);
            mBinding.notificationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            NotificationAdapter notificationAdapter = new NotificationAdapter(getActivity(),notifications);
            mBinding.notificationRecyclerView.setAdapter(notificationAdapter);
        });
    }

}
