package com.jangletech.qoogol.retrofit;

import com.jangletech.qoogol.model.AddElementResponse;
import com.jangletech.qoogol.model.BlockedConnResp;
import com.jangletech.qoogol.model.ChapterResponse;
import com.jangletech.qoogol.model.CityResponse;
import com.jangletech.qoogol.model.ClassList;
import com.jangletech.qoogol.model.ConnectionResponse;
import com.jangletech.qoogol.model.ContactResponse;
import com.jangletech.qoogol.model.CountryResponse;
import com.jangletech.qoogol.model.CourseResponse;
import com.jangletech.qoogol.model.DashBoard;
import com.jangletech.qoogol.model.DegreeResponse;
import com.jangletech.qoogol.model.DistrictResponse;
import com.jangletech.qoogol.model.FaqResponse;
import com.jangletech.qoogol.model.FetchEducationResponse;
import com.jangletech.qoogol.model.FetchSubjectResponseList;
import com.jangletech.qoogol.model.FollowRequestResponse;
import com.jangletech.qoogol.model.FollowersResponse;
import com.jangletech.qoogol.model.FollowingResponse;
import com.jangletech.qoogol.model.FriendRequestResponse;
import com.jangletech.qoogol.model.FriendsResponse;
import com.jangletech.qoogol.model.GenerateVerifyUserName;
import com.jangletech.qoogol.model.InstituteResponse;
import com.jangletech.qoogol.model.Language;
import com.jangletech.qoogol.model.LearningQuestResponse;
import com.jangletech.qoogol.model.NotificationResponse;
import com.jangletech.qoogol.model.ProcessQuestion;
import com.jangletech.qoogol.model.RegisterLoginModel;
import com.jangletech.qoogol.model.ResponseObj;
import com.jangletech.qoogol.model.SaveQuestResponse;
import com.jangletech.qoogol.model.SendInviteResponse;
import com.jangletech.qoogol.model.ShareResponse;
import com.jangletech.qoogol.model.ShareUserResponse;
import com.jangletech.qoogol.model.StartResumeTestResponse;
import com.jangletech.qoogol.model.StateResponse;
import com.jangletech.qoogol.model.TestDetailsResponse;
import com.jangletech.qoogol.model.TestListResponse;
import com.jangletech.qoogol.model.UniversityResponse;
import com.jangletech.qoogol.model.UserProfile;
import com.jangletech.qoogol.model.UserProfileResponse;
import com.jangletech.qoogol.model.VerifyResponse;
import com.jangletech.qoogol.util.Constant;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {

    @POST(Constant.UNIVERSITY)
    Call<UniversityResponse> getUniversity();

    @POST(Constant.INSTITUTE)
    Call<InstituteResponse> getInstitute();

    @POST(Constant.DEGREE)
    Call<DegreeResponse> getDegrees();

    @POST(Constant.COURSE)
    Call<CourseResponse> getCourses();

    @FormUrlEncoded
    @POST(Constant.FETCH_FAQ)
    Call<FaqResponse> fetchFaq(@Field(Constant.u_user_id) String userId,
                               @Field(Constant.CASE) String caseFaq,
                               @Field(Constant.faq_app) String faq_app,
                               @Field(Constant.faqt_id) String faqTopicId);

    @FormUrlEncoded
    @POST(Constant.LANGUAGE_API)
    Call<Language> fetchLanguages(@Field(Constant.u_user_id) String userId);

    @FormUrlEncoded
    @POST(Constant.COUNTRY_API)
    Call<CountryResponse> fetchNationalities(@Field(Constant.u_user_id) String userId);

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
    @POST(Constant.CLASS_MASTER)
    Call<ClassList> fetchClasses(@Field(Constant.co_id) String courseId);

    @FormUrlEncoded
    @POST(Constant.ADD_UNIVERSITY)
    Call<AddElementResponse> addUniversity(@Field(Constant.ubm_board_name) String boardName);

    @FormUrlEncoded
    @POST(Constant.ADD_INSTITUTE)
    Call<AddElementResponse> addInstitute(@Field(Constant.iom_name) String instituteName);

    @FormUrlEncoded
    @POST(Constant.FETCH_USER_SETTINGS)
    Call<VerifyResponse> fetchUserSettings(@Field(Constant.u_user_id) String userId,
                                           @Field(Constant.device_id) String deviceId,
                                           @Field(Constant.appName) String appName,
                                           @Field(Constant.CASE) String CaseL);

    @FormUrlEncoded
    @POST(Constant.FETCH_USER_SETTINGS)
    Call<VerifyResponse> updateUserSettings(@Field(Constant.u_user_id) String userId,
                                            @Field(Constant.device_id) String deviceId,
                                            @Field(Constant.appName) String appName,
                                            @Field(Constant.CASE) String casel,
                                            @Field(Constant.ubm_id) String ubmId,
                                            @Field(Constant.iom_id) String iomId,
                                            @Field(Constant.co_id) String coId,
                                            @Field(Constant.dm_id) String dmId);


    @FormUrlEncoded
    @POST(Constant.FETCH_USER_EDU)
    Call<VerifyResponse> updateUserEdu(@Field(Constant.u_user_id) String userId,
                                       @Field(Constant.CASE) String casel,
                                       @Field(Constant.device_id) String deviceId,
                                       @Field(Constant.appName) String appName,
                                       @Field(Constant.ue_startdate) String satrtDate,
                                       @Field(Constant.ue_enddate) String endDate,
                                       @Field(Constant.ue_marks) String marks,
                                       @Field(Constant.ue_grade) String grade,
                                       @Field(Constant.ubm_id) String universityId,
                                       @Field(Constant.iom_id) String instituteId,
                                       @Field(Constant.co_id) String courseId,
                                       @Field(Constant.dm_id) String dmId,
                                       @Field(Constant.ue_id) String ueId,
                                       @Field(Constant.ue_cy_num) String cyNum);

    @FormUrlEncoded
    @POST(Constant.FETCH_USER_EDU)
    Call<FetchEducationResponse> fetchUserEdu(@Field(Constant.u_user_id) String userId,
                                              @Field(Constant.CASE) String casel,
                                              @Field(Constant.device_id) String deviceId,
                                              @Field(Constant.appName) String appName);

    @FormUrlEncoded
    @POST(Constant.FETCH_USER_EDU)
    Call<FetchEducationResponse> fetchOtherUSersUserEdu(@Field(Constant.u_user_id) String userId,
                                                        @Field(Constant.CASE) String casel,
                                                        @Field(Constant.device_id) String deviceId,
                                                        @Field(Constant.appName) String appName,
                                                        @Field(Constant.other_user) String other_user);

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
    Call<VerifyResponse> verifyMobileEmail(@Field(Constant.appName) String appName,
                                           @Field(Constant.u_app_version) String appVersion,
                                           @Field(Constant.device_id) String deviceId,
                                           @Field(Constant.u_user_type) String userType,
                                           @Field(Constant.u_mob_1) String email,
                                           @Field(Constant.VERIFY) String Verify,
                                           @Field(Constant.CASE) String caseL);

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
                                                @Field(Constant.u_gender) String gender,
                                                @Field(Constant.userName) String userName
    );

    @FormUrlEncoded
    @POST(Constant.FETCH_USER_INFO)
    Call<UserProfile> fetchUserInfo(@Field(Constant.u_user_id) String userId,
                                    @Field(Constant.device_id) String deviceId,
                                    @Field(Constant.appName) String appName,
                                    @Field(Constant.u_app_version) String app_version);

    @FormUrlEncoded
    @POST(Constant.DASHBOARD)
    Call<DashBoard> fetchDashBoardDetails(@Field(Constant.u_user_id) String userId,
                                          @Field(Constant.device_id) String deviceId);

    @FormUrlEncoded
    @POST(Constant.FETCH_OTHER_USER_INFO)
    Call<UserProfile> fetchOtherUsersInfo(@Field(Constant.u_user_id) String userId,
                                          @Field(Constant.device_id) String deviceId,
                                          @Field(Constant.appName) String appName,
                                          @Field(Constant.u_app_version) String app_version,
                                          @Field(Constant.other_user) String other_user,
                                          @Field(Constant.CASE) String caseU);

    @FormUrlEncoded
    @POST(Constant.FETCH_USER_EDU)
    Call<VerifyResponse> deleteEdu(@Field(Constant.u_user_id) String userId,
                                   @Field(Constant.ue_id) String ueId,
                                   @Field(Constant.CASE) String caseDel);

    @FormUrlEncoded
    @POST(Constant.FETCH_TEST_LIST)
    Call<TestListResponse> fetchTestList(@Field(Constant.u_user_id) String userId,
                                         @Field(Constant.CASE) String caseFav,
                                         @Field(Constant.tm_recent_test) String recentTest,
                                         @Field(Constant.tm_popular_test) String popularTest,
                                         @Field(Constant.tm_diff_level) String diffLevel,
                                         @Field(Constant.tm_avg_rating) String avgRating,
                                         @Field(Constant.tm_id) String tmId,
                                         @Field(Constant.tm_catg) String cat,
                                         @Field(Constant.pagestart) String pageCount
    );

    @FormUrlEncoded
    @POST(Constant.FETCH_TEST_LIST)
    Call<TestListResponse> fetchFilteredTestList(@Field(Constant.u_user_id) String userId,
                                                 @Field(Constant.tm_id) String tmId,
                                                 @Field(Constant.tm_recent_test) String recentTest,
                                                 @Field(Constant.tm_popular_test) String popularTest,
                                                 @Field(Constant.tm_diff_level) String diffLevel,
                                                 @Field(Constant.tm_catg) String categories,
                                                 @Field(Constant.tm_avg_rating) String avgRating);


    @FormUrlEncoded
    @POST(Constant.FETCH_NOTIFICATIONS)
    Call<NotificationResponse> fetchNotifications(@Field(Constant.u_user_id) String userId,
                                                  @Field(Constant.device_id) String deviceId,
                                                  @Field(Constant.appName) String appName,
                                                  @Field(Constant.pagestart) String pageStart);

    @FormUrlEncoded
    @POST(Constant.UPDATE_NOTIFICATIONS)
    Call<ResponseObj> updateNotifications(@Field(Constant.u_user_id) String userId,
                                          @Field(Constant.device_id) String deviceId,
                                          @Field(Constant.appName) String appName,
                                          @Field(Constant.n_id) String nid,
                                          @Field(Constant.CASE) String update_case);

    @FormUrlEncoded
    @POST(Constant.FETCH_SUBJECTS)
    Call<FetchSubjectResponseList> fetchSubjectList(@Field(Constant.scr_co_id) String scr_co_id);


    @FormUrlEncoded
    @POST(Constant.FETCH_CHAPTERS)
    Call<ChapterResponse> fetchChapterList(@Field(Constant.u_user_id) int userId);


    @FormUrlEncoded
    @POST(Constant.TEST_DETAILS)
    Call<TestDetailsResponse> fetchTestDetails(@Field(Constant.u_user_id) String userId,
                                               @Field(Constant.tm_id) String tmId);

    @FormUrlEncoded
    @POST(Constant.SUBMIT_TEST)
    Call<VerifyResponse> submitTestQuestion(@Field(Constant.DataList) String json,
                                            @Field(Constant.u_user_id) String uId);


    @FormUrlEncoded
    @POST(Constant.START_RESUME_TEST)
    Call<StartResumeTestResponse> fetchTestQuestionAnswers(@Field(Constant.u_user_id) int userId,
                                                           @Field(Constant.tm_id) int testMasterId);

    @FormUrlEncoded
    @POST(Constant.START_RESUME_TEST)
    Call<StartResumeTestResponse> fetchAttemptedTests(@Field(Constant.u_user_id) int userId,
                                                      @Field(Constant.tt_id) int ttId);

    @FormUrlEncoded
    @POST(Constant.PROCESS_TEST)
    Call<ProcessQuestion> addTestLike(@Field(Constant.u_user_id) int userid,
                                      @Field(Constant.tm_id) int tmId,
                                      @Field(Constant.CASE) String caseL,
                                      @Field(Constant.tlc_like_flag) int like);

    @FormUrlEncoded
    @POST(Constant.PROCESS_QUESTION)
    Call<ProcessQuestion> likeReplyQuestComment(@Field(Constant.u_user_id) String userid,
                                                @Field(Constant.qlc_id) int qlcId,
                                                @Field(Constant.CASE) String caseL,
                                                @Field(Constant.qlc_like_flag) String questLike,
                                                @Field(Constant.quest_comment_reply_id) String replyId,
                                                @Field(Constant.qlc_comment_text) String text);

    @FormUrlEncoded
    @POST(Constant.PROCESS_TEST)
    Call<ProcessQuestion> likeTestComment(@Field(Constant.u_user_id) String userid,
                                          @Field(Constant.tm_id) int tmId,
                                          @Field(Constant.CASE) String caseL,
                                          @Field(Constant.tlc_like_flag) String flag,
                                          @Field(Constant.test_comment_reply_id) String replyId,
                                          @Field(Constant.tlc_comment_text) String text);


    @FormUrlEncoded
    @POST(Constant.PROCESS_TEST)
    Call<ProcessQuestion> deleteTestComment(@Field(Constant.u_user_id) String userid,
                                          @Field(Constant.tm_id) int tmId,
                                          @Field(Constant.CASE) String caseL,
                                          @Field(Constant.tlc_comment_flag) String flag,
                                          @Field(Constant.tlc_id) String commentId);

    @FormUrlEncoded
    @POST(Constant.PROCESS_QUESTION)
    Call<ProcessQuestion> deleteQuestComment(@Field(Constant.u_user_id) String userid,
                                            @Field(Constant.q_id) int qId,
                                            @Field(Constant.CASE) String caseL,
                                            @Field(Constant.qlc_comment_flag) String flag,
                                            @Field(Constant.qlc_id) String commentId);


    @FormUrlEncoded
    @POST(Constant.PROCESS_TEST)
    Call<ProcessQuestion> replyTestComment(@Field(Constant.u_user_id) String userid,
                                           @Field(Constant.tm_id) int tmId,
                                           @Field(Constant.CASE) String caseL,
                                           @Field(Constant.tlc_comment_flag) String flag,
                                           @Field(Constant.test_comment_reply_id) String replyId,
                                           @Field(Constant.tlc_comment_text) String text);

    @FormUrlEncoded
    @POST(Constant.PROCESS_QUESTION)
    Call<ProcessQuestion> replyQuestComment(@Field(Constant.u_user_id) String userid,
                                            @Field(Constant.qlc_id) int qlcId,
                                            @Field(Constant.CASE) String caseL,
                                            @Field(Constant.qlc_comment_flag) String flag,
                                            @Field(Constant.quest_comment_reply_id) String replyId,
                                            @Field(Constant.qlc_comment_text) String text);


    @FormUrlEncoded
    @POST(Constant.PROCESS_TEST)
    Call<ProcessQuestion> addFavTest(@Field(Constant.u_user_id) int userid,
                                     @Field(Constant.tm_id) int tmId,
                                     @Field(Constant.CASE) String caseL,
                                     @Field(Constant.isFavourite) int fav);


    @FormUrlEncoded
    @POST(Constant.REGISTER_LOGIN)
    Call<RegisterLoginModel> doRegisterLogin(@Field(Constant.u_mob_1) String mobile,
                                             @Field(Constant.CASE) String caseR,
                                             @Field(Constant.u_calling_code) int countryCode,
                                             @Field(Constant.u_Password) String password,
                                             @Field(Constant.device_id) String deviceId,
                                             @Field(Constant.appName) String appName,
                                             @Field(Constant.u_fcm_token) String token,
                                             @Field(Constant.CASE2) String case2);

    @FormUrlEncoded
    @POST(Constant.REGISTER_LOGIN)
    Call<RegisterLoginModel> doRegister(@Field(Constant.u_first_name) String fname,
                                        @Field(Constant.u_last_name) String lname,
                                        @Field(Constant.u_gender) String gender,
                                        @Field(Constant.u_birth_date) String dob,
                                        @Field(Constant.u_mob_1) String mobileEmail,
                                        @Field(Constant.OTP) String otp,
                                        @Field(Constant.u_Password) String password,
                                        @Field(Constant.CASE) String caseR,
                                        @Field(Constant.u_calling_code) int countryCode,
                                        @Field(Constant.device_id) String deviceId,
                                        @Field(Constant.appName) String appName,
                                        @Field(Constant.u_fcm_token) String token,
                                        @Field(Constant.CASE2) String case2);

    @FormUrlEncoded
    @POST(Constant.FETCH_QA)
    Call<LearningQuestResponse> fetchQAApi(@Field(Constant.u_user_id) String userid,
                                           @Field(Constant.q_id) String question,
                                           @Field(Constant.q_avg_ratings) String ratings,
                                           @Field(Constant.q_diff_level) String diff_level,
                                           @Field(Constant.q_trending) String trending,
                                           @Field(Constant.q_popular) String popular,
                                           @Field(Constant.q_recent) String recent,
                                           @Field(Constant.q_type) String question_type,
                                           @Field(Constant.q_option_type) String option_type
                                            );

    @FormUrlEncoded
    @POST(Constant.FETCH_QA)
    Call<LearningQuestResponse> fetchQAApi(@Field(Constant.u_user_id) String userid, @Field(Constant.q_id) String question,
                                           @Field(Constant.CASE) String CASE,
                                           @Field(Constant.q_avg_ratings) String ratings,
                                           @Field(Constant.q_diff_level) String diff_level,
                                           @Field(Constant.q_trending) String trending,
                                           @Field(Constant.q_popular) String popular,
                                           @Field(Constant.q_recent) String recent,
                                           @Field(Constant.q_type) String question_type,
                                           @Field(Constant.q_option_type) String option_type);

    @FormUrlEncoded
    @POST(Constant.FETCH_QA)
    Call<SaveQuestResponse> fetchSavedQAApi(@Field(Constant.u_user_id) String userid,
                                            @Field(Constant.CASE) String caseR);

    @FormUrlEncoded
    @POST(Constant.FETCH_QA)
    Call<LearningQuestResponse> fetchFavQAApi(@Field(Constant.u_user_id) String userid,
                                              @Field(Constant.CASE) String caseR);


    Call<LearningQuestResponse> fetchQAApi(@Field(Constant.u_user_id) int userid,
                                           @Field(Constant.q_id) String question);

    @FormUrlEncoded
    @POST(Constant.REFER)
    Call<GenerateVerifyUserName> generateVerifyUserName(@Field(Constant.u_first_name) String fName,
                                                        @Field(Constant.u_last_name) String lName,
                                                        @Field(Constant.CASE) String caseG,
                                                        @Field(Constant.userName) String userName);

    @FormUrlEncoded
    @POST(Constant.PROCESS_QUESTION)
    Call<ProcessQuestion> fetchLikes(@Field(Constant.u_user_id) int userid,
                                     @Field(Constant.q_id) int queId,
                                     @Field(Constant.CASE) String caseL,
                                     @Field(Constant.qlc_like_flag) int flag);

    @FormUrlEncoded
    @POST(Constant.PROCESS_QUESTION)
    Call<ProcessQuestion> fetchComments(@Field(Constant.u_user_id) int userid,
                                        @Field(Constant.q_id) int queId,
                                        @Field(Constant.CASE) String caseL,
                                        @Field(Constant.qlc_comment_flag) int flag);

    @FormUrlEncoded
    @POST(Constant.FETCH_SHAREDTQ_USERS)
    Call<ShareUserResponse> fetchSharedByUsers(@Field(Constant.u_user_id) String userid,
                                               @Field(Constant.q_id) int queId,
                                               @Field(Constant.device_id) String device_id,
                                               @Field(Constant.stq_by_user_id) String user);

    @FormUrlEncoded
    @POST(Constant.FETCH_SHAREDTQ_USERS)
    Call<ShareUserResponse> fetchSharedToUsers(@Field(Constant.u_user_id) String userid,
                                               @Field(Constant.q_id) int queId,
                                               @Field(Constant.device_id) String device_id,
                                               @Field(Constant.stq_to_user_id) String user);

    @FormUrlEncoded
    @POST(Constant.PROCESS_TEST)
    Call<ProcessQuestion> fetchTestComments(@Field(Constant.u_user_id) int userid,
                                            @Field(Constant.tm_id) int tmId,
                                            @Field(Constant.CASE) String caseL,
                                            @Field(Constant.tlc_comment_flag) int flag);

    @FormUrlEncoded
    @POST(Constant.PROCESS_TEST)
    Call<ProcessQuestion> submitTestFeedBack(@Field(Constant.u_user_id) String userid,
                                             @Field(Constant.tlc_rating) String rating,
                                             @Field(Constant.tlc_feedback) String feedback,
                                             @Field(Constant.tt_id) String tmId,
                                             @Field(Constant.CASE) String caseL);

    @FormUrlEncoded
    @POST(Constant.PROCESS_QUESTION)
    Call<ProcessQuestion> likeApi(@Field(Constant.u_user_id) int userid,
                                  @Field(Constant.q_id) int queId,
                                  @Field(Constant.CASE) String caseL,
                                  @Field(Constant.qlc_like_flag) int like);

    @FormUrlEncoded
    @POST(Constant.PROCESS_QUESTION)
    Call<ProcessQuestion> saveQueApi(@Field(Constant.u_user_id) int userid,
                                     @Field(Constant.q_id) int queId,
                                     @Field(Constant.CASE) String caseL,
                                     @Field(Constant.qlc_save_flag) int save);

    @FormUrlEncoded
    @POST(Constant.PROCESS_QUESTION)
    Call<ProcessQuestion> favApi(@Field(Constant.u_user_id) int userid,
                                 @Field(Constant.q_id) int queId,
                                 @Field(Constant.CASE) String caseL,
                                 @Field(Constant.qlc_fav_flag) int like);

    @FormUrlEncoded
    @POST(Constant.PROCESS_QUESTION)
    Call<ProcessQuestion> addRatingsApi(@Field(Constant.u_user_id) int userid,
                                        @Field(Constant.q_id) int queId,
                                        @Field("Case") String caseL,
                                        @Field(Constant.qlc_rating) String ratings,
                                        @Field(Constant.qlc_feedback) String feedback);

    @FormUrlEncoded
    @POST(Constant.PROCESS_QUESTION)
    Call<ProcessQuestion> submitSubjectiveQueApi(@Field(Constant.u_user_id) int userid,
                                                 @Field(Constant.q_id) int queId,
                                                 @Field("Case") String caseL,
                                                 @Field(Constant.sub_ans) String ratings);

    @FormUrlEncoded
    @POST(Constant.PROCESS_QUESTION)
    Call<ProcessQuestion> questionAttemptApi(@Field(Constant.u_user_id) int userid,
                                             @Field(Constant.q_id) int queId,
                                             @Field("Case") String caseL,
                                             @Field(Constant.attmpted) int attempted,
                                             @Field(Constant.solved_right) int solved_right);

    @FormUrlEncoded
    @POST(Constant.PROCESS_QUESTION)
    Call<ProcessQuestion> addCommentApi(@Field(Constant.u_user_id) String userid,
                                        @Field(Constant.q_id) int queId,
                                        @Field(Constant.CASE) String caseL,
                                        @Field(Constant.qlc_comment_text) String comment);


    @FormUrlEncoded
    @POST(Constant.FETCH_CONNECTIONS)
    Call<ConnectionResponse> fetchConnections(@Field(Constant.u_user_id) String userid,
                                              @Field("Case") String connectionCase,
                                              @Field(Constant.device_id) String device_id,
                                              @Field("200Q") String app,
                                              @Field(Constant.pagestart) String pagestart);

    @FormUrlEncoded
    @POST(Constant.FETCH_CONNECTIONS)
    Call<ConnectionResponse> searchConnections(@Field(Constant.u_user_id) String userid,
                                               @Field("Case") String connectionCase,
                                               @Field(Constant.device_id) String device_id,
                                               @Field("200Q") String app,
                                               @Field(Constant.SearchText) String searchText,
                                               @Field(Constant.pagestart) String pagestart);

    @FormUrlEncoded
    @POST(Constant.FETCH_CONNECTIONS)
    Call<FriendsResponse> searchFriends(@Field(Constant.u_user_id) String userid,
                                        @Field("Case") String connectionCase,
                                        @Field(Constant.device_id) String device_id,
                                        @Field("200Q") String app,
                                        @Field(Constant.SearchText) String searchText,
                                        @Field(Constant.pagestart) String pagestart);

    @FormUrlEncoded
    @POST(Constant.FETCH_CONNECTIONS)
    Call<FollowersResponse> searchFollowers(@Field(Constant.u_user_id) String userid,
                                            @Field("Case") String connectionCase,
                                            @Field(Constant.device_id) String device_id,
                                            @Field("200Q") String app,
                                            @Field(Constant.SearchText) String searchText,
                                            @Field(Constant.pagestart) String pagestart);

    @FormUrlEncoded
    @POST(Constant.FETCH_CONNECTIONS)
    Call<FollowingResponse> searchFollowing(@Field(Constant.u_user_id) String userid,
                                            @Field("Case") String connectionCase,
                                            @Field(Constant.device_id) String device_id,
                                            @Field("200Q") String app,
                                            @Field(Constant.SearchText) String searchText,
                                            @Field(Constant.pagestart) String pagestart);

    @FormUrlEncoded
    @POST(Constant.FETCH_CONNECTIONS)
    Call<FriendsResponse> fetchFriends(@Field(Constant.u_user_id) String userid,
                                       @Field("Case") String connectionCase,
                                       @Field(Constant.device_id) String device_id,
                                       @Field("200Q") String app,
                                       @Field(Constant.pagestart) String pagestart);

    @FormUrlEncoded
    @POST(Constant.FETCH_CONNECTIONS)
    Call<BlockedConnResp> fetchBlockedConnections(@Field(Constant.u_user_id) String userid,
                                                  @Field("Case") String connectionCase,
                                                  @Field(Constant.device_id) String device_id,
                                                  @Field("200Q") String app,
                                                  @Field(Constant.pagestart) int pagestart);

    @FormUrlEncoded
    @POST(Constant.FETCH_CONNECTIONS)
    Call<BlockedConnResp> fetchRefreshedBlockedConn(@Field(Constant.u_user_id) String userid,
                                                    @Field("Case") String connectionCase,
                                                    @Field(Constant.device_id) String device_id,
                                                    @Field("200Q") String app,
                                                    @Field(Constant.pagestart) String pagestart,
                                                    @Field("ForceRefresh") String refresh);


    @FormUrlEncoded
    @POST(Constant.FETCH_CONNECTIONS)
    Call<FriendsResponse> fetchRefreshedFriendss(@Field(Constant.u_user_id) String userid,
                                                 @Field("Case") String connectionCase,
                                                 @Field(Constant.device_id) String device_id,
                                                 @Field("200Q") String app,
                                                 @Field(Constant.pagestart) String pagestart,
                                                 @Field("ForceRefresh") String refresh);

    @FormUrlEncoded
    @POST(Constant.FETCH_CONNECTIONS)
    Call<FollowersResponse> fetchFollowers(@Field(Constant.u_user_id) String userid,
                                           @Field("Case") String connectionCase,
                                           @Field(Constant.device_id) String device_id,
                                           @Field("200Q") String app,
                                           @Field(Constant.pagestart) String pagestart);

    @FormUrlEncoded
    @POST(Constant.FETCH_CONNECTIONS)
    Call<FollowersResponse> fetchRefreshedFollowers(@Field(Constant.u_user_id) String userid,
                                                    @Field("Case") String connectionCase,
                                                    @Field(Constant.device_id) String device_id,
                                                    @Field("200Q") String app,
                                                    @Field(Constant.pagestart) String pagestart,
                                                    @Field("ForceRefresh") String refresh);

    @FormUrlEncoded
    @POST(Constant.FETCH_CONNECTIONS)
    Call<FollowingResponse> fetchFollowings(@Field(Constant.u_user_id) String userid,
                                            @Field("Case") String connectionCase,
                                            @Field(Constant.device_id) String device_id,
                                            @Field("200Q") String app,
                                            @Field(Constant.pagestart) String pagestart);

    @FormUrlEncoded
    @POST(Constant.FETCH_CONNECTIONS)
    Call<FollowingResponse> fetchRefreshedFollowings(@Field(Constant.u_user_id) String userid,
                                                     @Field("Case") String connectionCase,
                                                     @Field(Constant.device_id) String device_id,
                                                     @Field("200Q") String app,
                                                     @Field(Constant.pagestart) String pagestart,
                                                     @Field("ForceRefresh") String refresh);


    @FormUrlEncoded
    @POST(Constant.FETCH_CONNECTIONS)
    Call<FriendRequestResponse> fetchFriendRequests(@Field(Constant.u_user_id) String userid,
                                                    @Field("Case") String connectionCase,
                                                    @Field(Constant.device_id) String device_id,
                                                    @Field("200Q") String app,
                                                    @Field(Constant.pagestart) String pagestart);

    @FormUrlEncoded
    @POST(Constant.FETCH_CONNECTIONS)
    Call<FriendRequestResponse> fetchRefreshedFriendReq(@Field(Constant.u_user_id) String userid,
                                                        @Field("Case") String connectionCase,
                                                        @Field(Constant.device_id) String device_id,
                                                        @Field("200Q") String app,
                                                        @Field(Constant.pagestart) String pagestart,
                                                        @Field("ForceRefresh") String refresh);


    @FormUrlEncoded
    @POST(Constant.FETCH_CONNECTIONS)
    Call<FollowRequestResponse> fetchFollowRequests(@Field(Constant.u_user_id) String userid,
                                                    @Field("Case") String connectionCase,
                                                    @Field(Constant.device_id) String device_id,
                                                    @Field("200Q") String app,
                                                    @Field(Constant.pagestart) String pagestart);

    @FormUrlEncoded
    @POST(Constant.FETCH_CONNECTIONS)
    Call<FollowRequestResponse> fetchRefreshedFollowReq(@Field(Constant.u_user_id) String userid,
                                                        @Field("Case") String connectionCase,
                                                        @Field(Constant.device_id) String device_id,
                                                        @Field("200Q") String app,
                                                        @Field(Constant.pagestart) String pagestart,
                                                        @Field("ForceRefresh") String refresh);

    @FormUrlEncoded
    @POST(Constant.FETCH_CONNECTIONS)
    Call<ShareResponse> fetchConnectionsforShare(@Field(Constant.u_user_id) String userid,
                                                 @Field("Case") String connectionCase,
                                                 @Field(Constant.device_id) String device_id,
                                                 @Field("200Q") String app,
                                                 @Field(Constant.pagestart) int pagestart,
                                                 @Field(Constant.SearchText) String text);


    @FormUrlEncoded
    @POST(Constant.SHARE_QUESTION_TEST)
    Call<ResponseObj> shareAPI(@Field(Constant.q_T_list) String q_t_list,
                               @Field(Constant.TorQ) String t_or_q,
                               @Field("Case") String connectionCase,
                               @Field(Constant.device_id) String device_id,
                               @Field("SentBy") String user,
                               @Field(Constant.GroupMembersList) String list,
                               @Field(Constant.u_app_version) String version,
                               @Field(Constant.appName) String app,
                               @Field(Constant.mst_text) String comment);

    @FormUrlEncoded
    @POST(Constant.FETCH_CONNECTIONS)
    Call<ConnectionResponse> fetchRefreshedConnections(@Field(Constant.u_user_id) String userid,
                                                       @Field("Case") String connectionCase,
                                                       @Field(Constant.device_id) String device_id,
                                                       @Field("200Q") String app,
                                                       @Field(Constant.pagestart) String pagestart,
                                                       @Field("ForceRefresh") String refresh);


    @FormUrlEncoded
    @POST(Constant.UPDATE_CONNECTIONS)
    Call<ResponseObj> updateConnections(@Field(Constant.u_user_id) String userid,
                                        @Field(Constant.CASE) String connectionCase,
                                        @Field(Constant.device_id) String device_id,
                                        @Field(Constant.appName) String app,
                                        @Field(Constant.other_user) String other_user);

    @FormUrlEncoded
    @POST(Constant.PROCESS_TEST)
    Call<ProcessQuestion> addTestCommentApi(@Field(Constant.u_user_id) int userid,
                                            @Field(Constant.tm_id) int tmId,
                                            @Field(Constant.CASE) String caseL,
                                            @Field(Constant.tlc_comment_text) String comment);

    @FormUrlEncoded
    @POST(Constant.FETCH_VERIFIED_CONTACTLIST)
    Call<ContactResponse> fetchVerifiedContacts(@Field(Constant.u_user_id) String userid, @Field(Constant.device_id) String device_id, @Field("200Q") String app, @Field(Constant.pagestart) String pagestart, @Field(Constant.GroupMembersList) String list, @Field("Case") String Case, @Field(Constant.initial_letter) String letter, @Field(Constant.returnrows) String returnrows);

    @FormUrlEncoded
    @POST(Constant.INVITE_CONTACTLIST)
    Call<SendInviteResponse> inviteContacts(@Field(Constant.u_user_id) String userid, @Field(Constant.device_id) String device_id, @Field("200Q") String app, @Field(Constant.GroupMembersList) String list);


}
