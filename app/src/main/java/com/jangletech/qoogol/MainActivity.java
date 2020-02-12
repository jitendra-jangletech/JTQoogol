package com.jangletech.qoogol;

import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jangletech.qoogol.databinding.ActivityMainBinding;

public class MainActivity extends BaseActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();


        //DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.edit_profile,
                R.id.nav_home, R.id.nav_course, R.id.nav_exam,
                R.id.nav_practice_test,R.id.nav_test_my,R.id.nav_test_popular,R.id.nav_attended_by_friends,
                R.id.nav_shared_with_you,R.id.nav_shared_by_you,R.id.nav_create_test,
                R.id.nav_quest_trending,R.id.nav_quest_popular,R.id.nav_quest_recent,R.id.nav_saved_drafts,
                R.id.nav_reviews, R.id.nav_published,R.id.nav_notifications,R.id.nav_settings)
                .setDrawerLayout(mBinding.drawerLayout)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


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

        findViewById(R.id.nav_course).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.nav_course) {
                navController.popBackStack();
                navController.navigate(R.id.nav_course);
            }
        });

        findViewById(R.id.nav_exam).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.nav_exam) {
                navController.popBackStack();
                navController.navigate(R.id.nav_exam);
            }
        });

        findViewById(R.id.nav_practice_test).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.nav_practice_test) {
                navController.popBackStack();
                navController.navigate(R.id.nav_practice_test);
            }
        });

        findViewById(R.id.nav_test_my).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.nav_test_my) {
                navController.popBackStack();
                navController.navigate(R.id.nav_test_my);
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
            if (navController.getCurrentDestination().getId() != R.id.nav_shared_with_you){
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

        findViewById(R.id.nav_create_test).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.nav_create_test) {
                navController.popBackStack();
                navController.navigate(R.id.nav_create_test);
            }
        });

        findViewById(R.id.nav_quest_trending).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.nav_quest_trending) {
                navController.popBackStack();
                navController.navigate(R.id.nav_quest_trending);
            }
        });

        findViewById(R.id.nav_quest_popular).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.nav_quest_popular) {
                navController.popBackStack();
                navController.navigate(R.id.nav_quest_popular);
            }
        });

        findViewById(R.id.nav_quest_recent).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();

            if (navController.getCurrentDestination().getId() != R.id.nav_quest_recent) {
                navController.popBackStack();
                navController.navigate(R.id.nav_quest_recent);
            }
        });

        findViewById(R.id.nav_saved_drafts).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.nav_saved_drafts) {
                navController.popBackStack();
                navController.navigate(R.id.nav_saved_drafts);
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

        findViewById(R.id.edit_profile).setOnClickListener(v -> {
            mBinding.drawerLayout.closeDrawers();
            if (navController.getCurrentDestination().getId() != R.id.nav_edit_profile) {
                navController.popBackStack();
                navController.navigate(R.id.nav_edit_profile);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_search, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

}
