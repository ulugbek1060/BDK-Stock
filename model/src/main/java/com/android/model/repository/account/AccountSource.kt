package com.android.model.repository.account

import com.android.model.repository.account.entity.AccountEntity

interface AccountSource {

   /**
    * Execute sign-in request.
    * @throws ConnectionException
    * @throws BackendException
    * @throws ParseBackendResponseException
    * @return JWT-token
    */
   suspend fun signIn(phoneNumber: String, password: String): AccountEntity

   /**
    * Execute sign-in request.
    * @throws ConnectionException
    * @throws BackendException
    * @throws ParseBackendResponseException
    * @return JWT-token
    */
   suspend fun logout(): String


}