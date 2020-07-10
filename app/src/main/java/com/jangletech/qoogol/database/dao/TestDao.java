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

    @Query("Select * from TestModelNew where flag=:flag and userId=:uid")
    LiveData<List<TestModelNew>> getAllTests(String flag,String uid);

    @Query("Select * from TestModelNew where flag=:flag and userId=:uid and isFavourite=:isFav")
    LiveData<List<TestModelNew>> getAllFavTest(String flag,String uid,boolean isFav);

    @Query("update TestModelNew set isFavourite =:value where tm_id=:tmId and flag=:flag and userId=:uid")
    void updateFav(String flag,String uid,int tmId,boolean value);

    @Query("Select * from TestModelNew where flag=:flag and userId=:uid and tm_diff_level=:diff")
    LiveData<List<TestModelNew>> getAllTestsDiffLevel(String flag,String uid,String diff);

    @Query("Select * from TestModelNew where flag=:flag and userId=:uid and tm_avg_rating=:rating")
    LiveData<List<TestModelNew>> getAllTestsAvgRating(String flag,String uid,String rating);

    @Query("delete from TestModelNew")
    void delete();

}
