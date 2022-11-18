package com.android.model.database.vehicles

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.android.model.database.BaseDao
import com.android.model.database.vehicles.entity.VehicleModelRoomEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VehiclesDao : BaseDao<VehicleModelRoomEntity> {

   @Query("DELETE FROM vehicle")
   suspend fun clear()

   @Query("SELECT * FROM vehicle")
   fun getVehicles(): Flow<List<VehicleModelRoomEntity>?>

}