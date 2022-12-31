package com.android.model.repository.jobs

import com.android.model.repository.jobs.entity.JobEntity

interface JobSource {

   /**
    * Execute sign-in request.
    * @throws ConnectionException
    * @throws BackendException
    * @throws ParseBackendResponseException
    * @return JWT-token
    */
   suspend fun getJobs(): List<JobEntity>

}