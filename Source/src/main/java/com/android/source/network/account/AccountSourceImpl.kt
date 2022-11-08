package com.android.source.network.account

import com.android.model.source.account.AccountSource
import com.android.model.source.account.entity.UserEntity
import com.android.source.network.account.entity.SignInRequestEntity
import com.android.source.network.base.BaseNetworkSource
import javax.inject.Inject

class AccountSourceImpl @Inject constructor(
   private val accountApi: AccountApi
) : BaseNetworkSource(), AccountSource {

   override suspend fun signIn(phoneNumber: String, password: String): UserEntity =
      wrapNetworkException {
         val signInRequestEntity = SignInRequestEntity(phoneNumber, password)
         val response = accountApi.signIn(signInRequestEntity)
         UserEntity(
            id = response.user.id,
            firstname = response.user.firstName,
            lastname = response.user.lastName,
            jobTitle = response.user.jobTitle,
            address = response.user.address,
            phoneNumber = response.token.accessToken,
            token = response.token.accessToken
         )
      }
}