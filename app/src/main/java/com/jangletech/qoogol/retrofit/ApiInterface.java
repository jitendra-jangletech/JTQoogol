package com.jangletech.qoogol.retrofit;

import com.jangletech.qoogol.model.ChangePassword;
import com.jangletech.qoogol.model.CityResponse;
import com.jangletech.qoogol.model.Classes;
import com.jangletech.qoogol.model.CommonResponseObject;
import com.jangletech.qoogol.model.CountryResponse;
import com.jangletech.qoogol.model.CourseResponse;
import com.jangletech.qoogol.model.DashboardInfo;
import com.jangletech.qoogol.model.DegreeResponse;
import com.jangletech.qoogol.model.EmptyObject;
import com.jangletech.qoogol.model.Exams;
import com.jangletech.qoogol.model.FetchEducationsResponseDto;
import com.jangletech.qoogol.model.FetchPreferableExamsResponseDto;
import com.jangletech.qoogol.model.FetchSubjectResponse;
import com.jangletech.qoogol.model.FetchSubjectResponseList;
import com.jangletech.qoogol.model.GetUserPersonalDetails;
import com.jangletech.qoogol.model.InstituteResponse;
import com.jangletech.qoogol.model.LearningQuestResponse;
import com.jangletech.qoogol.model.MobileOtp;
import com.jangletech.qoogol.model.NotificationResponse;
import com.jangletech.qoogol.model.ProcessQuestion;
import com.jangletech.qoogol.model.ResponseObj;
import com.jangletech.qoogol.model.SignInModel;
import com.jangletech.qoogol.model.StartResumeTestResponse;
import com.jangletech.qoogol.model.StateResponse;
import com.jangletech.qoogol.model.TestDetailsResponse;
import com.jangletech.qoogol.model.TestListResponse;
import com.jangletech.qoogol.model.UniversityResponse;
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

    @POST(Constant.UNIVERSITY)
    Call<UniversityResponse> getUniversity();

    @POST(Constant.INSTITUTE)
    Call<InstituteResponse> getInstitute();

    @POST(Constant.DEGREE)
    Call<DegreeResponse> getDegrees();

    @POST(Constant.COURSE)
    Call<CourseResponse> getCourses();

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
    @POST("q152fetchtest")
    Call<TestListResponse> fetchTestList(@Field(Constant.u_user_id) int userId);

    @FormUrlEncoded
    @POST("sm28FetchNotifications")
    Call<NotificationResponse> fetchNotifications(@Field(Constant.u_user_id) int userId,
                                                  @Field("126") String deviceId);


    @FormUrlEncoded
    @POST("q111FetchSubjectMaster")
    Call<FetchSubjectResponseList> fetchSubjectList(@Field(Constant.u_user_id) int userId);

    @FormUrlEncoded
    @POST("q132FetchTestDetails")
    Call<TestDetailsResponse> fetchTestDetails(@Field(Constant.u_user_id) int userId,
                                               @Field(Constant.tm_id) int tmId);

    @FormUrlEncoded
    @POST("q131StartResumeTest")
    Call<StartResumeTestResponse> fetchTestQuestionAnswers(@Field(Constant.u_user_id) int userId,
                                                           @Field(Constant.tm_id) int testMasterId);

    @POST(Constant.FETCH_QA)
    Call<LearningQuestResponse> fetchQAApi(@Field(Constant.u_user_id) String userid);

    @FormUrlEncoded
    @POST(Constant.PROCESS_QUESTION)
    Call<ProcessQuestion> fetchComments(@Field(Constant.u_user_id) String userid, @Field(Constant.q_id) String queId, @Field("Case") String caseL);

    @FormUrlEncoded
    @POST(Constant.PROCESS_QUESTION)
    Call<ProcessQuestion> likeApi(@Field(Constant.u_user_id) String userid, @Field(Constant.q_id) String queId, @Field("Case") String caseL, @Field(Constant.qlc_like_flag) int like);


    @FormUrlEncoded
    @POST(Constant.PROCESS_QUESTION)
    Call<ProcessQuestion> favApi(@Field(Constant.u_user_id) String userid, @Field(Constant.q_id) String queId, @Field("Case") String caseL, @Field(Constant.qlc_fav_flag) int like);


    @FormUrlEncoded
    @POST(Constant.PROCESS_QUESTION)
    Call<ProcessQuestion> addCommentApi(@Field(Constant.u_user_id) String userid, @Field(Constant.q_id) String queId, @Field("Case") String caseL, @Field(Constant.qlc_comment_text) String comment);



}
