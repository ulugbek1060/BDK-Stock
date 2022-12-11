package com.android.model.repository.sales.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ClientForOrderEntity(
   val fullName: String,
   val phoneNumber: String,
   val address: String
) : Parcelable
