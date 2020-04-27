package com.jangletech.qoogol.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.jangletech.qoogol.database.converter.Converters;
import com.jangletech.qoogol.database.dao.LearningQuestionDao;
import com.jangletech.qoogol.database.dao.WordDao;
import com.jangletech.qoogol.model.LearningQuestions;
import com.jangletech.qoogol.model.Word;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Word.class, LearningQuestions.class},version = 1, exportSchema = false)
@TypeConverters(Converters.class)
public abstract  class QoogolDatabase extends RoomDatabase {

    public abstract WordDao wordDao();
    public abstract LearningQuestionDao learningQuestionDao();

    private static volatile QoogolDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static QoogolDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (QoogolDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            QoogolDatabase.class, "qoogol_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
