package com.jangletech.qoogol.ui.settings;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.jangletech.qoogol.BuildConfig;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.activities.RegisterLoginActivity;
import com.jangletech.qoogol.databinding.SettingsFragmentBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.dialog.UniversalDialog;
import com.jangletech.qoogol.model.UserProfile;
import com.jangletech.qoogol.model.UserProfileResponse;
import com.jangletech.qoogol.model.VerifyResponse;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.ui.personal_info.PersonalInfoViewModel;
import com.jangletech.qoogol.util.AppUtils;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;
import com.jangletech.qoogol.util.QoogolApp;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsFragment extends BaseFragment implements UniversalDialog.DialogButtonClickListener {

    private static final String TAG = "SettingsFragment";
    private SettingsViewModel mViewModel;
    private PersonalInfoViewModel personalInfoViewModel;
    private Context mContext;
    private UserProfile profile = new UserProfile();
    private SettingsFragmentBinding mBinding;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.settings_fragment, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(SettingsViewModel.class);
        personalInfoViewModel = ViewModelProviders.of(this).get(PersonalInfoViewModel.class);

        fetchUserProfile();
        personalInfoViewModel.getUserProfile(getUserId(getActivity())).observe(getViewLifecycleOwner(), userProfile -> {
            Log.d(TAG, "onChanged : " + userProfile);
            if (userProfile != null) {
                profile = userProfile;
                setMuteCheckBox(userProfile.getNotificationEnabled());
            }
        });


        mBinding.tvLogout.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle);
            builder.setTitle("Confirm Logout")
                    .setMessage("Are you sure, you want to logout?")
                    .setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            logout("O");
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();

        });

        mBinding.tvMoreSettings.setOnClickListener(v -> {
            if (isAppInstalled()) {
                Bundle bundle = new Bundle();
                Intent LaunchIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://chatchilli.com"));
                bundle.putString(Constant.u_user_id, getUserId(getActivity()));
                bundle.putString("SCREEN", "SETTINGS");
                LaunchIntent.putExtras(bundle);
                bundle.putString(Constant.device_id, getDeviceId(getActivity()));
                LaunchIntent.putExtra(Intent.ACTION_VIEW, bundle);
                startActivity(LaunchIntent);
            } else {
                Log.i(TAG, "Application is not currently installed.");
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle);
                builder.setTitle("Alert")
                        .setMessage("Chatchilli App is not installed on this device.\n " +
                                "Please install app to explore more things.")
                        .setPositiveButton("Install", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String appPackageName = "com.jangletech.chatchilli"; //
                                try {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                                } catch (android.content.ActivityNotFoundException anfe) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                                }
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

        mBinding.tvMuteGroupAlerts.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle);
            builder.setTitle("Alert")
                    .setMessage("Change in this setting will also effect in CHATCHILLI App, associated with same mobile number.\n" +
                            "would you like to continue?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d(TAG, "onClick Network : " + QoogolApp.getInstance().hasNetwork());
                            if (QoogolApp.getInstance().hasNetwork()) {
                                profile.setNotificationEnabled(mBinding.tvMuteGroupAlerts.isChecked() ? "1" : "0");
                                updateNotificationSetting(profile);
                            } else {
                                setMuteCheckBox(profile.getNotificationEnabled());
                            }
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setMuteCheckBox(profile.getNotificationEnabled());
                        }
                    })
                    .setCancelable(false)
                    .show();
        });

        mBinding.tvBlockedList.setOnClickListener(v -> {
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.nav_blocked_connections);
        });

        mBinding.tvDeactivateAccount.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.AlertDialogStyle);
            builder.setTitle("Deactivate Account")
                    .setMessage("Are you sure, you want to deactivate account?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            logout("X");
                        }
                    }).setNegativeButton("No", null)
                    .show();
        });
    }

    private void setMuteCheckBox(String flag) {
        if (flag.equalsIgnoreCase("true")) {
            mBinding.tvMuteGroupAlerts.setChecked(true);
        } else {
            mBinding.tvMuteGroupAlerts.setChecked(false);
        }
    }

    private void logout(String status) {
        ProgressDialog.getInstance().show(getActivity());
        Call<VerifyResponse> call = getApiService().logout(
                getUserId(getActivity()),
                getDeviceId(getActivity()),
                status,
                Constant.APP_NAME);

        call.enqueue(new Callback<VerifyResponse>() {
            @Override
            public void onResponse(Call<VerifyResponse> call, Response<VerifyResponse> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.body() != null) {
                    if (response.body().getResponse().equals("200")) {
                        new PreferenceManager(mContext).setIsLoggedIn(false);
                        new PreferenceManager(mContext).saveString(Constant.MOBILE, "");
                        new PreferenceManager(mContext).saveString(Constant.USER_ID, "");
                        Intent intent = new Intent(mContext, RegisterLoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        AppUtils.showToast(getActivity(), null, response.body().getErrorMsg());
                    }
                }
            }

            @Override
            public void onFailure(Call<VerifyResponse> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                apiCallFailureDialog(t);
                t.printStackTrace();
            }
        });
    }

    private void updateNotificationSetting(UserProfile userProfile) {
        Log.d(TAG, "updateNotificationSetting Mobile Length : "+userProfile.getMobileNumber().length());
        Call<UserProfileResponse> call = getApiService().updateUserProfile(
                getUserId(getActivity()),
                Constant.APP_NAME,
                BuildConfig.VERSION_NAME,
                getDeviceId(getActivity()),
                userProfile.getFirstName(),
                userProfile.getLastName(),
                "'n'",
                userProfile.getMobileNumber(),
                userProfile.getEmailAddress(),
                "'i'",
                userProfile.getPassword(),
                userProfile.getDob(),
                userProfile.getStrTagLine(),
                userProfile.getCityId(),
                userProfile.getNativeStateId(),
                userProfile.getNativeDistrictId(),
                userProfile.getU_NationalityId(),
                userProfile.getW_lm_id_array(),
                userProfile.getStrGender(),
                userProfile.getUserName(),
                userProfile.isPrivate() ? "1" : "0",
                mBinding.tvMuteGroupAlerts.isChecked() ? "1" : "0"
        );

        call.enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                ProgressDialog.getInstance().dismiss();
                if (response != null && response.body() != null) {
                    if (response.body().getResponseCode().equals("200")) {
                        showToast("Updated Successfully.");
                    } else {
                        showErrorDialog(requireActivity(), response.body().getResponseCode(), response.body().getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                Log.d(TAG, "onFailure: " + profile.getNotificationEnabled());
                setMuteCheckBox(profile.getNotificationEnabled());
                apiCallFailureDialog(t);
            }
        });
    }

    private void fetchUserProfile() {
        ProgressDialog.getInstance().show(getActivity());
        Call<UserProfile> call = getApiService().fetchUserInfo(getUserId(getActivity()), getDeviceId(getActivity()), Constant.APP_NAME, BuildConfig.VERSION_NAME);
        call.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.body() != null && response.body().getResponseCode().equals("200")) {
                    personalInfoViewModel.insert(response.body());
                } else if (response.body().getResponseCode().equals("501")) {
                    resetSettingAndLogout();
                } else {
                    AppUtils.showToast(getActivity(), null, response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                t.printStackTrace();
                apiCallFailureDialog(t);
            }
        });
    }

    @Override
    public void onPositiveButtonClick() {
        logout("O");
    }
}