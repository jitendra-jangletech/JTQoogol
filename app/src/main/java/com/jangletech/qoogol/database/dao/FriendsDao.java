package com.jangletech.qoogol.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.jangletech.qoogol.model.Friends;

import java.util.List;

/**
 * Created by Pritali on 6/8/2020.
 */
@Dao
public interface FriendsDao {

    @Query("SELECT * FROM Friends where user_id=:userID")
    LiveData<List<Friends>> getAllFriends(String userID);

    @Query("SELECT * FROM Friends")
    List<Friends> getFriends();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFriends(List<Friends> friendsList);

    @Delete
    void delete(List<Friends> friendsList);

    @Query("delete from Friends where user_id =:loggedInuserId and cn_user_id_2 =:cnUserId")
    void deleteFriends(String loggedInuserId,String cnUserId);

    @Update
    void updateFriends(List<Friends> friendsList);

    @Transaction
    default void upsertFriends(List<Friends> friendsList) {
        insertFriends(friendsList);
        updateFriends(friendsList);
    }
}
