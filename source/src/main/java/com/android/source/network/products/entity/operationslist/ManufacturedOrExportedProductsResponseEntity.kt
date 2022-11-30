package com.android.source.network.products.entity.operationslist

import com.android.source.network.products.entity.product.ManufacturedOrExportedProduct
import com.google.gson.annotations.SerializedName

data class ManufacturedOrExportedProductsResponseEntity(
  @SerializedName("data") val list: List<ManufacturedOrExportedProduct>
)