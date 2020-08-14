package com.jangletech.qoogol.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.jangletech.qoogol.BuildConfig;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.ActivityMainBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.enums.Nav;
import com.jangletech.qoogol.model.UserProfile;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.ui.doubts.DoubtListingDialog;
import com.jangletech.qoogol.ui.personal_info.PersonalInfoViewModel;
import com.jangletech.qoogol.util.AppUtils;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jangletech.qoogol.ui.BaseFragment.getUserId;
import static com.jangletech.qoogol.ui.BaseFragment.getUserName;
import static com.jangletech.qoogol.util.Constant.CALL_FROM;
import static com.jangletech.qoogol.util.Constant.profile;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding mBinding;
    private DrawerLayout drawerLayout;
    private NavController navController;
    private PersonalInfoViewModel mViewmodel;
    public static ImageView profileImage, badgeImg;
    public static TextView textViewDisplayName;
    public static TextView tvNavConnections, tvNavCredits, tvNavFollowers, tvNavFollowing;
    private String navigateFlag = "";
    private ApiInterface apiService = ApiClient.getInstance().getApi();
    private BottomNavigationView bottomNavigationView;
    public static String userId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mViewmodel = new ViewModelProvider(this).get(PersonalInfoViewModel.class);
        profileImage = findViewById(R.id.profilePic);
        badgeImg = findViewById(R.id.imgBadge);
        textViewDisplayName = findViewById(R.id.tvName);
        drawerLayout = findViewById(R.id.drawer_layout);
        tvNavConnections = findViewById(R.id.tvNavConnections);
        tvNavCredits = findViewById(R.id.tvNavCredits);
        tvNavFollowing = findViewById(R.id.tvNavFollowing);
        bottomNavigationView = findViewById(R.id.bottomNav);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setMargins(mBinding.marginLayout);
        getNotificationIntent(getIntent());
        userId = getUserId(this);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_test_popular, R.id.nav_attended_by_friends, R.id.nav_shared_with_you,
                R.id.nav_shared_by_you, R.id.nav_notifications, R.id.saved_questions,
                R.id.nav_test_popular, R.id.nav_recent_test, R.id.nav_share_app, R.id.nav_about,
                R.id.nav_faq, R.id.nav_fav, R.id.nav_syllabus, R.id.nav_edit_profile,
                R.id.nav_settings, R.id.nav_requests, R.id.nav_import_contacts,
                R.id.nav_code_conduct,
                R.id.nav_home, R.id.nav_learning, R.id.nav_shared_with_you_que, R.id.nav_shared_by_you_que, R.id.nav_test_my, R.id.nav_doubts)
                .setDrawerLayout(drawerLayout)
                .build();
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.nav_syllabus
                    || destination.getId() == R.id.nav_edit_profile
                    || destination.getId() == R.id.nav_test_filter
                    || destination.getId() == R.id.nav_comments
                    || destination.getId() == R.id.nav_share
                    || destination.getId() == R.id.nav_blocked_connections
                    || destination.getId() == R.id.nav_test_details) {
                hideBottomNav();
            } else {
                showBottomNav();
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            navigateFlag = "";
            if (item.getItemId() == R.id.nav_home) {
                if (navController.getCurrentDestination() != null &&
                        navController.getCurrentDestination().getId() != R.id.nav_home) {
                    if (navController.popBackStack(R.id.nav_home, false)) {
                    } else {
                        navController.navigate(R.id.nav_home);
                    }
                }
            }
            if (item.getItemId() == R.id.nav_learning) {
                //navigateFlag = Nav.LEARNING.toString();
                if (navController.getCurrentDestination().getId() != R.id.nav_learning) {
                    Bundle bundle = new Bundle();
                    bundle.putString("call_from", "learning");
                    navToFragment(R.id.nav_learning, bundle);
                }
            }
            if (item.getItemId() == R.id.nav_test_my) {
                //navigateFlag = Nav.MY_TEST.toString();
                //saveTestType(Nav.MY_TEST.toString());
                if (navController.getCurrentDestination() != null &&
                        navController.getCurrentDestination().getId() != R.id.nav_test_my) {
                    clearFilters();
                    navController.navigate(R.id.nav_test_my);
                }
            }
            if (item.getItemId() == R.id.nav_doubts) {
                //navigateFlag = Nav.ASK_DOUBTS.toString();
                if (navController.getCurrentDestination() != null &&
                        navController.getCurrentDestination().getId() != R.id.nav_doubts) {
                    navController.navigate(R.id.nav_doubts);
                }
            }
            if (item.getItemId() == R.id.nav_notifications) {
                //navigateFlag = Nav.NOTIFICATIONS.toString();
                if (navController.getCurrentDestination() != null &&
                        navController.getCurrentDestination().getId() != R.id.nav_notifications) {
                    navController.navigate(R.id.nav_notifications);
                }
            }
            return true;
        });


        mBinding.navHeader.tvName.setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            navigateFlag = Nav.USER_PROFILE.toString();
        });

        mBinding.navHeader.profilePic.setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            navigateFlag = Nav.USER_PROFILE.toString();
        });

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                loadProfileImage();
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                if (navigateFlag.equals(Nav.LEARNING.toString())) {
                    navigateFlag = "";
                    Bundle bundle = new Bundle();
                    bundle.putString("call_from", "learning");
                    navToFragment(R.id.nav_learning, bundle);
                }
                if (navigateFlag.equals(Nav.USER_PROFILE.toString())) {
                    navigateFlag = "";
                    Bundle bundle = new Bundle();
                    bundle.putInt(CALL_FROM, profile);
                    navToFragment(R.id.nav_edit_profile, bundle);
                }
                if (navigateFlag.equals(Nav.RECENT_TEST.toString())) {
                    navigateFlag = "";
                    clearFilters();
                    saveTestType(Nav.RECENT_TEST.toString());
                    Bundle bundle = new Bundle();
                    bundle.putString(CALL_FROM, Nav.RECENT_TEST.toString());
                    navToFragment(R.id.nav_recent_test, bundle);
                }
                if (navigateFlag.equals(Nav.FAVOURITE.toString())) {
                    navigateFlag = "";
                    navToFragment(R.id.nav_fav, Bundle.EMPTY);
                }
                if (navigateFlag.equals(Nav.HOME.toString())) {
                    navigateFlag = "";
                    navToFragment(R.id.nav_home, Bundle.EMPTY);
                }
                if (navigateFlag.equals(Nav.PENDING_REQ.toString())) {
                    navigateFlag = "";
                    navToFragment(R.id.nav_requests, Bundle.EMPTY);
                }
                if (navigateFlag.equals(Nav.BLOCKED_CONN.toString())) {
                    navigateFlag = "";
                    navToFragment(R.id.nav_blocked_connections, Bundle.EMPTY);
                }
                if (navigateFlag.equals(Nav.IMPORT_CONTACTS.toString())) {
                    navigateFlag = "";
                    navToFragment(R.id.nav_import_contacts, Bundle.EMPTY);
                }
                if (navigateFlag.equals(Nav.SAVED.toString())) {
                    navigateFlag = "";
                    navToFragment(R.id.saved_questions, Bundle.EMPTY);
                }

                if (navigateFlag.equals(Nav.MODIFY_SYLLABUS.toString())) {
                    navigateFlag = "";
                    navToFragment(R.id.nav_syllabus, Bundle.EMPTY);
                }
                if (navigateFlag.equals(Nav.SETTINGS.toString())) {
                    navigateFlag = "";
                    navToFragment(R.id.nav_settings, Bundle.EMPTY);
                }
                if (navigateFlag.equals(Nav.FAQ.toString())) {
                    navigateFlag = "";
                    navToFragment(R.id.nav_faq, Bundle.EMPTY);
                }
                if (navigateFlag.equals(Nav.SHARE_APP.toString())) {
                    //navToFragment(R.id.nav_share_app, Bundle.EMPTY);
                    navigateFlag = "";
                    if (getUserName(getApplicationContext()) != null && getUserName(getApplicationContext()).isEmpty()) {
                        fetchUserProfile();
                    } else {
                        shareAction();
                    }
                }
                if (navigateFlag.equals(Nav.ABOUT.toString())) {
                    navToFragment(R.id.nav_about, Bundle.EMPTY);
                }
                if (navigateFlag.equals(Nav.MY_TEST.toString())) {
                    navigateFlag = "";
                    clearFilters();
                    saveTestType(Nav.MY_TEST.toString());
                    Bundle bundle = new Bundle();
                    bundle.putString(CALL_FROM, Nav.MY_TEST.toString());
                    navToFragment(R.id.nav_test_my, bundle);
                }

                if (navigateFlag.equals(Nav.SHARED_BY_ME_QUE.toString())) {
                    navigateFlag = "";
                    clearFilters();
                    saveTestType(Nav.SHARED_BY_ME_QUE.toString());
                    Bundle bundle = new Bundle();
                    bundle.putString(CALL_FROM, Nav.SHARED_BY_ME_QUE.toString());
                    navToFragment(R.id.nav_shared_by_you_que, bundle);
                }

                if (navigateFlag.equals(Nav.SHARED_WITH_ME_QUE.toString())) {
                    navigateFlag = "";
                    clearFilters();
                    saveTestType(Nav.SHARED_WITH_ME_QUE.toString());
                    Bundle bundle = new Bundle();
                    bundle.putString(CALL_FROM, Nav.SHARED_WITH_ME_QUE.toString());
                    navToFragment(R.id.nav_shared_with_you_que, bundle);
                }
                if (navigateFlag.equals(Nav.POPULAR_TEST.toString())) {
                    navigateFlag = "";
                    clearFilters();
                    saveTestType(Nav.POPULAR_TEST.toString());
                    Bundle bundle = new Bundle();
                    bundle.putString("CALL_FROM", Nav.POPULAR_TEST.toString());
                    navToFragment(R.id.nav_test_popular, bundle);
                }
                if (navigateFlag.equals(Nav.SHARED_BY_YOU.toString())) {
                    navigateFlag = "";
                    clearFilters();
                    saveTestType(Nav.SHARED_BY_YOU.toString());
                    Bundle bundle = new Bundle();
                    bundle.putString(CALL_FROM, Nav.SHARED_BY_YOU.toString());
                    navToFragment(R.id.nav_shared_by_you, bundle);
                }
                if (navigateFlag.equals(Nav.SHARED_WITH_YOU.toString())) {
                    navigateFlag = "";
                    saveTestType(Nav.SHARED_WITH_YOU.toString());
                    Bundle bundle = new Bundle();
                    bundle.putString("CALL_FROM", Nav.SHARED_WITH_YOU.toString());
                    navToFragment(R.id.nav_shared_with_you, bundle);
                }
                if (navigateFlag.equals(Nav.ATTENDED_BY_FRIENDS.toString())) {
                    navigateFlag = "";
                    Bundle bundle = new Bundle();
                    bundle.putString("CALL_FROM", Nav.ATTENDED_BY_FRIENDS.toString());
                    navToFragment(R.id.nav_attended_by_friends, bundle);
                }
                if (navigateFlag.equals(Nav.CONNECTIONS.toString())) {
                    navigateFlag = "";
                    Bundle bundle = new Bundle();
                    bundle.putInt("position", 0);
                    navToFragment(R.id.nav_connections, bundle);
                }
                if (navigateFlag.equals(Nav.CONNECTIONS_FOLLOWING.toString())) {
                    navigateFlag = "";
                    Bundle bundle = new Bundle();
                    bundle.putInt("position", 3);
                    navToFragment(R.id.nav_connections, bundle);
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

        mBinding.navHeader.friendsLayout.setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.nav_connections) {
                navigateFlag = Nav.CONNECTIONS.toString();
            }
        });

        mBinding.navHeader.followersLayout.setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.nav_connections) {
                navigateFlag = Nav.CONNECTIONS_FOLLOWING.toString();
            }
        });

        mBinding.tvNavHome.setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.nav_home) {
                navigateFlag = Nav.HOME.toString();
            }
        });

        mBinding.navFav.setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawer(Gravity.LEFT);
            if (navController.getCurrentDestination().getId() != R.id.nav_fav) {
                navigateFlag = Nav.FAVOURITE.toString();
            }
        });


        findViewById(R.id.tvNavLearning).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.nav_learning) {
                navigateFlag = Nav.LEARNING.toString();
            }
        });

        findViewById(R.id.nav_recent_test).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.nav_recent_test) {
                navigateFlag = Nav.RECENT_TEST.toString();
            }
        });

        findViewById(R.id.nav_requests).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.nav_requests) {
                navigateFlag = Nav.PENDING_REQ.toString();
            }
        });

        findViewById(R.id.nav_doubts).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            DoubtListingDialog doubtListingDialog = new DoubtListingDialog(this, null, Constant.my_doubts);
            doubtListingDialog.show();
        });

        findViewById(R.id.nav_blocked_connections).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.nav_blocked_connections) {
                navigateFlag = Nav.BLOCKED_CONN.toString();
            }
        });

        findViewById(R.id.nav_import_contacts).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.nav_import_contacts) {
                navigateFlag = Nav.IMPORT_CONTACTS.toString();
            }
        });

        findViewById(R.id.saved_questions).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.saved_questions) {
                navigateFlag = Nav.SAVED.toString();
            }
        });

        findViewById(R.id.tvNavTest).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.nav_test_my) {
                navigateFlag = Nav.MY_TEST.toString();
            }
        });

        findViewById(R.id.nav_test_popular).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.nav_test_popular) {
                navigateFlag = Nav.POPULAR_TEST.toString();
            }
        });

        findViewById(R.id.nav_attended_by_friends).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.nav_attended_by_friends) {
                navigateFlag = Nav.ATTENDED_BY_FRIENDS.toString();
            }
        });

        findViewById(R.id.nav_shared_with_you).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.nav_shared_with_you) {
                navigateFlag = Nav.SHARED_WITH_YOU.toString();
            }
        });

        findViewById(R.id.nav_shared_by_you).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.nav_shared_by_you) {
                navigateFlag = Nav.SHARED_BY_YOU.toString();
            }
        });

        findViewById(R.id.nav_shared_by_you_que).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.nav_shared_by_you_que) {
                navigateFlag = Nav.SHARED_BY_ME_QUE.toString();
            }
        });

        findViewById(R.id.nav_shared_with_you_que).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.nav_shared_with_you_que) {
                navigateFlag = Nav.SHARED_WITH_ME_QUE.toString();
            }
        });

        findViewById(R.id.nav_settings).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.nav_settings) {
                navigateFlag = Nav.SETTINGS.toString();
            }
        });

        findViewById(R.id.nav_code_conduct).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            navToFragment(R.id.nav_code_conduct, Bundle.EMPTY);
            /*if (navController.getCurrentDestination().getId() != R.id.nav_settings) {
                navigateFlag = Nav.SETTINGS.toString();
            }*/
        });


        findViewById(R.id.tvTest).setOnClickListener(v -> {
            if (mBinding.testNavLayout.getVisibility() == View.VISIBLE) {
                mBinding.testNavLayout.setVisibility(View.GONE);
                mBinding.expand.setBackground(getDrawable(R.drawable.ic_expand));
                //collapse(mBinding.testNavLayout);
            } else {
                mBinding.testNavLayout.setVisibility(View.VISIBLE);
                mBinding.expand.setBackground(getDrawable(R.drawable.ic_collapse));
                expand(mBinding.testNavLayout);
            }
        });

        findViewById(R.id.tvLearning).setOnClickListener(v -> {
            if (mBinding.learningNavLayout.getVisibility() == View.VISIBLE) {
                mBinding.learningNavLayout.setVisibility(View.GONE);
                mBinding.expandLearning.setBackground(getDrawable(R.drawable.ic_expand));
                //collapse(mBinding.testNavLayout);
            } else {
                mBinding.learningNavLayout.setVisibility(View.VISIBLE);
                mBinding.expandLearning.setBackground(getDrawable(R.drawable.ic_collapse));
                expand(mBinding.learningNavLayout);
            }
        });


        findViewById(R.id.nav_syllabus).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.nav_syllabus) {
                navigateFlag = Nav.MODIFY_SYLLABUS.toString();
            }
        });

        findViewById(R.id.nav_faq).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.nav_faq) {
                navigateFlag = Nav.FAQ.toString();
            }
        });

        findViewById(R.id.profilePicLayout).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.nav_edit_profile) {
                navigateFlag = Nav.USER_PROFILE.toString();
            }
        });

        findViewById(R.id.nav_share_app).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.nav_share_app) {
                navigateFlag = Nav.SHARE_APP.toString();
            }
        });

        findViewById(R.id.nav_about).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.nav_about) {
                navigateFlag = Nav.ABOUT.toString();
            }
        });
    }

    /*@Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getNotificationIntent(intent);
    }*/

    private void getNotificationIntent(Intent intent) {
        if (intent != null) {
            if (intent.hasExtra("bundle")) {
                Bundle bundle = intent.getBundleExtra("bundle");
                if (bundle != null && bundle.getBoolean("fromNotification")) {
                    String action = bundle.getString(Constant.FB_ACTION);
                    Log.d(TAG, "getNotificationIntent From Notification : " + bundle.getString(Constant.FB_FROM_TYPE));
                    Log.d(TAG, "getNotificationIntent From Type : " + bundle.getString(Constant.FB_FROM_TYPE));
                    Log.d(TAG, "getNotificationIntent Action : " + bundle.getString(Constant.FB_ACTION));
                    Log.d(TAG, "getNotificationIntent msId : " + bundle.getString(Constant.FB_MS_ID));
                    if (action != null && action.equalsIgnoreCase("T")) {
                        navToFragment(R.id.nav_test_my, bundle);
                    } else if (action != null && action.equalsIgnoreCase("Q")) {
                        navToFragment(R.id.nav_learning, bundle);
                    } else if (action != null && action.equalsIgnoreCase("CF")) {
                        navToFragment(R.id.nav_requests, bundle);
                    } else if (action != null && action.equalsIgnoreCase("CAF") ||
                            action.equalsIgnoreCase("CA")) {
                        navToFragment(R.id.nav_connections, bundle);
                    }
                }
            }
        }
    }

    private void navToFragment(int resId, Bundle bundle) {
        navController.popBackStack();
        navController.navigate(resId, bundle);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

  /*  @Override
    public void onPositiveButtonClick() {
        new PreferenceManager(getApplicationContext()).setIsLoggedIn(false);
        new PreferenceManager(getApplicationContext()).saveString(Constant.MOBILE, "");
        new PreferenceManager(getApplicationContext()).saveString(Constant.USER_ID, "");
        Intent intent = new Intent(this, LaunchActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }*/

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawer(Gravity.LEFT);
        }
        if (navController.getCurrentDestination() != null) {
            if (navController.getCurrentDestination() != null &&
                    (navController.getCurrentDestination().getId() == R.id.nav_home ||
                            navController.getCurrentDestination().getId() == R.id.nav_notifications ||
                            navController.getCurrentDestination().getId() == R.id.nav_doubts ||
                            navController.getCurrentDestination().getId() == R.id.nav_test_my ||
                            navController.getCurrentDestination().getId() == R.id.nav_learning)) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
                builder.setTitle("Exit")
                        .setMessage("Are you sure, you want to close this application")
                        .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();

            } else {
                if (navController.getCurrentDestination().getId() == R.id.nav_edit_profile ||
                        navController.getCurrentDestination().getId() == R.id.nav_settings ||
                        navController.getCurrentDestination().getId() == R.id.nav_syllabus ||
                        navController.getCurrentDestination().getId() == R.id.nav_about ||
                        navController.getCurrentDestination().getId() == R.id.nav_import_contacts ||
                        navController.getCurrentDestination().getId() == R.id.nav_code_conduct ||
                        navController.getCurrentDestination().getId() == R.id.nav_faq ||
                        navController.getCurrentDestination().getId() == R.id.nav_fav ||
                        navController.getCurrentDestination().getId() == R.id.nav_shared_with_you_que ||
                        navController.getCurrentDestination().getId() == R.id.nav_shared_by_you_que ||
                        navController.getCurrentDestination().getId() == R.id.saved_questions ||
                        navController.getCurrentDestination().getId() == R.id.nav_shared_by_you ||
                        navController.getCurrentDestination().getId() == R.id.nav_shared_with_you ||
                        navController.getCurrentDestination().getId() == R.id.nav_test_popular ||
                        navController.getCurrentDestination().getId() == R.id.nav_recent_test ||
                        navController.getCurrentDestination().getId() == R.id.nav_connections ||
                        navController.getCurrentDestination().getId() == R.id.nav_requests) {
                    navController.navigate(R.id.nav_home);
                } else {
                    navController.popBackStack();
                }
            }
        }
    }

    private void saveTestType(String type) {
        new PreferenceManager(getApplicationContext()).saveString(CALL_FROM, type);
    }

    private void showBottomNav() {
        bottomNavigationView.setVisibility(View.VISIBLE);
    }

    private void hideBottomNav() {
        bottomNavigationView.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume MainActivity: ");
    }

    private void loadProfileImage() {
        String badge = new PreferenceManager(getApplicationContext()).getString("BADGE");
        String imgUrl = new PreferenceManager(getApplicationContext()).getString("PROF_IMG");
        String gender = new PreferenceManager(getApplicationContext()).getString(Constant.GENDER).replace("'", "");
        Log.d(TAG, "loadProfileImage Img Url : " + imgUrl);
        Log.d(TAG, "loadProfileImage Gender : " + gender);
        Log.d(TAG, "loadProfileImage Badge : " + badge);
        if (imgUrl != null && !imgUrl.isEmpty()) {
            loadProfilePic(imgUrl, MainActivity.profileImage);
        } else {
            if (gender.equalsIgnoreCase("F")) {
                loadProfilePic(Constant.PRODUCTION_FEMALE_PROFILE_API, MainActivity.profileImage);
            }
            if (gender.equalsIgnoreCase("M")) {
                loadProfilePic(Constant.PRODUCTION_MALE_PROFILE_API, MainActivity.profileImage);
            }
        }

        if (badge != null && !badge.isEmpty()) {
            if (badge.equalsIgnoreCase("B")) ;
            badgeImg.setImageDrawable(getDrawable(R.drawable.bronze));
            if (badge.equalsIgnoreCase("G")) ;
            badgeImg.setImageDrawable(getDrawable(R.drawable.gold));
            if (badge.equalsIgnoreCase("S")) ;
            badgeImg.setImageDrawable(getDrawable(R.drawable.silver));
            if (badge.equalsIgnoreCase("P")) ;
            badgeImg.setImageDrawable(getDrawable(R.drawable.platinum));
        }
    }

    private void fetchUserProfile() {
        ProgressDialog.getInstance().show(this);
        Call<UserProfile> call = apiService.fetchUserInfo(getUserId(getApplicationContext()), getDeviceId(), Constant.APP_NAME, Constant.APP_VERSION);
        call.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.body() != null && response.body().getResponseCode().equals("200")) {
                    new PreferenceManager(getApplicationContext()).saveString(Constant.userName, response.body().getUserName());
                    shareAction();
                } else if (response.body().getResponseCode().equals("501")) {
                    resetSettingAndLogout();
                } else {
                    //showErrorDialog(this, response.body().getResponseCode(), "");
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                Log.e(TAG, "onFailure UserProfile: " + t.getMessage());
                apiCallFailureDialog(t);
                showToast("Something went wrong!!");
                t.printStackTrace();
            }
        });
    }

    private void shareAction() {
        try {
            Bitmap bitmap;
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.qoogol);
            if (drawable instanceof BitmapDrawable) {
                bitmap = ((BitmapDrawable) drawable).getBitmap();
            } else if (drawable instanceof VectorDrawable) {
                bitmap = getBitmap((VectorDrawable) drawable);
            } else {
                throw new IllegalArgumentException("unsupported drawable type");
            }
            File cachePath = new File(getExternalCacheDir(), "images");
            cachePath.mkdirs(); // don't forget to make the directory
            FileOutputStream stream = new FileOutputStream(cachePath + "/image.png"); // overwrites this image every time
            bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);
            stream.close();

            File newFile = new File(cachePath, "image.png");
            Uri contentUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", newFile);

            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            sendIntent.setType("image/*");

            String shareMessage = String.format(Locale.ENGLISH,
                    "Install Qoogol android app for free academic tests, prepare for competitive exams & post doubts. Referral Code : %s", getUserName(getApplicationContext()) + "\n");
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n";
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Qoogol");
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);

            startActivity(Intent.createChooser(sendIntent, "Share app link via..."));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private void getEducationInfoList() {
//        //ProgressDialog.getInstance().show(getActivity());
//        Call<FetchEducationResponse> call = apiService.fetchUserEdu(userid, "L", getDeviceId(getActivity()), Constant.APP_NAME);
//
//        call.enqueue(new Callback<FetchEducationResponse>() {
//            @Override
//            public void onResponse(Call<FetchEducationResponse> call, Response<FetchEducationResponse> response) {
//                ProgressDialog.getInstance().dismiss();
//                if (response.body() != null && response.body().getResponseCode().equals("200")) {
//
//                } else {
//                    showErrorDialog(requireActivity(), response.body().getResponseCode(), "");
//                }
//            }
//
//            @Override
//            public void onFailure(Call<FetchEducationResponse> call, Throwable t) {
//                showToast("Something went wrong!!");
//                ProgressDialog.getInstance().dismiss();
//                t.printStackTrace();
//            }
//        });
//    }
}
