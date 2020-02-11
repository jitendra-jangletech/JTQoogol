package com.jangletech.qoogol.ui.published;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.jangletech.qoogol.R;

public class PublishedFragment extends Fragment {

    private PublishedViewModel publishedViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        publishedViewModel =
                ViewModelProviders.of(this).get(PublishedViewModel.class);
        View root = inflater.inflate(R.layout.fragment_published, container, false);
        publishedViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {

            }
        });
        return root;
    }
}