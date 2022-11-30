package com.android.source.network.products.entity.productforselection


import com.google.gson.annotations.SerializedName

data class SimpleProduct(
   @SerializedName("id")
   val id: Int, // 14
   @SerializedName("name")
   val name: String, // Updated product from ulugbek
   @SerializedName("price")
   val price: String, // 4100.00
   @SerializedName("unit")
   val unit: String // kg
)