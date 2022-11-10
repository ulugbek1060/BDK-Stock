package com.android.model.repository.account

import com.android.model.database.account.AccountDao
import com.android.model.repository.Repository
import com.android.model.repository.account.entity.AccountEntity
import com.android.model.repository.settings.AppSettings
import com.android.model.utils.AuthException
import com.android.model.utils.BackendException
import com.android.model.utils.EmptyFieldException
import com.android.model.utils.Field
import com.google.gson.JsonParseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AccountRepository @Inject constructor(
   private val accountSource: AccountSource,
   private val appSettings: AppSettings,
   private val accountDao: AccountDao
) : Repository {

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

      // save user data to database
      accountDao.insert(userEntity.toUserRoomEntity())
   }

   /**
    * Get the account info of the current signed-in user.
    */
   fun getAccount(): Flow<AccountEntity?> {
      return accountDao.getAccount().map { it?.toAccountEntity() }
   }

   suspend fun logout() {
      appSettings.setCurrentToken(null)
      accountDao.delete()
   }
}