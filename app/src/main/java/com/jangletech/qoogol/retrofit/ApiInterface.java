package com.jangletech.qoogol.retrofit;

import com.jangletech.qoogol.model.CityResponse;
import com.jangletech.qoogol.model.ConnectionResponse;
import com.jangletech.qoogol.model.CountryResponse;
import com.jangletech.qoogol.model.CourseResponse;
import com.jangletech.qoogol.model.DegreeResponse;
import com.jangletech.qoogol.model.DistrictResponse;
import com.jangletech.qoogol.model.FetchSubjectResponseList;
import com.jangletech.qoogol.model.InstituteResponse;
import com.jangletech.qoogol.model.Language;
import com.jangletech.qoogol.model.LearningQuestResponse;
import com.jangletech.qoogol.model.NotificationResponse;
import com.jangletech.qoogol.model.ProcessQuestion;
import com.jangletech.qoogol.model.RegisterLoginModel;
import com.jangletech.qoogol.model.ResponseObj;
import com.jangletech.qoogol.model.SignInModel;
import com.jangletech.qoogol.model.StartResumeTestResponse;
import com.jangletech.qoogol.model.StateResponse;
import com.jangletech.qoogol.model.TestDetailsResponse;
import com.jangletech.qoogol.model.TestListResponse;
import com.jangletech.qoogol.model.UniversityResponse;
import com.jangletech.qoogol.model.UserProfile;
import com.jangletech.qoogol.model.UserProfileResponse;
import com.jangletech.qoogol.model.VerifyResponse;
import com.jangletech.qoogol.util.Constant;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.QueryMap;

public interface ApiInterface {

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

    @FormUrlEncoded
    @POST(Constant.LANGUAGE_API)
    Call<Language> fetchLanguages(@Field(Constant.u_user_id) int userId);

    @FormUrlEncoded
    @POST(Constant.COUNTRY_API)
    Call<CountryResponse> fetchNationalities(@Field(Constant.u_user_id) int userId);

    @FormUrlEncoded
    @POST(Constant.STATE_API)
    Call<StateResponse> fetchStates(@Field(Constant.s_c_id) String s_c_id);

    @FormUrlEncoded
    @POST(Constant.DISTRICT_API)
    Call<DistrictResponse> fetchDistricts(@Field(Constant.dt_s_id) String dt_s_id);

    @FormUrlEncoded
    @POST(Constant.CITY_API)
    Call<CityResponse> fetchCities(@Field(Constant.ct_dt_id) String ct_dt_id);


    @FormUrlEncoded
    @POST(Constant.FETCH_USER_EDU)
    Call<CityResponse> fetchUserEdu(@Field(Constant.u_user_id) String userId);

    @Multipart
    @POST(Constant.UPDATE_PROFILE_IMAGE_API)
    Call<VerifyResponse> updateProfileImage(@Part(Constant.u_user_id) RequestBody userId,
                                          @Part(Constant.device_id) RequestBody deviceId,
                                          @Part(Constant.appName) RequestBody appName,
                                          @Part(Constant.u_app_version) RequestBody appVersion,
                                          @Part(Constant.CASE) RequestBody casel,
                                          @Part MultipartBody.Part image);

    @FormUrlEncoded
    @POST(Constant.REGISTER_LOGIN)
    Call<VerifyResponse> verifyMobileEmail(@Field(Constant.u_app_version) String appVersion,
                                           @Field(Constant.device_id) String deviceId,
                                           @Field(Constant.u_user_type) String userType,
                                           @Field(Constant.u_mob_1) String email,
                                           @Field(Constant.VERIFY) String Verify,
                                           @Field(Constant.CASE) String caseL,
                                           @Field(Constant.appName) String appName);

    @FormUrlEncoded
    @POST(Constant.UPDATE_USER_PROFILE)
    Call<UserProfileResponse> updateUserProfile(@Field(Constant.u_user_id) String userId,
                                                @Field(Constant.appName) String appName,
                                                @Field(Constant.u_app_version) String appVersion,
                                                @Field(Constant.device_id) String deviceId,
                                                @Field(Constant.u_first_name) String firstName,
                                                @Field(Constant.u_last_name) String lastName,
                                                @Field(Constant.CASE) String caseU,
                                                @Field(Constant.u_mob_1) String mobile,
                                                @Field(Constant.u_Email) String email,
                                                @Field(Constant.STATUS) String status,
                                                @Field(Constant.u_Password) String password,
                                                @Field(Constant.u_birth_date) String dob,
                                                @Field(Constant.u_tagline) String tagLine,
                                                @Field(Constant.u_native_ct_id) String u_native_ct_id,
                                                @Field(Constant.u_native_s_id) String u_native_s_id,
                                                @Field(Constant.u_native_dt_id) String u_native_dt_id,
                                                @Field(Constant.u_nationality) String u_nationality,
                                                @Field(Constant.w_lm_id_array) String w_lm_id_array,
                                                @Field(Constant.u_gender) String gender);

    @FormUrlEncoded
    @POST(Constant.FETCH_USER_INFO)
    Call<UserProfile> fetchUserInfo(@Field(Constant.u_user_id) int userId,
                                    @Field(Constant.device_id) String deviceId,
                                    @Field(Constant.appName) String appName,
                                    @Field(Constant.u_app_version) String app_version);

    @FormUrlEncoded
    @POST(Constant.FETCH_TEST_LIST)
    Call<TestListResponse> fetchTestList(@Field(Constant.u_user_id) int userId);

    @FormUrlEncoded
    @POST(Constant.FETCH_NOTIFICATIONS)
    Call<NotificationResponse> fetchNotifications(@Field(Constant.u_user_id) int userId,
                                                  @Field("126Q") String deviceId,
                                                  @Field("200Q") String appName);

    @FormUrlEncoded
    @POST(Constant.FETCH_SUBJECTS)
    Call<FetchSubjectResponseList> fetchSubjectList(@Field(Constant.u_user_id) int userId);

    @FormUrlEncoded
    @POST(Constant.TEST_DETAILS)
    Call<TestDetailsResponse> fetchTestDetails(@Field(Constant.u_user_id) int userId,
                                               @Field(Constant.tm_id) int tmId);

    @FormUrlEncoded
    @POST(Constant.START_RESUME_TEST)
    Call<StartResumeTestResponse> fetchTestQuestionAnswers(@Field(Constant.u_user_id) int userId,
                                                           @Field(Constant.tm_id) int testMasterId);

    @FormUrlEncoded
    @POST(Constant.PROCESS_TEST)
    Call<ProcessQuestion> addTestLike(@Field(Constant.u_user_id) int userid,
                                      @Field(Constant.tm_id) int tmId,
                                      @Field("Case") String caseL,
                                      @Field(Constant.qlc_like_flag) int like);


    @FormUrlEncoded
    @POST(Constant.REGISTER_LOGIN)
    Call<RegisterLoginModel> doRegisterLogin(@Field(Constant.u_mob_1) String mobile,
                                             @Field("Case") String caseR,
                                             @Field(Constant.u_calling_code) int countryCode,
                                             @Field(Constant.u_Password) String password,
                                             @Field(Constant.device_id) String deviceId,
                                             @Field(Constant.appName) String appName);

    @FormUrlEncoded
    @POST(Constant.FETCH_QA)
    Call<LearningQuestResponse> fetchQAApi(@Field(Constant.u_user_id) int userid);

    @FormUrlEncoded
    @POST(Constant.PROCESS_QUESTION)
    Call<ProcessQuestion> fetchComments(@Field(Constant.u_user_id) int userid,
                                        @Field(Constant.q_id) String queId,
                                        @Field("Case") String caseL);

    @FormUrlEncoded
    @POST(Constant.PROCESS_TEST)
    Call<ProcessQuestion> fetchTestComments(@Field(Constant.u_user_id) int userid,
                                            @Field(Constant.tm_id) int tmId,
                                            @Field("Case") String caseL);

    @FormUrlEncoded
    @POST(Constant.PROCESS_QUESTION)
    Call<ProcessQuestion> likeApi(@Field(Constant.u_user_id) int userid,
                                  @Field(Constant.q_id) String queId,
                                  @Field("Case") String caseL,
                                  @Field(Constant.qlc_like_flag) int like);

    @FormUrlEncoded
    @POST(Constant.PROCESS_QUESTION)
    Call<ProcessQuestion> favApi(@Field(Constant.u_user_id) int userid,
                                 @Field(Constant.q_id) String queId,
                                 @Field("Case") String caseL,
                                 @Field(Constant.qlc_fav_flag) int like);

    @FormUrlEncoded
    @POST(Constant.PROCESS_QUESTION)
    Call<ProcessQuestion> addCommentApi(@Field(Constant.u_user_id) int userid,
                                        @Field(Constant.q_id) String queId,
                                        @Field("Case") String caseL,
                                        @Field(Constant.qlc_comment_text) String comment);


    @FormUrlEncoded
    @POST(Constant.FETCH_CONNECTIONS)
    Call<ConnectionResponse> fetchConnections(@Field(Constant.u_user_id) String userid,
                                              @Field("Case") String connectionCase,
                                              @Field(Constant.device_id) String device_id,
                                              @Field("200Q") String app,
                                              @Field(Constant.pagestart) int pagestart);

    @FormUrlEncoded
    @POST(Constant.FETCH_CONNECTIONS)
    Call<ConnectionResponse> fetchRefreshedConnections(@Field(Constant.u_user_id) String userid,
                                                       @Field("Case") String connectionCase,
                                                       @Field(Constant.device_id) String device_id,
                                                       @Field("200Q") String app,
                                                       @Field(Constant.pagestart) int pagestart,
                                                       @Field("ForceRefresh") String refresh);


    @FormUrlEncoded
    @POST(Constant.UPDATE_CONNECTIONS)
    Call<ResponseObj> updateConnections(@Field(Constant.u_user_id) String userid,
                                        @Field("Case") String connectionCase,
                                        @Field(Constant.device_id) String device_id,
                                        @Field("200Q") String app,
                                        @Field(Constant.other_user) String other_user);

    @FormUrlEncoded
    @POST(Constant.PROCESS_TEST)
    Call<ProcessQuestion> addTestCommentApi(@Field(Constant.u_user_id) int userid,
                                            @Field(Constant.tm_id) int tmId,
                                            @Field("Case") String caseL,
                                            @Field(Constant.tlc_comment_text) String comment);

}
