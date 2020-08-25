package com.jangletech.qoogol.ui.personal_info;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
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
import androidx.appcompat.widget.AppCompatMultiAutoCompleteTextView;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.FragmentPersonalInfoBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.model.City;
import com.jangletech.qoogol.model.CityResponse;
import com.jangletech.qoogol.model.Country;
import com.jangletech.qoogol.model.CountryResponse;
import com.jangletech.qoogol.model.District;
import com.jangletech.qoogol.model.DistrictResponse;
import com.jangletech.qoogol.model.GenerateVerifyUserName;
import com.jangletech.qoogol.model.Language;
import com.jangletech.qoogol.model.ResponseObj;
import com.jangletech.qoogol.model.State;
import com.jangletech.qoogol.model.StateResponse;
import com.jangletech.qoogol.model.UserProfile;
import com.jangletech.qoogol.model.UserProfileResponse;
import com.jangletech.qoogol.model.VerifyResponse;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.ui.connections.FollowingViewModel;
import com.jangletech.qoogol.ui.connections.FriendReqViewModel;
import com.jangletech.qoogol.util.AESSecurities;
import com.jangletech.qoogol.util.AppUtils;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.DateUtils;
import com.jangletech.qoogol.util.PreferenceManager;
import com.jangletech.qoogol.util.TinyDB;
import com.jangletech.qoogol.util.UtilHelper;
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

import static android.app.Activity.RESULT_OK;
import static com.jangletech.qoogol.util.Constant.accept_friend_requests;
import static com.jangletech.qoogol.util.Constant.fetch_loged_in_user;
import static com.jangletech.qoogol.util.Constant.fetch_other_user;
import static com.jangletech.qoogol.util.Constant.follow;
import static com.jangletech.qoogol.util.Constant.qoogol;
import static com.jangletech.qoogol.util.Constant.reject_friend_requests;
import static com.jangletech.qoogol.util.Constant.remove_connection;
import static com.jangletech.qoogol.util.Constant.sent_friend_req;
import static com.jangletech.qoogol.util.Constant.unfollow;

public class PersonalInfoFragment extends BaseFragment {

    private static final String TAG = "PersonalInfoFragment";
    private static final int RESULT_REQUEST_LOAD_IMAGE_CODE = 1001;
    private PersonalInfoViewModel mViewModel;
    private android.app.ProgressDialog mDialog;
    private FragmentPersonalInfoBinding mBinding;
    private Dialog dialog;
    private String masterKey = "";
    private Context mContext;
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
    private HashMap<String, String> userProfileMap;
    private ApiInterface apiService = ApiClient.getInstance().getApi();
    private String userid = "";
    private PreferenceManager mSettings;
    private Call<UserProfile> call;
    private boolean isFragmentVisible = false;

    public static PersonalInfoFragment newInstance() {
        return new PersonalInfoFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context.getApplicationContext();
        userProfileMap = new HashMap();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isFragmentVisible) {
            fetchUserProfile(fetch_loged_in_user, "");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_personal_info, container, false);
        mViewModel = new ViewModelProvider(this).get(PersonalInfoViewModel.class);
        mBinding.setLifecycleOwner(this);
        masterKey = AESSecurities.getMasterKey(AppUtils.getDeviceId());
        return mBinding.getRoot();
    }

    public void initViews() {
        mSettings = new PreferenceManager(getActivity());
        userid = mSettings.getProfileFetchId();
        Log.d(TAG, "initViews UserId : " + mSettings.getUserId());
        if (!isFragmentVisible) {
            isFragmentVisible = true;
            if (userid.equalsIgnoreCase(mSettings.getUserId()))
                fetchUserProfile(fetch_loged_in_user, "");
            else
                fetchUserProfile(fetch_other_user, "");
        }

        mViewModel.getUserProfile(userid).observe(getViewLifecycleOwner(), userProfile -> {
            Log.d(TAG, "onChanged : " + userProfile);
            if (userProfile != null) {
                profile = userProfile;
                //Log.d(TAG, "initViews FName : " + userProfile.getFirstName());
                //Log.d(TAG, "initViews LName : " + userProfile.getLastName());
                //Log.d(TAG, "initViews Dob : " + userProfile.getDob());
                mBinding.setUserProfile(userProfile);
                //save User Badge Info
                new PreferenceManager(getActivity()).saveString("BADGE", userProfile.getBadge());
                new PreferenceManager(getActivity()).saveString(Constant.userName, userProfile.getUserName());

                Log.d(TAG, "Focus Languages : " + userProfile);
                //set First Name, Last Name, Mobile Number, Email, Password, Dob
                mBinding.etFirstName.setText(AESSecurities.getInstance().decrypt(TinyDB.getInstance(getActivity()).getString(Constant.cf_key1), userProfile.getFirstName()));
                mBinding.etLastName.setText(AESSecurities.getInstance().decrypt(TinyDB.getInstance(getActivity()).getString(Constant.cf_key2), userProfile.getLastName()));
                mBinding.etDob.setText(DateUtils.getFormattedDate(AESSecurities.getInstance().decrypt(TinyDB.getInstance(getActivity()).getString(Constant.cf_key3), userProfile.getDob())));
                mBinding.etMobile.setText(AESSecurities.getInstance().decrypt(TinyDB.getInstance(getActivity()).getString(Constant.cf_key4), userProfile.getMobileNumber()));
                mBinding.etEmail.setText(AESSecurities.getInstance().decrypt(TinyDB.getInstance(getActivity()).getString(Constant.cf_key5), userProfile.getEmailAddress()));
                mBinding.etPassword.setText(AESSecurities.getInstance().decrypt(TinyDB.getInstance(getActivity()).getString(Constant.cf_key6), userProfile.getPassword()));

                if (!userid.equalsIgnoreCase(mSettings.getUserId())) {
                    //manageUnwantedFields(userProfile);
                    getActionBar().setTitle("User Profile");
                    mBinding.userProfileLayout.setVisibility(View.GONE);
                    mBinding.publicProfileLayout.setVisibility(View.VISIBLE);
                    setPublicProfile(userProfile);
                } else {
                    mBinding.publicProfileLayout.setVisibility(View.GONE);
                    mBinding.userProfileLayout.setVisibility(View.VISIBLE);
                    updateUi(userProfile);
                }
            }
        });

        mViewModel.getUserNameData().observe(getViewLifecycleOwner(), new Observer<GenerateVerifyUserName>() {
            @Override
            public void onChanged(GenerateVerifyUserName generateVerifyUserName) {
                if (generateVerifyUserName != null) {
                    Log.d(TAG, "onChanged UserNames Executed: ");
                    getUserNames(generateVerifyUserName);
                }
            }
        });


        mBinding.userProfilePic.setOnClickListener(v -> {
            loadImage();
        });

        mBinding.etDob.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    mBinding.etDob.setError(null);
                }
            }
        });

        mBinding.btnCheckExist.setOnClickListener(v -> {
            HashMap<String, String> params = new HashMap<>();
            params.put(Constant.u_first_name, mBinding.etFirstName.getText().toString());
            params.put(Constant.u_last_name, mBinding.etLastName.getText().toString());
            params.put(Constant.CASE, "V");
            if (mBinding.userNameAutoCompleteTextView.getText().toString().trim().isEmpty())
                params.put(Constant.userName, mBinding.userNameAutoCompleteTextView.getTag().toString());
            else
                params.put(Constant.userName, mBinding.userNameAutoCompleteTextView.getText().toString().trim());

            if (mBinding.userNameAutoCompleteTextView.getText().toString().trim().isEmpty()) {
                mBinding.userNameAutoCompleteTextView.setError("Enter valid username.");
                //showToast("Please enter any unique username.");
            } else if (profile.getUserName() != null && profile.getUserName().equals(mBinding.userNameAutoCompleteTextView.getText().toString().trim())) {
                showToast("You have already taken this username.");
            } else {
                generateVerifyUserName(params);
            }
        });

        mBinding.btnGenerate.setOnClickListener(v -> {
            HashMap<String, String> params = new HashMap<>();
            params.put(Constant.u_first_name, mBinding.etFirstName.getText().toString());
            params.put(Constant.u_last_name, mBinding.etLastName.getText().toString());
            params.put(Constant.CASE, "G");
            params.put(Constant.userName, "");
            generateVerifyUserName(params);
        });

        mBinding.btnImportContacts.setOnClickListener(v -> {
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.nav_import_contacts);
            //MainActivity.navController.navigate(R.id.nav_import_contacts);
        });

        //set Mandatory Fields
        mBinding.tvFirstName.setText(Html.fromHtml(getString(R.string.first_name)));
        mBinding.tvLastName.setText(Html.fromHtml(getString(R.string.last_name)));
        mBinding.tvMobile.setText(Html.fromHtml(getString(R.string.mobile_number)));

        mBinding.etDob.setOnClickListener(v -> {
            showDatePicker();
        });

        mBinding.btnEmailVerify.setOnClickListener(v -> {
            mBinding.etEmail.setError(null);
            String email = mBinding.etEmail.getText().toString().trim();
            if (email.isEmpty() && !isValidMail(email)) {
                mBinding.etEmail.setError("Please enter valid email.");
                return;
            } else {
                verifyMobile(AESSecurities.getInstance().encrypt(
                        TinyDB.getInstance(getActivity()).getString(Constant.cf_key5),
                        mBinding.etEmail.getText().toString().trim()), "E");
            }
        });
        mBinding.btnMobileVerify.setOnClickListener(v -> {
            mBinding.etMobile.setError(null);
            if (mBinding.etMobile.getText().toString().trim().isEmpty() ||
                    mBinding.etMobile.getText().toString().trim().length() < 10) {
                mBinding.etMobile.setError("Please enter mobile number");
                return;
            }
            verifyMobile(AESSecurities.getInstance().encrypt(
                    TinyDB.getInstance(getActivity()).getString(Constant.cf_key4),
                    mBinding.etMobile.getText().toString().trim()), "M");
        });

        if (userid.equalsIgnoreCase(mSettings.getUserId())) {
            fetchLanguages();
            mViewModel.getLanguages().observe(getViewLifecycleOwner(), new Observer<List<Language>>() {
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
            mViewModel.getCountries().observe(getViewLifecycleOwner(), new Observer<List<Country>>() {
                @Override
                public void onChanged(@Nullable final List<Country> countries) {
                    mMapNationality = new HashMap<>();
                    for (Country country : countries) {
                        mMapNationality.put(country.getCountryId(), country.getCountryName());
                    }
                    populateCountries(mMapNationality);
                }
            });

            mViewModel.getStates().observe(getViewLifecycleOwner(), new Observer<List<State>>() {
                @Override
                public void onChanged(@Nullable final List<State> states) {
                    mMapStates = new HashMap<>();
                    for (State state : states) {
                        mMapStates.put(Integer.valueOf(state.getState_id()), state.getStateName());
                    }
                    populateState(mMapStates);
                }
            });

            mViewModel.getDistricts().observe(getViewLifecycleOwner(), new Observer<List<District>>() {
                @Override
                public void onChanged(@Nullable final List<District> districts) {
                    mMapDistricts = new HashMap<>();
                    for (District district : districts) {
                        mMapDistricts.put(Integer.valueOf(district.getDistrict_id()), district.getDistrictName());
                    }
                    populateDistrict(mMapDistricts);
                }
            });

            mViewModel.getCities().observe(getViewLifecycleOwner(), new Observer<List<City>>() {
                @Override
                public void onChanged(@Nullable final List<City> cities) {
                    mMapCities = new HashMap<>();
                    for (City city : cities) {
                        mMapCities.put(Integer.valueOf(city.getCity_id()), city.getCityName());
                    }
                    populateCity(mMapCities);
                }
            });

        } else {
            manageLayoutForOtherUser();
        }
    }

    private void populateUserNames(List<String> userNames) {
        mBinding.userNameAutoCompleteTextView.setText("");
        mBinding.userNameAutoCompleteTextView.requestFocus();
        Collections.sort(userNames);
        Log.d(TAG, "populateUserNames: " + userNames.size());
        ArrayAdapter<String> userNameAdapter = new ArrayAdapter(mContext, R.layout.textview_dropdown, userNames);
        mBinding.userNameAutoCompleteTextView.setThreshold(0);
        mBinding.userNameAutoCompleteTextView.setAdapter(userNameAdapter);
    }

    private void manageUnwantedFields(UserProfile userProfile) {
        if (userProfile.getStrTagLine().equalsIgnoreCase("")) {
            mBinding.taglineLayout.setVisibility(View.GONE);
        }

        if (userProfile.getU_language().equalsIgnoreCase("")) {
            mBinding.tvLanguage.setVisibility(View.GONE);
            mBinding.langAutocompleteView.setVisibility(View.GONE);
        }

        if (userProfile.getU_Nationality().equalsIgnoreCase("")) {
            mBinding.tvNationality.setVisibility(View.GONE);
            mBinding.nationalityAutocompleteView.setVisibility(View.GONE);
        }

        if (userProfile.getU_State().equalsIgnoreCase("")) {
            mBinding.tvState.setVisibility(View.GONE);
            mBinding.stateAutocompleteView.setVisibility(View.GONE);
        }

        if (userProfile.getU_District().equalsIgnoreCase("")) {
            mBinding.tvDivision.setVisibility(View.GONE);
            mBinding.divisionAutocompleteView.setVisibility(View.GONE);
        }

        if (userProfile.getU_State().equalsIgnoreCase("")) {
            mBinding.tvState.setVisibility(View.GONE);
            mBinding.stateAutocompleteView.setVisibility(View.GONE);
        }

        if (userProfile.getU_City().equalsIgnoreCase("")) {
            mBinding.tvCity.setVisibility(View.GONE);
            mBinding.cityAutocompleteView.setVisibility(View.GONE);
        }

        mBinding.radioMale.setVisibility(View.GONE);
        mBinding.radioFemale.setVisibility(View.GONE);
        mBinding.radioOthers.setVisibility(View.GONE);

        if (userProfile.getStrGender().equalsIgnoreCase("M"))
            mBinding.radioMale.setVisibility(View.VISIBLE);
        else if (userProfile.getStrGender().equalsIgnoreCase("F"))
            mBinding.radioFemale.setVisibility(View.VISIBLE);
        else if (userProfile.getStrGender().equalsIgnoreCase("O"))
            mBinding.radioOthers.setVisibility(View.VISIBLE);
    }

    private void manageLayoutForOtherUser() {
        mBinding.btnImportContacts.setVisibility(View.GONE);
        mBinding.btnSave.setVisibility(View.GONE);
        mBinding.passwordLayout.setVisibility(View.GONE);
        mBinding.refferalLayout.setVisibility(View.GONE);
        mBinding.pointsLayout.setVisibility(View.GONE);
        mBinding.mobileLayout.setVisibility(View.GONE);
        mBinding.mobileEmailLayout.setVisibility(View.GONE);

        mBinding.etDob.setOnClickListener(null);
        mBinding.userProfilePic.setOnClickListener(null);

        mBinding.genderGrp.setEnabled(false);
        mBinding.etFirstName.setEnabled(false);
        mBinding.etLastName.setEnabled(false);
        mBinding.langAutocompleteView.setEnabled(false);
        mBinding.etTagLine.setEnabled(false);
        mBinding.stateAutocompleteView.setEnabled(false);
        mBinding.nationalityAutocompleteView.setEnabled(false);
        mBinding.divisionAutocompleteView.setEnabled(false);
        mBinding.cityAutocompleteView.setEnabled(false);
    }

    private void getUserNames(GenerateVerifyUserName generateVerifyUserName) {
        if (generateVerifyUserName.getUserNames() != null) {
            String[] names = generateVerifyUserName.getUserNames().split(",", -1);
            List<String> userNames = new ArrayList<>();
            for (int i = 0; i < names.length; i++) {
                userNames.add(names[i]);
            }
            populateUserNames(userNames);
        }
    }

    private void verifyMobile(String mobile, String verify) {
        HashMap map = new HashMap();
        map.put(Constant.u_app_version, Constant.APP_VERSION);
        map.put(Constant.device_id, getDeviceId(getActivity()));
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

    /*private void setGenderProfilePic(UserProfile userProfile){
        if(userProfile.getStrGender().equalsIgnoreCase("M")){
            if (userProfile.getStrGender().equalsIgnoreCase("M")) {
                loadProfilePic(Constant.PRODUCTION_MALE_PROFILE_API);
            } else if (userProfile.getStrGender().equalsIgnoreCase("F")) {
                loadProfilePic(Constant.PRODUCTION_FEMALE_PROFILE_API);
            }
        }
    }*/

    private void setPublicProfile(UserProfile userProfile) {
        try {
            if (userProfile != null && userProfile.getIsConnected().equalsIgnoreCase("true")) {
                mBinding.addFriend.setText(getActivity().getResources().getString(R.string.unfriend));
            } else if (userProfile != null && userProfile.getIsInitiated_by_u1().equalsIgnoreCase("true")) {
                mBinding.addFriend.setText(getActivity().getResources().getString(R.string.cancel));
            } else if (userProfile != null && userProfile.getIsInitiated_by_u2().equalsIgnoreCase("true")) {
                mBinding.addFriend.setText(getActivity().getResources().getString(R.string.accept_friend_req));
            } else {
                mBinding.addFriend.setText(getActivity().getResources().getString(R.string.add_friend));
            }

            if (userProfile != null && userProfile.getU1FollowsU2().equalsIgnoreCase("true")) {
                mBinding.follow.setText(getActivity().getResources().getString(R.string.unfollow));
            } else {
                mBinding.follow.setText(getActivity().getResources().getString(R.string.follow));
            }

            //set user badge
            if (userProfile.getBadge() != null && userProfile.getBadge().equalsIgnoreCase("B"))
                mBinding.imgBadge.setImageDrawable(getActivity().getDrawable(R.drawable.bronze));
            if (userProfile.getBadge() != null && userProfile.getBadge().equalsIgnoreCase("S"))
                mBinding.imgBadge.setImageDrawable(getActivity().getDrawable(R.drawable.silver));
            if (userProfile.getBadge() != null && userProfile.getBadge().equalsIgnoreCase("G"))
                mBinding.imgBadge.setImageDrawable(getActivity().getDrawable(R.drawable.gold));
            if (userProfile.getBadge() != null && userProfile.getBadge().equalsIgnoreCase("P"))
                mBinding.imgBadge.setImageDrawable(getActivity().getDrawable(R.drawable.platinum));


            mBinding.tvName.setText(userProfile.getFirstName() + " " + userProfile.getLastName());
            if (userProfile.getStrGender() != null && userProfile.getStrGender().equalsIgnoreCase("M")) {
                mBinding.tvGender.setText("Male");
            } else if (userProfile.getStrGender() != null && userProfile.getStrGender().equalsIgnoreCase("F")) {
                mBinding.tvGender.setText("Female");
            }
            if (userProfile.getEndPathImage() != null && !userProfile.getEndPathImage().isEmpty()) {
                loadProfilePic(getProfileImageUrl(userProfile.getEndPathImage()));
            } else if (userProfile.getStrGender() != null && userProfile.getStrGender().equalsIgnoreCase("M")) {
                loadProfilePic(Constant.PRODUCTION_MALE_PROFILE_API);
            } else if (userProfile.getStrGender() != null && userProfile.getStrGender().equalsIgnoreCase("F")) {
                loadProfilePic(Constant.PRODUCTION_FEMALE_PROFILE_API);
            }

            if (userProfile.getU_Nationality() == null || userProfile.getU_Nationality().isEmpty()) {
                mBinding.nationalityLayout.setVisibility(View.GONE);
            } else if (userProfile.getU_State() == null || userProfile.getU_State().isEmpty()) {
                mBinding.stateLayout.setVisibility(View.GONE);
            } else if (userProfile.getU_District() == null || userProfile.getU_District().isEmpty()) {
                mBinding.divisionLayout.setVisibility(View.GONE);
            } else if (userProfile.getU_City() == null || userProfile.getU_City().isEmpty()) {
                mBinding.cityLayout.setVisibility(View.GONE);
            } else if (userProfile.getU_language() == null || userProfile.getU_language().isEmpty()) {
                mBinding.languageLayout.setVisibility(View.GONE);
            }

            mBinding.addFriend.setOnClickListener(v -> {
                if (userProfile.getIsConnected().equalsIgnoreCase("true")) {
                    updateConnection(userProfile.getUserId(), remove_connection);
                } else if (userProfile.getIsInitiated_by_u1().equalsIgnoreCase("true")) {
                    updateConnection(userProfile.getUserId(), reject_friend_requests);
                } else if (userProfile.getIsInitiated_by_u2().equalsIgnoreCase("true")) {
                    updateConnection(userProfile.getUserId(), accept_friend_requests);
                } else {
                    updateConnection(userProfile.getUserId(), sent_friend_req);
                }
            });

            mBinding.follow.setOnClickListener(v -> {
                if (userProfile.getU1FollowsU2().equalsIgnoreCase("true")) {
                    updateConnection(userProfile.getUserId(), unfollow);
                } else {
                    updateConnection(userProfile.getUserId(), follow);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateConnection(String user, String Processcase) {
        ApiInterface apiService = ApiClient.getInstance().getApi();
        ProgressDialog.getInstance().show(getActivity());
        Call<ResponseObj> call = apiService.updateConnections(String.valueOf(new PreferenceManager(getActivity()).getInt(Constant.USER_ID)), Processcase, getDeviceId(getActivity()), qoogol, user);
        call.enqueue(new Callback<ResponseObj>() {
            @Override
            public void onResponse(Call<ResponseObj> call, retrofit2.Response<ResponseObj> response) {
                try {
                    ProgressDialog.getInstance().dismiss();
                    if (response.body() != null && response.body().getResponse().equalsIgnoreCase("200")) {
                        fetchUserProfile(fetch_other_user, "");
                        if (Processcase.equalsIgnoreCase(reject_friend_requests) || Processcase.equalsIgnoreCase(accept_friend_requests)) {
                            removeReqFromDb(user);
                        } else if (Processcase.equalsIgnoreCase(unfollow)) {
                            removeFollowingFromDb(user);
                        }
                    } else {
                        Toast.makeText(getActivity(), UtilHelper.getAPIError(String.valueOf(response.body())), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ProgressDialog.getInstance().dismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseObj> call, Throwable t) {
                t.printStackTrace();
                ProgressDialog.getInstance().dismiss();
            }
        });
    }

    private void removeFollowingFromDb(String user) {
        FollowingViewModel mViewModel = ViewModelProviders.of(this).get(FollowingViewModel.class);
        mViewModel.deleteUpdatedConnection(user);
    }

    private void removeReqFromDb(String user) {
        FriendReqViewModel mViewModel = ViewModelProviders.of(this).get(FriendReqViewModel.class);
        mViewModel.deleteUpdatedConnection(user);
    }

    private void updateUi(UserProfile userProfile) {
        if (userProfile.getEndPathImage() != null) {
            //display actual profile pic
            Log.d(TAG, "updateUi : " + userProfile.getUserId());
            Log.d(TAG, "updateUi: " + getProfileImageUrl(userProfile.getEndPathImage()));
            Log.d(TAG, "updateUi Gender : " + userProfile.getStrGender());
            loadProfilePic(getProfileImageUrl(userProfile.getEndPathImage()));
            if (userProfile.getStrGender().equalsIgnoreCase("M"))
                mBinding.radioMale.setChecked(true);
            if (userProfile.getStrGender().equalsIgnoreCase("F"))
                mBinding.radioFemale.setChecked(true);
            if (userProfile.getStrGender().equalsIgnoreCase("O"))
                mBinding.radioOthers.setChecked(true);
        } else {
            if (userProfile.getStrGender().equalsIgnoreCase("M")) {
                loadProfilePic(Constant.PRODUCTION_MALE_PROFILE_API);
                mBinding.radioMale.setChecked(true);
            } else if (userProfile.getStrGender().equalsIgnoreCase("F")) {
                loadProfilePic(Constant.PRODUCTION_FEMALE_PROFILE_API);
                mBinding.radioFemale.setChecked(true);
            } else if (userProfile.getStrGender().equalsIgnoreCase("O")) {
                mBinding.radioOthers.setChecked(true);
            }
        }

        Log.d(TAG, "Nationality Id: " + userProfile.getU_NationalityId());
        fetchStates(userProfile.getU_NationalityId());

        Log.d(TAG, "State Id: " + userProfile.getU_StateId());
        fetchDistricts(userProfile.getU_StateId());

        Log.d(TAG, "District Id: " + userProfile.getU_DistrictId());
        fetchCities(userProfile.getU_DistrictId());

        gender = userProfile.getStrGender();

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
                mBinding.etDob.setError("Required");
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

            userInfo();

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

        mBinding.userNameAutoCompleteTextView.setOnItemClickListener((parent, view, position, id) -> {
            final String name = ((TextView) view).getText().toString();
            mBinding.userNameAutoCompleteTextView.setTag(name);
            mBinding.userNameAutoCompleteTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_right_tick, 0);
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
                Log.i(TAG, "Language selected : " + key + " name: " + name);
                getLanguageTag();
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

    private void userInfo() {
        UserProfile userProfile = new UserProfile();
        Log.d(TAG, "Email : " + mBinding.etEmail.getText().toString().trim());
        Log.d(TAG, "Encrypted Email : " + AESSecurities.getInstance().encrypt(TinyDB.getInstance(getActivity()).getString(Constant.cf_key5), mBinding.etEmail.getText().toString().trim()));

        userProfileMap.put(Constant.u_Email_encrypted, AESSecurities.getInstance().encrypt(TinyDB.getInstance(getActivity()).getString(Constant.cf_key5), mBinding.etEmail.getText().toString().trim()));
        userProfileMap.put(Constant.u_mob_1_encrypted, AESSecurities.getInstance().encrypt(TinyDB.getInstance(getActivity()).getString(Constant.cf_key4), mBinding.etMobile.getText().toString().trim()));
        userProfileMap.put(Constant.u_birth_date_encrypted, AESSecurities.getInstance().encrypt(TinyDB.getInstance(getActivity()).getString(Constant.cf_key3), convertDateToDataBaseFormat(mBinding.etDob.getText().toString())));
        userProfileMap.put(Constant.u_Password_encrypted, AESSecurities.getInstance().encrypt(TinyDB.getInstance(getActivity()).getString(Constant.cf_key6), mBinding.etPassword.getText().toString().trim()));

        if (mBinding.userNameAutoCompleteTextView.getText().toString().trim().isEmpty())
            userProfileMap.put(Constant.userName, mBinding.userNameAutoCompleteTextView.getTag().toString());
        else
            userProfileMap.put(Constant.userName, mBinding.userNameAutoCompleteTextView.getText().toString().trim());
        //userProfileMap.put(Constant.userName, getEmptyStringIfNull(String.valueOf(mBinding.userNameAutoCompleteTextView.getTag())));

        userProfileMap.put(Constant.u_user_id, String.valueOf(userid));
        userProfileMap.put(Constant.u_app_version, Constant.APP_VERSION);
        userProfileMap.put(Constant.device_id, getDeviceId(getActivity()));
        userProfileMap.put(Constant.appName, Constant.APP_NAME);
        userProfileMap.put(Constant.u_first_name_encrypted, AESSecurities.getInstance().encrypt(TinyDB.getInstance(getActivity()).getString(Constant.cf_key1), mBinding.etFirstName.getText().toString().trim()));
        userProfileMap.put(Constant.u_last_name_encrypted, AESSecurities.getInstance().encrypt(TinyDB.getInstance(getActivity()).getString(Constant.cf_key2), mBinding.etLastName.getText().toString().trim()));
        userProfileMap.put(Constant.CASE, "n");
        userProfileMap.put(Constant.STATUS, "i");
        userProfileMap.put(Constant.u_tagline, mBinding.etTagLine.getText().toString().trim());

        userProfileMap.put(Constant.u_native_ct_id, getEmptyStringIfNull(String.valueOf(mBinding.cityAutocompleteView.getTag())));
        userProfileMap.put(Constant.u_native_s_id, getEmptyStringIfNull(String.valueOf(mBinding.stateAutocompleteView.getTag())));
        userProfileMap.put(Constant.u_native_dt_id, getEmptyStringIfNull(String.valueOf(mBinding.divisionAutocompleteView.getTag())));
        userProfileMap.put(Constant.u_nationality, getEmptyStringIfNull(String.valueOf(mBinding.nationalityAutocompleteView.getTag())));
        userProfileMap.put(Constant.u_gender, getSingleQuoteString(gender));
        Log.d(TAG, "Language Array : " + getLanguageArray(mBinding.langAutocompleteView.getTag()));
        userProfileMap.put(Constant.w_lm_id_array, getLanguageArray(mBinding.langAutocompleteView.getTag()));
        updateUserProfile(userProfileMap);
    }

    private void updateUserProfile(HashMap<String, String> userProfileMap) {
        Log.d(TAG, "updateUserProfile: " + userProfileMap);

        Log.d(TAG, "Focus First Name : " + AESSecurities.getInstance().decrypt(TinyDB.getInstance(getActivity()).getString(Constant.cf_key1), userProfileMap.get(Constant.u_first_name_encrypted)));
        Log.d(TAG, "Focus First Name : " + AESSecurities.getInstance().decrypt(TinyDB.getInstance(getActivity()).getString(Constant.cf_key2), userProfileMap.get(Constant.u_last_name_encrypted)));
        Log.d(TAG, "Focus Mobile : " + AESSecurities.getInstance().decrypt(TinyDB.getInstance(getActivity()).getString(Constant.cf_key4), userProfileMap.get(Constant.u_mob_1_encrypted)));

        ProgressDialog.getInstance().show(requireActivity());
        Call<UserProfileResponse> call = apiService.updateUserProfile(
                userProfileMap.get(Constant.u_user_id),
                userProfileMap.get(Constant.appName),
                userProfileMap.get(Constant.u_app_version),
                userProfileMap.get(Constant.device_id),
                userProfileMap.get(Constant.u_first_name_encrypted),
                userProfileMap.get(Constant.u_last_name_encrypted),
                userProfileMap.get(Constant.CASE),
                userProfileMap.get(Constant.u_mob_1_encrypted),
                userProfileMap.get(Constant.u_Email_encrypted),
                userProfileMap.get(Constant.STATUS),
                userProfileMap.get(Constant.u_Password_encrypted),
                userProfileMap.get(Constant.u_birth_date_encrypted),
                userProfileMap.get(Constant.u_tagline),

                String.valueOf(userProfileMap.get(Constant.u_native_ct_id)),
                String.valueOf(userProfileMap.get(Constant.u_native_s_id)),
                String.valueOf(userProfileMap.get(Constant.u_native_dt_id)),
                String.valueOf(userProfileMap.get(Constant.u_nationality)),
                String.valueOf(userProfileMap.get(Constant.w_lm_id_array)),
                userProfileMap.get(Constant.u_gender),
                userProfileMap.get(Constant.userName)
        );

        call.enqueue(new Callback<UserProfileResponse>() {
            @Override
            public void onResponse(Call<UserProfileResponse> call, Response<UserProfileResponse> response) {
                ProgressDialog.getInstance().dismiss();
                if (response != null && response.body() != null) {
                    if (response.body().getResponseCode().equals("200")) {
                        String displayName = mBinding.etFirstName.getText().toString().trim() + " " + mBinding.etLastName.getText().toString().trim();
                        new PreferenceManager(requireActivity()).saveString(Constant.DISPLAY_NAME, displayName);
                        new PreferenceManager(requireActivity()).saveString(Constant.GENDER, userProfileMap.get(Constant.u_gender));
                        fetchUserProfile(0, "FINISH");
                    } else {
                        showErrorDialog(requireActivity(), response.body().getResponseCode(), response.body().getMessage());
                    }
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
        Log.d(TAG, "loadProfilePic : " + url);
        //new PreferenceManager(getActivity()).saveString(Constant.PROFILE_PIC,url);
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


    private void fetchUserProfile(int call_from, String flag) {
        //ProgressDialog.getInstance().show(requireActivity());
        if (call_from == fetch_loged_in_user)
            call = apiService.fetchUserInfo(userid, getDeviceId(getActivity()), Constant.APP_NAME, Constant.APP_VERSION);
        else
            call = apiService.fetchOtherUsersInfo(mSettings.getUserId(), getDeviceId(getActivity()), Constant.APP_NAME, Constant.APP_VERSION, userid, "UP");

        call.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                //ProgressDialog.getInstance().dismiss();
                if (response.body() != null && response.body().getResponseCode().equals("200")) {
                    mViewModel.insert(response.body());
                    if (flag.equalsIgnoreCase("FINISH"))
                        showToast("Successfully updated profile info.");
                    //Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigate(R.id.nav_home);
                } else if (response.body().getResponseCode().equals("501")) {
                    resetSettingAndLogout();
                } else {
                    showErrorDialog(requireActivity(), response.body().getResponseCode(), "");
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                //ProgressDialog.getInstance().dismiss();
                Log.e(TAG, "onFailure UserProfile: " + t.getMessage());
                apiCallFailureDialog(t);
                showToast("Something went wrong!!");
                t.printStackTrace();
            }
        });
    }


    private void fetchLanguages() {
        try {
            ProgressDialog.getInstance().show(requireActivity());
            Call<Language> call = apiService.fetchLanguages(userid);
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
                    Log.e(TAG, "onFailure Languages: " + t.getMessage());
                    showToast("Something went wrong!!");
                    t.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchNationalities() {
        ProgressDialog.getInstance().show(requireActivity());
        Call<CountryResponse> call = apiService.fetchNationalities(userid);
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
                Log.e(TAG, "onFailure Nationalities: " + t.getMessage());
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
                Log.e(TAG, "onFailure States: " + t.getMessage());
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
                Log.e(TAG, "onFailure Districts: " + t.getMessage());
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
                Log.e(TAG, "onFailure City : " + t.getMessage());
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
        newCalendar.setTime(date != null ? date : new Date());
        DatePickerDialog dialog = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Dialog, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String formattedDate = year + "-" + (month + 1) + "-" + day;
                if (AppUtils.isEnteredDOBValid(year, (month + 1), day)) {
                    mBinding.etDob.setText(DateUtils.getFormattedDate(formattedDate));
                } else {
                    showToast("Minimum age required is 13 years.");
                    mBinding.etDob.setText("");
                }
            }
        }, //newCalendar.get(1990), newCalendar.get(1), newCalendar.get(Calendar.DAY_OF_MONTH));
                newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();

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
        ArrayAdapter<String> nationalityAdapter = new ArrayAdapter<>(mContext, R.layout.textview_dropdown, list);
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
        ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(mContext, R.layout.textview_dropdown, list);
        mBinding.stateAutocompleteView.setThreshold(0);
        mBinding.stateAutocompleteView.setAdapter(stateAdapter);
    }

    private void populateLanguage(Map<Integer, String> mMapLanguage) {
        List<String> list = new ArrayList<>(mMapLanguage.values());
        Collections.sort(list);
        Log.d(TAG, "populateLanguage: " + list.size());
        ArrayAdapter<String> languageAdapter = new ArrayAdapter<>(requireActivity(), R.layout.textview_dropdown, list);
        mBinding.langAutocompleteView.setThreshold(0);
        mBinding.langAutocompleteView.setTokenizer(new AppCompatMultiAutoCompleteTextView.CommaTokenizer());
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
                if (response != null && response.body() != null) {
                    if (response.body().getResponse().equals("200")) {
                        createVerifyOTPDialog(response.body().getNewOTP());
                    } else {
                        showErrorDialog(requireActivity(), response.body().getResponse(), response.body().getErrorMsg());
                    }
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
                Log.e(TAG, "onFailure onActivityResult: " + e.getMessage());
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
        initViews();

    }

    private void loadImage() {
        Intent i = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_REQUEST_LOAD_IMAGE_CODE);
    }

    private void generateVerifyUserName(HashMap<String, String> params) {
        ProgressDialog.getInstance().show(requireActivity());
        Log.d(TAG, "generateVerifyUserName: " + params);
        String strCase = params.get(Constant.CASE);
        Call<GenerateVerifyUserName> call = apiService.generateVerifyUserName(
                AESSecurities.getInstance().encrypt(masterKey, params.get(Constant.u_first_name)),
                AESSecurities.getInstance().encrypt(masterKey, params.get(Constant.u_last_name)),
                params.get(Constant.CASE),
                params.get(Constant.userName)
        );
        call.enqueue(new Callback<GenerateVerifyUserName>() {
            @Override
            public void onResponse(Call<GenerateVerifyUserName> call, Response<GenerateVerifyUserName> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.body() != null && response.body().getResponse().equals("200")) {
                    if (strCase.equals("G")) {
                        mViewModel.setUserNameData(response.body());
                        getUserNames(response.body());
                    } else if (strCase.equals("V")) {
                        mBinding.userNameAutoCompleteTextView.setTag(params.get(Constant.userName));
                        mBinding.userNameAutoCompleteTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_right_tick, 0);
                    }
                } else {
                    if (strCase.equals("V")) {
                        //mBinding.userNameAutoCompleteTextView.setTag("");
                    }
                    showErrorDialog(requireActivity(), response.body().getResponse(), "");
                }
            }

            @Override
            public void onFailure(Call<GenerateVerifyUserName> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                Log.e(TAG, "onFailure Languages: " + t.getMessage());
                showToast("Something went wrong!!");
                t.printStackTrace();
            }
        });
    }

    private void updateProfileImage(File imageFile) {
        //pass it like this
        //File file = new File("/storage/emulated/0/Download/Corrections 6.jpg");
        RequestBody requestFile =
                RequestBody.create(MediaType.parse("image/png"), imageFile);

        HashMap<String, String> params = new HashMap<>();
        params.put(Constant.u_user_id, String.valueOf(userid));
        params.put(Constant.device_id, getDeviceId(getActivity()));
        params.put(Constant.appName, Constant.APP_NAME);
        params.put(Constant.u_app_version, Constant.APP_VERSION);
        params.put(Constant.CASE, "N");

        RequestBody userId =
                RequestBody.create(MediaType.parse("multipart/form-data"), userid);
        RequestBody deviceId =
                RequestBody.create(MediaType.parse("multipart/form-data"), getDeviceId(getActivity()));
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
                    new PreferenceManager(requireActivity()).saveString(Constant.PROFILE_PIC, getProfileImageUrl(response.body().getW_user_profile_image_name()));
                } else {
                    showErrorDialog(requireActivity(), response.body().getResponse(), "");
                }
            }

            @Override
            public void onFailure(Call<VerifyResponse> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                t.printStackTrace();
                Log.e(TAG, "onFailure Upload Profile Pic: " + t.getMessage());
                showToast("Something went wrong!!");
            }
        });
    }

    private void getLanguageTag() {
        String keys = mBinding.langAutocompleteView.getText()
                .toString();
        Log.d(TAG, "getLanguageTag Keys : " + keys);
        String languageKey = "";
        for (String s : keys.split(",", -1)) {
            languageKey = languageKey + "," + getKeyFromValue(mMapLanguage, s.trim());
            Log.d(TAG, "Key : " + getKeyFromValue(mMapLanguage, s.trim()));
        }
        Log.d(TAG, "getLanguageTag Final : " + languageKey.replace("-1", ""));
        mBinding.langAutocompleteView.setTag(languageKey.replace("-1", ""));
    }

   /* @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause Called: ");
        userInfo();
    }*/
}
