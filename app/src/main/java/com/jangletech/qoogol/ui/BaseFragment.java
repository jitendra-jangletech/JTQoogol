package com.jangletech.qoogol.ui;

import android.util.Log;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputLayout;

public class BaseFragment extends Fragment {

    private static final String TAG = "BaseFragment";

    public static boolean hasError(ViewGroup viewGroup) {
        boolean result = false;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            if (viewGroup.getChildAt(i) instanceof TextInputLayout) {
                if (((TextInputLayout) viewGroup.getChildAt(i)).getError() != null) {
                    result = true;
                }
            }
        }
        Log.d(TAG, "hasError: " + result);
        return result;
    }

    public void clearErrors(ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            if (viewGroup.getChildAt(i) instanceof TextInputLayout) {
                if (((TextInputLayout) viewGroup.getChildAt(i)).getError() == null) {
                    ((TextInputLayout) viewGroup.getChildAt(i)).setError(null);
                }
            }
        }
    }
}
