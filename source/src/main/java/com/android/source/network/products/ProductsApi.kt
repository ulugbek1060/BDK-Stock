package com.android.source.network.products

import retrofit2.http.POST

interface ProductsApi {

   @POST("api/product/create")
   suspend fun createProduct()

}