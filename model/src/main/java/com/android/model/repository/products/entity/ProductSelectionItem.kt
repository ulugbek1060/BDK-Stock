package com.android.model.repository.products.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.math.BigDecimal

@Parcelize
data class ProductSelectionItem(
   val id: Long,
   val name: String,
   val unit: String,
   val price: String
) : Parcelable {
   var amount: String? = null

   fun calculate(): String {
      if (amount == "" || amount == null) return ""
      return BigDecimal(price).multiply(BigDecimal(amount)).toString()
   }

   override fun toString(): String {
      return name
   }
}