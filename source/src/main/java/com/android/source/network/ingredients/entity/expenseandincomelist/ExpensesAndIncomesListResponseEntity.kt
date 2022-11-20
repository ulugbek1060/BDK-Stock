package com.android.source.network.ingredients.entity.expenseandincomelist

import com.google.gson.annotations.SerializedName

data class ExpensesAndIncomesListResponseEntity(
   @SerializedName("ok") val ok: Boolean,
   @SerializedName("data") val expansesAndIncomesList: List<ExOrInIngredient>
)