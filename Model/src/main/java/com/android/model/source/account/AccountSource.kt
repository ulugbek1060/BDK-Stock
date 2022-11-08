package com.android.model.source.account

import com.android.model.source.account.entity.UserEntity

interface AccountSource {

   /**
    * Execute sign-in request.
    * @throws ConnectionException
    * @throws BackendException
    * @throws ParseBackendResponseException
    * @return JWT-token
    */
   suspend fun signIn(phoneNumber: String, password: String): UserEntity

}