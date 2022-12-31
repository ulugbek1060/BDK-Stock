package com.android.model.repository.ingredients

import androidx.paging.PagingData
import com.android.model.di.IoDispatcher
import com.android.model.repository.base.BasePageSource
import com.android.model.repository.base.BaseRepository
import com.android.model.repository.base.DataLoader
import com.android.model.repository.ingredients.entity.IngredientEntity
import com.android.model.repository.ingredients.entity.IngredientExOrInEntity
import com.android.model.repository.ingredients.entity.SimpleIngredient
import com.android.model.utils.EmptyFieldException
import com.android.model.utils.Field
import com.android.model.utils.Results
import com.android.model.utils.wrapBackendExceptions
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class IngredientsRepository @Inject constructor(
   private val ingredientsSource: IngredientsSource,
   @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseRepository() {

   suspend fun createIngredient(
      ingredientName: String,
      ingredientUnit: String
   ): IngredientEntity {
      if (ingredientName.isBlank()) throw EmptyFieldException(Field.NAME)
      if (ingredientUnit.isBlank()) throw EmptyFieldException(Field.UNIT)

      return wrapBackendExceptions {
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
      BasePageSource(loader = loader, defaultPageSize = DEFAULT_PAGE_SIZE)
   }

   suspend fun addExpensesAndIncomesOfIngredient(
      ingredientId: Long?,
      amount: String,
      statusOfActions: Int,
      comments: String
   ): String {
      if (ingredientId == null) throw EmptyFieldException(Field.NAME)
      if (amount.isBlank()) throw EmptyFieldException(Field.AMOUNT)
      if (comments.isBlank()) throw EmptyFieldException(Field.COMMENT)

      return wrapBackendExceptions {
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
      BasePageSource(loader, defaultPageSize = DEFAULT_PAGE_SIZE)
   }

   fun getIngredientsList(hasDefaultIngredient: Boolean = false): Flow<Results<List<SimpleIngredient>>> =
      flow {
         if (hasDefaultIngredient) {
            emit(emptyList())
         } else {
            emit(wrapBackendExceptions { ingredientsSource.getIngredientList() })
         }
      }
         .flowOn(ioDispatcher)
         .asResult()
}