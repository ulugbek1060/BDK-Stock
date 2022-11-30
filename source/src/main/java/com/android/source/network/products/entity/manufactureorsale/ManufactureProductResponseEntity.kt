package com.android.source.network.products.entity.manufactureorsale

import com.android.source.network.products.entity.product.ManufacturedOrExportedProduct
import com.google.gson.annotations.SerializedName

data class ManufactureProductResponseEntity(
   @SerializedName("message") val message: String,
   @SerializedName("data") val manufacturedProduct: ManufacturedOrExportedProduct
)