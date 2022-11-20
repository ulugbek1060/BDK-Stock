package com.android.source.network.ingredients.entity.createingredient


import com.android.source.network.ingredients.entity.ingredientslist.Ingredient
import com.google.gson.annotations.SerializedName

data class CreateIngredientResponseEntity(
   @SerializedName("data")
    val `data`: Ingredient,
   @SerializedName("ok")
    val ok: Boolean // true
)