package com.android.source.network.drivers

import com.android.model.utils.Const
import com.android.model.utils.Const.HEADER_KEY_CONTENT_TYPE
import com.android.model.utils.Const.HEADER_VALUE_ACCEPT
import com.android.model.utils.Const.HEADER_VALUE_CONTENT_TYPE
import com.android.source.network.drivers.entity.automodel.VehicleModelResponseEntity
import com.android.source.network.drivers.entity.createdrivers.DriverCreateRequestEntity
import com.android.source.network.drivers.entity.createdrivers.DriverCreateResponseEntity
import com.android.source.network.drivers.entity.driverlist.DriversListResponseEntity
import com.android.source.network.drivers.entity.getdriver.DriverInfoResponseEntity
import com.android.source.network.drivers.entity.updatedrivers.DriverUpdateRequestEntity
import com.android.source.network.drivers.entity.updatedrivers.DriverUpdateResponseEntity
import retrofit2.http.*

interface DriversApi {

   @Headers("Content-Type: application/json", "Accept: application/json")
   @POST("api/driver/create")
   suspend fun createDriver(
      @Body body: DriverCreateRequestEntity
   ): DriverCreateResponseEntity

   @Headers("Content-Type: application/json", "Accept: application/json")
   @POST("api/driver/update")
   suspend fun updateDriver(
      @Body body: DriverUpdateRequestEntity,
   ): DriverUpdateResponseEntity

   @Headers("Content-Type: application/json", "Accept: application/json")
   @GET("api/driver/getById/{id}")
   suspend fun getDriverById(
      @Path("id") id: Long,
   ): DriverInfoResponseEntity

   @Headers("Content-Type: application/json", "Accept: application/json")
   @GET("api/driver/get")
   suspend fun getDriversList(
      @Query("search") query: String?,
      @Query("page") pageIndex: Int,
      @Query("count") pageSize: Int,
   ): DriversListResponseEntity

   @Headers("Content-Type: application/json", "Accept: application/json")
   @GET("api/automodel/list")
   suspend fun getVehicleModels(): VehicleModelResponseEntity
}