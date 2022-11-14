package com.android.source.network.account.entity.signin

import com.google.gson.annotations.SerializedName

data class Token(
   @SerializedName("acces_token") val accessToken: String,
   @SerializedName("type") val type: String
)