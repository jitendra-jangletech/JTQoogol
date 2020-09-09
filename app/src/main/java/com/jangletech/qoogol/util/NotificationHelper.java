package com.jangletech.qoogol.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.jangletech.qoogol.BuildConfig;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.activities.MainActivity;

import java.util.Map;

import static androidx.core.app.NotificationCompat.PRIORITY_MAX;

/*
 *
 *
 *  * /////////////////////////////////////////////////////////////////////////////////////////////////
 *  * //
 *  * //            Copyright (c) 2020. JangleTech Systems Private Limited, Thane, India
 *  * //
 *  * /////////////////////////////////////////////////////////////////////////////////////////////////
 *
 */

public class NotificationHelper {

    public static final String REPLY_ACTION = "reply_action";
    public static final String REPLY_DISMISS = "dismiss_action";

    // Build notification for foreground service
    public static void showNotification(Context context, Map<String, String> body) {

        String title = body.get(Constant.FB_TITLE);
        String fromType = (body.get(Constant.FB_FROM_TYPE));
        String action = (body.get(Constant.FB_ACTION));
        String ms_id = (body.get(Constant.FB_MS_ID));
        String message = AppUtils.decodedString(body.get(Constant.FB_MSG_BODY));
        // Create the Foreground Service
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // Create an Intent for the activity you want to start
        Intent resultIntent = new Intent(context, MainActivity.class);
        Bundle bundle = new Bundle();
        bundle.putBoolean("fromNotification", true);
        bundle.putString(Constant.FB_FROM_TYPE, fromType);
        bundle.putString(Constant.FB_U_G_ID, body.get(Constant.FB_U_G_ID));
        bundle.putString(Constant.w_user_name, title);
        bundle.putString(Constant.FB_ACTION, action);
        bundle.putString(Constant.FB_MS_ID, ms_id);
        resultIntent.putExtra("bundle", bundle);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Create the TaskStackBuilder and add the intent, which inflates the back stack
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(resultIntent);
        // Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        if (notificationManager != null) {
            String channelId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? createNotificationChannel(notificationManager) : "";
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, channelId);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                notificationBuilder.setSmallIcon(R.drawable.logo);
                notificationBuilder.setColor(context.getResources().getColor(R.color.colorPrimary));
            } else {
                notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
            }
            notificationBuilder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_star));
            notificationBuilder.setPriority(PRIORITY_MAX)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                    .setContentIntent(resultPendingIntent);
            // notificationBuilder.addAction(R.drawable.ic_send,"Reply", getReplyAction(context));
            // notificationBuilder.addAction(R.drawable.ic_close,"Dismiss", getDismissAction(context));

            notificationManager.notify(1002 /* Request Code */, notificationBuilder.build());
        }
    }

    /*private static PendingIntent getReplyAction(Context context) {
        Intent yesReceive = new Intent();
        yesReceive.setAction(REPLY_ACTION);
        return PendingIntent.getBroadcast(context, 12345, yesReceive, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private static PendingIntent getDismissAction(Context context) {
        Intent yesReceive = new Intent();
        yesReceive.setAction(REPLY_DISMISS);
        return PendingIntent.getBroadcast(context, 12345, yesReceive, PendingIntent.FLAG_UPDATE_CURRENT);
    }*/

    @RequiresApi(Build.VERSION_CODES.O)
    private static String createNotificationChannel(NotificationManager notificationManager) {
        String channelId = BuildConfig.APPLICATION_ID + "message_channel_id";
        String channelName = "Message";
        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
        channel.setImportance(NotificationManager.IMPORTANCE_HIGH);
        channel.enableVibration(true);
        notificationManager.createNotificationChannel(channel);
        return channelId;
    }
}
