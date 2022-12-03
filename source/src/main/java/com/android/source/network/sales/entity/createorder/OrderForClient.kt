package com.android.source.network.sales.entity.createorder

import com.android.model.repository.sales.entity.ClientForOrderEntity
import com.google.gson.annotations.SerializedName

data class OrderForClient(
   @SerializedName("first_name") val fullName: String,
   @SerializedName("last_name") val phoneNumber: String,
   @SerializedName("address") val address: String
) {
   fun toClientForEntity() = ClientForOrderEntity(
      fullName = fullName, phoneNumber = phoneNumber, address = address
   )
}
