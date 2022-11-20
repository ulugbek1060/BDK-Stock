package com.android.source.network.ingredients

import com.android.model.repository.ingredients.IngredientsSource
import com.android.model.repository.ingredients.entity.IngredientEntity
import com.android.model.repository.ingredients.entity.IngredientExOrInEntity
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

   override suspend fun getIngredients(pageIndex: Int, pageSize: Int): List<IngredientEntity> =
      wrapNetworkException {
         ingredientsApi.getIngredients(pageIndex, pageSize).data.list.map { ingredient ->
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

   override suspend fun getIngredientsByQuery(
      query: String,
      pageIndex: Int,
      pageSize: Int
   ): List<IngredientEntity> = wrapNetworkException {
      ingredientsApi.getIngredientsByQuery(query, pageIndex, pageSize).data.list.map { ingredient ->
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
      ingredientId: Int,
      amount: String,
      statusOfActions: Int,
      comments: String
   ): IngredientExOrInEntity = wrapNetworkException {
      val body = AddExOrInIngredientRequestEntity(
         materialId = ingredientId,
         amount = amount,
         comment = comments,
         status = statusOfActions
      )
      val ingredientAction = ingredientsApi.addExpenseAndIncomeIngredient(body).exOrInIngredients
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

   override suspend fun getExpensesAndIncomesOfIngredients(
      pageIndex: Int,
      pageSize: Int
   ): List<IngredientExOrInEntity> = wrapNetworkException {
      ingredientsApi.getExpensesAndIncomesIngredient(
         pageIndex = pageIndex,
         pageSize = pageSize
      ).expansesAndIncomesList.map { ingredientAction ->
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

   override suspend fun getExpensesAndIncomesOfIngredientsByQuery(
      query: String,
      pageIndex: Int,
      pageSize: Int
   ): List<IngredientExOrInEntity> = wrapNetworkException {
      ingredientsApi.getExpensesAndIncomesIngredientByQuery(
         query = query,
         pageIndex = pageIndex,
         pageSize = pageSize
      ).expansesAndIncomesList.map { ingredientAction ->
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
}