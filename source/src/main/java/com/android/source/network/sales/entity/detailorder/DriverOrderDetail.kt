package com.android.source.network.sales.entity.detailorder

import com.android.model.repository.sales.entity.DriverForOrderEntity
import com.android.model.repository.sales.entity.VehicleModel

data class DriverOrderDetail(
   val id: Long,
   val name: String,
   val autoRegNumber: String,
   val phoneNumber: String,
   val autoModel: AutoModelDetail
) {
   fun toDriverForOrderEntity() = DriverForOrderEntity(
      id = id,
      name = name,
      autoRegNumber = autoRegNumber,
      phoneNumber = phoneNumber,
      autoModel = VehicleModel(
         id = autoModel.id,
         name = autoModel.name
      )
   )
}
