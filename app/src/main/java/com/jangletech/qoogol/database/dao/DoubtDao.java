package com.jangletech.qoogol.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.jangletech.qoogol.model.DoubtInfo;

import java.util.List;

/**
 * Created by Pritali on 8/21/2020.
 */
@Dao
public interface DoubtDao {
    @Query("SELECT * FROM DoubtInfo where user_id=:userID")
    List<DoubtInfo> getMyDoubts(String userID);

    @Query("SELECT * FROM DoubtInfo where question_id=:questionID")
    List<DoubtInfo> getQuestionDoubts(String questionID);

    @Query("SELECT * FROM DoubtInfo")
    List<DoubtInfo> getAllDoubts();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDoubts(List<DoubtInfo> doubtInfoList);

    @Delete
    void delete(List<DoubtInfo> doubtInfoList);

    @Update
    void updateDoubts(List<DoubtInfo> doubtInfoList);
}
