package com.jangletech.qoogol.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.jangletech.qoogol.model.Comments;

import java.util.List;

/**
 * Created by Pritali on 4/23/2020.
 */

@Dao
public interface CommentsDao {

    @Query("SELECT * FROM Comments")
    List<Comments> getAll();

    @Insert
    void insert(Comments comments);

    @Delete
    void delete(Comments comments);

    @Update
    void update(Comments comments);
}
