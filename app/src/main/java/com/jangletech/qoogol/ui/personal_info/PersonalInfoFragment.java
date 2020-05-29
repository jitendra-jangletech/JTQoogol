package com.jangletech.qoogol.ui.personal_info;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.activities.MainActivity;
import com.jangletech.qoogol.databinding.FragmentPersonalInfoBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.model.City;
import com.jangletech.qoogol.model.CityResponse;
import com.jangletech.qoogol.model.Country;
import com.jangletech.qoogol.model.CountryResponse;
import com.jangletech.qoogol.model.District;
import com.jangletech.qoogol.model.DistrictResponse;
import com.jangletech.qoogol.model.Language;
import com.jangletech.qoogol.model.State;
import com.jangletech.qoogol.model.StateResponse;
import com.jangletech.qoogol.model.UserProfile;
import com.jangletech.qoogol.model.UserProfileResponse;
import com.jangletech.qoogol.model.VerifyResponse;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.DateUtils;
import com.jangletech.qoogol.util.PreferenceManager;
import com.mukesh.OtpView;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Url;

import static android.app.Activity.RESULT_OK;

public class PersonalInfoFragment extends BaseFragment {

    private static final String TAG = "PersonalInfoFragment";
    private static final int RESULT_REQUEST_LOAD_IMAGE_CODE = 1001;
    private PersonalInfoViewModel mViewModel;
    private android.app.ProgressDialog mDialog;
    private FragmentPersonalInfoBinding mBinding;
    private Dialog dialog;
    private String gender = "";
    private Map<Integer, String> mMapNationality;
    private Map<Integer, String> mMapLanguage;
    private Map<Integer, String> mMapStates;
    private Map<Integer, String> mMapCities;
    private Map<Integer, String> mMapDistricts;
    private UserProfile profile;
    private boolean isMail = false;
    private boolean isMailVerified = false;
    private boolean isMobileVerified = false;
    ApiInterface apiService = ApiClient.getInstance().getApi();

    public static PersonalInfoFragment newInstance() {
        return new PersonalInfoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_personal_info, container, false);
        mViewModel = new ViewModelProvider(this).get(PersonalInfoViewModel.class);
        initViews();
        return mBinding.getRoot();
    }

    public void initViews() {
        fetchUserProfile();
        mViewModel.getUserProfileInfo().observe(requireActivity(), new Observer<UserProfile>() {
            @Override
            public void onChanged(@Nullable final UserProfile userProfile) {
                mBinding.setUserProfile(userProfile);
                Log.d(TAG, "First Name : " + userProfile.getFirstName());
                profile = userProfile;
                updateUi(userProfile);
            }
        });

        fetchLanguages();
        mViewModel.getLanguages().observe(requireActivity(), new Observer<List<Language>>() {
            @Override
            public void onChanged(@Nullable final List<Language> languages) {
                mMapLanguage = new HashMap<>();
                for (Language language : languages) {
                    mMapLanguage.put(language.getLang_id(), language.getLanguageName());
                }
                populateLanguage(mMapLanguage);
            }
        });

        fetchNationalities();
        mViewModel.getCountries().observe(requireActivity(), new Observer<List<Country>>() {
            @Override
            public void onChanged(@Nullable final List<Country> countries) {
                mMapNationality = new HashMap<>();
                for (Country country : countries) {
                    mMapNationality.put(country.getCountryId(), country.getCountryName());
                }
                populateCountries(mMapNationality);
            }
        });

        mViewModel.getStates().observe(requireActivity(), new Observer<List<State>>() {
            @Override
            public void onChanged(@Nullable final List<State> states) {
                mMapStates = new HashMap<>();
                for (State state : states) {
                    mMapStates.put(Integer.valueOf(state.getState_id()), state.getStateName());
                }
                populateState(mMapStates);
            }
        });

        mViewModel.getDistricts().observe(requireActivity(), new Observer<List<District>>() {
            @Override
            public void onChanged(@Nullable final List<District> districts) {
                mMapDistricts = new HashMap<>();
                for (District district : districts) {
                    mMapDistricts.put(Integer.valueOf(district.getDistrict_id()), district.getDistrictName());
                }
                populateDistrict(mMapDistricts);
            }
        });

        mViewModel.getCities().observe(requireActivity(), new Observer<List<City>>() {
            @Override
            public void onChanged(@Nullable final List<City> cities) {
                mMapCities = new HashMap<>();
                for (City city : cities) {
                    mMapCities.put(Integer.valueOf(city.getCity_id()), city.getCityName());
                }
                populateCity(mMapCities);
            }
        });

        mBinding.userProfilePic.setOnClickListener(v -> {
            loadImage();
        });

        mBinding.btnImportContacts.setOnClickListener(v->{
            MainActivity.navController.navigate(R.id.nav_import_contacts);
        });

        //set Mandatory Fields
        mBinding.tvFirstName.setText(Html.fromHtml(getString(R.string.first_name)));
        mBinding.tvLastName.setText(Html.fromHtml(getString(R.string.last_name)));
        mBinding.tvMobile.setText(Html.fromHtml(getString(R.string.mobile_number)));

        mBinding.etDob.setOnClickListener(v -> {
            //showToast("Cliked");
            showDatePicker();
        });

        mBinding.btnEmailVerify.setOnClickListener(v -> {
            mBinding.etEmail.setError(null);
            String email = mBinding.etEmail.getText().toString().trim();
            if (email.isEmpty() && isValidMail(email)) {
                mBinding.etEmail.setError("Please enter valid email.");
                return;
            }
            verifyMobile(mBinding.etEmail.getText().toString().trim(), "E");
        });
        mBinding.btnMobileVerify.setOnClickListener(v -> {
            mBinding.etMobile.setError(null);
            if (mBinding.etMobile.getText().toString().trim().isEmpty() ||
                    mBinding.etMobile.getText().toString().trim().length() < 10) {
                mBinding.etMobile.setError("Please enter mobile number");
                return;
            }
            verifyMobile(mBinding.etMobile.getText().toString().trim(), "M");
        });
    }

    private void verifyMobile(String mobile, String verify) {
        HashMap map = new HashMap();
        map.put(Constant.u_app_version, Constant.APP_VERSION);
        map.put(Constant.device_id, getDeviceId());
        map.put(Constant.u_user_type, "u");
        map.put(Constant.u_mob_1, mobile);
        map.put(Constant.VERIFY, verify);
        map.put(Constant.CASE, "1");
        map.put(Constant.appName, Constant.APP_NAME);

        if (verify != null && verify.equalsIgnoreCase("E")) {
            isMail = true;
        } else {
            isMail = false;
        }

        verifyMobileEmail(map);
    }


    private void updateUi(UserProfile userProfile) {
        if (userProfile.getStrGender().equalsIgnoreCase("M")) {
            loadProfilePic(Constant.PRODUCTION_MALE_PROFILE_API);
            mBinding.radioMale.setChecked(true);
        } else if (userProfile.getStrGender().equalsIgnoreCase("F")) {
            loadProfilePic(Constant.PRODUCTION_FEMALE_PROFILE_API);
            mBinding.radioFemale.setChecked(true);
        } else if (userProfile.getStrGender().equalsIgnoreCase("O")) {
            mBinding.radioOthers.setChecked(true);
        }

        Log.d(TAG, "Nationality Id: " + userProfile.getU_NationalityId());
        fetchStates(userProfile.getU_NationalityId());

        Log.d(TAG, "State Id: " + userProfile.getU_StateId());
        fetchDistricts(userProfile.getU_StateId());

        Log.d(TAG, "District Id: " + userProfile.getU_DistrictId());
        fetchCities(userProfile.getU_DistrictId());

        gender = userProfile.getStrGender();

        //display actual profile pic
        Log.d(TAG, "updateUi: " + getProfileImageUrl(userProfile.getEndPathImage()));
        loadProfilePic(getProfileImageUrl(userProfile.getEndPathImage()));

        mBinding.genderGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (mBinding.radioMale.isChecked()) {
                    gender = "M";
                } else if (mBinding.radioFemale.isChecked()) {
                    gender = "F";
                } else {
                    gender = "O";
                }
            }
        });

        mBinding.btnSave.setOnClickListener(v -> {
            mBinding.etFirstName.setError(null);
            mBinding.etLastName.setError(null);
            mBinding.etMobile.setError(null);
            mBinding.etTagLine.setError(null);
            mBinding.etPassword.setError(null);
            if (mBinding.etFirstName.getText().toString().trim().isEmpty()) {
                mBinding.etFirstName.setError("Required");
                return;
            }
            if (mBinding.etLastName.getText().toString().trim().isEmpty()) {
                mBinding.etLastName.setError("Required");
                return;
            }
            if (mBinding.etMobile.getText().toString().trim().isEmpty()) {
                mBinding.etMobile.setError("Required");
                return;
            }

            if (mBinding.etDob.getText().toString().trim().isEmpty()) {
                mBinding.etMobile.setError("Required");
                return;
            }

            if (mBinding.genderGrp.getCheckedRadioButtonId() == -1) {
                showToast("Please Select Gender");
                return;
            }

            if (!mBinding.etPassword.getText().toString().isEmpty() && mBinding.etPassword.getText().toString().length() < 8) {
                mBinding.etPassword.setError("Password should be 8 characters long");
                return;
            }


            HashMap userProfileMap = new HashMap();
            userProfileMap.put(Constant.u_user_id, getSingleQuoteString(String.valueOf(new PreferenceManager(requireActivity()).getInt(Constant.USER_ID))));
            userProfileMap.put(Constant.u_app_version, Constant.APP_VERSION);
            userProfileMap.put(Constant.device_id, getSingleQuoteString(getDeviceId()));
            userProfileMap.put(Constant.appName, getSingleQuoteString(Constant.APP_NAME));
            userProfileMap.put(Constant.u_first_name, getSingleQuoteString(mBinding.etFirstName.getText().toString().trim()));
            userProfileMap.put(Constant.u_last_name, getSingleQuoteString(mBinding.etLastName.getText().toString().trim()));
            userProfileMap.put(Constant.CASE, getSingleQuoteString("n"));
            userProfileMap.put(Constant.STATUS, getSingleQuoteString("i"));
            userProfileMap.put(Constant.u_mob_1, mBinding.etMobile.getText().toString().trim());
            userProfileMap.put(Constant.u_Email, mBinding.etEmail.getText().toString().trim());
            userProfileMap.put(Constant.u_birth_date, convertDateToDataBaseFormat(mBinding.etDob.getText().toString()));
            userProfileMap.put(Constant.u_Password, mBinding.etPassword.getText().toString().trim());
            userProfileMap.put(Constant.u_tagline, getSingleQuoteString(mBinding.etTagLine.getText().toString().trim()));

            userProfileMap.put(Constant.u_native_ct_id, mBinding.cityAutocompleteView.getTag().toString());
            userProfileMap.put(Constant.u_native_s_id, mBinding.stateAutocompleteView.getTag());
            userProfileMap.put(Constant.u_native_dt_id, mBinding.divisionAutocompleteView.getTag());
            userProfileMap.put(Constant.u_nationality, mBinding.nationalityAutocompleteView.getTag());
            userProfileMap.put(Constant.w_lm_id_array, mBinding.langAutocompleteView.getTag());
            userProfileMap.put(Constant.u_gender, getSingleQuoteString(gender));

            updateUserProfile(userProfileMap);

        });

        mBinding.nationalityAutocompleteView.setOnItemClickListener((parent, view, position, id) -> {
            final String name = ((TextView) view).getText().toString();
            int key = getKeyFromValue(mMapNationality, name);
            if (key != -1) {
                mBinding.nationalityAutocompleteView.setTag(key);
                ProgressDialog.getInstance().show(requireActivity());
                fetchStates(String.valueOf(key));
                //fetchMasterData(UtilHelper.getStateApi(), Constant.s_c_id, "" + key, Params.STATE.value);
            }
        });

        mBinding.stateAutocompleteView.setOnItemClickListener((parent, view, position, id) -> {
            final String name = ((TextView) view).getText().toString();
            String key = getKeyStringFromValue(mMapStates, name);
            if (!key.isEmpty()) {
                mBinding.stateAutocompleteView.setTag(key);
                if (userProfile != null && userProfile.getU_State() != null &&
                        !userProfile.getU_State().equalsIgnoreCase(name)) {
                    mBinding.divisionAutocompleteView.setText("");
                    mBinding.cityAutocompleteView.setText("");
                }
                Log.d(TAG, "State selected : " + key + " name: " + name);
                ProgressDialog.getInstance().show(requireActivity());
                fetchDistricts(key);
                //fetchMasterData(UtilHelper.getDistrictApi(), Constant.dt_s_id, "" + key, Params.DISTRICT.value);
            }
        });


        mBinding.divisionAutocompleteView.setOnItemClickListener((parent, view, position, id) -> {
            final String name = ((TextView) view).getText().toString();
            String key = getKeyStringFromValue(mMapDistricts, name);
            if (!key.isEmpty()) {
                mBinding.divisionAutocompleteView.setTag(key);
                if (userProfile != null && userProfile.getU_District() != null &&
                        !userProfile.getU_District().equalsIgnoreCase(name)) {
                    mBinding.cityAutocompleteView.setText("");
                }
                Log.d(TAG, "District selected : " + key + " name: " + name);
                ProgressDialog.getInstance().show(requireActivity());
                fetchCities(key);
                //fetchMasterData(UtilHelper.getCityApi(), Constant.ct_dt_id, "" + key, Params.CITY.value);
            }
        });

        mBinding.cityAutocompleteView.setOnItemClickListener((parent, view, position, id) -> {
            final String name = ((TextView) view).getText().toString();
            String key = getKeyStringFromValue(mMapCities, name);
            if (!key.isEmpty()) {
                mBinding.cityAutocompleteView.setTag(key);
                Log.d(TAG, "City selected : " + key + " name: " + name);
            }
        });

        mBinding.langAutocompleteView.setOnItemClickListener((parent, view, position, id) -> {
            final String name = ((TextView) view).getText().toString();
            int key = getKeyFromValue(mMapLanguage, name);
            if (key != -1) {
                mBinding.langAutocompleteView.setTag(key);
                Log.i(TAG, "Language selected : " + key + " name: " + name);
            }
        });

        mBinding.etMobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString().trim();
                if (userProfile != null && !userProfile.getMobileNumber().isEmpty() && userProfile.getMobileNumber().equalsIgnoreCase(text)) {
                    mBinding.btnMobileVerify.setText("Verified");
                    mBinding.btnMobileVerify.setEnabled(false);
                    mBinding.btnMobileVerify.setAlpha((float) 0.5);
                } else {
                    mBinding.btnMobileVerify.setText("Verify");
                    mBinding.btnMobileVerify.setEnabled(true);
                    mBinding.btnMobileVerify.setAlpha((float) 1);
                }
            }
        });

        mBinding.etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString().trim();
                if (userProfile != null && !userProfile.getEmailAddress().isEmpty() && userProfile.getEmailAddress().equalsIgnoreCase(text)) {
                    mBinding.btnEmailVerify.setText("Verified");
                    mBinding.btnEmailVerify.setEnabled(false);
                    mBinding.btnEmailVerify.setAlpha((float) 0.5);
                } else {
                    mBinding.btnEmailVerify.setText("Verify");
                    mBinding.btnEmailVerify.setEnabled(true);
                    mBinding.btnEmailVerify.setAlpha((float) 1);
                }
            }
        });
    }

    private void updateUserProfile(HashMap<String, String> userProfileMap) {
        Log.d(TAG, "updateUserProfile: " + userProfileMap);
        ProgressDialog.getInstance().show(requireActivity());
        Call<UserProfileResponse> call = apiService.updateUserProfile(
                userProfileMap.get(Constant.u_user_id),
                userProfileMap.get(Constant.appName),
                userProfileMap.get(Constant.u_app_version),
                userProfileMap.get(Constant.device_id),
                userProfileMap.get(Constant.u_first_name),
                userProfileMap.get(Constant.u_last_name),
                userProfileMap.get(Constant.CASE),
                userProfileMap.get(Constant.u_mob_1),
                userProfileMap.get(Constant.u_Email),
                userProfileMap.get(Constant.STATUS),
                userProfileMap.get(Constant.u_Password),
                userProfileMap.get(Constant.u_birth_date),
                userProfileMap.get(Constant.u_tagline),

                String.valueOf(userProfileMap.get(Constant.u_native_ct_id)),
                String.valueOf(userProfileMap.get(Constant.u_native_s_id)),
                String.valueOf(userProfileMap.get(Constant.u_native_dt_id)),
                String.valueOf(userProfileMap.get(Constant.u_nationality)),
                String.valueOf(userProfileMap.get(Constant.w_lm_id_array)),
                userProfileMap.get(Constant.u_gender)
        );
        call.enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.body() != null && response.body().getResponseCode().equals("200")) {
                    showToast("Profile Updated Successfully.");
                    String displayName = mBinding.etFirstName.getText().toString().trim()+" "+mBinding.etLastName.getText().toString().trim();
                    new PreferenceManager(requireActivity()).saveString(Constant.DISPLAY_NAME,displayName);
                    new PreferenceManager(requireActivity()).saveString(Constant.GENDER,userProfileMap.get(Constant.u_gender));
                    MainActivity.navController.navigate(R.id.nav_home);
                } else {
                    showErrorDialog(requireActivity(), response.body().getResponseCode(), "");
                }
            }

            @Override
            public void onFailure(Call<UserProfileResponse> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                showToast("Something went wrong!!");
                t.printStackTrace();
            }
        });
    }

    private void loadProfilePic(String url) {
        Log.d(TAG, "loadProfilePic : "+ url);
        RequestOptions options = new RequestOptions()
                .centerCrop()
                .circleCrop()
                .placeholder(R.drawable.load)
                .error(R.drawable.ic_profile_default);
        Glide.with(this).load(url)
                .apply(options)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mBinding.userProfilePic);
    }

    private void fetchUserProfile() {
        ProgressDialog.getInstance().show(requireActivity());
        Call<UserProfile> call = apiService.fetchUserInfo(new PreferenceManager(requireActivity()).getInt(Constant.USER_ID),
                getDeviceId(), Constant.APP_NAME, Constant.APP_VERSION);
        call.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.body() != null && response.body().getResponseCode().equals("200")) {
                    mViewModel.setUserProfileResponse(response.body());
                } else  if(response.body().getResponseCode().equals("501")){
                    resetSettingAndLogout();
                }else {
                    showErrorDialog(requireActivity(), response.body().getResponseCode(), "");
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                showToast("Something went wrong!!");
                t.printStackTrace();
            }
        });
    }


    private void fetchLanguages() {
        ProgressDialog.getInstance().show(requireActivity());
        Call<Language> call = apiService.fetchLanguages(new PreferenceManager(requireActivity()).getInt(Constant.USER_ID));
        call.enqueue(new Callback<Language>() {
            @Override
            public void onResponse(Call<Language> call, Response<Language> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.body() != null) {
                    mViewModel.setLanguageList(response.body().getLanguageList());
                } else {
                    showErrorDialog(requireActivity(), response.body().getResponseCode(), "");
                }
            }

            @Override
            public void onFailure(Call<Language> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                showToast("Something went wrong!!");
                t.printStackTrace();
            }
        });
    }

    private void fetchNationalities() {
        ProgressDialog.getInstance().show(requireActivity());
        Call<CountryResponse> call = apiService.fetchNationalities(new PreferenceManager(requireActivity()).getInt(Constant.USER_ID));
        call.enqueue(new Callback<CountryResponse>() {
            @Override
            public void onResponse(Call<CountryResponse> call, Response<CountryResponse> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.body() != null && response.body().getResponse().equals("200")) {
                    mViewModel.setCountryList(response.body().getMasterDataList());
                    mMapNationality = new HashMap<>();
                    for (Country country : response.body().getMasterDataList()) {
                        mMapNationality.put(country.getCountryId(), country.getCountryName());
                    }
                    populateCountries(mMapNationality);
                } else {
                    showErrorDialog(requireActivity(), response.body().getResponse(), "");
                }
            }

            @Override
            public void onFailure(Call<CountryResponse> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                showToast("Something went wrong!!");
                t.printStackTrace();
            }
        });
    }

    private void fetchStates(String key) {
        ProgressDialog.getInstance().show(requireActivity());
        Call<StateResponse> call = apiService.fetchStates(key);
        call.enqueue(new Callback<StateResponse>() {
            @Override
            public void onResponse(Call<StateResponse> call, Response<StateResponse> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.body() != null && response.body().getResponseCode().equals("200")) {
                    mViewModel.setStateList(response.body().getStateList());
                } else {
                    showErrorDialog(requireActivity(), response.body().getResponseCode(), "");
                }
            }

            @Override
            public void onFailure(Call<StateResponse> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                showToast("Something went wrong!!");
                t.printStackTrace();
            }
        });
    }


    private void fetchDistricts(String key) {
        ProgressDialog.getInstance().show(requireActivity());
        Call<DistrictResponse> call = apiService.fetchDistricts(key);
        call.enqueue(new Callback<DistrictResponse>() {
            @Override
            public void onResponse(Call<DistrictResponse> call, Response<DistrictResponse> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.body() != null && response.body().getResponseCode().equals("200")) {
                    mViewModel.setDistrictList(response.body().getDistrictList());
                } else {
                    showErrorDialog(requireActivity(), response.body().getResponseCode(), "");
                }
            }

            @Override
            public void onFailure(Call<DistrictResponse> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                showToast("Something went wrong!!");
                t.printStackTrace();
            }
        });
    }

    private void fetchCities(String key) {
        ProgressDialog.getInstance().show(requireActivity());
        Call<CityResponse> call = apiService.fetchCities(key);
        call.enqueue(new Callback<CityResponse>() {
            @Override
            public void onResponse(Call<CityResponse> call, Response<CityResponse> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.body() != null) {
                    mViewModel.setCityList(response.body().getCityList());
                } else {
                    showErrorDialog(requireActivity(), response.body().getResponseCode(), "");
                }
            }

            @Override
            public void onFailure(Call<CityResponse> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                showToast("Something went wrong!!");
                t.printStackTrace();
            }
        });
    }

    private void showDatePicker() {
        DateFormat format = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        Date date = null;
        try {
            date = format.parse(mBinding.etDob.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar newCalendar = Calendar.getInstance();
        newCalendar.setTime(date);
        DatePickerDialog dialog = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Dialog, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String formattedDate = year + "-" + (month + 1) + "-" + day;
                mBinding.etDob.setText(DateUtils.getFormattedDate(formattedDate));
            }
        }, //newCalendar.get(1990), newCalendar.get(1), newCalendar.get(Calendar.DAY_OF_MONTH));
                newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();

    }

    private String getSingleQuoteString(String text) {
        return String.format("'%s'", text);
    }

    private Integer getKeyFromValue(Map<Integer, String> map, String name) {
        int selectedKey = -1;
        for (Map.Entry<Integer, String> e : map.entrySet()) {
            int key = e.getKey();
            String value = e.getValue();
            if (value.equals(name)) {
                selectedKey = key;
                break;
            }
        }
        return selectedKey;
    }

    private void populateCountries(Map<Integer, String> mMapNationality) {
        List<String> list = new ArrayList<>(mMapNationality.values());
        Collections.sort(list);
        Log.d(TAG, "populateCountries: " + list.size());
        ArrayAdapter<String> nationalityAdapter = new ArrayAdapter<>(requireActivity(), R.layout.textview_dropdown, list);
        mBinding.nationalityAutocompleteView.setThreshold(0);
        mBinding.nationalityAutocompleteView.setAdapter(nationalityAdapter);
    }

    private void populateDistrict(Map<Integer, String> districtMap) {
        List<String> list = new ArrayList<>(districtMap.values());
        Collections.sort(list);
        Log.d(TAG, "populateDistrict: " + list.size());
        ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(requireActivity(), R.layout.textview_dropdown, list);
        mBinding.divisionAutocompleteView.setThreshold(0);
        mBinding.divisionAutocompleteView.setAdapter(stateAdapter);
    }

    private void populateCity(Map<Integer, String> cityMap) {
        List<String> list = new ArrayList<>(cityMap.values());
        Collections.sort(list);
        Log.d(TAG, "populateCity: " + list.size());
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(requireActivity(), R.layout.textview_dropdown, list);
        mBinding.cityAutocompleteView.setThreshold(0);
        mBinding.cityAutocompleteView.setAdapter(cityAdapter);
    }

    private void populateState(Map<Integer, String> stateMap) {
        List<String> list = new ArrayList<>(stateMap.values());
        Collections.sort(list);
        Log.d(TAG, "populateState: " + list.size());
        ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(requireActivity(), R.layout.textview_dropdown, list);
        mBinding.stateAutocompleteView.setThreshold(0);
        mBinding.stateAutocompleteView.setAdapter(stateAdapter);
    }

    private void populateLanguage(Map<Integer, String> mMapLanguage) {
        List<String> list = new ArrayList<>(mMapLanguage.values());
        Collections.sort(list);
        Log.d(TAG, "populateLanguage: " + list.size());
        ArrayAdapter<String> languageAdapter = new ArrayAdapter<>(requireActivity(), R.layout.textview_dropdown, list);
        mBinding.langAutocompleteView.setThreshold(0);
        mBinding.langAutocompleteView.setAdapter(languageAdapter);
    }

    private String getKeyStringFromValue(Map<Integer, String> map, String name) {
        String selectedKey = "";
        for (Map.Entry<Integer, String> e : map.entrySet()) {
            int key = e.getKey();
            String value = e.getValue();
            if (value.equals(name)) {
                selectedKey += key;
                break;
            }
        }
        return selectedKey;
    }

    private boolean isValidMail(String email) {
        return Pattern.compile(Constant.EMAIL_STRING).matcher(email).matches();

    }

    private void verifyMobileEmail(HashMap<String, String> map) {
        Log.d(TAG, "verifyMobileEmail: " + map);
        ProgressDialog.getInstance().show(requireActivity());
        Call<VerifyResponse> call = apiService.verifyMobileEmail(
                map.get(Constant.appName),
                map.get(Constant.u_app_version),
                map.get(Constant.device_id),
                map.get(Constant.u_user_type),
                map.get(Constant.u_mob_1),
                map.get(Constant.VERIFY),
                map.get(Constant.CASE));

        call.enqueue(new Callback<VerifyResponse>() {
            @Override
            public void onResponse(Call<VerifyResponse> call, Response<VerifyResponse> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.body() != null && response.body().getResponse().equals("200")) {
                    createVerifyOTPDialog(response.body().getNewOTP());
                } else {
                    showErrorDialog(requireActivity(), response.body().getResponse(), "");
                }
            }

            @Override
            public void onFailure(Call<VerifyResponse> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                showToast("Something went wrong!!");
                t.printStackTrace();
            }
        });
    }

    private void createVerifyOTPDialog(final String otp) {
        dialog = new Dialog(requireActivity());
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(android.R.color.transparent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.otp_layout);
        final OtpView otpText = dialog.findViewById(R.id.otp_view);
        // TextView resendButton = dialog.findViewById(R.id.resend_otp);
        Button btnDone = dialog.findViewById(R.id.btnDone);
        AppCompatImageView close = dialog.findViewById(R.id.close);

        // TextView tvDesc = dialog.findViewById(R.id.desc);
        close.setOnClickListener(view -> dialog.dismiss());

        btnDone.setOnClickListener(v -> {
            if (Objects.requireNonNull(otpText.getText()).toString().equalsIgnoreCase(otp)) {
                otpText.setText("");
                if (isMail) {
                    isMailVerified = true;
                    mBinding.btnEmailVerify.setText("Verified");
                    mBinding.btnEmailVerify.setEnabled(false);
                    mBinding.btnEmailVerify.setAlpha((float) 0.5);
                    dialog.dismiss();
                } else {
                    isMobileVerified = true;
                    mBinding.btnMobileVerify.setText("Verified");
                    mBinding.btnMobileVerify.setEnabled(false);
                    mBinding.btnMobileVerify.setAlpha((float) 0.5);
                    dialog.dismiss();
                }
            } else {
                otpText.setText("");
                otpText.setError("Enter valid OTP");
                otpText.requestFocus();
                showToast("Enter valid OTP");
            }
        });
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_REQUEST_LOAD_IMAGE_CODE && resultCode == RESULT_OK && data != null) {
            try {
                final Uri imageUri = data.getData();
                CropImage.activity(imageUri)
                        .start(requireActivity(), this);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(requireActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (result != null && resultCode == RESULT_OK) {
                File imageFile = new File(result.getUri().getPath());
                updateProfileImage(imageFile);
                //new compressImageTask(mUserId, result.getUri(), requireActivity()).execute();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(requireActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(requireActivity(), "You haven't picked Image", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(PersonalInfoViewModel.class);
    }

    private void loadImage() {
        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_REQUEST_LOAD_IMAGE_CODE);
    }

//    private class compressImageTask extends AsyncTask<Void, Void, byte[]> {
//
//        private Uri data;
//        private String userId;
//        private Context mContext;
//
//        public compressImageTask(Uri data, String userId, Context context) {
//            this.data = data;
//            this.mContext = context;
//            this.userId = userId;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            ProgressDialog.getInstance().show(requireActivity());
//        }
//
//        @Override
//        protected byte[] doInBackground(final Void... voids) {
//            try {
//               return new ImageOptimization(requireActivity()).compressImage(data.toString());
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(byte[] bytes) {
//            super.onPostExecute(bytes);
//            ProgressDialog.getInstance().dismiss();
//        }
//    }

    private void updateProfileImage(File imageFile) {
        //pass it like this
        //File file = new File("/storage/emulated/0/Download/Corrections 6.jpg");
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("image/png"), imageFile);

        HashMap<String, String> params = new HashMap<>();
        params.put(Constant.u_user_id, String.valueOf(new PreferenceManager(requireActivity()).getInt(Constant.USER_ID)));
        params.put(Constant.device_id, getDeviceId());
        params.put(Constant.appName, Constant.APP_NAME);
        params.put(Constant.u_app_version, Constant.APP_VERSION);
        params.put(Constant.CASE, "N");

        RequestBody userId =
                RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(new PreferenceManager(requireActivity()).getInt(Constant.USER_ID)));
        RequestBody deviceId =
                RequestBody.create(MediaType.parse("multipart/form-data"), getDeviceId());
        RequestBody appName =
                RequestBody.create(MediaType.parse("multipart/form-data"), Constant.APP_NAME);
        RequestBody appVersion =
                RequestBody.create(MediaType.parse("multipart/form-data"), Constant.APP_VERSION);
        RequestBody caseq =
                RequestBody.create(MediaType.parse("multipart/form-data"), "N");

        Log.d(TAG, "Profile Image Params : " + params);
        Log.d(TAG, "Profile Image Size: " + imageFile.getTotalSpace());

        MultipartBody.Part body =
                MultipartBody.Part.createFormData("file", imageFile.getName(), requestFile);

        ProgressDialog.getInstance().show(requireActivity());
        Call<VerifyResponse> call = apiService.updateProfileImage(userId,
                deviceId, appName, appVersion, caseq, body);
        call.enqueue(new Callback<VerifyResponse>() {
            @Override
            public void onResponse(Call<VerifyResponse> call, Response<VerifyResponse> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.body() != null && response.body().getResponse().equals("200")) {
                    loadProfilePic(getProfileImageUrl(response.body().getW_user_profile_image_name()));
                    new PreferenceManager(requireActivity()).saveString(Constant.PROFILE_PIC,getProfileImageUrl(response.body().getW_user_profile_image_name()));
                } else {
                    showErrorDialog(requireActivity(), response.body().getResponse(), "");
                }
            }

            @Override
            public void onFailure(Call<VerifyResponse> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                t.printStackTrace();
                Log.d(TAG, "onFailure: " + t.getMessage());
                Log.e(TAG, "onFailure: " + t.getStackTrace());
                showToast("Something went wrong!!");
            }
        });
    }
}
