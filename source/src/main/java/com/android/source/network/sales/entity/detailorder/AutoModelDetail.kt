package com.android.source.network.sales.entity.detailorder

import com.google.gson.annotations.SerializedName

data class AutoModelDetail(
   @SerializedName("id") val id: Int,
   @SerializedName("name") val name: String
)
