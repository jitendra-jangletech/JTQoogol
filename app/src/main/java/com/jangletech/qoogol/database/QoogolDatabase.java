package com.jangletech.qoogol.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.jangletech.qoogol.database.converter.Converters;
import com.jangletech.qoogol.database.dao.LearningQuestionDao;
import com.jangletech.qoogol.database.dao.WordDao;
import com.jangletech.qoogol.model.LearningQuestions;
import com.jangletech.qoogol.model.Word;
import com.jangletech.qoogol.database.dao.TestDao;
import com.jangletech.qoogol.database.dao.TestQuestionDao;
import com.jangletech.qoogol.model.TestModel;
import com.jangletech.qoogol.model.TestQuestion;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Word.class, LearningQuestions.class},version = 1, exportSchema = false)
@TypeConverters(Converters.class)
@Database(entities = {TestModel.class, TestQuestion.class},version = 1)
public abstract  class QoogolDatabase extends RoomDatabase {

    public abstract WordDao wordDao();
    public abstract LearningQuestionDao learningQuestionDao();
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

    public static QoogolDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (QoogolDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            QoogolDatabase.class,"qoogol_database")
                            .addCallback(sRoomDatabaseCallback)
                            QoogolDatabase.class, "qoogol_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);

            // If you want to keep data through app restarts,
            // comment out the following block
            databaseWriteExecutor.execute(() -> {
                // Populate the database in the background.
                // If you want to start with more words, just add them.

                TestDao dao = INSTANCE.testDao();
                dao.deleteAll();

                List<TestModel> list  = setMyTestList();

                for (int i = 0; i <list.size() ; i++) {
                    dao.insert(list.get(i));
                }

            });
        }
    };

    public static List<TestModel> setMyTestList() {
        List<TestModel> testList = new ArrayList<>();
        testList.clear();

        TestModel testModel = new TestModel("Shapes and Angles", "Mathematics", "40",
                "30", "Hard", "88/100", "219", "Jan 2020", "2093",
                true, true, "Mr. Sharan",
                "Phd. Mathematics", "Unit Test-Final", "4.3",
                "100", true, 1, false);

        TestModel testModel1 = new TestModel("Reading Comprehension", "English", "120",
                "40", "Easy", "53/100", "102", "Mar 2019", "1633",
                false, true,
                "Mr. Goswami", "Phd. English", "Unit Test-Final", "2.7",
                "60", false, 9, false);

        TestModel testModel2 = new TestModel("When the Earth Shook!", "Physics", "40",
                "60", "Medium", "12/100", "10", "Jul 2019", "8353",
                true, false, "Mr. Narayan", "Phd. Physics",
                "Unit Test-Final", "2", "30", false, 0, false);

        TestModel testModel3 = new TestModel("Shapes and Angles", "Mathematics", "40",
                "30", "Hard", "88/100", "219", "Jan 2020", "2093",
                true, true, "Mr. Sharan", "Phd. Mathematics",
                "Unit Test-Final", "4.3", "100", false, 25, false);

        TestModel testModel4 = new TestModel("Reading Comprehension", "English", "120",
                "40", "Easy", "53/100", "102", "Mar 2019", "1633",
                false, true, "Mr. Goswami", "Phd. English",
                "Unit Test-Final", "2.7", "60", true, 3, false);

        TestModel testModel5 = new TestModel("When the Earth Shook!", "Evs", "40",
                "60", "Medium", "12/100", "10", "Jul 2019", "8353",
                true, false, "Mr. Narayan", "Phd. Evs",
                "Unit Test-Final", "2", "30", true, 4, true);

        testList.add(testModel);
        testList.add(testModel1);
        testList.add(testModel2);
        testList.add(testModel3);
        testList.add(testModel4);
        testList.add(testModel5);

        return testList;
    }


}
