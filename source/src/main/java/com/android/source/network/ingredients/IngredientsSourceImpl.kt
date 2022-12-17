package com.android.source.network.ingredients

import com.android.model.repository.ingredients.IngredientsSource
import com.android.model.repository.ingredients.entity.IngredientEntity
import com.android.model.repository.ingredients.entity.IngredientExOrInEntity
import com.android.model.repository.ingredients.entity.SimpleIngredient
import com.android.source.network.base.BaseNetworkSource
import com.android.source.network.ingredients.entity.addexpenseorincome.AddExOrInIngredientRequestEntity
import com.android.source.network.ingredients.entity.createingredient.CreateIngredientRequestEntity
import javax.inject.Inject

class IngredientsSourceImpl @Inject constructor(
   private val ingredientsApi: IngredientsApi
) : BaseNetworkSource(), IngredientsSource {

   override suspend fun createIngredients(ingredientName: String, unit: String): IngredientEntity =
      wrapNetworkException {
         val body = CreateIngredientRequestEntity(
            name = ingredientName,
            unit = unit
         )
         val ingredient = ingredientsApi.createIngredients(body).data
         IngredientEntity(
            id = ingredient.id,
            amount = ingredient.amount,
            name = ingredient.name,
            unit = ingredient.unit,
            createdAt = ingredient.createdAt,
            updatedAt = ingredient.updatedAt,
            isDeleted = ingredient.isDeleted
         )
      }

   override suspend fun getIngredients(
      query: String?,
      pageIndex: Int,
      pageSize: Int,
   ): List<IngredientEntity> =
      wrapNetworkException {
         ingredientsApi.getIngredients(
            query = query,
            pageIndex = pageIndex,
            pageSize = pageSize
         ).data.list.map { ingredient ->
            IngredientEntity(
               id = ingredient.id,
               amount = ingredient.amount,
               name = ingredient.name,
               unit = ingredient.unit,
               createdAt = ingredient.createdAt,
               updatedAt = ingredient.updatedAt,
               isDeleted = ingredient.isDeleted
            )
         }
      }

   override suspend fun addExpensesAndIncomesOfIngredient(
      ingredientId: Long,
      amount: String,
      statusOfActions: Int,
      comments: String
   ): String = wrapNetworkException {
      val body = AddExOrInIngredientRequestEntity(
         materialId = ingredientId,
         amount = amount,
         comment = comments,
         status = statusOfActions
      )
      val ingredientAction = ingredientsApi.addExpenseAndIncomeIngredient(body).message
//      IngredientExOrInEntity(
//         id = ingredientAction.id,
//         name = ingredientAction.name,
//         amount = ingredientAction.amount,
//         comment = ingredientAction.comment,
//         creator = ingredientAction.userId,
//         status = ingredientAction.status,
//         unit = ingredientAction.unit,
//         createdAt = ingredientAction.createdAt,
//         updatedAt = ingredientAction.updatedAt
//      )
      ingredientAction
   }

   override suspend fun getExpensesAndIncomesOfIngredients(
      query: String?,
      operationsStatus: Int?,
      fromDate: String?,
      toDate: String?,
      ingredientId: Int?,
      pageIndex: Int,
      pageSize: Int
   ): List<IngredientExOrInEntity> = wrapNetworkException {
      ingredientsApi.getExpensesAndIncomesIngredient(
         query = query,
         operationsStatus = operationsStatus,
         fromDate = fromDate,
         toDate = toDate,
         ingredientId = ingredientId,
         pageIndex = pageIndex,
         pageSize = pageSize
      )
         .operations
         .operationsList
         .map { ingredientAction ->
            IngredientExOrInEntity(
               id = ingredientAction.id,
               name = ingredientAction.name,
               amount = ingredientAction.amount,
               comment = ingredientAction.comment,
               creator = ingredientAction.userId,
               status = ingredientAction.status,
               unit = ingredientAction.unit,
               createdAt = ingredientAction.createdAt,
               updatedAt = ingredientAction.updatedAt
            )
         }
   }

   override suspend fun getIngredientList(): List<SimpleIngredient> = wrapNetworkException {
      ingredientsApi.getIngredientList().list.map {
         SimpleIngredient(
            id = it.id,
            name = it.name,
            unit = it.unit
         )
      }
   }
}