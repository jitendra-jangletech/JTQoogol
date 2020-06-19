package com.jangletech.qoogol.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.ActivityMainBinding;
import com.jangletech.qoogol.dialog.UniversalDialog;
import com.jangletech.qoogol.enums.Nav;
import com.jangletech.qoogol.ui.personal_info.PersonalInfoViewModel;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;

import static com.jangletech.qoogol.util.Constant.CALL_FROM;
import static com.jangletech.qoogol.util.Constant.profile;

public class MainActivity extends BaseActivity implements UniversalDialog.DialogButtonClickListener {

    private static final String TAG = "MainActivity";
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding mBinding;
    private DrawerLayout drawerLayout;
    private NavController navController;
    private PersonalInfoViewModel mViewmodel;
    public static ImageView profileImage;
    public static TextView textViewDisplayName;
    public static TextView tvNavConnections, tvNavCredits, tvNavFollowers, tvNavFollowing;
    private String navigateFlag = "";
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mViewmodel = new ViewModelProvider(this).get(PersonalInfoViewModel.class);
        profileImage = findViewById(R.id.profilePic);
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
        NavigationView navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_test_popular, R.id.nav_attended_by_friends, R.id.nav_shared_with_you,
                R.id.nav_shared_by_you,R.id.nav_notifications,
                R.id.nav_faq, R.id.nav_fav, R.id.nav_syllabus,
                R.id.nav_settings, R.id.saved_questions, R.id.nav_requests, R.id.nav_import_contacts,
                R.id.nav_home, R.id.nav_learning, R.id.nav_test_my, R.id.nav_doubts)
                .setDrawerLayout(drawerLayout)
                .build();
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                if (destination.getId() == R.id.nav_syllabus
                        || destination.getId() == R.id.nav_edit_profile
                        || destination.getId() == R.id.nav_test_filter
                        || destination.getId() == R.id.nav_blocked_connections
                        || destination.getId() == R.id.nav_test_details) {
                    hideBottomNav();
                } else {
                    showBottomNav();
                }
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_home) {
                    if (navController.getCurrentDestination().getId() != R.id.nav_home) {
                        if (navController.popBackStack(R.id.nav_home, false)) {
                        } else {
                            navController.navigate(R.id.nav_home);
                        }
                    }
                }
                if (item.getItemId() == R.id.nav_learning) {
                    if (navController.getCurrentDestination().getId() != R.id.nav_learning) {
                        navController.navigate(R.id.nav_learning);
                    }
                }
                if (item.getItemId() == R.id.nav_test_my) {
                    if (navController.getCurrentDestination().getId() != R.id.nav_test_my) {
                        navController.navigate(R.id.nav_test_my);
                    }
                }
                if (item.getItemId() == R.id.nav_doubts) {
                    if (navController.getCurrentDestination().getId() != R.id.nav_doubts) {
                        navController.navigate(R.id.nav_doubts);
                    }
                }
                if (item.getItemId() == R.id.nav_notifications) {
                    if (navController.getCurrentDestination().getId() != R.id.nav_notifications) {
                        navController.navigate(R.id.nav_notifications);
                    }
                }
                return true;
            }
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

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
                if (navigateFlag.equals(Nav.LEARNING.toString())) {
                    Bundle bundle = new Bundle();
                    bundle.putString("call_from", "learning");
                    navToFragment(R.id.nav_learning, bundle);
                }
                if (navigateFlag.equals(Nav.USER_PROFILE.toString())) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(CALL_FROM, profile);
                    navToFragment(R.id.nav_edit_profile, bundle);
                }
                if (navigateFlag.equals(Nav.FAVOURITE.toString())) {
                    navToFragment(R.id.nav_fav, Bundle.EMPTY);
                }
                if (navigateFlag.equals(Nav.HOME.toString())) {
                    navToFragment(R.id.nav_home, Bundle.EMPTY);
                }
                if (navigateFlag.equals(Nav.PENDING_REQ.toString())) {
                    navToFragment(R.id.nav_requests, Bundle.EMPTY);
                }
                if (navigateFlag.equals(Nav.BLOCKED_CONN.toString())) {
                    navToFragment(R.id.nav_blocked_connections, Bundle.EMPTY);
                }
                if (navigateFlag.equals(Nav.IMPORT_CONTACTS.toString())) {
                    navToFragment(R.id.nav_import_contacts, Bundle.EMPTY);
                }
                if (navigateFlag.equals(Nav.SAVED.toString())) {
                    navToFragment(R.id.saved_questions, Bundle.EMPTY);
                }
                if (navigateFlag.equals(Nav.MODIFY_SYLLABUS.toString())) {
                    navToFragment(R.id.nav_syllabus, Bundle.EMPTY);
                }
                if (navigateFlag.equals(Nav.SETTINGS.toString())) {
                    navToFragment(R.id.nav_settings, Bundle.EMPTY);
                }
                if (navigateFlag.equals(Nav.FAQ.toString())) {
                    navToFragment(R.id.nav_faq, Bundle.EMPTY);
                }
                /*if (navigateFlag.equals(Nav.NOTIFICATIONS.toString())) {
                    navToFragment(R.id.nav_notifications, Bundle.EMPTY);
                }*/
                if (navigateFlag.equals(Nav.LOGOUT.toString())) {
                    showLogoutDialog();
                }
                if (navigateFlag.equals(Nav.MY_TEST.toString())) {
                    Bundle bundle = new Bundle();
                    bundle.putString(CALL_FROM, Nav.MY_TEST.toString());
                    navToFragment(R.id.nav_test_my, bundle);
                }
                if (navigateFlag.equals(Nav.POPULAR_TEST.toString())) {
                    Bundle bundle = new Bundle();
                    bundle.putString("CALL_FROM", Nav.POPULAR_TEST.toString());
                    navToFragment(R.id.nav_test_popular, bundle);
                }
                if (navigateFlag.equals(Nav.SHARED_BY_YOU.toString())) {
                    Bundle bundle = new Bundle();
                    bundle.putString("CALL_FROM", Nav.SHARED_BY_YOU.toString());
                    navToFragment(R.id.nav_shared_by_you, bundle);
                }
                if (navigateFlag.equals(Nav.SHARED_WITH_YOU.toString())) {
                    Bundle bundle = new Bundle();
                    bundle.putString("CALL_FROM", Nav.SHARED_WITH_YOU.toString());
                    navToFragment(R.id.nav_shared_with_you, bundle);
                }
                if (navigateFlag.equals(Nav.ATTENDED_BY_FRIENDS.toString())) {
                    Bundle bundle = new Bundle();
                    bundle.putString("CALL_FROM", Nav.ATTENDED_BY_FRIENDS.toString());
                    navToFragment(R.id.nav_attended_by_friends, bundle);
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {

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

        findViewById(R.id.nav_requests).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.nav_requests) {
                navigateFlag = Nav.PENDING_REQ.toString();
            }
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

        /*findViewById(R.id.nav_notifications).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.nav_notifications) {
                navigateFlag = Nav.NOTIFICATIONS.toString();
            }
        });*/

        findViewById(R.id.nav_settings).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.nav_settings) {
                navigateFlag = Nav.SETTINGS.toString();
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

        /*findViewById(R.id.nav_doubts).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.nav_doubts) {
                navigateFlag = Nav.ASK_DOUBTS.toString();
            }
        });*/

        findViewById(R.id.nav_logout).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            navigateFlag = Nav.LOGOUT.toString();
        });
    }

    private void showLogoutDialog() {
        UniversalDialog universalDialog = new UniversalDialog(this, "Confirm Log Out",
                "you are signing out of your Qoogol app on this device",
                "Logout", "Cancel", this);
        universalDialog.show();
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

    @Override
    public void onPositiveButtonClick() {
        new PreferenceManager(getApplicationContext()).setIsLoggedIn(false);
        new PreferenceManager(getApplicationContext()).saveString(Constant.MOBILE, "");
        new PreferenceManager(getApplicationContext()).saveString(Constant.USER_ID, "");
        Intent intent = new Intent(this, LaunchActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawer(Gravity.LEFT);
        }
        if (navController.getCurrentDestination().getId() == R.id.nav_home) {

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
            navController.navigate(R.id.nav_home);
            //navController.navigateUp();
        }
    }

    private void showBottomNav() {
        bottomNavigationView.setVisibility(View.VISIBLE);
    }

    private void hideBottomNav() {
        bottomNavigationView.setVisibility(View.GONE);
    }
}
