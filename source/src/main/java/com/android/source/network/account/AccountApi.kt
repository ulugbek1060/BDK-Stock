package com.android.source.network.account

import com.android.source.network.account.entity.logout.LogoutSuccessResponse
import com.android.source.network.account.entity.signin.SignInRequestEntity
import com.android.source.network.account.entity.signin.SignInResponseEntity
import com.android.source.network.account.entity.userinfo.UserInfoResponseEntity
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface AccountApi {

   @Headers("Content-Type: application/json", "Accept: application/json")
   @POST("api/login")
   suspend fun signIn(@Body body: SignInRequestEntity): SignInResponseEntity

   @GET("api/user/info")
   suspend fun getCurrentUserInfo(): UserInfoResponseEntity

   @Headers("Content-Type: application/json", "Accept: application/json")
   @GET("api/user/logout")
   suspend fun logout(): LogoutSuccessResponse
}