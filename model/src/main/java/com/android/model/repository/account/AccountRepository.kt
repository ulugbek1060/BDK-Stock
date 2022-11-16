package com.android.model.repository.account

import com.android.model.database.account.AccountDao
import com.android.model.database.employees.EmployeesDao
import com.android.model.repository.account.entity.AccountEntity
import com.android.model.repository.base.BaseRepository
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
   private val accountDao: AccountDao,
   private val employeeDao: EmployeesDao
) : BaseRepository() {

   private val TAG = this.javaClass.simpleName

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
      try {
         val accountEntity = accountSource.signIn(phoneNumber, password)

         appSettings.setCurrentToken(accountEntity.token)

         // save user data to database
         accountDao.insert(accountEntity.toUserRoomEntity())

      } catch (e: Exception) {
         throw if (e is BackendException && e.code == 401) {
            AuthException(e)
         } else {
            throw e
         }
      }
   }

   /**
    * Get the account info of the current signed-in user.
    */
   fun getAccount(): Flow<AccountEntity?> {
      return accountDao.getAccount().map { it?.toAccountEntity() }
   }

   suspend fun logoutManually(): String = accountSource.logout()

   /**
    * function supports only code 401 from backend & null token
    */
   suspend fun logout() {
      appSettings.setCurrentToken(null)
      accountDao.delete()
      employeeDao.clear()
   }
}