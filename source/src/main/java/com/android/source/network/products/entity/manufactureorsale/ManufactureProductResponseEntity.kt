package com.android.source.network.products.entity.manufactureorsale

import com.android.source.network.products.entity.product.ManufacturedOrExportedProduct

data class ManufactureProductResponseEntity(
   val message: String,
   val manufacturedProduct: ManufacturedOrExportedProduct
)