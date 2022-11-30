package com.android.model.repository.products.entity

// manufactured or exported product operations
// status maight be 0 or 1 0 -> manufactured product 1 -> exported product
data class ProductOperationEntity(
   val id: Long,
   val name: String,
   val amount: String,
   val comment: String,
   val unit: String,
   val status: Int,
   val createdAt: String
)
