package com.jangletech.qoogol;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.jangletech.qoogol.databinding.ActivityLaunchBinding;
import com.jangletech.qoogol.util.PreferenceManager;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;

import java.security.MessageDigest;
import java.util.Objects;


public class LaunchActivity extends BaseActivity implements
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient googleApiClient;
    private ActivityLaunchBinding mBinding;
    private static final int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_launch);
        computePakageHash();
        //Init Google sdk
        initGoogleSdk();
        performAutoSignIn();

        CallbackManager callbackManager = CallbackManager.Factory.create();

        mBinding.signInBtn.setOnClickListener(v -> {
            Intent intent = new Intent(LaunchActivity.this, SignInActivity.class);
            startActivity(intent);

        });

        mBinding.signUpBtn.setOnClickListener(v -> {
            Intent intent = new Intent(LaunchActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        mBinding.googleSignIn.setOnClickListener(v -> {
            Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
            startActivityForResult(intent, RC_SIGN_IN);
        });

        mBinding.facebookSignIn.setOnClickListener(v -> mBinding.loginButton.performClick());

        mBinding.linkedInSignIn.setOnClickListener(v -> loginHandle());

        mBinding.loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Toast.makeText(LaunchActivity.this, "FaceBook SignIn Successful \n" + loginResult.getAccessToken(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                // App code
                Toast.makeText(LaunchActivity.this, "Facebook SignIn Cancelled ", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Toast.makeText(LaunchActivity.this, "Facebook SignIn Error " + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Google SignIn Connection Failed!!", Toast.LENGTH_SHORT).show();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else {
            LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);
        }

    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            Toast.makeText(this, "Successful", Toast.LENGTH_SHORT).show();
            assert account != null;
            Toast.makeText(this, "UserName : " + account.getDisplayName() + "\n Email : "
                    + account.getEmail() + "\n Id : " + account.getId(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Sign in cancel", Toast.LENGTH_LONG).show();
        }
    }


    private void computePakageHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.jangletech.qoogol",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (Exception e) {
            Log.e("TAG", Objects.requireNonNull(e.getMessage()));
        }
    }

    private void loginHandle() {
        LISessionManager.getInstance(getApplicationContext()).init(LaunchActivity.this, buildScope(), new AuthListener() {
            @Override
            public void onAuthSuccess() {
                Toast.makeText(LaunchActivity.this, "LinkedIn Success.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthError(LIAuthError error) {
                // Handle authentication errors
                Toast.makeText(getApplicationContext(), "Login Error " + error.toString(), Toast.LENGTH_LONG).show();
            }
        }, true);
    }

    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE, Scope.R_EMAILADDRESS);
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkNetworkConnection(mBinding.rootLayout,this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void performAutoSignIn(){
        if(new PreferenceManager(getApplicationContext()).isLoggedIn()){
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
            finish();
        }else{
            //Sign In Manually
        }
    }
}
