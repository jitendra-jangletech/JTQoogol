package com.jangletech.qoogol.ui.edit_profile;


import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;


import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.AddEducationBinding;
import com.jangletech.qoogol.databinding.FragmentEditProfileBinding;
import com.jangletech.qoogol.model.SignUp;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.util.PreferenceManager;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jangletech.qoogol.util.Constant.add_edu;
import static com.jangletech.qoogol.util.Constant.user_id;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfileFragment extends Fragment implements View.OnClickListener {

    FragmentEditProfileBinding fragmentEditProfileBinding;
    AddEducationBinding addEducationBinding;
    AlertDialog educationDialog;
    ApiInterface apiService = ApiClient.getInstance().getApi();
    private static final String TAG = "EditProfileFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentEditProfileBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_profile, container, false);
        setListeners();
        fetAccountDetails();
        return fragmentEditProfileBinding.getRoot();
    }

    private void fetAccountDetails() {
        Map<String, Integer> requestBody = new HashMap<>();
        requestBody.put(user_id, Integer.parseInt(new PreferenceManager(getActivity()).getUserId()));
        Call<SignUp> call = apiService.getAccountDetails(requestBody);
        call.enqueue(new Callback<SignUp>() {
            @Override
            public void onResponse(Call<SignUp> call, Response<SignUp> response) {

               // if(response.)
                    if (response.body().getStatusCode().equalsIgnoreCase("1")) {
                    } else {
                        Toast.makeText(getActivity(),response.body().getMessage(),Toast.LENGTH_SHORT).show();
                    }

            }

            @Override
            public void onFailure(Call<SignUp> call, Throwable t) {
                Log.i(TAG, t.toString());
            }
        });
    }

    private void setListeners() {
        fragmentEditProfileBinding.btnPersonalInfo.setOnClickListener(this);
        fragmentEditProfileBinding.btnEducationInfo.setOnClickListener(this);
        fragmentEditProfileBinding.btnAccountInfo.setOnClickListener(this);
        fragmentEditProfileBinding.EducationInfolayout.setOnClickListener(this);
        fragmentEditProfileBinding.btnPreferences.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnPersonalInfo:
                if (fragmentEditProfileBinding.personalInfoLayout.getVisibility() == View.GONE) {
                    fragmentEditProfileBinding.personalInfoLayout.setVisibility(View.VISIBLE);
//                    fragmentEditProfileBinding.btnPersonalInfo.setCompoundDrawablesWithIntrinsicBounds(null, null, getActivity().getResources().getDrawable(R.drawable.ic_minus_gray), null);
                } else {
                    fragmentEditProfileBinding.personalInfoLayout.setVisibility(View.GONE);
//                    fragmentEditProfileBinding.btnPersonalInfo.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getResources().getDrawable(R.drawable.ic_add_gray), null);
                }
                break;

            case R.id.btnEducationInfo:
                if (fragmentEditProfileBinding.educationalInfoLayout.getVisibility() == View.GONE) {
                    fragmentEditProfileBinding.educationalInfoLayout.setVisibility(View.VISIBLE);
//                    fragmentEditProfileBinding.btnEducationInfo.setCompoundDrawablesWithIntrinsicBounds(null, null, getActivity().getResources().getDrawable(R.drawable.ic_minus_gray), null);
                } else {
                    fragmentEditProfileBinding.educationalInfoLayout.setVisibility(View.GONE);
//                    fragmentEditProfileBinding.btnEducationInfo.setCompoundDrawablesWithIntrinsicBounds(null, null, mContext.getResources().getDrawable(R.drawable.ic_add_gray), null);
                }
                break;

            case R.id.EducationInfolayout:
                addEducation(add_edu);
                break;

            case R.id.btnAccountInfo:
                if (fragmentEditProfileBinding.accountInfoLayout.getVisibility() == View.GONE) {
                    fragmentEditProfileBinding.accountInfoLayout.setVisibility(View.VISIBLE);
                } else {
                    fragmentEditProfileBinding.accountInfoLayout.setVisibility(View.GONE);
                }
                break;
            case R.id.btnPreferences:
                if (fragmentEditProfileBinding.preferencesLayout.getVisibility() == View.GONE) {
                    fragmentEditProfileBinding.preferencesLayout.setVisibility(View.VISIBLE);
                } else {
                    fragmentEditProfileBinding.preferencesLayout.setVisibility(View.GONE);
                }
                break;
        }
    }

    private void addEducation(final int called_from) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        addEducationBinding = DataBindingUtil.inflate(
                LayoutInflater.from(getActivity()),
                R.layout.add_education, null,false);
        dialogBuilder.setView(addEducationBinding.getRoot());

        addEducationBinding.saveEdu.setOnClickListener(v -> {
            if (called_from==add_edu)
                saveEducation();
        });

        educationDialog = dialogBuilder.create();
        educationDialog.show();
    }

    private void saveEducation() {
        educationDialog.dismiss();
    }
}
