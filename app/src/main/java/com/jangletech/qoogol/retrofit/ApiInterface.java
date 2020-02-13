package com.jangletech.qoogol.retrofit;

import com.jangletech.qoogol.model.Classes;
import com.jangletech.qoogol.model.Country;
import com.jangletech.qoogol.model.Course;
import com.jangletech.qoogol.model.Degree;
import com.jangletech.qoogol.model.Institute;
import com.jangletech.qoogol.model.MobileOtp;
import com.jangletech.qoogol.model.SignInModel;
import com.jangletech.qoogol.model.SignUp;
import com.jangletech.qoogol.model.State;
import com.jangletech.qoogol.model.University;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;

/**
 * Created by Pritali on 1/30/2020.
 */
public interface ApiInterface {

    @POST("auth/signInNew")
    Call<SignInModel> signCall(@QueryMap Map<String, String> request);

    @POST("auth/countryList")
    Call<List<Country>> getCountries();

    @POST("auth/statesForCountry")
    Call<List<State>> getStates(@QueryMap Map<String, Integer> request);

    @POST("auth/getUnivBoard")
    Call<List<University>> getUniversity(@QueryMap Map<String, Integer> request);

    @POST("auth/getInstOrg")
    Call<List<Institute>> getInstitute(@QueryMap Map<String, Integer> request);

    @POST("auth/degreeList")
    Call<List<Degree>> getDegrees();

    @POST("auth/coursesForDegree")
    Call<List<Course>> getCourses(@QueryMap Map<String, Integer> request);

    @POST("auth/classesForDegreeNew")
    Call<Classes> getClasses(@QueryMap Map<String, Object> request);

    @POST("auth/signUpNew")
    Call<SignUp> signUpApi(@QueryMap Map<String, Object> request);

//    @POST("auth/generateAndSendOtpMobile")
    @POST("auth/verifyMobileNo")
    Call<MobileOtp> getMobileOtp(@QueryMap Map<String, String> request);

    @POST("user/fetchUserAccountDetails")
    Call<SignUp> getAccountDetails(@QueryMap Map<String, Integer> request);


}
