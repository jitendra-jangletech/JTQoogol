package com.jangletech.qoogol.activities;

import android.app.LauncherActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.ActivityLaunchBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.model.RegisterLoginModel;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.facebook.FacebookSdk.getApplicationContext;


public class LaunchActivity extends BaseActivity {

    private static final String TAG = "LaunchActivity";
    private ActivityLaunchBinding mBinding;
    private RegisterLoginViewModel mViewModel;
    ApiInterface apiService = ApiClient.getInstance().getApi();
    RegisterLoginModel registerLoginModel;
    boolean isOtpSent = false;
    int countryCode = 91; //todo country code hardcoded
    String strMobile = "";
    String strPasswordOtp = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_launch);
        mViewModel = new ViewModelProvider(this).get(RegisterLoginViewModel.class);
        performAutoLogin();
        mViewModel.getRegisterLoginData().observe(this, new Observer<RegisterLoginModel>() {
            @Override
            public void onChanged(@Nullable final RegisterLoginModel loginResponseModel) {
                registerLoginModel = loginResponseModel;
            }
        });

        mBinding.btnLoginRegister.setOnClickListener(v -> {
            mBinding.etMobileNumber.setError(null);
            mBinding.etPassOTP.setError(null);
            if (isOtpSent && mBinding.etPassOTP.getText().toString().isEmpty()) {
                mBinding.etPassOTP.setError("Please enter valid password or otp");
                return;
            }

            strMobile = mBinding.etMobileNumber.getText().toString().trim();
            //if(strMobile.equals(new PreferenceManager(getApplicationContext()).getString(Constant.MOBILE)))
            //strMobile = new PreferenceManager(getApplicationContext()).getString(Constant.MOBILE);//mBinding.etMobileNumber.getText().toString().trim();
            strPasswordOtp = mBinding.etPassOTP.getText().toString().trim();
            if (isOtpSent && registerLoginModel.getNewOTP().equals(strPasswordOtp)) {
                doRegisterLogin(strMobile, "2", countryCode, strPasswordOtp, getDeviceId(), "Q");
                return;
            } else if(isOtpSent && (strPasswordOtp.length() == 4) && !registerLoginModel.getNewOTP().equals(strPasswordOtp) ){
                mBinding.etPassOTP.setError("Please enter valid otp");
                return;
            }else if(!isOtpSent && (strPasswordOtp.length() >= 8)){
                doRegisterLogin(strMobile, "2", countryCode, strPasswordOtp, getDeviceId(), "Q");
            }else if(!isOtpSent && strPasswordOtp.isEmpty()){
                mBinding.etPassOTP.setError("Please enter otp or password.");
                return;
            }else if(!isOtpSent && strPasswordOtp.length() < 8){
                mBinding.etPassOTP.setError("Please enter otp or password.");
                return;
            }
        });

        mBinding.resendOtp.setOnClickListener(v -> {
            mBinding.etPassOTP.setError(null);
            mBinding.etPassOTP.setText("");
            mBinding.etMobileNumber.setError(null);
            if (mBinding.etMobileNumber.getText().toString().isEmpty()) {
                mBinding.etMobileNumber.setError("Please enter valid email or mobile");
                return;
            }
            //if contains only digits consider mobile number
            if (mBinding.etMobileNumber.getText().toString().trim().matches("[0-9]+") &&
                    mBinding.etMobileNumber.getText().toString().trim().length() < 10) {
                mBinding.etMobileNumber.setError("Please enter valid mobile number");
                return;
            }
            strMobile = mBinding.etMobileNumber.getText().toString().trim();
            new PreferenceManager(getApplicationContext()).saveString(Constant.MOBILE,strMobile);
            if (!isOtpSent) {
                doRegisterLogin(strMobile, isOtpSent ? "R" : "1", countryCode, "", getDeviceId(), "Q");
            } else {
                doRegisterLogin(strMobile, isOtpSent ? "R" : "1", countryCode, "", getDeviceId(), "Q");
            }
        });
    }

    private void performAutoLogin() {
        boolean isLoggedIn = new PreferenceManager(LaunchActivity.this).isLoggedIn();
        if (isLoggedIn) {
            Intent i = new Intent(LaunchActivity.this, MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        }
    }

    private void doRegisterLogin(String mobile, String caseR, int countryCode, String passwordOtp, String deviceId, String appName) {
        Log.d(TAG, "Mobile : " + mobile);
        ProgressDialog.getInstance().show(this);
        Call<RegisterLoginModel> call = apiService.doRegisterLogin(mobile, caseR, countryCode, passwordOtp, deviceId, appName, new PreferenceManager(getApplicationContext()).getToken());
        call.enqueue(new Callback<RegisterLoginModel>() {
            @Override
            public void onResponse(Call<RegisterLoginModel> call, Response<RegisterLoginModel> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.body() != null && response.body().getResponse().equals("200")) {
                    mViewModel.setRegisterLoginModel(response.body());
                    if (caseR.equals("1") || caseR.equals("R")) {
                        new PreferenceManager(LaunchActivity.this).saveString(Constant.MOBILE, mobile);
                        isOtpSent = true;
                        setTimer();
                    } else {
                        if (!response.body().getU_user_id().isEmpty()) {
                            new PreferenceManager(LaunchActivity.this).saveInt(Constant.USER_ID, Integer.parseInt(response.body().getU_user_id()));
                            new PreferenceManager(LaunchActivity.this).saveUserId(response.body().getU_user_id());
                            new PreferenceManager(LaunchActivity.this).setIsLoggedIn(true);
                            Intent i = new Intent(LaunchActivity.this, MainActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                        }
                    }
                } else {
                    showErrorDialog(LaunchActivity.this,response.body().getResponse(),response.body().getMessage());
                    showToast("Error Code : " + response.body().getResponse());
                }
            }

            @Override
            public void onFailure(Call<RegisterLoginModel> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                showToast("Something went wrong!!");
                t.printStackTrace();
            }
        });
    }

    private void setTimer() {
        new CountDownTimer(120000, 1000) {
            public void onTick(long millisUntilFinished) {
                mBinding.resendOtp.setEnabled(false);
                mBinding.resendOtp.setText(String.format(Locale.ENGLISH, "%d:%d sec", millisUntilFinished / (60 * 1000) % 60, millisUntilFinished / 1000 % 60));
            }

            public void onFinish() {
                mBinding.resendOtp.setText(getResources().getString(R.string.resend_otp));
                mBinding.resendOtp.setEnabled(true);
            }
        }.start();
    }
}
