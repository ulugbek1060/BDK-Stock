package com.android.model.repository.account.entity

import com.android.model.database.account.entity.AccountJobRoom
import com.android.model.database.account.entity.AccountRoomEntity
import com.android.model.repository.jobs.entity.JobEntity

data class AccountEntity(
   val id: Long,
   val firstname: String,
   val lastname: String,
   val address: String,
   val job: JobEntity,
   val phoneNumber: String
) {

   var token: String? = null

   fun toUserRoomEntity(): AccountRoomEntity {
      return AccountRoomEntity(
         id = id,
         firstname = firstname,
         lastname = lastname,
         address = address,
         job = AccountJobRoom(
            id = job.id,
            name = job.name
         ),
         phoneNumber = phoneNumber
      )
   }
}