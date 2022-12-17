package com.android.source.network.products.entity.operationslist

import com.google.gson.annotations.SerializedName

data class ManufacturedOrExportedProductsResponseEntity(
   @SerializedName("data") val operations: ProductOperations
)