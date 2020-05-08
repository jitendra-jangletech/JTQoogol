package com.jangletech.qoogol.util;

/**
 * Created by Pritali on 1/28/2020.
 */
public class Constant {

    public static final String PRODUCTION_BASE_FILE_API = "https://jtmobileappstorage.blob.core.windows.net/spotmeets/media/images/";

    //Question Filter Type
    public static final String FILTER_APPLIED = "FILTER_APPLIED";

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
    public static final String u_first_name = "104";
    public static final String u_last_name = "105";
    public static final String w_user_profile_image_name = "507";


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

    //Question media
    public static String TEXt = "1";
    public static String IMAGE = "2";
    public static String VIDEO = "3";
    public static String AUDIO = "4";


    //Question type
    public static String Fill_THE_BLANKS  = "3";
    public static String ONE_LINE_ANSWER = "4";
    public static String SHORT_ANSWER = "5";
    public static String LONG_ANSWER = "6";

    //Options type
    public static String SCQ = "1";
    public static String SCQ_IMAGE  = "2";
    public static String SCQ_IMAGE_WITH_TEXT  = "3";
    public static String MCQ = "4";
    public static String MCQ_IMAGE = "5";
    public static String MCQ_IMAGE_WITH_TEXT = "6";
    public static String FILL_THE_BLANKS = "7";
    public static String TRUE_FALSE = "8";
    public static String SINGLE_LINE_ANSWER = "9";
    public static String MULTI_LINE_ANSWER = "10";
    public static String MATCH_PAIR = "11";
    public static String MATCH_PAIR_IMAGE = "12";


    //learning
    public static final String QUESTION_IMAGES_API = "https://jtmobileappstorage.blob.core.windows.net/spotmeets/media/qoogol/questions/";
    public static final String FETCH_QA = "q151FetchQA";
    public static final String PROCESS_QUESTION = "q141ProcessQuestion";
    public static final String COUNTRY = "sm05CountryData";
    public static final String STATE = "sm06StateData";
    public static final String CITY = "sm09CityData";
    public static final String UNIVERSITY = "sm12UBMData";
    public static final String COURSE = "sm15CourseData";
    public static final String INSTITUTE = "sm13InstOrgData";
    public static final String DEGREE = "sm14DegreeData";
    public static final String FETCH_CONNECTIONS = "sm25FetchConnections";
    public static final String UPDATE_CONNECTIONS = "sm24UpdateConnections";
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
    public static final String cn_user_id_2 = "308";
    public static final String cn_connected = "309";
    public static final String u_gender = "110";
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


    //Fetch connections cases
    public static final String friends = "C";
    public static final String followers = "FL";
    public static final String following = "FG";
    public static final String blocked_users = "B";
    public static final String requests = "R";
    public static final String friendrequests = "FRR";
    public static final String followrequests = "FLr";

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
    public  static final String dm_id = "255";
    public  static final String dm_degree_name = "256";

    //Course
    public  static final String co_id = "261";
    public  static final String co_dm_id = "262";
    public  static final String co_name = "263";

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

    // SubjectMaster
    public static final String sm_id = "1300";
    public static final String sm_sub_name = "1301";

    // ChapterMaster
    public static final String cm_id = "1315";
    public static final String cm_chapter_name = "1316";


    //TestQuestion Api Start/Resume Test Constants
    public static final String tm_id = "200";
    public static final String tm_sm_id = "201";
    public static final String tm_up_u_id = "202";
    public static final String tm_name = "203";     //Test Name
    public static final String tm_diff_level = "204";   //Test Difficulty Level
    public static final String tm_tot_marks = "205";    //Test Total Marks
    public static final String tm_type = "206";
    public static final String tm_ranking = "207";        //Test Ranking
    public static final String tm_rating_count = "208";
    public static final String tm_status = "209";
    public static final String tm_neg_mks = "210";         //Test Negative Marks
    public static final String tm_catg = "211";            //Test Category
    public static final String tm_category_1 = "212";
    public static final String tm_category_2 = "213";
    public static final String tm_category_3 = "214";
    public static final String tm_avg_rating = "215";
    public static final String tm_sharable = "216";         //isTestSharable
    public static final String tm_comp_quest_count = "217";  //Test Question Count
    public static final String tm_duration = "218";        //Test Master Duration
    public static final String tm_cm_id = "219";
    public static final String tm_visibility = "220";
    public static final String tq_id = "225"; // Test Question Id
    public static final String tq_tm_id = "226";  //Test Question Test Master Id
    public static final String tq_q_id = "227";
    public static final String tq_marks = "228";       //Question Marks
    public static final String tq_quest_seq_num = "229";     //Question Sequence Number
    public static final String tq_compulsory_quest = "230";   //isCompulsory Test boolean
    public static final String tq_ref_id = "231";
    public static final String tq_quest_catg = "232";       //Question Category
    public static final String tq_quest_disp_num = "233";    //Display Number
    public static final String tq_duration = "234";          //Duration
    public static final String tq_status = "235";      //Question Status
    public static final String tt_id = "240";          //Test Id
    public static final String tt_tm_id = "241";
    public static final String tt_up_u_id = "242";
    public static final String tt_user_ranking = "243";
    public static final String tt_user_test_ratings = "244";
    public static final String tt_obtain_marks = "245";
    public static final String tt_comment = "246";
    public static final String tt_cdatetime = "247";
    public static final String tt_status = "248";
    public static final String tt_duration_taken = "249";    //Test Duration Taken
    public static final String ttqa_id = "255";
    public static final String ttqa_tq_id = "256";
    public static final String ttqa_tt_id = "257";
    public static final String ttqa_obtain_marks = "258";
    public static final String ttqa_sub_ans = "259";     //Subjective Answer
    public static final String ttqa_mcq_ans_1 = "260";   // option 1
    public static final String ttqa_mcq_ans_2 = "261";   // option 2
    public static final String ttqa_mcq_ans_3 = "262";   // option 3
    public static final String ttqa_mcq_ans_4 = "263";    //option 4
    public static final String ttqa_mcq_ans_5 = "264";     //option 5
    public static final String ttqa_duration_taken = "265";    //time taken for question
    public static final String ttqa_md_id = "266";             //media id

    public static String ttqa_marked = "267";   //Marked Quest
    public static String ttqa_saved = "268";    //saved  Quest
    public static String ttqa_visited = "269";  //visited Quest
    public static String ttqa_attempted = "270"; //Attempted Quest

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

    //Media
    public static final String w_ans_text = "1701";
    public static final String w_media_names = "1702";
    public static final String w_media_paths = "1703";
}