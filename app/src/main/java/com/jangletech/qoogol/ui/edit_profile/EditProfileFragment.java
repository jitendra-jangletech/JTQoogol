package com.jangletech.qoogol.ui.edit_profile;


import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;


import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.AddEducationBinding;
import com.jangletech.qoogol.databinding.FragmentEditProfileBinding;
import com.jangletech.qoogol.model.Profile;
import com.jangletech.qoogol.model.ProfileData;
import com.jangletech.qoogol.model.SignUp;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.util.PreferenceManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

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
    ApiInterface apiService;
    private static final String TAG = "EditProfileFragment";
    private ProfileViewModel mViewModel;
    private Calendar mCalendar;
    DatePickerDialog.OnDateSetListener date;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentEditProfileBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_profile, container, false);
        initView();
        setListeners();
        fetchAccountDetails();
        fetchPersonalDetails();
        return fragmentEditProfileBinding.getRoot();
    }

    private void initView() {
        mViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        apiService = ApiClient.getInstance().getApi();
        mCalendar = Calendar.getInstance();


        date = (view, year, monthOfYear, dayOfMonth) -> {
            mCalendar.set(Calendar.YEAR, year);
            mCalendar.set(Calendar.MONTH, monthOfYear);
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            EditProfileFragment.this.updateLabel();
        };
    }

    private void updateLabel() {
        String myFormat = "dd-MMM-yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
        fragmentEditProfileBinding.birthdate.getEditText().setText(sdf.format(mCalendar.getTime()));
    }

    private void fetchAccountDetails() {
        Map<String, Integer> requestBody = new HashMap<>();
        requestBody.put(user_id,Integer.parseInt(new PreferenceManager(getActivity()).getUserId()));
        Call<Profile> call = apiService.getAccountDetails(requestBody);
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                try {
                    if (response.body().getStatusCode().equalsIgnoreCase("1")) {
                    } else {
                        Toast.makeText(getActivity(),response.body().getMessage(),Toast.LENGTH_SHORT).show();
                    }
                } catch(Exception e) {
                    Log.e(TAG,e.toString());
                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                Log.i(TAG, t.toString());
            }
        });
    }


    private void fetchPersonalDetails() {
        Map<String, Integer> requestBody = new HashMap<>();
        requestBody.put(user_id,Integer.parseInt(new PreferenceManager(getActivity()).getUserId()));
        Call<Profile> call = apiService.getPersonalDetails(requestBody);
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                try {
                    if (response.body().getStatusCode().equalsIgnoreCase("1")) {
                        mViewModel.mpersonalData = new ProfileData();
                        if (response.body().getObject()!=null)
                            mViewModel.mpersonalData = response.body().getObject();
                            setPersonalDetails(mViewModel.mpersonalData);
                    } else {
                        Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }catch(Exception e) {
                    Log.e(TAG,e.toString());
                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                Log.i(TAG, t.toString());
            }
        });
    }

    private void setPersonalDetails(ProfileData mpersonalData) {
        fragmentEditProfileBinding.etFirstName.setText(mpersonalData.getFirstName());
        fragmentEditProfileBinding.etLastName.setText(mpersonalData.getLastName());
        fragmentEditProfileBinding.genderAutocompleteView.setText(mpersonalData.getGender());
        fragmentEditProfileBinding.birthdate.getEditText().setText(mpersonalData.getDobString());
        fragmentEditProfileBinding.countryAutocompleteTextview.setText(mpersonalData.getCountryName());
        fragmentEditProfileBinding.stateAutocompleteTextview.setText(mpersonalData.getStateName());
        fragmentEditProfileBinding.cityAutocompleteTextview.setText(mpersonalData.getCityName());
    }

    private void fetchEducationalDetails() {
        Map<String, Integer> requestBody = new HashMap<>();
        requestBody.put(user_id,Integer.parseInt(new PreferenceManager(getActivity()).getUserId()));
        Call<Profile> call = apiService.getEducationDetails(requestBody);
        call.enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                try {
                    if (response.body().getStatusCode().equalsIgnoreCase("1")) {
                    } else {
                        Toast.makeText(getActivity(),response.body().getMessage(),Toast.LENGTH_SHORT).show();
                    }
                } catch(Exception e) {
                    Log.e(TAG,e.toString());
                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
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
        fragmentEditProfileBinding.birthdate.setOnClickListener(this);
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
            case R.id.birthdate:
                new DatePickerDialog(Objects.requireNonNull(EditProfileFragment.this.getActivity()),
                        android.R.style.Theme_Holo_Dialog,
                        date,
                        mCalendar.get(Calendar.YEAR),
                        mCalendar.get(Calendar.MONTH),
                        mCalendar.get(Calendar.DAY_OF_MONTH)).show();
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
