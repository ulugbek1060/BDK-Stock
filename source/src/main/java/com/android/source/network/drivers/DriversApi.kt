package com.android.source.network.drivers

import com.android.source.network.drivers.entity.driver.CreateDriverRequestEntity
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface DriversApi {

   @POST("api/driver/create")
   suspend fun createDriver(
      @Body body: CreateDriverRequestEntity
   ): String

   @GET("api/automodel/list")
   suspend fun getVehicleModels()
}