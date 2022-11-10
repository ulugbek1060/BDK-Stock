package com.android.model.repository.account.entity

import com.android.model.database.account.entity.AccountRoomEntity

data class AccountEntity(
   val id: Long,
   val firstname: String,
   val lastname: String,
   val address: String,
   val jobTitle: String,
   val phoneNumber: String
) {

   var token: String? = null

   fun toUserRoomEntity(): AccountRoomEntity {
      return AccountRoomEntity(
         id = id,
         firstname = firstname,
         lastname = lastname,
         address = address,
         jobTitle = jobTitle,
         phoneNumber = phoneNumber
      )
   }
}