package com.jangletech.qoogol.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.activities.LaunchActivity;
import com.jangletech.qoogol.activities.MainActivity;
import com.jangletech.qoogol.activities.RegisterLoginViewModel;
import com.jangletech.qoogol.databinding.FragmentExistingUserBinding;
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


public class ExistingUserFragment extends BaseFragment {

    private static final String TAG = "ExistingUserFragment";
    private FragmentExistingUserBinding mBinding;
    private ApiInterface apiService = ApiClient.getInstance().getApi();
    private boolean isOtpSent = false;
    private RegisterLoginViewModel mViewModel;
    private RegisterLoginModel registerLoginModel;
    private int countryCode = 91; //todo country code hardcoded
    private String strMobile = "";
    private String strPasswordOtp = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_existing_user, container, false);
        //performAutoLogin();
        initViews();
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(RegisterLoginViewModel.class);
        mViewModel.getRegisterLoginData().observe(getViewLifecycleOwner(), new Observer<RegisterLoginModel>() {
            @Override
            public void onChanged(@Nullable final RegisterLoginModel loginResponseModel) {
                registerLoginModel = loginResponseModel;
            }
        });
    }

    public void initViews() {
        mBinding.termsPrivacy.setText(Html.fromHtml(getResources().getString(R.string.terms_and_conditions)));
        mBinding.login.setOnClickListener(v -> {

            mBinding.tilEmailMobileUserName.setError(null);
            mBinding.tilPasswordOtp.setError(null);
            if (isOtpSent && mBinding.tilPasswordOtp.getEditText().getText().toString().isEmpty()) {
                mBinding.tilPasswordOtp.setError("Please enter valid password or otp");
                return;
            }

            strMobile = mBinding.tilEmailMobileUserName.getEditText().getText().toString().trim();
            //if(strMobile.equals(new PreferenceManager(getApplicationContext()).getString(Constant.MOBILE)))
            //strMobile = new PreferenceManager(getApplicationContext()).getString(Constant.MOBILE);//mBinding.etMobileNumber.getText().toString().trim();
            strPasswordOtp = mBinding.tilPasswordOtp.getEditText().getText().toString().trim();
            if (isOtpSent && registerLoginModel.getNewOTP().equals(strPasswordOtp)) {
                doRegisterLogin(strMobile, "2", countryCode, strPasswordOtp, getDeviceId(), "Q");
                return;
            } else if (isOtpSent && (strPasswordOtp.length() == 4) && !registerLoginModel.getNewOTP().equals(strPasswordOtp)) {
                mBinding.tilPasswordOtp.setError("Please enter valid otp");
                return;
            } else if (strPasswordOtp.length() >= 8) {
                doRegisterLogin(strMobile, "2", countryCode, strPasswordOtp, getDeviceId(), "Q");
            } else if (!isOtpSent && strPasswordOtp.isEmpty()) {
                mBinding.tilPasswordOtp.setError("Please enter otp or password.");
                return;
            } else if (!isOtpSent && strPasswordOtp.length() < 8) {
                mBinding.tilPasswordOtp.setError("Please enter otp or password.");
                return;
            }
        });
        mBinding.sendOtp.setOnClickListener(v -> {
            mBinding.tilPasswordOtp.setError(null);
            mBinding.tilPasswordOtp.getEditText().setText("");
            mBinding.tilEmailMobileUserName.setError(null);
            if (mBinding.tilEmailMobileUserName.getEditText().getText().toString().isEmpty()) {
                mBinding.tilEmailMobileUserName.setError("Please enter valid email or mobile");
                return;
            }
            //if contains only digits consider mobile number
            if (mBinding.tilEmailMobileUserName.getEditText().getText().toString().trim().matches("[0-9]+") &&
                    mBinding.tilEmailMobileUserName.getEditText().getText().toString().trim().length() < 10) {
                mBinding.tilEmailMobileUserName.setError("Please enter valid mobile number");
                return;
            }
            strMobile = mBinding.tilEmailMobileUserName.getEditText().getText().toString().trim();
            new PreferenceManager(getApplicationContext()).saveString(Constant.MOBILE, strMobile);
            if (!isOtpSent) {
                doRegisterLogin(strMobile, isOtpSent ? "R" : "1", countryCode, "", getDeviceId(), "Q");
            } else {
                doRegisterLogin(strMobile, isOtpSent ? "R" : "1", countryCode, "", getDeviceId(), "Q");
            }
        });
        mBinding.tvNewUser.setOnClickListener(v -> {
            replaceFragment(R.id.container, new NewUserFragment(), Bundle.EMPTY);
//            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
//            fragmentTransaction.replace(R.id.container, new NewUserFragment());
//            fragmentTransaction.addToBackStack("NEW_USER");
//            fragmentTransaction.commit();
        });
    }

    private void setTimer() {
        showToast("Otp Sent Successfully");
        new CountDownTimer(120000, 1000) {
            public void onTick(long millisUntilFinished) {
                mBinding.sendOtp.setEnabled(false);
                mBinding.tvSendOtp.setEnabled(false);
                mBinding.tvSendOtp.setText(String.format(Locale.ENGLISH, "%d:%d sec", millisUntilFinished / (60 * 1000) % 60, millisUntilFinished / 1000 % 60));
            }

            public void onFinish() {
                mBinding.tvSendOtp.setText(getResources().getString(R.string.resend_otp));
                mBinding.tvSendOtp.setEnabled(true);
                mBinding.sendOtp.setEnabled(true);
            }
        }.start();
    }

    private void doRegisterLogin(String mobile, String caseR, int countryCode, String passwordOtp, String deviceId, String appName) {
        Log.d(TAG, "Mobile : " + mobile);
        ProgressDialog.getInstance().show(getActivity());
        Call<RegisterLoginModel> call = apiService.doRegisterLogin(mobile, caseR, countryCode, passwordOtp, deviceId, appName, new PreferenceManager(getApplicationContext()).getToken(), "E");
        call.enqueue(new Callback<RegisterLoginModel>() {
            @Override
            public void onResponse(Call<RegisterLoginModel> call, Response<RegisterLoginModel> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.body() != null && response.body().getResponse().equals("200")) {
                    mViewModel.setRegisterLoginModel(response.body());
                    if (caseR.equals("1") || caseR.equals("R")) {
                        new PreferenceManager(getActivity()).saveString(Constant.MOBILE, mobile);
                        isOtpSent = true;
                        setTimer();
                    } else {
                        if (!response.body().getU_user_id().isEmpty()) {
                            Log.d(TAG, "onResponse Launch UserId : " + response.body().getU_user_id());
                            new PreferenceManager(getActivity()).saveInt(Constant.USER_ID, Integer.parseInt(response.body().getU_user_id()));
                            new PreferenceManager(getActivity()).saveUserId(response.body().getU_user_id());
                            new PreferenceManager(getActivity()).setIsLoggedIn(true);
                            Intent i = new Intent(getActivity(), MainActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                        }
                    }
                } else {
                    showErrorDialog(getActivity(), response.body().getResponse(), response.body().getMessage());
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
}