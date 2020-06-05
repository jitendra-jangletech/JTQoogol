package com.jangletech.qoogol.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.jangletech.qoogol.model.TestModelNew;

import java.util.List;

@Dao
public interface TestDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<TestModelNew> testModelNewList);

    @Query("Select * from TestModelNew")
    LiveData<List<TestModelNew>> getAllTests();

    @Query("delete from TestModelNew")
    void delete();

}
