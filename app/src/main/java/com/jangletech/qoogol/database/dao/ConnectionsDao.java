package com.jangletech.qoogol.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.jangletech.qoogol.model.Connections;
import com.jangletech.qoogol.model.Friends;

import java.util.List;

/**
 * Created by Pritali on 6/8/2020.
 */
@Dao
public interface ConnectionsDao {

    @Query("SELECT * FROM Connections where user_id=:userID")
    LiveData<List<Connections>> getAllConnections(String userID);

    @Query("SELECT * FROM Connections")
    List<Connections> getConnections();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertConnections(List<Connections> connectionsList);

    @Delete
    void delete(List<Connections> connectionsList);

    @Query("delete from Connections where user_id =:loggedInuserId and cn_user_id_2 =:cnUserId")
    void deleteConnections(String loggedInuserId,String cnUserId);

    @Update
    void updateConnections(List<Connections> connectionsList);

    @Transaction
    default void upsertConnections(List<Connections> connectionsList) {
        insertConnections(connectionsList);
        updateConnections(connectionsList);
    }
}
