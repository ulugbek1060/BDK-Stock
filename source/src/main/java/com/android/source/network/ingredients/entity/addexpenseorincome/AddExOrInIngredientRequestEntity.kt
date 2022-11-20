package com.android.source.network.ingredients.entity.addexpenseorincome


import com.google.gson.annotations.SerializedName

data class AddExOrInIngredientRequestEntity(
   @SerializedName("amount")
   val amount: String, // 1200
   @SerializedName("comment")
   val comment: String,
   @SerializedName("material_id")
   val materialId: Int, // 3
   @SerializedName("status")
   val status: Int // 0
)