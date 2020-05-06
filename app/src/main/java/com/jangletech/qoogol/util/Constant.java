package com.jangletech.qoogol.util;

import com.jangletech.qoogol.ui.syllabus.stream.StreamFragment;

/**
 * Created by Pritali on 1/28/2020.
 */
public class Constant {

    //Selected Board
    public static final String BOARD = "BOARD";

    //Question Filter Type
    public static final String FILTER_APPLIED = "FILTER_APPLIED";
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


    //learning
    public static final String FETCH_QA = "q051FetchQA";
    public static final String COUNTRY = "sm05CountryData";
    public static final String STATE = "sm06StateData";
    public static final String CITY = "sm09CityData";
    public static final String UNIVERSITY = "sm12UBMData";
    public static final String COURSE = "sm15CourseData";
    public static final String INSTITUTE = "sm13InstOrgData";
    public static final String DEGREE = "sm14DegreeData";
    public static final String masterDataList = "61";

    //comman
    public static final String Response = "Response";
    public static final String prev_q_id = "prev_q_id";
    public static final String question_list = "List1";
    public static final String u_user_id = "101";


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

    // ChapterMaster
    public static final String ex_id = "1435";
    public static final String ex_exam_name = "1436";

    // Questions
    public static final String q_id = "1325";
    public static final String q_cm_id = "326";
    public static final String q_md_id = "327";
    public static final String q_sm_id = "328";
    public static final String q_up_u_id = "329";
    public static final String q_credit = "330";
    public static final String q_mcq_op_1 = "1331";
    public static final String q_mcq_op_2 = "1332";
    public static final String q_mcq_op_3 = "1333";
    public static final String q_mcq_op_4 = "1334";
    public static final String q_mcq_op_5 = "1335";
    public static final String q_marks = "1336";
    public static final String q_diff_level = "1337";
    public static final String q_trending = "338";
    public static final String q_popular = "339";
    public static final String q_recent = "340";
    public static final String q_avg_ratings = "341";
    public static final String q_no_of_ratings = "342";
    public static final String q_status = "343";
    public static final String q_cdatetime = "344";
    public static final String q_udatetime = "345";
    public static final String q_type = "1346";
    public static final String q_source = "347";
    public static final String q_quest = "1348";
    public static final String q_view_count = "349";
    public static final String q_category = "1350";
    public static final String q_duration = "351";
    public static final String q_likes = "352";
    public static final String q_comments = "353";
    public static final String q_views = "354";
    public static final String q_shares = "355";
    public static final String q_last_used = "356";
    public static final String q_topic_id = "357";
    public static final String q_quest_desc = "1358";
    public static final String q_attempted_by = "359";
    public static final String q_solved_by = "360";
    public static final String q_media_type = "1361";
    public static final String q_options_type = "1362";

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

    //latest Added parameter For Test List
    public static final String publishedDate = "1223E";
    public static final String author = "1223F";
    public static final String quest_count = "1223D";
    public static final String likeCount = "1223A";
    public static final String shareCount = "1223B";
    public static final String commentsCount = "1223C";
    public static final String isLike = "1403";
    public static final String isFavourite = "1410";

    //Notification Fields
    public static final String cn_connected = "309";
    public static final String cn_request_active = "313";
    public static final String cn_initiated_by_u1 = "311";
    public static final String cn_initiated_by_u2 = "312";
    public static final String n_id = "297";
    public static final String n_sent_by_u_id = "298";
    public static final String n_sent_to_u_id = "299";
    public static final String n_type = "300";
    public static final String n_read_by_user = "301";
    public static final String w_notification_desc = "515";
    public static final String n_ref_id = "302";
    public static final String n_ref_type = "303";
    public static final String n_cdatetime = "305";
    public static final String w_user_profile_image_name = "507";





    //Start Resume missing Parameter
    public static final String sm_sub_name = "1301";

    /***
     *  SCQ("1"),
     *     MCQ("2"),
     *     Fill_THE_BLANKS("3"),
     *     ONE_LINE_ANSWER("4"),
     *     SHORT_ANSWER("5"),
     *     LONG_ANSWER("6");
     */

    public static final String SCQ = "1";
    public static final String MCQ = "2";
    public static final String Fill_THE_BLANKS = "3";
    public static final String ONE_LINE_ANSWER = "4";
    public static final String SHORT_ANSWER = "5";
    public static final String LONG_ANSWER = "6";

    public static final String PRODUCTION_BASE_FILE_API = "https://jtmobileappstorage.blob.core.windows.net/spotmeets/media/images/";


}