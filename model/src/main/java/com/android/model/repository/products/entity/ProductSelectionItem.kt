package com.android.model.repository.products.entity

data class ProductSelectionItem(
   val id: Long,
   val name: String,
   val unit: String,
   val price: String
){
   override fun toString(): String {
      return name
   }
}