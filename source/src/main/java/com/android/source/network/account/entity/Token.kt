package com.android.source.network.account.entity

import com.google.gson.annotations.SerializedName

data class Token(
   @SerializedName("acces_token") val accessToken: String,
   @SerializedName("type") val type: String
)