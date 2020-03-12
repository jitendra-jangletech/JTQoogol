package com.jangletech.qoogol.ui.personal_info;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;

import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.model.GetUserPersonalDetails;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PersonalInfoRepository {
    private static final String TAG = "PersonalInfoRepository";

    private Application application;

    public PersonalInfoRepository(Application application) {
        this.application = application;
    }

    public MutableLiveData<GetUserPersonalDetails> getMutableLiveData() {
        MutableLiveData<GetUserPersonalDetails> personalDetailsMutableLiveData = new MutableLiveData<>();
        ApiInterface apiService = ApiClient.getInstance().getApi();

        Map<String, String> arguments = new HashMap<>();
        arguments.put(Constant.user_id, new PreferenceManager(application.getApplicationContext()).getUserId());

        Call<GetUserPersonalDetails> call = apiService.getPersonalDetails(arguments);
        call.enqueue(new Callback<GetUserPersonalDetails>() {
            @Override
            public void onResponse(Call<GetUserPersonalDetails> call, Response<GetUserPersonalDetails> response) {
                if (response.body() != null && response.body().getStatusCode().equalsIgnoreCase("1")) {
                    if (response.body().getObject() != null) {
                        GetUserPersonalDetails getUserPersonalDetails = (GetUserPersonalDetails) response.body();
                        personalDetailsMutableLiveData.setValue(getUserPersonalDetails);
                    }
                }
            }

            @Override
            public void onFailure(Call<GetUserPersonalDetails> call, Throwable t) {
                t.printStackTrace();
                //ProgressDialog.getInstance().dismiss();
            }
        });

        return personalDetailsMutableLiveData;

    }


}
