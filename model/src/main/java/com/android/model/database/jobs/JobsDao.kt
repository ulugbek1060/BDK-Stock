package com.android.model.database.jobs

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.android.model.database.jobs.entity.JobRoomEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JobsDao {

   @Insert(onConflict = REPLACE)
   suspend fun save(jobs: List<JobRoomEntity>)

   @Query("DELETE FROM job")
   suspend fun clear()

   @Query("SELECT * FROM job")
   fun getJobs(): Flow<List<JobRoomEntity>?>

}