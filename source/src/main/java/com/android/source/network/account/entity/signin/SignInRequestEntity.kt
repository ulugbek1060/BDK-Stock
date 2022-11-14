package com.android.source.network.account.entity.signin

import com.google.gson.annotations.SerializedName

data class SignInRequestEntity(
   @SerializedName("phone_number") val phoneNumber: String,
   @SerializedName("password") val password: String
)