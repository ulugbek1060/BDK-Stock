package com.android.model.repository.dashboard.entity

class PaycheckEntity(
   val id: Long,
   val orderId: Long?,
   val name: String,
   val amount: String,
   val comment: String?,
   val payType: String?,
   val createdAt: String?
)