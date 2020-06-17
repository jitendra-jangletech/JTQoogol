package com.jangletech.qoogol.database.repo;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.jangletech.qoogol.database.QoogolDatabase;
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

    public LiveData<List<Education>> getUserEducations() {
        return educationDetailsDao.getAllUserEducations();
    }
    /*public LiveData<TestDetailsResponse> getTestDetails() {
        return testDetailsDao.getTestDetails();
    }*/

    public LiveData<List<Notification>> getAllNotifications() {
        return notificationDao.getAllNotifications();
    }

   /* public LiveData<List<AttemptedTest>> getAllAttemptedTests() {
        return attemptedTestDao.getAllAttemptedTests();
    }*/

    public LiveData<List<TestModelNew>> getAllTests() {
        return testDao.getAllTests();
    }

    public void insertTests(List<TestModelNew> testModelNewList) {
        insertTestsAsync(testModelNewList);
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

    public void insertQuestions(List<LearningQuestionsNew> learningQuestions) {
        learningQuestionDao.upsertQuestions(learningQuestions);
    }

    public void insertQuestion(LearningQuestions learningQuestions) {
        learningQuestionDao.insertQuestion(learningQuestions);
    }

    public void deleteQuestion(String questionId) {
        learningQuestionDao.deleteQuestion(questionId);
    }

    public void insertConnections(List<Connections> connectionsList) {
        connectionsDao.upsertConnections(connectionsList);
    }
    public void insertFriends(List<Friends> friendsList) {
        friendsDao.upsertFriends(friendsList);
    }

    public void insertFriendReq(List<FriendRequest> friendRequests) {
        friendReqDao.insertFriendReq(friendRequests);
    }

    public void insertFollowdReq(List<FollowRequest> followRequestList) {
        followReqDao.insertFollowdReq(followRequestList);
    }


    public void insertFollowings(List<Following> followingList) {
        followingsDao.upsertFollowings(followingList);
    }

    public void insertFollowers(List<Followers> followersList) {
        followersDao.upsertFollowers(followersList);
    }

    public LiveData<List<Connections>> getConnectionsFromDb(String userID) {
        return connectionsDao.getAllConnections(userID);
    }

    public LiveData<List<Friends>> getFriendsFromDb(String userID) {
        return friendsDao.getAllFriends(userID);
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

    public LiveData<List<LearningQuestions>> getSavedQuestionsFromDb() {
        return learningQuestionDao.getAllSavedQuestions();
    }


    public List<LearningQuestionsNew> getQuestions() {
        return learningQuestionDao.getQuestions();
    }


}
