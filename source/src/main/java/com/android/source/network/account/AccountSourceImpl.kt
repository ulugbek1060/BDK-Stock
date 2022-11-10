package com.android.source.network.account

import com.android.model.repository.account.AccountSource
import com.android.model.repository.account.entity.AccountEntity
import com.android.source.network.account.entity.SignInRequestEntity
import com.android.source.network.base.BaseNetworkSource
import javax.inject.Inject

class AccountSourceImpl @Inject constructor(
   private val accountApi: AccountApi
) : BaseNetworkSource(), AccountSource {

   override suspend fun signIn(phoneNumber: String, password: String): AccountEntity =
      wrapNetworkException {
         val signInRequestEntity = SignInRequestEntity(phoneNumber, password)
         val response = accountApi.signIn(signInRequestEntity)
         val account = AccountEntity(
            id = response.user.id,
            firstname = response.user.firstName,
            lastname = response.user.lastName,
            jobTitle = response.user.jobTitle,
            address = response.user.address,
            phoneNumber = response.user.phoneNumber
         )
         account.token = response.token.accessToken
         account
      }
}