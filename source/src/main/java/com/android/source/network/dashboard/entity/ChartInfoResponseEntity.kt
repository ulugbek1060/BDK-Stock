package com.android.source.network.dashboard.entity

import com.google.gson.annotations.SerializedName

data class ChartInfoResponseEntity(
   @SerializedName("aprel") val april: String,
   @SerializedName("avgust") val avgust: String,
   @SerializedName("dekabr") val december: String,
   @SerializedName("fevral") val february: String,
   @SerializedName("iyul") val june: String,
   @SerializedName("iyun") val july: String,
   @SerializedName("mart") val march: String,
   @SerializedName("may") val may: String,
   @SerializedName("noyabr") val november: String,
   @SerializedName("oktyabr") val october: String,
   @SerializedName("sentabr") val september: String,
   @SerializedName("yanvar") val january: String
)