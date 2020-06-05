package com.jangletech.qoogol.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.jangletech.qoogol.model.Education;
import java.util.List;

@Dao
public interface EducationDetailsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Education education);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<Education> educations);

    @Query("Select * from Education where ue_id =:ueId")
    LiveData<Education> getUserEducation(String ueId);

    @Query("Select * from Education")
    LiveData<List<Education>> getAllUserEducations();

    @Query("delete from Education")
    void deleteAllEducations();

    @Query("delete from Education where ue_id =:ueId")
    void deleteEducation(String ueId);

}
