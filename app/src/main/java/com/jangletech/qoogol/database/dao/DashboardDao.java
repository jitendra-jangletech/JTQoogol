package com.jangletech.qoogol.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.jangletech.qoogol.model.DashBoard;

@Dao
public interface DashboardDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(DashBoard dashBoard);

    @Query("Select * from DashBoard where userId =:uId")
    LiveData<DashBoard> getDashboardDetails(String uId);

    @Query("delete from DashBoard")
    void delete();

}
