package com.android.model.database.account.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.android.model.repository.account.entity.AccountEntity
import com.android.model.repository.jobs.entity.JobEntity

@Entity(tableName = "account")
data class AccountRoomEntity(
   @PrimaryKey val id: Long,
   val firstname: String,
   val lastname: String,
   val address: String,
   val job: AccountJobRoom,
   val phoneNumber: String
) {
   fun toAccountEntity() = AccountEntity(
      id = id,
      firstname = firstname,
      lastname = lastname,
      address = address,
      job = JobEntity(
         id = job.id,
         name = job.name
      ),
      phoneNumber = phoneNumber
   )
}