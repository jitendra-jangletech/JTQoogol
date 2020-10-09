package com.jangletech.qoogol.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.jangletech.qoogol.R;

public class CreateTestActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_test);
        navController = Navigation.findNavController(CreateTestActivity.this, R.id.nav_host_create_test);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_create)
                .build();
        NavigationUI.setupActionBarWithNavController(CreateTestActivity.this, navController);

    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}