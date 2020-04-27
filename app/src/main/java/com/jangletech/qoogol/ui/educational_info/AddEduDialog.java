package com.jangletech.qoogol.ui.educational_info;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.AddEditEducationBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.model.City;
import com.jangletech.qoogol.model.CityObject;
import com.jangletech.qoogol.model.ClassData;
import com.jangletech.qoogol.model.Classes;
import com.jangletech.qoogol.model.Country;
import com.jangletech.qoogol.model.Course;
import com.jangletech.qoogol.model.Degree;
import com.jangletech.qoogol.model.Institute;
import com.jangletech.qoogol.model.State;
import com.jangletech.qoogol.model.University;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
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

import static com.jangletech.qoogol.util.Constant.board_id;
import static com.jangletech.qoogol.util.Constant.country_id;
import static com.jangletech.qoogol.util.Constant.degree_id;
import static com.jangletech.qoogol.util.Constant.degree_name;
import static com.jangletech.qoogol.util.Constant.duration;
import static com.jangletech.qoogol.util.Constant.state_id;

/**
 * Created by Pritali on 3/16/2020.
 */
public class AddEduDialog extends Dialog {

    AddEditEducationBinding addEditEducationBinding;
    Activity context;
    Map<Integer, String> mMapCountry;
    Map<Integer, String> mMapState;
    Map<Integer, String> mMapUniversity;
    Map<Integer, String> mMapInstitute;
    Map<Integer, String> mMapDegree;
    Map<Integer, String> mMapCourse;
    Map<Integer, String> mMapClass;
    Map<Integer, String> mMapCity;
    ApiInterface apiService = ApiClient.getInstance().getApi();
    List<Course> courseList;;

    public AddEduDialog(@NonNull Activity context) {
        super(context,android.R.style.Theme_Light);
        this.context = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        addEditEducationBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.add_edit_education, null, false);
        setContentView(addEditEducationBinding.getRoot());
        fetchCountryData();
        fetchDegreeData();
        setListeners();
    }

    private boolean validate() {
        boolean isValid = true;
        if (addEditEducationBinding.countryAutocompleteView.getText().toString().trim().isEmpty()){
            addEditEducationBinding.countryLayout.setError("Please select country.");
            isValid = false;
        }

        else if (addEditEducationBinding.stateAutocompleteView.getText().toString().trim().isEmpty()){
            addEditEducationBinding.stateAutocompleteView.setError("Please select state.");
            isValid = false;
        }

        else if (addEditEducationBinding.cityAutocompleteView.getText().toString().trim().isEmpty()){
            addEditEducationBinding.cityAutocompleteView.setError("Please select city.");
            isValid = false;
        }

        else if (addEditEducationBinding.universityBoardAutocompleteView.getText().toString().trim().isEmpty()){
            addEditEducationBinding.universityBoardAutocompleteView.setError("Please select university.");
            isValid = false;
        }

        else if (addEditEducationBinding.instituteAutocompleteView.getText().toString().trim().isEmpty()){
            addEditEducationBinding.instituteAutocompleteView.setError("Please select institute.");
            isValid = false;
        }

        else if (addEditEducationBinding.degreeAutocompleteView.getText().toString().trim().isEmpty()){
            addEditEducationBinding.degreeAutocompleteView.setError("Please select degree.");
            isValid = false;
        }

        else if (addEditEducationBinding.courseAutocompleteView.getText().toString().trim().isEmpty()){
            addEditEducationBinding.courseAutocompleteView.setError("Please select course.");
            isValid = false;
        }

        else if (addEditEducationBinding.classAutocompleteView.getText().toString().trim().isEmpty()){
            addEditEducationBinding.classAutocompleteView.setError("Please select class.");
            isValid = false;
        }

        else if (addEditEducationBinding.etstartdate.getText().toString().trim().isEmpty()){
            addEditEducationBinding.tilStartdate.setError("Please select start date.");
            isValid = false;
        }

        else if (addEditEducationBinding.etenddate.getText().toString().trim().isEmpty()){
            addEditEducationBinding.tilEnddate.setError("Please select end date.");
            isValid = false;
        }

        return isValid;
    }

    private void setListeners() {

        addEditEducationBinding.saveEdu.setOnClickListener(v -> {
            if (validate()) {

            }
        });

        addEditEducationBinding.btnClose.setOnClickListener(v -> dismiss());
        addEditEducationBinding.countryAutocompleteView.setOnItemClickListener((parent, view, position, id) -> {
            final String name = ((TextView) view).getText().toString();
            int key = UtilHelper.getKeyFromValue(mMapCountry, name);
            if (key != -1) {
                addEditEducationBinding.countryAutocompleteView.setTag(key);
                fetchStateData(key);
            }
        });

        addEditEducationBinding.stateAutocompleteView.setOnItemClickListener((parent, view, position, id) -> {
            final String state = ((TextView) view).getText().toString();
            final String country = addEditEducationBinding.countryAutocompleteView.getText().toString();
            int state_id = UtilHelper.getKeyFromValue(mMapState, state);
            int country_id = UtilHelper.getKeyFromValue(mMapCountry, country);
            if (state_id != -1 && country_id != -1) {
                addEditEducationBinding.stateAutocompleteView.setTag(state_id);
                fetchUniversityData(country_id, state_id);
                fetchCityData(state_id);
            }
        });

        addEditEducationBinding.cityAutocompleteView.setOnItemClickListener((parent, view, position, id) -> {
            final String name = ((TextView) view).getText().toString();
            int key = UtilHelper.getKeyFromValue(mMapCity, name);
            if (key != -1) {
                addEditEducationBinding.cityAutocompleteView.setTag(key);
            }
        });

        addEditEducationBinding.universityBoardAutocompleteView.setOnItemClickListener((parent, view, position, id) -> {
            final String name = ((TextView) view).getText().toString();
            int key = UtilHelper.getKeyFromValue(mMapUniversity, name);
            if (key != -1) {
                //signUpData.setBoard(key);
                addEditEducationBinding.universityBoardAutocompleteView.setTag(key);
                fetchInstituteData(key);
            }
        });

        addEditEducationBinding.instituteAutocompleteView.setOnItemClickListener((parent, view, position, id) -> {
            final String name = ((TextView) view).getText().toString();
            int key = UtilHelper.getKeyFromValue(mMapInstitute, name);
            if (key != -1) {
                addEditEducationBinding.instituteAutocompleteView.setTag(key);
            }
        });


        addEditEducationBinding.degreeAutocompleteView.setOnItemClickListener((parent, view, position, id) -> {
            final String name = ((TextView) view).getText().toString();
            int key = UtilHelper.getKeyFromValue(mMapDegree, name);
            if (key != -1) {
                addEditEducationBinding.degreeAutocompleteView.setTag(key);
                fetchCourseData(key);
            }
        });

        addEditEducationBinding.courseAutocompleteView.setOnItemClickListener((parent, view, position, id) -> {
            final String name = ((TextView) view).getText().toString();
            int key = UtilHelper.getKeyFromValue(mMapCourse, name);
            if (key != -1) {
                addEditEducationBinding.courseAutocompleteView.setTag(key);
                fetchClassData(name, courseList.get(position).getDuration());
            }
        });

        addEditEducationBinding.classAutocompleteView.setOnItemClickListener((parent, view, position, id) -> {
            final String name = ((TextView) view).getText().toString();
            int key = UtilHelper.getKeyFromValue(mMapClass, name);
            if (key != -1) {
                //signUpData.setCyNum(key);
                addEditEducationBinding.classAutocompleteView.setTag(key);
            }
        });

        addEditEducationBinding.etstartdate.setOnTouchListener((v, event) -> {
            getDate(event,0);
            return true;
        });

        addEditEducationBinding.etenddate.setOnTouchListener((v, event) -> {
            getDate(event,1);
            return true;
        });
    }

    private void getDate(MotionEvent event, int call_from) {
        if (MotionEvent.ACTION_UP == event.getAction()) {
            Calendar newCalendar = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Dialog, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    if (call_from==0)
                        addEditEducationBinding.etstartdate.setText("" + year + "-" + (month + 1) + "-" + day);
                    else
                        addEditEducationBinding.etenddate.setText("" + year + "-" + (month + 1) + "-" + day);
                }
            }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.show();
        }
    }

    private void populateCountries(Map<Integer, String> mMapCountry) {
        List<String> list = new ArrayList<>(mMapCountry.values());
        Collections.sort(list);
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(context,
                R.layout.autocomplete_list_item, list);
        addEditEducationBinding.countryAutocompleteView.setAdapter(countryAdapter);
    }

    private void populateStates(Map<Integer, String> mMapState) {
        List<String> list = new ArrayList<>(mMapState.values());
        Collections.sort(list);
        ArrayAdapter<String> stateAdapter = new ArrayAdapter<String>(context,
                R.layout.autocomplete_list_item, list);
        addEditEducationBinding.stateAutocompleteView.setAdapter(stateAdapter);
    }

    private void populateCities(Map<Integer, String> mMapCity) {
        List<String> list = new ArrayList<>(mMapCity.values());
        Collections.sort(list);
        ArrayAdapter<String> stateAdapter = new ArrayAdapter<String>(context,
                R.layout.autocomplete_list_item, list);
        addEditEducationBinding.cityAutocompleteView.setAdapter(stateAdapter);
    }


    private void populateUniversityBoard(Map<Integer, String> mMapUniversity) {
        List<String> list = new ArrayList<>(mMapUniversity.values());
        Collections.sort(list);
        ArrayAdapter<String> universityAdapter = new ArrayAdapter<String>(context,
                R.layout.autocomplete_list_item, list);
        addEditEducationBinding.universityBoardAutocompleteView.setAdapter(universityAdapter);
    }

    private void populateInstitutre(Map<Integer, String> mMapInstitute) {
        List<String> list = new ArrayList<>(mMapInstitute.values());
        Collections.sort(list);
        ArrayAdapter<String> instituteAdapter = new ArrayAdapter<String>(context,
                R.layout.autocomplete_list_item, list);
        addEditEducationBinding.instituteAutocompleteView.setAdapter(instituteAdapter);
    }

    private void populateDegrees(Map<Integer, String> mMapDegree) {
        List<String> list = new ArrayList<>(mMapDegree.values());
        Collections.sort(list);
        ArrayAdapter<String> degreeAdapter = new ArrayAdapter<String>(context,
                R.layout.autocomplete_list_item, list);
        addEditEducationBinding.degreeAutocompleteView.setAdapter(degreeAdapter);
    }

    private void populateCourse(Map<Integer, String> mMapCourse) {
        List<String> list = new ArrayList<>(mMapCourse.values());
        Collections.sort(list);
        ArrayAdapter<String> courseAdapter = new ArrayAdapter<String>(context,
                R.layout.autocomplete_list_item, list);
        addEditEducationBinding.courseAutocompleteView.setAdapter(courseAdapter);
    }

    private void populateClass(Map<Integer, String> mMapClass) {
        List<String> list = new ArrayList<>(mMapClass.values());
        Collections.sort(list);
        ArrayAdapter<String> classAdapter = new ArrayAdapter<String>(context,
                R.layout.autocomplete_list_item, list);
        addEditEducationBinding.classAutocompleteView.setAdapter(classAdapter);
    }



    private void fetchCountryData() {
//        ProgressDialog.getInstance().show(context);
//        Call<List<Country>> call = apiService.getCountries();
//        call.enqueue(new Callback<List<Country>>() {
//            @Override
//            public void onResponse(Call<List<Country>> call, retrofit2.Response<List<Country>> response) {
//                try {
//                    List<Country> list = response.body();
//                    if (list != null && list.size() > 0) {
//                        mMapCountry = new HashMap<>();
//                        for (Country country : list) {
//                            mMapCountry.put(country.getCountryId(), country.getCountryName());
//                        }
//                        ProgressDialog.getInstance().dismiss();
//                        populateCountries(mMapCountry);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    ProgressDialog.getInstance().dismiss();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<Country>> call, Throwable t) {
//                t.printStackTrace();
//                ProgressDialog.getInstance().dismiss();
//            }
//        });
    }

    public void fetchStateData(int countryId) {
        ProgressDialog.getInstance().show(context);
        Map<String, Integer> requestBody = new HashMap<>();
        requestBody.put("countryId", countryId);
        Call<List<State>> call = apiService.getStates(requestBody);
        call.enqueue(new Callback<List<State>>() {
            @Override
            public void onResponse(Call<List<State>> call, retrofit2.Response<List<State>> response) {
                try {
                    List<State> list = response.body();
                    if (list != null && list.size() > 0) {
                        mMapState = new HashMap<>();
                        for (State state : list) {
                            mMapState.put(Integer.valueOf(state.getStateId()), state.getStateName());
                        }
                        ProgressDialog.getInstance().dismiss();
                        populateStates(mMapState);
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

    private void fetchCityData(int key) {
        ProgressDialog.getInstance().show(context);
        Map<String, Integer> requestBody = new HashMap<>();
        requestBody.put("stateId", key);
        Call<City> call = apiService.getCities(requestBody);
        call.enqueue(new Callback<City>() {
            @Override
            public void onResponse(Call<City> call, retrofit2.Response<City> response) {
                try {
                    List<CityObject> list = response.body().getObject();
                    if (list != null && list.size() > 0) {
                        mMapCity = new HashMap<>();
                        for (CityObject cityObject : list) {
                            mMapCity.put(Integer.valueOf(cityObject.getCityId()), cityObject.getCityName());
                        }
                        ProgressDialog.getInstance().dismiss();
                        populateCities(mMapCity);
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

    private void fetchUniversityData(int country, int state) {
        ProgressDialog.getInstance().show(context);
        Map<String, Integer> requestBody = new HashMap<>();
        requestBody.put(country_id, country);
        requestBody.put(state_id, state);
        Call<List<University>> call = apiService.getUniversity(requestBody);
        call.enqueue(new Callback<List<University>>() {
            @Override
            public void onResponse(Call<List<University>> call, Response<List<University>> response) {
                try {
                    List<University> list = response.body();
                    if (list != null && list.size() > 0) {
                        mMapUniversity = new HashMap<>();
                        for (University university : list) {
                            mMapUniversity.put(Integer.valueOf(university.getUnivBoardId()), university.getName());
                        }
                        ProgressDialog.getInstance().dismiss();
                        populateUniversityBoard(mMapUniversity);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ProgressDialog.getInstance().dismiss();
                }
            }

            @Override
            public void onFailure(Call<List<University>> call, Throwable t) {
                t.printStackTrace();
                ProgressDialog.getInstance().dismiss();
            }
        });
    }


    private void fetchInstituteData(int university) {
        ProgressDialog.getInstance().show(context);
        Map<String, Integer> requestBody = new HashMap<>();
        requestBody.put(board_id, university);
        Call<List<Institute>> call = apiService.getInstitute(requestBody);
        call.enqueue(new Callback<List<Institute>>() {
            @Override
            public void onResponse(Call<List<Institute>> call, Response<List<Institute>> response) {
                try {
                    List<Institute> list = response.body();
                    if (list != null && list.size() > 0) {
                        mMapInstitute = new HashMap<>();
                        for (Institute institute : list) {
                            mMapInstitute.put(Integer.valueOf(institute.getInstOrgId()), institute.getName());
                        }
                        ProgressDialog.getInstance().dismiss();
                        populateInstitutre(mMapInstitute);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ProgressDialog.getInstance().dismiss();

                }
            }

            @Override
            public void onFailure(Call<List<Institute>> call, Throwable t) {
                t.printStackTrace();
                ProgressDialog.getInstance().dismiss();
            }
        });
    }

    private void fetchDegreeData() {
        ProgressDialog.getInstance().show(context);
        Call<List<Degree>> call = apiService.getDegrees();
        call.enqueue(new Callback<List<Degree>>() {
            @Override
            public void onResponse(Call<List<Degree>> call, retrofit2.Response<List<Degree>> response) {
                try {
                    List<Degree> list = response.body();
                    if (list != null && list.size() > 0) {
                        mMapDegree = new HashMap<>();
                        for (Degree degree : list) {
                            mMapDegree.put(degree.getDegreeId(), degree.getName());
                        }
                        ProgressDialog.getInstance().dismiss();
                        populateDegrees(mMapDegree);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ProgressDialog.getInstance().dismiss();
                }
            }

            @Override
            public void onFailure(Call<List<Degree>> call, Throwable t) {
                t.printStackTrace();
                ProgressDialog.getInstance().dismiss();
            }
        });
    }


    private void fetchCourseData(int degree) {
        ProgressDialog.getInstance().show(context);
        Map<String, Integer> requestBody = new HashMap<>();
        requestBody.put(degree_id, degree);
        Call<List<Course>> call = apiService.getCourses(requestBody);
        call.enqueue(new Callback<List<Course>>() {
            @Override
            public void onResponse(Call<List<Course>> call, Response<List<Course>> response) {
                try {
                    List<Course> list = response.body();
                    courseList = new ArrayList<>();
                    courseList.addAll(list);
                    if (list != null && list.size() > 0) {
                        mMapCourse = new HashMap<>();
                        for (Course course : list) {
                            mMapCourse.put(Integer.valueOf(course.getCourseId()), course.getName());
                        }
                        ProgressDialog.getInstance().dismiss();
                        populateCourse(mMapCourse);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ProgressDialog.getInstance().dismiss();
                }
            }

            @Override
            public void onFailure(Call<List<Course>> call, Throwable t) {
                t.printStackTrace();
                ProgressDialog.getInstance().dismiss();
            }
        });
    }

    private void fetchClassData(String course_name, int course_duration) {
        ProgressDialog.getInstance().show(context);
        Map<String, Object> requestBody = new HashMap<>();
        Course course = new Course();
        course.setDuration(course_duration);
        course.setName(course_name);

        requestBody.put(degree_name, course.getName());
        requestBody.put(duration, course.getDuration());
        Call<Classes> call = apiService.getClasses(requestBody);
        call.enqueue(new Callback<Classes>() {
            @Override
            public void onResponse(Call<Classes> call, Response<Classes> response) {
                try {
                    List<ClassData> list = response.body().getObject();
                    if (list != null && list.size() > 0) {
                        mMapClass = new HashMap<>();
                        for (ClassData classitem : list) {
                            mMapClass.put(Integer.valueOf(classitem.getValue()), classitem.getDispText());
                        }
                        ProgressDialog.getInstance().dismiss();
                        populateClass(mMapClass);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    ProgressDialog.getInstance().dismiss();
                }
            }

            @Override
            public void onFailure(Call<Classes> call, Throwable t) {
                t.printStackTrace();
                ProgressDialog.getInstance().dismiss();
            }
        });
    }

}
