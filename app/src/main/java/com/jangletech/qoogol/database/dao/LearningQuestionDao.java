package com.jangletech.qoogol.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.jangletech.qoogol.model.LearningQuestions;
import com.jangletech.qoogol.model.LearningQuestionsNew;

import java.util.List;

/**
 * Created by Pritali on 4/23/2020.
 */
@Dao
public interface LearningQuestionDao {

    @Query("SELECT * FROM LearningQuestionsNew")
    LiveData<List<LearningQuestionsNew>> getAllQuestions();

    @Query("SELECT * FROM LearningQuestionsNew where is_fav=:fav")
    LiveData<List<LearningQuestionsNew>> getFavQuestions(String fav);

    @Query("SELECT * FROM LearningQuestions")
    LiveData<List<LearningQuestions>> getAllSavedQuestions();

    @Query("SELECT * FROM LearningQuestionsNew")
    List<LearningQuestionsNew> getQuestions();

    @Query("SELECT * FROM LearningQuestions")
    List<LearningQuestions> getSavedQuestions();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertQuestions(List<LearningQuestionsNew> learningQuestions);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSavedQuestions(List<LearningQuestions> learningQuestions);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertQuestion(LearningQuestions learningQuestions);

    @Query("delete from LearningQuestions where question_id=:questionId")
    void deleteQuestion(String questionId);

    @Delete
    void delete(List<LearningQuestionsNew> learningQuestions);

    @Update
    void updateQuestions(List<LearningQuestionsNew> learningQuestions);

    @Transaction
    default void upsertQuestions(List<LearningQuestionsNew> messageList) {
        insertQuestions(messageList);
        updateQuestions(messageList);
    }
}
