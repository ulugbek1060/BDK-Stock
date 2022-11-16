package com.android.model.repository.clients

import com.android.model.repository.clients.entity.ClientEntity
import com.android.model.utils.BackendException
import com.android.model.utils.EmptyFieldException
import com.google.gson.JsonParseException

interface ClientSource {

   /**
    * Create client name, phone number, vehicle, registration number.
    * @throws EmptyFieldException
    * @throws InvalidCredentialsException
    * @throws ConnectionException
    * @throws BackendException
    * @throws ParseBackendResponseException
    * @throws JsonParseException
    */
   suspend fun registerClient(fullName: String, phoneNumber: String, address: String): ClientEntity

   /**
    * Updates client.
    * @throws EmptyFieldException
    * @throws InvalidCredentialsException
    * @throws ConnectionException
    * @throws BackendException
    * @throws ParseBackendResponseException
    * @throws JsonParseException
    */
   suspend fun updateClient(
      clientId: Long,
      fullName: String,
      phoneNumber: String,
      address: String
   ): String

   /**
    * Gets client by id.
    * @throws EmptyFieldException
    * @throws InvalidCredentialsException
    * @throws ConnectionException
    * @throws BackendException
    * @throws ParseBackendResponseException
    * @throws JsonParseException
    */
   suspend fun getClientById(clientId: Long): ClientEntity

   /**
    * @throws Exception
    */
   suspend fun getClients(query: String, pageIndex: Int, pageSize: Int): List<ClientEntity>

}