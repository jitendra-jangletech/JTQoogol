package com.jangletech.qoogol.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.databinding.AddEditEducationBinding;
import com.jangletech.qoogol.databinding.AddEducationBinding;
import com.jangletech.qoogol.databinding.ItemEducationBinding;
import com.jangletech.qoogol.databinding.LayoutChangePasswordBinding;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.model.Country;
import com.jangletech.qoogol.model.FetchEducationsObject;
import com.jangletech.qoogol.model.State;
import com.jangletech.qoogol.model.StateResponse;
import com.jangletech.qoogol.model.University;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.ui.educational_info.AddEduDialog;
import com.jangletech.qoogol.util.UtilHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.jangletech.qoogol.util.Constant.country_id;
import static com.jangletech.qoogol.util.Constant.state_id;

public class EducationAdapter extends RecyclerView.Adapter<EducationAdapter.ViewHolder>  {

    Activity activity;
    List<FetchEducationsObject> educationList;
    ItemEducationBinding itemEducationBinding;
    OnEduClick onEduClick;
    Dialog addEditEduDialog;
    AddEditEducationBinding addEditEducationBinding;
    ApiInterface apiService = ApiClient.getInstance().getApi();
    Map<Integer, String> mMapCountry;
    Map<Integer, String> mMapState;
    Map<Integer, String> mMapUniversity;
    Map<Integer, String> mMapInstitute;
    Map<Integer, String> mMapDegree;
    Map<Integer, String> mMapCourse;
    Map<Integer, String> mMapClass;


    public EducationAdapter(Activity activity, List<FetchEducationsObject> itemlist, OnEduClick onEduClick) {
        this.activity = activity;
        this.educationList = itemlist;
        this.onEduClick = onEduClick;
    }

    @NonNull
    @Override
    public EducationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        itemEducationBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_education, parent, false);

        return  new EducationAdapter.ViewHolder(itemEducationBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull EducationAdapter.ViewHolder holder, int position) {
        FetchEducationsObject fetchEducationsObject = educationList.get(position);
        holder.itemEducationBinding.courseTextview.setText(fetchEducationsObject.getCourseName());
        holder.itemEducationBinding.degreeTextview.setText(fetchEducationsObject.getDegreeName());
        holder.itemEducationBinding.dateTextview.setText(fetchEducationsObject.getStartDateStr() + " - " + fetchEducationsObject.getEndDateStr());


        holder.itemEducationBinding.delete.setOnClickListener(v -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity, R.style.Dialog);
            alertDialogBuilder.setMessage("Are you sure you want to delete?");
                    alertDialogBuilder.setPositiveButton("yes",
                            (arg0, arg1) -> onEduClick.onDelete(fetchEducationsObject));

            alertDialogBuilder.setNegativeButton("No", (dialog, which) -> {
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        });


        holder.itemEducationBinding.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void addEditEducation() {
        addEditEduDialog = new Dialog(activity, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
        addEditEducationBinding = DataBindingUtil.inflate(LayoutInflater.from(activity), R.layout.add_edit_education, null, false);
        addEditEduDialog.setContentView(addEditEducationBinding.getRoot());
        addEditEduDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
        addEditEduDialog.show();
        //fetchCountryData();
        setListeners();

        addEditEducationBinding.btnClose.setOnClickListener(v ->
                addEditEduDialog.dismiss()
        );

        addEditEducationBinding.saveEdu.setOnClickListener(v -> {
           if (validate()) {
               addEducationApi();
           }
        });
    }

    private void addEducationApi() {
    }

    private boolean validate() {
        boolean isValid = true;
        if (addEditEducationBinding.countryAutocompleteView.getText().toString().trim().isEmpty()){
            addEditEducationBinding.countryAutocompleteView.setError("Please select country.");
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
            }
        });
    }

    private void populateCountries(Map<Integer, String> mMapCountry) {
        List<String> list = new ArrayList<>(mMapCountry.values());
        Collections.sort(list);
        ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(activity,
                R.layout.autocomplete_list_item, list);
        addEditEducationBinding.countryAutocompleteView.setAdapter(countryAdapter);
    }

    private void populateStates(Map<Integer, String> mMapState) {
        List<String> list = new ArrayList<>(mMapState.values());
        Collections.sort(list);
        ArrayAdapter<String> stateAdapter = new ArrayAdapter<String>(activity,
                R.layout.autocomplete_list_item, list);
        addEditEducationBinding.stateAutocompleteView.setAdapter(stateAdapter);
    }

    private void populateUniversityBoard(Map<Integer, String> mMapUniversity) {
        List<String> list = new ArrayList<>(mMapUniversity.values());
        Collections.sort(list);
        ArrayAdapter<String> universityAdapter = new ArrayAdapter<String>(activity,
                R.layout.autocomplete_list_item, list);
        addEditEducationBinding.universityBoardAutocompleteView.setAdapter(universityAdapter);
    }



    /*private void fetchCountryData() {
        ProgressDialog.getInstance().show(activity);
        Call<List<Country>> call = apiService.getCountries();
        call.enqueue(new Callback<List<Country>>() {
            @Override
            public void onResponse(Call<List<Country>> call, retrofit2.Response<List<Country>> response) {
                try {
                    List<Country> list = response.body();
                    if (list != null && list.size() > 0) {
                        mMapCountry = new HashMap<>();
                        for (Country country : list) {
                            mMapCountry.put(country.getCountryId(), country.getCountryName());
                        }
                        ProgressDialog.getInstance().dismiss();
                        populateCountries(mMapCountry);
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
    }*/

    public void fetchStateData(int countryId) {
        ProgressDialog.getInstance().show(activity);
        Map<String, Integer> requestBody = new HashMap<>();
        requestBody.put("countryId", countryId);
        Call<StateResponse> call = apiService.getStates();
        call.enqueue(new Callback<StateResponse>() {
            @Override
            public void onResponse(Call<StateResponse> call, retrofit2.Response<StateResponse> response) {
                try {
                    List<State> list = response.body().getStateList();
                    if (list != null && list.size() > 0) {
                        mMapState = new HashMap<>();
                        for (State state : list) {
                            mMapState.put(Integer.valueOf(state.getS_id()), state.getS_name());
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
            public void onFailure(Call<StateResponse> call, Throwable t) {
                t.printStackTrace();
                ProgressDialog.getInstance().dismiss();
            }
        });
    }

    private void fetchUniversityData(int country, int state) {
        ProgressDialog.getInstance().show(activity);
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



    public interface OnEduClick {
        void onDelete(FetchEducationsObject fetchEducationsObject);
    }

    @Override
    public int getItemCount() {
        return educationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemEducationBinding itemEducationBinding;
        public ViewHolder(@NonNull ItemEducationBinding itemView) {
            super(itemView.getRoot());
            this.itemEducationBinding = itemView;
        }
    }
}
