package com.android.model.repository.account

import com.android.model.database.account.AccountDao
import com.android.model.database.employees.EmployeesDao
import com.android.model.database.jobs.JobsDao
import com.android.model.database.vehicles.VehiclesDao
import com.android.model.di.IoDispatcher
import com.android.model.repository.account.entity.AccountEntity
import com.android.model.repository.account.entity.PermsWithJobIdEntity
import com.android.model.repository.account.entity.UserPermissionEntity
import com.android.model.repository.base.BaseRepository
import com.android.model.repository.settings.AppSettings
import com.android.model.repository.settings.UserPermissions
import com.android.model.utils.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AccountRepository @Inject constructor(
   private val accountSource: AccountSource,
   private val appSettings: AppSettings,
   private val userPermissions: UserPermissions,
   private val accountDao: AccountDao,
   private val employeeDao: EmployeesDao,
   private val jobsDao: JobsDao,
   private val vehiclesDao: VehiclesDao,
   @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseRepository() {

   fun isSignedIn(): Boolean {
      return appSettings.getCurrentToken() != null
   }

   suspend fun signIn(phoneNumber: String, password: String) {

      if (phoneNumber.isBlank()) throw EmptyFieldException(Field.PHONE_NUMBER)
      if (password.isBlank()) throw EmptyFieldException(Field.PASSWORD)

      wrapBackendExceptions {
         val accountEntity = accountSource.signIn(phoneNumber, password)

         appSettings.setCurrentToken(accountEntity.accessToken)

         saveUserInf()
      }
   }

   private suspend fun saveUserInf() {
      val account = accountSource.getUserInfo()
      accountDao.insert(account.toUserRoomEntity())
   }


   fun getAccount(): Flow<AccountEntity?> {
      return accountDao.getAccount().map { it?.toAccountEntity() }
   }


   fun getPermissions(): Flow<Results<List<UserPermissionEntity>>> = flow {
      emit(wrapBackendExceptions { accountSource.getPermissions() })
   }
      .flowOn(ioDispatcher)
      .asResult()


   suspend fun updateUsersPermissions(
      jobId: Int?,
      permissions: List<Int>
   ): String {
      if (jobId == null) throw EmptyFieldException(Field.JOB)

      return wrapBackendExceptions {
         accountSource.updateUserPermissions(
            jobId = jobId,
            permissions = permissions
         )
      }
   }


   fun getPermissionOfJobTitle(jobId: Int): Flow<Results<PermsWithJobIdEntity>> =
      flow {
         emit(wrapBackendExceptions {
            accountSource.getPermissionsOfJobTitle(jobId = jobId)
         })
      }
         .flowOn(ioDispatcher)
         .asResult()


   fun getUserPermissions(): Flow<Results<List<String>>> = flow {
      val data = try {
         wrapBackendExceptions {
            val list = accountSource.getUserPermissions()
            userPermissions.savePermissions(list)
            list
         }
      } catch (e: AuthException) {
         throw e
      } catch (e: Exception) {
         userPermissions.getPermissions()
      }
      emit(data)
   }
      .flowOn(ioDispatcher)
      .asResult()


   suspend fun logoutManually(): String = accountSource.logout()


   suspend fun logout() {
      appSettings.setCurrentToken(null)
      accountDao.delete()
      employeeDao.clear()
      jobsDao.clear()
      vehiclesDao.clear()
   }
}