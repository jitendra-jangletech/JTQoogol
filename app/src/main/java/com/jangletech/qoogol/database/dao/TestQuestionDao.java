package com.jangletech.qoogol.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.jangletech.qoogol.model.TestQuestion;

import java.util.List;

@Dao
public interface TestQuestionDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(TestQuestion testQuestion);

    @Query("DELETE FROM testquestion")
    void deleteAll();

    @Query("SELECT * from testquestion")
    LiveData<List<TestQuestion>> getTestQuestions();
}
