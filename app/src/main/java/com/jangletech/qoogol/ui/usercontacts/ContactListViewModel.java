package com.jangletech.qoogol.ui.usercontacts;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jangletech.qoogol.model.Contacts;
import com.jangletech.qoogol.util.PreferenceManager;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jangletech.qoogol.ui.BaseFragment.getDeviceId;

/*
 *
 *
 *  * /////////////////////////////////////////////////////////////////////////////////////////////////
 *  * //
 *  * //            Copyright (c) 2020. JangleTech Systems Private Limited, Thane, India
 *  * //
 *  * /////////////////////////////////////////////////////////////////////////////////////////////////
 *
 */

public class ContactListViewModel extends AndroidViewModel {

    private static final String TAG = "ContactListViewModel";
    int firstIndex = 0;
    int lastIndex;
    List<Contacts> contactsList;
    List<Contacts> filteredList;
    String pageFetch = "0";
    String previousFetch = "0";
    private MutableLiveData<List<Contacts>> contactListLiveData;
    private PreferenceManager mSettings;

    public ContactListViewModel(@NonNull Application application) {
        super(application);
        contactListLiveData = new MutableLiveData<>();
        contactsList = new ArrayList<>();
        filteredList = new ArrayList<>();
        mSettings = new PreferenceManager(application);
        lastIndex = 100;
    }

    void setContactListLiveData() {
        contactListLiveData.setValue(contactsList);
    }



}
