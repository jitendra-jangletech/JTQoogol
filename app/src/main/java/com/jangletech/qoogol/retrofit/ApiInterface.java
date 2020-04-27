package com.jangletech.qoogol.retrofit;

import com.jangletech.qoogol.model.ChangePassword;
import com.jangletech.qoogol.model.City;
import com.jangletech.qoogol.model.CityResponse;
import com.jangletech.qoogol.model.Classes;
import com.jangletech.qoogol.model.CommonResponseObject;
import com.jangletech.qoogol.model.Country;
import com.jangletech.qoogol.model.CountryResponse;
import com.jangletech.qoogol.model.Course;
import com.jangletech.qoogol.model.DashboardInfo;
import com.jangletech.qoogol.model.Degree;
import com.jangletech.qoogol.model.EmptyObject;
import com.jangletech.qoogol.model.Exams;
import com.jangletech.qoogol.model.FetchEducationsResponseDto;
import com.jangletech.qoogol.model.FetchPreferableExamsResponseDto;
import com.jangletech.qoogol.model.GetUserPersonalDetails;
import com.jangletech.qoogol.model.Institute;
import com.jangletech.qoogol.model.LearningQuestResponse;
import com.jangletech.qoogol.model.LearningQuestionsNew;
import com.jangletech.qoogol.model.MobileOtp;
import com.jangletech.qoogol.model.SignInModel;
import com.jangletech.qoogol.model.State;
import com.jangletech.qoogol.model.StateResponse;
import com.jangletech.qoogol.model.TestingQuestionNew;
import com.jangletech.qoogol.model.TestingRequestDto;
import com.jangletech.qoogol.model.University;
import com.jangletech.qoogol.model.UserSelectedExams;
import com.jangletech.qoogol.model.signup.SignUpResponseDto;
import com.jangletech.qoogol.util.Constant;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by Pritali on 1/30/2020.
 */
public interface ApiInterface {

    //Todo Change Master Data Url

    @POST("auth/signInNew")
    Call<SignInModel> signCall(@QueryMap Map<String, String> request);

    @POST(Constant.COUNTRY)
    Call<CountryResponse> getCountries();

    @POST(Constant.STATE)
    Call<StateResponse> getStates();

    @POST(Constant.CITY)
    Call<CityResponse> getCities();

    @POST("auth/getUnivBoard")
    Call<List<University>> getUniversity(@QueryMap Map<String, Integer> request);

    @POST("auth/getInstOrg")
    Call<List<Institute>> getInstitute(@QueryMap Map<String, Integer> request);

    @POST("auth/degreeList")
    Call<List<Degree>> getDegrees();

    @POST("auth/coursesForDegree")
    Call<List<Course>> getCourses(@QueryMap Map<String, Integer> request);

    @POST("masterData/classesForDegreeNew")
    Call<Classes> getClasses(@QueryMap Map<String, Object> request);

    @POST("auth/signUpNew")
    Call<SignUpResponseDto> signUpApi(@QueryMap Map<String, Object> request);

    @POST("auth/verifyMobileNo")
    Call<MobileOtp> getMobileOtp(@QueryMap Map<String, String> request);

    @POST("auth/verifyEmail")
    Call<MobileOtp> getEmailOtp(@QueryMap Map<String, String> request);

    @POST("user/fetchEduDetails")
    Call<FetchEducationsResponseDto> getEducationDetails(@QueryMap Map<String, String> request);

    @POST("user/getApplicableExams")
    Call<FetchPreferableExamsResponseDto> getApplicableExams(@QueryMap Map<String, String> request);

    @POST("user/fetchUserSelectedExams")
    Call<UserSelectedExams> getUserSelectedExams(@QueryMap Map<String, Integer> request);

    @POST("dashboard/dashboardStats")
    Call<DashboardInfo> getDashboardStatistics(@QueryMap Map<String, String> request);

    @POST("user/fetchPersonalDetails")
    Call<GetUserPersonalDetails> getPersonalDetails(@QueryMap Map<String, String> request);

    @POST("user/deleteEduDetails")
    Call<EmptyObject> deleteEduDetails(@QueryMap Map<String, Object> request);

    @POST("user/updatePassword")
    Call<ChangePassword> changePassword(@QueryMap Map<String, String> request);

    @POST("user/updatePersonalDetails")
    Call<GetUserPersonalDetails> updatePersonalDetails(@QueryMap Map<String, String> request);

    @POST("user/saveUserSelectedExams")
    Call<CommonResponseObject> saveUserSelectedExams(@Query("userId") int userId,
                                                     @Body List<Exams> selectedExamsList);
    @FormUrlEncoded
    @POST("q031StartResumeTest")
    Call<TestingQuestionNew> fetchTestQuestionAnswers(@Field(Constant.u_user_id) int userId,
                                                      @Field(Constant.tt_id) int testTakenId,
                                                      @Field(Constant.tt_tm_id) int testTakentmId);

    @POST(Constant.FETCH_QA)
    Call<LearningQuestResponse> fetchQAApi(@QueryMap Map<String, Object> request);

}
