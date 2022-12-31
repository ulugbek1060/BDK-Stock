package com.android.model.repository.drivers.entity

import java.io.Serializable

data class DriverEntity(
   val id: Long,
   val driverFullName: String,
   val phoneNumber: String,
   val autoRegNumber: String,
   val vehicle: VehicleModelEntity,
   val isDeleted: String? = null,
   val createdAt: String? = null,
   val updatedAt: String? = null
) : Serializable