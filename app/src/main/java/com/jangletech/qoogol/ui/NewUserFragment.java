package com.jangletech.qoogol.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
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
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.util.AppUtils;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.DateUtils;
import com.jangletech.qoogol.util.PreferenceManager;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.facebook.FacebookSdk.getApplicationContext;


public class NewUserFragment extends BaseFragment {

    private static final String TAG = "NewUserFragment";
    private FragmentNewUserBinding mBinding;
    private RegisterLoginModel registerLoginModel;
    private boolean isValidated = true;
    private boolean isOtpSent = false;
    private int countryCode = 91; //todo country code hardcoded
    private String strMobile = "";
    private String strPasswordOtp = "";
    private RegisterLoginViewModel mViewModel;
    private ApiInterface apiService = ApiClient.getInstance().getApi();
    private String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private String gender = "M";
    private String dob = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_new_user, container, false);
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
        //Log.d(TAG, "initViews String : " + getActivity().getString(R.string.terms_privacy));
        //mBinding.termsPrivacy.setText(getActivity().getString(R.string.terms_privacy));

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
                    doRegisterLogin(strMobile, "N", countryCode, "", getDeviceId(), "Q");
            }
        });

        mBinding.tvExistingUser.setOnClickListener(v -> {
            replaceFragment(R.id.container, new ExistingUserFragment(), Bundle.EMPTY);
        });

        mBinding.tilDob.getEditText().setOnClickListener(v -> {
            showDatePicker();
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
            new PreferenceManager(getApplicationContext()).saveString(Constant.MOBILE, strMobile);
            if (!isOtpSent) {
                doRegisterLogin(strMobile, isOtpSent ? "R" : "1", countryCode, "", getDeviceId(), "Q");
            } else {
                doRegisterLogin(strMobile, isOtpSent ? "R" : "1", countryCode, "", getDeviceId(), "Q");
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
        DateFormat format = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        Date date = null;
        try {
            date = format.parse(mBinding.tilDob.getEditText().getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar newCalendar = Calendar.getInstance();
        newCalendar.setTime(date != null ? date : new Date());
        DatePickerDialog dialog = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Dialog, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String formattedDate = year + "-" + (month + 1) + "-" + day;
                if (AppUtils.isEnteredDOBValid(year, (month + 1), day)) {
                    mBinding.tilDob.getEditText().setText(DateUtils.getFormattedDate(formattedDate));
                } else {
                    showToast("Minimum age required is 13 years.");
                    mBinding.tilDob.getEditText().setText("");
                }
            }
        }, //newCalendar.get(1990), newCalendar.get(1), newCalendar.get(Calendar.DAY_OF_MONTH));
                newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
    }

    private void doRegisterLogin(String mobile, String caseR, int countryCode, String passwordOtp, String deviceId, String appName) {
        Log.d(TAG, "First Name : " + mBinding.tilFirstName.getEditText().getText().toString());
        Log.d(TAG, "Last Name : " + mBinding.tilLastName.getEditText().getText().toString());
        Log.d(TAG, "Gender Name : " + gender);
        Log.d(TAG, "Dob : " + mBinding.tilDob.getEditText().getText().toString().trim());
        Log.d(TAG, "Email Mobile : " + mBinding.tilEmailMobile.getEditText().getText().toString().trim());
        Log.d(TAG, "Otp : " + mBinding.tilOtp.getEditText().getText().toString().trim());
        Log.d(TAG, "Password  : " + mBinding.tilPassword.getEditText().getText().toString().trim());
        Log.d(TAG, "Case R  : " + caseR);
        Log.d(TAG, "Country Code : " + countryCode);
        Log.d(TAG, "Device Id : " + deviceId);
        Log.d(TAG, "App Name : " + appName);
        Log.d(TAG, "Token : " + new PreferenceManager(getApplicationContext()).getToken());
        Log.d(TAG, "Case2 : N");

        ProgressDialog.getInstance().show(getActivity());
        Call<RegisterLoginModel> call = apiService.doRegister(
                mBinding.tilFirstName.getEditText().getText().toString(),
                mBinding.tilLastName.getEditText().getText().toString(),
                gender,
                mBinding.tilDob.getEditText().getText().toString().trim(),
                mBinding.tilEmailMobile.getEditText().getText().toString().trim(),
                mBinding.tilOtp.getEditText().getText().toString().trim(),
                mBinding.tilPassword.getEditText().getText().toString().trim(),
                caseR,
                countryCode,
                deviceId,
                appName,
                new PreferenceManager(getApplicationContext()).getToken(),
                "N"
        );
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
                            //deleteOfflineData();
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

    private void setTimer() {
        showToast("Otp Sent Successfully");
        new CountDownTimer(120000, 1000) {
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
}