package com.android.model.repository.ingredients

import com.android.model.repository.ingredients.entity.IngredientEntity
import com.android.model.repository.ingredients.entity.IngredientExOrInEntity

interface IngredientsSource {

   /**
    * Creates ingredient with total amount 0.00
    * @throws ConnectionException
    * @throws BackendException
    * @throws ParseBackendResponseException
    */
   suspend fun createIngredients(
      ingredientName: String,
      unit: String
   ): IngredientEntity

   /**
    * Fetches list of Ingredients with page index and page size like items size
    * @throws ConnectionException
    * @throws BackendException
    * @throws ParseBackendResponseException
    */
   suspend fun getIngredients(
      query: String?,
      pageIndex: Int,
      pageSize: Int
   ): List<IngredientEntity>

   /**
    * Adds action of ingredients like expenses or incomes of ingredients
    * @throws ConnectionException
    * @throws BackendException
    * @throws ParseBackendResponseException
    */
   suspend fun addExpensesAndIncomesOfIngredient(
      ingredientId: Int,
      amount: String,
      statusOfActions: Int,
      comments: String
   ): IngredientExOrInEntity

   /**
    * Fetches actions of Ingredients expenses and incomes with pagination
    * @throws ConnectionException
    * @throws BackendException
    * @throws ParseBackendResponseException
    */
   suspend fun getExpensesAndIncomesOfIngredients(
      query: String?,
      operationsStatus: String?,
      fromDate: String?,
      toDate: String?,
      ingredientId: Int?,
      pageIndex: Int,
      pageSize: Int
   ): List<IngredientExOrInEntity>
}