package com.jangletech.qoogol.database;

import android.content.Context;
import android.os.Environment;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import com.jangletech.qoogol.database.converter.Converters;
import com.jangletech.qoogol.database.dao.DashboardDao;
import com.jangletech.qoogol.database.dao.EducationDetailsDao;
import com.jangletech.qoogol.database.dao.LearningQuestionDao;
import com.jangletech.qoogol.database.dao.NotificationDao;
import com.jangletech.qoogol.database.dao.TestDao;
import com.jangletech.qoogol.database.dao.TestDetailsDao;
import com.jangletech.qoogol.database.dao.UserProfileDao;
import com.jangletech.qoogol.model.AttemptedTest;
import com.jangletech.qoogol.model.DashBoard;
import com.jangletech.qoogol.model.Education;
import com.jangletech.qoogol.model.LearningQuestionsNew;
import com.jangletech.qoogol.model.Notification;
import com.jangletech.qoogol.model.TestDetailsResponse;
import com.jangletech.qoogol.model.TestModelNew;
import com.jangletech.qoogol.model.TestQuestion;
import com.jangletech.qoogol.model.UserProfile;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {LearningQuestionsNew.class, DashBoard.class, TestModelNew.class,
        TestQuestion.class,UserProfile.class, Education.class,
        Notification.class}, version = 1, exportSchema = false)
@TypeConverters(Converters.class)
public abstract class QoogolDatabase extends RoomDatabase {

    public abstract UserProfileDao userProfileDao();
    public abstract EducationDetailsDao educationDetailsDao();
    public abstract NotificationDao notificationDao();
    public abstract DashboardDao dashboardDao();
    //public abstract TestDetailsDao testDetailsDao();
    public abstract TestDao testDao();
    public abstract LearningQuestionDao learningQuestionDao();

    private static volatile QoogolDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    private static final String DB_NAME = "qdb";
    private static final String DB_PATH = String.format("%s/%s",
            Environment.getExternalStorageDirectory().getAbsolutePath(), DB_NAME);

    public static QoogolDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (QoogolDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            QoogolDatabase.class, DB_NAME)
                            .build();
                }
            }
        }
        return INSTANCE;
    }


}
