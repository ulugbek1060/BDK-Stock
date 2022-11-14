package com.android.source.network.job

import com.android.model.utils.Const
import com.android.source.network.job.entity.JobsResponseEntity
import retrofit2.http.GET
import retrofit2.http.Header

interface JobApi {

   @GET("api/job/get")
   suspend fun getJobs(
      @Header(Const.HEADER_KEY_CONTENT_TYPE) type: String = Const.HEADER_VALUE_CONTENT_TYPE,
      @Header(Const.HEADER_KEY_ACCEPT) accept: String = Const.HEADER_VALUE_ACCEPT
   ): JobsResponseEntity

}