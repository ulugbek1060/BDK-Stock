package com.android.source.network.products.entity.productlist

import com.android.source.network.products.entity.product.Product
import com.google.gson.annotations.SerializedName

data class ProductListData(
   @SerializedName("data") val productList: List<Product>
)
