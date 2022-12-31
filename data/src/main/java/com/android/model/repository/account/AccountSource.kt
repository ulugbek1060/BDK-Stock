package com.android.model.repository.account

import com.android.model.repository.account.entity.AccountEntity
import com.android.model.repository.account.entity.PermsWithJobIdEntity
import com.android.model.repository.account.entity.Token
import com.android.model.repository.account.entity.UserPermissionEntity

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

   fun getUserInfo(): AccountEntity

   /**
    * Execute sign-in request.
    * @throws ConnectionException
    * @throws BackendException
    * @throws ParseBackendResponseException
    */
   suspend fun logout(): String

   /**
    * Fetches all permissions
    */
   suspend fun getPermissions(): List<UserPermissionEntity>

   /**
    * Update permission for job title
    */
   suspend fun updateUserPermissions(
      jobId: Int, permissions: List<Int>
   ): String

   /**
    * Fetches job's permissions
    */
   suspend fun getPermissionsOfJobTitle(
      jobId:Int
   ):PermsWithJobIdEntity
}