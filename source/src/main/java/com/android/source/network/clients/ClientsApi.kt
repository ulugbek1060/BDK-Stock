package com.android.source.network.clients

import com.android.source.network.clients.entity.clientlist.ClientListResponseEntity
import com.android.source.network.clients.entity.create.ClientCreateRequestEntity
import com.android.source.network.clients.entity.create.ClientCreateResponseEntity
import com.android.source.network.clients.entity.getclient.ClientByIdResponseEntity
import com.android.source.network.clients.entity.update.ClientUpdateRequestEntity
import com.android.source.network.clients.entity.update.ClientUpdateResponseEntity
import retrofit2.http.*

interface ClientsApi {

   @Headers("Content-Type: application/json", "Accept: application/json")
   @POST("api/client/create")
   suspend fun registerClient(@Body body: ClientCreateRequestEntity): ClientCreateResponseEntity

   @Headers("Content-Type: application/json", "Accept: application/json")
   @POST("api/client/update")
   suspend fun updateClient(@Body body: ClientUpdateRequestEntity): ClientUpdateResponseEntity

   @Headers("Content-Type: application/json", "Accept: application/json")
   @GET("api/client/getById/{id}")
   suspend fun getClientById(id: Long): ClientByIdResponseEntity

   @Headers("Content-Type: application/json", "Accept: application/json")
   @GET("api/client/get")
   suspend fun getClients(
      @Query("search") query: String,
      @Query("page") pageIndex: Int,
      @Query("count") pageSize: Int,
   ): ClientListResponseEntity
}