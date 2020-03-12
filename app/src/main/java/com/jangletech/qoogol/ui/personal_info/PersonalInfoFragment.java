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
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.jangletech.qoogol.R;
import com.jangletech.qoogol.activities.SignInActivity;
import com.jangletech.qoogol.databinding.FragmentPersonalInfoBinding;
import com.jangletech.qoogol.dialog.ChangePasswordDialog;
import com.jangletech.qoogol.dialog.ProgressDialog;
import com.jangletech.qoogol.model.ChangePassword;
import com.jangletech.qoogol.model.GetUserPersonalDetails;
import com.jangletech.qoogol.retrofit.ApiClient;
import com.jangletech.qoogol.retrofit.ApiInterface;
import com.jangletech.qoogol.util.Constant;
import com.jangletech.qoogol.util.PreferenceManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.facebook.FacebookSdk.getApplicationContext;

public class PersonalInfoFragment extends Fragment implements ChangePasswordDialog.ChangeDialogClickListener{

    private static final String TAG = "PersonalInfoFragment";
    private PersonalInfoViewModel mViewModel;
    private FragmentPersonalInfoBinding mBinding;
    ChangePasswordDialog changePasswordDialog;
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

        mBinding.changePassword.setOnClickListener(v -> {
            //Todo Change Password Dialog
        });

        mBinding.tvMobileVerify.setOnClickListener(v -> {
            //Todo Mobile Verify Dialog
        });

        mBinding.tvEmailVerify.setOnClickListener(v -> {
            //Todo Email Verify Dialog
        });

        mBinding.changePassword.setOnClickListener(v->{

            //getActivity().getFragmentManager().popBackStack();
           /* Toast.makeText(getActivity(), "HEllo", Toast.LENGTH_SHORT).show();
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
            transaction.replace(R.id.nav_host_fragment, new ChangePasswordFragment());
            transaction.addToBackStack(null);
            transaction.commit();*/

            //changePasswordDialog = new ChangePasswordDialog(getActivity(),this);
            //changePasswordDialog.show();

            Dialog dialog=new Dialog(getActivity(),android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen);
            dialog.setContentView(R.layout.layout_change_password);
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation_2;
            dialog.show();
        });

        mBinding.etDob.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(MotionEvent.ACTION_UP == event.getAction()) {
                    Calendar newCalendar = Calendar.getInstance();
                    DatePickerDialog dialog = new DatePickerDialog(getContext(), android.R.style.Theme_Holo_Dialog, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                            mBinding.etDob.setText(""+day+"-"+(month+1)+"-"+year);
                        }
                    },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    dialog.show();
                }

                return true;
            }
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



    public void fetchPersonalDetails() {
        try {
            ProgressDialog.getInstance().show(getActivity());
            Map<String, String> arguments = new HashMap<>();
            arguments.put(Constant.user_id, new PreferenceManager(getContext()).getUserId());

            Call<GetUserPersonalDetails> call = apiService.getPersonalDetails(arguments);
            call.enqueue(new Callback<GetUserPersonalDetails>() {
                @Override
                public void onResponse(Call<GetUserPersonalDetails> call, Response<GetUserPersonalDetails> response) {
                    if (response.body() != null && response.body().getObject() != null
                            && response.body().getStatusCode().equalsIgnoreCase("1")) {
                        GetUserPersonalDetails getUserPersonalDetails = (GetUserPersonalDetails) response.body();
                        updatePersonalDetailsUi(response.body());
                    }
                }

                @Override
                public void onFailure(Call<GetUserPersonalDetails> call, Throwable t) {
                    t.printStackTrace();
                    ProgressDialog.getInstance().dismiss();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updatePersonalDetailsUi(GetUserPersonalDetails personalDetails) {
        Log.d(TAG, "updatePersonalDetailsUi: " + personalDetails);

        mBinding.etMobile.setText("" + personalDetails.getObject().getMobileNo1());

        mBinding.etFirstName.setText(personalDetails.getObject().getFirstName());
        mBinding.etLastName.setText(personalDetails.getObject().getLastName());

        mBinding.etEmail.setText(personalDetails.getObject().getEmail());

        if(personalDetails.getObject().getDobString()!=null) {
            //SimpleDateFormat targetFormat = new SimpleDateFormat("MM-dd-yyyy");
            //Date date = new Date(personalDetails.getObject().getDobString());


            try {
                Date date = new SimpleDateFormat("dd/MMM/yyyy").parse(personalDetails.getObject().getDobString());
                mBinding.etDob.setText(date.toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }


        }



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

    public void disableLayoutFields(){
        mBinding.etFirstName.setClickable(false);
        mBinding.etLastName.setEnabled(false);
        mBinding.etDob.setEnabled(false);
        mBinding.genderRadioGrp.setEnabled(false);
        mBinding.stateAutocompleteView.setEnabled(false);
        mBinding.countryAutocompleteView.setEnabled(false);
        mBinding.cityAutocompleteView.setEnabled(false);
    }

    @Override
    public void onSubmitClick(String oldPwd,String newPwd) {
        changePassword(oldPwd,newPwd);
    }

    public void changePassword(String oldPwd,String newPwd){
        ProgressDialog.getInstance().show(getActivity());
        Map<String, String> arguments = new HashMap<>();
        arguments.put("userId",new PreferenceManager(getApplicationContext()).getUserId());
        arguments.put("oldPassword",oldPwd);
        arguments.put("newPassword", newPwd);

        Call<ChangePassword> call = apiService.changePassword(arguments);
        call.enqueue(new Callback<ChangePassword>() {
            @Override
            public void onResponse(Call<ChangePassword> call, Response<ChangePassword> response) {
                if (response.body()!=null
                        && response.body().getStatusCode() == 1) {
                    ProgressDialog.getInstance().dismiss();
                    if(changePasswordDialog!=null){
                        changePasswordDialog.dismiss();
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.AlertDialogStyle);
                    builder.setTitle("Success");
                    builder.setMessage("Password Changed Successfully, Please Sign-In With New Password.");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new PreferenceManager(getApplicationContext()).setIsLoggedIn(false);
                            Intent i = new Intent(getActivity(), SignInActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i);
                        }
                    }).show();

                }else if(response.body().getStatusCode() == 0){
                    ProgressDialog.getInstance().dismiss();
                    Toast.makeText(getActivity(), ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ChangePassword> call, Throwable t) {
                t.printStackTrace();
                ProgressDialog.getInstance().dismiss();
            }
        });
    }
}
