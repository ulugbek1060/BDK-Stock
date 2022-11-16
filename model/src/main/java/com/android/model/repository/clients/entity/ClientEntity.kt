package com.android.model.repository.clients.entity

data class ClientEntity(
   val clientId: Long,
   val fullName: String,
   val phoneNumber: String,
   val address: String? = null,
   val createdAt: String? = null,
   val updatedAt: String? = null
)