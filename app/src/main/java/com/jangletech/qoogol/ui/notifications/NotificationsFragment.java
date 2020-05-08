package com.jangletech.qoogol.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.NotificationAdapter;
import com.jangletech.qoogol.databinding.FragmentNotificationsBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.model.Notification;
import com.jangletech.qoogol.model.NotificationResponse;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsFragment extends BaseFragment {

    private static final String TAG = "NotificationsFragment";
    private NotificationsViewModel mViewModel;
    private FragmentNotificationsBinding mBinding;
    ApiInterface apiService = ApiClient.getInstance().getApi();


    public static NotificationsFragment newInstance() {
        return new NotificationsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_notifications, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(NotificationsViewModel.class);
        fetchNotifications();
        mViewModel.getNotificationList().observe(getActivity(), new Observer<List<Notification>>() {
            @Override
            public void onChanged(@Nullable final List<Notification> notifications) {
                mBinding.notificationRecyclerView.setHasFixedSize(true);
                mBinding.notificationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                NotificationAdapter notificationAdapter = new NotificationAdapter(getActivity(), notifications);
                mBinding.notificationRecyclerView.setAdapter(notificationAdapter);
            }
        });
    }

    private void fetchNotifications() {
        ProgressDialog.getInstance().show(getActivity());
        Call<NotificationResponse> call = apiService.fetchNotifications(new PreferenceManager(getActivity()).getInt(Constant.USER_ID), getDeviceId());//todo change userId
        call.enqueue(new Callback<NotificationResponse>() {
            @Override
            public void onResponse(Call<NotificationResponse> call, Response<NotificationResponse> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.body() != null && response.body().getResponse().equals("200")) {
                    mViewModel.setNotificationList(response.body().getNotifications());
                } else {
                    showErrorDialog(getActivity(),response.body().getResponse(),response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<NotificationResponse> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                t.printStackTrace();
            }
        });
    }
}
