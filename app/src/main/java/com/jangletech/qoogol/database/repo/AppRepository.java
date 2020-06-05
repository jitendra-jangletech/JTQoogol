package com.jangletech.qoogol.database.repo;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.jangletech.qoogol.database.QoogolDatabase;
import com.jangletech.qoogol.database.dao.LearningQuestionDao;
import com.jangletech.qoogol.model.LearningQuestionsNew;

import java.util.List;

/**
 * Created by Pritali on 6/2/2020.
 */
public class AppRepository {
    private LearningQuestionDao learningQuestionDao;

    public AppRepository(Context application) {
        QoogolDatabase db = QoogolDatabase.getDatabase(application);
        learningQuestionDao = db.learningQuestionDao();
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
