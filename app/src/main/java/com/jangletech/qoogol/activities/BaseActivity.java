package com.jangletech.qoogol.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.service.NetworkSchedulerService;
import com.jangletech.qoogol.util.AppUtils;
import com.jangletech.qoogol.util.NetworkUtil;


public class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        //scheduleJob();

    }

   /* @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void scheduleJob() {
        JobInfo myJob = new JobInfo.Builder(0, new ComponentName(this, NetworkSchedulerService.class))
                .setRequiresCharging(true)
                .setMinimumLatency(1000)
                .setOverrideDeadline(2000)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .build();

        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(myJob);
    }*/

    @Override
    protected void onStart() {
        super.onStart();
       /* Intent startServiceIntent = new Intent(this, NetworkSchedulerService.class);
        startService(startServiceIntent);*/
    }

    public void noInternetConnection(View view, String text,Context mContext) {

        if (text.equalsIgnoreCase(AppUtils.NOT_CONNECTED)) {
            Snackbar snackbar = Snackbar
                    .make(view, "Could not connect to internet", Snackbar.LENGTH_INDEFINITE)
                    .setActionTextColor(Color.YELLOW)
                    .setAction("RETRY", v -> {
                        //boolean isConnected = ConnectivityReceiver.isConnected(this);
                        if (!isConnected(this)) {
                            noInternetConnection(view, AppUtils.NOT_CONNECTED,mContext);
                        } else {
                            noInternetConnection(view, AppUtils.CONNECTED,mContext);
                        }
                    });

            View v = snackbar.getView();
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) v.getLayoutParams();
            params.gravity = Gravity.TOP;

            v.setBackgroundColor(ContextCompat.getColor(this, R.color.color_red));
            v.setLayoutParams(params);

            snackbar.show();

            TextView tv = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
            tv.setTextColor(Color.WHITE);
            snackbar.show();
        }

       /* if (text.equalsIgnoreCase(AppUtils.CONNECTED)) {
            Snackbar snackbar = Snackbar.make(view, "We are back...", Snackbar.LENGTH_LONG);
            TextView tv = snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
            tv.setTextColor(Color.WHITE);

            View v = snackbar.getView();
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) v.getLayoutParams();
            params.gravity = Gravity.TOP;

            v.setBackgroundColor(ContextCompat.getColor(this, R.color.color_green));
            v.setLayoutParams(params);

            snackbar.show();
        }*/
    }

    public static boolean hasError(ViewGroup viewGroup) {
        boolean result = false;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            if (viewGroup.getChildAt(i) instanceof TextInputLayout) {
                if (((TextInputLayout) viewGroup.getChildAt(i)).getError() != null) {
                    result = true;
                }
            }
        }
        Log.d(TAG, "hasError: "+result);
        return result;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        stopService(new Intent(this, NetworkSchedulerService.class));
        super.onStop();
    }

    public static boolean isConnected(Context context) {
        int conn = NetworkUtil.getConnectivityStatus(context);
        boolean status = false;
        if (conn == NetworkUtil.TYPE_WIFI) {
            status = true; //"Wifi enabled";
        } else if (conn == NetworkUtil.TYPE_MOBILE) {
            status = true; //"Mobile data enabled";
        } else if (conn == NetworkUtil.TYPE_NOT_CONNECTED) {
            status = false; //"Not connected to Internet";
        }
        return status;
    }

    public boolean checkNetworkConnection(View view,Context mContext){
        boolean status = false;
        if(!isConnected(this)){
            noInternetConnection(view,AppUtils.NOT_CONNECTED,mContext);
            status = false;
        }else{
            noInternetConnection(view,AppUtils.CONNECTED,mContext);
            status = true;
        }
        return status;
    }

}
