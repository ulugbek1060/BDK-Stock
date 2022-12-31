package com.android.model.repository.products.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ProductOperationEntity(
  val id: Long,
  val name: String? = null,
  val amount: String,
  val comment: String,
  val unit: String,
  val creator:String,
  val status: Int,
  val createdAt: String
) : Parcelable
