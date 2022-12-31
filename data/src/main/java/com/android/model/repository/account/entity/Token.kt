package com.android.model.repository.account.entity

data class Token(
   val accessToken: String,
   val refreshToken: String,
   val expireDate: Long
)