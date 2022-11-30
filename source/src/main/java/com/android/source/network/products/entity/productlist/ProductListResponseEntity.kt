package com.android.source.network.products.entity.productlist

import com.google.gson.annotations.SerializedName

class ProductListResponseEntity(
   @SerializedName("message") val message: String,
   @SerializedName("data") val productListData: ProductListData
)