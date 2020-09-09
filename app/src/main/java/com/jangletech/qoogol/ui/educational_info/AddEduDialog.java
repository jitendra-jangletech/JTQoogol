package com.jangletech.qoogol.ui.educational_info;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;

import com.google.android.material.textfield.TextInputLayout;
import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.AddEditEducationBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.model.AddElementResponse;
import com.jangletech.qoogol.model.ClassList;
import com.jangletech.qoogol.model.Course;
import com.jangletech.qoogol.model.CourseResponse;
import com.jangletech.qoogol.model.Degree;
import com.jangletech.qoogol.model.DegreeResponse;
import com.jangletech.qoogol.model.Education;
import com.jangletech.qoogol.model.Institute;
import com.jangletech.qoogol.model.InstituteResponse;
import com.jangletech.qoogol.model.University;
import com.jangletech.qoogol.model.UniversityResponse;
import com.jangletech.qoogol.model.VerifyResponse;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.ui.BaseFragment;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.DateUtils;
import com.jangletech.qoogol.util.PreferenceManager;
import com.jangletech.qoogol.util.UtilHelper;

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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jangletech.qoogol.util.Constant.board_id;

public class AddEduDialog extends Dialog {

    private static final String TAG = "AddEduDialog";
    private AddEditEducationBinding addEditEducationBinding;
    private Activity context;
    private Education education;
    private Calendar mCalender;
    private Map<Integer, String> mMapUniversity;
    private Map<Integer, String> mMapInstitute;
    private Map<Integer, String> mMapDegree;
    private Map<Integer, String> mMapCourse;
    private ApiInterface apiService = ApiClient.getInstance().getApi();
    private List<Course> courseList;
    private int pos;
    private HashMap<String, String> params;
    private ApiCallListener apiCallListener;
    private boolean flag;

    public AddEduDialog(@NonNull Activity context, Education education, boolean flag, ApiCallListener apiCallListener,
                        int pos) {
        super(context, android.R.style.Theme_DeviceDefault_Light_DarkActionBar);
        this.context = context;
        this.education = education;
        this.apiCallListener = apiCallListener;
        this.flag = flag;
        this.pos = pos;
        mCalender = Calendar.getInstance();
        params = new HashMap<>();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        addEditEducationBinding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.add_edit_education, null, false);
        setContentView(addEditEducationBinding.getRoot());
        fetchUniversityData();
        fetchDegreeData();
        if (education != null) {
            fetchClassList(education.getCo_id());
            fetchInstituteData(0);
            fetchCourseData(Integer.parseInt(education.getDm_id()));
            addEditEducationBinding.universityBoardAutocompleteView.setTag(education.getUbm_id());
            addEditEducationBinding.instituteAutocompleteView.setTag(education.getIom_id());
            addEditEducationBinding.degreeAutocompleteView.setTag(education.getDm_id());
            addEditEducationBinding.courseAutocompleteView.setTag(education.getCo_id());
            addEditEducationBinding.courseYearAutocompleteView.setTag(education.getUe_cy_num());
            addEditEducationBinding.universityBoardAutocompleteView.setText(education.getUbm_board_name());
            addEditEducationBinding.instituteAutocompleteView.setText(education.getIom_name());
            addEditEducationBinding.degreeAutocompleteView.setText(education.getDm_degree_name());
            addEditEducationBinding.courseAutocompleteView.setText(education.getCo_name());
            addEditEducationBinding.courseYearAutocompleteView.setText(education.getUe_cy_num());
            addEditEducationBinding.etstartdate.setText(DateUtils.getFormattedDate(education.getUe_startdate()));
            addEditEducationBinding.etenddate.setText(DateUtils.getFormattedDate(education.getUe_enddate()));
        }
        setListeners();
    }

    private String getSingleQuoteString(String text) {
        return String.format("'%s'", text);
    }


    private void setListeners() {
        addEditEducationBinding.etstartdate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    addEditEducationBinding.etstartdate.setError(null);
                    addEditEducationBinding.tilStartdate.setError(null);
                }
            }
        });

        addEditEducationBinding.etenddate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    addEditEducationBinding.etenddate.setError(null);
                    addEditEducationBinding.tilEnddate.setError(null);
                }
            }
        });

        addEditEducationBinding.delete.setOnClickListener(v -> {
            if(!flag) {
                if (education == null) {
                    dismiss();
                } else {
                    androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogStyle);
                    builder.setTitle("Confirm")
                            .setMessage("Are you sure you want to delete education?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    apiCallListener.onDialogEduDelete(education, pos);
                                    dismiss();
                                }
                            }).setNegativeButton("No", null)
                            .show();
                }
            }
        });

        addEditEducationBinding.addUniversity.setOnClickListener(v -> {
            showAddElementDialog("Add University");
        });

        addEditEducationBinding.addInstitute.setOnClickListener(v -> {
            showAddElementDialog("Add Institute");
        });

        addEditEducationBinding.btnSave.setOnClickListener(v -> {
            if (addEditEducationBinding.universityBoardAutocompleteView.getText().toString().trim().isEmpty()) {
                addEditEducationBinding.universityBoardAutocompleteView.setError("Please select university.");
                return;
            } else if (addEditEducationBinding.instituteAutocompleteView.getText().toString().trim().isEmpty()) {
                addEditEducationBinding.instituteAutocompleteView.setError("Please select institute.");
                return;
            } else if (addEditEducationBinding.degreeAutocompleteView.getText().toString().trim().isEmpty()) {
                addEditEducationBinding.degreeAutocompleteView.setError("Please select degree.");
                return;
            } else if (addEditEducationBinding.courseAutocompleteView.getText().toString().trim().isEmpty()) {
                addEditEducationBinding.courseAutocompleteView.setError("Please select course.");
                return;
            } else if (addEditEducationBinding.courseYearAutocompleteView.getText().toString().trim().isEmpty()) {
                addEditEducationBinding.courseYearAutocompleteView.setError("Please select course year.");
                return;
            } else if (addEditEducationBinding.etstartdate.getText().toString().trim().isEmpty()) {
                addEditEducationBinding.tilStartdate.setError("Please select start date.");
                return;
            } else if (addEditEducationBinding.etenddate.getText().toString().trim().isEmpty()) {
                addEditEducationBinding.tilEnddate.setError("Please select end date.");
                return;
            }

            params.put(Constant.u_user_id, String.valueOf(new PreferenceManager(context).getInt(Constant.USER_ID)));
            params.put(Constant.appName, Constant.APP_NAME);
            params.put(Constant.device_id, BaseFragment.getDeviceId(getContext()));
            params.put(Constant.ubm_id, addEditEducationBinding.universityBoardAutocompleteView.getTag().toString());
            params.put(Constant.iom_id, addEditEducationBinding.instituteAutocompleteView.getTag().toString());
            params.put(Constant.dm_id, addEditEducationBinding.degreeAutocompleteView.getTag().toString());
            params.put(Constant.co_id, addEditEducationBinding.courseAutocompleteView.getTag().toString());
            params.put(Constant.ue_startdate, addEditEducationBinding.etstartdate.getText().toString());
            params.put(Constant.ue_enddate, addEditEducationBinding.etenddate.getText().toString());
            if (addEditEducationBinding.courseYearAutocompleteView.getTag() != null)
                params.put(Constant.ue_cy_num, addEditEducationBinding.courseYearAutocompleteView.getTag().toString());

            if (education != null) {
                params.put(Constant.CASE, "U");
                params.put(Constant.ue_id, education.getUe_id());
                updateUserEducation(params);
            } else {
                params.put(Constant.CASE, "I");
                updateUserEducation(params);
            }
        });

        addEditEducationBinding.btnClose.setOnClickListener(v -> {
            if (!flag)
                dismiss();
        });

        addEditEducationBinding.universityBoardAutocompleteView.setOnItemClickListener((parent, view, position, id) -> {
            final String name = ((TextView) view).getText().toString();
            int key = UtilHelper.getKeyFromValue(mMapUniversity, name);
            if (key != -1) {
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
            Log.e(TAG, "Degree Id : " + key);
            Log.e(TAG, "Degree : " + name);
        });

        addEditEducationBinding.courseAutocompleteView.setOnItemClickListener((parent, view, position, id) -> {
            final String name = ((TextView) view).getText().toString();
            int key = UtilHelper.getKeyFromValue(mMapCourse, name);
            if (key != -1) {
                addEditEducationBinding.courseAutocompleteView.setTag(key);
                fetchClassList(String.valueOf(key));
            }
            Log.e(TAG, "Course Id : " + key);
            Log.e(TAG, "Course : " + name);
        });

        addEditEducationBinding.courseYearAutocompleteView.setOnItemClickListener((parent, view, position, id) -> {
            final String name = ((TextView) view).getText().toString();
            addEditEducationBinding.courseYearAutocompleteView.setTag(name);
            getDate(0);
        });

        addEditEducationBinding.etstartdate.setOnTouchListener((v, event) -> {
            if (MotionEvent.ACTION_UP == event.getAction()) {
                getDate(0);
            }
            return true;
        });

        addEditEducationBinding.etenddate.setOnTouchListener((v, event) -> {
            if (MotionEvent.ACTION_UP == event.getAction()) {
                getDate(1);
            }
            return true;
        });
    }

    private void fetchClassList(String courseId) {
        Log.d(TAG, "fetchClassList: " + courseId);
        ProgressDialog.getInstance().show(context);
        Call<ClassList> call = apiService.fetchClasses(courseId);
        call.enqueue(new Callback<ClassList>() {
            @Override
            public void onResponse(Call<ClassList> call, Response<ClassList> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.body() != null) {
                    List<String> list = new ArrayList<>();
                    for (int i = 0; i < response.body().getClassResponseList().size(); i++) {
                        list.add(response.body().getClassResponseList().get(i).getClm_class_num());
                    }
                    populateClasses(list);
                } else {
                    Toast.makeText(context, "Something went wrong.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ClassList> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                Toast.makeText(context, "Something went wrong.", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    private void showAddElementDialog(String title) {
        final Dialog dialog = new Dialog(context);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.add_element_dialog);
        dialog.show();
        TextInputLayout inputLayout = dialog.findViewById(R.id.tvInputLayout);
        inputLayout.requestFocus();
        TextView btnAdd = dialog.findViewById(R.id.btnAdd);
        TextView btnCancel = dialog.findViewById(R.id.btnCancel);
        TextView tvHead = dialog.findViewById(R.id.tvHead);
        tvHead.setText(title);
        if (title.contains("Institute")) {
            inputLayout.setHint("Institute");
        } else {
            inputLayout.setHint("University");
        }
        btnAdd.setOnClickListener(v -> {
            if (inputLayout.getEditText().getText().toString().isEmpty()) {
                if (title.contains("Institute")) {
                    inputLayout.setError("Please Enter Institute Name.");
                } else {
                    inputLayout.setError("Please Enter University Name.");
                }
                return;
            }

            String text = inputLayout.getEditText().getText().toString();
            if (title.contains("Institute")) {
                dialog.dismiss();
                addNewInstitute(text);
            } else if (title.contains("University")) {
                dialog.dismiss();
                addNewUniversity(text);
            }
        });

        btnCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
    }

    private void getDate(int call_from) {
//        if (MotionEvent.ACTION_UP == event.getAction()) {
        DateFormat format = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
        Date date = null;
        try {
            if (call_from == 0 && !addEditEducationBinding.etstartdate.getText().toString().isEmpty()) {
                date = format.parse(addEditEducationBinding.etstartdate.getText().toString());
            }
            if (call_from == 1 && !addEditEducationBinding.etenddate.getText().toString().isEmpty()) {
                date = format.parse(addEditEducationBinding.etenddate.getText().toString());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar newCalendar = Calendar.getInstance();
        if (date != null)
            newCalendar.setTime(date);
        DatePickerDialog dialog = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Dialog, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String formattedDate = year + "-" + (month + 1) + "-" + day;
                String formattedEndDate = (year + 1) + "-" + (month + 1) + "-" + day;
                if (call_from == 0) {
                    mCalender.set(Calendar.YEAR, year);
                    mCalender.set(Calendar.MONTH, month);
                    mCalender.set(Calendar.DAY_OF_MONTH, day);

                    addEditEducationBinding.etstartdate.setText(DateUtils.getFormattedDate(formattedDate));
                    addEditEducationBinding.etenddate.setText(DateUtils.getFormattedDate(formattedEndDate));
                } else {
                    addEditEducationBinding.etenddate.setText(DateUtils.getFormattedDate(formattedDate));
                }
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        if (call_from == 1)
            dialog.getDatePicker().setMinDate(mCalender.getTimeInMillis());
        dialog.show();
        //}
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

    private void populateClasses(List<String> list) {
        Collections.sort(list);
        ArrayAdapter<String> classAdapter = new ArrayAdapter<String>(context,
                R.layout.autocomplete_list_item, list);
        addEditEducationBinding.courseYearAutocompleteView.setAdapter(classAdapter);
    }

    private void updateUserEducation(HashMap<String, String> params) {
        Log.d(TAG, "Update User Education Params : " + params);
        Log.e(TAG, "Save Degree Id : " + params.get(Constant.dm_id));
        ProgressDialog.getInstance().show(context);
        Call<VerifyResponse> call = apiService.updateUserEdu(
                params.get(Constant.u_user_id),
                params.get(Constant.CASE),
                params.get(Constant.device_id),
                params.get(Constant.appName),
                params.get(Constant.ue_startdate),
                params.get(Constant.ue_enddate),
                params.get(Constant.ue_marks),
                params.get(Constant.ue_grade),
                params.get(Constant.ubm_id),
                params.get(Constant.iom_id),
                params.get(Constant.co_id),
                params.get(Constant.dm_id),
                params.get(Constant.ue_id),
                params.get(Constant.ue_cy_num)
        );
        call.enqueue(new Callback<VerifyResponse>() {
            @Override
            public void onResponse(Call<VerifyResponse> call, Response<VerifyResponse> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.body() != null && response.body().getResponse().equals("200")) {
                    Toast.makeText(context, "Education Details Updated Successfully.", Toast.LENGTH_SHORT).show();
                    apiCallListener.onSuccess();
                    dismiss();
                } else {
                    Toast.makeText(context, "Error : " + response.body().getResponse(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<VerifyResponse> call, Throwable t) {
                t.printStackTrace();
                Toast.makeText(context, "Something went wrong!!", Toast.LENGTH_SHORT).show();
                ProgressDialog.getInstance().dismiss();
            }
        });
    }

    private void addNewUniversity(String text) {
        ProgressDialog.getInstance().show(context);
        Call<AddElementResponse> call = apiService.addUniversity(text);
        call.enqueue(new Callback<AddElementResponse>() {
            @Override
            public void onResponse(Call<AddElementResponse> call, Response<AddElementResponse> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.body() != null && response.body().getResponse().equals("200")) {
                    fetchUniversityData();
                    addEditEducationBinding.universityBoardAutocompleteView.setText(text);
                    addEditEducationBinding.universityBoardAutocompleteView.setTag(response.body().getUbm_id());
                } else {
                    Toast.makeText(context, "Error : " + response.body().getResponse(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AddElementResponse> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                Toast.makeText(context, "Something went wrong!!", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    private void addNewInstitute(String text) {
        ProgressDialog.getInstance().show(context);
        Call<AddElementResponse> call = apiService.addInstitute(text);
        call.enqueue(new Callback<AddElementResponse>() {
            @Override
            public void onResponse(Call<AddElementResponse> call, Response<AddElementResponse> response) {
                ProgressDialog.getInstance().dismiss();
                if (response.body() != null && response.body().getResponse().equals("200")) {
                    fetchInstituteData(0);
                    addEditEducationBinding.instituteAutocompleteView.setText(text);
                    if (response.body().getIom_id() != null)
                        addEditEducationBinding.instituteAutocompleteView.setTag(response.body().getIom_id());
                    else
                        addEditEducationBinding.instituteAutocompleteView.setTag(response.body().getInstituteId());
                } else {
                    Toast.makeText(context, "Error : " + response.body().getResponse(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AddElementResponse> call, Throwable t) {
                ProgressDialog.getInstance().dismiss();
                Toast.makeText(context, "Something went wrong!!", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    private void fetchUniversityData() {
        ProgressDialog.getInstance().show(context);
        Call<UniversityResponse> call = apiService.getUniversity();
        call.enqueue(new Callback<UniversityResponse>() {
            @Override
            public void onResponse(Call<UniversityResponse> call, Response<UniversityResponse> response) {
                try {
                    List<University> list = response.body().getMasterDataList();
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
            public void onFailure(Call<UniversityResponse> call, Throwable t) {
                t.printStackTrace();
                ProgressDialog.getInstance().dismiss();
            }
        });
    }


    private void fetchInstituteData(int university) {
        ProgressDialog.getInstance().show(context);
        Map<String, Integer> requestBody = new HashMap<>();
        requestBody.put(board_id, university);
        Call<InstituteResponse> call = apiService.getInstitute();
        call.enqueue(new Callback<InstituteResponse>() {
            @Override
            public void onResponse(Call<InstituteResponse> call, Response<InstituteResponse> response) {
                try {
                    List<Institute> list = response.body().getMasterDataList();
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
            public void onFailure(Call<InstituteResponse> call, Throwable t) {
                t.printStackTrace();
                ProgressDialog.getInstance().dismiss();
            }
        });
    }

    private void fetchDegreeData() {
        ProgressDialog.getInstance().show(context);
        Call<DegreeResponse> call = apiService.getDegrees();
        call.enqueue(new Callback<DegreeResponse>() {
            @Override
            public void onResponse(Call<DegreeResponse> call, retrofit2.Response<DegreeResponse> response) {
                try {
                    List<Degree> list = response.body().getMasterDataList();
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
            public void onFailure(Call<DegreeResponse> call, Throwable t) {
                t.printStackTrace();
                ProgressDialog.getInstance().dismiss();
            }
        });
    }

    private void fetchCourseData(int degree) {
        ProgressDialog.getInstance().show(context);
        Call<CourseResponse> call = apiService.getCourses(degree);
        call.enqueue(new Callback<CourseResponse>() {
            @Override
            public void onResponse(Call<CourseResponse> call, Response<CourseResponse> response) {
                try {
                    List<Course> list = response.body().getMasterDataList();
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
            public void onFailure(Call<CourseResponse> call, Throwable t) {
                t.printStackTrace();
                ProgressDialog.getInstance().dismiss();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (!flag)
                dismiss();
            else
                //do nothing
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public interface ApiCallListener {
        void onSuccess();

        void onDialogEduDelete(Education education, int pos);
    }
}
