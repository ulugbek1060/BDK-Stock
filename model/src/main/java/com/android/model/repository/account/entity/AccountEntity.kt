package com.android.model.repository.account.entity

import com.android.model.database.account.entity.AccountJobRoom
import com.android.model.database.account.entity.AccountRoomEntity
import com.android.model.repository.jobs.entity.JobEntity

data class AccountEntity(
   val id: Long,
   val firstname: String?,
   val lastname: String?,
   val address: String?,
   val job: JobEntity?,
   val phoneNumber: String?
) {

   var token: String? = null

   fun toUserRoomEntity(): AccountRoomEntity {
      val jobEntity = AccountJobRoom(
         id = job?.id ?: 0,
         name = job?.name ?: "Undefined error with job name"
      )
      return AccountRoomEntity(
         id = id,
         firstname = firstname ?: "Undefined error with firstname",
         lastname = lastname ?: "Undefined error with lastname",
         address = address ?: "Undefined error with address",
         job = jobEntity,
         phoneNumber = phoneNumber ?: "Undefined error with phoneNumber"
      )
   }
}