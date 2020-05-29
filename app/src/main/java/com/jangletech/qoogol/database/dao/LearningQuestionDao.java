package com.jangletech.qoogol.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
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
    List<LearningQuestionsNew> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(LearningQuestionsNew learningQuestions);

    @Delete
    void delete(LearningQuestionsNew learningQuestions);

    @Update
    void update(LearningQuestionsNew learningQuestions);
}
