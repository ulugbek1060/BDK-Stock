package com.android.model.database.vehicles

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.android.model.database.vehicles.entity.VehicleModelRoomEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VehiclesDao {

   @Insert(onConflict = REPLACE)
   fun save(list: List<VehicleModelRoomEntity>)

   @Query("DELETE FROM vehicle")
   fun clear()

   @Query("SELECT * FROM vehicle")
   fun getVehicles(): Flow<List<VehicleModelRoomEntity>?>

}