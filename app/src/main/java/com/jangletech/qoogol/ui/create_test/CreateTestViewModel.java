package com.jangletech.qoogol.ui.create_test;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.jangletech.qoogol.model.TestListResponse;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.util.AppUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateTestViewModel extends AndroidViewModel {

    private static final String TAG = "CreateTestViewModel";
    private MutableLiveData<TestListResponse> testResponse;
    private Application application;

    public CreateTestViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        testResponse = new MutableLiveData<>();
    }

    public void setTestResponse(TestListResponse response) {
        this.testResponse.setValue(response);
    }

    public LiveData<TestListResponse> getTestListResponse() {
        return testResponse;
    }

    public void fetchTestList() {
        Call<TestListResponse> call = ApiClient.getInstance().getApi().fetchCreatedTestList(AppUtils.getUserId());
        call.enqueue(new Callback<TestListResponse>() {
            @Override
            public void onResponse(Call<TestListResponse> call, Response<TestListResponse> response) {
                //ProgressDialog.getInstance().dismiss();
                if (response.body().getResponse() != null) {
                    if (response.body().getResponse().equals("200")) {
                        //setCreatedTestList(response.body().getTestList());
                        setTestResponse(response.body());
                    } else if (response.body().getResponse().equals("501")) {
                        //resetSettingAndLogout();
                    } else {
                        //showErrorDialog(getActivity(), response.body().getResponse(), response.body().getMessage());
                    }
                } else {
                    Log.e(TAG, "Null Response: ");
                    setTestResponse(response.body());
                }
            }

            @Override
            public void onFailure(Call<TestListResponse> call, Throwable t) {
                //ProgressDialog.getInstance().dismiss();
                //AppUtils.showToast(getActivity(), t, "");
                //apiCallFailureDialog(t);
                t.printStackTrace();
            }
        });
    }
}
