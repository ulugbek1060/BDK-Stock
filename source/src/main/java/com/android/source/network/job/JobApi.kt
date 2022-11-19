package com.android.source.network.job

import com.android.source.network.job.entity.JobsResponseEntity
import retrofit2.http.GET
import retrofit2.http.Headers

interface JobApi {
   @Headers("Content-Type: application/json", "Accept: application/json")
   @GET("api/job/get")
   suspend fun getJobs(): JobsResponseEntity
}