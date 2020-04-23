package com.jangletech.qoogol.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.jangletech.qoogol.model.TestModel;

import java.util.List;

@Dao
public interface TestDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(TestModel testModel);

    @Query("DELETE FROM tests")
    void deleteAll();

    @Query("SELECT * from tests")
    LiveData<List<TestModel>> getAllTests();

}
