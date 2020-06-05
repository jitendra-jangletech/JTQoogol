package com.jangletech.qoogol.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.jangletech.qoogol.model.TestDetailsResponse;

@Dao
public interface TestDetailsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TestDetailsResponse testDetailsResponse);

    /*@Query("Select * from TestDetailsResponse")
    LiveData<TestDetailsResponse> getTestDetails();

    @Query("delete from TestDetailsResponse")
    void delete();*/
}
