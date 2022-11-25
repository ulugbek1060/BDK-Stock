package com.android.source.network.products.entity.createproduct


import com.google.gson.annotations.SerializedName

data class CreateProductResponseEntity(
   @SerializedName("data")
   val product: CreateProductData,
   @SerializedName("message")
   val message: String // product created
)