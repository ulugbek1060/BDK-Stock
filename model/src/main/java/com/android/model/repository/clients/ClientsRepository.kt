package com.android.model.repository.clients

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.android.model.repository.base.BaseRepository
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
      if (fullName.isBlank()) throw EmptyFieldException(Field.FulLName)
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
      if (fullName.isBlank()) throw EmptyFieldException(Field.FulLName)
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

   suspend fun getClients(
      query: String
   ): Flow<PagingData<ClientEntity>> {
      val loader: ClientLoader = { pageIndex ->
         clientSource.getClients(
            query = query,
            pageIndex = pageIndex,
            pageSize = DEFAULT_PAGE_SIZE
         )
      }
      return Pager(
         config = PagingConfig(
            pageSize = DEFAULT_PAGE_SIZE,
            enablePlaceholders = false
         ),
         pagingSourceFactory = {
            ClientsPagingSource(loader)
         }
      ).flow
   }

   private companion object {
      const val DEFAULT_PAGE_SIZE = 10
   }
}