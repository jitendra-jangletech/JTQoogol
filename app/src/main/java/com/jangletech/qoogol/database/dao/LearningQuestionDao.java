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

    @Query("SELECT * FROM LearningQuestionsNew")
    List<LearningQuestionsNew> getQuestions();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertQuestions(List<LearningQuestionsNew> learningQuestions);

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
