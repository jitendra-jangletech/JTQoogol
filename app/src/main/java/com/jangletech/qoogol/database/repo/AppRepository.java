package com.jangletech.qoogol.database.repo;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.jangletech.qoogol.database.QoogolDatabase;
import com.jangletech.qoogol.database.dao.DashboardDao;
import com.jangletech.qoogol.database.dao.EducationDetailsDao;
import com.jangletech.qoogol.database.dao.LearningQuestionDao;
import com.jangletech.qoogol.database.dao.NotificationDao;
import com.jangletech.qoogol.database.dao.TestDao;
import com.jangletech.qoogol.database.dao.UserProfileDao;
import com.jangletech.qoogol.model.Connections;
import com.jangletech.qoogol.model.DashBoard;
import com.jangletech.qoogol.model.Education;
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
    //private final TestDetailsDao testDetailsDao;

    public AppRepository(Context context) {
        QoogolDatabase db = QoogolDatabase.getDatabase(context);
        userProfileDao = db.userProfileDao();
        educationDetailsDao = db.educationDetailsDao();
        notificationDao = db.notificationDao();
        dashboardDao = db.dashboardDao();
        testDao = db.testDao();
        learningQuestionDao = db.learningQuestionDao();
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
        deleteNotificationAsync();
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

   /* private void deleteAttemptedTestAsync() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    attemptedTestDao.deleteAttemptedTests();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }*/



    private void deleteNotificationAsync() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    notificationDao.deleteAllNotifications();
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
    public LiveData<List<LearningQuestionsNew>> getQuestionsFromDb(){
        return learningQuestionDao.getAllQuestions();
    }
    public List<LearningQuestionsNew> getQuestions(){
        return learningQuestionDao.getQuestions();
    }


}
