package com.jangletech.qoogol;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.jangletech.qoogol.activities.BaseActivity;
import com.jangletech.qoogol.activities.SignInActivity;
import com.jangletech.qoogol.databinding.ActivitySignupBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.dialog.UniversalDialog;
import com.jangletech.qoogol.model.ClassData;
import com.jangletech.qoogol.model.Classes;
import com.jangletech.qoogol.model.Country;
import com.jangletech.qoogol.model.CountryResponse;
import com.jangletech.qoogol.model.Course;
import com.jangletech.qoogol.model.CourseResponse;
import com.jangletech.qoogol.model.Degree;
import com.jangletech.qoogol.model.DegreeResponse;
import com.jangletech.qoogol.model.Institute;
import com.jangletech.qoogol.model.InstituteResponse;
import com.jangletech.qoogol.model.MobileOtp;
import com.jangletech.qoogol.model.State;
import com.jangletech.qoogol.model.University;
import com.jangletech.qoogol.model.UniversityResponse;
import com.jangletech.qoogol.model.signup.SignUpRequestDto;
import com.jangletech.qoogol.model.signup.SignUpResponseDto;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.GenericTextWatcher;
import com.jangletech.qoogol.util.UtilHelper;
import com.mukesh.OtpView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jangletech.qoogol.util.Constant.board;
import static com.jangletech.qoogol.util.Constant.board_id;
import static com.jangletech.qoogol.util.Constant.country;
import static com.jangletech.qoogol.util.Constant.country_id;
import static com.jangletech.qoogol.util.Constant.course;
import static com.jangletech.qoogol.util.Constant.cyNum;
import static com.jangletech.qoogol.util.Constant.degree;
import static com.jangletech.qoogol.util.Constant.degree_id;
import static com.jangletech.qoogol.util.Constant.degree_name;
import static com.jangletech.qoogol.util.Constant.duration;
import static com.jangletech.qoogol.util.Constant.email;
import static com.jangletech.qoogol.util.Constant.first_name;
import static com.jangletech.qoogol.util.Constant.institute;
import static com.jangletech.qoogol.util.Constant.is_email_verified;
import static com.jangletech.qoogol.util.Constant.is_mobile_verified;
import static com.jangletech.qoogol.util.Constant.last_name;
import static com.jangletech.qoogol.util.Constant.mobile_no;
import static com.jangletech.qoogol.util.Constant.mobile_number;
import static com.jangletech.qoogol.util.Constant.password;
import static com.jangletech.qoogol.util.Constant.state;
import static com.jangletech.qoogol.util.Constant.state_id;

public class SignUpActivity extends BaseActivity
        implements View.OnClickListener, UniversalDialog.DialogButtonClickListener {

    private ActivitySignupBinding mBinding;
    private SignUpViewModel mViewModel;
    private UniversalDialog.DialogButtonClickListener uni;
    SignUpRequestDto signUpRequestDto;
    ApiInterface apiService = ApiClient.getInstance().getApi();
    private static final String TAG = "SignUpActivity";

    boolean isValid = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_signup);
        mViewModel = ViewModelProviders.of(this).get(SignUpViewModel.class);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        signUpRequestDto = new SignUpRequestDto();
        setTitle(getResources().getString(R.string.sign_up_title));
        uni = this;
        confirmPasswordCheck();
        setTextWatcher();
        setListeners();
        fetchCountryData();
        fetchDegreeData();
        fetchUniversityData(0,0);
        fetchInstituteData(0);


        mBinding.selectAutocompleteView.setOnItemClickListener((parent, view, position, id) -> {

        });
    }


    private void setListeners() {
        mBinding.btnSignUp.setOnClickListener(this);
        mBinding.tvMobileVerify.setOnClickListener(this);
        mBinding.tvEmailVerify.setOnClickListener(this);
        mBinding.countryAutocompleteView.setOnItemClickListener((parent, view, position, id) -> {
            final String name = ((TextView) view).getText().toString();
            int key = UtilHelper.getKeyFromValue(mViewModel.mMapCountry, name);
            if (key != -1) {
                mBinding.countryAutocompleteView.setTag(key);
                fetchStateData(key);
            }
        });

        mBinding.stateAutocompleteView.setOnItemClickListener((parent, view, position, id) -> {
            final String state = ((TextView) view).getText().toString();
            final String country = mBinding.countryAutocompleteView.getText().toString();
            int state_id = UtilHelper.getKeyFromValue(mViewModel.mMapState, state);
            int country_id = UtilHelper.getKeyFromValue(mViewModel.mMapCountry, country);
            if (state_id != -1 && country_id != -1) {
                mBinding.stateAutocompleteView.setTag(state_id);
                fetchUniversityData(country_id, state_id);
            }
        });

        mBinding.universityBoardAutocompleteView.setOnItemClickListener((parent, view, position, id) -> {
            final String name = ((TextView) view).getText().toString();
            int key = UtilHelper.getKeyFromValue(mViewModel.mMapUniversity, name);
            if (key != -1) {
                //signUpData.setBoard(key);
                mBinding.universityBoardAutocompleteView.setTag(key);
                fetchInstituteData(key);
            }
        });

        mBinding.instituteAutocompleteView.setOnItemClickListener((parent, view, position, id) -> {
            final String name = ((TextView) view).getText().toString();
            int key = UtilHelper.getKeyFromValue(mViewModel.mMapInstitute, name);
            if (key != -1) {
                mBinding.instituteAutocompleteView.setTag(key);
            }
        });


        mBinding.degreeAutocompleteView.setOnItemClickListener((parent, view, position, id) -> {
            final String name = ((TextView) view).getText().toString();
            int key = UtilHelper.getKeyFromValue(mViewModel.mMapDegree, name);
            if (key != -1) {
                mBinding.degreeAutocompleteView.setTag(key);
                fetchCourseData(key);
            }
        });

        mBinding.courseAutocompleteView.setOnItemClickListener((parent, view, position, id) -> {
            final String name = ((TextView) view).getText().toString();
            int key = UtilHelper.getKeyFromValue(mViewModel.mMapCourse, name);
            if (key != -1) {
                //signUpData.setCourse(key);
                Log.d(TAG, "courseAutocompleteView value : " + key);
                mBinding.courseAutocompleteView.setTag(key);
                fetchClassData(name);
            }
        });

        mBinding.classAutocompleteView.setOnItemClickListener((parent, view, position, id) -> {
            final String name = ((TextView) view).getText().toString();
            int key = UtilHelper.getKeyFromValue(mViewModel.mMapClass, name);
            if (key != -1) {
                //signUpData.setCyNum(key);
                mBinding.classAutocompleteView.setTag(key);
            }
        });
    }


    private boolean validateSignUpForm() {
        isValid = true;
        if (TextUtils.isEmpty(mBinding.tilFirstName.getEditText().getText())) {
            mBinding.tilFirstName.setError(getResources().getString(R.string.empty_first_name));
            mBinding.etFirstName.requestFocus();
            isValid = false;
        } else if (TextUtils.isEmpty(mBinding.tilLastName.getEditText().getText())) {
            mBinding.tilLastName.setError(getResources().getString(R.string.empty_last_name));
            mBinding.etLastName.requestFocus();
            isValid = false;
        } else if (TextUtils.isEmpty(mBinding.tilMobile.getEditText().getText())) {
            mBinding.tilMobile.setError(getResources().getString(R.string.empty_mobile));
            mBinding.etMobile.requestFocus();
            isValid = false;
        } else if (TextUtils.isEmpty(mBinding.tilEmail.getEditText().getText())) {
            mBinding.tilEmail.setError(getResources().getString(R.string.empty_email));
            mBinding.etEmail.requestFocus();
            isValid = false;
        } else if (!UtilHelper.isValidEmail(mBinding.tilEmail.getEditText().getText().toString())) {
            mBinding.tilEmail.setError(getResources().getString(R.string.valid_email));
            mBinding.etEmail.requestFocus();
            isValid = false;
        } else if (mBinding.countryAutocompleteView.getTag() == null) {
            Toast.makeText(this, R.string.select_country, Toast.LENGTH_SHORT).show();
            isValid = false;
        } else if (mBinding.stateAutocompleteView.getTag() == null) {
            Toast.makeText(this, "Please select state.", Toast.LENGTH_SHORT).show();
            isValid = false;
        } else if (mBinding.universityBoardAutocompleteView.getTag() == null) {
            Toast.makeText(this, "Please select university/board", Toast.LENGTH_SHORT).show();
            isValid = false;
        } else if (mBinding.instituteAutocompleteView.getTag() == null) {
            Toast.makeText(this, "Please select institute", Toast.LENGTH_SHORT).show();
            isValid = false;
        } else if (mBinding.degreeAutocompleteView.getTag() == null) {
            Toast.makeText(this, "Please select degree", Toast.LENGTH_SHORT).show();
            isValid = false;
        } else if (mBinding.courseAutocompleteView.getTag() == null) {
            Toast.makeText(this, "Please select course", Toast.LENGTH_SHORT).show();
            isValid = false;
        } else if (mBinding.classAutocompleteView.getTag() == null) {
            Toast.makeText(this, "Please select class", Toast.LENGTH_SHORT).show();
            isValid = false;
        } else if (TextUtils.isEmpty(mBinding.tilCreatePassword.getEditText().getText())) {
            mBinding.tilCreatePassword.setError(getResources().getString(R.string.empty_password));
            isValid = false;
        } else if (TextUtils.isEmpty(mBinding.tilConfirmPassword.getEditText().getText())) {
            mBinding.tilConfirmPassword.setError(getResources().getString(R.string.empty_confirm_password));
            isValid = false;
        }
        return isValid;
    }

    private void populateCountries(Map<Integer, String> mMapCountry) {
        List<String> list = new ArrayList<>(mMapCountry.values());
        Collections.sort(list);
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(this,
                R.layout.autocomplete_list_item, list);
        mBinding.countryAutocompleteView.setAdapter(countryAdapter);
    }


    private void populateStates(Map<Integer, String> mMapState) {
        List<String> list = new ArrayList<>(mMapState.values());
        Collections.sort(list);
        ArrayAdapter<String> stateAdapter = new ArrayAdapter<String>(this,
                R.layout.autocomplete_list_item, list);
        mBinding.stateAutocompleteView.setAdapter(stateAdapter);
    }

    private void populateSelect() {
        String[] select = getResources().getStringArray(R.array.Select);
        ArrayAdapter<String> selectAdapter = new ArrayAdapter<String>(this,
                R.layout.autocomplete_list_item, select);
        mBinding.selectAutocompleteView.setAdapter(selectAdapter);
    }

    private void populateUniversityBoard(Map<Integer, String> mMapUniversity) {
        List<String> list = new ArrayList<>(mMapUniversity.values());
        Collections.sort(list);
        ArrayAdapter<String> universityAdapter = new ArrayAdapter<String>(this,
                R.layout.autocomplete_list_item, list);
        mBinding.universityBoardAutocompleteView.setAdapter(universityAdapter);
    }

    private void populateInstitutre(Map<Integer, String> mMapInstitute) {
        List<String> list = new ArrayList<>(mMapInstitute.values());
        Collections.sort(list);
        ArrayAdapter<String> instituteAdapter = new ArrayAdapter<String>(this,
                R.layout.autocomplete_list_item, list);
        mBinding.instituteAutocompleteView.setAdapter(instituteAdapter);
    }

    private void populateDegrees(Map<Integer, String> mMapDegree) {
        List<String> list = new ArrayList<>(mMapDegree.values());
        Collections.sort(list);
        ArrayAdapter<String> degreeAdapter = new ArrayAdapter<String>(this,
                R.layout.autocomplete_list_item, list);
        mBinding.degreeAutocompleteView.setAdapter(degreeAdapter);
    }

    private void populateCourse(Map<Integer, String> mMapCourse) {
        List<String> list = new ArrayList<>(mMapCourse.values());
        Collections.sort(list);
        ArrayAdapter<String> courseAdapter = new ArrayAdapter<String>(this,
                R.layout.autocomplete_list_item, list);
        mBinding.courseAutocompleteView.setAdapter(courseAdapter);
    }

    private void populateClass(Map<Integer, String> mMapClass) {
        List<String> list = new ArrayList<>(mMapClass.values());
        Collections.sort(list);
        ArrayAdapter<String> classAdapter = new ArrayAdapter<String>(this,
                R.layout.autocomplete_list_item, list);
        mBinding.classAutocompleteView.setAdapter(classAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        populateSelect();
    }

    private void fetchCountryData() {
        ProgressDialog.getInstance().show(this);
        Call<CountryResponse> call = apiService.getCountries();
        call.enqueue(new Callback<CountryResponse>() {
            @Override
            public void onResponse(Call<CountryResponse> call, retrofit2.Response<CountryResponse> response) {
                try {
                    List<Country> list = response.body().getMasterDataList();
                    mViewModel.setCountryList(list);
                    if (list != null && list.size() > 0) {
                        mViewModel.mMapCountry = new HashMap<>();
                        for (Country country : list) {
                            mViewModel.mMapCountry.put(country.getCountryId(), country.getCountryName());
                        }
                        ProgressDialog.getInstance().dismiss();
                        populateCountries(mViewModel.mMapCountry);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ProgressDialog.getInstance().dismiss();
                }
            }

            @Override
            public void onFailure(Call<CountryResponse> call, Throwable t) {
                t.printStackTrace();
                ProgressDialog.getInstance().dismiss();
            }
        });
    }

    public void fetchStateData(int countryId) {
        ProgressDialog.getInstance().show(this);
        Map<String, Integer> requestBody = new HashMap<>();
        requestBody.put("countryId", countryId);
        Call<List<State>> call = apiService.getStates(requestBody);
        call.enqueue(new Callback<List<State>>() {
            @Override
            public void onResponse(Call<List<State>> call, retrofit2.Response<List<State>> response) {
                ProgressDialog.getInstance().dismiss();
                try {
                    List<State> list = response.body();
                    mViewModel.setStateList(list);
                    if (list != null && list.size() > 0) {
                        mViewModel.mMapState = new HashMap<>();
                        for (State state : list) {
                            mViewModel.mMapState.put(Integer.valueOf(state.getStateId()), state.getStateName());
                        }
                        ProgressDialog.getInstance().dismiss();
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

    private void fetchUniversityData(int country, int state) {
        ProgressDialog.getInstance().show(this);
        Call<UniversityResponse> call = apiService.getUniversity();
        call.enqueue(new Callback<UniversityResponse>() {
            @Override
            public void onResponse(Call<UniversityResponse> call, Response<UniversityResponse> response) {
                ProgressDialog.getInstance().dismiss();
                try {
                    List<University> list = response.body().getMasterDataList();
                    mViewModel.setUniversityList(list);
                    if (list != null && list.size() > 0) {
                        mViewModel.mMapUniversity = new HashMap<>();
                        for (University university : list) {
                            mViewModel.mMapUniversity.put(Integer.valueOf(university.getUnivBoardId()), university.getName());
                        }
                        populateUniversityBoard(mViewModel.mMapUniversity);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ProgressDialog.getInstance().dismiss();
                }
            }

            @Override
            public void onFailure(Call<UniversityResponse> call, Throwable t) {
                t.printStackTrace();
                ProgressDialog.getInstance().dismiss();
            }
        });
    }

    private void fetchInstituteData(int university) {
        ProgressDialog.getInstance().show(this);
        Call<InstituteResponse> call = apiService.getInstitute();
        call.enqueue(new Callback<InstituteResponse>() {
            @Override
            public void onResponse(Call<InstituteResponse> call, Response<InstituteResponse> response) {
                try {
                    List<Institute> list = response.body().getMasterDataList();
                    mViewModel.setInstituteList(list);
                    if (list != null && list.size() > 0) {
                        mViewModel.mMapInstitute = new HashMap<>();
                        for (Institute institute : list) {
                            mViewModel.mMapInstitute.put(Integer.valueOf(institute.getInstOrgId()), institute.getName());
                        }
                        ProgressDialog.getInstance().dismiss();
                        populateInstitutre(mViewModel.mMapInstitute);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ProgressDialog.getInstance().dismiss();

                }
            }

            @Override
            public void onFailure(Call<InstituteResponse> call, Throwable t) {
                t.printStackTrace();
                ProgressDialog.getInstance().dismiss();
            }
        });
    }

    private void fetchDegreeData() {
        ProgressDialog.getInstance().show(this);
        Call<DegreeResponse> call = apiService.getDegrees();
        call.enqueue(new Callback<DegreeResponse>() {
            @Override
            public void onResponse(Call<DegreeResponse> call, retrofit2.Response<DegreeResponse> response) {
                try {
                    List<Degree> list = response.body().getMasterDataList() ;
                    mViewModel.setDegreeList(list);
                    if (list != null && list.size() > 0) {
                        mViewModel.mMapDegree = new HashMap<>();
                        for (Degree degree : list) {
                            mViewModel.mMapDegree.put(degree.getDegreeId(), degree.getName());
                        }
                        ProgressDialog.getInstance().dismiss();
                        populateDegrees(mViewModel.mMapDegree);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ProgressDialog.getInstance().dismiss();
                }
            }

            @Override
            public void onFailure(Call<DegreeResponse> call, Throwable t) {
                t.printStackTrace();
                ProgressDialog.getInstance().dismiss();
            }
        });
    }


    private void fetchCourseData(int degree) {
        ProgressDialog.getInstance().show(this);
        Map<String, Integer> requestBody = new HashMap<>();
        requestBody.put(Constant.co_dm_id, degree);
        Call<CourseResponse> call = apiService.getCourses(null);
        call.enqueue(new Callback<CourseResponse>() {
            @Override
            public void onResponse(Call<CourseResponse> call, Response<CourseResponse> response) {
                try {
                    List<Course> list = response.body().getMasterDataList();
                    mViewModel.setCourseList(list);
                    if (list != null && list.size() > 0) {
                        mViewModel.mMapCourse = new HashMap<>();
                        for (Course course : list) {
                            mViewModel.mMapCourse.put(Integer.valueOf(course.getCourseId()), course.getName());
                        }
                        ProgressDialog.getInstance().dismiss();
                        populateCourse(mViewModel.mMapCourse);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ProgressDialog.getInstance().dismiss();
                }
            }

            @Override
            public void onFailure(Call<CourseResponse> call, Throwable t) {
                t.printStackTrace();
                ProgressDialog.getInstance().dismiss();
            }
        });
    }

    private void fetchClassData(String course_name) {
        ProgressDialog.getInstance().show(this);
        Map<String, Object> requestBody = new HashMap<>();

        requestBody.put(degree_name, course_name);
        Call<Classes> call = apiService.getClasses(null);
        call.enqueue(new Callback<Classes>() {
            @Override
            public void onResponse(Call<Classes> call, Response<Classes> response) {
                try {
                    List<ClassData> list = response.body().getObject();
                    mViewModel.setClassList(list);
                    if (list != null && list.size() > 0) {
                        mViewModel.mMapClass = new HashMap<>();
                        for (ClassData classitem : list) {
                            mViewModel.mMapClass.put(Integer.valueOf(classitem.getValue()), classitem.getDispText());
                        }
                        ProgressDialog.getInstance().dismiss();
                        populateClass(mViewModel.mMapClass);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ProgressDialog.getInstance().dismiss();
                }
            }

            @Override
            public void onFailure(Call<Classes> call, Throwable t) {
                Log.i(TAG, t.toString());
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSignUp:
                if (validateSignUpForm()) {
                    callSignUpApi();
                }
                break;
            case R.id.tvMobileVerify:
                if (!TextUtils.isEmpty(mBinding.tilMobile.getEditText().getText())) {
                    callMobileVerifyApi();
                } else {
                    mBinding.tilMobile.setError(getResources().getString(R.string.empty_mobile));
                }
                break;

            case R.id.tvEmailVerify:
                if (!TextUtils.isEmpty(mBinding.tilEmail.getEditText().getText())) {
                    callEmailVerifyApi();
                } else {
                    mBinding.tilEmail.setError(getResources().getString(R.string.empty_email));
                }
                break;
        }
    }

    private void callMobileVerifyApi() {
        ProgressDialog.getInstance().show(this);
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put(mobile_number, mBinding.tilMobile.getEditText().getText().toString());
        Call<MobileOtp> call = apiService.getMobileOtp(requestBody);
        call.enqueue(new Callback<MobileOtp>() {
            @Override
            public void onResponse(Call<MobileOtp> call, Response<MobileOtp> response) {
                try {
                    if (response.body() != null && response.body().getStatusCode().equalsIgnoreCase("1")) {
                        ProgressDialog.getInstance().dismiss();
                        createVerifySmsOtpDialog(response.body().getObject());
                    } else {
                        Toast.makeText(SignUpActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ProgressDialog.getInstance().dismiss();
                }
            }

            @Override
            public void onFailure(Call<MobileOtp> call, Throwable t) {
                t.printStackTrace();
                ProgressDialog.getInstance().dismiss();
            }
        });
    }

    private void callEmailVerifyApi() {
        ProgressDialog.getInstance().show(this);
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put(email, mBinding.tilEmail.getEditText().getText().toString());
        Call<MobileOtp> call = apiService.getEmailOtp(requestBody);
        call.enqueue(new Callback<MobileOtp>() {
            @Override
            public void onResponse(Call<MobileOtp> call, Response<MobileOtp> response) {
                try {
                    if (response.body().getStatusCode().equalsIgnoreCase("1")) {
                        ProgressDialog.getInstance().dismiss();
                        createVerifyEmailOtpDialog(response.body().getObject());
                    } else {
                        //Todo Fixed Universal Dialog Issue
                       /* UniversalDialog universalDialog = new UniversalDialog(getApplicationContext(),"Error",
                                response.body().getMessage(),
                                "", "Ok",uni);
                        universalDialog.show();*/
                        Toast.makeText(SignUpActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ProgressDialog.getInstance().dismiss();
                }
            }

            @Override
            public void onFailure(Call<MobileOtp> call, Throwable t) {
                t.printStackTrace();
                ProgressDialog.getInstance().dismiss();
            }
        });
    }


    private void callSignUpApi() {

        ProgressDialog.getInstance().show(this);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put(first_name, mBinding.etFirstName.getText().toString());
        requestBody.put(last_name, mBinding.etLastName.getText().toString());
        requestBody.put(email, mBinding.etEmail.getText().toString());
        requestBody.put(mobile_no, mBinding.etMobile.getText().toString());
        requestBody.put(password, mBinding.etCreatePassword.getText().toString());
        requestBody.put(country, mBinding.countryAutocompleteView.getTag());
        requestBody.put(state, mBinding.stateAutocompleteView.getTag());
        requestBody.put(board, mBinding.universityBoardAutocompleteView.getTag());
        requestBody.put(institute, mBinding.instituteAutocompleteView.getTag());
        requestBody.put(degree, mBinding.degreeAutocompleteView.getTag());
        requestBody.put(course, mBinding.courseAutocompleteView.getTag());
        requestBody.put(cyNum, mBinding.classAutocompleteView.getTag());
        requestBody.put(is_mobile_verified, true);
        requestBody.put(is_email_verified, true);


        try {
            Call<SignUpResponseDto> call = apiService.signUpApi(requestBody);
            call.enqueue(new Callback<SignUpResponseDto>() {
                @Override
                public void onResponse(Call<SignUpResponseDto> call, Response<SignUpResponseDto> response) {
                    Log.d(TAG, "onResponse SignUpResponseDto : " + response.body());
                    ProgressDialog.getInstance().dismiss();
                    if (response.body() != null && response.body().getStatusCode() == 1) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this, R.style.AlertDialogStyle);
                        builder.setTitle("Success");
                        builder.setMessage("Sign-up successfully, Please sing in with your credentials.");
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        }).setCancelable(false).show();
                    } else {
                        Toast.makeText(SignUpActivity.this, "Error : " + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<SignUpResponseDto> call, Throwable t) {
                    t.printStackTrace();
                    ProgressDialog.getInstance().dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            ProgressDialog.getInstance().dismiss();
        }
    }

    private void setTextWatcher() {
        mBinding.etFirstName.addTextChangedListener(new GenericTextWatcher(mBinding.tilFirstName, this));
        mBinding.etLastName.addTextChangedListener(new GenericTextWatcher(mBinding.tilLastName, this));
        mBinding.etMobile.addTextChangedListener(new GenericTextWatcher(mBinding.tilMobile, this));
        mBinding.etEmail.addTextChangedListener(new GenericTextWatcher(mBinding.tilEmail, this));
        mBinding.etCreatePassword.addTextChangedListener(new GenericTextWatcher(mBinding.tilCreatePassword, this));
    }

    private void confirmPasswordCheck() {
        mBinding.tilConfirmPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(mBinding.etConfirmPassword.getText().toString().trim())) {
                    mBinding.tilConfirmPassword.setError("Please re-enter password");
                } else if (!(mBinding.etCreatePassword.getText().toString().trim().equals(mBinding.etConfirmPassword.getText().toString().trim()))) {
                    mBinding.tilConfirmPassword.setError("Password Not Matched.");
                } else {
                    mBinding.tilConfirmPassword.setError(null);
                    if (mBinding.tilConfirmPassword.isErrorEnabled()) {
                        mBinding.tilConfirmPassword.setErrorEnabled(false);
                    }
                }
            }
        });
    }

    private void createVerifySmsOtpDialog(String otp) {
        Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.otp_layout);
        dialog.setCancelable(false);
        OtpView otpText = dialog.findViewById(R.id.otp_view);
        ImageView close = dialog.findViewById(R.id.btnClose);

        TextView textViewMsg = dialog.findViewById(R.id.tvDialogMsg);
        textViewMsg.setText(getResources().getString(R.string.verify_mobile_dialog_msg));

        close.setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.btnValidate).setOnClickListener(v -> {
            if (!otpText.getText().toString().isEmpty()) {
                if (otp.equalsIgnoreCase(otpText.getText().toString())) {
                    signUpRequestDto.setMobileVerified(true);
                    mBinding.tvMobileVerify.setText("Verified");
                    mBinding.tvMobileVerify.setClickable(false);
                    mBinding.etMobile.setEnabled(false);
                    mBinding.tvMobileVerify.setTextColor(getResources().getColor(R.color.color_green));
                } else {
                    signUpRequestDto.setMobileVerified(false);
                }
            }
            dialog.dismiss();
        });
        dialog.show();
    }

    private void createVerifyEmailOtpDialog(String otp) {
        Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.otp_layout);
        dialog.setCancelable(false);
        OtpView otpText = dialog.findViewById(R.id.otp_view);
        ImageView close = dialog.findViewById(R.id.btnClose);
        TextView textViewMsg = dialog.findViewById(R.id.tvDialogMsg);

        textViewMsg.setText(getResources().getString(R.string.verify_email_dialog_msg));

        close.setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.btnValidate).setOnClickListener(v -> {
            if (!otpText.getText().toString().isEmpty()) {
                if (otp.equalsIgnoreCase(otpText.getText().toString())) {
                    signUpRequestDto.setMobileVerified(true);
                    mBinding.tvMobileVerify.setText("Verified");
                    mBinding.tvMobileVerify.setClickable(false);
                    mBinding.etMobile.setEnabled(false);
                    mBinding.tvEmailVerify.setTextColor(getResources().getColor(R.color.color_green));
                } else {
                    signUpRequestDto.setEmailVerified(false);
                }
            }
            dialog.dismiss();
        });
        dialog.show();
    }

    @Override
    public void onPositiveButtonClick() {

    }
}
