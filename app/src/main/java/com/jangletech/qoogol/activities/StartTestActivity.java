package com.jangletech.qoogol.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.jangletech.qoogol.R;

public class StartTestActivity extends AppCompatActivity {

    ViewPager viewPager;
    TabLayout resulttabs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_test);
    }
}
