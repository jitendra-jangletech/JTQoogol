package com.jangletech.qoogol.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.jangletech.qoogol.model.BlockedConnections;
import com.jangletech.qoogol.model.Friends;

import java.util.List;

/**
 * Created by Pritali on 6/8/2020.
 */
@Dao
public interface BlockedDao {

    @Query("SELECT * FROM BlockedConnections where user_id=:userID")
    LiveData<List<BlockedConnections>> getAllBlockedConn(String userID);

    @Query("SELECT * FROM BlockedConnections")
    List<BlockedConnections> getBlockedConn();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertBlockedConn(List<BlockedConnections> blockedList);

    @Delete
    void delete(List<BlockedConnections> blockedList);

    @Update
    void updateBlockedConn(List<BlockedConnections> blockedList);

    @Transaction
    default void upsertBlockedConn(List<BlockedConnections> blockedList) {
        insertBlockedConn(blockedList);
        updateBlockedConn(blockedList);
    }
}
