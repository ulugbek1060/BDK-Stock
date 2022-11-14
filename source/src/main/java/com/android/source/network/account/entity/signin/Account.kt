package com.android.source.network.account.entity.signin

import com.android.source.network.job.entity.Job
import com.google.gson.annotations.SerializedName

data class Account(
   @SerializedName("address") val address: String,
   @SerializedName("first_name") val firstName: String,
   @SerializedName("id") val id: Long,
   @SerializedName("job") val job: Job,
   @SerializedName("last_name") val lastName: String,
   @SerializedName("phone_number") val phoneNumber: String
)