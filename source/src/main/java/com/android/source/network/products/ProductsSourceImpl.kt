package com.android.source.network.products

import com.android.model.repository.products.ProductsSource
import com.android.model.repository.products.entity.IngredientItem
import com.android.model.repository.products.entity.ProductEntity
import com.android.model.repository.products.entity.ProductOperationEntity
import com.android.model.repository.products.entity.SimpleIngredientItem
import com.android.source.network.base.BaseNetworkSource
import com.android.source.network.products.entity.createproduct.ProductCreateRequestEntity
import com.android.source.network.products.entity.exportproduct.ExportProductRequestEntity
import com.android.source.network.products.entity.manufactureorsale.ManufactureProductRequestEntity
import com.android.source.network.products.entity.product.IngredientOfProduct
import com.android.source.network.products.entity.updateingredientofproduct.ProductUpdateIngredientRequestEntity
import com.android.source.network.products.entity.updateproduct.ProductUpdateRequestEntity
import javax.inject.Inject

class ProductsSourceImpl @Inject constructor(
   private val productsApi: ProductsApi
) : BaseNetworkSource(), ProductsSource {

   override suspend fun createProducts(
      name: String,
      unit: String,
      price: String,
      ingredients: List<SimpleIngredientItem>
   ): ProductEntity = wrapNetworkException {

      val body = ProductCreateRequestEntity(
         name = name,
         unit = unit,
         price = price,
         materials = ingredients
            .map {
               IngredientOfProduct(
                  it.id,
                  amount = it.amount
               )
            }
      )

      val product = productsApi
         .createProduct(body)
         .product

      ProductEntity(
         id = product.id,
         name = product.name,
         price = product.price,
         amount = product.amount,
         createdAt = product.createdAt,
         updatedAt = product.updatedAt
      )
   }

   override suspend fun updateProducts(
      productId: Long,
      name: String,
      unit: String,
      price: String
   ): ProductEntity = wrapNetworkException {

      val body = ProductUpdateRequestEntity(
         name = name,
         unit = unit,
         price = price
      )

      val product = productsApi
         .updateProduct(
            productId = productId,
            body
         )
         .updatedProduct

      ProductEntity(
         id = product.id,
         name = product.name,
         amount = product.amount,
         price = product.price,
         createdAt = product.createdAt,
         updatedAt = product.updatedAt
      )
   }

   override suspend fun manufactureProducts(
      productId: Long,
      amount: String,
      comment: String
   ): ProductOperationEntity = wrapNetworkException {

      val body = ManufactureProductRequestEntity(
         productId = productId,
         amount = amount,
         comment = comment
      )

      val operation = productsApi
         .manufactureProduct(body)
         .manufacturedProduct

      ProductOperationEntity(
         id = operation.id,
         name = operation.name,
         amount = operation.amount,
         comment = operation.comment ?: "no comment",
         unit = operation.unit ?: "undefined unit",
         status = operation.status,
         createdAt = operation.createdAt
      )
   }

   override suspend fun updateIngredientOfProducts(
      productId: Long,
      ingredientList: List<SimpleIngredientItem>
   ): String = wrapNetworkException {

      val body = ProductUpdateIngredientRequestEntity(
         productId = productId,
         newIngredientList = ingredientList.map {
            IngredientOfProduct(
               id = it.id,
               amount = it.amount
            )
         }
      )

      productsApi
         .updateIngredientsOfProduct(body)
         .message
   }

   override suspend fun exportIngredient(
      productId: Long,
      amount: String,
      comment: String
   ): String = wrapNetworkException {

      val body = ExportProductRequestEntity(
         productId = productId,
         amount = amount,
         comment = comment
      )

      productsApi
         .export(body)
         .message
   }

   override suspend fun getProductList(
      query: String?,
      pageIndex: Int,
      pageSize: Int
   ): List<ProductEntity> = wrapNetworkException {

      productsApi
         .getProductList(
            query = query,
            pageIndex = pageIndex,
            pageSize = pageSize
         )
         .productListData
         .productList
         .map {
            ProductEntity(
               id = it.id,
               name = it.name,
               price = it.price,
               amount = it.amount,
               createdAt = it.createdAt,
               updatedAt = it.updatedAt
            )
         }
   }

   override suspend fun getOperations(
      productId: Long?,
      query: String?,
      pageIndex: Int,
      pageSize: Int,
      status: Int?,
      fromDate: String?,
      toDate: String?
   ): List<ProductOperationEntity> = wrapNetworkException {
      productsApi
         .getOperationList(
            productId,
            query,
            pageIndex,
            pageSize,
            status,
            fromDate,
            toDate
         )
         .list
         .map {
            ProductOperationEntity(
               id = it.id,
               name = it.name,
               amount = it.amount,
               comment = it.comment ?: "no comment something went wrong!!!",
               unit = it.unit ?: "undefined unit!!!",
               status = it.status,
               createdAt = it.createdAt
            )
         }
   }

   override suspend fun getIngredientsOfProduct(
      productId: Long
   ): List<IngredientItem> = wrapNetworkException {
      productsApi
         .getIngredientsOfProduct(productId = productId)
         .ingredients
         .map {
            IngredientItem(
               ingredientId = it.materialId,
               name = it.name,
               amount = it.amount,
               unit = it.unit
            )
         }
   }

   override suspend fun getProductsForSelect(): List<IngredientItem> = wrapNetworkException {
      productsApi
         .getProductsForSelect()
         .ingredients
         .map {
            IngredientItem(
               ingredientId = it.materialId,
               name = it.name,
               amount = it.amount,
               unit = it.unit
            )
         }
   }
}