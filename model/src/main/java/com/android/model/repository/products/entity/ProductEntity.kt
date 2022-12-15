package com.android.model.repository.products.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductEntity(
   val id: Long,
   val name: String,
   val price: String,
   val unit: String,
   val amount: String,
   val createdAt: String,
   val updatedAt: String
) : Parcelable {
   fun toProductSelectionItem() = ProductSelectionItem(
      id = id,
      name = name,
      unit = unit,
      price = price
   )
}