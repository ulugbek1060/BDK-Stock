package com.android.model.repository.products.entity

data class ProductEntity(
   val id: Long,
   val name: String,
   val price: String,
   val unit: String,
   val amount: String,
   val createdAt: String,
   val updatedAt: String
)