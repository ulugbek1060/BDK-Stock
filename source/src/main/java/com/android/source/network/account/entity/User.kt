package com.android.source.network.account.entity

import com.google.gson.annotations.SerializedName

data class User(
   @SerializedName("address") val address: String,
   @SerializedName("first_name") val firstName: String,
   @SerializedName("id") val id: Long,
   @SerializedName("job") val jobTitle: String,
   @SerializedName("last_name") val lastName: String,
   @SerializedName("phone_number") val phoneNumber: String
)