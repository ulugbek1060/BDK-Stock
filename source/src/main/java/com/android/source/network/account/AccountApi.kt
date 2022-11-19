package com.android.source.network.account

import com.android.model.utils.Const.HEADER_KEY_ACCEPT
import com.android.model.utils.Const.HEADER_KEY_CONTENT_TYPE
import com.android.model.utils.Const.HEADER_VALUE_ACCEPT
import com.android.model.utils.Const.HEADER_VALUE_CONTENT_TYPE
import com.android.source.network.account.entity.logout.LogoutSuccessResponse
import com.android.source.network.account.entity.signin.SignInRequestEntity
import com.android.source.network.account.entity.signin.SignInResponseEntity
import retrofit2.http.*

interface AccountApi {

   @Headers("Content-Type: application/json", "Accept: application/json")
   @POST("api/login")
   suspend fun signIn(@Body body: SignInRequestEntity): SignInResponseEntity

   @Headers("Content-Type: application/json", "Accept: application/json")
   @GET("api/user/logout")
   suspend fun logout(): LogoutSuccessResponse
}