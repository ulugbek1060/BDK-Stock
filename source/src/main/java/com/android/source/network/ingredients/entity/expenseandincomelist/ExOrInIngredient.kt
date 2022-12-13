package com.android.source.network.ingredients.entity.expenseandincomelist


import com.google.gson.annotations.SerializedName

data class ExOrInIngredient(
   @SerializedName("amount")
   val amount: String?, // 1200.00
   @SerializedName("comment")
   val comment: String?,
   @SerializedName("id")
   val id: Long, // 11
   @SerializedName("name")
   val name: String, // KOST
   @SerializedName("status")
   val status: Int, // 0
   @SerializedName("unit")
   val unit: String, // kg
   @SerializedName("user_id")
   val userId: String?, // Admin
   @SerializedName("created_at")
   val createdAt: String, // 2022-11-20T10:19:25.000000Z
   @SerializedName("updated_at")
   val updatedAt: String, // 2022-11-20T10:19:25.000000Z
)