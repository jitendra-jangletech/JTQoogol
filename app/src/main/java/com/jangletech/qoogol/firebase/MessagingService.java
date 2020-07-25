package com.jangletech.qoogol.firebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.jangletech.qoogol.util.NotificationHelper;
import com.jangletech.qoogol.util.PreferenceManager;

import java.util.Map;

/**
 * Created by Pritali on 5/20/2020.
 */
public class MessagingService extends FirebaseMessagingService {

    private static final String TAG = "MessagingService";
    private PreferenceManager mSettings;

    @Override
    public void onCreate() {
        super.onCreate();
        mSettings = new PreferenceManager(getApplicationContext());
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String, String> body = remoteMessage.getData();
        Log.i(TAG, "onMessageReceived : " + remoteMessage + "Remote data: " + body);
        if (!body.isEmpty()) {
            NotificationHelper.showNotification(getApplicationContext(), body);
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.i(TAG, "Token : " + token);
        new PreferenceManager(getApplicationContext()).saveToken(token);
    }
}
