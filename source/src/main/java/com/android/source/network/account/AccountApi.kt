package com.android.source.network.account

import com.android.source.network.account.entity.logout.LogoutSuccessResponse
import com.android.source.network.account.entity.signin.SignInRequestEntity
import com.android.source.network.account.entity.signin.SignInResponseEntity
import com.android.source.network.account.entity.userinfo.UserInfoResponseEntity
import com.android.source.network.account.entity.userpermissions.PermissionOfJobTitleResponseEntity
import com.android.source.network.account.entity.userpermissions.UpdatePermsRequestEntity
import com.android.source.network.account.entity.userpermissions.UpdatePermsResponseEntity
import com.android.source.network.account.entity.userpermissions.UserPermissionsResponseEntity
import retrofit2.http.*

interface AccountApi {

   @Headers("Content-Type: application/json", "Accept: application/json")
   @POST("api/login")
   suspend fun signIn(@Body body: SignInRequestEntity): SignInResponseEntity

   @GET("api/user/info")
   suspend fun getCurrentUserInfo(): UserInfoResponseEntity

   @Headers("Content-Type: application/json", "Accept: application/json")
   @GET("api/user/logout")
   suspend fun logout(): LogoutSuccessResponse

   @Headers("Content-Type: application/json", "Accept: application/json")
   @GET("/api/permission/select")
   suspend fun getPermissions(): UserPermissionsResponseEntity

   @Headers("Content-Type: application/json", "Accept: application/json")
   @POST("api/job/role-perms")
   suspend fun updateUsersPermissions(
      @Body body: UpdatePermsRequestEntity
   ): UpdatePermsResponseEntity

   @Headers("Content-Type: application/json", "Accept: application/json")
   @GET("/api/job/permission/{jobId}")
   suspend fun getPermissionsOfJobTitle(
      @Path("jobId") jobId: Int
   ): PermissionOfJobTitleResponseEntity

   @Headers("Content-Type: application/json", "Accept: application/json")
   @GET("/api/user/perms")
   suspend fun getUserPermissions(): List<String>

}