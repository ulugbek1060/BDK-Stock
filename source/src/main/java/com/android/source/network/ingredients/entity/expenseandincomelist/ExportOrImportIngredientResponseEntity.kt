package com.android.source.network.ingredients.entity.expenseandincomelist

import com.google.gson.annotations.SerializedName

data class ExportOrImportIngredientResponseEntity(
   @SerializedName("ok") val ok: Boolean,
   @SerializedName("data") val operations: IngredientOperations
)