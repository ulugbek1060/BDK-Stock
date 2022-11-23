package com.android.model.repository.ingredients

import androidx.paging.PagingData
import com.android.model.repository.base.BasePageSource
import com.android.model.repository.base.BaseRepository
import com.android.model.repository.base.DataLoader
import com.android.model.repository.ingredients.entity.IngredientEntity
import com.android.model.repository.ingredients.entity.IngredientExOrInEntity
import com.android.model.utils.EmptyFieldException
import com.android.model.utils.Field
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IngredientsRepository @Inject constructor(
   private val ingredientsSource: IngredientsSource
) : BaseRepository() {

   suspend fun createIngredient(
      ingredientName: String,
      ingredientUnit: String
   ): IngredientEntity {
      if (ingredientName.isBlank()) throw EmptyFieldException(Field.IngredientName)
      if (ingredientUnit.isBlank()) throw EmptyFieldException(Field.IngredientUnit)

      return wrapExceptions {
         ingredientsSource.createIngredients(ingredientName, ingredientUnit)
      }
   }

   fun getIngredientsList(
      query: String? = null
   ): Flow<PagingData<IngredientEntity>> = getPagerData {
      val loader: DataLoader<IngredientEntity> = { pageIndex ->
         ingredientsSource.getIngredients(
            query = query,
            pageIndex = pageIndex,
            pageSize = DEFAULT_PAGE_SIZE
         )
      }
      BasePageSource(loader)
   }

   suspend fun addExpensesAndIncomesOfIngredient(
      ingredientId: Int?,
      amount: String,
      statusOfActions: Int,
      comments: String
   ): IngredientExOrInEntity {
      if (ingredientId == null) throw EmptyFieldException(Field.IngredientName)
      if (amount.isBlank()) throw EmptyFieldException(Field.Amount)
      if (comments.isBlank()) throw EmptyFieldException(Field.Comments)

      return wrapExceptions {
         ingredientsSource.addExpensesAndIncomesOfIngredient(
            ingredientId = ingredientId,
            amount = amount,
            statusOfActions = statusOfActions,
            comments = comments
         )
      }
   }

   fun getExpensesAndIncomesOfIngredients(
      query: String? = null,
      operationsStatus: Int? = null,
      fromDate: String? = null,
      toDate: String? = null,
      ingredientId: Int? = null,
   ): Flow<PagingData<IngredientExOrInEntity>> = getPagerData {
      val loader: DataLoader<IngredientExOrInEntity> = { pageIndex ->
         ingredientsSource.getExpensesAndIncomesOfIngredients(
            query = query,
            operationsStatus = operationsStatus,
            fromDate = fromDate,
            toDate = toDate,
            ingredientId = ingredientId,
            pageIndex = pageIndex,
            pageSize = DEFAULT_PAGE_SIZE
         )
      }
      BasePageSource(loader)
   }
}