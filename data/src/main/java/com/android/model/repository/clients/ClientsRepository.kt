package com.android.model.repository.clients

import androidx.paging.PagingData
import com.android.model.repository.base.BasePageSource
import com.android.model.repository.base.BaseRepository
import com.android.model.repository.base.DataLoader
import com.android.model.repository.clients.entity.ClientEntity
import com.android.model.utils.EmptyFieldException
import com.android.model.utils.Field
import com.android.model.utils.wrapBackendExceptions
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
      if (fullName.isBlank()) throw EmptyFieldException(Field.FULL_NAME)
      if (phoneNumber.isBlank()) throw EmptyFieldException(Field.PHONE_NUMBER)
      if (address.isBlank()) throw EmptyFieldException(Field.ADDRESS)
      return wrapBackendExceptions {
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
      if (fullName.isBlank()) throw EmptyFieldException(Field.FULL_NAME)
      if (phoneNumber.isBlank()) throw EmptyFieldException(Field.PHONE_NUMBER)
      if (address.isBlank()) throw EmptyFieldException(Field.ADDRESS)

      return wrapBackendExceptions {
         clientSource.updateClient(
            clientId, fullName, phoneNumber, address
         )
      }
   }

   suspend fun getClientById(clientId: Long): ClientEntity = wrapBackendExceptions {
      clientSource.getClientById(clientId)
   }

   fun getClientsList(query: String? = null): Flow<PagingData<ClientEntity>> = getPagerData {
      val loader: DataLoader<ClientEntity> = { pageIndex ->
         clientSource.getClientsList(
            query = query,
            pageIndex = pageIndex,
            pageSize = DEFAULT_PAGE_SIZE
         )
      }
      BasePageSource(loader = loader, defaultPageSize = DEFAULT_PAGE_SIZE)
   }
}