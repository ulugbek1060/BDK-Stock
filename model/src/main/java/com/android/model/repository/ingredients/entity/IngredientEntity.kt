package com.android.model.repository.ingredients.entity

data class IngredientEntity(
   val id: Long,
   val amount: String,
   val name: String,
   val unit: String,
   val createdAt: String? = null,
   val updatedAt: String? = null,
   val isDeleted: String? = null,
) : java.io.Serializable