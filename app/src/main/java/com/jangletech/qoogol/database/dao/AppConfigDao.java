package com.jangletech.qoogol.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.jangletech.qoogol.model.AppConfigResponse;

@Dao
public interface AppConfigDao {

    @Query("SELECT * FROM AppConfigResponse")
    LiveData<AppConfigResponse> getAppConfig();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAppConfig(AppConfigResponse appConfigResponse);
}
