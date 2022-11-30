package com.android.source.network.account.entity.userinfo

import com.google.gson.annotations.SerializedName

data class UserInfoResponseEntity(
   @SerializedName("data") val data: Account
)