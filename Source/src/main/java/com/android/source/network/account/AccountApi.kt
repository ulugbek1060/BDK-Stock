package com.android.source.network.account

import com.android.model.utils.Const.HEADER_KEY_ACCEPT
import com.android.model.utils.Const.HEADER_KEY_CONTENT_TYPE
import com.android.model.utils.Const.HEADER_VALUE_ACCEPT
import com.android.model.utils.Const.HEADER_VALUE_CONTENT_TYPE
import com.android.source.network.account.entity.SignInRequestEntity
import com.android.source.network.account.entity.SignInResponseEntity
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AccountApi {

   @POST("api/login")
   suspend fun signIn(
      @Body body: SignInRequestEntity,
      @Header(HEADER_KEY_CONTENT_TYPE) type: String = HEADER_VALUE_CONTENT_TYPE,
      @Header(HEADER_KEY_ACCEPT) accept: String = HEADER_VALUE_ACCEPT
   ): SignInResponseEntity

}