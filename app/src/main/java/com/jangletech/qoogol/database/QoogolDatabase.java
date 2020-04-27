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
import com.jangletech.qoogol.enums.QuestionType;
import com.jangletech.qoogol.model.LearningQuestions;
import com.jangletech.qoogol.model.TestModel;
import com.jangletech.qoogol.model.TestQuestion;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {LearningQuestions.class, TestModel.class, TestQuestion.class},version = 1)
@TypeConverters(Converters.class)
public abstract  class QoogolDatabase extends RoomDatabase {

    public abstract TestDao testDao();
    public abstract TestQuestionDao testQuestionDao();
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
                            QoogolDatabase.class,"qoogol_database")
                            .addCallback(sRoomDatabaseCallback)
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
                TestQuestionDao testQuestionDao = INSTANCE.testQuestionDao();

                List<TestModel> list  = setMyTestList();
                for (int i = 0; i <list.size() ; i++) {
                    dao.insert(list.get(i));
                }

                List<TestQuestion> testQList  = setQuestionList();
                for (int i = 0; i <testQList.size() ; i++) {
                    testQuestionDao.insert(testQList.get(i));
                }
            });
        }
    };

    public static List<TestModel> setMyTestList() {
        List<TestModel> testList = new ArrayList<>();

//        TestModel testModel = new TestModel(0,"Shapes and Angles", "Mathematics", "40",
//                "30", "Hard", "88/100", "219", "Jan 2020", "2093",
//                true, true, "Mr. Sharan",
//                "Phd. Mathematics", "Unit Test-Final", "4.3",
//                "100", true, 1, false);
//
//        TestModel testModel1 = new TestModel(1,"Reading Comprehension", "English", "120",
//                "40", "Easy", "53/100", "102", "Mar 2019", "1633",
//                false, true,
//                "Mr. Goswami", "Phd. English", "Unit Test-Final", "2.7",
//                "60", false, 9, false);
//
//        TestModel testModel2 = new TestModel(2,"When the Earth Shook!", "Physics", "40",
//                "60", "Medium", "12/100", "10", "Jul 2019", "8353",
//                true, false, "Mr. Narayan", "Phd. Physics",
//                "Unit Test-Final", "2", "30", false, 0, false);
//
//        TestModel testModel3 = new TestModel(3,"Shapes and Angles", "Mathematics", "40",
//                "30", "Hard", "88/100", "219", "Jan 2020", "2093",
//                true, true, "Mr. Sharan", "Phd. Mathematics",
//                "Unit Test-Final", "4.3", "100", false, 25, false);
//
//        TestModel testModel4 = new TestModel(4,"Reading Comprehension", "English", "120",
//                "40", "Easy", "53/100", "102", "Mar 2019", "1633",
//                false, true, "Mr. Goswami", "Phd. English",
//                "Unit Test-Final", "2.7", "60", true, 3, false);
//
//        TestModel testModel5 = new TestModel(5,"When the Earth Shook!", "Evs", "40",
//                "60", "Medium", "12/100", "10", "Jul 2019", "8353",
//                true, false, "Mr. Narayan", "Phd. Evs",
//                "Unit Test-Final", "2", "30", true, 4, true);
//
//        testList.add(testModel);
//        testList.add(testModel1);
//        testList.add(testModel2);
//        testList.add(testModel3);
//        testList.add(testModel4);
//        testList.add(testModel5);

        return testList;
    }

    private static List<TestQuestion> setQuestionList() {
        List<TestQuestion> testQuestionList = new ArrayList<>();
        TestQuestion testQuestion = new TestQuestion(0, 1, "MCQ",
                "Web Pages are saved in which of the following format?", "http://", "HTML", "DOC", "URL", "BMP");

        TestQuestion testQuestion1 = new TestQuestion(1, 2, "SCQ",
                "System software acts as a bridge between the hardware and _____ software", "Management", "Processing",
                "Utility", "Application", "Embedded");


        TestQuestion testQuestion2 = new TestQuestion(2, 3, "SCQ",
                "Who is appointed as the brand ambassador of VISA recently?",
                "Ram Sing Yadav", "Arpinder Singh", "PV Sindhu", "Sania Mirza", "Sachin Tendulkar");


        TestQuestion testQuestion3 = new TestQuestion(3, 4, "MCQ",
                "Who built Jama Masjid at Delhi?",
                "Jahangir", "Qutub-ud-din-Albak", "Akbar", "Birbal", "Aurangjeb");
        TestQuestion testQuestion4 = new TestQuestion(4, 5, QuestionType.MCQ.toString(),
                "Which of the following helps in the blood clotting?", "Vitamin A", "Vitamin D", "Vitamin K", "Folic acid",
                "Calcium");

        TestQuestion testQuestion5 = new TestQuestion(5, 6, QuestionType.TRUE_FALSE.toString(),
                "Narendra Modi is Prime Minister of india?",
                "", "", "", "", "");

        TestQuestion testQuestion6 = new TestQuestion(6, 7, QuestionType.FILL_THE_BLANKS.toString(),
                "____ is origin of COVID-19 outbreak?",
                "", "", "", "", "");

        TestQuestion testQuestion7 = new TestQuestion(7, 8, QuestionType.MULTI_LINE_ANSWER.toString(),
                "Explain Android Activity Lifecycle?",
                "", "", "", "", "");

        testQuestionList.add(testQuestion);
        testQuestionList.add(testQuestion1);
        testQuestionList.add(testQuestion2);
        testQuestionList.add(testQuestion3);
        testQuestionList.add(testQuestion4);
        testQuestionList.add(testQuestion5);
        testQuestionList.add(testQuestion6);
        testQuestionList.add(testQuestion7);
        return testQuestionList;
    }


}
