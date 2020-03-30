package com.jangletech.qoogol.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.ActivitySingleSelectionBinding;
import com.jangletech.qoogol.ui.syllabus.ClassFragment;


public class SingleSelectionActivity extends AppCompatActivity {

    private ActivitySingleSelectionBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_single_selection);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragmentContainer, new ClassFragment());
        ft.commit();
    }


}
