package com.android.source.network.job

import com.android.model.repository.jobs.JobSource
import com.android.model.repository.jobs.entity.JobEntity
import com.android.source.network.base.BaseNetworkSource
import javax.inject.Inject

class JobSourceImpl @Inject constructor(
   private val jobApi: JobApi
) : BaseNetworkSource(), JobSource {

   override suspend fun getJobs(): List<JobEntity> = wrapNetworkException {
      jobApi.getJobs().jobs.listJobs.map { it.toJobEntity() }
   }
}