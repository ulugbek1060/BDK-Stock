package com.android.model.repository.ingredients.entity

data class IngredientExOrInEntity(
   val id: Long,
   val name: String,
   val amount: String? = null,
   val comment: String? = null,
   val creator: String? = null,
   val status: Int,
   val unit: String,
   val createdAt: String,
   val updatedAt: String,
) : java.io.Serializable