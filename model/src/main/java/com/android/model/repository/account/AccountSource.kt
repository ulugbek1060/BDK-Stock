package com.android.model.repository.account

import com.android.model.repository.account.entity.AccountEntity
import com.android.model.repository.jobs.entity.JobEntity

interface AccountSource {

   /**
    * Execute sign-in request.
    * @throws ConnectionException
    * @throws BackendException
    * @throws ParseBackendResponseException
    * @return JWT-token
    */
   suspend fun signIn(phoneNumber: String, password: String): AccountEntity


}