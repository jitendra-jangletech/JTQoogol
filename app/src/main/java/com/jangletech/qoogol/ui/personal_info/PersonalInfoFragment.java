package com.jangletech.qoogol.ui.personal_info;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.activities.SignInActivity;
import com.jangletech.qoogol.databinding.FragmentPersonalInfoBinding;
import com.jangletech.qoogol.databinding.LayoutChangePasswordBinding;
import com.jangletech.qoogol.dialog.ChangePasswordDialog;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.model.ChangePassword;
import com.jangletech.qoogol.model.City;
import com.jangletech.qoogol.model.CityObject;
import com.jangletech.qoogol.model.Country;
import com.jangletech.qoogol.model.GetUserPersonalDetails;
import com.jangletech.qoogol.model.State;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;
import com.jangletech.qoogol.util.UtilHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.facebook.FacebookSdk.getApplicationContext;

public class PersonalInfoFragment extends BaseFragment implements ChangePasswordDialog.ChangeDialogClickListener {

    private static final String TAG = "PersonalInfoFragment";
    private PersonalInfoViewModel mViewModel;
    private FragmentPersonalInfoBinding mBinding;
    Dialog changePasswordDialog;
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
        fetchPersonalDetails();
        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated PersonalInfoFragment : ");

        fetchCountryData();
        setClickListeners();

        mBinding.tvMobileVerify.setOnClickListener(v -> {
            //Todo Mobile Verify Dialog
        });

        mBinding.tvEmailVerify.setOnClickListener(v -> {
            //Todo Email Verify Dialog
        });

        mBinding.changePassword.setOnClickListener(v -> {
            showChangePasswordDialog();
        });

        mBinding.etDob.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction()) {
                    Calendar newCalendar = Calendar.getInstance();
                        DatePickerDialog dialog = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Dialog, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                            mBinding.etDob.setText("" + year + "-" + (month + 1) + "-" + day);
                        }
                    }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    dialog.show();
                }

                return true;
            }
        });

        mBinding.btnSave.setOnClickListener(v -> {
            validateForm();
        });

        /*mBinding.etDob.setOnClickListener(v->{
            Calendar newCalendar = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Dialog, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                    mBinding.etDob.setText(""+year);
                }
            },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.show();

        });*/
    }

    private void showChangePasswordDialog() {
        changePasswordDialog = new Dialog(getActivity(), android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        LayoutChangePasswordBinding layoutChangePasswordBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.layout_change_password, null, false);
        changePasswordDialog.setContentView(layoutChangePasswordBinding.getRoot());
        changePasswordDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
        changePasswordDialog.show();
        layoutChangePasswordBinding.btnClose.setOnClickListener(v -> {
            changePasswordDialog.dismiss();
        });

        layoutChangePasswordBinding.btnSubmit.setOnClickListener(v -> {

            clearErrors(layoutChangePasswordBinding.rootLayout);
           /* layoutChangePasswordBinding.tilOldPassword.setError(null);
            layoutChangePasswordBinding.tilNewPassword.setError(null);
            layoutChangePasswordBinding.tilConfirmNewPassword.setError(null);*/

            if (layoutChangePasswordBinding.tilOldPassword.getEditText().getText().toString().trim().isEmpty()) {
                layoutChangePasswordBinding.tilOldPassword.setError("Enter current password");
                return;
            }

            if (layoutChangePasswordBinding.tilNewPassword.getEditText().getText().toString().trim().isEmpty()) {
                layoutChangePasswordBinding.tilNewPassword.setError("Enter new password");
                return;
            }

            if (layoutChangePasswordBinding.tilConfirmNewPassword.getEditText().getText().toString().trim().isEmpty()) {
                layoutChangePasswordBinding.tilConfirmNewPassword.setError("Enter confirm new password");
                return;
            }

            if (!(layoutChangePasswordBinding.tilNewPassword.getEditText().getText().toString().trim()
                    .equals(layoutChangePasswordBinding.tilConfirmNewPassword.getEditText().getText().toString().trim()))) {
                layoutChangePasswordBinding.tilConfirmNewPassword.setError("New Password & Confirm Password Not Matched.");
                return;
            }

            if (!hasError(layoutChangePasswordBinding.rootLayout)) {
                changePassword(layoutChangePasswordBinding.tilOldPassword.getEditText().getText().toString().trim(),
                        layoutChangePasswordBinding.tilNewPassword.getEditText().getText().toString().trim());
            }
        });
    }


    public void fetchPersonalDetails() {
        try {
            ProgressDialog.getInstance().show(getActivity());
            Map<String, String> arguments = new HashMap<>();
            arguments.put(Constant.user_id, new PreferenceManager(getContext()).getUserId());

            Call<GetUserPersonalDetails> call = apiService.getPersonalDetails(arguments);
            call.enqueue(new Callback<GetUserPersonalDetails>() {
                @Override
                public void onResponse(Call<GetUserPersonalDetails> call, Response<GetUserPersonalDetails> response) {
                    ProgressDialog.getInstance().dismiss();
                    if (response.body() != null && response.body().getObject() != null
                            && response.body().getStatusCode().equalsIgnoreCase("1")) {
                        GetUserPersonalDetails getUserPersonalDetails = (GetUserPersonalDetails) response.body();
                        updatePersonalDetailsUi(getUserPersonalDetails);
                    } else {
                        Toast.makeText(getActivity(), "Something went wrong.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<GetUserPersonalDetails> call, Throwable t) {
                    t.printStackTrace();
                    ProgressDialog.getInstance().dismiss();
                }
            });
        } catch (Exception e) {
            ProgressDialog.getInstance().dismiss();
            e.printStackTrace();
        }
    }

    public void updatePersonalDetailsUi(GetUserPersonalDetails personalDetails) {
        Log.d(TAG, "updatePersonalDetailsUi: " + personalDetails);
        mBinding.countryAutocompleteView.setTag(personalDetails.getObject().getCountryId());
        mBinding.stateAutocompleteView.setTag(personalDetails.getObject().getStateId());
        mBinding.cityAutocompleteView.setTag(personalDetails.getObject().getCityId());
        mBinding.etMobile.setText("" + personalDetails.getObject().getMobileNo1());
        mBinding.etFirstName.setText(personalDetails.getObject().getFirstName());
        mBinding.etLastName.setText(personalDetails.getObject().getLastName());
        mBinding.etEmail.setText(personalDetails.getObject().getEmail());
        mBinding.etDob.setText(personalDetails.getObject().getDobString());

        if (personalDetails.getObject().getGender() != null &&
                personalDetails.getObject().getGender().equalsIgnoreCase("M")) {
            mBinding.radioBtnMale.setChecked(true);
        } else if (personalDetails.getObject().getGender() != null
                && personalDetails.getObject().getGender().equalsIgnoreCase("F")) {
            mBinding.radioBtnFemale.setChecked(true);
        }

        mBinding.countryAutocompleteView.setText(personalDetails.getObject().getCountryName());
        mBinding.stateAutocompleteView.setText(personalDetails.getObject().getStateName());
        mBinding.cityAutocompleteView.setText(personalDetails.getObject().getCityName());

        if (personalDetails.getObject().getMobile1Verified()) {
            mBinding.tvMobileVerify.setText("Verified");
            mBinding.tvMobileVerify.setEnabled(false);
            mBinding.tvMobileVerify.setTextColor(getResources().getColor(R.color.color_green));
        }

        if (personalDetails.getObject().getEmailVerified()) {
            mBinding.tvEmailVerify.setText("Verified");
            mBinding.tvEmailVerify.setEnabled(false);
            mBinding.tvEmailVerify.setTextColor(getResources().getColor(R.color.color_green));
        }
        // disableLayoutFields();

        ProgressDialog.getInstance().dismiss();
    }

   /* public void disableLayoutFields() {
        mBinding.etFirstName.setClickable(false);
        mBinding.etLastName.setEnabled(false);
        mBinding.etDob.setEnabled(false);
        mBinding.genderRadioGrp.setEnabled(false);
        mBinding.stateAutocompleteView.setEnabled(false);
        mBinding.countryAutocompleteView.setEnabled(false);
        mBinding.cityAutocompleteView.setEnabled(false);
    }*/

    @Override
    public void onSubmitClick(String oldPwd, String newPwd) {
        changePassword(oldPwd, newPwd);
    }

    public void changePassword(String oldPwd, String newPwd) {
        ProgressDialog.getInstance().show(getActivity());
        Map<String, String> arguments = new HashMap<>();
        arguments.put("userId", new PreferenceManager(getApplicationContext()).getUserId());
        arguments.put("oldPassword", oldPwd);
        arguments.put("newPassword", newPwd);

        Call<ChangePassword> call = apiService.changePassword(arguments);
        call.enqueue(new Callback<ChangePassword>() {
            @Override
            public void onResponse(Call<ChangePassword> call, Response<ChangePassword> response) {
                if (response.body() != null
                        && response.body().getStatusCode() == 1) {
                    ProgressDialog.getInstance().dismiss();
                    if (changePasswordDialog != null) {
                        changePasswordDialog.dismiss();
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle);
                    builder.setTitle("Success");
                    builder.setMessage("Password Changed Successfully, Please Sign-In With New Password.");
                    builder.setCancelable(false);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new PreferenceManager(getApplicationContext()).setIsLoggedIn(false);
                            Intent i = new Intent(getActivity(), SignInActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                        }
                    }).show();

                } else if (response.body().getStatusCode() == 0) {
                    ProgressDialog.getInstance().dismiss();
                    Toast.makeText(getActivity(), "" + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ChangePassword> call, Throwable t) {
                t.printStackTrace();
                ProgressDialog.getInstance().dismiss();
            }
        });
    }


    private void updatePersonalDetails() {
        ProgressDialog.getInstance().show(getActivity());
        Map<String, String> arguments = new HashMap<>();
        arguments.put("firstName", mBinding.tilFirstName.getEditText().getText().toString());
        arguments.put("lastName", mBinding.tilLastName.getEditText().getText().toString());
        arguments.put("gender", mBinding.radioBtnFemale.isChecked() ? "F" : "M");
        arguments.put("dob", mBinding.tilDob.getEditText().getText().toString());
        arguments.put("stateId", mBinding.stateAutocompleteView.getTag().toString());
        arguments.put("countryId", mBinding.countryAutocompleteView.getTag().toString());
        arguments.put("cityId", mBinding.cityAutocompleteView.getTag().toString());
        arguments.put("cityName", mBinding.cityAutocompleteView.getText().toString());
        arguments.put("stateName", mBinding.stateAutocompleteView.getText().toString());
        arguments.put("countryName", mBinding.countryAutocompleteView.getText().toString());
        arguments.put("userId", new PreferenceManager(getActivity()).getUserId());

        Call<GetUserPersonalDetails> call = apiService.updatePersonalDetails(arguments);
        call.enqueue(new Callback<GetUserPersonalDetails>() {
            @Override
            public void onResponse(Call<GetUserPersonalDetails> call, Response<GetUserPersonalDetails> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.body().getStatusCode().equalsIgnoreCase("1")) {
                    GetUserPersonalDetails getUserPersonalDetails = (GetUserPersonalDetails) response.body();
                    updatePersonalDetailsUi(getUserPersonalDetails);
                } else {
                    Toast.makeText(getActivity(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetUserPersonalDetails> call, Throwable t) {
                t.printStackTrace();
                ProgressDialog.getInstance().dismiss();
            }
        });

    }

    private void fetchCountryData() {
        ProgressDialog.getInstance().show(getActivity());
        Call<List<Country>> call = apiService.getCountries();
        call.enqueue(new Callback<List<Country>>() {
            @Override
            public void onResponse(Call<List<Country>> call, retrofit2.Response<List<Country>> response) {
                try {
                    ProgressDialog.getInstance().dismiss();
                    List<Country> list = response.body();
                    mViewModel.setCountryList(list);
                    if (list != null && list.size() > 0) {
                        mViewModel.mMapCountry = new HashMap<>();
                        for (Country country : list) {
                            mViewModel.mMapCountry.put(country.getCountryId(), country.getCountryName());
                        }
                        populateCountries(mViewModel.mMapCountry);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ProgressDialog.getInstance().dismiss();
                }
            }

            @Override
            public void onFailure(Call<List<Country>> call, Throwable t) {
                t.printStackTrace();
                ProgressDialog.getInstance().dismiss();
            }
        });
    }

    public void fetchStateData(int countryId) {
        ProgressDialog.getInstance().show(getActivity());
        Map<String, Integer> requestBody = new HashMap<>();
        requestBody.put("countryId", countryId);
        Call<List<State>> call = apiService.getStates(requestBody);
        call.enqueue(new Callback<List<State>>() {
            @Override
            public void onResponse(Call<List<State>> call, retrofit2.Response<List<State>> response) {
                try {
                    ProgressDialog.getInstance().dismiss();
                    List<State> list = response.body();
                    mViewModel.setStateList(list);
                    if (list != null && list.size() > 0) {
                        mViewModel.mMapState = new HashMap<>();
                        for (State state : list) {
                            mViewModel.mMapState.put(Integer.valueOf(state.getStateId()), state.getStateName());
                        }

                        populateStates(mViewModel.mMapState);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ProgressDialog.getInstance().dismiss();
                }
            }

            @Override
            public void onFailure(Call<List<State>> call, Throwable t) {
                t.printStackTrace();
                ProgressDialog.getInstance().dismiss();
            }
        });
    }

    private void populateCountries(Map<Integer, String> mMapCountry) {
        List<String> list = new ArrayList<>(mMapCountry.values());
        Collections.sort(list);
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.autocomplete_list_item, list);
        mBinding.countryAutocompleteView.setAdapter(countryAdapter);
    }

    private void populateStates(Map<Integer, String> mMapState) {
        List<String> list = new ArrayList<>(mMapState.values());
        Collections.sort(list);
        ArrayAdapter<String> stateAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.autocomplete_list_item, list);
        mBinding.stateAutocompleteView.setAdapter(stateAdapter);
    }

    private void setClickListeners() {
        mBinding.countryAutocompleteView.setOnItemClickListener((parent, view, position, id) -> {
            final String name = ((TextView) view).getText().toString();
            int key = UtilHelper.getKeyFromValue(mViewModel.mMapCountry, name);
            if (key != -1) {
                mBinding.countryAutocompleteView.setTag(key);
                fetchStateData(key);
            }
        });

        mBinding.stateAutocompleteView.setOnItemClickListener((parent, view, position, id) -> {
            final String name = ((TextView) view).getText().toString();
            int key = UtilHelper.getKeyFromValue(mViewModel.mMapState, name);
            if (key != -1) {
                mBinding.stateAutocompleteView.setTag(key);
                fetchCityData(key);
            }
        });

        mBinding.cityAutocompleteView.setOnItemClickListener((parent, view, position, id) -> {
            final String name = ((TextView) view).getText().toString();
            int key = UtilHelper.getKeyFromValue(mViewModel.mMapCity, name);
            if (key != -1) {
                mBinding.cityAutocompleteView.setTag(key);
            }
        });

    }

    private void fetchCityData(int key) {
        ProgressDialog.getInstance().show(getActivity());
        Map<String, Integer> requestBody = new HashMap<>();
        requestBody.put("stateId", key);
        Call<City> call = apiService.getCities(requestBody);
        call.enqueue(new Callback<City>() {
            @Override
            public void onResponse(Call<City> call, retrofit2.Response<City> response) {
                try {
                    ProgressDialog.getInstance().dismiss();
                    List<CityObject> list = response.body().getObject();
                    mViewModel.setCityList(list);
                    if (list != null && list.size() > 0) {
                        mViewModel.mMapCity = new HashMap<>();
                        for (CityObject cityObject : list) {
                            mViewModel.mMapCity.put(Integer.valueOf(cityObject.getCityId()), cityObject.getCityName());
                        }

                        populateCities(mViewModel.mMapCity);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ProgressDialog.getInstance().dismiss();
                }
            }

            @Override
            public void onFailure(Call<City> call, Throwable t) {
                t.printStackTrace();
                ProgressDialog.getInstance().dismiss();
            }
        });
    }

    private void populateCities(Map<Integer, String> mMapCity) {
        List<String> list = new ArrayList<>(mMapCity.values());
        Collections.sort(list);
        ArrayAdapter<String> stateAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.autocomplete_list_item, list);
        mBinding.cityAutocompleteView.setAdapter(stateAdapter);
    }

    private void validateForm() {

        clearErrors(mBinding.rootLayout);

        if (mBinding.tilFirstName.getEditText().getText().toString().trim().isEmpty()) {
            mBinding.tilFirstName.setError("Please enter first name");
            return;
        }

        if (mBinding.tilLastName.getEditText().getText().toString().trim().isEmpty()) {
            mBinding.tilLastName.setError("Please enter last name");
            return;
        }

        if (mBinding.tilMobile.getEditText().getText().toString().trim().isEmpty()) {
            mBinding.tilMobile.setError("Please enter mobile");
            return;
        }

        if (mBinding.tilEmail.getEditText().getText().toString().trim().isEmpty()) {
            mBinding.tilEmail.setError("Please enter email");
            return;
        }

        if (mBinding.tilDob.getEditText().getText().toString().trim().isEmpty()) {
            mBinding.tilDob.setError("Please enter date of birth");
            return;
        }

        if (mBinding.genderRadioGrp.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getActivity(), "Please select gender", Toast.LENGTH_LONG).show();
            return;
        }

        if (mBinding.countryAutocompleteView.getTag().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Please select country", Toast.LENGTH_LONG).show();
            return;
        }

        if (mBinding.stateAutocompleteView.getTag().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Please select state", Toast.LENGTH_LONG).show();
            return;
        }

        if (mBinding.cityAutocompleteView.getTag().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Please select city", Toast.LENGTH_LONG).show();
            return;
        }

        updatePersonalDetails();
    }

}
