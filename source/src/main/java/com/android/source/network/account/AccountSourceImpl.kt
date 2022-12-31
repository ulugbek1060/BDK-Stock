package com.android.source.network.account

import com.android.model.repository.account.AccountSource
import com.android.model.repository.account.entity.AccountEntity
import com.android.model.repository.account.entity.PermsWithJobIdEntity
import com.android.model.repository.account.entity.Token
import com.android.model.repository.account.entity.UserPermissionEntity
import com.android.model.repository.jobs.entity.JobEntity
import com.android.source.network.account.entity.signin.SignInRequestEntity
import com.android.source.network.account.entity.userpermissions.UpdatePermsRequestEntity
import com.android.source.network.base.BaseNetworkSource
import javax.inject.Inject

class AccountSourceImpl @Inject constructor(
   private val accountApi: AccountApi
) : BaseNetworkSource(), AccountSource {

   override suspend fun signIn(phoneNumber: String, password: String) = wrapNetworkException {
      val signInRequestEntity = SignInRequestEntity(phoneNumber, password)
      val response = accountApi.signIn(signInRequestEntity)
      Token(
         accessToken = response.accessToken,
         refreshToken = response.refreshToken,
         expireDate = response.expiresIn
      )
   }

   override suspend fun getUserInfo(): AccountEntity = wrapNetworkException {
      val userInfo = accountApi.getCurrentUserInfo().data
      AccountEntity(
         id = userInfo.id,
         firstname = userInfo.firstName,
         lastname = userInfo.lastName,
         address = userInfo.address,
         job = JobEntity(userInfo.job.id.toInt(), userInfo.job.name),
         phoneNumber = userInfo.phoneNumber
      )
   }

   override suspend fun logout(): String = wrapNetworkException {
      accountApi.logout().success
   }

   override suspend fun getPermissions(): List<UserPermissionEntity> = wrapNetworkException {
      accountApi.getPermissions().permissions.map {
         UserPermissionEntity(
            id = it.id, name = it.name
         )
      }
   }

   override suspend fun updateUserPermissions(jobId: Int, permissions: List<Int>): String =
      wrapNetworkException {
         val body = UpdatePermsRequestEntity(
            jobId = jobId, permissions = permissions
         )
         accountApi.updateUsersPermissions(body = body).message
      }

   override suspend fun getPermissionsOfJobTitle(jobId: Int): PermsWithJobIdEntity =
      wrapNetworkException {
         val permsOfJobTitle = accountApi.getPermissionsOfJobTitle(jobId = jobId)
         PermsWithJobIdEntity(
            jobId = jobId, permissions = permsOfJobTitle.permissions.map {
               UserPermissionEntity(
                  id = it.id,
                  name = it.name
               )
            }.toSet()
         )
      }
}