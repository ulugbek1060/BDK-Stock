package com.android.model.source.account.entity

data class UserEntity(
   val id: Long,
   val firstname: String,
   val lastname: String,
   val address: String,
   val jobTitle: String,
   val phoneNumber: String,
   val token: String
)