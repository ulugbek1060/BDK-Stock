package com.android.model.repository.sales.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VehicleModel(
   val id: Int,
   val name: String
) : Parcelable
