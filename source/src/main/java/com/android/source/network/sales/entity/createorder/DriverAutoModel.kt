package com.android.source.network.sales.entity.createorder

import com.google.gson.annotations.SerializedName

data class DriverAutoModel(
   @SerializedName("id") val id: Int,
   @SerializedName("name") val name: String
)