package com.android.model.repository.jobs

import androidx.room.withTransaction
import com.android.model.database.AppDatabase
import com.android.model.repository.base.BaseRepository
import com.android.model.utils.BackendException
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class JobRepository @Inject constructor(
   private val jobSource: JobSource,
   private val appDatabase: AppDatabase
) : BaseRepository() {

   fun getJobs() = networkBoundResult(
      query = {
         appDatabase.getJobsDao().getJobs()
      },
      fetch = {
         jobSource.getJobs()
      },
      saveFetchedResult = { jobEntityList ->
         appDatabase.withTransaction {
            appDatabase.getJobsDao().apply {
               clear()
               save(jobEntityList.map { it.toJobRoomEntity() })
            }
         }
      }
   ).map { result ->
      result.map { list ->
         list?.map {
            it.mapTotJobEntity()
         }
      }
   }
}