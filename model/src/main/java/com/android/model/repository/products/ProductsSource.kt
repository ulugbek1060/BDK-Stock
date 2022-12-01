package com.android.model.repository.products

import com.android.model.repository.products.entity.*

interface ProductsSource {

   /**
    * Create product.
    * Arguments: name, unit, price, ([SimpleIngredientItem] as list)
    * @return [ProductEntity]
    *
    * @throws ConnectionException
    * @throws BackendException
    * @throws ParseBackendResponseException
    */
   suspend fun createProducts(
      name: String,
      unit: String,
      price: String,
      ingredients: List<SimpleIngredientItem>
   ): ProductEntity

   /**
    * Updates products.
    * Arguments: productId, name, unit, price
    * @return [ProductEntity]
    *
    * @throws ConnectionException
    * @throws BackendException
    * @throws ParseBackendResponseException
    */
   suspend fun updateProducts(
      productId: Long,
      name: String,
      unit: String,
      price: String
   ): ProductEntity

   /**
    * Manufactures Products.
    * Arguments: productId, amount, comment
    * @return [String] success message
    *
    * @throws ConnectionException
    * @throws BackendException
    * @throws ParseBackendResponseException
    */
   suspend fun manufactureProducts(
      productId: Long,
      amount: String,
      comment: String
   ): String


   /**
    * Updates Ingredients of Products.
    * Arguments: productId, (ingredients -> (ingredientId, amount) as list)
    * @return [String] success message
    *
    * @throws ConnectionException
    * @throws BackendException
    * @throws ParseBackendException
    */
   suspend fun updateIngredientOfProducts(
      productId: Long,
      ingredientList: List<SimpleIngredientItem>
   ): String // success message


   /**
    * Exports Products as Rasxod.
    * Arguments: productId, amount, comment
    * @return [String] success message
    *
    * @throws ConnectionException
    * @throws BackendException
    * @throws ParseBackendException
    */
   suspend fun exportProduct(
      productId: Long,
      amount: String,
      comment: String
   ): String

   /**
    * Fetched Products list.
    * Arguments: query, pageIndex, pageSize
    * @return [List]
    *
    * @throws ConnectionException
    * @throws BackendException
    * @throws ParseBackendException
    */
   suspend fun getProductList(
      query: String?,
      pageIndex: Int,
      pageSize: Int
   ): List<ProductEntity>

   /**
    * Fetches operations list.
    * Arguments: productId, query, pageIndex, pageSize, status, fromDate, toDate
    * @return [ProductOperationEntity] -> [List]
    *
    * @throws ConnectionException
    * @throws BackendException
    * @throws ParseBackendException
    */
   suspend fun getOperations(
      productId: Long?,
      query: String?,
      pageIndex: Int,
      pageSize: Int,
      status: Int?,
      fromDate: String?,
      toDate: String?
   ): List<ProductOperationEntity>

   /**
    * Get ingredients of Product.
    * Arguments: productId
    * @return [IngredientItem] -> [List]
    *
    * @throws ConnectionException
    * @throws BackendException
    * @throws ParseBackendException
    */
   suspend fun getIngredientsOfProduct(
      productId: Long
   ): List<IngredientItem>

   /**
    * Fetch Products for selection.
    * no arguments
    * @return [ProductSelectionItem] -> [List]
    *
    * @throws ConnectionException
    * @throws BackendException
    * @throws ParseBackendException
    */
   suspend fun getProductsForSelect(): List<ProductSelectionItem>
}