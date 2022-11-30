package com.android.source.network.products.entity.createproduct

import com.android.source.network.products.entity.product.IngredientOfProduct

data class ProductCreateRequestEntity(
   val name: String,
   val unit: String,
   val price: String,
   val materials: List<IngredientOfProduct>
)