package com.android.model.source.account

import com.android.model.models.*
import com.android.model.source.settings.AppSettings
import com.google.gson.JsonParseException
import javax.inject.Inject

class AccountRepository @Inject constructor(
   private val accountSource: AccountSource,
   private val appSettings: AppSettings
) {

   fun isSignedIn(): Boolean {
      // user is signed-in if auth token exists
      return appSettings.getCurrentToken() != null
   }

   /**
    * Try to sign-in with the email and password.
    * @throws EmptyFieldException
    * @throws InvalidCredentialsException
    * @throws ConnectionException
    * @throws BackendException
    * @throws ParseBackendResponseException
    * @throws JsonParseException
    */
   suspend fun signIn(phoneNumber: String, password: String) {

      if (phoneNumber.isBlank()) throw EmptyFieldException(Field.PhoneNumber)
      if (password.isBlank()) throw EmptyFieldException(Field.Password)

      // if there is error throws
      val userEntity = try {
         accountSource.signIn(phoneNumber, password)
      } catch (e: Exception) {
         if (e is BackendException && e.code == 401) {
            throw AuthException(e)
         } else {
            throw e
         }
      }
      appSettings.setCurrentToken(userEntity.token)
   }

   fun logout() {
      appSettings.setCurrentToken(null)
   }
}