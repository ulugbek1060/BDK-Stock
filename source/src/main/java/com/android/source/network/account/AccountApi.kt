package com.android.source.network.account

import com.android.model.utils.Const.HEADER_KEY_ACCEPT
import com.android.model.utils.Const.HEADER_KEY_CONTENT_TYPE
import com.android.model.utils.Const.HEADER_VALUE_ACCEPT
import com.android.model.utils.Const.HEADER_VALUE_CONTENT_TYPE
import com.android.source.network.account.entity.logout.LogoutSuccessResponse
import com.android.source.network.account.entity.signin.SignInRequestEntity
import com.android.source.network.account.entity.signin.SignInResponseEntity
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AccountApi {

   @POST("api/login")
   suspend fun signIn(
      @Body body: SignInRequestEntity,
      @Header(HEADER_KEY_CONTENT_TYPE) type: String = HEADER_VALUE_CONTENT_TYPE,
      @Header(HEADER_KEY_ACCEPT) accept: String = HEADER_VALUE_ACCEPT
   ): SignInResponseEntity

   @GET("api/user/logout")
   suspend fun logout(
      @Header(HEADER_KEY_CONTENT_TYPE) type: String = HEADER_VALUE_CONTENT_TYPE,
      @Header(HEADER_KEY_ACCEPT) accept: String = HEADER_VALUE_ACCEPT
   ): LogoutSuccessResponse


}