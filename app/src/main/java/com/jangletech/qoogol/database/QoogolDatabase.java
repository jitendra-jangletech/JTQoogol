package com.jangletech.qoogol.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.jangletech.qoogol.database.dao.WordDao;
import com.jangletech.qoogol.model.Word;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Word.class},version = 1, exportSchema = false)
public abstract  class QoogolDatabase extends RoomDatabase {

    public abstract WordDao wordDao();

    private static volatile QoogolDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static QoogolDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (QoogolDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            QoogolDatabase.class, "word_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
