package com.jangletech.qoogol.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;

import androidx.appcompat.widget.AppCompatAutoCompleteTextView;


/////////////////////////////////////////////////////////////////////////////////////////////////
//
//            Copyright 2019 JANGLETECH SYSTEMS PRIVATE LIMITED. All rights reserved.
//
/////////////////////////////////////////////////////////////////////////////////////////////////

public class CustomAutoCompleteTextView extends AppCompatAutoCompleteTextView {

    private int mThreshold;

    public CustomAutoCompleteTextView(Context context) {
        super(context);
    }

    public CustomAutoCompleteTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public CustomAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction,
                                  Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        try {
            if (focused && getFilter() != null) {
                performFiltering(getText(), 0);
                showDropDown();
            }
        } catch (Exception ex) {
            Log.e("CoustomView", ex.getMessage());
        }
    }

    @Override
    public boolean enoughToFilter() {
        return true;
    }

    @Override
    public int getThreshold() {
        return mThreshold;
    }

    @Override
    public void setThreshold(int threshold) {
        if (threshold <= 0) {
            threshold = 0;
        }
        mThreshold = threshold;
    }
}
