package com.android.source.network.dashboard.entity

import com.google.gson.annotations.SerializedName

data class GetPaysResponseEntity(
   @SerializedName("pcard") val totalSumInCard: String,
   @SerializedName("pcash") val totalSumInCash: String,
   @SerializedName("rcard") val expenditureInCard: String,
   @SerializedName("rcash") val expenditureInCash: String
)