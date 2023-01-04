package com.android.model.repository.sales.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

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
) : Parcelable {

   fun calculateTotalWeight(): String {
      var weight = "0"
      try {
         products.forEach { product ->
            if (product.amount != "")
               weight = BigDecimal(weight).plus(BigDecimal(product.amount)).toString()
         }
      } catch (e: Exception) {
         weight
      }
      return "$weight ${products.first().unit}"
   }

}
