package com.jangletech.qoogol.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.activities.MainActivity;
import com.jangletech.qoogol.activities.RegisterLoginViewModel;
import com.jangletech.qoogol.database.repo.AppRepository;
import com.jangletech.qoogol.databinding.FragmentExistingUserBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.model.LearningQuestionsNew;
import com.jangletech.qoogol.model.LocalDataResponse;
import com.jangletech.qoogol.model.RegisterLoginModel;
import com.jangletech.qoogol.model.TestModelNew;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.service.DownloadAsyncTask;
import com.jangletech.qoogol.util.AESSecurities;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;
import com.jangletech.qoogol.util.TinyDB;
import com.jangletech.qoogol.util.UtilHelper;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ExistingUserFragment extends BaseFragment {

    private static final String TAG = "ExistingUserFragment";
    private FragmentExistingUserBinding mBinding;
    private boolean isOtpSent = false;
    private RegisterLoginViewModel mViewModel;
    private RegisterLoginModel registerLoginModel;
    private int countryCode = 91;
    private String strMobile = "";
    private String strPasswordOtp = "";
    public AppRepository mAppRepository;
    private CountDownTimer countDownTimer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_existing_user, container, false);
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
        mAppRepository = new AppRepository(getActivity());
        Log.d(TAG, "initViews Args : " + getArguments().getString(Constant.u_mob_1));
        String mobile = TinyDB.getInstance(getActivity()).getString(Constant.u_mob_1);
        if (mobile != null && !mobile.isEmpty()) {
            mBinding.tilEmailMobileUserName.getEditText().setText(mobile);
            TinyDB.getInstance(getActivity()).putString(Constant.u_mob_1, "");
        }

        mBinding.termsPrivacy.setText(Html.fromHtml(getResources().getString(R.string.terms_and_conditions)));
        mBinding.termsPrivacy.setMovementMethod(LinkMovementMethod.getInstance());

        mBinding.login.setOnClickListener(v -> {
            mBinding.tilEmailMobileUserName.setError(null);
            mBinding.tilPasswordOtp.setError(null);
            if (isOtpSent && mBinding.tilPasswordOtp.getEditText().getText().toString().isEmpty()) {
                mBinding.tilPasswordOtp.setError("Please enter valid password or otp");
                return;
            }

            strMobile = mBinding.tilEmailMobileUserName.getEditText().getText().toString().trim();
            strPasswordOtp = mBinding.tilPasswordOtp.getEditText().getText().toString().trim();
            if (isOtpSent && registerLoginModel.getNewOTP().equals(strPasswordOtp)) {
                doRegisterLogin(strMobile, "2", countryCode, strPasswordOtp, getDeviceId(getActivity()), "Q");
                return;
            } else if (isOtpSent && (strPasswordOtp.length() == 4) && !registerLoginModel.getNewOTP().equals(strPasswordOtp)) {
                mBinding.tilPasswordOtp.setError("Please enter valid otp");
                return;
            } else if (strPasswordOtp.length() >= 8) {
                doRegisterLogin(strMobile, "2", countryCode, strPasswordOtp, getDeviceId(getActivity()), "Q");
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
            new PreferenceManager(getActivity()).saveString(Constant.MOBILE, strMobile);
            if (!isOtpSent) {
                doRegisterLogin(strMobile, isOtpSent ? "R" : "1", countryCode, "", getDeviceId(getActivity()), "Q");
            } else {
                doRegisterLogin(strMobile, isOtpSent ? "R" : "1", countryCode, "", getDeviceId(getActivity()), "Q");
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
        countDownTimer = new CountDownTimer(120000, 1000) {
            public void onTick(long millisUntilFinished) {
                mBinding.sendOtp.setEnabled(false);
                mBinding.tvSendOtp.setEnabled(false);
                mBinding.tvSendOtp.setText(String.format(Locale.ENGLISH, "%d:%d sec", millisUntilFinished / (60 * 1000) % 60, millisUntilFinished / 1000 % 60));
            }

            public void onFinish() {
                try {
                    mBinding.tvSendOtp.setText(getResources().getString(R.string.resend_otp));
                    mBinding.tvSendOtp.setEnabled(true);
                    mBinding.sendOtp.setEnabled(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void doRegisterLogin(String mobile, String caseR, int countryCode, String passwordOtp, String deviceId, String appName) {
        Log.d(TAG, "Mobile = : " + AESSecurities.getInstance().encrypt(TinyDB.getInstance(getActivity()).getString(Constant.cf_key4), mobile));
        Log.d(TAG, "Case = : " + caseR);
        Log.d(TAG, "Country Code = : " + countryCode);
        Log.d(TAG, "Password  = : " + AESSecurities.getInstance().encrypt(TinyDB.getInstance(getActivity()).getString(Constant.cf_key6), passwordOtp));
        Log.d(TAG, "deviceId  = : " + deviceId);
        Log.d(TAG, "appName  = : " + appName);
        Log.d(TAG, "Token  = : " + new PreferenceManager(getActivity()).getToken());
        Log.d(TAG, "Case2 = : E");

        ProgressDialog.getInstance().show(getActivity());
        Call<RegisterLoginModel> call = getApiService().doRegisterLogin(
                AESSecurities.getInstance().encrypt(TinyDB.getInstance(getActivity()).getString(Constant.cf_key4), mobile),
                caseR,
                countryCode,
                AESSecurities.getInstance().encrypt(TinyDB.getInstance(getActivity()).getString(Constant.cf_key6), passwordOtp),
                deviceId,
                appName,
                new PreferenceManager(getActivity()).getToken(),
                "E"
        );
        call.enqueue(new Callback<RegisterLoginModel>() {
            @Override
            public void onResponse(Call<RegisterLoginModel> call, Response<RegisterLoginModel> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.body() != null) {
                    if (response.body().getResponse().equals("200")) {
                        mViewModel.setRegisterLoginModel(response.body());
                        if (caseR.equals("1") || caseR.equals("R")) {
                            new PreferenceManager(getActivity()).saveString(Constant.MOBILE, mobile);
                            isOtpSent = true;
                            setTimer();
                        } else {
                            if (!response.body().getU_user_id().isEmpty()) {
                                try {
                                    Log.d(TAG, "onResponse Launch UserId : " + response.body().getU_user_id());
                                    new PreferenceManager(getActivity()).saveInt(Constant.USER_ID, Integer.parseInt(response.body().getU_user_id()));
                                    new PreferenceManager(getActivity()).saveUserId(response.body().getU_user_id());
                                    new PreferenceManager(getActivity()).setIsLoggedIn(true);
                                    TinyDB.getInstance(getActivity()).putString(Constant.selected_ue_id, response.body().getUeId());
                                    Log.d(TAG, "onResponse UEID : " + response.body().getUeId());
                                    callOfflineApi(response.body().getU_user_id());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                try {
                                    Log.d(TAG, "onResponse Launch UserId : " + response.body().getU_user_id());
                                    new PreferenceManager(getActivity()).saveInt(Constant.USER_ID, Integer.parseInt(response.body().getU_user_id()));
                                    new PreferenceManager(getActivity()).saveUserId(response.body().getU_user_id());
                                    new PreferenceManager(getActivity()).setIsLoggedIn(true);
                                    callOfflineApi(response.body().getU_user_id());

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } else if (response.body().getResponse().equals("315")) {
                        showErrorDialog(getActivity(), response.body().getResponse(), response.body().getMessage(), "NEW_USER", mobile);
                    } else {
                        showErrorDialog(getActivity(), response.body().getResponse(), response.body().getMessage(), "NEW_USER", mobile);
                    }
                }
            }

            @Override
            public void onFailure(Call<RegisterLoginModel> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                t.printStackTrace();
                apiCallFailureDialog(t);
            }
        });
    }


    private void callOfflineApi(String u_user_id) {
        Call<LocalDataResponse> call = getApiService().fetchLocalDataApi(getUserId(getActivity()), getDeviceId(getActivity()), Constant.forcerefresh);
        call.enqueue(new Callback<LocalDataResponse>() {
            @Override
            public void onResponse(Call<LocalDataResponse> call, retrofit2.Response<LocalDataResponse> response) {
                try {
                    if (response.body() != null && response.body().getResponse().equalsIgnoreCase("200")) {
                        ExecutorService executor = Executors.newSingleThreadExecutor();
                        executor.execute(() -> mAppRepository.insertQuestions(response.body().getQuestionDataList()));
                        List<TestModelNew> newList = response.body().getTestDataList();
                        if (response.body().getTestDataList() != null) {
                            for (TestModelNew testModelNew : newList) {
                                testModelNew.setFlag("PRACTICE");
                                Log.d(TAG, "PRACTICE UserId : " + u_user_id);
                                testModelNew.setUserId(u_user_id);
                            }
                            executor.execute(() -> mAppRepository.insertTests(newList));
                        }
                    }
                    navigateOnHomeScreen();
                } catch (Exception e) {
                    e.printStackTrace();
                    navigateOnHomeScreen();
                }
            }

            @Override
            public void onFailure(Call<LocalDataResponse> call, Throwable t) {
                t.printStackTrace();
                navigateOnHomeScreen();
            }
        });
    }

    private void navigateOnHomeScreen() {
        try {
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            TinyDB.getInstance(getActivity()).putBoolean(Constant.IS_EDUCATION_ADDED, false);
            Intent i = new Intent(getActivity(), MainActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String getQuestionImages() {
        try {
            String images = "";
            List<LearningQuestionsNew> list = mAppRepository.getQuestions();
            for (LearningQuestionsNew learningQuestionsNew : list) {
                images = images + learningQuestionsNew.getImageList();
            }
            return images;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private void downloadImages() {
        try {
            DownloadAsyncTask downloadAsyncTask = new DownloadAsyncTask(UtilHelper.getDirectory(getActivity()));
            downloadAsyncTask.execute(createMediaPathDownloaded((getQuestionImages())), "1");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private String createMediaPathDownloaded(String img) {
        String mediaPaths = "";
        String[] imglist = img.split(",");
        for (String mediaList1 : imglist) {
            String path = Constant.QUESTION_IMAGES_API + mediaList1.trim();
            mediaPaths = mediaPaths + path + ",";
        }
        return mediaPaths;
    }
}