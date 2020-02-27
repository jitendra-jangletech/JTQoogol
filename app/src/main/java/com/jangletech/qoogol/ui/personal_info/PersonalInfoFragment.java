package com.jangletech.qoogol.ui.personal_info;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.FragmentPersonalInfoBinding;
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

public class PersonalInfoFragment extends Fragment {

    private static final String TAG = "PersonalInfoFragment";
    private PersonalInfoViewModel mViewModel;
    private FragmentPersonalInfoBinding mBinding;
    ApiInterface apiService = ApiClient.getInstance().getApi();

    public static PersonalInfoFragment newInstance() {
        return new PersonalInfoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel =
                ViewModelProviders.of(this).get(PersonalInfoViewModel.class);
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_personal_info, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated PersonalInfoFragment : ");
        fetchPersonalDetails();
        mViewModel = ViewModelProviders.of(this).get(PersonalInfoViewModel.class);


        mBinding.changePassword.setOnClickListener(v->{
            //Todo Change Password Dialog
        });

        mBinding.tvMobileVerify.setOnClickListener(v->{
            //Todo Mobile Verify Dialog
        });

        mBinding.tvEmailVerify.setOnClickListener(v->{
            //Todo Email Verify Dialog
        });


    }

    public void fetchPersonalDetails(){
        ProgressDialog.getInstance().show(getActivity());
        Map<String, String> arguments = new HashMap<>();
        arguments.put(Constant.user_id,new PreferenceManager(getContext()).getUserId());

        Call<GetUserPersonalDetails> call = apiService.getPersonalDetails(arguments);
        call.enqueue(new Callback<GetUserPersonalDetails>() {
            @Override
            public void onResponse(Call<GetUserPersonalDetails> call, Response<GetUserPersonalDetails> response) {
                if(response.isSuccessful()){
                    GetUserPersonalDetails getUserPersonalDetails = (GetUserPersonalDetails)response.body();
                    updatePersonalDetailsUi(getUserPersonalDetails);
                }else{
                    Log.e(TAG, "onResponse Failed : ");
                    ProgressDialog.getInstance().dismiss();
                }
                Log.d(TAG, "onResponse: "+response.body());
            }

            @Override
            public void onFailure(Call<GetUserPersonalDetails> call, Throwable t) {
                t.printStackTrace();
                ProgressDialog.getInstance().dismiss();
            }
        });
    }

    public void updatePersonalDetailsUi(GetUserPersonalDetails personalDetails){

        mBinding.etFirstName.setText(personalDetails.getObject().getFirstName());
        mBinding.etLastName.setText(personalDetails.getObject().getLastName());


        mBinding.etEmail.setText(personalDetails.getObject().getEmail());
        mBinding.etDob.setText(personalDetails.getObject().getDobString());

//        Log.d(TAG, "updatePersonalDetailsUi Gender : "+personalDetails.getObject().getGender());
        if(personalDetails.getObject().getGender()!=null &&
                personalDetails.getObject().getGender().equalsIgnoreCase("M")){
            mBinding.radioBtnMale.setChecked(true);
        }else if(personalDetails.getObject().getGender()!=null
                && personalDetails.getObject().getGender().equalsIgnoreCase("F")){
            mBinding.radioBtnFemale.setChecked(true);
        }

        mBinding.countryAutocompleteView.setText(personalDetails.getObject().getCountryName());
        mBinding.stateAutocompleteView.setText(personalDetails.getObject().getStateName());
        mBinding.cityAutocompleteView.setText(personalDetails.getObject().getCityName());

        ProgressDialog.getInstance().dismiss();

    }
}
