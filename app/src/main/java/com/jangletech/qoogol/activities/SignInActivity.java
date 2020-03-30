package com.jangletech.qoogol.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.jangletech.qoogol.MainActivity;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.SignUpViewModel;
import com.jangletech.qoogol.databinding.ActivitySignInBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.model.SignInModel;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.GenericTextWatcher;
import com.jangletech.qoogol.util.PreferenceManager;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    ActivitySignInBinding mBinding;
    private GoogleApiClient googleApiClient;
    private static final int RC_SIGN_IN = 1;
    private SignUpViewModel mViewModel;
    ApiInterface apiService = ApiClient.getInstance().getApi();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_in);
        setTextWatcher();
        initGoogleSdk();
        CallbackManager callbackManager = CallbackManager.Factory.create();
        mViewModel = ViewModelProviders.of(this).get(SignUpViewModel.class);

        Objects.requireNonNull(getSupportActionBar(), "Action Bar ").setDisplayHomeAsUpEnabled(true);
        setTitle(getResources().getString(R.string.sign_in_title));

        mBinding.signInBtn.setOnClickListener(v -> validateSignInForm());

        mBinding.googleSignIn.setOnClickListener(v -> {
            Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
            startActivityForResult(intent, RC_SIGN_IN);
        });

        mBinding.linkedInSignIn.setOnClickListener(v -> Toast.makeText(this, "Hi", Toast.LENGTH_SHORT).show());

        mBinding.facebookSignIn.setOnClickListener(v -> {
            Toast.makeText(this, "Hi", Toast.LENGTH_SHORT).show();
            mBinding.loginButton.performClick();
        });

        mBinding.loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Toast.makeText(SignInActivity.this, "FaceBook SignIn Successful \n" + loginResult.getAccessToken(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                // App code
                Toast.makeText(SignInActivity.this, "Facebook SignIn Cancelled ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Toast.makeText(SignInActivity.this, "Facebook SignIn Error " + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void validateSignInForm() {


        if (mBinding.tilEmail.getEditText().getText().toString().trim().isEmpty()) {
            mBinding.tilEmail.setError(getResources().getString(R.string.valid_email));
            return;
        }
        if (TextUtils.isEmpty(Objects.requireNonNull(mBinding.tilEmail.getEditText()).getText().toString().trim())) {
            mBinding.tilEmail.setError(getResources().getString(R.string.valid_email));
            return;
        }
        if (TextUtils.isEmpty(mBinding.tilPassword.getEditText().getText().toString().trim())) {
            mBinding.tilPassword.setError(getResources().getString(R.string.empty_password));
            return;
        }

        if (!hasError(mBinding.signInLayout)) {
            callSignInApi();
        }
    }

    private void initGoogleSdk() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE, Scope.R_EMAILADDRESS);
    }

    private void loginHandle() {
        LISessionManager.getInstance(getApplicationContext()).init(SignInActivity.this, buildScope(), new AuthListener() {
            @Override
            public void onAuthSuccess() {
                Toast.makeText(SignInActivity.this, "LinkedIn Success.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthError(LIAuthError error) {
                // Handle authentication errors
                Toast.makeText(getApplicationContext(), "Login Error " + error.toString(), Toast.LENGTH_LONG).show();
            }
        }, true);
    }

    public void setTextWatcher() {
        mBinding.etEmail.addTextChangedListener(new GenericTextWatcher(mBinding.tilEmail, this));
        mBinding.etPassword.addTextChangedListener(new GenericTextWatcher(mBinding.tilPassword, this));
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signInBtn:
                validateSignInForm();
                break;
        }
    }

    private void callSignInApi() {
        ProgressDialog.getInstance().show(this);
        Map<String, String> arguments = new HashMap<>();
        arguments.put(Constant.SIGN_IN_FIELD, mBinding.tilEmail.getEditText().getText().toString());
        arguments.put(Constant.PASSWORD, mBinding.tilPassword.getEditText().getText().toString());

        Call<SignInModel> call = apiService.signCall(arguments);
        call.enqueue(new Callback<SignInModel>() {
            @Override
            public void onResponse(Call<SignInModel> call, Response<SignInModel> response) {
                if (response.body().getStatusCode().equalsIgnoreCase("1")) {
                    SignInModel signInModel = (SignInModel) response.body();
                    new PreferenceManager(getApplicationContext()).saveUserId(response.body().getObject().getUserId());
                    //auto sign-in flag set
                    new PreferenceManager(getApplicationContext()).setIsLoggedIn(true);
                    mViewModel.setData(signInModel);
                    ProgressDialog.getInstance().dismiss();
                    Intent i = new Intent(SignInActivity.this, MainActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                } else {
                    ProgressDialog.getInstance().dismiss();
                    Toast.makeText(SignInActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SignInModel> call, Throwable t) {
                t.printStackTrace();
                ProgressDialog.getInstance().dismiss();
            }
        });
    }
}
