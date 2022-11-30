package com.android.source.network.account.entity.signin

import com.google.gson.annotations.SerializedName

data class SignInResponseEntity(
   @SerializedName("token_type") val tokenType: String,
   @SerializedName("expires_in") val expiresIn: Long,
   @SerializedName("access_token") val accessToken: String,
   @SerializedName("refresh_token") val refreshToken: String
)