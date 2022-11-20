package com.android.source.network.clients

import com.android.model.repository.clients.ClientSource
import com.android.model.repository.clients.entity.ClientEntity
import com.android.source.network.base.BaseNetworkSource
import com.android.source.network.clients.entity.createclients.ClientCreateRequestEntity
import com.android.source.network.clients.entity.updateclients.ClientUpdateRequestEntity
import javax.inject.Inject

class ClientsSourceImpl @Inject constructor(
   private val clientsApi: ClientsApi
) : BaseNetworkSource(), ClientSource {

   override suspend fun registerClient(
      fullName: String,
      phoneNumber: String,
      address: String
   ): ClientEntity = wrapNetworkException {
      val body = ClientCreateRequestEntity(
         name = fullName,
         address = address,
         phoneNumber = phoneNumber
      )
      val client = clientsApi.registerClient(body).client
      ClientEntity(
         clientId = client.id,
         fullName = fullName,
         phoneNumber = phoneNumber,
         address = address,
         createdAt = client.createdAt,
         updatedAt = client.updatedAt
      )
   }

   override suspend fun updateClient(
      clientId: Long,
      fullName: String,
      phoneNumber: String,
      address: String
   ): String = wrapNetworkException {
      val body = ClientUpdateRequestEntity(
         id = clientId,
         name = fullName,
         phoneNumber = phoneNumber,
         address = address
      )
      val response = clientsApi.updateClient(body)
      response.msg
   }

   override suspend fun getClientById(clientId: Long): ClientEntity = wrapNetworkException {
      val client = clientsApi.getClientById(clientId).client
      ClientEntity(
         clientId = client.id,
         fullName = client.name,
         phoneNumber = client.phoneNumber,
         address = client.address,
         createdAt = client.createdAt,
         updatedAt = client.updatedAt
      )
   }

   override suspend fun getClientsList(
      pageIndex: Int,
      pageSize: Int
   ): List<ClientEntity> = wrapNetworkException {
      clientsApi.getClients(pageIndex, pageSize).clients.clients.map {
         ClientEntity(
            clientId = it.id,
            fullName = it.name,
            phoneNumber = it.phoneNumber,
            address = it.address,
            createdAt = it.createdAt,
            updatedAt = it.updatedAt
         )
      }
   }

   override suspend fun getClientsByQuery(
      query: String,
      pageIndex: Int,
      pageSize: Int
   ): List<ClientEntity> = wrapNetworkException {
      clientsApi.getClientsByQuery(query, pageIndex, pageSize).clients.clients.map {
         ClientEntity(
            clientId = it.id,
            fullName = it.name,
            phoneNumber = it.phoneNumber,
            address = it.address,
            createdAt = it.createdAt,
            updatedAt = it.updatedAt
         )
      }
   }
}