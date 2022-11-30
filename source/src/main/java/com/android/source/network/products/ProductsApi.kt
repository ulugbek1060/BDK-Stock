package com.android.source.network.products

import com.android.source.network.products.entity.createproduct.ProductCreateRequestEntity
import com.android.source.network.products.entity.createproduct.ProductCreateResponseEntity
import com.android.source.network.products.entity.exportproduct.ExportProductRequestEntity
import com.android.source.network.products.entity.exportproduct.ExportProductResponseEntity
import com.android.source.network.products.entity.getingredientsofproduct.IngredientsOfProductResponseEntity
import com.android.source.network.products.entity.manufactureorsale.ManufactureProductRequestEntity
import com.android.source.network.products.entity.manufactureorsale.ManufactureProductResponseEntity
import com.android.source.network.products.entity.operationslist.ManufacturedOrExportedProductsResponseEntity
import com.android.source.network.products.entity.productforselection.ProductForSelectionResponseEntity
import com.android.source.network.products.entity.productlist.ProductListResponseEntity
import com.android.source.network.products.entity.updateingredientofproduct.ProductUpdateIngredientRequestEntity
import com.android.source.network.products.entity.updateingredientofproduct.ProductUpdateIngredientResponseEntity
import com.android.source.network.products.entity.updateproduct.ProductUpdateRequestEntity
import com.android.source.network.products.entity.updateproduct.ProductUpdateResponseEntity
import retrofit2.http.*

interface ProductsApi {

   // create product
   @Headers("Content-Type: application/json", "Accept: application/json")
   @POST("api/product/create")
   suspend fun createProduct(
      @Body body: ProductCreateRequestEntity
   ): ProductCreateResponseEntity

   // manufacture product if ingredients exist in stock
   @Headers("Content-Type: application/json", "Accept: application/json")
   @POST("api/product/add")
   suspend fun manufactureProduct(
      @Body body: ManufactureProductRequestEntity
   ): ManufactureProductResponseEntity

   // update product
   @Headers("Content-Type: application/json", "Accept: application/json")
   @POST("api/product/updateProduct/{id}")
   suspend fun updateProduct(
      @Path("id") productId: Long,
      @Body body: ProductUpdateRequestEntity
   ): ProductUpdateResponseEntity

   // update ingredient of products
   @Headers("Content-Type: application/json", "Accept: application/json")
   @POST("api/product/updateRetsept")
   suspend fun updateIngredientsOfProduct(
      @Body body: ProductUpdateIngredientRequestEntity
   ): ProductUpdateIngredientResponseEntity

   // export product
   @Headers("Content-Type: application/json", "Accept: application/json")
   @POST("api/product/consump")
   suspend fun export(
      @Body body: ExportProductRequestEntity
   ): ExportProductResponseEntity

   // get all created products
   @Headers("Content-Type: application/json", "Accept: application/json")
   @GET("api/product/product-list")
   suspend fun getProductList(
      @Query("search") query: String?,
      @Query("page") pageIndex: Int,
      @Query("count") pageSize: Int
   ): ProductListResponseEntity

   // get manufactured and exported products
   @Headers("Content-Type: application/json", "Accept: application/json")
   @GET("api/product/arrived-list")
   suspend fun getOperationList(
      @Query("product_id") productId: Long?,
      @Query("search") query: String?,
      @Query("page") pageIndex: Int,
      @Query("count") pageSize: Int,
      @Query("status") status: Int?,
      @Query("dateFrom") fromDate: String?,
      @Query("dateTo") toDate: String?
   ): ManufacturedOrExportedProductsResponseEntity

   // get ingredients of product by its id
   @Headers("Content-Type: application/json", "Accept: application/json")
   @GET("api/product/retsept/{id}")
   suspend fun getIngredientsOfProduct(
      @Path("id") productId: Long
   ): IngredientsOfProductResponseEntity

   // get products for selection for users
   @Headers("Content-Type: application/json", "Accept: application/json")
   @GET("api/product/select")
   suspend fun getProductsForSelect(): ProductForSelectionResponseEntity

}