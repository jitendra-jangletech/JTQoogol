package com.jangletech.qoogol.activities;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfImageObject;
import com.jangletech.qoogol.BuildConfig;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.ActivityMainBinding;
import com.jangletech.qoogol.databinding.AlertDialogBinding;
import com.jangletech.qoogol.databinding.MediaUploadLayoutBinding;
import com.jangletech.qoogol.dialog.AddImageDialog;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.dialog.PublicProfileDialog;
import com.jangletech.qoogol.enums.Nav;
import com.jangletech.qoogol.listeners.QueMediaListener;
import com.jangletech.qoogol.model.ImageObject;
import com.jangletech.qoogol.model.UserProfile;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.ui.personal_info.PersonalInfoViewModel;
import com.jangletech.qoogol.util.AppUtils;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.vincent.filepicker.activity.NormalFilePickActivity;
import com.vincent.filepicker.filter.entity.NormalFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jangletech.qoogol.ui.BaseFragment.getUserId;
import static com.jangletech.qoogol.ui.BaseFragment.getUserName;
import static com.jangletech.qoogol.util.Constant.CALL_FROM;
import static com.jangletech.qoogol.util.Constant.profile;

public class MainActivity extends BaseActivity implements PublicProfileDialog.PublicProfileClickListener,AddImageDialog.AddImageClickListener {

    private static final String TAG = "MainActivity";
    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding mBinding;
    private DrawerLayout drawerLayout;
    private NavController navController;
    public static boolean isTestScreenEnabled = false;
    private PersonalInfoViewModel mViewmodel;
    public static ImageView profileImage, badgeImg;
    public static TextView textViewDisplayName;
    public static TextView tvNavConnections, tvNavCredits, tvNavFollowers, tvNavFollowing;
    private String navigateFlag = "";
    private ApiInterface apiService = ApiClient.getInstance().getApi();
    public static BottomNavigationView bottomNavigationView;
    public static String userId = "";
    QueMediaListener queMediaListener;
    private static final int CAMERA_REQUEST = 1, GALLERY_REQUEST = 2, PICKFILE_REQUEST_CODE=3, VIDEO_REQUEST = 4, AUDIO_REQUEST = 5;
    private Uri mphotouri;
    private AlertDialog mediaDialog;
    private int optionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mBinding.tvAppVersion.setText(" " + BuildConfig.VERSION_NAME);
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
                R.id.nav_shared_with_you,
                R.id.nav_shared_by_you, R.id.nav_notifications, R.id.saved_questions,
                R.id.nav_share_app, R.id.nav_about,
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
                    || destination.getId() == R.id.nav_blocked_connections
                    || destination.getId() == R.id.nav_test_details
                    || destination.getId() == R.id.nav_scq_question
                    || destination.getId() == R.id.nav_mcq_question
                    || destination.getId() == R.id.nav_longans_question
                    || destination.getId() == R.id.nav_shortans_question
                    || destination.getId() == R.id.nav_scan_quest
                    || destination.getId() == R.id.nav_mtp_question
                    || destination.getId() == R.id.nav_quest_type
                    || destination.getId() == R.id.nav_true_false_frag
                    || destination.getId() == R.id.nav_fill_the_blanks
                    || destination.getId() == R.id.nav_scq_image
                    || destination.getId() == R.id.nav_upload_question) {
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

                if (navigateFlag.equals(Nav.FAVOURITE.toString())) {
                    navigateFlag = "";
                    navToFragment(R.id.nav_fav, Bundle.EMPTY);
                }
                if (navigateFlag.equals(Nav.HOME.toString())) {
                    navigateFlag = "";
                    navToFragment(R.id.nav_home, Bundle.EMPTY);
                }
                if (navigateFlag.equals(Nav.CREATE_PDF.toString())) {
                    navigateFlag = "";
                    navToFragment(R.id.nav_create_pdf, Bundle.EMPTY);
                }

                if (navigateFlag.equals(Nav.PENDING_REQ.toString())) {
                    navigateFlag = "";
                    navToFragment(R.id.nav_requests, Bundle.EMPTY);
                }
                if (navigateFlag.equals(Nav.UPLOAD_QUE.toString())) {
                    navigateFlag = "";
                    navToFragment(R.id.nav_upload_question, Bundle.EMPTY);
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

                if (navigateFlag.equals(Nav.DOUBTS.toString())) {
                    navigateFlag = "";
                    navToFragment(R.id.nav_doubts, Bundle.EMPTY);
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

                if (navigateFlag.equalsIgnoreCase(Nav.CREATE_TEST.toString())) {
                    navigateFlag = "";
                    Intent i = new Intent(MainActivity.this, CreateTestActivity.class);
                    startActivity(i);
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

        mBinding.navFav.setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawer(Gravity.LEFT);
            if (navController.getCurrentDestination().getId() != R.id.nav_fav) {
                navigateFlag = Nav.FAVOURITE.toString();
            }
        });

        mBinding.navProfile.setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawer(Gravity.LEFT);
            if (navController.getCurrentDestination().getId() != R.id.nav_edit_profile) {
                navigateFlag = Nav.USER_PROFILE.toString();
            }
        });


        findViewById(R.id.tvNavLearning).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.nav_learning) {
                navigateFlag = Nav.LEARNING.toString();
            }
        });

        findViewById(R.id.nav_create_pdf).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.nav_create_pdf) {
                navigateFlag = Nav.CREATE_PDF.toString();
            }
        });

        findViewById(R.id.nav_requests).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.nav_requests) {
                navigateFlag = Nav.PENDING_REQ.toString();
            }
        });

        findViewById(R.id.nav_upload_question).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.nav_upload_question) {
                navigateFlag = Nav.UPLOAD_QUE.toString();
            }
        });

        findViewById(R.id.nav_doubts).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.nav_doubts) {
                navigateFlag = Nav.DOUBTS.toString();
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

        /*findViewById(R.id.nav_test_popular).setOnClickListener(v -> {
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
        });*/

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

        findViewById(R.id.nav_create_test).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            navigateFlag = Nav.CREATE_TEST.toString();
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
        });


        findViewById(R.id.tvTest).setOnClickListener(v -> {
            if (mBinding.testNavLayout.getVisibility() == View.VISIBLE) {
                mBinding.testNavLayout.setVisibility(View.GONE);
                mBinding.expand.setBackground(getDrawable(R.drawable.ic_expand));
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

    public void setOnDataListener(QueMediaListener queMediaListener){
       this.queMediaListener =queMediaListener;
    }

    private void getNotificationIntent(Intent intent) {
        if (intent != null) {
            if (intent.hasExtra("bundle")) {
                Bundle bundle = intent.getBundleExtra("bundle");
                if (bundle != null && bundle.getBoolean("fromNotification")) {
                    String action = bundle.getString(Constant.FB_ACTION);
                    String uId = bundle.getString(Constant.FB_U_G_ID);
                    Log.d(TAG, "getNotificationIntent UId : " + uId);
                    Log.d(TAG, "getNotificationIntent From Notification : " + bundle.getString(Constant.FB_FROM_TYPE));
                    Log.d(TAG, "getNotificationIntent From Type : " + bundle.getString(Constant.FB_FROM_TYPE));
                    Log.d(TAG, "getNotificationIntent Action : " + bundle.getString(Constant.FB_ACTION));
                    Log.d(TAG, "getNotificationIntent msId : " + bundle.getString(Constant.FB_MS_ID));
                    if (action != null && action.equalsIgnoreCase("T")) {
                        navToFragment(R.id.nav_test_my, bundle);
                    } else if (action != null && action.equalsIgnoreCase("Q")) {
                        navToFragment(R.id.nav_learning, bundle);
                    } else {
                        try {
                            PublicProfileDialog publicProfileDialog = new PublicProfileDialog(this, uId, this);
                            publicProfileDialog.show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    /*else if (action != null && action.equalsIgnoreCase("CF") ||
                            action.equalsIgnoreCase("CC")) {
                        //navToFragment(R.id.nav_requests, bundle);
                        PublicProfileDialog publicProfileDialog = new PublicProfileDialog(this, userId, this);
                        publicProfileDialog.show();
                    } else if (action != null && action.equalsIgnoreCase("CAF") ||
                            action.equalsIgnoreCase("CA") || action.equalsIgnoreCase("CN")) {
                        navToFragment(R.id.nav_connections, bundle);
                    }*/
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == com.vincent.filepicker.Constant.REQUEST_CODE_PICK_FILE && resultCode == RESULT_OK && data != null) {
            if (resultCode == RESULT_OK) {
                ArrayList<NormalFile> list = data.getParcelableArrayListExtra(com.vincent.filepicker.Constant.RESULT_PICK_FILE);
                //StringBuilder builder = new StringBuilder();
                for (NormalFile file : list) {
                    String path = file.getPath();
                    Log.i(TAG, "onActivityResult File Path : " + file.getPath());
                    extractImages(path);
                }
            }
        } else {
            if (queMediaListener !=null)
                queMediaListener.onMediaReceived(requestCode,resultCode,data,mphotouri);
        }

    }

    private void extractImages(String filepath) {
        Log.i(TAG, "extractImages Copied File Path : " + filepath);
        PRStream prStream;

        File file;

        PdfObject pdfObject;

        PdfImageObject pdfImageObject;

        FileOutputStream fos;

        try {

            // Create pdf reader
            //file = new File(filepath);

            PdfReader reader = new PdfReader(filepath);

            File folder = new File(Environment.getExternalStorageDirectory() + "/Qoogol/Temp");

            if (!folder.exists())
                folder.mkdir();
            else
                AppUtils.deleteDir(folder);

            // Get number of objects in pdf document

            int numOfObject = reader.getXrefSize();
            int imageCount = 0;

            Log.i(TAG, "No. Of Objects Found : " + numOfObject);

            for (int i = 0; i < numOfObject; i++) {

                // Get PdfObject

                pdfObject = reader.getPdfObject(i);

                if (pdfObject != null && pdfObject.isStream()) {

                    prStream = (PRStream) pdfObject; //cast object to stream

                    PdfObject type = prStream.get(PdfName.SUBTYPE); //get the object type

                    // Check if the object is the image type object

                    if (type != null && (type.toString().equals(PdfName.IMAGE.toString()))) {
//                            type.toString().equals(PdfName.IMAGEB.toString()) ||
//                            type.toString().equals(PdfName.IMAGEC.toString()) ||
//                            type.toString().equals(PdfName.IMAGEI.toString()))) {

                        imageCount++;
                        // Get the image from the stream
                        pdfImageObject = new PdfImageObject(prStream);
                        fos = new FileOutputStream(folder.getPath() + "/" + System.currentTimeMillis() + ".jpg");

                        // Read bytes of image in to an array

                        byte[] imgdata = pdfImageObject.getImageAsBytes();

                        // Write the bytes array to file

                        fos.write(imgdata, 0, imgdata.length);

                        fos.flush();

                        fos.close();
                    }
                }
            }

            //mBinding.tvNoOfImages.setText("Images Found : " + imageCount);
            showToast("Images Extracted : " + imageCount);
            new AddImageDialog(this, optionId, this)
                    .show();
            Log.i(TAG, "extractImages Total Image Count : " + imageCount);

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void openMediaDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        MediaUploadLayoutBinding mediaUploadLayoutBinding = DataBindingUtil.inflate(
                LayoutInflater.from(this),
                R.layout.media_upload_layout, null, false);
        dialogBuilder.setView(mediaUploadLayoutBinding.getRoot());
        mediaUploadLayoutBinding.camera.setOnClickListener(view -> {
            mediaDialog.dismiss();
            requestStoragePermission(true, false, false,false);
        });


        mediaUploadLayoutBinding.gallery.setOnClickListener(view -> {
            mediaDialog.dismiss();
            requestStoragePermission(false, true, false,false);
        });


        mediaUploadLayoutBinding.videos.setOnClickListener(view -> {
            requestStoragePermission(false, false, false,true);
            mediaDialog.dismiss();
        });

        mediaUploadLayoutBinding.audios.setOnClickListener(view -> {
            requestStoragePermission(false, false, true,false);
            mediaDialog.dismiss();
        });

        mediaUploadLayoutBinding.scanPdf.setOnClickListener(v -> {
            mediaDialog.dismiss();
            new AddImageDialog(this, 1, this)
                    .show();
        });

        mediaUploadLayoutBinding.documents.setOnClickListener(v -> {
            mediaDialog.dismiss();
            requestStoragePermission(false, false, false,false);
        });

        mediaDialog = dialogBuilder.create();
        Objects.requireNonNull(mediaDialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        mediaDialog.show();
    }

    private void requestStoragePermission(final boolean isCamera, final boolean isPictures, final boolean isAudio,  final boolean isVideo) {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            if (isCamera) {
                                dispatchTakePictureIntent();
                            } else if (isPictures) {
                                getImages();
                            } else if (isAudio) {
                                getAudio();
                            } else  if (isVideo){
                                getVideo();
                            } else {
                                getDocument();
                            }
                        }
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions,
                                                                   PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .withErrorListener(error -> {
                    // Toast.makeText(mContext, "Error occurred! ", Toast.LENGTH_SHORT).show();
                })
                .onSameThread()
                .check();
    }
    protected void getDocument() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        String[] mimeTypes =
                {"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                        "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                        "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                        "text/plain","application/rtf","application/pdf","application/zip", "application/vnd.android.package-archive"};

        intent.setType("application/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, PICKFILE_REQUEST_CODE);
    }

    private void getVideo() {
        Intent pickPhoto = new Intent();
        pickPhoto.setType("video/*");
        pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pickPhoto.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        pickPhoto.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(pickPhoto, VIDEO_REQUEST);
    }

    private void getAudio() {
        Intent pickAudio = new Intent();
        pickAudio.setType("audio/*");
        pickAudio.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pickAudio.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        pickAudio.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(pickAudio, AUDIO_REQUEST);
    }

    private void getImages() {
        Intent pickPhoto = new Intent();
        pickPhoto.setType("image/*");
        pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pickPhoto.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        pickPhoto.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(pickPhoto, "Select Picture"), GALLERY_REQUEST);
    }

    private void showSettingsDialog() {
        Dialog builder = new Dialog(this);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        AlertDialogBinding alertDialogBinding = DataBindingUtil.inflate(LayoutInflater.from(this), R.layout.alert_dialog, null, false);
        builder.setContentView(alertDialogBinding.getRoot());
        alertDialogBinding.tvTitle.setText("Need Permissions");
        alertDialogBinding.tvDesc.setText("This app needs permission to use this feature. You can grant them in app settings.");
        alertDialogBinding.btnPositive.setText("GOTO SETTINGS");
        alertDialogBinding.btnPositive.setOnClickListener(view -> {
            builder.dismiss();
            openSettings();
        });
        alertDialogBinding.btnNeutral.setText("Cancel");
        alertDialogBinding.btnNeutral.setOnClickListener(view -> builder.dismiss());
        Objects.requireNonNull(builder.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        builder.show();
    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = AppUtils.createImageFile(this);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        photoFile);

                mphotouri = photoURI;
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST);
            }
        }
    }


    private void navToFragment(int resId, Bundle bundle) {
        try {
            navController.popBackStack();
            navController.navigate(resId, bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


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
                        navController.getCurrentDestination().getId() == R.id.nav_about ||
                        navController.getCurrentDestination().getId() == R.id.nav_import_contacts ||
                        navController.getCurrentDestination().getId() == R.id.nav_code_conduct ||
                        navController.getCurrentDestination().getId() == R.id.nav_faq ||
                        navController.getCurrentDestination().getId() == R.id.nav_doubts ||
                        navController.getCurrentDestination().getId() == R.id.nav_fav ||
                        navController.getCurrentDestination().getId() == R.id.nav_shared_with_you_que ||
                        navController.getCurrentDestination().getId() == R.id.nav_shared_by_you_que ||
                        navController.getCurrentDestination().getId() == R.id.saved_questions ||
                        navController.getCurrentDestination().getId() == R.id.nav_shared_by_you ||
                        navController.getCurrentDestination().getId() == R.id.nav_shared_with_you ||
                        navController.getCurrentDestination().getId() == R.id.nav_connections ||
                        navController.getCurrentDestination().getId() == R.id.nav_upload_question ||
                        navController.getCurrentDestination().getId() == R.id.nav_requests) {
                    navController.navigate(R.id.nav_home);
                } else if (navController.getCurrentDestination().getId() == R.id.nav_syllabus) {
                    if (isTestScreenEnabled)
                        navController.navigate(R.id.nav_test_my);
                    else {
                        Bundle bundle = new Bundle();
                        bundle.putString("call_from", "learning");
                        navToFragment(R.id.nav_learning, bundle);
                    }
                } else {
                    navController.popBackStack();
                }
            }
        }
    }

    private void saveTestType(String type) {
        new PreferenceManager(getApplicationContext()).saveString(CALL_FROM, type);
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
        Call<UserProfile> call = apiService.fetchUserInfo(getUserId(getApplicationContext()), getDeviceId(), Constant.APP_NAME, BuildConfig.VERSION_NAME);
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
                    AppUtils.showToast(getApplicationContext(), null, response.body().getMessage());
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                t.printStackTrace();
                apiCallFailureDialog(t);
            }
        });
    }

    private void shareAction() {
        try {
            Bitmap bitmap;
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.logo);
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

    @Override
    public void onFriendUnFriendClick() {

    }

    @Override
    public void onFollowUnfollowClick() {

    }

    @Override
    public void onViewImage(String path) {
        showFullScreen(path);
    }

    @Override
    public void onImageClickListener(ImageObject imageObject, int opt) {
        if (queMediaListener!=null)
        queMediaListener.onScanImageClick(imageObject.getUri());
    }

    @Override
    public void onScanPdfClick(int option) {
        optionId = option;
        Intent intent4 = new Intent(this, NormalFilePickActivity.class);
        intent4.putExtra(com.vincent.filepicker.Constant.MAX_NUMBER, 1);
        intent4.putExtra(NormalFilePickActivity.SUFFIX, new String[]{"pdf"});
        startActivityForResult(intent4, com.vincent.filepicker.Constant.REQUEST_CODE_PICK_FILE);
    }
}
