package com.jangletech.qoogol.dialog;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class CommentViewModelNew extends AndroidViewModel {

    private MutableLiveData<Integer> count;

    public CommentViewModelNew(@NonNull Application application) {
        super(application);
        count = new MutableLiveData<>();
    }

}
