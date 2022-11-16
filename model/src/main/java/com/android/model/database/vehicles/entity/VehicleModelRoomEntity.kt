package com.android.model.database.vehicles.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.android.model.repository.drivers.entity.VehicleModelEntity

@Entity(tableName = "vehicle")
data class VehicleModelRoomEntity(
   @PrimaryKey val id: Int,
   val name: String
) {
   fun toVehicleModelEntity() = VehicleModelEntity(
      id = id,
      name = name
   )
}