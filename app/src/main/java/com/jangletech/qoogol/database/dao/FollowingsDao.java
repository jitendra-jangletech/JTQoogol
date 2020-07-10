package com.jangletech.qoogol.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.jangletech.qoogol.model.Following;

import java.util.List;

/**
 * Created by Pritali on 6/10/2020.
 */
@Dao
public interface FollowingsDao {

    @Query("SELECT * FROM Following where user_id=:userID")
    LiveData<List<Following>> getAllFollowings(String userID);

    @Query("SELECT * FROM Following")
    List<Following> getFollowings();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFollowings(List<Following> followingList);

    @Delete
    void delete(List<Following> followingList);

    @Query("delete from Following where user_id =:loggedInuserId and cn_user_id_2 =:cnUserId")
    void deleteFollowing(String loggedInuserId,String cnUserId);

    @Update
    void updateFollowings(List<Following> followingList);

    @Transaction
    default void upsertFollowings(List<Following> followingList) {
        insertFollowings(followingList);
        updateFollowings(followingList);
    }
}
