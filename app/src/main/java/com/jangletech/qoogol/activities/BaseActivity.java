package com.jangletech.qoogol.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.VectorDrawable;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.util.AppUtils;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.NetworkUtil;
import com.jangletech.qoogol.util.PreferenceManager;

import java.net.UnknownHostException;
import java.util.Objects;


public class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
    }

    public void saveString(String key, String value) {
        new PreferenceManager(getApplicationContext()).saveString(key, value);
    }

    public static void expand(final View v) {
        int matchParentMeasureSpec = View.MeasureSpec.makeMeasureSpec(((View) v.getParent()).getWidth(), View.MeasureSpec.EXACTLY);
        int wrapContentMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        v.measure(matchParentMeasureSpec, wrapContentMeasureSpec);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int) (targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Expansion speed of 1dp/ms
        a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                } else {
                    v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Collapse speed of 1dp/ms
        a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
        v.startAnimation(a);
    }

    public void clearFilters() {
        saveString(Constant.tm_diff_level, "");
        saveString(Constant.tm_avg_rating, "");
    }

    public void apiCallFailureDialog(Throwable t) {
        if (t instanceof UnknownHostException) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
            builder.setTitle("Alert")
                    .setMessage("Check your internet connection.")
                    .setPositiveButton("OK", null)
                    .show();

        }
    }

    public String getSingleQuoteString(String text) {
        return String.format("'%s'", text);
    }

    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    public void resetSettingAndLogout() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
        alertDialog.setTitle(getResources().getString(R.string.warning));
        alertDialog.setMessage("You have signed-in from another device. Logging out.");
        alertDialog.setPositiveButton("Ok", (dialog, which) -> {
            new PreferenceManager(getApplicationContext()).setIsLoggedIn(false);
            new PreferenceManager(getApplicationContext()).saveString(Constant.MOBILE, "");
            new PreferenceManager(getApplicationContext()).saveString(Constant.USER_ID, "");
            Intent intent = new Intent(this, LaunchActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            Objects.requireNonNull(this).finish();
            dialog.dismiss();
        });
        alertDialog.setCancelable(false)
                .show();
    }

    public void loadProfilePic(String url, ImageView imageView) {
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .circleCrop()
                .placeholder(R.drawable.load)
                .error(R.drawable.ic_profile_default);
        Glide.with(this).load(url)
                .apply(options)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    //To Device get Android Id
    public String getDeviceId() {
        return Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    public void showErrorDialog(Activity activity, String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.AlertDialogStyle);
        builder.setTitle("Error Code : " + title)
                .setMessage(msg)
                .setPositiveButton("OK", null)
                .show();
    }

    public String getProfileImageUrl(String imageName) {
        String userId = String.valueOf(new PreferenceManager(getApplicationContext()).getInt(Constant.USER_ID));
        return Constant.PRODUCTION_BASE_FILE_API + "000000" + userId + "/" + imageName;
    }

    public void setMargins(View view) {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }

        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(0, result, 0, 0);
            view.requestLayout();
        }
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

    public String getString(String key) {
        return new PreferenceManager(getApplicationContext()).getString(key);
    }

    public static Bitmap getBitmap(VectorDrawable vectorDrawable) {
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }
}
