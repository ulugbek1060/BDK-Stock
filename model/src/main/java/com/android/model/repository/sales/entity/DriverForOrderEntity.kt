package com.android.model.repository.sales.entity

data class DriverForOrderEntity(
   val id: Long,
   val name: String,
   val autoRegNumber: String,
   val phoneNumber: String,
   val autoModel: VehicleModel
)
