package com.android.model.repository.drivers

import androidx.paging.PagingData
import androidx.room.withTransaction
import com.android.model.database.AppDatabase
import com.android.model.repository.base.BasePageSource
import com.android.model.repository.base.BaseRepository
import com.android.model.repository.base.DataLoader
import com.android.model.repository.drivers.entity.DriverEntity
import com.android.model.utils.EmptyFieldException
import com.android.model.utils.Field
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DriversRepository @Inject constructor(
   private val driversSource: DriversSource,
   private val appDatabase: AppDatabase
) : BaseRepository() {

   suspend fun registerDriver(
      fullName: String,
      phoneNumber: String,
      vehicleModelId: Int?,
      regNumber: String
   ): DriverEntity {
      if (fullName.isBlank()) throw EmptyFieldException(Field.FullName)
      if (phoneNumber.isBlank()) throw EmptyFieldException(Field.PhoneNumber)
      if (vehicleModelId == null) throw EmptyFieldException(Field.VehicleModel)
      if (regNumber.isBlank()) throw EmptyFieldException(Field.RegNumber)

      return wrapExceptions {
         driversSource.createDriver(
            fullName = fullName,
            phoneNumber = phoneNumber,
            autoModelId = vehicleModelId,
            regNumber = regNumber
         )
      }
   }

   suspend fun updateDriver(
      driverId: Long,
      fullName: String,
      phoneNumber: String,
      autoModelId: Int?,
      regNumber: String
   ): String {
      if (fullName.isBlank()) throw EmptyFieldException(Field.FullName)
      if (phoneNumber.isBlank()) throw EmptyFieldException(Field.PhoneNumber)
      if (autoModelId == null) throw EmptyFieldException(Field.VehicleModel)
      if (regNumber.isBlank()) throw EmptyFieldException(Field.RegNumber)

      return wrapExceptions {
         driversSource.updateDriver(
            driverId = driverId,
            fullName = fullName,
            phoneNumber = phoneNumber,
            autoModelId = autoModelId,
            regNumber = regNumber
         )
      }
   }

   suspend fun getDriverById(driverId: Long): DriverEntity = wrapExceptions {
      driversSource.getDriverById(driverId)
   }

   suspend fun getVehicleModelsList() = networkBoundResult(
      query = {
         appDatabase.getVehiclesDao().getVehicles()
      },
      fetch = {
         driversSource.getVehiclesModelsList()
      },
      saveFetchedResult = { modelsList ->
         appDatabase.withTransaction {
            appDatabase.getVehiclesDao().insert(
               modelsList.map { it.toVehicleRoomEntity() }
            )
         }
      }
   ).map { result ->
      result.map { list ->
         list?.map {
            it.toVehicleModelEntity()
         }
      }
   }

   fun getDriversList(): Flow<PagingData<DriverEntity>> = getPagerData {
      val loader: DataLoader<DriverEntity> = { pageIndex ->
         driversSource.getDrivers(pageIndex, PAGE_SIZE)
      }
      BasePageSource(loader)
   }

   fun getDriversByQuery(query: String): Flow<PagingData<DriverEntity>> = getPagerData {
      val loader: DataLoader<DriverEntity> = { pageIndex ->
         driversSource.getDriversByQuery(query, pageIndex, PAGE_SIZE)
      }
      BasePageSource(loader)
   }

   companion object {
      const val PAGE_SIZE = 10
   }
}