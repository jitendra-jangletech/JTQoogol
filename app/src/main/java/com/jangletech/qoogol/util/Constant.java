package com.jangletech.qoogol.util;

/**
 * Created by Pritali on 1/28/2020.
 */
public class Constant {

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
    public static final String COURSE = "sm15CourseData";
    public static final String INSTITUTE = "sm13InstOrgData";
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

    // Questions
    public static final String q_id = "325";
    public static final String q_cm_id = "326";
    public static final String q_md_id = "327";
    public static final String q_sm_id = "328";
    public static final String q_up_u_id = "329";
    public static final String q_credit = "330";
    public static final String q_mcq_op_1 = "331";
    public static final String q_mcq_op_2 = "332";
    public static final String q_mcq_op_3 = "333";
    public static final String q_mcq_op_4 = "334";
    public static final String q_mcq_op_5 = "335";
    public static final String q_marks = "336";
    public static final String q_diff_level = "337";
    public static final String q_trending = "338";
    public static final String q_popular = "339";
    public static final String q_recent = "340";
    public static final String q_avg_ratings = "341";
    public static final String q_no_of_ratings = "342";
    public static final String q_status = "343";
    public static final String q_cdatetime = "344";
    public static final String q_udatetime = "345";
    public static final String q_type = "346";
    public static final String q_source = "347";
    public static final String q_quest = "348";
    public static final String q_view_count = "349";
    public static final String q_category = "350";
    public static final String q_duration = "351";
    public static final String q_likes = "352";
    public static final String q_comments = "353";
    public static final String q_views = "354";
    public static final String q_shares = "355";
    public static final String q_last_used = "356";
    public static final String q_topic_id = "357";
    public static final String q_quest_desc = "358";
    public static final String q_attempted_by = "359";
    public static final String q_solved_by = "360";

    //Answers
    public static final String a_id = "363";
    public static final String a_q_id = "364";
    public static final String a_up_u_id = "365";
    public static final String a_sub_ans = "366";
    public static final String a_mcq_1 = "367";
    public static final String a_upvotes = "368";
    public static final String a_downvotes = "369";
    public static final String a_cdatetime = "370";
    public static final String a_udatetime = "371";
    public static final String a_status = "372";
    public static final String a_mcq_2 = "373";
    public static final String a_mcq_3 = "374";
    public static final String a_mcq_4 = "375";
    public static final String a_mcq_5 = "376";
    public static final String a_md_id = "377";
    public static final String a_ans_desc = "378";

    // QLikesComments
    public static final String qlc_id = "381";
    public static final String qlc_q_id = "382";
    public static final String qlc_like_flag = "383";
    public static final String qlc_comment_flag = "384";
    public static final String qlc_comment_text = "385";
    public static final String qlc_user_id = "386";
    public static final String qlc_share_flag = "387";
    public static final String qlc_cdatetime = "388";
    public static final String qlc_deleted = "389";
    public static final String qlc_fav_flag = "390";

    // ChapterMaster
    public static final String cm_id = "315";
    public static final String cm_chapter_name = "316";


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
}