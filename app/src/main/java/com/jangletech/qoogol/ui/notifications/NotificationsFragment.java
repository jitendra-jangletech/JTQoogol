package com.jangletech.qoogol.ui.notifications;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuItemCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.jangletech.qoogol.util.AppUtils;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsFragment extends BaseFragment implements NotificationAdapter.onItemClickListener, PublicProfileDialog.PublicProfileClickListener, SearchView.OnQueryTextListener {

    private static final String TAG = "NotificationsFragment";
    private NotificationsViewModel mViewModel;
    private FragmentNotificationsBinding mBinding;
    private Context mContext;
    private NotificationResponse notificationResponse;
    private NotificationAdapter notificationAdapter;
    private Boolean isScrolling = false;
    private String pageStart = "0";
    private LinearLayoutManager linearLayoutManager;
    private int currentItems, scrolledOutItems, totalItems;
    private ApiInterface apiService = ApiClient.getInstance().getApi();
    private NotificationAdapter.onItemClickListener onItemClickListener;
    private List<Notification> notificationList = new ArrayList<>();

    public static NotificationsFragment newInstance() {
        return new NotificationsFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context.getApplicationContext();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_notifications, container, false);
        mBinding.setLifecycleOwner(this);
        linearLayoutManager = new LinearLayoutManager(getContext());
        mBinding.notificationRecyclerView.setHasFixedSize(true);
        mBinding.notificationRecyclerView.setLayoutManager(linearLayoutManager);
        notificationAdapter = new NotificationAdapter(getActivity(), notificationList, this);
        notificationAdapter.setHasStableIds(true);
        mBinding.notificationRecyclerView.setAdapter(notificationAdapter);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(NotificationsViewModel.class);
        fetchNotifications(pageStart);
        onItemClickListener = this;
        mViewModel.getAllNotifications(getUserId(getActivity())).observe(getViewLifecycleOwner(), new Observer<List<Notification>>() {
            @Override
            public void onChanged(@Nullable final List<Notification> notifications) {
                Log.d(TAG, "onChanged: " + notifications.size());
                if (notifications != null) {
                    if (mBinding.swipeToRefresh.isRefreshing())
                        mBinding.swipeToRefresh.setRefreshing(false);
                    if (notificationResponse != null)
                        pageStart = notificationResponse.getPage();
                    notificationList = notifications;
                    notificationAdapter.updateNotificationList(notifications);
                    if (notifications.size() > 0) {
                        mBinding.tvNoTest.setVisibility(View.GONE);
                    } else {
                        mBinding.tvNoTest.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        mBinding.swipeToRefresh.setOnRefreshListener(() -> fetchNotifications(pageStart));
        mBinding.notificationRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = linearLayoutManager.getChildCount();
                totalItems = linearLayoutManager.getItemCount();
                scrolledOutItems = linearLayoutManager.findFirstVisibleItemPosition();
                if (dy > 0) {
                    //hideBottomNav();
                    if (isScrolling && (currentItems + scrolledOutItems == totalItems)) {
                        isScrolling = false;
                        fetchNotifications(pageStart);
                    } else {
                        //showBottomNav();
                    }
                }
            }
        });
        enableSwipe();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.action_conn_search, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);

        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return true;
            }
        });
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
        Call<ResponseObj> call = apiService.updateNotifications(new PreferenceManager(getActivity()).getUserId(), getDeviceId(getActivity()), "Q", n_id, "D");
        call.enqueue(new Callback<ResponseObj>() {
            @Override
            public void onResponse(Call<ResponseObj> call, Response<ResponseObj> response) {
                mBinding.swipeToRefresh.setRefreshing(false);
                if (response.body() != null && response.body().getResponse().equals("200")) {
                    deleteFromdb(n_id);
                    showToast("Notification deleted.");
                    fetchNotifications(pageStart);
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

    private void fetchNotifications(String pageStart) {
        Log.d(TAG, "fetchNotifications PageStart : " + pageStart);
        mBinding.swipeToRefresh.setRefreshing(true);
        Call<NotificationResponse> call = apiService.fetchNotifications(
                AppUtils.getUserId(),
                getDeviceId(getActivity()),
                "Q",
                pageStart
        );
        call.enqueue(new Callback<NotificationResponse>() {
            @Override
            public void onResponse(Call<NotificationResponse> call, Response<NotificationResponse> response) {
                if (response != null && response.body() != null) {
                    if (response.body().getResponse().equals("200")) {
                        notificationResponse = response.body();
                        mViewModel.insert(response.body().getNotifications());
                        if (response.body().getNotifications().size() == 0) {
                            mBinding.tvNoTest.setText("No Notifications.");
                        }
                    } else {
                        showErrorDialog(getActivity(), response.body().getResponse(), response.body().getMessage());
                    }
                }
                if (mBinding.swipeToRefresh.isRefreshing())
                    mBinding.swipeToRefresh.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<NotificationResponse> call, Throwable t) {
                mBinding.swipeToRefresh.setRefreshing(false);
                apiCallFailureDialog(t);
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

        PublicProfileDialog publicProfileDialog = new PublicProfileDialog(getActivity(), notification.getN_sent_by_u_id(), this);
        publicProfileDialog.show();
    }

    @Override
    public void onFriendUnFriendClick() {

    }

    @Override
    public void onFollowUnfollowClick() {

    }

    @Override
    public void onViewImage(String path) {
        showFullScreen(path);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.d(TAG, "onQueryTextChange Text : " + newText.trim().toLowerCase());
        return true;
    }
}
