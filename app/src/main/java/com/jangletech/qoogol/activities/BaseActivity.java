package com.jangletech.qoogol.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.service.NetworkSchedulerService;
import com.jangletech.qoogol.util.AppUtils;
import com.jangletech.qoogol.util.NetworkUtil;

import static com.facebook.FacebookSdk.getApplicationContext;


public class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
    }

    //To Device get Android Id
    public String getDeviceId() {
        return Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    public void showErrorDialog(Activity activity,String title,String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity,R.style.AlertDialogStyle);
        builder.setTitle("Error Code : "+title)
                .setMessage(msg)
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void noInternetConnection(View view, String text, Context mContext) {

        if (text.equalsIgnoreCase(AppUtils.NOT_CONNECTED)) {
            Snackbar snackbar = Snackbar
                    .make(view, "Could not connect to internet", Snackbar.LENGTH_INDEFINITE)
                    .setActionTextColor(Color.YELLOW)
                    .setAction("RETRY", v -> {
                        //boolean isConnected = ConnectivityReceiver.isConnected(this);
                        if (!isConnected(this)) {
                            noInternetConnection(view, AppUtils.NOT_CONNECTED, mContext);
                        } else {
                            noInternetConnection(view, AppUtils.CONNECTED, mContext);
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
        Log.d(TAG, "hasError: " + result);
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

    public boolean checkNetworkConnection(View view, Context mContext) {
        boolean status = false;
        if (!isConnected(this)) {
            noInternetConnection(view, AppUtils.NOT_CONNECTED, mContext);
            status = false;
        } else {
            noInternetConnection(view, AppUtils.CONNECTED, mContext);
            status = true;
        }
        return status;
    }

}
