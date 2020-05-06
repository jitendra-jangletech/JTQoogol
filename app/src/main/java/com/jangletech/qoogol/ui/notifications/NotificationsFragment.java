package com.jangletech.qoogol.ui.notifications;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.NotificationAdapter;
import com.jangletech.qoogol.databinding.FragmentNotificationsBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.model.FetchSubjectResponseList;
import com.jangletech.qoogol.model.Notification;
import com.jangletech.qoogol.model.NotificationResponse;
import com.jangletech.qoogol.model.TestModelNew;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsFragment extends Fragment {

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
        mBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_notifications, container, false);
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
                Log.d(TAG, "onChanged Size : " + notifications.size());
                mBinding.notificationRecyclerView.setHasFixedSize(true);
                mBinding.notificationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                NotificationAdapter notificationAdapter = new NotificationAdapter(getActivity(),notifications);
                mBinding.notificationRecyclerView.setAdapter(notificationAdapter);
            }
        });

    }

    private void fetchNotifications() {
        ProgressDialog.getInstance().show(getActivity());
        Call<NotificationResponse> call = apiService.fetchNotifications(1002,"49d48c300948655f");//todo change userId
        call.enqueue(new Callback<NotificationResponse>() {
            @Override
            public void onResponse(Call<NotificationResponse> call, Response<NotificationResponse> response) {
                ProgressDialog.getInstance().dismiss();
                mViewModel.setNotificationList(response.body().getNotifications());
            }

            @Override
            public void onFailure(Call<NotificationResponse> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                t.printStackTrace();
            }
        });
    }

}
