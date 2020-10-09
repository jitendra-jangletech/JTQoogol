package com.jangletech.qoogol.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.model.AppConfigResponse;
import com.jangletech.qoogol.model.MasterResponse;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.util.AESSecurities;
import com.jangletech.qoogol.util.AppUtils;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;
import com.jangletech.qoogol.util.TinyDB;

import java.text.ParseException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends BaseActivity {

    private static final String TAG = "SplashActivity";
    private ApiInterface apiService = ApiClient.getInstance().getApi();
    private SplashViewModel mViewModel;
    private boolean isLogged = true;
    private static final int MY_REQUEST_CODE = 2020;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mViewModel = new ViewModelProvider(this).get(SplashViewModel.class);
        isLogged = true;
        fetchAppConfig();
        mViewModel.getAppConfigResponse().observe(this, new Observer<AppConfigResponse>() {
            @Override
            public void onChanged(AppConfigResponse appConfigResponse) {
                if (appConfigResponse != null) {
                    String masterKey = "";
                    try {
                        masterKey = AESSecurities.getMasterKey(appConfigResponse.getAlgo(), AppUtils.getDeviceId(), appConfigResponse.getDateTime());
                    } catch (ParseException e) {
                        e.printStackTrace();
                        Log.e(TAG, "onChanged Error : " + e.getMessage());
                    }
                    Log.d(TAG, "onChanged MasterKey : " + masterKey);
                    Log.d(TAG, "onChanged First Name key : " + AESSecurities.getInstance().decrypt("1" + masterKey, appConfigResponse.getFirstNameKey()));
                    Log.d(TAG, "onChanged Last Name key : " + AESSecurities.getInstance().decrypt("2" + masterKey, appConfigResponse.getLastNameKey()));
                    Log.d(TAG, "onChanged Dob key : " + AESSecurities.getInstance().decrypt("3" + masterKey, appConfigResponse.getDobKey()));
                    Log.d(TAG, "onChanged Mobile key : " + AESSecurities.getInstance().decrypt("4" + masterKey, appConfigResponse.getMobileKey()));
                    Log.d(TAG, "onChanged Email key : " + AESSecurities.getInstance().decrypt("5" + masterKey, appConfigResponse.getEmailKey()));
                    Log.d(TAG, "onChanged Password key : " + AESSecurities.getInstance().decrypt("6" + masterKey, appConfigResponse.getPasswordKey()));
                    new PreferenceManager(SplashActivity.this).setImageSize(Integer.parseInt(appConfigResponse.getCf_image_mb()));


                    TinyDB.getInstance(SplashActivity.this).putString(Constant.cf_key1, AESSecurities.getInstance().decrypt("1" + masterKey, appConfigResponse.getFirstNameKey()));
                    TinyDB.getInstance(SplashActivity.this).putString(Constant.cf_key2, AESSecurities.getInstance().decrypt("2" + masterKey, appConfigResponse.getLastNameKey()));
                    TinyDB.getInstance(SplashActivity.this).putString(Constant.cf_key3, AESSecurities.getInstance().decrypt("3" + masterKey, appConfigResponse.getDobKey()));
                    TinyDB.getInstance(SplashActivity.this).putString(Constant.cf_key4, AESSecurities.getInstance().decrypt("4" + masterKey, appConfigResponse.getMobileKey()));
                    TinyDB.getInstance(SplashActivity.this).putString(Constant.cf_key5, AESSecurities.getInstance().decrypt("5" + masterKey, appConfigResponse.getEmailKey()));
                    TinyDB.getInstance(SplashActivity.this).putString(Constant.cf_key6, AESSecurities.getInstance().decrypt("6" + masterKey, appConfigResponse.getPasswordKey()));
                    fetchMasterData();
                }
            }
        });
    }

    private void fetchAppConfig() {
        Log.d(TAG, "fetchAppConfig DeviceId : " + getDeviceId());
        Call<AppConfigResponse> call = apiService.fetchAppConfig(Constant.APP_NAME, AppUtils.getDeviceId());
        call.enqueue(new Callback<AppConfigResponse>() {
            @Override
            public void onResponse(Call<AppConfigResponse> call, Response<AppConfigResponse> response) {
                if (response.body() != null) {
                    Log.d(TAG, "onResponse: " + response.body());
                    mViewModel.insert(response.body());
                } else {
                    AppUtils.showToast(getApplicationContext(), null, response.body().getMessage());
                    showErrorDialog(SplashActivity.this, response.body().getResponse(), response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<AppConfigResponse> call, Throwable t) {
                AppUtils.showToast(getApplicationContext(), t, "");
                t.printStackTrace();
            }
        });
    }

    private void fetchMasterData() {
        Call<MasterResponse> call = apiService.fetchMaster("10");
        call.enqueue(new Callback<MasterResponse>() {
            @Override
            public void onResponse(Call<MasterResponse> call, Response<MasterResponse> response) {
                if (response.body() != null) {
                    if (response.body().getResponse() == 200) {
                        Log.d(TAG, "onResponse: " + response.body());
                        setMasterData(response.body());
                    } else {
                        showToast(response.body().getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<MasterResponse> call, Throwable t) {
                AppUtils.showToast(getApplicationContext(), t, "");
                t.printStackTrace();
                performAutoLogin();
            }
        });
    }

    private void setMasterData(MasterResponse body) {
        for (MasterResponse.Master master : body.getMasterList()) {
            Log.d(TAG, "setMasterData: " + master.getMdt_id() + " = " + master.getMdt_desc());
            TinyDB.getInstance(this).putString(master.getMdt_id(), master.getMdt_desc());
        }
        performAutoLogin();
    }

    private void performAutoLogin() {
        Log.d(TAG, "performAutoLogin: ");
        if (isLogged) {
            isLogged = false;
            boolean isLoggedIn = new PreferenceManager(SplashActivity.this).isLoggedIn();
            if (isLoggedIn) {
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            } else {
                Intent i = new Intent(SplashActivity.this, RegisterLoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
            }
        }
    }

   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 2020) {
            Log.d(TAG, "onActivityResult: " + resultCode);
            forceAppUpdate();
        }
    }*/

    /*private void forceAppUpdate() {
        // Creates instance of the manager.
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(SplashActivity.this);
        // Returns an intent object that you use to check for an update.
        com.google.android.play.core.tasks.Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            AppUpdateType.IMMEDIATE,
                            this,
                            MY_REQUEST_CODE
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                    appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {
                try {
                    appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.IMMEDIATE, this, MY_REQUEST_CODE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });
    }*/
}