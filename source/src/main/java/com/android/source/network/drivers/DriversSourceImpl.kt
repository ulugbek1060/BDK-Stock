package com.android.source.network.drivers

import com.android.model.repository.drivers.DriversSource
import com.android.model.repository.drivers.entity.DriverEntity
import com.android.model.repository.drivers.entity.VehicleModelEntity
import com.android.source.network.base.BaseNetworkSource
import com.android.source.network.drivers.entity.create.DriverCreateRequestEntity
import com.android.source.network.drivers.entity.update.DriverUpdateRequestEntity
import javax.inject.Inject

class DriversSourceImpl @Inject constructor(
   private val driversApi: DriversApi
) : BaseNetworkSource(), DriversSource {

   override suspend fun createDriver(
      fullName: String,
      phoneNumber: String,
      autoModelId: Int,
      regNumber: String
   ): DriverEntity = wrapNetworkException {
      val body = DriverCreateRequestEntity(
         name = fullName,
         phoneNumber = phoneNumber,
         automodelId = autoModelId,
         avtoNumber = regNumber
      )
      val response = driversApi.createDriver(body).driver
      DriverEntity(
         id = response.id,
         driverFullName = response.name,
         phoneNumber = response.phoneNumber,
         autoRegNumber = response.avtoNumber,
         vehicle = VehicleModelEntity(
            id = response.model.id,
            name = response.model.name
         )
      )
   }

   override suspend fun updateDriver(
      driverId: Long,
      fullName: String,
      phoneNumber: String,
      autoModelId: Int,
      regNumber: String
   ): String = wrapNetworkException {
      val body = DriverUpdateRequestEntity(
         id = driverId,
         name = fullName,
         phoneNumber = phoneNumber,
         automodelId = autoModelId,
         avtoNumber = regNumber,
      )
      val response = driversApi.updateDriver(body)
      response.msg
   }

   override suspend fun getDriverById(driverId: Long): DriverEntity = wrapNetworkException {
      val response = driversApi.getDriverById(id = driverId).driverInfo
      DriverEntity(
         id = response.id,
         driverFullName = response.name,
         phoneNumber = response.phoneNumber,
         autoRegNumber = response.avtoNumber,
         vehicle = VehicleModelEntity(
            id = response.automodelId
         ),
         isDeleted = response.isDelete,
         createdAt = response.createdAt,
         updatedAt = response.updatedAt
      )
   }

   override suspend fun getModelsList(): List<VehicleModelEntity> = wrapNetworkException {
      driversApi.getVehicleModels().modelsList.map { vehicleModel ->
         VehicleModelEntity(
            id = vehicleModel.id,
            name = vehicleModel.name
         )
      }
   }

   override suspend fun getDrivers(
      search: String,
      pageIndex: Int,
      pageSize: Int
   ): List<DriverEntity> = wrapNetworkException {
      driversApi.getDriversList(search, pageIndex, pageSize).driverList.map { driver ->
         DriverEntity(
            id = driver.id,
            driverFullName = driver.name,
            phoneNumber = driver.phoneNumber,
            autoRegNumber = driver.avtoNumber,
            vehicle = VehicleModelEntity(
               id = driver.model.id,
               name = driver.model.name
            )
         )
      }
   }
}