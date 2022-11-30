package com.android.source.network.products.entity.getingredientsofproduct


import com.google.gson.annotations.SerializedName

data class IngredientsOfProductResponseEntity(
   @SerializedName("data") val ingredients: List<Data>
)