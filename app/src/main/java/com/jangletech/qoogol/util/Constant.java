package com.jangletech.qoogol.util;


/**
 * Created by Pritali on 1/28/2020.
 */
public class Constant {

    public static final String EMAIL_STRING = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    public static final String mdt_id = "273";
    public static final String mdt_desc = "276";

    //FAQ
    public static final String faqt_id = "600";
    public static final String faqt_name = "601";
    public static final String faqt_app = "602";
    public static final String faqt_status = "603";
    public static final String faqt_seq = "604";

    public static final String faq_id = "605";
    public static final String faq_question = "606";
    public static final String faq_answer = "607";
    public static final String faq_app = "608";
    public static final String faq_type = "609";
    public static final String faq_category = "610";
    public static final String faq_status = "611";
    public static final String faq_level = "612";
    public static final String faq_references = "613";
    public static final String faq_faqt_id = "614";
    public static final String faq_p_faq_id = "615";
    public static final String faq_seq = "616";
    public static final String faq_related_faqs = "617";
    public static final String w_media_name = "534";
    public static final String md_from_type = "425A";


    //Selected Board
    public static final String BOARD = "BOARD";
    public static final String MOBILE = "MOBILE";
    public static final String USER_ID = "USER_ID";
    public static final String TM_ID = "TM_ID";

    public static final String DISPLAY_NAME = "DISPLAY_NAME";
    public static final String PROFILE_PIC = "PROFILE_PIC";
    public static final String GENDER = "GENDER";

    public static final String PRODUCTION_BASE_FILE_API = "https://jtmobileappstorage.blob.core.windows.net/spotmeets/media/images/";
    public static final String PRODUCTION_MALE_PROFILE_API = "https://jtmobileappstorage.blob.core.windows.net/spotmeets/media/images/male.png";
    public static final String PRODUCTION_FEMALE_PROFILE_API = "https://jtmobileappstorage.blob.core.windows.net/spotmeets/media/images/female.png";

    //Question Filter Type
    public static final String FILTER_APPLIED = "FILTER_APPLIED";
    public static final String SORT_APPLIED = "SORT_APPLIED";
    public static final String TEST_NAME = "TEST_NAME";

    //Sort Type
    public static final String SORT_LIST = "SORT_LIST";
    public static final String SORT_GRID = "SORT_GRID";

    /*//Different Question Types
    public static final String SCQ = "SCQ";
    public static final String MCQ = "MCQ";
    public static final String FILL_THE_BLANKS = "FILL_THE_BLANKS";
    public static final String TRUE_FALSE = "TRUE_FALSE";


*/


    //User table
    public static final String qoogol = "Q";
    public static final String forcerefresh = "F";
    public static final String u_first_name = "104";
    public static final String u_last_name = "105";
    public static final String w_user_profile_image_name = "507";
    public static final String CASE = "Case";

    public static final String STATUS = "status";
    public static final String VERIFY = "Verify";
    public static final String u_imei_num = "126";
    public static final String GroupMembersList = "70"; /// List of master data id, desc
    public static final String u_fcm_token  = "178Q"; /// List of master data id, desc


    // Firebase notification Constant
    public static final String FB_FROM_TYPE = "fromtype";
    public static final String FB_U_G_ID = "userID";
    public static final String FB_MSG_BODY = "body";
    public static final String FB_TITLE = "title";
    public static final String FB_MS_ID = "msID";
    public static final String FB_ACTION = "action";



    //Work fields
    public static final String w_contact_name = "541";
    public static final String w_contact_number = "540";
    public static final String w_otp_sent = "542";
    public static final String initial_letter = "541";
    public static final String returnrows = "ReturnRows";
    public static final String q_T_list = "69";
    public static final String TorQ = "TorQ";

    //Preferences
    public static final String PREF_NAME = "qoogol";
    public static final String IS_LOGGED_IN = "LOGGED_IN";

    //Sign In
    public static final String SIGN_IN_FIELD = "signInField";
    public static final String PASSWORD = "password";
    public static final String OBJECT = "object";

    //user
    public static final String user_id = "userId";

    //course
    public static final String courseId = "courseId";

    //country
    public static final String country_id = "countryId";
    public static final String country_name = "name";

    //state
    public static final String state_id = "stateId";
    public static final String state_name = "name";
    public static final String state_abbr = "abbr";

    //university
    public static final String board_id = "boardId";

    //degree
    public static final String degree_id = "degreeId";
    public static final String degree_name = "degreeName";
    public static final String duration = "duration";

    //signup
    public static final String first_name = "firstName";
    public static final String last_name = "lastName";
    public static final String email = "email";
    public static final String mobile_no = "mobileNo";
    public static final String password = "password";
    public static final String country = "country";
    public static final String state = "state";
    public static final String board = "board";
    public static final String institute = "institute";
    public static final String degree = "degree";
    public static final String course = "course";
    public static final String cyNum = "cyNum";
    public static final String mobile_number = "mobileNo";
    public static final String is_mobile_verified = "mobile1Verified";
    public static final String is_email_verified = "emailVerified";

    //edit profile
    public static final int add_edu = 0;
    public static final String user_eduid = "userEduId";

    //learning adapter
    public static int learning = 0;
    public static int test = 1;
    public static int notification = 2;

    //Question media
    public static String TEXt = "1";
    public static String IMAGE = "2";
    public static String VIDEO = "3";
    public static String AUDIO = "4";


    //Question type
    public static String Fill_THE_BLANKS_TEST = "3";
    public static String ONE_LINE_ANSWER = "4";
    public static String SHORT_ANSWER = "5";
    public static String LONG_ANSWER = "6";

    //Options type
    public static String SCQ = "1";
    public static String SCQ_IMAGE = "2";
    public static String SCQ_IMAGE_WITH_TEXT = "3";
    public static String MCQ = "4";
    public static String MCQ_IMAGE = "5";
    public static String MCQ_IMAGE_WITH_TEXT = "6";
    public static String FILL_THE_BLANKS = "7";
    public static String TRUE_FALSE = "8";
    public static String SINGLE_LINE_ANSWER = "9";
    public static String MULTI_LINE_ANSWER = "10";
    public static String MATCH_PAIR = "11";
    public static String MATCH_PAIR_IMAGE = "12";

    //Register & Login Fields
    public static final String w_user_name = "539";
    public static final String u_mob_1 = "108";
    public static final String u_calling_code = "177";
    public static final String u_Password = "199M";
    public static final String appName = "200Q";
    public static final String u_app_version = "165";
    public static final String u_user_type = "106";
    public static final String u_app_type = "199";
    public static final String u_t_and_c = "114";
    public static final String u_referred_by = "143";

    public static final String APP_NAME ="Q";
    public static final String APP_VERSION = "1.28";

    // QLikesComments
    public static final String tlc_id = "1401";
    public static final String tlc_tm_id = "1402";
    public static final String tlc_like_flag = "1403";
    public static final String tlc_comment_flag = "1404";
    public static final String tlc_comment_text = "1405";
    public static final String tlc_user_id = "1406";
    public static final String tlc_share_flag = "1407";
    public static final String tlc_cdatetime = "1408";
    public static final String tlc_deleted = "1409";
    public static final String tlc_fav_flag = "1410";

    //learning
    public static final String QUESTION_IMAGES_API = "https://jtmobileappstorage.blob.core.windows.net/spotmeets/media/qoogol/questions/";
    public static final String FETCH_QA = "q151FetchQA";
    public static final String PROCESS_QUESTION = "q141ProcessQuestion";
    public static final String PROCESS_TEST = "q133ProcessTest";
    public static final String START_RESUME_TEST = "q131StartResumeTest";
    public static final String TEST_DETAILS = "q132FetchTestDetails";
    public static final String FETCH_TEST_LIST = "q152fetchtest";
    public static final String FETCH_USER_INFO = "sm21FetchUserInfo";
    public static final String FETCH_USER_EDU = "q112ProcessUserEdu";
    public static final String FETCH_USER_SETTINGS = "q113ProcessUserSetting";

    public static final String FETCH_FAQ = "sm18FAQ";
    public static final String UPDATE_USER_PROFILE = "sm23UpdateUserDetails";
    public static final String FETCH_SUBJECTS = "q111FetchSubjectMaster";
    public static final String FETCH_CHAPTERS = "q114FetchChapterMaster";
    public static final String FETCH_NOTIFICATIONS = "sm28FetchNotifications";
    public static final String COUNTRY = "sm05CountryData";
    public static final String STATE = "sm06StateData";
    public static final String CITY = "sm09CityData";
    public static final String UNIVERSITY = "sm12UBMData";
    public static final String REGISTER_LOGIN = "sm01Signup";
    public static final String UPDATE_PROFILE_IMAGE_API = "sm22UpdateProfilePic";
    public static final String COURSE = "sm15CourseData";
    public static final String INSTITUTE = "sm13InstOrgData";
    public static final String DEGREE = "sm14DegreeData";
    public static final String FETCH_CONNECTIONS = "sm25FetchConnections";
    public static final String UPDATE_CONNECTIONS = "sm24UpdateConnections";
    public static final String COUNTRY_API = "sm05CountryData";
    public static final String STATE_API = "sm06StateData";  /*Pass country id as params to fetch state s_c_id = ["94"]*/
    public static final String DISTRICT_API = "sm07DistrictData"; /*Pass state id as params to fetch district dt_s_id = ["12"]*/
    public static final String CITY_API = "sm09CityData";
    public static final String CLASS_MASTER = "q115FetchClassMaster";

    public static final String ADD_UNIVERSITY = "sm37AddUBM";
    public static final String ADD_INSTITUTE = "sm38AddIOM";
    public static final String LANGUAGE_API = "sm11LanguageData";
    public static final String FETCH_VERIFIED_CONTACTLIST = "sm99ImportContacts";
    public static final String SHARE_QUESTION_TEST = "sm34UpdateMessages ";
    public static final String INVITE_CONTACTLIST = "sm98SendInvite";
    public static final String masterDataList = "61";
    public static final String likesList = "60";
    public static final String commentsList = "62";

    //Connections
    public static final String row_count = "548";
    public static final String connection_list = "68";
    public static final String cn_id = "306";
    public static final String cn_u1_follows_u2 = "316H";
    public static final String cn_u2_follows_u1 = "316I";
    public static final String cn_blocked_by_u1 = "315";
    public static final String cn_blocked_by_u2 = "316";
    public static final String cn_user_id_2 = "308";
    public static final String cn_connected = "309";
    public static final String u_gender = "110";
    public static final String u_relation = "109";
    public static final String lm_id = "279";
    public static final String u_Email = "199L";
    public static final String u_Message_alerts = "199E";
    public static final String u_AV_alerts = "199D";
    public static final String u_notification_alerts = "199C";
    public static final String u_total_points = "98";
    public static final String u_lm_id = "156";
    public static final String u_app_live = "172";
    public static final String u_status_text = "187";
    public static final String u_online_status = "116";
    public static final String u_birth_date = "107";
    public static final String w_datetime = "543";
    public static final String u_latest_lat = "122";
    public static final String u_latest_long = "123";
    public static final String u_conn_count = "199B";
    public static final String u_followers = "200M";
    public static final String u_followings = "201M";
    public static final String ucn_count = "71";
    public static final String w_u_ms_count = "522";
    public static final String w_distance = "549";
    public static final String other_user = "516";
    public static final String RecordType = "RecordType";
    public static final String group_id = "384";
    public static final String un_count = "72"; /// count of connections

    public static final String u_native_ct_id = "159";
    public static final String u_native_s_id = "160";
    public static final String u_native_dt_id = "103";
    public static final String u_nationality = "111";
    public static final String w_lm_id_array = "546A";

    public static final String cn_initiated_by_u1 = "311";
    public static final String cn_initiated_by_u2 = "312";
    public static final String w_hobby_desc = "511";
    public static final String cn_request_active = "313";
    public static final String cn_request_date = "314";
    public static final String contact_from_phone = "551";

    public static final String u_hobby = "133";
    public static final String w_socialcomm_desc = "513";
    public static final String u_socialcomm = "127";
    public static final String w_religion_desc = "512";
    public static final String u_religion = "128";
    public static final String w_nationality_desc = "514";
    public static final String lm_name = "280";
    public static final String lm_lang_cat = "281";
    public static final String lm_app_lang = "282";

    public static final String u_purpose = "135";
    public static final String w_purpose_desc = "510";
    public static final String u_industry = "174";
    public static final String w_industry_desc = "509";
    public static final String u_p_id = "166";
    public static final String w_p_id_desc = "501";
    public static final String u_company_id = "161";
    public static final String w_iom_name_company = "519";
    public static final String u_project_code = "163";
    public static final String dor_ubm_id = "269";
    public static final String dor_iom_id = "270";
    public static final String dor_co_id = "271";
    public static final String u_batch = "129";
    public static final String u_cy = "171";
    public static final String dt_id = "229";
    public static final String dt_name = "231";



    //Fetch connections cases
    public static final String friends = "C";
    public static final String friends_and_groups = "A";
    public static final String followers = "FL";
    public static final String following = "FG";
    public static final String blocked_users = "B";
    public static final String requests = "R";
    public static final String friendrequests = "FRR";
    public static final String followrequests = "FLR";

    public static final String remove_connection = "X";
    public static final String accept_follow_requests = "AF";
    public static final String reject_follow_requests = "RF";
    public static final String accept_friend_requests = "A";
    public static final String reject_friend_requests = "R";
    public static final String block = "B";
    public static final String unblock = "U";
    public static final String follow = "F";
    public static final String unfollow = "UF";

    //Status code
    public static final String DB_TIMEOUT_ERROR = "Database Timeout error. Close app and try again.";
    public static final String DB_NETWORK_ERROR = "Database Network issue. Try again later..";
    public static final String GENERAL_ERROR = "General issue. Try again later.";
    public static final String App_ERROR = "Application issue. Contact support.";
    public static final String MULTILOGIN_ERROR = "You have logged in from another device. Continue?";
    public static final String ERROR = "Something went wrong. Try again later.";

    //comman
    public static final String Response = "Response";
    public static final String prev_q_id = "prev_q_id";
    public static final String question_list = "List1";
    public static final String u_user_id = "101";
    public static final String device_id = "126Q";
    public static final String pagestart = "PageStart";
    public static final String u_tagline = "141";
    public static final String m_age = "335";
    public static final String u_filter_matches = "193";



    //Country
    public static final String c_id = "200";
    public static final String c_name = "201";
    public static final String c_calling_code = "202";
    public static final String c_abbr_2 = "207";

    //university
    public static final String ubm_id = "242";
    public static final String ubm_board_name = "244";

    //InstOrgMaster
    public static final String iom_id = "248";
    public static final String iom_name = "249";

    //DegreeMaster
    public static final String dm_id = "255";
    public static final String dm_degree_name = "256";

    // Q_UserEdu
    public static final String ue_id = "1450";
    public static final String ue_user_id = "1451";
    public static final String ue_startdate = "1452";
    public static final String ue_enddate = "1453";
    public static final String ue_marks = "1454";
    public static final String ue_grade = "1455";
    public static final String ue_dor_id = "1456";
    public static final String ue_cy_num = "1457";

    //Course
    public static final String co_id = "261";
    public static final String co_dm_id = "262";
    public static final String co_name = "263";
    public static final String _70E = "70E";

    // ChapterMaster
    public static final String ex_id = "1435";
    public static final String ex_exam_name = "1436";

    // Questions
    public static final String q_id = "1325";
    public static final String q_cm_id = "1326";
    public static final String q_md_id = "1327";
    public static final String q_sm_id = "1328";
    public static final String q_up_u_id = "1329";
    public static final String q_credit = "1330";
    public static final String q_mcq_op_1 = "1331";
    public static final String q_mcq_op_2 = "1332";
    public static final String q_mcq_op_3 = "1333";
    public static final String q_mcq_op_4 = "1334";
    public static final String q_mcq_op_5 = "1335";
    public static final String q_marks = "1336";
    public static final String q_diff_level = "1337";
    public static final String q_trending = "1338";
    public static final String q_popular = "1339";
    public static final String q_recent = "1340";
    public static final String q_avg_ratings = "1341";
    public static final String q_no_of_ratings = "1342";
    public static final String q_status = "1343";
    public static final String q_cdatetime = "1344";
    public static final String q_udatetime = "1345";
    public static final String q_type = "1346";
    public static final String q_source = "1347";
    public static final String q_quest = "1348";
    public static final String q_view_count = "1349";
    public static final String q_category = "1350";
    public static final String q_duration = "1351";
    public static final String q_likes = "1352";
    public static final String q_comments = "1353";
    public static final String q_views = "1354";
    public static final String q_shares = "1355";
    public static final String q_last_used = "1356";
    public static final String q_topic_id = "1357";
    public static final String q_quest_desc = "1358";
    public static final String q_attempted_by = "1359";
    public static final String q_solved_by = "1360";
    public static final String q_media_type = "1361";
    public static final String q_option_type = "1362";
    public static final String friend_req_sent = "311";
    public static final String friend_req_received = "312";
    public static final String follow_req_sent = "316E";
    public static final String follow_req_received = "316F";
    public static final String solved_right = "1394";
    public static final String attmpted = "1393";
    public static final String q_favs = "1355A";
    public static final String attmpted_count = "1355B";
    public static final String right_solved_count = "1355C";


    //Answers
    public static final String a_id = "1363";
    public static final String a_q_id = "1364";
    public static final String a_up_u_id = "1365";
    public static final String a_sub_ans = "1366";
    public static final String a_mcq_1 = "1367";
    public static final String a_upvotes = "1368";
    public static final String a_downvotes = "1369";
    public static final String a_cdatetime = "1370";
    public static final String a_udatetime = "1371";
    public static final String a_status = "1372";
    public static final String a_mcq_2 = "1373";
    public static final String a_mcq_3 = "1374";
    public static final String a_mcq_4 = "1375";
    public static final String a_mcq_5 = "1376";
    public static final String a_md_id = "1377";
    public static final String a_ans_desc = "1378";

    // QLikesComments
    public static final String qlc_id = "1381";
    public static final String qlc_q_id = "1382";
    public static final String qlc_like_flag = "1383";
    public static final String qlc_comment_flag = "1384";
    public static final String qlc_comment_text = "1385";
    public static final String qlc_user_id = "1386";
    public static final String qlc_share_flag = "1387";
    public static final String qlc_cdatetime = "1388";
    public static final String qlc_deleted = "1389";
    public static final String qlc_fav_flag = "1390";
    public static final String qlc_rating = "1391";
    public static final String qlc_feedback = "1392";

    // SubjectMaster
    public static final String sm_id = "1300";
    public static final String sm_sub_name = "1301";

    // ChapterMaster
    public static final String cm_id = "1315";
    public static final String cm_chapter_name = "1316";

    // Q_SubjectCourseR
    public static final String scr_id = "1425";
    public static final String scr_sm_id = "1426";
    public static final String scr_co_id = "1427";
    public static final String scr_ex_id = "1428";
    public static final String scr_sem_id = "1429";
    public static final String scr_cy_num = "1430";
//    public static final String cm_id = "1315";
//    public static final String cm_chapter_name = "1316";


    //TestQuestion Api Start/Resume Test Constants
    public static final String tm_id = "1200";
    public static final String tm_sm_id = "1201";
    public static final String tm_up_u_id = "1202";
    public static final String tm_name = "1203";     //Test Name
    public static final String tm_diff_level = "1204";   //Test Difficulty Level
    public static final String tm_tot_marks = "1205";    //Test Total Marks
    public static final String tm_type = "1206";
    public static final String tm_ranking = "1207";        //Test Ranking
    public static final String tm_rating_count = "1208";
    public static final String tm_status = "1209";
    public static final String tm_neg_mks = "1210";         //Test Negative Marks
    public static final String tm_catg = "1211";            //Test Category
    public static final String tm_category_1 = "1212";
    public static final String tm_category_2 = "1213";
    public static final String tm_category_3 = "1214";
    public static final String tm_avg_rating = "1215";
    public static final String tm_sharable = "1216";         //isTestSharable
    public static final String tm_comp_quest_count = "1217";  //Test Question Count
    public static final String tm_duration = "1218";        //Test Master Duration
    public static final String tm_cm_id = "1219";
    public static final String tm_visibility = "1220";
    public static final String tq_id = "1225"; // Test Question Id
    public static final String tq_tm_id = "1226";  //Test Question Test Master Id
    public static final String tq_q_id = "1227";
    public static final String tq_marks = "1228";       //Question Marks
    public static final String tq_quest_seq_num = "1229";     //Question Sequence Number
    public static final String tq_compulsory_quest = "1230";   //isCompulsory Test boolean
    public static final String tq_ref_id = "1231";
    public static final String tq_quest_catg = "1232";       //Question Category
    public static final String tq_quest_disp_num = "1233";    //Display Number
    public static final String tq_duration = "1234";          //Duration
    public static final String tq_status = "1235";      //Question Status
    public static final String tt_id = "1240";          //Test Id
    public static final String tt_tm_id = "1241";
    public static final String tt_up_u_id = "1242";
    public static final String tt_user_ranking = "1243";
    public static final String tt_user_test_ratings = "1244";
    public static final String tt_obtain_marks = "1245";
    public static final String tt_comment = "1246";
    public static final String tt_cdatetime = "1247";
    public static final String tt_status = "1248";
    public static final String tt_duration_taken = "1249";    //Test Duration Taken
    public static final String ttqa_id = "1255";
    public static final String ttqa_tq_id = "1256";
    public static final String ttqa_tt_id = "1257";
    public static final String ttqa_obtain_marks = "1258";
    public static final String ttqa_sub_ans = "1259";     //Subjective Answer
    public static final String ttqa_mcq_ans_1 = "1260";   // option 1
    public static final String ttqa_mcq_ans_2 = "1261";   // option 2
    public static final String ttqa_mcq_ans_3 = "1262";   // option 3
    public static final String ttqa_mcq_ans_4 = "1263";    //option 4
    public static final String ttqa_mcq_ans_5 = "1264";     //option 5
    public static final String ttqa_duration_taken = "1265";    //time taken for question
    public static final String ttqa_md_id = "1266";             //media id

    public static final String ttqa_marked = "1267";   //Marked Quest
    public static final String ttqa_saved = "1268";    //saved  Quest
    public static final String ttqa_visited = "1269";  //visited Quest
    public static final String ttqa_attempted = "1270"; //Attempted Quest

    public static final String tm_attempted_by = "1198"; //test attempted by
    public static final String test_description = "1199";

    //City Master Data
    public static final String ct_id = "235";
    public static final String ct_sd_id = "236";
    public static final String ct_dt_id = "237";
    public static final String ct_name = "238";

    //State MAster Data
    public static final String s_id = "225";
    public static final String s_c_id = "226";
    public static final String s_name = "227";
    public static final String s_state_abbr = "228";
    public static final String dt_s_id = "230";

    //latest Added parameter For Test List
    public static final String publishedDate = "1223E";
    public static final String author = "1223F";
    public static final String quest_count = "1223D";
    public static final String likeCount = "1223A";
    public static final String shareCount = "1223B";
    public static final String commentsCount = "1223C";
    public static final String isLike = "1403";
    public static final String isFavourite = "1410";
    public static final String _1223G = "1223G";


    //Notification Fields
    //public static final String cn_connected = "309";
    public static final String n_id = "297";
    public static final String n_sent_by_u_id = "298";
    public static final String n_sent_to_u_id = "299";
    public static final String n_type = "300";
    public static final String n_read_by_user = "301";
    public static final String w_notification_desc = "515";
    public static final String n_ref_id = "302";
    public static final String n_ref_type = "303";
    public static final String n_cdatetime = "305";
    //public static final String w_user_profile_image_name = "507";


    public static final String CALL_FROM = "CALL_FROM";


//    //Start Resume missing Parameter
//    public static final String sm_sub_name = "1301";

    /***
     *  SCQ("1"),
     *     MCQ("2"),
     *     Fill_THE_BLANKS("3"),
     *     ONE_LINE_ANSWER("4"),
     *     SHORT_ANSWER("5"),
     *     LONG_ANSWER("6");
     */

   /* public static final String SCQ = "1";
    public static final String MCQ = "2";
    public static final String Fill_THE_BLANKS = "3";
    public static final String ONE_LINE_ANSWER = "4";
    public static final String SHORT_ANSWER = "5";
    public static final String LONG_ANSWER = "6";*/

    //Media
    public static final String w_ans_text = "1701";
    public static final String w_media_names = "1702";
    public static final String w_media_paths = "1703";
}