package com.jangletech.qoogol.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.activities.MainActivity;
import com.jangletech.qoogol.activities.RegisterLoginViewModel;
import com.jangletech.qoogol.databinding.FragmentNewUserBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.model.RegisterLoginModel;
import com.jangletech.qoogol.model.VerifyResponse;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.util.AESSecurities;
import com.jangletech.qoogol.util.AppUtils;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.DateUtils;
import com.jangletech.qoogol.util.PreferenceManager;
import com.jangletech.qoogol.util.TinyDB;

import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewUserFragment extends BaseFragment {

    private static final String TAG = "NewUserFragment";
    private CountDownTimer countDownTimer;
    private FragmentNewUserBinding mBinding;
    private RegisterLoginModel registerLoginModel;
    private boolean isValidated = true;
    private boolean isOtpSent = false;
    private int countryCode = 91; //todo country code hardcoded
    private String strMobile = "";
    private String strReferralCode = "";
    private String strPasswordOtp = "";
    private RegisterLoginViewModel mViewModel;
    private ApiInterface apiService = ApiClient.getInstance().getApi();
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private String gender = "M";
    private String dob = "";
    private Calendar mCalendar, mCalendarMax;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_user, container, false);
        mCalendar = Calendar.getInstance();
        mCalendarMax = Calendar.getInstance();
        mCalendarMax.add(Calendar.YEAR, -13);
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
        String mobile = getMobileFromBundle(getArguments());
        if (mobile != null && !mobile.isEmpty()) {
            mBinding.tilEmailMobile.getEditText().setText(mobile);
        }

        mBinding.termsPrivacy.setText(Html.fromHtml(getResources().getString(R.string.terms_and_conditions)));
        mBinding.termsPrivacy.setMovementMethod(LinkMovementMethod.getInstance());

        mBinding.radioMale.setOnClickListener(v -> {
            gender = "M";
        });
        mBinding.radioFemale.setOnClickListener(v -> {
            gender = "F";
        });
        mBinding.radioOther.setOnClickListener(v -> {
            gender = "O";
        });

        mBinding.register.setOnClickListener(v -> {
            isValidated = true;
            if (mBinding.tilFirstName.getEditText().getText().toString().isEmpty()) {
                mBinding.tilFirstName.setError("Please enter first name.");
                mBinding.tilFirstName.getEditText().requestFocus();
                isValidated = false;
                return;
            } else if (mBinding.tilLastName.getEditText().getText().toString().isEmpty()) {
                mBinding.tilLastName.setError("Please enter last name.");
                mBinding.tilLastName.getEditText().requestFocus();
                isValidated = false;
                return;
            } else if (mBinding.tilDob.getEditText().getText().toString().isEmpty()) {
                mBinding.tilDob.setError("Please enter date of birth.");
                showToast("Please enter date of birth.");
                //mBinding.tilDob.getEditText().requestFocus();
                isValidated = false;
                return;
            } else if (mBinding.tilEmailMobile.getEditText().getText().toString().isEmpty()) {
                mBinding.tilEmailMobile.setError("Please enter valid email or mobile number.");
                mBinding.tilEmailMobile.getEditText().requestFocus();
                isValidated = false;
                return;
            } else if (mBinding.tilOtp.getEditText().getText().toString().isEmpty()) {
                mBinding.tilOtp.setError("Please enter valid otp.");
                mBinding.tilOtp.getEditText().requestFocus();
                isValidated = false;
                return;
            } else if (mBinding.tilPassword.getEditText().getText().toString().isEmpty()) {
                mBinding.tilPassword.setError("Please enter password.");
                mBinding.tilPassword.getEditText().requestFocus();
                isValidated = false;
                return;
            } else {
                if (isOtpSent)
                    doRegisterLogin(strMobile, "N", countryCode, "", getDeviceId(getActivity()), "Q");
            }
        });

        mBinding.tvExistingUser.setOnClickListener(v -> {
            replaceFragment(R.id.container, new ExistingUserFragment(), Bundle.EMPTY);
        });

        mBinding.tilDob.getEditText().setOnClickListener(v -> {
            showDatePicker();
        });

        mBinding.btnVerify.setOnClickListener(v -> {
            if (mBinding.refrerralView.getText().toString().trim().isEmpty() ||
                    mBinding.refrerralView.getText().toString().trim().length() < 4) {
                mBinding.refrerralView.setError("Enter valid referral code.");
                return;
            }
            doReferralCodeVerification(mBinding.refrerralView.getText().toString().trim());
        });

        mBinding.sendOtp.setOnClickListener(v -> {
            mBinding.tilOtp.setError(null);
            mBinding.tilOtp.getEditText().setText("");
            mBinding.tilEmailMobile.setError(null);
            if (mBinding.tilEmailMobile.getEditText().getText().toString().isEmpty()) {
                mBinding.tilEmailMobile.setError("Please enter valid email or mobile number.");
                return;
            }
            //if contains only digits consider mobile number
            if (mBinding.tilEmailMobile.getEditText().getText().toString().trim().matches("[0-9]+") &&
                    mBinding.tilEmailMobile.getEditText().getText().toString().trim().length() < 10) {
                mBinding.tilEmailMobile.setError("Please enter valid mobile number.");
                return;
            }
            strMobile = mBinding.tilEmailMobile.getEditText().getText().toString().trim();
            new PreferenceManager(getActivity()).saveString(Constant.MOBILE, strMobile);
            if (!isOtpSent) {
                doRegisterLogin(strMobile, isOtpSent ? "R" : "1", countryCode, "", getDeviceId(getActivity()), "Q");
            } else {
                doRegisterLogin(strMobile, isOtpSent ? "R" : "1", countryCode, "", getDeviceId(getActivity()), "Q");
            }
        });

        mBinding.tilFirstName.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    isValidated = false;
                    mBinding.tilFirstName.setError("Please enter first name.");
                } else {
                    isValidated = true;
                    mBinding.tilFirstName.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mBinding.tilLastName.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    isValidated = false;
                    mBinding.tilLastName.setError("Please enter last name.");
                } else {
                    isValidated = true;
                    mBinding.tilLastName.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mBinding.tilLastName.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    isValidated = false;
                    mBinding.tilLastName.setError("Please enter last name.");
                } else {
                    isValidated = true;
                    mBinding.tilLastName.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mBinding.tilDob.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    isValidated = false;
                    mBinding.tilDob.setError("Please enter date of birth.");
                } else {
                    isValidated = true;
                    mBinding.tilDob.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mBinding.tilEmailMobile.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isValidated = false;
                if (s.toString().isEmpty()) {
                    mBinding.tilEmailMobile.setError("Please enter Email Id or Mobile number.");
                    return;
                }/* else if (s.toString().trim().matches("[0-9]+") && s.toString().trim().length() != 10) {
                    mBinding.tilEmailMobile.setError("Please enter valid mobile number.");
                    return;
                }*/ else {
                    isValidated = true;
                    mBinding.tilEmailMobile.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (strMobile != null && !strMobile.isEmpty()) {
                    if (!strMobile.equalsIgnoreCase(s.toString())) {
                        mBinding.rlReferral.setVisibility(View.GONE);
                        //referralCode = "";
                    }
                }
            }
        });

        mBinding.tilOtp.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().isEmpty()) {
                    isValidated = false;
                    mBinding.tilOtp.setError("Please enter valid otp.");
                } else {
                    isValidated = true;
                    mBinding.tilOtp.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mBinding.tilPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().isEmpty()) {
                    isValidated = false;
                    mBinding.tilPassword.setError("Please enter password.");
                } else if (s.toString().trim().length() < 8) {
                    isValidated = false;
                    mBinding.tilPassword.setError("Password should contains atleast 8 characters");
                } else {
                    isValidated = true;
                    mBinding.tilPassword.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void showDatePicker() {
        DatePickerDialog dialog = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Dialog, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String formattedDate = year + "-" + (month + 1) + "-" + day;
                mCalendar.set(Calendar.YEAR, year);
                mCalendar.set(Calendar.MONTH, month);
                mCalendar.set(Calendar.DAY_OF_MONTH, day);

                if (AppUtils.isEnteredDOBValid(year, (month + 1), day)) {
                    mBinding.tilDob.getEditText().setText(DateUtils.getFormattedDate(formattedDate));
                } else {
                    showToast("Minimum age required is 13 years.");
                    mBinding.tilDob.getEditText().setText("");
                }
            }
        }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getDatePicker().setMaxDate(mCalendarMax.getTimeInMillis());
        dialog.show();
    }

    private void doRegisterLogin(String mobile, String caseR, int countryCode, String passwordOtp, String deviceId, String appName) {
        Log.d(TAG, "First Name : " + AESSecurities.getInstance().encrypt(TinyDB.getInstance(getActivity()).getString(Constant.cf_key1), mBinding.tilFirstName.getEditText().getText().toString().trim()));
        Log.d(TAG, "Last Name : " + AESSecurities.getInstance().encrypt(TinyDB.getInstance(getActivity()).getString(Constant.cf_key2), mBinding.tilLastName.getEditText().getText().toString().trim()));
        Log.d(TAG, "Gender Name : " + gender);
        Log.d(TAG, "Dob : " + AESSecurities.getInstance().encrypt(TinyDB.getInstance(getActivity()).getString(Constant.cf_key3), mBinding.tilDob.getEditText().getText().toString().trim()));
        Log.d(TAG, "Email: " + AESSecurities.getInstance().encrypt(TinyDB.getInstance(getActivity()).getString(Constant.cf_key1), mBinding.tilEmailMobile.getEditText().getText().toString().trim()));
        Log.d(TAG, "Otp : " + mBinding.tilOtp.getEditText().getText().toString().trim());
        Log.d(TAG, "Password  : " + AESSecurities.getInstance().encrypt(TinyDB.getInstance(getActivity()).getString(Constant.cf_key6), mBinding.tilPassword.getEditText().getText().toString().trim()));
        Log.d(TAG, "Case R  : " + caseR);
        Log.d(TAG, "Country Code : " + countryCode);
        Log.d(TAG, "Device Id : " + deviceId);
        Log.d(TAG, "App Name : " + appName);
        Log.d(TAG, "Token : " + new PreferenceManager(getActivity()).getToken());
        Log.d(TAG, "Case2 : N");
        Log.d(TAG, "Referral Code  : " + strReferralCode);

        /*if (!isReferralCodeVerified)
            strReferralCode = "";*/

        ProgressDialog.getInstance().show(getActivity());
        Call<RegisterLoginModel> call = apiService.doRegister(
                AESSecurities.getInstance().encrypt(TinyDB.getInstance(getActivity()).getString(Constant.cf_key1), mBinding.tilFirstName.getEditText().getText().toString().trim()),
                AESSecurities.getInstance().encrypt(TinyDB.getInstance(getActivity()).getString(Constant.cf_key2), mBinding.tilLastName.getEditText().getText().toString().trim()),
                gender,
                AESSecurities.getInstance().encrypt(TinyDB.getInstance(getActivity()).getString(Constant.cf_key3), mBinding.tilDob.getEditText().getText().toString().trim()),
                AESSecurities.getInstance().encrypt(TinyDB.getInstance(getActivity()).getString(Constant.cf_key4), mBinding.tilEmailMobile.getEditText().getText().toString().trim()),
                mBinding.tilOtp.getEditText().getText().toString().trim(),
                AESSecurities.getInstance().encrypt(TinyDB.getInstance(getActivity()).getString(Constant.cf_key6), mBinding.tilPassword.getEditText().getText().toString().trim()),
                caseR,
                countryCode,
                deviceId,
                appName,
                new PreferenceManager(getActivity()).getToken(),
                "N",
                strReferralCode
        );
        call.enqueue(new Callback<RegisterLoginModel>() {
            @Override
            public void onResponse(Call<RegisterLoginModel> call, Response<RegisterLoginModel> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.body() != null && response.body().getResponse() != null &&
                        response.body().getResponse().equals("200")) {
                    mViewModel.setRegisterLoginModel(response.body());
                    if (caseR.equals("1") || caseR.equals("R")) {
                        new PreferenceManager(getActivity()).saveString(Constant.MOBILE, mobile);
                        isOtpSent = true;
                        setTimer();
                    } else {
                        if (!response.body().getU_user_id().isEmpty()) {
                            //deleteOfflineData();
                            countDownTimer.cancel();
                            Log.d(TAG, "onResponse Launch UserId : " + response.body().getU_user_id());
                            new PreferenceManager(getActivity()).saveInt(Constant.USER_ID, Integer.parseInt(response.body().getU_user_id()));
                            new PreferenceManager(getActivity()).saveUserId(response.body().getU_user_id());
                            new PreferenceManager(getActivity()).setIsLoggedIn(true);
                            Intent i = new Intent(getActivity(), MainActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                        }
                    }
                } else if (response.body() != null && response.body().getResponse().equals("316")) {
                    showErrorDialog(getActivity(), response.body().getResponse(), response.body().getMessage(), "EXISTING_USER", mobile);
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

    private void setTimer() {
        mBinding.rlReferral.setVisibility(View.VISIBLE);
        showToast("Otp Sent Successfully");
        countDownTimer = new CountDownTimer(120000, 1000) {
            public void onTick(long millisUntilFinished) {
                mBinding.tvSendOtp.setEnabled(false);
                mBinding.tvSendOtp.setText(String.format(Locale.ENGLISH, "%d:%d sec", millisUntilFinished / (60 * 1000) % 60, millisUntilFinished / 1000 % 60));
            }

            public void onFinish() {
                mBinding.tvSendOtp.setText(getResources().getString(R.string.resend_otp));
                mBinding.tvSendOtp.setEnabled(true);
            }
        }.start();
    }

    private void doReferralCodeVerification(String referralCode) {
        ProgressDialog.getInstance().show(getActivity());
        Call<VerifyResponse> call = apiService.doReferCodeVerification(referralCode);
        call.enqueue(new Callback<VerifyResponse>() {
            @Override
            public void onResponse(Call<VerifyResponse> call, Response<VerifyResponse> response) {
                ProgressDialog.getInstance().dismiss();
                if (response != null) {
                    if (response.body() != null &&
                            response.body().getResponse().equals("200")) {
                        strReferralCode = referralCode;
                        //isReferralCodeVerified = true;
                        mBinding.refrerralView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_right_tick, 0);
                        mBinding.refrerralView.setEnabled(false);
                        mBinding.btnVerify.setEnabled(false);
                    } else {
                        //isReferralCodeVerified = false;
                        showToast("Referral code is not valid.");
                    }
                }
            }

            @Override
            public void onFailure(Call<VerifyResponse> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                showToast("Something went wrong!!");
                t.printStackTrace();
            }
        });
    }
}