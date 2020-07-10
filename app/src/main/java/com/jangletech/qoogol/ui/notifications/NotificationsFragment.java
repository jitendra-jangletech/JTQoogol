package com.jangletech.qoogol.ui.notifications;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.adapter.NotificationAdapter;
import com.jangletech.qoogol.databinding.FragmentNotificationsBinding;
import com.jangletech.qoogol.dialog.PublicProfileDialog;
import com.jangletech.qoogol.model.Notification;
import com.jangletech.qoogol.model.NotificationResponse;
import com.jangletech.qoogol.model.ResponseObj;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jangletech.qoogol.util.Constant.CALL_FROM;
import static com.jangletech.qoogol.util.Constant.connectonId;
import static com.jangletech.qoogol.util.Constant.fromTest;
import static com.jangletech.qoogol.util.Constant.from_question;
import static com.jangletech.qoogol.util.Constant.from_user;

public class NotificationsFragment extends BaseFragment implements NotificationAdapter.onItemClickListener, PublicProfileDialog.PublicProfileClickListener {

    private static final String TAG = "NotificationsFragment";
    private NotificationsViewModel mViewModel;
    private FragmentNotificationsBinding mBinding;
    private Context mContext;
    private NotificationAdapter notificationAdapter;
    ApiInterface apiService = ApiClient.getInstance().getApi();
    NotificationAdapter.onItemClickListener onItemClickListener;
    List<Notification> notificationList = new ArrayList<>();

    public static NotificationsFragment newInstance() {
        return new NotificationsFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context.getApplicationContext();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_notifications, container, false);
        mBinding.setLifecycleOwner(this);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(NotificationsViewModel.class);
        fetchNotifications();
        onItemClickListener = this;
        mViewModel.getAllNotifications(getUserId()).observe(getViewLifecycleOwner(), new Observer<List<Notification>>() {
            @Override
            public void onChanged(@Nullable final List<Notification> notifications) {
                Log.d(TAG, "onChanged: " + notifications.size());
                if (notifications != null) {
                    //showToast("Data Updated");
                    if (mBinding.swipeToRefresh.isRefreshing())
                        mBinding.swipeToRefresh.setRefreshing(false);
                    notificationList.clear();
                    notificationList.addAll(notifications);
                    mBinding.notificationRecyclerView.setHasFixedSize(true);
                    mBinding.notificationRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                    notificationAdapter = new NotificationAdapter(getActivity(), notifications, onItemClickListener);
                    mBinding.notificationRecyclerView.setAdapter(notificationAdapter);
                }
            }
        });
        mBinding.swipeToRefresh.setOnRefreshListener(() -> fetchNotifications());

        enableSwipe();
    }

    private void enableSwipe() {


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.RIGHT) {
                    int position = viewHolder.getAdapterPosition();
                    Notification notification = notificationList.get(position);
                    deleteNotification(notification.getN_id());
                }
            }
        }).attachToRecyclerView(mBinding.notificationRecyclerView);


    }

    private void deleteNotification(String n_id) {
        Call<ResponseObj> call = apiService.updateNotifications(new PreferenceManager(getActivity()).getUserId(), getDeviceId(), "Q", n_id, "D");
        call.enqueue(new Callback<ResponseObj>() {
            @Override
            public void onResponse(Call<ResponseObj> call, Response<ResponseObj> response) {
                //ProgressDialog.getInstance().dismiss();
                mBinding.swipeToRefresh.setRefreshing(false);
                if (response.body() != null && response.body().getResponse().equals("200")) {
                    deleteFromdb(n_id);
                    showToast("Notification deleted.");
                    fetchNotifications();
                }
            }

            @Override
            public void onFailure(Call<ResponseObj> call, Throwable t) {
                //ProgressDialog.getInstance().dismiss();
                mBinding.swipeToRefresh.setRefreshing(false);
                apiCallFailureDialog(t);
                Log.e(TAG, "onFailure NOtifications : " + t.getMessage());
                t.printStackTrace();
            }
        });
    }

    private void deleteFromdb(String n_id) {
        mViewModel.deleteNotification(n_id);
    }

    private void fetchNotifications() {
        //ProgressDialog.getInstance().show(getActivity());
        mBinding.swipeToRefresh.setRefreshing(true);
        Call<NotificationResponse> call = apiService.fetchNotifications(new PreferenceManager(getActivity()).getUserId(), getDeviceId(), "Q");
        call.enqueue(new Callback<NotificationResponse>() {
            @Override
            public void onResponse(Call<NotificationResponse> call, Response<NotificationResponse> response) {
                //ProgressDialog.getInstance().dismiss();
                mBinding.swipeToRefresh.setRefreshing(false);
                if (response.body() != null && response.body().getResponse().equals("200")) {
                    //mViewModel.setNotificationList(response.body().getNotifications());
                    //mViewModel.delete();
                    mViewModel.insert(response.body().getNotifications());
                } else {
                    showErrorDialog(getActivity(), response.body().getResponse(), response.body().getMessage());
                }
                if (mBinding.swipeToRefresh.isRefreshing())
                    mBinding.swipeToRefresh.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<NotificationResponse> call, Throwable t) {
                //ProgressDialog.getInstance().dismiss();
                mBinding.swipeToRefresh.setRefreshing(false);
                apiCallFailureDialog(t);
                Log.e(TAG, "onFailure NOtifications : " + t.getMessage());
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
        notificationAdapter = null;
    }

    @Override
    public void onItemClick(Notification notification) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("fromNotification", true);
        bundle.putString(Constant.FB_MS_ID, notification.getN_ref_id());
        if (notification.getN_ref_type().equalsIgnoreCase(from_user) ||
                notification.getN_ref_type().equalsIgnoreCase(Constant.from_close_friend) ||
                notification.getN_ref_type().equalsIgnoreCase(Constant.from_friend)) {
            //bundle.putInt(CALL_FROM, connectonId);
            //bundle.putString(Constant.fetch_profile_id, notification.getN_ref_id());
            //NavHostFragment.findNavController(this).navigate(R.id.nav_edit_profile, bundle);
            PublicProfileDialog publicProfileDialog = new PublicProfileDialog(getActivity(),notification.getN_sent_by_u_id(),this);
            publicProfileDialog.show();
        } else if (notification.getN_ref_type().equalsIgnoreCase(from_question)) {
            NavHostFragment.findNavController(this).navigate(R.id.nav_learning, bundle);
        } else if (notification.getN_ref_type().equalsIgnoreCase(fromTest)) {
            NavHostFragment.findNavController(this).navigate(R.id.nav_test_my, bundle);
        }
    }

    @Override
    public void onViewImage(String path) {
        showFullScreen(path);
    }
}
