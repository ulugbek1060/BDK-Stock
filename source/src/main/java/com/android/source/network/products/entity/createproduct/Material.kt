package com.android.source.network.products.entity.createproduct


import com.google.gson.annotations.SerializedName

data class Material(
   @SerializedName("amount")
   val amount: String, // 32.378
   @SerializedName("id")
   val id: Int // pariatur
)