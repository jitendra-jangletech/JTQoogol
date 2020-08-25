package com.jangletech.qoogol.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.textfield.TextInputLayout;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.activities.MainActivity;
import com.jangletech.qoogol.activities.RegisterLoginActivity;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;
import com.jangletech.qoogol.util.TinyDB;

import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;


public class BaseFragment extends Fragment {

    private static final String TAG = "BaseFragment";
    private SharedPreferences preferences;

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

    public static String getDefinedTestCategory(String strCat) {
        String defCat = "";
        if (strCat != null) {
            if (strCat.equalsIgnoreCase("A")) {
                defCat = "Annual-Practice";
            }
            if (strCat.equalsIgnoreCase("U")) {
                defCat = "Unit Test-Practice";
            }
            if (strCat.equalsIgnoreCase("S")) {
                defCat = "Semester-Practice";
            }
        }
        return defCat;
    }

    public String getLocalString(String key) {
        return TinyDB.getInstance(getActivity()).getString(key);
    }

    public boolean isAppInstalled() {
        PackageManager pm = getActivity().getPackageManager();
        try {
            pm.getPackageInfo("com.jangletech.chatchilli", PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return false;
    }

    public void replaceFragment(int resId, Fragment fragment, Bundle bundle) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        String backStateName = fragment.getClass().getName();
        boolean fragmentPopped = fragmentManager.popBackStackImmediate(backStateName, 0);
        if (!fragmentPopped) {
            fragment.setArguments(bundle);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
            fragmentTransaction.replace(resId, fragment);
            fragmentTransaction.addToBackStack(backStateName);
            fragmentTransaction.commit();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void showFullScreen(final String profilePath) {
        final Dialog dialog = new Dialog(requireActivity(), android.R.style.Theme_Light);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.image_fullscreen_preview);
        ImageView imageView = dialog.findViewById(R.id.image_preview);
        imageView.setOnTouchListener(new ImageMatrixTouchHandler(dialog.getWindow().getContext()));

        Glide.with(requireActivity()).load(profilePath)
                .thumbnail(0.5f)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .dontTransform()
                .dontAnimate()
                .into(imageView);

        dialog.show();
    }


    public String getLanguageArray(Object languages) {
        String res = "";
        if (languages != null) {
            if (!languages.toString().isEmpty()) {
                String[] lang = languages.toString().split(",", -1);
                for (int i = 0; i < lang.length; i++) {
                    if (lang != null && !lang[i].isEmpty()) {
                        res = res + "," + lang[i];
                    }
                }
            }
        }

        if (res.startsWith(","))
            return res.replaceFirst(",", "");
        else if (res.endsWith(","))
            return res.substring(0, res.length() - 1);
        else
            return res;
    }

    public String getEmptyStringIfNull(String string) {
        return string != null ? string : "";
    }

    public String getStringValue(Object object) {
        return String.valueOf(object);
    }

    public ActionBar getActionBar() {
        return ((MainActivity) getActivity()).getSupportActionBar();
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

    public void loadImages(String url, ImageView imageView) {
        RequestOptions options = new RequestOptions()
                .placeholder(R.drawable.load);
        Glide.with(this).load(url)
                .apply(options)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imageView);
    }

    public void clearFilters() {
        saveString(getActivity(), Constant.tm_diff_level, "");
        saveString(getActivity(), Constant.tm_avg_rating, "");
    }

    public void resetSettingAndLogout() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(requireActivity(), R.style.AlertDialogStyle);
        alertDialog.setTitle(getActivity().getResources().getString(R.string.warning));
        alertDialog.setMessage("You have signed-in from another device. Logging out.");
        alertDialog.setPositiveButton("Ok", (dialog, which) -> {
            new PreferenceManager(getActivity()).setIsLoggedIn(false);
            new PreferenceManager(getActivity()).saveString(Constant.MOBILE, "");
            new PreferenceManager(getActivity()).saveString(Constant.USER_ID, "");
            Intent intent = new Intent(requireActivity(), RegisterLoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            Objects.requireNonNull(requireActivity()).finish();
            dialog.dismiss();
        });
        alertDialog.setCancelable(false).show();
    }

    public String getTempImageUrl(String path) {
        return Constant.QUESTION_IMAGES_API + path.split("/")[1].split(":")[0];
    }

    public void setFragmentTitle(String title) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(title);
    }


    public void clearErrors(ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            if (viewGroup.getChildAt(i) instanceof TextInputLayout) {
                if (((TextInputLayout) viewGroup.getChildAt(i)).getError() == null) {
                    ((TextInputLayout) viewGroup.getChildAt(i)).setError(null);
                }
            }
        }
    }

    public void showErrorDialog(Activity activity, String title, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.AlertDialogStyle);
        builder.setTitle("Error Code : " + title)
                .setMessage(msg)
                .setPositiveButton("OK", null)
                .show();
    }

    public void showErrorDialog(Activity activity, String title, String msg, String flag, String mobile) {
        Bundle bundle = new Bundle();
        bundle.putString(Constant.u_mob_1, mobile);
        Log.d(TAG, "showErrorDialog Mobile : " + mobile);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.AlertDialogStyle);
        builder.setTitle("Error Code : " + title)
                .setMessage(msg)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (flag.equals("NEW_USER")) {
                            replaceFragment(R.id.container, new NewUserFragment(), bundle);
                        } else if (flag.equals("EXISTING_USER")) {
                            replaceFragment(R.id.container, new ExistingUserFragment(), bundle);
                        }
                    }
                })
                .show();
    }

    public String getMobileFromBundle(Bundle bundle) {
        if (bundle != null) {
            return bundle.getString(Constant.u_mob_1);
        }
        return "";
    }

    public void setTimer(TextView timer, int seconds, int minutes) {
        CountDownTimer countDownTimer = new CountDownTimer(60 * 1000 * 60, 1000) {
            int timerCountSeconds = seconds;
            int timerCountMinutes = minutes;

            public void onTick(long millisUntilFinished) {
                // timer.setText(new SimpleDateFormat("mm:ss").format(new Date( millisUntilFinished)));
                if (timerCountSeconds < 59) {
                    timerCountSeconds++;
                } else {
                    timerCountSeconds = 0;
                    timerCountMinutes++;
                }
                if (timerCountMinutes < 10) {
                    if (timerCountSeconds < 10) {
                        timer.setText(String.valueOf("0" + timerCountMinutes + ":0" + timerCountSeconds));
                    } else {
                        timer.setText(String.valueOf("0" + timerCountMinutes + ":" + timerCountSeconds));
                    }
                } else {
                    if (timerCountSeconds < 10) {
                        timer.setText(String.valueOf(timerCountMinutes + ":0" + timerCountSeconds));
                    } else {
                        timer.setText(String.valueOf(timerCountMinutes + ":" + timerCountSeconds));
                    }
                }
            }

            public void onFinish() {
                timer.setText("00:00");
            }
        }.start();
    }
    /*public void addFragment(Fragment fragment){
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragmentContainer, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }*/

    public void showToast(String msg) {
        try {
            Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveFilter(boolean value) {
        preferences = android.preference.PreferenceManager.getDefaultSharedPreferences(getActivity());
        preferences.edit()
                .putBoolean(Constant.TEST_FILTER_APPLIED, value)
                .apply();
    }

    public boolean getFilter(String key) {
        preferences = android.preference.PreferenceManager.getDefaultSharedPreferences(getActivity());
        return preferences.getBoolean(key, false);
    }

    public void setFilterIcon(Menu menu, Context mContext, boolean flag) {
        if (flag)
            menu.getItem(1).setIcon(ContextCompat.getDrawable(mContext, R.drawable.ic_filter_applied));
            //menuItem.setIcon(getActivity().getResources().getDrawable(R.drawable.ic_filter_applied));
        else
            menu.getItem(1).setIcon(ContextCompat.getDrawable(mContext, R.drawable.ic_filter));
        //menuItem.setIcon(getActivity().getResources().getDrawable(R.drawable.ic_filter));
    }

    public void apiCallFailureDialog(Throwable t) {
        if (t instanceof UnknownHostException) {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity(), R.style.AlertDialogStyle);
            builder.setTitle("Alert")
                    .setMessage("Check your internet connection.")
                    .setPositiveButton("OK", null)
                    .show();
        }
    }

    public String getSingleQuoteString(String text) {
        return String.format("'%s'", text);
    }

    //To Device get Android Id
    public static String getDeviceId(Context mContext) {
        return Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public String getString(String key) {
        return new PreferenceManager(getActivity()).getString(key);
    }

    public static void saveString(Context mContext, String key, String value) {
        new PreferenceManager(mContext).saveString(key, value);
    }

    public static String getUserId(Context mContext) {
        return new PreferenceManager(mContext).getUserId();
    }

    public static String getUserName(Context mContext) {
        return String.valueOf(new PreferenceManager(mContext).getString(Constant.userName));
    }

    public void dismissRefresh(SwipeRefreshLayout swipeRefreshLayout) {
        if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    public String getProfileImageUrl(String imageName) {
        //String userId = String.valueOf(new PreferenceManager(
        // getApplicationContext()).getInt(Constant.USER_ID));
        return Constant.PRODUCTION_BASE_FILE_API + imageName;
    }

    @SuppressLint("SimpleDateFormat")
    public static String convertDateToDataBaseFormat(String strDate) {
        String formattedDate = "";
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MMM-yyyy");
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = inputFormat.parse(strDate);
            assert date != null;
            formattedDate = outputFormat.format(date);
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
        return formattedDate;
    }
}
