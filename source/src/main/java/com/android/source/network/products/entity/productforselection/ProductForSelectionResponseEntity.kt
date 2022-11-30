package com.android.source.network.products.entity.productforselection


import com.google.gson.annotations.SerializedName

data class ProductForSelectionResponseEntity(
   @SerializedName("data") val productsList: List<SimpleProduct>
)