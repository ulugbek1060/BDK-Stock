package com.android.source.network.drivers

import com.android.model.utils.Const
import com.android.model.utils.Const.HEADER_KEY_CONTENT_TYPE
import com.android.model.utils.Const.HEADER_VALUE_ACCEPT
import com.android.model.utils.Const.HEADER_VALUE_CONTENT_TYPE
import com.android.source.network.drivers.entity.automodel.VehicleModelResponseEntity
import com.android.source.network.drivers.entity.create.DriverCreateRequestEntity
import com.android.source.network.drivers.entity.create.DriverCreateResponseEntity
import com.android.source.network.drivers.entity.driverlist.DriversListResponseEntity
import com.android.source.network.drivers.entity.getdriver.DriverInfoResponseEntity
import com.android.source.network.drivers.entity.update.DriverUpdateRequestEntity
import com.android.source.network.drivers.entity.update.DriverUpdateResponseEntity
import retrofit2.http.*

interface DriversApi {

   @POST("api/driver/create")
   suspend fun createDriver(
      @Body body: DriverCreateRequestEntity,
      @Header(HEADER_KEY_CONTENT_TYPE) type: String = HEADER_VALUE_CONTENT_TYPE,
      @Header(Const.HEADER_KEY_ACCEPT) accept: String = HEADER_VALUE_ACCEPT
   ): DriverCreateResponseEntity

   @POST("api/driver/update")
   suspend fun updateDriver(
      @Body body: DriverUpdateRequestEntity,
      @Header(HEADER_KEY_CONTENT_TYPE) type: String = HEADER_VALUE_CONTENT_TYPE,
      @Header(Const.HEADER_KEY_ACCEPT) accept: String = HEADER_VALUE_ACCEPT
   ): DriverUpdateResponseEntity

   @GET("api/driver/getById/{id}")
   suspend fun getDriverById(
      @Path("id") id: Long,
      @Header(HEADER_KEY_CONTENT_TYPE) type: String = HEADER_VALUE_CONTENT_TYPE,
      @Header(Const.HEADER_KEY_ACCEPT) accept: String = HEADER_VALUE_ACCEPT
   ): DriverInfoResponseEntity

   @GET("api/driver/get")
   suspend fun getDriversList(
      @Query("search") query: String,
      @Query("page") pageIndex: Int,
      @Query("count") pageSize: Int,
      @Header(HEADER_KEY_CONTENT_TYPE) type: String = HEADER_VALUE_CONTENT_TYPE,
      @Header(Const.HEADER_KEY_ACCEPT) accept: String = HEADER_VALUE_ACCEPT
   ): DriversListResponseEntity

   @GET("api/automodel/list")
   suspend fun getVehicleModels(
      @Header(HEADER_KEY_CONTENT_TYPE) type: String = HEADER_VALUE_CONTENT_TYPE,
      @Header(Const.HEADER_KEY_ACCEPT) accept: String = HEADER_VALUE_ACCEPT
   ): VehicleModelResponseEntity
}