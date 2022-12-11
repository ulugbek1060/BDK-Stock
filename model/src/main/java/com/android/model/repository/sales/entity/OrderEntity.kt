package com.android.model.repository.sales.entity

import android.content.Context
import android.os.Parcelable
import com.android.model.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class OrderEntity(
   val id: Long,
   val identification: String,
   val status: Int,
   val summa: String,
   val paid: String,
   val debit: String,
   val createdAt: String,
   val client: ClientForOrderEntity,
   val driver: DriverForOrderEntity,
   val products: List<OrderedProduct>
) : Parcelable
