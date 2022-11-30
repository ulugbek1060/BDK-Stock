package com.android.source.network.products.entity.exportproduct

import com.android.source.network.products.entity.product.ManufacturedOrExportedProduct
import com.google.gson.annotations.SerializedName

data class ExportProductResponseEntity(
   @SerializedName("message") val message: String,
   @SerializedName("data") val exportedProduct: ManufacturedOrExportedProduct
)
