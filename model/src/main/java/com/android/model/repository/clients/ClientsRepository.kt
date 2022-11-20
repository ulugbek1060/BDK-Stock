package com.android.model.repository.clients

import androidx.paging.PagingData
import com.android.model.repository.base.BasePageSource
import com.android.model.repository.base.BaseRepository
import com.android.model.repository.base.DataLoader
import com.android.model.repository.clients.entity.ClientEntity
import com.android.model.utils.EmptyFieldException
import com.android.model.utils.Field
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ClientsRepository @Inject constructor(
   private val clientSource: ClientSource
) : BaseRepository() {

   suspend fun registerClient(
      fullName: String,
      phoneNumber: String,
      address: String
   ): ClientEntity {
      if (fullName.isBlank()) throw EmptyFieldException(Field.FullName)
      if (phoneNumber.isBlank()) throw EmptyFieldException(Field.PhoneNumber)
      if (address.isBlank()) throw EmptyFieldException(Field.Address)
      return wrapExceptions {
         clientSource.registerClient(
            fullName = fullName, phoneNumber = phoneNumber, address = address
         )
      }
   }

   suspend fun updateClient(
      clientId: Long,
      fullName: String,
      phoneNumber: String,
      address: String
   ): String {
      if (fullName.isBlank()) throw EmptyFieldException(Field.FullName)
      if (phoneNumber.isBlank()) throw EmptyFieldException(Field.PhoneNumber)
      if (address.isBlank()) throw EmptyFieldException(Field.Address)
      return wrapExceptions {
         clientSource.updateClient(
            clientId, fullName, phoneNumber, address
         )
      }
   }

   suspend fun getClientById(clientId: Long): ClientEntity = wrapExceptions {
      clientSource.getClientById(clientId)
   }

   fun getClientsList(): Flow<PagingData<ClientEntity>> = getPagerData {
      val loader: DataLoader<ClientEntity> = { pageIndex ->
         clientSource.getClientsList(pageIndex = pageIndex, pageSize = DEFAULT_PAGE_SIZE)
      }
      BasePageSource(loader)
   }

   fun getClientsByQuery(query: String): Flow<PagingData<ClientEntity>> = getPagerData {
      val loader: DataLoader<ClientEntity> = { pageIndex ->
         clientSource.getClientsByQuery(query, pageIndex = pageIndex, pageSize = DEFAULT_PAGE_SIZE)
      }
      BasePageSource(loader)
   }

   private companion object {
      const val DEFAULT_PAGE_SIZE = 10
   }
}