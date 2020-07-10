package com.jangletech.qoogol.activities;

import android.content.Intent;
import android.os.Bundle;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.util.PreferenceManager;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        performAutoLogin();
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                performAutoLogin();
            }
        }, 1000);*/
    }

    private void performAutoLogin() {
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