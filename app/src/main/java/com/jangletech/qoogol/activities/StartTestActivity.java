package com.jangletech.qoogol.activities;

import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.ActivityStartTestBinding;
import com.jangletech.qoogol.util.EndDrawerToggle;

public class StartTestActivity extends AppCompatActivity {

    private ActivityStartTestBinding mBinding;
    Toolbar toolbar;
    ViewPager2 practiceViewPager;
    private EndDrawerToggle endDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_start_test);
        toolbar = findViewById(R.id.toolbar_new);
        practiceViewPager = findViewById(R.id.practice_viewpager);
        setupNavigationDrawer();
        setTitle("Demo Test");
        mBinding.drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
                //prepareQuestPaletList();
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        endDrawerToggle.syncState();
    }

    private void setupNavigationDrawer() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        endDrawerToggle = new EndDrawerToggle(this, mBinding.drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mBinding.drawerLayout.addDrawerListener(endDrawerToggle);
        endDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // This is required to make the drawer toggle work
        if (item.getItemId() == android.R.id.home) { // use android.R.id
            mBinding.drawerLayout.openDrawer(Gravity.RIGHT);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
