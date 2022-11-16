package com.android.model.repository.drivers

import com.android.model.repository.drivers.entity.DriverEntity
import com.android.model.repository.drivers.entity.VehicleModelEntity
import com.android.model.utils.BackendException
import com.android.model.utils.EmptyFieldException
import com.google.gson.JsonParseException

interface DriversSource {

   /**
    * Create driver name, phone number, vehicle, registration number.
    * @throws EmptyFieldException
    * @throws InvalidCredentialsException
    * @throws ConnectionException
    * @throws BackendException
    * @throws ParseBackendResponseException
    * @throws JsonParseException
    */
   suspend fun createDriver(
      fullName: String,
      phoneNumber: String,
      autoModelId: Int,
      regNumber: String
   ): DriverEntity

   /**
    * Update driver name, phone number, vehicle, registration number.
    * @throws EmptyFieldException
    * @throws InvalidCredentialsException
    * @throws ConnectionException
    * @throws BackendException
    * @throws ParseBackendResponseException
    * @throws JsonParseException
    */
   suspend fun updateDriver(
      driverId: Long,
      fullName: String,
      phoneNumber: String,
      autoModelId: Int,
      regNumber: String
   ): String

   /**
    * @throws InvalidCredentialsException
    * @throws ConnectionException
    * @throws BackendException
    * @throws ParseBackendResponseException
    * @throws JsonParseException
    */
   suspend fun getDriverById(
      driverId: Long
   ): DriverEntity


   /**
    * @throws InvalidCredentialsException
    * @throws ConnectionException
    * @throws BackendException
    * @throws ParseBackendResponseException
    * @throws JsonParseException
    */
   suspend fun getModelsList(): List<VehicleModelEntity>


   /**
    * @throws Exception
    */
   suspend fun getDrivers(search: String, pageIndex: Int, pageSize: Int): List<DriverEntity>

}