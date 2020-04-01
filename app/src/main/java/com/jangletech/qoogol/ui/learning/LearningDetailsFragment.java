package com.jangletech.qoogol.ui.learning;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jangletech.qoogol.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LearningDetailsFragment extends Fragment {


    public LearningDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_learning_details, container, false);
    }

}
