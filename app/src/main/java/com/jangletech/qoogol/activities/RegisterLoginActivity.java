package com.jangletech.qoogol.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.ui.ExistingUserFragment;
import com.jangletech.qoogol.ui.NewUserFragment;

public class RegisterLoginActivity extends AppCompatActivity {

    private static final String TAG = "RegisterLoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_login);
        //addFragment(R.id.container, new ExistingUserFragment(), "EXISTING_USER");
        replaceFragment(R.id.container, new ExistingUserFragment(), Bundle.EMPTY);
    }

    public void replaceFragment(int resId, Fragment fragment, Bundle bundle) {
        String backStateName = fragment.getClass().getName();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        fragmentTransaction.replace(resId, fragment);
        fragmentTransaction.addToBackStack(backStateName);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.container);
        if (fragment instanceof NewUserFragment) {
            Log.d(TAG, "onBackPressed NewUserFragment : ");
            fragmentManager.popBackStack();
        } else if (fragment instanceof ExistingUserFragment) {
            Log.d(TAG, "onBackPressed ExistingUserFragment : ");
            finish();
        } else {
            finish();
        }
    }
}