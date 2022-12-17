package com.android.model.repository.jobs.entity

import com.android.model.database.jobs.entity.JobRoomEntity
import java.io.Serializable

data class JobEntity(
   val id: Int,
   val name: String
) : Serializable {

   fun toJobRoomEntity() =
      JobRoomEntity(
         id = id.toLong(),
         name = name
      )

   override fun toString(): String {
      return name
   }
}