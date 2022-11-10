package com.android.source.network.account.entity

import com.google.gson.annotations.SerializedName

data class SignInResponseEntity(
   @SerializedName("token") val token: Token,
   @SerializedName("user") val user: User
)