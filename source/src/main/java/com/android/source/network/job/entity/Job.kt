package com.android.source.network.job.entity


import com.android.model.repository.jobs.entity.JobEntity
import com.google.gson.annotations.SerializedName

data class Job(
   @SerializedName("id")
   val id: Long, // 1
   @SerializedName("name")
   val name: String, // Admin
) : java.io.Serializable {

   fun toJobEntity() = JobEntity(
      id = id,
      name = name
   )
}