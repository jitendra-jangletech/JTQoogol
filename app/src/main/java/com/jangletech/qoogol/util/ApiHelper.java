package com.jangletech.qoogol.util;

import android.app.Activity;

import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;

import java.util.List;

public class ApiHelper {
    private Activity mActivity;
    private ApiCallListener apiCallListener;
    private ApiInterface apiService = ApiClient.getInstance().getApi();

    public ApiHelper(Activity mActivity, ApiCallListener apiCallListener){
        this.mActivity = mActivity;
        this.apiCallListener= apiCallListener;
    }

  /*  private void fetchEducationDetails() {
        ProgressDialog.getInstance().show(mActivity);
        Call<T> call = apiService.fetchUserEdu(new PreferenceManager(mActivity).getInt(Constant.USER_ID),
                "L", getDeviceId(), Constant.APP_NAME);
        call.enqueue(new Callback<FetchEducationResponse>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.body() != null){
                    apiCallListener.onSuccess(response.body());
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                onFailure();
            }
        });
    }*/

    public interface ApiCallListener{
        void onSuccess(List<Object> objectList);
        void onError(Throwable t);
    }
}
