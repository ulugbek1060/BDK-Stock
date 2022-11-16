package com.android.model.repository.drivers.entity

import com.android.model.database.vehicles.entity.VehicleModelRoomEntity
import java.io.Serializable

data class VehicleModelEntity(
   val id: Int? = null,
   val name: String? = null
) : Serializable {

   fun toVehicleRoomEntity() = VehicleModelRoomEntity(
      id = id ?: 0,
      name = name ?: "undefined error from mapping"
   )

   override fun toString(): String {
      return name ?: "undefined error from mapping"
   }
}