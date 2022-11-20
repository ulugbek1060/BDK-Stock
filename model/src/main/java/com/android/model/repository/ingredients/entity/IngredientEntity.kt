package com.android.model.repository.ingredients.entity

import com.google.gson.annotations.SerializedName

data class IngredientEntity(
   val id: Long,
   val amount: String,
   val name: String,
   val unit: String,
   val createdAt: String? = null,
   val updatedAt: String? = null,
   val isDeleted: String? = null,
)