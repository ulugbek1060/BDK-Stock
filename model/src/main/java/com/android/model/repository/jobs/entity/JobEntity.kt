package com.android.model.repository.jobs.entity

import com.android.model.database.jobs.entity.JobRoomEntity
import java.io.Serializable

data class JobEntity(
   val id: Long,
   val name: String
) : Serializable {

   // for saving to database
   fun toJobRoomEntity() =
      JobRoomEntity(
         id = id,
         name = name
      )

   override fun toString(): String {
      return name
   }
}