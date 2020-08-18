package com.jangletech.qoogol.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.NotificationItemBinding;
import com.jangletech.qoogol.model.Notification;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.DateUtils;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<Notification> notifications;
    private Context mContext;
    private NotificationItemBinding itemBinding;
    private onItemClickListener onItemClickListener;

    public NotificationAdapter(Context context, List<Notification> notifications, onItemClickListener onItemClickListener) {
        this.mContext = context;
        this.notifications = notifications;
        this.onItemClickListener = onItemClickListener;
    }


    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                R.layout.notification_item, parent, false);
        return new NotificationAdapter.ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.ViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        holder.itemBinding.notificationText.setText(notification.getW_notification_desc());
        if (notification.getN_cdatetime() != null)
            holder.itemBinding.tvDate.setText(DateUtils.localeDateFormat(notification.getN_cdatetime()));
        //holder.itemBinding.tvDate.setText(DateUtils.getFormattedDate(notification.getN_cdatetime().substring(0,10)));
        Glide.with(mContext)
                .load(getImageUrl(notification))
                .circleCrop()
                .placeholder(R.drawable.profile)
                .error(R.drawable.profile)
                .apply(RequestOptions.circleCropTransform())
                .into(holder.itemBinding.profilePic);

        holder.itemBinding.getRoot().setOnClickListener(v ->
                onItemClickListener.onItemClick(notifications.get(position)));

    }


    public interface onItemClickListener {
        void onItemClick(Notification notification);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        NotificationItemBinding itemBinding;

        public ViewHolder(@NonNull NotificationItemBinding itemView) {
            super(itemView.getRoot());
            this.itemBinding = itemView;
        }
    }

    public void updateNotificationList(List<Notification> notificationsList) {
        notifications = notificationsList;
        notifyDataSetChanged();
    }

    public String getImageUrl(Notification notification) {
        String paddedString = "";
        if (!notification.getW_user_profile_image_name().isEmpty()) {
            paddedString = Constant.PRODUCTION_BASE_FILE_API
                    + notification.getW_user_profile_image_name();
        }
        return paddedString;
    }
}
