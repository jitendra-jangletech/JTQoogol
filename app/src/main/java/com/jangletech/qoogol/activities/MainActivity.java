package com.jangletech.qoogol.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.ActivityMainBinding;
import com.jangletech.qoogol.dialog.UniversalDialog;
import com.jangletech.qoogol.model.UserProfile;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.ui.personal_info.PersonalInfoViewModel;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity implements UniversalDialog.DialogButtonClickListener {

    private static final String TAG = "MainActivity";
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding mBinding;
    public static NavController navController;
    private PersonalInfoViewModel mViewmodel;
    public static ImageView profileImage;
    public static TextView textViewDisplayName;
    ApiInterface apiService = ApiClient.getInstance().getApi();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mViewmodel = new ViewModelProvider(this).get(PersonalInfoViewModel.class);
        profileImage = findViewById(R.id.profilePic);
        textViewDisplayName = findViewById(R.id.tvName);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //setMargins(mBinding.marginLayout);
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        setUserInfo();
        NavigationView navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.edit_profile,
                R.id.nav_home, R.id.nav_learning,
                R.id.nav_home, /*R.id.nav_course, R.id.nav_exam, R.id.nav_questions*/
                R.id.nav_practice_test, R.id.nav_test_my, R.id.nav_test_popular, R.id.nav_attended_by_friends,
                R.id.nav_blocked_connections, R.id.nav_import_contacts,
                R.id.nav_shared_with_you, R.id.nav_shared_by_you, R.id.nav_requests,
                R.id.nav_reviews, R.id.nav_published, R.id.nav_notifications, R.id.nav_settings, R.id.nav_fav_test)
                .setDrawerLayout(mBinding.drawerLayout)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //fetchUserProfile();
        mViewmodel.getUserProfileInfo().observe(this, new Observer<UserProfile>() {
            @Override
            public void onChanged(@Nullable final UserProfile userProfile) {
                Log.d(TAG, "First Name : " + userProfile.getFirstName());
                new PreferenceManager(getApplicationContext()).saveString(Constant.DISPLAY_NAME,userProfile.getFirstName()+" "+userProfile.getLastName());
                new PreferenceManager(getApplicationContext()).saveString(Constant.GENDER,userProfile.getStrGender());
                new PreferenceManager(getApplicationContext()).saveString(Constant.PROFILE_PIC,getProfileImageUrl(userProfile.getEndPathImage()));
                setUserInfo();
            }
        });

       /* mBinding.drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                setUserInfo();
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });*/

        mBinding.navHeader.tvName.setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawer(Gravity.LEFT);
            navController.navigate(R.id.nav_edit_profile);
        });

        mBinding.navHeader.profilePic.setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawer(Gravity.LEFT);
            navController.navigate(R.id.nav_edit_profile);
        });
        Intent intent = getIntent();
        if (intent.hasExtra("bundle")) {
            Bundle bundle = intent.getBundleExtra("bundle");
            if (bundle != null && bundle.getBoolean("fromNotification")) {
                if (bundle.getString(Constant.FB_FROM_TYPE).equalsIgnoreCase(Constant.qoogol))
                    navController.navigate(R.id.nav_learning, bundle);
                else
                    navController.navigate(R.id.nav_test, bundle);
            }

        }


        /***
         * Navigations From Home Fragment
         */
        mBinding.navHome.setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.nav_home) {
                navController.popBackStack();
                navController.navigate(R.id.nav_home);
            }
        });

        mBinding.navFav.setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.nav_fav) {
                navController.popBackStack();
                navController.navigate(R.id.nav_fav);
            }
        });


        findViewById(R.id.nav_learning).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.nav_learning) {
                navController.popBackStack();
                Bundle bundle = new Bundle();
                bundle.putString("call_from", "learning");
                navController.navigate(R.id.nav_learning, bundle);
            }
        });

        findViewById(R.id.nav_requests).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.nav_requests) {
                navController.popBackStack();
                navController.navigate(R.id.nav_requests);
            }
        });

        findViewById(R.id.nav_blocked_connections).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.nav_blocked_connections) {
                navController.popBackStack();
                navController.navigate(R.id.nav_blocked_connections);
            }
        });

        findViewById(R.id.nav_import_contacts).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.nav_import_contacts) {
                navController.popBackStack();
                navController.navigate(R.id.nav_import_contacts);
            }
        });


        findViewById(R.id.nav_practice_test).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.nav_practice_test) {
                navController.popBackStack();
                //navController.navigate(R.id.nav_practice_test);
                startActivity(new Intent(this, PracticeTestActivity.class));
            }
        });

        findViewById(R.id.nav_saved).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.nav_saved) {
                navController.popBackStack();
               // Bundle bundle = new Bundle();
                //bundle.putString("call_from", "saved_questions");
                navController.navigate(R.id.nav_saved);
            }
        });

        findViewById(R.id.nav_test_my).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.nav_test_my) {
                navController.popBackStack();
                Bundle bundle = new Bundle();
                bundle.putString(Constant.CALL_FROM,"MY_TEST");
                navController.navigate(R.id.nav_test_my,bundle);
            }
        });


        findViewById(R.id.nav_test_popular).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.nav_test_popular) {
                navController.popBackStack();
                navController.navigate(R.id.nav_test_popular);
            }
        });


        findViewById(R.id.nav_attended_by_friends).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.nav_attended_by_friends) {
                navController.popBackStack();
                navController.navigate(R.id.nav_attended_by_friends);
            }
        });

        findViewById(R.id.nav_shared_with_you).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.nav_shared_with_you) {
                navController.popBackStack();
                navController.navigate(R.id.nav_shared_with_you);
            }
        });

        findViewById(R.id.nav_shared_by_you).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.nav_shared_by_you) {
                navController.popBackStack();
                navController.navigate(R.id.nav_shared_by_you);
            }
        });

        findViewById(R.id.nav_reviews).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.nav_reviews) {
                navController.popBackStack();
                navController.navigate(R.id.nav_reviews);
            }
        });

        findViewById(R.id.nav_published).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.nav_published) {
                navController.popBackStack();
                navController.navigate(R.id.nav_published);
            }
        });

        findViewById(R.id.nav_notifications).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.nav_notifications) {
                navController.popBackStack();
                navController.navigate(R.id.nav_notifications);
            }
        });

        findViewById(R.id.nav_settings).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.nav_settings) {
                navController.popBackStack();
                navController.navigate(R.id.nav_settings);
            }
        });

        findViewById(R.id.nav_faq).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.nav_faq) {
                navController.popBackStack();
                navController.navigate(R.id.nav_faq);
            }
        });


        findViewById(R.id.edit_profile).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.nav_edit_profile) {
                navController.popBackStack();
                navController.navigate(R.id.nav_edit_profile);
            }
        });

        findViewById(R.id.nav_logout).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            UniversalDialog universalDialog = new UniversalDialog(this, "Confirm Log Out",
                    "you are signing out of your Qoogol app on this device",
                    "Logout", "Cancel", this);
            universalDialog.show();
        });
    }

    private void setUserInfo() {
        Log.d(TAG, "setUserInfo: ");
        String displayName = new PreferenceManager(this).getString(Constant.DISPLAY_NAME);
        String gender = new PreferenceManager(this).getString(Constant.GENDER);
        String imageUrl = new PreferenceManager(this).getString(Constant.PROFILE_PIC);
        if(!displayName.isEmpty()) {
            mBinding.navHeader.tvName.setText(displayName.isEmpty() ? "Qoogol User" : displayName);
            if (imageUrl.isEmpty()) {
                if (gender.equalsIgnoreCase("M")) {
                    loadProfilePic(Constant.PRODUCTION_MALE_PROFILE_API,profileImage);
                } else if (gender.equalsIgnoreCase("F")) {
                    loadProfilePic(Constant.PRODUCTION_FEMALE_PROFILE_API,profileImage);
                } else {
                    loadProfilePic(Constant.PRODUCTION_MALE_PROFILE_API,profileImage);
                }
            } else {
                loadProfilePic(imageUrl,profileImage);
            }
        }else{
            fetchUserProfile();
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onPositiveButtonClick() {
        new PreferenceManager(getApplicationContext()).setIsLoggedIn(false);
        new PreferenceManager(getApplicationContext()).saveString(Constant.MOBILE, "");
        new PreferenceManager(getApplicationContext()).saveString(Constant.USER_ID, "");
        Intent intent = new Intent(this, LaunchActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void fetchUserProfile() {
        //ProgressDialog.getInstance().show(this);
        Log.d(TAG, "fetchUserProfile: ");
        Call<UserProfile> call = apiService.fetchUserInfo(new PreferenceManager(getApplicationContext()).getInt(Constant.USER_ID),
                getDeviceId(), Constant.APP_NAME, Constant.APP_VERSION);
        call.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                //ProgressDialog.getInstance().dismiss();
                if (response.body() != null && response.body().getResponseCode().equals("200")) {
                    mViewmodel.setUserProfileResponse(response.body());
                } else {
                    showErrorDialog(MainActivity.this, response.body().getResponseCode(), "");
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                //ProgressDialog.getInstance().dismiss();
                showToast("Something went wrong!!");
                t.printStackTrace();
            }
        });
    }



    @Override
    protected void onResume() {
        super.onResume();
        setUserInfo();
    }

    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }
}
