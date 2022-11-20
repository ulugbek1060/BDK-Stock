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
   suspend fun getIngredients(pageIndex: Int, pageSize: Int): List<IngredientEntity>

   /**
    * Fetches list of Ingredients with page index and page size like items size
    * and by query inserted by user
    * @throws ConnectionException
    * @throws BackendException
    * @throws ParseBackendResponseException
    */
   suspend fun getIngredientsByQuery(
      query: String,
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
      pageIndex: Int,
      pageSize: Int
   ): List<IngredientExOrInEntity>

   /**
    * Fetches actions of Ingredients expenses and incomes with pagination by query inserted by users
    * @throws ConnectionException
    * @throws BackendException
    * @throws ParseBackendResponseException
    */
   suspend fun getExpensesAndIncomesOfIngredientsByQuery(
      query: String,
      pageIndex: Int,
      pageSize: Int
   ): List<IngredientExOrInEntity>
}