package com.android.source.network.ingredients.entity.ingredientslist


import com.google.gson.annotations.SerializedName

data class Ingredient(
   @SerializedName("amount")
   val amount: String, // 0
   @SerializedName("id")
   val id: Long, // 3
   @SerializedName("name")
   val name: String, // KOST
   @SerializedName("unit")
   val unit: String, // kg
   @SerializedName("created_at")
   val createdAt: String? = null, // 2022-11-20T05:47:21.000000Z
   @SerializedName("updated_at")
   val updatedAt: String? = null, // 2022-11-20T05:47:21.000000Z
   @SerializedName("is_delete")
   val isDeleted: String? = null,
)