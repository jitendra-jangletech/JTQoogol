package com.jangletech.qoogol.model;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;
import com.jangletech.qoogol.util.Constant;

import java.util.Locale;

import static com.jangletech.qoogol.util.Constant.cn_initiated_by_u1;
import static com.jangletech.qoogol.util.Constant.cn_initiated_by_u2;
import static com.jangletech.qoogol.util.Constant.cn_request_active;


/*
 *
 *
 *  * /////////////////////////////////////////////////////////////////////////////////////////////////
 *  * //
 *  * //            Copyright (c) 2020. JangleTech Systems Private Limited, Thane, India
 *  * //
 *  * /////////////////////////////////////////////////////////////////////////////////////////////////
 *
 */

@Entity(tableName = "userprofile", indices = @Index(value = {"userId"}, unique = true))
public class UserProfile {

    @SerializedName(Constant.u_native_ct_id)
    private String cityId;

    @SerializedName(Constant.userName)
    private String userName;

    public String getCityId() {
        return cityId!=null?cityId:"";
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    @SerializedName("Response")
    private String responseCode;

    @PrimaryKey
    @ColumnInfo(name = "userId")
    @NonNull
    @SerializedName(Constant.u_user_id)
    private String userId;

    @ColumnInfo
    private String u_photo;

    @SerializedName(Constant.u_tagline)
    @ColumnInfo
    private String strTagLine;

    @SerializedName(Constant.u_status_text)
    @ColumnInfo
    private String statusText;

    @SerializedName(Constant.w_user_profile_image_name)
    private String endPathImage;

    @SerializedName(Constant.m_age)
    private String age;

    @SerializedName(Constant.ucn_count)
    @ColumnInfo
    private String connection_count;

    @SerializedName(Constant.un_count)
    @ColumnInfo
    private String notificationCount;

    @SerializedName(Constant.w_u_ms_count)
    @ColumnInfo
    private String ms_count;

    @SerializedName(Constant.u_first_name)
    @ColumnInfo
    private String firstName;

    @SerializedName(Constant.u_last_name)
    @ColumnInfo
    private String lastName;

    @SerializedName(Constant.u_birth_date)
    @ColumnInfo(name = "date_of_birth")
    private String dob;

    @SerializedName(Constant.u_gender)
    @ColumnInfo
    private String strGender;

    @SerializedName(Constant.u_relation)
    @ColumnInfo
    private String strRelationShip;

    @SerializedName(Constant.lm_id)
    @ColumnInfo
    private String u_languageId;

    @SerializedName(Constant.lm_name)
    @ColumnInfo
    private String u_language;

    @SerializedName(Constant.u_nationality)
    @ColumnInfo
    private String u_NationalityId;

    @SerializedName(Constant.w_nationality_desc)
    @ColumnInfo
    private String u_Nationality;

    @SerializedName(Constant.u_religion)
    @ColumnInfo
    private String u_ReligionId;

    @SerializedName(Constant.w_religion_desc)
    @ColumnInfo
    private String u_Religion;

    @SerializedName(Constant.u_socialcomm)
    @ColumnInfo
    private String u_SocialCommunityId;

    @SerializedName(Constant.w_socialcomm_desc)
    @ColumnInfo
    private String u_SocialCommunity;

    @SerializedName(Constant.u_hobby)
    @ColumnInfo
    private String u_HobbyId;

    @SerializedName(Constant.w_hobby_desc)
    @ColumnInfo
    private String u_Hobby;

    @SerializedName(Constant.u_purpose)
    @ColumnInfo
    private String u_PurposeId;

    @SerializedName(Constant.w_purpose_desc)
    @ColumnInfo
    private String u_Purpose;

    @SerializedName(Constant.u_industry)
    @ColumnInfo
    private String u_IndustryId;

    @SerializedName(Constant.w_industry_desc)
    @ColumnInfo
    private String u_Industry;

    @SerializedName(Constant.u_p_id)
    @ColumnInfo
    private String u_ProfessionId;

    @SerializedName(Constant.w_p_id_desc)
    @ColumnInfo
    private String u_Profession;

    @SerializedName(Constant.u_company_id)
    @ColumnInfo
    private String u_CompanyNameId;

    @SerializedName(Constant.w_iom_name_company)
    @ColumnInfo
    private String u_CompanyName;

    @SerializedName(Constant.u_project_code)
    @ColumnInfo
    private String strProjectCode;

    @SerializedName(Constant.dor_ubm_id)
    @ColumnInfo
    private String u_BoardId;

    public void setU_BoardId(String u_BoardId) {
        this.u_BoardId = u_BoardId;
    }

    @SerializedName(Constant.ubm_board_name)
    @ColumnInfo
    private String u_Board;

    @SerializedName(Constant.dor_iom_id)
    @ColumnInfo
    private String u_CollegeId;

    @SerializedName(Constant.iom_name)
    @ColumnInfo
    private String u_College;

    @SerializedName(Constant.dm_id)
    @ColumnInfo
    private String u_DegreeId;

    @SerializedName(Constant.dm_degree_name)
    @ColumnInfo
    private String u_Degree;

    @SerializedName(Constant.dor_co_id)
    @ColumnInfo
    private String u_CourseId;

    @SerializedName(Constant.co_name)
    @ColumnInfo
    private String u_Course;

    @SerializedName(Constant.u_batch)
    @ColumnInfo
    private String u_Batches;

    @SerializedName(Constant.u_cy)
    @ColumnInfo
    private String strCourseYear;

    //@SerializedName(Constant.)
    @ColumnInfo
    private String strCountry;

    @SerializedName(Constant.u_app_live)
    @ColumnInfo
    private boolean isAppLive;

    @SerializedName(Constant.u_online_status)
    @ColumnInfo(name = "onlineStatus")
    private String onlineStatus;

    @SerializedName(Constant.s_id)
    @ColumnInfo
    private String u_StateId;

    @SerializedName(Constant.s_name)
    @ColumnInfo
    private String u_State;

    @SerializedName(Constant.dt_id)
    @ColumnInfo
    private String u_DistrictId;

    public void setU_StateId(String u_StateId) {
        this.u_StateId = u_StateId;
    }

    public void setU_DistrictId(String u_DistrictId) {
        this.u_DistrictId = u_DistrictId;
    }

    @SerializedName(Constant.dt_name)
    @ColumnInfo
    private String u_District;

    @SerializedName(Constant.ct_id)
    @ColumnInfo
    private String u_CityId;

    @SerializedName(Constant.ct_name)
    @ColumnInfo
    private String u_City;

    @SerializedName(Constant.contact_from_phone)
    private boolean contactFromPhone;

    @SerializedName(Constant.cn_connected)
    private boolean isConnected;

    @SerializedName(cn_request_active)
    private boolean isRequestActive;

    @SerializedName(cn_initiated_by_u1)
    private boolean isInitiated_by_u1;

    @SerializedName(cn_initiated_by_u2)
    private boolean isInitiated_by_u2;

    @ColumnInfo(name = "total_points")
    @SerializedName(Constant.u_total_points)
    private String totalPointsEarned;

    @ColumnInfo(name = "appVersion")
    @SerializedName(Constant.u_app_version)
    private String appVersion;

    @ColumnInfo(name = "u_notification_alerts")
    @SerializedName(Constant.u_notification_alerts)
    private boolean notificationEnabled;

    @ColumnInfo(name = "u_AV_alerts")
    @SerializedName(Constant.u_AV_alerts)
    private boolean isAVCallEnabled;

    @ColumnInfo(name = "u_message_alerts")
    @SerializedName(Constant.u_Message_alerts)
    private boolean messageAlertsEnabled;

    @SerializedName(Constant.u_mob_1)
    @ColumnInfo(name = "mobileNumber")
    private String mobileNumber;

    @SerializedName(Constant.u_Password)
    @ColumnInfo(name = "password")
    private String password;

    @SerializedName(Constant.u_Email)
    @ColumnInfo(name = "email")
    private String emailAddress;

    private int illegalAccessCode;

    @NonNull
    public String getUserId() {
        return userId;
    }

    public void setUserId(@NonNull String userId) {
        this.userId = userId;
    }

    public String getU_StateId() {
        return u_StateId;
    }


    public String getU_DistrictId() {
        return u_DistrictId!=null?u_DistrictId:"";
    }


    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getU_BoardId() {
        return u_BoardId;
    }

    public String getU_DegreeId() {
        return u_DegreeId;
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getConnection_count() {
        return connection_count == null ? "0" : connection_count;
    }


    public String getStrGender() {
        return strGender == null ? "" : strGender;
    }

    public void setStrGender(String strGender) {
        this.strGender = strGender;
    }


    public String getStrRelationShip() {
        return strRelationShip == null ? "" : strRelationShip;
    }

    public void setStrRelationShip(String strRelationShip) {
        this.strRelationShip = strRelationShip;
    }



    public void setU_PurposeId(String u_PurposeId) {
        this.u_PurposeId = u_PurposeId;
    }

    public String getU_CollegeId() {
        return u_CollegeId;
    }

    public String getU_Nationality() {
        return u_Nationality == null ? "" : u_Nationality;
    }

    public void setU_CityId(String u_CityId) {
        this.u_CityId = u_CityId;
    }

    public String getDob() {
        return dob == null ? "" : dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public void setU_HobbyId(String u_HobbyId) {
        this.u_HobbyId = u_HobbyId;
    }


    public String getStrCourseYear() {
        return strCourseYear == null ? "" : strCourseYear;
    }


    public String getStrTagLine() {
        return strTagLine == null ? "" : strTagLine;
    }



    public String getStrProjectCode() {
        return strProjectCode == null ? "" : strProjectCode;
    }





    public String getU_District() {
        return u_District == null ? "" : u_District;
    }



    public String getU_language() {
        return u_language == null ? "" : u_language;
    }



    public String getU_NationalityId() {
        return u_NationalityId;
    }


    public String getU_Religion() {
        return u_Religion == null ? "" : u_Religion;
    }




    public String getU_SocialCommunity() {
        return u_SocialCommunity == null ? "" : u_SocialCommunity;
    }


    public String getU_Hobby() {
        return u_Hobby == null ? "" : u_Hobby;
    }

    public void setU_Hobby(String u_Hobby) {
        this.u_Hobby = u_Hobby;
    }


    public String getU_Purpose() {
        return u_Purpose == null ? "" : u_Purpose;
    }

    public void setU_Purpose(String u_Purpose) {
        this.u_Purpose = u_Purpose;
    }


    public String getU_Industry() {
        return u_Industry == null ? "" : u_Industry;
    }


    public String getU_Profession() {
        return u_Profession == null ? "" : u_Profession;
    }


    public String getU_Board() {
        return u_Board == null ? "" : u_Board;
    }



    public String getU_College() {
        return u_College == null ? "" : u_College;
    }



    public String getU_Course() {
        return u_Course == null ? "" : u_Course;
    }



    public String getU_State() {
        return u_State == null ? "" : u_State;
    }



    public String getU_City() {
        return u_City == null ? "" : u_City;
    }



    public String getU_Batches() {
        return u_Batches == null ? "" : u_Batches;
    }




    public String getU_Degree() {
        return u_Degree == null ? "" : u_Degree;
    }


    public String getU_CompanyName() {
        return u_CompanyName == null ? "" : u_CompanyName;
    }

    public String getAge() {
        return age == null ? "" : age;
    }

    public String getNotificationCount() {
        return notificationCount;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }

    public String getU_photo() {
        return u_photo;
    }

    public void setU_photo(String u_photo) {
        this.u_photo = u_photo;
    }

    public String getEndPathImage() {
        return endPathImage;
    }

    public void setEndPathImage(String endPathImage) {
        this.endPathImage = endPathImage;
    }

    public boolean isConnected() {
        return isConnected;
    }


    public boolean isRequestActive() {
        return isRequestActive;
    }


    public boolean isInitiated_by_u1() {
        return isInitiated_by_u1;
    }


    public boolean isInitiated_by_u2() {
        return isInitiated_by_u2;
    }


    public String getStatusText() {
        return statusText != null && !statusText.trim().isEmpty() ? statusText : "";
    }


    public int getIllegalAccessCode() {
        return illegalAccessCode;
    }

    public void setIllegalAccessCode(int illegalAccessCode) {
        this.illegalAccessCode = illegalAccessCode;
    }

    public String getTotalPointsEarned() {
        return totalPointsEarned == null ? "" : totalPointsEarned;
    }


    public String getAppVersion() {
        return appVersion == null ? "" : appVersion.trim();
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public boolean isNotificationEnabled() {
        return notificationEnabled;
    }

    public void setNotificationEnabled(boolean notificationEnabled) {
        this.notificationEnabled = notificationEnabled;
    }

    public boolean isAVCallEnabled() {
        return isAVCallEnabled;
    }

    public void setAVCallEnabled(boolean AVCallEnabled) {
        isAVCallEnabled = AVCallEnabled;
    }

    public boolean isContactFromPhone() {
        return contactFromPhone;
    }

    public void setContactFromPhone(boolean contactFromPhone) {
        this.contactFromPhone = contactFromPhone;
    }

    public void setMessageAlertsEnabled(boolean messageAlertsEnabled) {
        this.messageAlertsEnabled = messageAlertsEnabled;
    }

    public boolean isMessageAlertsEnabled() {
        return messageAlertsEnabled;
    }

    public String getMobileNumber() {
        return mobileNumber == null ? "" : mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getPassword() {
        return password == null ? "" : password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmailAddress() {
        return emailAddress == null ? "" : emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public void setStrTagLine(String strTagLine) {
        this.strTagLine = strTagLine;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setConnection_count(String connection_count) {
        this.connection_count = connection_count;
    }

    public void setNotificationCount(String notificationCount) {
        this.notificationCount = notificationCount;
    }

    public String getMs_count() {
        return ms_count;
    }

    public void setMs_count(String ms_count) {
        this.ms_count = ms_count;
    }

    public String getU_languageId() {
        return u_languageId;
    }

    public void setU_languageId(String u_languageId) {
        this.u_languageId = u_languageId;
    }

    public void setU_language(String u_language) {
        this.u_language = u_language;
    }

    public void setU_NationalityId(String u_NationalityId) {
        this.u_NationalityId = u_NationalityId;
    }

    public void setU_Nationality(String u_Nationality) {
        this.u_Nationality = u_Nationality;
    }

    public String getU_ReligionId() {
        return u_ReligionId;
    }

    public void setU_ReligionId(String u_ReligionId) {
        this.u_ReligionId = u_ReligionId;
    }

    public void setU_Religion(String u_Religion) {
        this.u_Religion = u_Religion;
    }

    public String getU_SocialCommunityId() {
        return u_SocialCommunityId;
    }

    public void setU_SocialCommunityId(String u_SocialCommunityId) {
        this.u_SocialCommunityId = u_SocialCommunityId;
    }

    public void setU_SocialCommunity(String u_SocialCommunity) {
        this.u_SocialCommunity = u_SocialCommunity;
    }

    public String getU_HobbyId() {
        return u_HobbyId;
    }

    public String getU_PurposeId() {
        return u_PurposeId;
    }

    public String getU_IndustryId() {
        return u_IndustryId;
    }

    public void setU_IndustryId(String u_IndustryId) {
        this.u_IndustryId = u_IndustryId;
    }

    public void setU_Industry(String u_Industry) {
        this.u_Industry = u_Industry;
    }

    public String getU_ProfessionId() {
        return u_ProfessionId;
    }

    public void setU_ProfessionId(String u_ProfessionId) {
        this.u_ProfessionId = u_ProfessionId;
    }

    public void setU_Profession(String u_Profession) {
        this.u_Profession = u_Profession;
    }

    public String getU_CompanyNameId() {
        return u_CompanyNameId;
    }

    public void setU_CompanyNameId(String u_CompanyNameId) {
        this.u_CompanyNameId = u_CompanyNameId;
    }

    public void setU_CompanyName(String u_CompanyName) {
        this.u_CompanyName = u_CompanyName;
    }

    public void setStrProjectCode(String strProjectCode) {
        this.strProjectCode = strProjectCode;
    }

    public void setU_Board(String u_Board) {
        this.u_Board = u_Board;
    }

    public void setU_CollegeId(String u_CollegeId) {
        this.u_CollegeId = u_CollegeId;
    }

    public void setU_College(String u_College) {
        this.u_College = u_College;
    }

    public void setU_DegreeId(String u_DegreeId) {
        this.u_DegreeId = u_DegreeId;
    }

    public void setU_Degree(String u_Degree) {
        this.u_Degree = u_Degree;
    }

    public String getU_CourseId() {
        return u_CourseId;
    }

    public void setU_CourseId(String u_CourseId) {
        this.u_CourseId = u_CourseId;
    }

    public void setU_Course(String u_Course) {
        this.u_Course = u_Course;
    }

    public void setU_Batches(String u_Batches) {
        this.u_Batches = u_Batches;
    }

    public void setStrCourseYear(String strCourseYear) {
        this.strCourseYear = strCourseYear;
    }

    public String getStrCountry() {
        return strCountry;
    }

    public void setStrCountry(String strCountry) {
        this.strCountry = strCountry;
    }

    public boolean isAppLive() {
        return isAppLive;
    }

    public void setAppLive(boolean appLive) {
        isAppLive = appLive;
    }

    public void setU_State(String u_State) {
        this.u_State = u_State;
    }

    public void setU_District(String u_District) {
        this.u_District = u_District;
    }

    public String getU_CityId() {
        return u_CityId;
    }

    public void setU_City(String u_City) {
        this.u_City = u_City;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public void setRequestActive(boolean requestActive) {
        isRequestActive = requestActive;
    }

    public void setInitiated_by_u1(boolean initiated_by_u1) {
        isInitiated_by_u1 = initiated_by_u1;
    }

    public void setInitiated_by_u2(boolean initiated_by_u2) {
        isInitiated_by_u2 = initiated_by_u2;
    }

    public void setTotalPointsEarned(String totalPointsEarned) {
        this.totalPointsEarned = totalPointsEarned;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
