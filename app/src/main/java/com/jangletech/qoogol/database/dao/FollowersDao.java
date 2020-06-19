package com.jangletech.qoogol.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.jangletech.qoogol.model.Followers;

import java.util.List;

/**
 * Created by Pritali on 6/10/2020.
 */
@Dao
public interface FollowersDao {

    @Query("SELECT * FROM Followers where user_id=:userID")
    LiveData<List<Followers>> getAllFollowers(String userID);

    @Query("SELECT * FROM Followers")
    List<Followers> getFollowers();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFollowers(List<Followers> followersList);

    @Delete
    void delete(List<Followers> followersList);

    @Update
    void updateFollowers(List<Followers> followersList);

    @Transaction
    default void upsertFollowers(List<Followers> followersList) {
        insertFollowers(followersList);
        updateFollowers(followersList);
    }
}
