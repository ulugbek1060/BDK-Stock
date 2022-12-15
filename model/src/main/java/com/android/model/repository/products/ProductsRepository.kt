package com.android.model.repository.products

import androidx.paging.PagingData
import com.android.model.di.IoDispatcher
import com.android.model.repository.base.BasePageSource
import com.android.model.repository.base.BaseRepository
import com.android.model.repository.base.DataLoader
import com.android.model.repository.ingredients.IngredientsSource
import com.android.model.repository.ingredients.entity.SimpleIngredient
import com.android.model.repository.products.entity.*
import com.android.model.utils.EmptyFieldException
import com.android.model.utils.Field
import com.android.model.utils.Results
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ProductsRepository @Inject constructor(
   private val productsSource: ProductsSource,
   private val ingredientsSource: IngredientsSource,
   @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : BaseRepository() {

   suspend fun createProduct(
      name: String, unit: String, price: String, ingredients: List<SimpleIngredientItem>
   ): String {

      if (name.isBlank()) throw EmptyFieldException(Field.NAME)
      if (unit.isBlank()) throw EmptyFieldException(Field.UNIT)
      if (price.isBlank()) throw EmptyFieldException(Field.PRICE)
      if (ingredients.isEmpty()) throw EmptyFieldException(Field.INGREDIENT)

      return suspendRunCatching {
         productsSource.createProducts(
            name = name, unit = unit, price = price, ingredients = ingredients
         )
      }
   }

   suspend fun updateProducts(
      productId: Long, name: String, unit: String, price: String
   ): ProductEntity {
      if (name.isBlank()) throw EmptyFieldException(Field.NAME)
      if (unit.isBlank()) throw EmptyFieldException(Field.UNIT)
      if (price.isBlank()) throw EmptyFieldException(Field.PRICE)

      return suspendRunCatching {
         productsSource.updateProducts(
            productId = productId, name = name, unit = unit, price = price
         )
      }
   }

   suspend fun updateIngredientOfProducts(
      productId: Long, ingredientList: List<SimpleIngredientItem>
   ): String {
      if (ingredientList.isEmpty()) throw EmptyFieldException(Field.INGREDIENT)

      return suspendRunCatching {
         productsSource.updateIngredientOfProducts(
            productId = productId, ingredientList = ingredientList
         )
      }
   }

   suspend fun manufactureProduct(
      productId: Long?, amount: String, comment: String
   ): String {
      if (productId == null) throw EmptyFieldException(Field.PRODUCT)
      if (amount.isBlank()) throw EmptyFieldException(Field.AMOUNT)
      if (comment.isBlank()) throw EmptyFieldException(Field.COMMENT)

      return suspendRunCatching {
         productsSource.manufactureProducts(
            productId = productId, amount = amount, comment = comment
         )
      }
   }

   suspend fun exportProduct(
      productId: Long?, amount: String, comment: String
   ): String {
      if (productId == null) throw EmptyFieldException(Field.PRODUCT)
      if (amount.isBlank()) throw EmptyFieldException(Field.AMOUNT)
      if (comment.isBlank()) throw EmptyFieldException(Field.COMMENT)

      return suspendRunCatching {
         productsSource.exportProduct(
            productId = productId, amount = amount, comment = comment
         )
      }
   }

   fun getProductList(
      query: String? = null,
   ): Flow<PagingData<ProductEntity>> = getPagerData {
      val loader: DataLoader<ProductEntity> = { pageIndex ->
         productsSource.getProductList(query, pageIndex, DEFAULT_PAGE_SIZE)
      }
      BasePageSource(loader, DEFAULT_PAGE_SIZE)
   }

   fun getOperationsList(
      productId: Long? = null,
      query: String? = null,
      status: Int? = null,
      fromDate: String? = null,
      toDate: String? = null
   ): Flow<PagingData<ProductOperationEntity>> = getPagerData {
      val loader: DataLoader<ProductOperationEntity> = { pageIndex ->
         productsSource.getOperations(
            productId = productId,
            query = query,
            pageIndex = pageIndex,
            pageSize = DEFAULT_PAGE_SIZE,
            status = status,
            fromDate = fromDate,
            toDate = toDate
         )
      }
      BasePageSource(loader, DEFAULT_PAGE_SIZE)
   }

   fun getIngredientsForSelect(): Flow<Results<List<SimpleIngredient>>> = flow {
      emit(suspendRunCatching { ingredientsSource.getIngredientList() })
   }.flowOn(ioDispatcher).asResult()

   fun getIngredientsOfProduct(productId: Long): Flow<Results<List<IngredientItem>>> = flow {
      emit(suspendRunCatching { productsSource.getIngredientsOfProduct(productId) })
   }.flowOn(ioDispatcher).asResult()

   fun getProductsForSelect(hasDefaultProduct: Boolean = false): Flow<List<ProductSelectionItem>> =
      flow {
         if (hasDefaultProduct) {
            emit(emptyList())
         } else {
            emit(suspendRunCatching { productsSource.getProductsForSelect() })
         }
      }.flowOn(ioDispatcher)

}