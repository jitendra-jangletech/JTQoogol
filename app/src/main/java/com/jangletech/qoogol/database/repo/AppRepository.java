package com.jangletech.qoogol.database.repo;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.jangletech.qoogol.database.QoogolDatabase;
import com.jangletech.qoogol.database.dao.BlockedDao;
import com.jangletech.qoogol.database.dao.ConnectionsDao;
import com.jangletech.qoogol.database.dao.DashboardDao;
import com.jangletech.qoogol.database.dao.EducationDetailsDao;
import com.jangletech.qoogol.database.dao.FollowReqDao;
import com.jangletech.qoogol.database.dao.FollowersDao;
import com.jangletech.qoogol.database.dao.FollowingsDao;
import com.jangletech.qoogol.database.dao.FriendReqDao;
import com.jangletech.qoogol.database.dao.FriendsDao;
import com.jangletech.qoogol.database.dao.LearningQuestionDao;
import com.jangletech.qoogol.database.dao.NotificationDao;
import com.jangletech.qoogol.database.dao.TestDao;
import com.jangletech.qoogol.database.dao.UserProfileDao;
import com.jangletech.qoogol.model.BlockedConnections;
import com.jangletech.qoogol.model.Connections;
import com.jangletech.qoogol.model.DashBoard;
import com.jangletech.qoogol.model.Education;
import com.jangletech.qoogol.model.FollowRequest;
import com.jangletech.qoogol.model.Followers;
import com.jangletech.qoogol.model.Following;
import com.jangletech.qoogol.model.FriendRequest;
import com.jangletech.qoogol.model.Friends;
import com.jangletech.qoogol.model.LearningQuestions;
import com.jangletech.qoogol.model.LearningQuestionsNew;
import com.jangletech.qoogol.model.Notification;
import com.jangletech.qoogol.model.TestModelNew;
import com.jangletech.qoogol.model.UserProfile;

import java.util.List;

public class AppRepository {

    private static final String TAG = "UserProfileRepository";
    private final UserProfileDao userProfileDao;
    private final EducationDetailsDao educationDetailsDao;
    private final NotificationDao notificationDao;
    private final DashboardDao dashboardDao;
    private final TestDao testDao;
    private final LearningQuestionDao learningQuestionDao;
    private final FriendsDao friendsDao;
    private final FollowersDao followersDao;
    private final FollowingsDao followingsDao;
    private final FriendReqDao friendReqDao;
    private final FollowReqDao followReqDao;
    private final ConnectionsDao connectionsDao;
    private final BlockedDao blockedDao;
    //private final TestDetailsDao testDetailsDao;

    public AppRepository(Context context) {
        QoogolDatabase db = QoogolDatabase.getDatabase(context);
        userProfileDao = db.userProfileDao();
        educationDetailsDao = db.educationDetailsDao();
        notificationDao = db.notificationDao();
        dashboardDao = db.dashboardDao();
        testDao = db.testDao();
        learningQuestionDao = db.learningQuestionDao();
        friendsDao = db.friendsDao();
        followersDao = db.followersDao();
        followingsDao = db.followingsDao();
        friendReqDao = db.friendReqDao();
        followReqDao = db.followReqDao();
        connectionsDao = db.connectionsDao();
        blockedDao = db.blockedDao();
        //testDetailsDao = db.testDetailsDao();
    }

    public LiveData<DashBoard> getDashBoardData(String uId) {
        return dashboardDao.getDashboardDetails(uId);
    }

    public LiveData<UserProfile> getUserProfile(String userId) {
        return userProfileDao.getUserProfile(userId);
    }

    public UserProfile getUserProfilePrev(String userId) {
        return userProfileDao.getUserProfilePrev(userId);
    }

    public LiveData<List<Education>> getUserEducations(String uid) {
        return educationDetailsDao.getAllUserEducations(uid);
    }
    /*public LiveData<TestDetailsResponse> getTestDetails() {
        return testDetailsDao.getTestDetails();
    }*/

    public LiveData<List<Notification>> getAllNotifications(String uid) {
        return notificationDao.getAllNotifications(uid);
    }

   /* public LiveData<List<AttemptedTest>> getAllAttemptedTests() {
        return attemptedTestDao.getAllAttemptedTests();
    }*/

    public LiveData<List<TestModelNew>> getAllTests(String flag,String uId) {
        return testDao.getAllTests(flag,uId);
    }
    public LiveData<List<TestModelNew>> getAllFavTests(String flag,String uId) {
        return testDao.getAllFavTest(flag,uId,true);
    }

    public void updateFavTest(String flag,String uId,int tmId,boolean value) {
         testDao.updateFav(flag,uId,tmId,value);
    }

    public LiveData<List<TestModelNew>> getAllTestsFiltered(String flag,String uId,String diffLevel) {
        return testDao.getAllTestsDiffLevel(flag,uId,diffLevel);
    }
    public LiveData<List<TestModelNew>> getAllTestsAvgRating(String flag,String uId,String rating) {
        return testDao.getAllTestsAvgRating(flag,uId,rating);
    }

    public void insertTests(List<TestModelNew> testModelNewList) {
        insertTestsAsync(testModelNewList);
    }

    public void insertConnections(List<Connections> connections){
        insertAllConnectionsAsync(connections);
    }

    public void deleteConnections(String logInuser, String otherUser) {
        connectionsDao.deleteConnections(logInuser,otherUser);
    }
    public void insertFriendRequests(List<FriendRequest> friendRequests){
        insertAllFriendReqAsync(friendRequests);
    }

    public void insertFollowRequests(List<FollowRequest> followRequests){
        insertAllFollowReqAsync(followRequests);
    }

    /*public void insertTestDetails(TestDetailsResponse testDetailsResponse) {
        insertTestDetailsAsync(testDetailsResponse);
    }*/

    public void deleteAllEducations() {
        deleteAllEducationsAsync();
    }

    public void deleteTests() {
        deleteTestsAsync();
    }

  /*  public void insertAttemptedTest(List<AttemptedTest> attemptedTests) {
        insertAttemptedTestAsync(attemptedTests);
    }

    public void deleteAttemptedTest() {
        deleteAttemptedTestAsync();
    }*/

    public void insertEducationInfo(List<Education> educations) {
        insertEducationAsync(educations);
    }

    public void deleteEducationInfo(String id) {
        deleteEducationAsync(id);
    }

    public void insertPersonalInfo(UserProfile userProfile) {
        insertAsync(userProfile);
    }

    public void deleteNotifications() {
        deleteNotificationAsync("");
    }

    public void deleteNotification(String nId) {
        deleteNotificationAsync(nId);
    }

    public void deleteDashboardData() {
        deleteDashboardDataAsync();
    }

    public void insertDashboardData(DashBoard dashBoard) {
        insertDashboardDataAsync(dashBoard);
    }

    public void insertNotifications(List<Notification> notifications) {
        insertNotificationAsync(notifications);
    }

    public void deletePersonalInfo() {
        deleteAsync();
    }

    private void insertEducationAsync(final List<Education> educations) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    educationDetailsDao.insertAll(educations);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void insertTestsAsync(final List<TestModelNew> testModelNewList) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    testDao.insertAll(testModelNewList);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /*private void insertTestDetailsAsync(TestDetailsResponse testDetailsResponse) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    testDetailsDao.insert(testDetailsResponse);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }*/

    /*private void insertAttemptedTestAsync(final List<AttemptedTest> attemptedTests) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    attemptedTestDao.insertAll(attemptedTests);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }*/


    private void insertNotificationAsync(final List<Notification> notifications) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    notificationDao.insertAll(notifications);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void insertAllConnectionsAsync(final List<Connections> connections) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    connectionsDao.insertConnections(connections);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void insertAllFriendReqAsync(final List<FriendRequest> friendRequests) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    friendReqDao.insertFriendReq(friendRequests);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void insertAllFollowReqAsync(final List<FollowRequest> followRequests) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    followReqDao.insertFollowdReq(followRequests);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void insertDashboardDataAsync(DashBoard dashBoard) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    dashboardDao.insert(dashBoard);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private void deleteDashboardDataAsync() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    dashboardDao.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private void deleteTestsAsync() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    testDao.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void deleteEducationAsync(String id) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    educationDetailsDao.deleteEducation(id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void deleteAllEducationsAsync() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    educationDetailsDao.deleteAllEducation();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }




    private void deleteNotificationAsync(String nId) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(nId.isEmpty()) {
                        notificationDao.deleteAllNotifications();
                    }else{
                        notificationDao.deleteNotification(nId);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void deleteAsync() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    userProfileDao.deleteUserProfile();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void insertAsync(final UserProfile userProfile) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    userProfileDao.insert(userProfile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void updatePersonalInfoAsync(final UserProfile userProfile) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    userProfileDao.insert(userProfile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void insertQuestions(List<LearningQuestionsNew> learningQuestions) {
        learningQuestionDao.upsertQuestions(learningQuestions);
    }

    public void insertSavedQuestions(List<LearningQuestions> learningQuestions) {
        learningQuestionDao.insertSavedQuestions(learningQuestions);
    }

    public void insertQuestion(LearningQuestions learningQuestions) {
        learningQuestionDao.insertQuestion(learningQuestions);
    }

    public void deleteQuestion(int questionId) {
        learningQuestionDao.deleteQuestion(questionId);
    }

    public void updateQuestion(int questionId, String flag) {
        learningQuestionDao.updateQuestion(questionId,flag);
    }

    public void insertFriends(List<Friends> friendsList) {
        friendsDao.upsertFriends(friendsList);
    }

    public void deleteFriends(String logInuser, String otherUser) {
        friendsDao.deleteFriends(logInuser,otherUser);
    }

    public void insertBlockedList(List<BlockedConnections> blockedConnections) {
        blockedDao.upsertBlockedConn(blockedConnections);
    }

    public void insertFriendReq(List<FriendRequest> friendRequests) {
        friendReqDao.insertFriendReq(friendRequests);
    }

    public void deleteFriendReq(String logInuser, String otherUser) {
        friendReqDao.deleteFriendReq(logInuser,otherUser);
    }

    public void insertFollowdReq(List<FollowRequest> followRequestList) {
        followReqDao.insertFollowdReq(followRequestList);
    }

    public void deleteFollowReq(String logInuser, String otherUser) {
        followReqDao.deleteFollowReq(logInuser,otherUser);
    }


    public void insertFollowings(List<Following> followingList) {
        followingsDao.upsertFollowings(followingList);
    }

    public void deleteFollowings(String logInuser, String otherUser) {
        followingsDao.deleteFollowing(logInuser,otherUser);
    }

    public void insertFollowers(List<Followers> followersList) {
        followersDao.upsertFollowers(followersList);
    }

    public void deleteFollowers(String logInuser, String otherUser) {
        followersDao.deleteFollower(logInuser,otherUser);
    }


    public LiveData<List<Connections>> getConnectionsFromDb(String userID) {
        return connectionsDao.getAllConnections(userID);
    }

    public LiveData<List<Friends>> getFriendsFromDb(String userID) {
        return friendsDao.getAllFriends(userID);
    }

    public LiveData<List<BlockedConnections>> getBlockListFromDb(String userID) {
        return blockedDao.getAllBlockedConn(userID);
    }

    public LiveData<List<FriendRequest>> getFriendReqFromDb(String userID) {
        return friendReqDao.getAllFriendReq(userID);
    }

    public LiveData<List<FollowRequest>> getFollowReqFromDb(String userID) {
        return followReqDao.getAllFollowReq(userID);
    }

    public LiveData<List<Followers>> getFollowersFromDb(String userID) {
        return followersDao.getAllFollowers(userID);
    }

    public LiveData<List<Following>> getFollowingFromDb(String userID) {
        return followingsDao.getAllFollowings(userID);
    }


    public LiveData<List<LearningQuestionsNew>> getQuestionsFromDb() {
        return learningQuestionDao.getAllQuestions();
    }

//    public LiveData<List<LearningQuestionsNew>> getQuestionsFromDb(String qtrending, String qpopular, String qrecent,
//                                                                   String ratings, String short_ans, String long_ans, String fill_the_blanks,
//                                                                   String scq, String mcq, String true_false, String match_pair) {
//        return learningQuestionDao.getAllQuestions(qtrending,qpopular,qrecent,ratings,short_ans,long_ans,fill_the_blanks,scq,mcq,true_false,match_pair);
//    }

    public LiveData<List<LearningQuestionsNew>> getQuestionFromDb(String q_id){
        try {
            return learningQuestionDao.getQuestion(String.valueOf(q_id));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public LiveData<List<LearningQuestionsNew>> getSharedQuestions(String CASE) {
        return learningQuestionDao.getSharedQuestions(CASE);
    }


    public LiveData<List<LearningQuestionsNew>> getFavQuestionsFromDb() {
        return learningQuestionDao.getFavQuestions("true");
    }

    public LiveData<List<LearningQuestions>> getSavedQuestionsFromDb() {
        return learningQuestionDao.getAllSavedQuestions();
    }

    public List<LearningQuestionsNew> getQuestions() {
        return learningQuestionDao.getQuestions();
    }

    public List<LearningQuestions> getSavedQuestions() {
        return learningQuestionDao.getSavedQuestions();
    }
}
