package com.android.source.network.account

import com.android.model.repository.account.AccountSource
import com.android.model.repository.account.entity.AccountEntity
import com.android.model.repository.jobs.entity.JobEntity
import com.android.source.network.account.entity.signin.SignInRequestEntity
import com.android.source.network.base.BaseNetworkSource
import javax.inject.Inject

class AccountSourceImpl @Inject constructor(
   private val accountApi: AccountApi
) : BaseNetworkSource(), AccountSource {

   private val TAG = this.javaClass.simpleName

   override suspend fun signIn(phoneNumber: String, password: String): AccountEntity =
      wrapNetworkException {
         val signInRequestEntity = SignInRequestEntity(phoneNumber, password)
         val response = accountApi.signIn(signInRequestEntity)

         val jobId = response.user.job.id
         val jobName = response.user.job.name

         val account = AccountEntity(
            id = response.user.id,
            firstname = response.user.firstName,
            lastname = response.user.lastName,
            job = JobEntity(
               id = jobId,
               name = jobName
            ),
            address = response.user.address,
            phoneNumber = response.user.phoneNumber
         )
         account.token = response.token.accessToken
         account
      }

   override suspend fun logout(): String = wrapNetworkException {
      accountApi.logout().success
   }
}