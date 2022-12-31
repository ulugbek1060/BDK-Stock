package com.android.model.database.jobs.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.android.model.repository.jobs.entity.JobEntity

@Entity(tableName = "job")
data class JobRoomEntity(
   @PrimaryKey val id: Long,
   val name: String
) {

   fun mapTotJobEntity() = JobEntity(
      id = id.toInt(),
      name = name
   )
}