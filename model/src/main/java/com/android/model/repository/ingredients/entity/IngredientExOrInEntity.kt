package com.android.model.repository.ingredients.entity

import com.google.gson.annotations.SerializedName

data class IngredientExOrInEntity(
   val id: Long,
   val name: String,
   val amount: String? = null,
   val comment: String? = null,
   val creator: String? = null,
   val status: String,
   val unit: String,
   val createdAt: String,
   val updatedAt: String,
)