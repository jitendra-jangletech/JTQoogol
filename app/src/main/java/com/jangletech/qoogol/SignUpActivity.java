package com.jangletech.qoogol;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.jangletech.qoogol.databinding.ActivitySignupBinding;
import com.jangletech.qoogol.model.ClassData;
import com.jangletech.qoogol.model.Country;
import com.jangletech.qoogol.model.Course;
import com.jangletech.qoogol.model.Degree;
import com.jangletech.qoogol.model.Institute;
import com.jangletech.qoogol.model.MobileOtp;
import com.jangletech.qoogol.model.SignUpData;
import com.jangletech.qoogol.model.State;
import com.jangletech.qoogol.model.University;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
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
import static com.jangletech.qoogol.util.Constant.last_name;
import static com.jangletech.qoogol.util.Constant.mobile_no;
import static com.jangletech.qoogol.util.Constant.mobile_number;
import static com.jangletech.qoogol.util.Constant.password;
import static com.jangletech.qoogol.util.Constant.state;
import static com.jangletech.qoogol.util.Constant.state_id;

public class SignUpActivity extends BaseActivity implements View.OnClickListener {

    private ActivitySignupBinding mBinding;
    private SignUpViewModel mViewModel;
    ApiInterface apiService = ApiClient.getInstance().getApi();
    private static final String TAG = "SignUpActivity";
    boolean isValid = true;
    SignUpData signUpData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_signup);
        mViewModel = ViewModelProviders.of(this).get(SignUpViewModel.class);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        signUpData = new SignUpData();
        setTitle(getResources().getString(R.string.sign_up_title));
        confirmPasswordCheck();
        setTextWatcher();
        setListeners();
        fetchCountryData();
        fetchDegreeData();
        createVerifyOTPDialog();



        mBinding.selectAutocompleteView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(SignUpActivity.this, "" + parent.getSelectedItem(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setListeners() {
        mBinding.btnSignUp.setOnClickListener(this);
        mBinding.countryAutocompleteView.setOnItemClickListener((parent, view, position, id) -> {
            final String name = ((TextView) view).getText().toString();
            int key = UtilHelper.getKeyFromValue(mViewModel.mMapCountry, name);
            if (key != -1) {
                mBinding.countryAutocompleteView.setTag(key);
                fetchStateData(key);
                signUpData.setCountry(key);
            }
        });

        mBinding.stateAutocompleteView.setOnItemClickListener((parent, view, position, id) -> {
            final String state = ((TextView) view).getText().toString();
            final String country = mBinding.countryAutocompleteView.getText().toString();
            int state_id = UtilHelper.getKeyFromValue(mViewModel.mMapState, state);
            int country_id = UtilHelper.getKeyFromValue(mViewModel.mMapCountry, country);
            if (state_id != -1 && country_id != -1) {
                signUpData.setState(state_id);
                mBinding.stateAutocompleteView.setTag(state_id);
                fetchUniversityData(country_id, state_id);
            }
        });

        mBinding.universityBoardAutocompleteView.setOnItemClickListener((parent, view, position, id) -> {
            final String name = ((TextView) view).getText().toString();
            int key = UtilHelper.getKeyFromValue(mViewModel.mMapUniversity, name);
            if (key != -1) {
                signUpData.setBoard(key);
                mBinding.universityBoardAutocompleteView.setTag(key);
                fetchInstituteData(key);
            }
        });

        mBinding.degreeAutocompleteView.setOnItemClickListener((parent, view, position, id) -> {
            final String name = ((TextView) view).getText().toString();
            int key = UtilHelper.getKeyFromValue(mViewModel.mMapDegree, name);
            if (key != -1) {
                signUpData.setDegree(key);
                mBinding.degreeAutocompleteView.setTag(key);
                fetchCourseData(key);
            }
        });

        mBinding.courseAutocompleteView.setOnItemClickListener((parent, view, position, id) -> {
            final String name = ((TextView) view).getText().toString();
            int key = UtilHelper.getKeyFromValue(mViewModel.mMapCourse, name);
            if (key != -1) {
                signUpData.setCourse(key);
                mBinding.courseAutocompleteView.setTag(key);
                fetchClassData(name, mViewModel.getCourseList().getValue().get(position).getDuration());
            }
        });

        mBinding.classAutocompleteView.setOnItemClickListener((parent, view, position, id) -> {
            final String name = ((TextView) view).getText().toString();
            int key = UtilHelper.getKeyFromValue(mViewModel.mMapClass, name);
            if (key != -1) {
                signUpData.setCyNum(key);
                mBinding.classAutocompleteView.setTag(key);
            }
        });
    }


    private boolean validateSignUpForm() {
        isValid = true;

        if (TextUtils.isEmpty(mBinding.tilFirstName.getEditText().getText())) {
            mBinding.tilFirstName.setError(getResources().getString(R.string.empty_first_name));
            isValid = false;
        }
        if (TextUtils.isEmpty(mBinding.tilLastName.getEditText().getText())) {
            mBinding.tilLastName.setError(getResources().getString(R.string.empty_last_name));
            isValid = false;
        }
        if (TextUtils.isEmpty(mBinding.tilMobile.getEditText().getText())) {
            mBinding.tilMobile.setError(getResources().getString(R.string.empty_mobile));
            isValid = false;
        }
        if (TextUtils.isEmpty(mBinding.tilEmail.getEditText().getText())) {
            mBinding.tilEmail.setError(getResources().getString(R.string.empty_email));
            isValid = false;
        } else if (!UtilHelper.isValidEmail(mBinding.tilEmail.getEditText().getText().toString())) {
            mBinding.tilEmail.setError(getResources().getString(R.string.valid_email));
            isValid = false;
        }
        if (TextUtils.isEmpty(mBinding.tilCreatePassword.getEditText().getText())) {
            mBinding.tilCreatePassword.setError(getResources().getString(R.string.empty_password));
            isValid = false;
        }
        if (TextUtils.isEmpty(mBinding.tilConfirmPassword.getEditText().getText())) {
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
        Call<List<Country>> call = apiService.getCountries();
        call.enqueue(new Callback<List<Country>>() {
            @Override
            public void onResponse(Call<List<Country>> call, retrofit2.Response<List<Country>> response) {
                try {
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
                }
            }

            @Override
            public void onFailure(Call<List<Country>> call, Throwable t) {
                Log.i("", t.toString());
            }
        });
    }

    public void fetchStateData(int countryId) {
        Map<String, Integer> requestBody = new HashMap<>();
        requestBody.put("countryId", countryId);
        Call<List<State>> call = apiService.getStates(requestBody);
        call.enqueue(new Callback<List<State>>() {
            @Override
            public void onResponse(Call<List<State>> call, retrofit2.Response<List<State>> response) {
                try {
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
                }
            }

            @Override
            public void onFailure(Call<List<State>> call, Throwable t) {
                Log.i(TAG, t.toString());
            }
        });
    }

    private void fetchUniversityData(int country, int state) {
        Map<String, Integer> requestBody = new HashMap<>();
        requestBody.put(country_id, country);
        requestBody.put(state_id, state);
        Call<List<University>> call = apiService.getUniversity(requestBody);
        call.enqueue(new Callback<List<University>>() {
            @Override
            public void onResponse(Call<List<University>> call, Response<List<University>> response) {
                try {
                    List<University> list = response.body();
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
                }
            }

            @Override
            public void onFailure(Call<List<University>> call, Throwable t) {
                Log.i(TAG, t.toString());
            }
        });
    }

    private void fetchInstituteData(int university) {
        Map<String, Integer> requestBody = new HashMap<>();
        requestBody.put(board_id, university);
        Call<List<Institute>> call = apiService.getInstitute(requestBody);
        call.enqueue(new Callback<List<Institute>>() {
            @Override
            public void onResponse(Call<List<Institute>> call, Response<List<Institute>> response) {
                try {
                    List<Institute> list = response.body();
                    mViewModel.setInstituteList(list);
                    if (list != null && list.size() > 0) {
                        mViewModel.mMapInstitute = new HashMap<>();
                        for (Institute institute : list) {
                            mViewModel.mMapInstitute.put(Integer.valueOf(institute.getInstOrgId()), institute.getName());
                        }
                        populateInstitutre(mViewModel.mMapInstitute);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<List<Institute>> call, Throwable t) {
                Log.i(TAG, t.toString());
            }
        });
    }

    private void fetchDegreeData() {
        Call<List<Degree>> call = apiService.getDegrees();
        call.enqueue(new Callback<List<Degree>>() {
            @Override
            public void onResponse(Call<List<Degree>> call, retrofit2.Response<List<Degree>> response) {
                try {
                    List<Degree> list = response.body();
                    mViewModel.setDegreeList(list);
                    if (list != null && list.size() > 0) {
                        mViewModel.mMapDegree = new HashMap<>();
                        for (Degree degree : list) {
                            mViewModel.mMapDegree.put(degree.getDegreeId(), degree.getName());
                        }
                        populateDegrees(mViewModel.mMapDegree);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<List<Degree>> call, Throwable t) {
                Log.i("", t.toString());
            }
        });
    }


    private void fetchCourseData(int degree) {
        Map<String, Integer> requestBody = new HashMap<>();
        requestBody.put(degree_id, degree);
        Call<List<Course>> call = apiService.getCourses(requestBody);
        call.enqueue(new Callback<List<Course>>() {
            @Override
            public void onResponse(Call<List<Course>> call, Response<List<Course>> response) {
                try {
                    List<Course> list = response.body();
                    mViewModel.setCourseList(list);
                    if (list != null && list.size() > 0) {
                        mViewModel.mMapCourse = new HashMap<>();
                        for (Course course : list) {
                            mViewModel.mMapCourse.put(Integer.valueOf(course.getCourseId()), course.getName());
                        }
                        populateCourse(mViewModel.mMapCourse);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<List<Course>> call, Throwable t) {
                Log.i(TAG, t.toString());
            }
        });
    }

    private void fetchClassData(String course_name, int course_duration) {
        Map<String, Object> requestBody = new HashMap<>();
        Course course = new Course();
        course.setDuration(course_duration);
        course.setName(course_name);
        requestBody.put(degree_name, course.getName());
        requestBody.put(duration, course.getDuration());
        Call<List<ClassData>> call = apiService.getClasses(requestBody);
        call.enqueue(new Callback<List<ClassData>>() {
            @Override
            public void onResponse(Call<List<ClassData>> call, Response<List<ClassData>> response) {
                try {
                    List<ClassData> list = response.body();
                    mViewModel.setClassList(list);
                    if (list != null && list.size() > 0) {
                        mViewModel.mMapClass = new HashMap<>();
                        for (ClassData classitem : list) {
                            mViewModel.mMapClass.put(Integer.valueOf(classitem.getValue()), classitem.getDispText());
                        }
                        populateClass(mViewModel.mMapClass);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<List<ClassData>> call, Throwable t) {
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
        }
    }

    private void getMobileOtp() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put(mobile_number, "7448148405");
        Call<MobileOtp> call = apiService.getMobileOtp(requestBody);
        call.enqueue(new Callback<MobileOtp>() {
            @Override
            public void onResponse(Call<MobileOtp> call, Response<MobileOtp> response) {
                try {

                    Log.i(TAG, response.body().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<MobileOtp> call, Throwable t) {
                Log.i(TAG, t.toString());
            }
        });
    }

    private void callSignUpApi() {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put(first_name, mBinding.tilFirstName.getEditText().getText().toString());
        requestBody.put(last_name, mBinding.tilLastName.getEditText().getText().toString());
        requestBody.put(email, mBinding.tilEmail.getEditText().getText().toString());
        requestBody.put(mobile_no, Integer.parseInt(mBinding.tilMobile.getEditText().getText().toString()));
        requestBody.put(password, mBinding.tilCreatePassword.getEditText().getText().toString());
        requestBody.put(country, signUpData.getCountry());
        requestBody.put(state, signUpData.getState());
        requestBody.put(board, signUpData.getBoard());
        requestBody.put(institute, signUpData.getInstitute());
        requestBody.put(degree, signUpData.getDegree());
        requestBody.put(course, signUpData.getCourse());
        requestBody.put(cyNum, signUpData.getCyNum());

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

    private void createVerifyOTPDialog() {
        Dialog dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.otp_layout);
        final OtpView otpText = dialog.findViewById(R.id.otp_view);

        dialog.show();
    }

    private void verifyMobileOtp() {

    }

    private void verifyEmailOtp() {

    }
}
