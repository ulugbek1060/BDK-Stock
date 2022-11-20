package com.android.source.network.ingredients

import com.android.source.network.ingredients.entity.addexpenseorincome.AddExOrInIngredientRequestEntity
import com.android.source.network.ingredients.entity.addexpenseorincome.AddExOrInIngredientsResponseEntity
import com.android.source.network.ingredients.entity.createingredient.CreateIngredientRequestEntity
import com.android.source.network.ingredients.entity.createingredient.CreateIngredientResponseEntity
import com.android.source.network.ingredients.entity.expenseandincomelist.ExpensesAndIncomesListResponseEntity
import com.android.source.network.ingredients.entity.ingredientslist.IngredientsListResponseEntity
import retrofit2.http.*

interface IngredientsApi {

   @Headers("Content-Type: application/json", "Accept: application/json")
   @POST("api/meterial/create")
   suspend fun createIngredients(
      @Body body: CreateIngredientRequestEntity
   ): CreateIngredientResponseEntity

   @Headers("Content-Type: application/json", "Accept: application/json")
   @GET("api/meterial/get")
   suspend fun getIngredients(
      @Query("page") pageIndex: Int,
      @Query("count") pageSize: Int
   ): IngredientsListResponseEntity

   @Headers("Content-Type: application/json", "Accept: application/json")
   @GET("api/meterial/get")
   suspend fun getIngredientsByQuery(
      @Query("search") query: String,
      @Query("page") pageIndex: Int,
      @Query("count") pageSize: Int
   ): IngredientsListResponseEntity

   /**
    *  Расход -> expense
    *  Приход -> income
    */
   @Headers("Content-Type: application/json", "Accept: application/json")
   @POST("api/meterial/add-material")
   suspend fun addExpenseAndIncomeIngredient(
      @Body body: AddExOrInIngredientRequestEntity
   ): AddExOrInIngredientsResponseEntity

   @Headers("Content-Type: application/json", "Accept: application/json")
   @GET("api/meterial/index")
   suspend fun getExpensesAndIncomesIngredient(
      @Query("page") pageIndex: Int,
      @Query("count") pageSize: Int
   ): ExpensesAndIncomesListResponseEntity

   @Headers("Content-Type: application/json", "Accept: application/json")
   @GET("api/meterial/index")
   suspend fun getExpensesAndIncomesIngredientByQuery(
      @Query("search") query: String,
      @Query("page") pageIndex: Int,
      @Query("count") pageSize: Int
   ): ExpensesAndIncomesListResponseEntity
}