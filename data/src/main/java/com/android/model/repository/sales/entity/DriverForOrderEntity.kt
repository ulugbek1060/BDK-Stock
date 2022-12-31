package com.android.model.repository.sales.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DriverForOrderEntity(
   val id: Long,
   val name: String,
   val autoRegNumber: String,
   val phoneNumber: String,
   val autoModel: VehicleModel
):Parcelable
