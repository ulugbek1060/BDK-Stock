package com.android.source.network.sales.entity.createorder

import com.android.model.repository.sales.entity.DriverForOrderEntity
import com.android.model.repository.sales.entity.VehicleModel
import com.google.gson.annotations.SerializedName

data class DriverForOrder(
   @SerializedName("id") val id: Long,
   @SerializedName("name") val name: String,
   @SerializedName("avto_number") val autoRegNumber: String,
   @SerializedName("phone_number") val phoneNumber: String,
   @SerializedName("model") val autoModel: DriverAutoModel
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