package com.android.model.repository.account

import com.android.model.repository.account.entity.AccountEntity
import com.android.model.repository.account.entity.Token

interface AccountSource {

   /**
    * Execute sign-in request.
    * @throws ConnectionException
    * @throws BackendException
    * @throws ParseBackendResponseException
    */
   suspend fun signIn(phoneNumber: String, password: String): Token

   /**
    * Fetches user info from local
    * no arguments
    *
    * @throws ConnectionException
    * @throws BackendException
    * @throws ParseBackendResponseException
    */
   suspend

   fun getUSerInfo(): AccountEntity

   /**
    * Execute sign-in request.
    * @throws ConnectionException
    * @throws BackendException
    * @throws ParseBackendResponseException
    */
   suspend fun logout(): String

}