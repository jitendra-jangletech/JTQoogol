package com.jangletech.qoogol.database;

import android.content.Context;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.jangletech.qoogol.database.converter.Converters;
import com.jangletech.qoogol.database.dao.LearningQuestionDao;
import com.jangletech.qoogol.database.dao.TestDao;
import com.jangletech.qoogol.database.dao.TestQuestionDao;
import com.jangletech.qoogol.model.LearningQuestionsNew;
import com.jangletech.qoogol.model.TestModel;
import com.jangletech.qoogol.model.TestQuestion;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {LearningQuestionsNew.class, TestModel.class, TestQuestion.class}, version = 1, exportSchema = false)
@TypeConverters(Converters.class)
public abstract class QoogolDatabase extends RoomDatabase {

    public abstract TestDao testDao();

    public abstract TestQuestionDao testQuestionDao();


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
                            QoogolDatabase.class, "qoogol_database")
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }


    public abstract LearningQuestionDao learningQuestionDao();


    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            databaseWriteExecutor.execute(() -> {
                TestDao dao = INSTANCE.testDao();
                TestQuestionDao testQuestionDao = INSTANCE.testQuestionDao();

            });
        }
    };

}
