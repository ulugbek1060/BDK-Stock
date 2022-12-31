package com.android.model.repository.sales.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderListItem(
   val id: Long,
   val identification: String,
   val client: String,
   val summa: String,
   val status: Int,
   val createdAt: String
) : Parcelable
