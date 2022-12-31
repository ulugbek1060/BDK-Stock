package com.android.model.repository.sales.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@Parcelize
data class OrderedProduct(
   val id: Long,
   val name: String,
   val unit: String,
   val amount: String,
   val price: String,
   val summa: String
) : Parcelable {
   fun calculate(): String {
      if (amount.isBlank() || price.isBlank()) return "0"
      return BigDecimal(price).multiply(BigDecimal(amount)).toString()
   }

   override fun toString(): String {
      return name
   }
}
